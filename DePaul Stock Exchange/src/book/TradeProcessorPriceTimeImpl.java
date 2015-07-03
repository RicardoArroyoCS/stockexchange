package book;

import java.util.ArrayList;
import java.util.HashMap;

import price.Price;
import tradable.InvalidTradableValue;
import tradable.Order.BookSide;
import tradable.Tradable;
import messages.FillMessage;
import messages.InvalidInputError;

public class TradeProcessorPriceTimeImpl implements TradeProcessor {
	private HashMap<String, FillMessage> fillMessages = new HashMap<String, FillMessage>();
	private ProductBookSide book;
	
	public TradeProcessorPriceTimeImpl(ProductBookSide pb){
		book = pb;
	}
	
	private String makeFillKey(FillMessage fm){
		String key = fm.getUser() + fm.getId() + fm.getPrice().toString();
		return key;
	}
	
	private boolean isNewFill(FillMessage fm){
		String key = makeFillKey(fm);
		
		if(!fillMessages.containsKey(key))
			return true;
		
		FillMessage oldFill = fillMessages.get(key);
		if(!oldFill.getSide().equals(fm.getSide()))
			return true;
		if(!oldFill.getId().equals(fm.getId()))
			return true;
		
		return false;
	}
	
	private void addFillMessage(FillMessage fm) throws InvalidInputError{
		if(isNewFill(fm) == true){
			String key = makeFillKey(fm);
			fillMessages.put(key, fm);
		}
		else{
			String key = makeFillKey(fm);
			int currentVolume = fillMessages.get(key).getVolume();
			fillMessages.get(key).setFillVolume(currentVolume + fm.getVolume());
			fillMessages.get(key).setDetails(fm.getDetails());
		}
		
	}
	
	private BookSide createBookSide(String sideIn){
		BookSide side = null;
		switch(sideIn){
		case "BUY":  
			side = BookSide.BUY;
			break;
		case "SELL":
			side = BookSide.SELL;
			break;
		}
		return side;
	}
	
	public HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidInputError, InvalidTradableValue{
		fillMessages = new HashMap<String, FillMessage>();
		ArrayList<Tradable> tradedOut= new ArrayList<Tradable>();
		ArrayList<Tradable> entriesAtPrice= book.getEntriesAtTopOfBook();
		
		for (Tradable t : entriesAtPrice){
			if(trd.getRemainingVolume() == 0)
			{
				break;
			}
			else
			{
				if(trd.getRemainingVolume() >= t.getRemainingVolume())
				{
					tradedOut.add(t);
					Price tPrice;
					if(t.getPrice().isMarket())
						tPrice= trd.getPrice();
					else
						tPrice= t.getPrice();
					
					FillMessage tFill = new FillMessage(t.getUser(),t.getProduct(), tPrice, 
							t.getRemainingVolume(),"leaving 0", createBookSide(t.getSide()),t.getId());
					addFillMessage(tFill);
			
					
					FillMessage trdFill = new FillMessage(trd.getUser(),trd.getProduct(), tPrice, 
							t.getRemainingVolume(),"leaving " + (trd.getRemainingVolume()-t.getRemainingVolume()) ,
							createBookSide(trd.getSide()),trd.getId());

					addFillMessage(trdFill);
					
					trd.setRemainingVolume(trd.getRemainingVolume()-t.getRemainingVolume());
					
					t.setRemainingVolume(0);
					
					book.addOldEntry(t);
				} //end If
				
				else
				{
					int remainder = t.getRemainingVolume() - trd.getRemainingVolume();
					
					Price tPrice;
					//yes
					if(t.getPrice().isMarket())
						tPrice= trd.getPrice();
					//no
					else
						tPrice= t.getPrice();
					
					FillMessage tFill = new FillMessage(t.getUser(),t.getProduct(), tPrice, 
							trd.getRemainingVolume(),"leaving "+ remainder, createBookSide(t.getSide()),t.getId());

					addFillMessage(tFill);
					
					FillMessage trdFill = new FillMessage(trd.getUser(),trd.getProduct(), tPrice, 
							trd.getRemainingVolume(),"leaving 0", createBookSide(trd.getSide()),trd.getId());
					
					addFillMessage(trdFill);
					
					trd.setRemainingVolume(0);
					
					t.setRemainingVolume(remainder);
					
					book.addOldEntry(trd);
					break;
				}
			} //end Else
			
		} //end foreach
		for(Tradable o : tradedOut)
		{
			entriesAtPrice.remove(o);
			if(entriesAtPrice.isEmpty())
				book.clearIfEmpty(book.topOfBookPrice());
		}
		return fillMessages;
	} //end function;

}
