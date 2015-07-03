package book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import messages.CancelMessage;
import messages.FillMessage;
import messages.InvalidInputError;
import price.Price;
import publishers.MessagePublisher;
import tradable.InvalidTradableValue;
import tradable.Tradable;
import tradable.Order.BookSide;
import tradable.TradableDTO;

public class ProductBookSide {
	public BookSide side;
	private HashMap<Price, ArrayList<Tradable>> bookEntries = new HashMap<Price, ArrayList<Tradable>>();
	private TradeProcessor trade;
	private ProductBook parent;
	
	
	public ProductBookSide(ProductBook pBook, BookSide sideIn) {
		parent = pBook;
		trade = TradeProcessorFactory.getInstance().makeTradeProcessor(this);
		
		switch (sideIn) {
		case BUY:
			this.side = BookSide.BUY;
			break;
		case SELL:
			this.side = BookSide.SELL;
			break;
		}
	}

	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName){
		ArrayList<TradableDTO> ordersWithRemainingQty = new ArrayList<TradableDTO>();

		for (Price p : bookEntries.keySet()) {
			for(Tradable t :bookEntries.get(p)){
				if(t.getUser().equals(userName)){
					if(t.getRemainingVolume() > 0){
						TradableDTO temp = new TradableDTO(t.getProduct(), t.getPrice(), t.getOriginalVolume(), 
								t.getRemainingVolume(), t.getCancelledVolume(), t.getUser(), t.getSide(), false, t.getId());
						ordersWithRemainingQty.add(temp);
					}
				}
			}
		}
		//added
		Collections.sort(ordersWithRemainingQty);
		
		if(this.side == BookSide.BUY)
			Collections.reverse(ordersWithRemainingQty);
		return ordersWithRemainingQty;
	}

	synchronized ArrayList<Tradable> getEntriesAtTopOfBook() {
		if (bookEntries.isEmpty())
			return null;
		else {
			ArrayList<Price> sorted = new ArrayList<Price>(bookEntries.keySet());
																					
			Collections.sort(sorted); 
			if (side == BookSide.BUY) {
				Collections.reverse(sorted); // Reverse them
			}
			return bookEntries.get(sorted.get(0));
		}
	}

	public synchronized String[] getBookDepth(){
		if(bookEntries.isEmpty()){
			String e[] = new String[1];
			e[0]= "<Empty>";
			
			return e;	
		}
		else{
			String e[] = new String[bookEntries.size()];
			int totalRemainingVolume = 0;
			int count =0;
			
			ArrayList<Price> sorted = new ArrayList<Price>(bookEntries.keySet()); 
			Collections.sort(sorted);
			if (side == BookSide.BUY) 
				Collections.reverse(sorted); // Reverse them
			
			for(Price p: sorted){
				String temp = "";
				totalRemainingVolume = 0;
				for(Tradable t :bookEntries.get(p)){
					totalRemainingVolume += t.getRemainingVolume();
				}
				temp = p.toString() + " x " + totalRemainingVolume;
				e[count] = temp;
				count++;
			}
			return e;
		}
		
	}

	synchronized ArrayList<Tradable> getEntriesAtPrice(Price price) {
		if (bookEntries.isEmpty())
			return null;
		else
			return bookEntries.get(price);
	}

	public synchronized boolean hasMarketPrice() {
		for(Price p : bookEntries.keySet()){
			if(p.isMarket() == true)
				return true;
		}
		return false;
	}

	public synchronized boolean hasOnlyMarketPrice() {
		for(Price p : bookEntries.keySet()){
			if(p.isMarket() == true && bookEntries.size() == 1)
				return true;
		}
		return false;
	}

	public synchronized Price topOfBookPrice() {
		if (bookEntries.isEmpty())
			return null;
			//return PriceFactory.makeLimitPrice((long)0);
			
		else{
			ArrayList<Price> sorted = new ArrayList<Price>(bookEntries.keySet());
			
			Collections.sort(sorted); 
			if (side == BookSide.BUY) 
				Collections.reverse(sorted); // Reverse them
			
			return sorted.get(0);
		}
	}

	public synchronized int topOfBookVolume() {
		if(bookEntries.isEmpty())
			return 0;
		else{
			int remainingVolume = 0;
			
			Price p = topOfBookPrice();
			for(Tradable t : bookEntries.get(p))
				remainingVolume += t.getRemainingVolume();
			
			return remainingVolume;
		}
	}

	public synchronized boolean isEmpty() {
		if (bookEntries.isEmpty())
			return true;
		return false;
	}

	/*
	 * ProductBookSide Manipulation Methods: These methods perform various
	 * operations that will add and remove Tradables in the ProductBookSide.
	 * Additionally, some of these methods will work with the TradeProcessor
	 * object in performing the execution of a trade. Note that most of the
	 * methods here require the “synchronized” keyword. An overview of what this
	 * synchronized keyword does and why it is needed can be found in Appendix A
	 * of this document “Blocking Multithreaded Execution Using ‘synchronized’”.
	 */
	public synchronized void cancelAll() throws Exception {
		ArrayList<Price> keySet = new ArrayList<Price>(bookEntries.keySet());
		for(Price p : keySet){
			ArrayList<Tradable> tradables = new ArrayList<Tradable>(bookEntries.get(p));
			for(Tradable t : tradables){
				if(t.isQuote())
					submitQuoteCancel(t.getUser());
				else
					submitOrderCancel(t.getId());
				
			
			}
		}
		
	}

	public synchronized TradableDTO removeQuote(String user) {
		int count;
		for(Price p : bookEntries.keySet()){
			count = 0;
			for(Tradable t : bookEntries.get(p)){	
				
				if(t.isQuote() == true && t.getUser().equals(user)){
					TradableDTO temp = new TradableDTO(t.getProduct(), t.getPrice(), t.getOriginalVolume(), 
							t.getRemainingVolume(), t.getCancelledVolume(), t.getUser(), t.getSide(), true, t.getId());
					
					bookEntries.get(p).remove(count);
					
					if(bookEntries.get(p).size() <1)
						bookEntries.remove(p);
					
					return temp;
				}
				count++;
			}
		}
		return null;
	}

	public synchronized void submitOrderCancel(String orderId) throws Exception {
		boolean isFound = false;
		
		ArrayList<Price> setPrices = new ArrayList<Price>(bookEntries.keySet());
		for(Price p : setPrices){
			ArrayList<Tradable> tempList = new ArrayList<Tradable>( bookEntries.get(p));
			for(Tradable t: tempList){
				if(t.getId().equals(orderId)){
					isFound = true;
					Tradable temp = t;
					bookEntries.get(p).remove(t);
					
					CancelMessage cm = 	new CancelMessage(temp.getUser(), temp.getProduct(), temp.getPrice(), 
							temp.getRemainingVolume(), "Cancelled Order", temp.getSide(), temp.getId());
					
					
					MessagePublisher.getInstance().publishCancel(cm);
					this.addOldEntry(temp);
					
					if(bookEntries.get(p).size() <1){
						bookEntries.remove(p);
						break;
					}

				}
			}
		}
		
		if(isFound == false)
			parent.checkTooLateToCancel(orderId);
	}

	public synchronized void submitQuoteCancel(String userName) throws Exception {
		TradableDTO dto = removeQuote(userName);
		
		if(dto != null){
			String message = "Quote " + dto.side + "-Side Cancelled";
			CancelMessage cm = 	new CancelMessage(dto.user, dto.product, dto.price, 
					dto.remainingVolume, message, dto.side, dto.id);
			MessagePublisher.getInstance().publishCancel(cm);
		}
	}

	public void addOldEntry(Tradable t) throws InvalidTradableValue {
		parent.addOldEntry(t);
	}

	public synchronized void addToBook(Tradable trd) {
		if(bookEntries.containsKey(trd.getPrice()))
			bookEntries.get(trd.getPrice()).add(trd);
		else{
			ArrayList<Tradable> temp = new ArrayList<Tradable>();
			bookEntries.put(trd.getPrice(), temp);
			bookEntries.get(trd.getPrice()).add(trd);
		}
	}

	public HashMap<String, FillMessage> tryTrade(Tradable trd) throws Exception {
		HashMap<String, FillMessage> allFills = null;
		if(this.side.equals(BookSide.BUY)){ 
			allFills = trySellAgainstBuySideTrade(trd);
		}
		else if(this.side.equals(BookSide.SELL)){ 
			allFills = tryBuyAgainstSellSideTrade(trd);
		}
		
		if( allFills != null){
			for(String s : allFills.keySet()){
				MessagePublisher.getInstance().publishFill(allFills.get(s));
			}
		}
		return allFills;
	}

	public synchronized HashMap<String, FillMessage> trySellAgainstBuySideTrade(Tradable trd) throws InvalidInputError, InvalidTradableValue {
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMsgs = new HashMap<String, FillMessage>();
		
		while( ((trd.getRemainingVolume() > 0 && !this.isEmpty()) && 
				trd.getPrice().lessOrEqual(topOfBookPrice()))
				|| ((trd.getRemainingVolume() > 0 && !this.isEmpty()) && trd.getPrice().isMarket()) ){
			HashMap<String, FillMessage> someMsgs = trade.doTrade(trd);
			fillMsgs = mergeFills(fillMsgs, someMsgs);
			
		}
		allFills.putAll(fillMsgs);
		return allFills;
	}

	private HashMap<String, FillMessage> mergeFills(HashMap<String, FillMessage> existing,
			HashMap<String, FillMessage> newOnes) throws InvalidInputError {
		
		if(existing.isEmpty()){
			return new HashMap<String, FillMessage>(newOnes);
		}
		else{
			HashMap<String, FillMessage> results = new HashMap<>(existing);
			for(String key : newOnes.keySet()){
				if(!existing.containsKey(key)){
					results.put(key, newOnes.get(key));
				}
				else{
					FillMessage fm = results.get(key);
					fm.setFillVolume(newOnes.get(key).getVolume());
					fm.setDetails(newOnes.get(key).getDetails());
				}
			}
			return results;
		}
		
	}

	public synchronized HashMap<String, FillMessage> tryBuyAgainstSellSideTrade(
			Tradable trd) throws InvalidInputError, InvalidTradableValue {
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMsgs = new HashMap<String, FillMessage>();
		
		while( (trd.getRemainingVolume() > 0 && !this.isEmpty() && trd.getPrice().greaterOrEqual(topOfBookPrice()))
			  || (trd.getRemainingVolume() > 0 && !this.isEmpty() && trd.getPrice().isMarket()) ) {
			HashMap<String, FillMessage> someMsgs = trade.doTrade(trd);
			fillMsgs = mergeFills(fillMsgs, someMsgs);
			
		}
		allFills.putAll(fillMsgs);
		return allFills;
	}


	public synchronized void clearIfEmpty(Price p) {
		if(bookEntries.get(p).isEmpty())
			bookEntries.remove(p);
	}

	public synchronized void removeTradable(Tradable t) {
		ArrayList<Tradable> entries = bookEntries.get(t.getPrice());
		if(entries != null){
			if(bookEntries.get(t.getPrice()).remove(t)){
				if(entries.isEmpty()){
					this.clearIfEmpty(t.getPrice());
				}
			}
		}
		
	}

}
