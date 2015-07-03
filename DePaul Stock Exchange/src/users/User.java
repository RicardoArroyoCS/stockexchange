package users;

import java.util.ArrayList;

import book.NoSuchProductException;
import messages.CancelMessage;
import messages.FillMessage;
import price.InvalidPriceOperation;
import price.Price;
import publishers.AlreadySubscribedException;
import tradable.Order.BookSide;
import tradable.TradableDTO;

public interface User {
	String getUserName();
	void acceptLastSale(String product, Price p, int v);
	void acceptMessage(FillMessage fm);
	void acceptMessage(CancelMessage cm);
	void acceptMarketMessage(String message);
	void acceptTicker(String product, Price p, char direction);
	void acceptCurrentMarket(String product, Price bp, int bv, Price sp, int sv);
	
	void connect() throws AlreadyConnectedException, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException;
	void disConnect() throws UserNotConnectedException, InvalidConnectionIdException;
	void showMarketDisplay() throws Exception;
	String submitOrder(String product, Price price, int volume, BookSide side) throws Exception;
	void submitOrderCancel(String proudct, BookSide side, String orderid) throws Exception;
	void submitQuote(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws Exception;
	void submitQuoteCancel(String product) throws Exception;
	void subscribeCurrentMarket(String product) throws AlreadySubscribedException, UserNotConnectedException, InvalidConnectionIdException;
	void subscribeLastSale(String product) throws AlreadySubscribedException, Exception;
	void subscribeMessage(String product) throws AlreadySubscribedException, Exception;
	void subscribeTicker(String product) throws AlreadySubscribedException, UserNotConnectedException, InvalidConnectionIdException;
	Price getAllStockValue() throws InvalidPriceOperation;
	Price getAccountCosts();
	Price getNetAccountValue() throws InvalidPriceOperation;
	String[][] getBookDepth(String product) throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException;
	String getMarketState() throws UserNotConnectedException, InvalidConnectionIdException;
	ArrayList<TradableUserData> getOrderIds();
	ArrayList<String> getProductList();
	Price getStockPositionValue(String sym);
	int getStockPositionVolume(String product);
	ArrayList<String> getHoldings();
	ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException;
	
}
