package book;

import java.util.ArrayList;
import java.util.HashMap;

import messages.MarketMessage;
import publishers.MarketDataDTO;
import publishers.MessagePublisher;
import tradable.Order;
import tradable.Order.BookSide;
import tradable.Quote;
import tradable.TradableDTO;

public class ProductService {
	public enum MarketState {	CLOSED, PREOPEN, OPEN	}
	
	private HashMap<String, ProductBook> allBooks = new HashMap<String, ProductBook>();
	private MarketState mktState = MarketState.CLOSED;
	private static ProductService ourInstance;
	
	public static ProductService getInstance(){
		if (ourInstance == null){
			if (ourInstance == null)
				ourInstance = new ProductService();
			
		}

		return ourInstance;
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, String product) throws NoSuchProductException{
		if(!allBooks.containsKey(product))
			throw new NoSuchProductException("Product does not exist");
		return allBooks.get(product).getOrdersWithRemainingQty(userName);
	}
	
	public synchronized MarketDataDTO getMarketData(String product) throws NoSuchProductException{
		if(!allBooks.containsKey(product))
			throw new NoSuchProductException("Product does not exist");
		return allBooks.get(product).getMarketData();
	}
	
	public synchronized MarketState getMarketState(){
		return mktState;
	}
	
	public synchronized String[][] getBookDepth(String product) throws NoSuchProductException{
		if(!allBooks.containsKey(product))
			throw new NoSuchProductException("Product does not exist");
		return allBooks.get(product).getBookDepth();
	}
	
	public synchronized ArrayList<String> getProductList() throws NoSuchProductException{
		if(allBooks == null)
			throw new NoSuchProductException("Product list does not exist");
		return new ArrayList<String>(allBooks.keySet());
	}
	
	/* Market and Product Service Manipulation Methods */
	public synchronized void setMarketState(MarketState ms) throws Exception{
		HashMap<String, String> stateMap = new HashMap<String, String>();
		stateMap.put("CLOSED", "PREOPEN");
		stateMap.put("PREOPEN", "OPEN");
		stateMap.put("OPEN", "CLOSED");

		if(!stateMap.get(mktState.name()).equals(ms.name()) )
			throw new InvalidMarketStateTransition("Market cannot move to this state");
		
		switch(ms){
		case CLOSED:
			mktState = MarketState.CLOSED;
			break;
		case PREOPEN:
			mktState = MarketState.PREOPEN;
			break;
		case OPEN:
			mktState = MarketState.OPEN;
			break;
		}
		
		
		MarketMessage mktMsg = new MarketMessage(mktState.name());
		MessagePublisher.getInstance().publishMarketMessage(mktMsg);
		
		if(mktState.equals(MarketState.OPEN)){
			for(String s : allBooks.keySet())
				allBooks.get(s).openMarket();
		}
			
		if(mktState.equals(MarketState.CLOSED)){
			for(String s : allBooks.keySet())
				allBooks.get(s).closeMarket();
		}
	}
	
	public synchronized void createProduct(String product) throws DataValidationException, ProductAlreadyExistsException{
		if(product.isEmpty() || product == null)
			throw new DataValidationException("Invalid Product Argument");
		if(allBooks.containsKey(product))
			throw new ProductAlreadyExistsException("Product Already Exists");
		
		allBooks.put(product, new ProductBook(product));
	}
	
	public synchronized void submitQuote(Quote q) throws Exception{
		if(mktState == MarketState.CLOSED)
			throw new InvalidMarketStateException("Market is Closed");

		if(!allBooks.containsKey(q.getProduct()))
			throw new NoSuchProductException("Product " + q.getProduct() + " does not exist");
		
		allBooks.get(q.getProduct()).addToBook(q);
	}
	
	public synchronized String submitOrder(Order o) throws Exception{
		if(mktState == MarketState.CLOSED)
			throw new InvalidMarketStateException("Market is Closed");
		
		if(mktState == MarketState.PREOPEN && o.getPrice().isMarket())
			throw new InvalidMarketStateException("Market State cannot be Preopen and Order cannot be Market Price at the same time.");
		
		if(!allBooks.containsKey(o.getProduct()))
			throw new NoSuchProductException("Product " + o.getProduct() + " does not exist");
		
		allBooks.get(o.getProduct()).addToBook(o);
		
		return o.getId();
	}
	
	public synchronized void submitOrderCancel(String product, BookSide side, String orderId) throws Exception{
		if(mktState == MarketState.CLOSED)
			throw new InvalidMarketStateException("Market is Closed");
		
		if(!allBooks.containsKey(product))
			throw new NoSuchProductException("Product " + product + " does not exist");
		
		allBooks.get(product).cancelOrder(side, orderId);
	}
	
	public synchronized void submitQuoteCancel(String userName, String product) throws Exception{
		if(mktState == MarketState.CLOSED)
			throw new InvalidMarketStateException("Market is Closed");
		
		if(!allBooks.containsKey(product))
			throw new NoSuchProductException("Product " +product +" does not exist");
		allBooks.get(product).cancelQuote(userName);
	}
	
}
