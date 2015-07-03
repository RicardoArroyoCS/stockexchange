package messages;

import price.Price;
import tradable.Order.BookSide;

public class MessageImpl implements Message {
	private String user;
	private String product;
	private Price price;
	private int volume;
	private String details;
	private BookSide side;
	public String id;
	
	
	public MessageImpl(String user, String product, Price price, int volume, String details, BookSide side, String id) throws InvalidInputError {
		this.setUser(user);
		this.setProduct(product);
		this.setPrice(price);
		this.setVolume(volume);
		this.setDetails(details);
		this.setSide(side);
		this.setId(id);
	}
	
	
	/*
	 * Setters (Need to do checks)
	 */
	public void setUser(String user) throws InvalidInputError{
		if(user == null || user == "")
			throw new InvalidInputError("Input Cannot be Null or Empty");
		this.user = user;
	}
	
	public void setProduct(String product) throws InvalidInputError{
		if(product == null || product == "")
			throw new InvalidInputError("Input Cannot be Null or Empty");
		this.product = product;
	}
	
	public void setPrice(Price price) throws InvalidInputError{
		if(price == null)
			throw new InvalidInputError("Input Cannot be Null or Empty");
		this.price = price;
	}
	
	public void setVolume(int volume) throws InvalidInputError{
		if(volume<0)
			throw new InvalidInputError("Input Cannot be negative");
		this.volume = volume;
	}
	
	public void setDetails(String details) throws InvalidInputError{
		if(details == null)
			throw new InvalidInputError("Input Cannot be negative");
		this.details = details;
	}
	
	public void setSide(BookSide side) throws InvalidInputError{
		if(side == null)
			throw new InvalidInputError("Input Cannot be negative");
		this.side = side;
	}
	
	public void setId(String id) throws InvalidInputError{
		if(id == null)
			throw new InvalidInputError("Input Cannot be negative");
		this.id = id;
	}

	/* 
	 * Accessors 
	 */
	public String getUser(){
		return user;
	}
	
	public String getProduct(){
		return product;
	}
	
	public Price getPrice(){
		return price;
	}
	
	public int getVolume(){
		return volume;
	}
	
	public String getDetails(){
		return details;
	}
	
	public String getSide(){
		switch(side){
		case BUY:
			return "BUY";
		case SELL:
			return "SELL";
		}
		return "";
	}
	
	public String getId(){
		return id;
	}
	
}
