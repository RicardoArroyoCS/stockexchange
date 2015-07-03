package book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import book.ProductService.MarketState;
import messages.CancelMessage;
import messages.FillMessage;
import price.*;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MarketDataDTO;
import publishers.MessagePublisher;
import tradable.*;
import tradable.Order.*;

public class ProductBook {
	private String productSymbol;
	private ProductBookSide buySide;
	private ProductBookSide sellSide;
	private String lastCurrentMarket = "";
	private HashSet<String> userQuotes = new HashSet<>();
	private HashMap<Price, ArrayList<Tradable>> oldEntries = new HashMap<Price, ArrayList<Tradable>>();
	
	
	public ProductBook(String symbol){
		productSymbol = symbol;
		buySide = new ProductBookSide(this, BookSide.BUY);
		sellSide = new ProductBookSide(this, BookSide.SELL);
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName){
		ArrayList<TradableDTO> temp = new ArrayList<TradableDTO>();
		
		for(TradableDTO tdo : buySide.getOrdersWithRemainingQty(userName))
		{
			temp.add(tdo);
		}
		
		for(TradableDTO tdo : sellSide.getOrdersWithRemainingQty(userName))
		{
			temp.add(tdo);
		}
		return temp;
	}
	
	public synchronized void checkTooLateToCancel(String orderId) throws Exception{
		String message = "Too Late to Cancel";
		boolean containsOrder = false;
		
		for(Price p : oldEntries.keySet()){
			for(Tradable t : oldEntries.get(p)){
				if(t.getId().equals(orderId)){
					CancelMessage cm = 	new CancelMessage(t.getUser(), t.getProduct(), t.getPrice(), 
							t.getRemainingVolume(), message, t.getSide(), t.getId());
					
					MessagePublisher.getInstance().publishCancel(cm);
					containsOrder = true;
				}
					
			}
		}
		
		if(containsOrder == false)
			throw new OrderNotFoundException("Order cannot be found.");
	}
	
	public synchronized String[][] getBookDepth(){
		String[][] bd = new String[2][];
		bd[0] = buySide.getBookDepth();
		bd[1] = sellSide.getBookDepth();
		
		return bd;
	}
	
	public synchronized MarketDataDTO getMarketData(){
		Price buyPrice = buySide.topOfBookPrice();
		Price sellPrice = sellSide.topOfBookPrice();
		
		if(buyPrice == null)
			buyPrice = PriceFactory.makeLimitPrice((long)0.00);
		
		if(sellPrice == null)
			sellPrice = PriceFactory.makeLimitPrice((long)0.00);
		
		int buyVolume = buySide.topOfBookVolume();
		int sellVolume = sellSide.topOfBookVolume();
		
		return new MarketDataDTO(this.productSymbol, buyPrice, buyVolume,
				sellPrice, sellVolume); 
	}
	
	public synchronized void addOldEntry(Tradable t) throws InvalidTradableValue{
		if(!oldEntries.containsKey(t.getPrice())) {
			oldEntries.put(t.getPrice(), new ArrayList<Tradable>());
		}
		Tradable temp = t;
		
		temp.setCancelledVolume(t.getRemainingVolume());
		temp.setRemainingVolume(0);
		
		oldEntries.get(t.getPrice()).add(temp);
	}
	
	public synchronized void openMarket() throws Exception{
		Price bestBuyPrice = buySide.topOfBookPrice();
		Price bestSellPrice = sellSide.topOfBookPrice();
		
		if(bestBuyPrice != null && bestSellPrice != null){
			while(bestBuyPrice.greaterOrEqual(bestSellPrice) || bestBuyPrice.isMarket() || bestSellPrice.isMarket() ){
				ArrayList<Tradable> topOfBuySide = buySide.getEntriesAtPrice(bestBuyPrice);
				ArrayList<Tradable> toRemove = new ArrayList<Tradable>();
				HashMap<String, FillMessage> allFills = null;
				
				for(Tradable t : topOfBuySide){
					allFills = sellSide.tryTrade(t);
					if(t.getRemainingVolume() == 0){
						toRemove.add(t);
					}
				}
				
				for(Tradable t : toRemove){
					buySide.removeTradable(t);
				}
				updateCurrentMarket();
				
				Price lastSalePrice = this.determineLastSalePrice(allFills);
				int lastSaleVolume = this.determineLastSaleQuantity(allFills);
				
				LastSalePublisher.getInstance().publishLastSale(productSymbol, lastSalePrice, lastSaleVolume);
				
				bestBuyPrice = buySide.topOfBookPrice();
				bestSellPrice = sellSide.topOfBookPrice();
				
				if(bestBuyPrice == null || bestSellPrice == null)
					break;
			}
		}
	}
	
	public synchronized void closeMarket() throws Exception{
		buySide.cancelAll();
		sellSide.cancelAll();
		this.updateCurrentMarket();
	}
	
