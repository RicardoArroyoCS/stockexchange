package users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import messages.InvalidInputError;
import price.Price;
import publishers.AlreadySubscribedException;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MessagePublisher;
import publishers.NotSubscribedException;
import publishers.TickerPublisher;
import tradable.InvalidTradableValue;
import tradable.Order;
import tradable.Order.BookSide;
import tradable.Quote;
import tradable.TradableDTO;
import book.NoSuchProductException;
import book.ProductService;

public class UserCommandService {
	private static UserCommandService instance = new UserCommandService();
	HashMap<String, Long> connectedUserIds = new HashMap<String, Long>();
	HashMap<String, User> connectedUsers = new HashMap<String, User>();
	HashMap<String, Long> connectedTime = new HashMap<String, Long>();
	
	public static UserCommandService getInstance(){
		return instance;
	}
	
	private void verifyUser(String userName, long connId) throws UserNotConnectedException, InvalidConnectionIdException{
		if(!connectedUserIds.containsKey(userName))
			throw new UserNotConnectedException("User is not Connected");
		if(connId != connectedUserIds.get(userName))
			throw new InvalidConnectionIdException("Invalid Connected ID");
	}
	
	public synchronized long connect(User user) throws AlreadyConnectedException{
		if(connectedUserIds.containsKey(user))
			throw new AlreadyConnectedException("User is already connnected");
		
		connectedUserIds.put(user.getUserName(), System.nanoTime());
		connectedUsers.put(user.getUserName(), user);
		connectedTime.put(user.getUserName(), System.currentTimeMillis());
		
		return connectedUserIds.get(user.getUserName());
	}
	
	public synchronized void disConnect(String userName, long connId) throws UserNotConnectedException, InvalidConnectionIdException{
		verifyUser(userName, connId);
		connectedUserIds.remove(userName);
		connectedUsers.remove(userName);
		connectedTime.remove(userName);
	}
	
	public String[][] getBookDepth(String userName, long connId, String product) throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException{
		verifyUser(userName, connId);
		return ProductService.getInstance().getBookDepth(product);
	}
	
	public String getMarketState(String userName, long connId) throws UserNotConnectedException, InvalidConnectionIdException{
		verifyUser(userName, connId);
		return ProductService.getInstance().getMarketState().name();
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, long connId, String product) throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException{
		verifyUser(userName, connId);
		return ProductService.getInstance().getOrdersWithRemainingQty(userName, product);
	}
	
	public ArrayList<String> getProducts(String userName, long connId) throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException{
		verifyUser(userName, connId);
		ArrayList<String> temp = new ArrayList<String>(ProductService.getInstance().getProductList());
		Collections.sort(temp);
		return temp;
	}
	
	public String submitOrder(String userName, long connId, String product, Price price, int volume, BookSide side) throws InvalidTradableValue, InvalidInputError, Exception{
		verifyUser(userName, connId);
		Order o = new Order(userName, product, price, volume, side.name());
		ProductService.getInstance().submitOrder(o);
		return o.getId();
		
	}
	
	public void submitOrderCancel(String userName, long connId, String product, BookSide side, String orderId) throws Exception{
		verifyUser(userName, connId);
		ProductService.getInstance().submitOrderCancel(product, side, orderId);
	}
	
	public void submitQuote(String userName, long connId, String product, Price bPrice, int bVolume, Price sPrice, int sVolume) throws Exception{
		verifyUser(userName, connId);
		Quote temp = new Quote(userName, product, bPrice, bVolume, sPrice, sVolume);
		ProductService.getInstance().submitQuote(temp);
	}
	
	public void submitQuoteCancel(String userName, long connId, String product) throws Exception{
		verifyUser(userName, connId);
		ProductService.getInstance().submitQuoteCancel(userName, product);
	}

	public void subscribeCurrentMarket(String userName, long connId, String product) throws AlreadySubscribedException, UserNotConnectedException, InvalidConnectionIdException{
		verifyUser(userName, connId);
		CurrentMarketPublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	public void subscribeLastSale(String userName, long connId, String product) throws AlreadySubscribedException, Exception{
		verifyUser(userName, connId);
		LastSalePublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	public void subscribeMessages(String userName, long connId, String product) throws AlreadySubscribedException, Exception{
		verifyUser(userName, connId);
		MessagePublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	public void subscribeTicker(String userName, long connId, String product) throws AlreadySubscribedException, UserNotConnectedException, InvalidConnectionIdException{
		verifyUser(userName, connId);
		TickerPublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	public void unSubscribeCurrentMarket(String userName, long connId, String product) throws NotSubscribedException, UserNotConnectedException, InvalidConnectionIdException{
		verifyUser(userName, connId);
		CurrentMarketPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	public void unSubscribeLastSale(String userName, long connId, String product) throws NotSubscribedException, Exception{
		verifyUser(userName, connId);
		LastSalePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	public void unSubscribeTicker(String userName, long connId, String product) throws NotSubscribedException, UserNotConnectedException, InvalidConnectionIdException{
		verifyUser(userName, connId);
		TickerPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	public void unSubscribeMessages(String userName, long connId, String product) throws NotSubscribedException, Exception{
		verifyUser(userName, connId);
		MessagePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
}
