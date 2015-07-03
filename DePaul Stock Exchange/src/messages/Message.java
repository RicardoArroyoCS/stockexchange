package messages;

import price.Price;
import tradable.Order.BookSide;

public interface Message {
	/*
	 * Setters
	 */
	void setUser(String user) throws InvalidInputError;
	
	void setProduct(String product) throws InvalidInputError;
	
	void setPrice(Price p) throws InvalidInputError;
	
	void setVolume(int volume) throws InvalidInputError;
	
	void setDetails(String details) throws InvalidInputError;
	
	void setSide(BookSide side) throws InvalidInputError;
	
	void setId(String id) throws InvalidInputError;
	

	/*
	 * Accessors
	 */
	String getUser();
	
	String getProduct();
	
	Price getPrice();
	
	int getVolume();
	
	String getDetails();
	
	String getSide();
	
	String getId();
	
	
}
