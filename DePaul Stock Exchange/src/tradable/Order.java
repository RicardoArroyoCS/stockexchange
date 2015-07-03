package tradable;

import messages.InvalidInputError;
import price.Price;

public class Order implements Tradable {
	
	public enum BookSide {	BUY, SELL	}
	
	private String userName;
	private String productSymbol;
	private String id;
	private BookSide side;
	private Price price;
	private int originalVolume;
	private int remainingVolume;
	private int cancelledVolume;
	
	public Order(String userName, String productSymbol, Price orderPrice, int originalVolume, String side)
			throws InvalidTradableValue, InvalidInputError{
		this.setUserName(userName);
		this.setProduct(productSymbol);
		this.setPrice(orderPrice);
		this.setOriginalVolume(originalVolume);
		this.setRemainingVolume(originalVolume);
		
		switch(side){
			case "BUY":
				this.side = BookSide.BUY;
				break;
			case "SELL":
				this.side = BookSide.SELL;
				break;
		}
		
		this.id = userName + productSymbol + price.toString() + System.nanoTime();
	}
	
	public void setUserName(String userName) throws InvalidInputError{
		if(userName == null)
			throw new InvalidInputError("Input Cannot be Null or Empty");
		this.userName = userName;
	}
	
	public void setProduct(String productSymbol) throws InvalidInputError{
		if(productSymbol == null)
			throw new InvalidInputError("Input Cannot be Null or Empty");
		this.productSymbol = productSymbol;
	}
	
	public void setPrice(Price orderPrice) throws InvalidInputError{
		if(orderPrice == null)
			throw new InvalidInputError("Input Cannot be Null or Empty");
		this.price = orderPrice;
	}
	
	public void setOriginalVolume(int originalVolume) throws InvalidTradableValue{
		if (originalVolume <= 0)
			throw new InvalidTradableValue("Cannot have a zero value for Original volume.");
		this.originalVolume = originalVolume;
	}
	
	public String getProduct(){
		return productSymbol;
	}
	
	public Price getPrice(){
		return price;
	}
	
	public int getOriginalVolume(){
		return originalVolume;
	}
	
	public int getRemainingVolume(){
		return remainingVolume;
	}
	
	public int getCancelledVolume(){
		return cancelledVolume;
	}
	
	public void setCancelledVolume(int newCancelledVolume) throws InvalidTradableValue {
		if(newCancelledVolume < 0 || ((newCancelledVolume + this.cancelledVolume) > this.originalVolume))
			throw new InvalidTradableValue("Invalid Cancelled Volume");
		cancelledVolume = newCancelledVolume;
		
	}
	
	public void setRemainingVolume(int newRemainingVolume) throws InvalidTradableValue{
		if(newRemainingVolume < 0 || newRemainingVolume + this.cancelledVolume > this.originalVolume)
			throw new InvalidTradableValue("Invalid Remaining Volume");	
		remainingVolume = newRemainingVolume;
	}
	
	public String getUser(){
		return userName;
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
	
	public boolean isQuote(){
		return false;
	}
	
	public String getId(){
		return id;
	}
	
	public String toString(){
		return this.userName + " order: " +  this.getSide()  
				+ " " + this.originalVolume + " " + this.productSymbol + " at " + this.getPrice()
				+ "(Original Vol: " + this.originalVolume + ", " 
				+ "CXL'd Vol: " + this.cancelledVolume + "), " 
				+ "ID: "+ this.getId();
	}
}