	public synchronized void cancelOrder(BookSide side, String orderId) throws Exception{
		switch(side){
		case BUY:
			buySide.submitOrderCancel(orderId);
			break;
		case SELL:
			sellSide.submitOrderCancel(orderId);
			break;
		}
		this.updateCurrentMarket();
	}
	
	public synchronized void cancelQuote(String userName) throws Exception{
		buySide.submitQuoteCancel(userName);
		sellSide.submitQuoteCancel(userName);
		this.updateCurrentMarket();
	}
	
	public synchronized void addToBook(Quote q) throws Exception{
		Price sellQuote = q.getQuoteSide("SELL").getPrice();
		Price buyQuote = q.getQuoteSide("BUY").getPrice();
		
		if(sellQuote.lessOrEqual(buyQuote))
			throw new DataValidationException("SELL Price is less than or Equal to BUY Price");
		if(q.getQuoteSide("SELL").getOriginalVolume() <= 0 || q.getQuoteSide("BUY").getOriginalVolume() <= 0)
			throw new DataValidationException("BUY or SELL side is less than or equal to zero");
		
		
		if(this.userQuotes.contains(q.getUserName())){
			buySide.removeQuote(q.getUserName());
			sellSide.removeQuote(q.getUserName());
			this.updateCurrentMarket();
		}
		this.addToBook(BookSide.BUY, q.getQuoteSide("BUY"));
		this.addToBook(BookSide.SELL, q.getQuoteSide("SELL"));
		
		userQuotes.add(q.getUserName());
		this.updateCurrentMarket();
			
	}
	
	public synchronized void addToBook(Order o) throws Exception{
		
		switch(o.getSide()){
		case "BUY":
			this.addToBook(BookSide.BUY, o);
			break;
		case "SELL":
			this.addToBook(BookSide.SELL, o);
			break;
		}
		this.updateCurrentMarket();
	}
	
	public synchronized void updateCurrentMarket(){
		Price buyTopPrice = buySide.topOfBookPrice();
		if(buyTopPrice == null)
			buyTopPrice = PriceFactory.makeLimitPrice((long)0);
		
		Price sellTopPrice = sellSide.topOfBookPrice();
		if(sellTopPrice == null)
			sellTopPrice = PriceFactory.makeLimitPrice((long)0);
		
		String str = buyTopPrice.toString() + buySide.topOfBookVolume()
				+ sellTopPrice.toString() + sellSide.topOfBookVolume();
		
		if(!this.lastCurrentMarket.equals(str)){
			MarketDataDTO mktDTO = new MarketDataDTO(this.productSymbol, buyTopPrice,
					buySide.topOfBookVolume(), sellTopPrice, sellSide.topOfBookVolume());

			CurrentMarketPublisher.getInstance().publishCurrentMarket(mktDTO);
			lastCurrentMarket = str;
		}
	}
	
	private synchronized Price determineLastSalePrice(HashMap<String, FillMessage> fills){
		ArrayList<FillMessage> msgs = new ArrayList<>(fills.values());
		Collections.sort(msgs);
		if(msgs.get(0).getSide().equals("BUY"))
			return msgs.get(0).getPrice();
		
		Collections.reverse(msgs);
		return msgs.get(0).getPrice();
	}
	
	private synchronized int determineLastSaleQuantity(HashMap<String, FillMessage> fills){
		ArrayList<FillMessage> msgs = new ArrayList<>(fills.values());
		Collections.sort(msgs);
		
		Collections.reverse(msgs);
		return msgs.get(0).getVolume();		
	}
	
	private synchronized void addToBook(BookSide side, Tradable trd) throws Exception{
		if(ProductService.getInstance().getMarketState().equals(MarketState.PREOPEN)){
			switch(side){
				case BUY:
					buySide.addToBook(trd);
					break;
				case SELL:
					sellSide.addToBook(trd);
					break;
			}	
		}
		else{
			HashMap<String, FillMessage> allFills = null;
			
			switch(side){
			case BUY:
				allFills = sellSide.tryTrade(trd);
				break;
			case SELL:
				allFills = buySide.tryTrade(trd);
				break;
			}
			
			if(allFills != null && !allFills.isEmpty()){
				this.updateCurrentMarket();
				int difference =  trd.getOriginalVolume() - trd.getRemainingVolume();
				Price lastSalePrice = this.determineLastSalePrice(allFills);
				LastSalePublisher.getInstance().publishLastSale(this.productSymbol, lastSalePrice, difference);
			}
			
			if(trd.getRemainingVolume() > 0){
				if(trd.getPrice().isMarket()){
					CancelMessage cm = 	new CancelMessage(trd.getUser(), trd.getProduct(), trd.getPrice(), 
							trd.getRemainingVolume(), "Cancelled", trd.getSide(), trd.getId());
					
					MessagePublisher.getInstance().publishCancel(cm);				
				}
				else{
					switch(side){
					case BUY:
						buySide.addToBook(trd);
						break;
					case SELL:
						sellSide.addToBook(trd);
						break;
					}						
				}
			}
			
		}
	}
	
	
}
