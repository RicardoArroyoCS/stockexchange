package users;

import gui.UserDisplayManager;

import java.util.ArrayList;

import messages.CancelMessage;
import messages.FillMessage;
import price.InvalidPriceOperation;
import price.Price;
import publishers.AlreadySubscribedException;
import tradable.Order.BookSide;
import tradable.TradableDTO;

import java.sql.Timestamp;

import book.NoSuchProductException;

public class UserImpl implements User {
	private String userName;
	private long connectionId;
	private ArrayList<String> stocksAvailable = new ArrayList<String>();;
	private ArrayList<TradableUserData> submittedOrders = new ArrayList<TradableUserData>();
	private Position position;
	private UserDisplayManager facade;

	public UserImpl(String user){
		userName = user;
		position = new Position();
	}
	
	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return userName;
	}

	@Override
	public void acceptLastSale(String product, Price p, int v) {
		// TODO Auto-generated method stub
		try{
			if (facade != null) {
				facade.updateLastSale(product, p, v);
				position.updateLastSale(product, p);
			}
		}catch(Exception e){
			System.out.println("AcceptLastSale: Some Exception Occured: " + e);
		}
		
	}

	public BookSide toBookSide(String s){
		switch(s){
		case "BUY":
			return BookSide.BUY;
		case "SELL":
			return BookSide.SELL;
		}
		return BookSide.BUY;
	}
	
	@Override
	public void acceptMessage(FillMessage fm) {
		// TODO Auto-generated method stub
		try{
			if (facade != null) {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String summary = "{" + timestamp.toString() + "} " + "Fill Message: "
					+ fm.getSide() + " " + fm.getVolume() + " " + fm.getProduct() + " at " 
					+ fm.getPrice().toString() + " " + fm.getDetails() + " [Tradable Id: " + fm.getId() + "]";
				
				facade.updateMarketActivity(summary);
				position.updatePosition(fm.getProduct(), fm.getPrice(), toBookSide(fm.getSide()), fm.getVolume());
			}
		}catch(Exception e){
			System.out.println("AcceptMessage(fm): Some Exception Occured: " + e);
		}

	}

	@Override
	public void acceptMessage(CancelMessage cm) {
		// TODO Auto-generated method stub
		try{
			if (facade != null) {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String summary = "{" + timestamp.toString() + "} " + "Cancel Message: "
					+ cm.getSide() + " " + cm.getVolume() + " " + cm.getProduct() + " at " 
					+ cm.getPrice().toString() + " " + cm.getDetails() + " [Tradable Id: " + cm.getId() + "]";
				
				facade.updateMarketActivity(summary);
			}
		}catch(Exception e){
			System.out.println("AcceptMessage(cm): Some Exception Occured: " + e);
		}
	}

	@Override
	public void acceptMarketMessage(String message) {
		// TODO Auto-generated method stub
		try{
			if (facade != null) 
				facade.updateMarketState(message);
			
		}catch(Exception e){
			System.out.println("AcceptMarketMessage: Some Exception Occured: " + e);
		}
		
	}

	@Override
	public void acceptTicker(String product, Price p, char direction) {
		// TODO Auto-generated method stub
		try{
			if (facade != null) 
				facade.updateTicker(product, p, direction);
			
		}catch(Exception e){
			System.out.println("AcceptTicker: Some Exception Occured: " + e);
		}
	}

	@Override
	public void acceptCurrentMarket(String product, Price bp, int bv, Price sp,
			int sv) {
		// TODO Auto-generated method stub
		try{
			if (facade != null) 
				facade.updateMarketData(product, bp, bv, sp, sv);
			
		}catch(Exception e){
			System.out.println("AcceptCurrentMarket: Some Exception Occured: " + e);
		}
	}

	@Override
	public void connect() throws AlreadyConnectedException, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException {
		// TODO Auto-generated method stub
		connectionId = UserCommandService.getInstance().connect(this);
		stocksAvailable = UserCommandService.getInstance().getProducts(userName, connectionId);
	}

	@Override
	public void disConnect() throws UserNotConnectedException, InvalidConnectionIdException {
		// TODO Auto-generated method stub
		UserCommandService.getInstance().disConnect(userName, connectionId);
	}

	@Override
	public void showMarketDisplay() throws Exception {
		// TODO Auto-generated method stub
		if(stocksAvailable == null)
			throw new UserNotConnectedException("User is not Connceted");
		if(facade == null)
			facade = new UserDisplayManager(this);
		
		facade.showMarketDisplay();
	}

	@Override
	public String submitOrder(String product, Price price, int volume, BookSide side) throws Exception {
		// TODO Auto-generated method stub
		String id = UserCommandService.getInstance().submitOrder(userName, connectionId, product, price, volume, side);
		TradableUserData tradable = new TradableUserData(userName, product, side, id);
		submittedOrders.add(tradable);
		
		return id;
	}

	public void submitOrderCancel(String product, BookSide side, String orderid) throws Exception{
		UserCommandService.getInstance().submitOrderCancel(userName, connectionId, product, side, orderid);
	}
	
	@Override
	public void submitQuote(String product, Price buyPrice, int buyVolume,
			Price sellPrice, int sellVolume) throws Exception {
		// TODO Auto-generated method stub
		UserCommandService.getInstance().submitQuote(userName, connectionId, product, buyPrice, buyVolume, sellPrice, sellVolume);
	}

	@Override
	public void submitQuoteCancel(String product) throws Exception {
		// TODO Auto-generated method stub
		UserCommandService.getInstance().submitQuoteCancel(userName, connectionId, product);
	}

	@Override
	public void subscribeCurrentMarket(String product) throws AlreadySubscribedException, UserNotConnectedException, InvalidConnectionIdException {
		// TODO Auto-generated method stub
		UserCommandService.getInstance().subscribeCurrentMarket(userName, connectionId, product);
	}

	@Override
	public void subscribeLastSale(String product) throws AlreadySubscribedException, Exception {
		// TODO Auto-generated method stub
		UserCommandService.getInstance().subscribeLastSale(userName, connectionId, product);
	}

	@Override
	public void subscribeMessage(String product) throws AlreadySubscribedException, Exception {
		// TODO Auto-generated method stub
		UserCommandService.getInstance().subscribeMessages(userName, connectionId, product);
	}

	public void subscribeTicker(String product) throws AlreadySubscribedException, UserNotConnectedException, InvalidConnectionIdException{
		UserCommandService.getInstance().subscribeTicker(userName, connectionId, product);
	}
	
	@Override
	public Price getAllStockValue() throws InvalidPriceOperation {
		// TODO Auto-generated method stub
		return position.getAllStockValue();
	}

	@Override
	public Price getAccountCosts() {
		// TODO Auto-generated method stub
		return position.getAccountCosts();
	}

	@Override
	public Price getNetAccountValue() throws InvalidPriceOperation {
		// TODO Auto-generated method stub
		return position.getNetAccountValue();
	}

	@Override
	public String[][] getBookDepth(String product) throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException {
		// TODO Auto-generated method stub
		return UserCommandService.getInstance().getBookDepth(userName, connectionId, product);
	}

	@Override
	public String getMarketState() throws UserNotConnectedException, InvalidConnectionIdException {
		// TODO Auto-generated method stub
		return UserCommandService.getInstance().getMarketState(userName, connectionId);
	}

	@Override
	public ArrayList<TradableUserData> getOrderIds() {
		// TODO Auto-generated method stub
		return submittedOrders;
	}

	@Override
	public ArrayList<String> getProductList() {
		// TODO Auto-generated method stub
		return stocksAvailable;
	}

	@Override
	public Price getStockPositionValue(String sym) {
		// TODO Auto-generated method stub
		return position.getStockPositionValues(sym);
	}

	@Override
	public int getStockPositionVolume(String product) {
		// TODO Auto-generated method stub
		return position.getStockPositionVolume(product);
	}

	@Override
	public ArrayList<String> getHoldings() {
		// TODO Auto-generated method stub
		return position.getHoldings();
	}

	@Override
	public ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException {
		// TODO Auto-generated method stub
		return UserCommandService.getInstance().getOrdersWithRemainingQty(userName, connectionId, product);
	}

}
