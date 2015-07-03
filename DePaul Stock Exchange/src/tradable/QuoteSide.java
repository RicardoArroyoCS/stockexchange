package tradable;

import messages.InvalidInputError;
import price.Price;

public class QuoteSide implements Tradable {
	
	public enum BookSide {	BUY, SELL	}
	
	private String userName;
	private String productSymbol;
	private String id;
	private BookSide side;
	private Price price;
	private int originalVolume;
	private int remainingVolume;
	private int cancelledVolume;
	
	public QuoteSide(String userName, String productSymbol, Price orderPrice, int originalVolume, BookSide side)
			throws InvalidTradableValue, InvalidInputError{
		this.setUserName(userName);
		this.setProduct(productSymbol);
		this.setPrice(orderPrice);
		this.setOriginalVolume(originalVolume);
		this.setRemainingVolume(originalVolume);
		this.side = side;
		this.id = userName + productSymbol + System.nanoTime();
	}
	
	public QuoteSide(QuoteSide qs)
			throws InvalidTradableValue, InvalidInputError{//copy constructor 
		this.setUserName(qs.userName);
		this.setProduct(qs.productSymbol);
		this.setPrice(qs.price);
		this.setOriginalVolume(qs.originalVolume);
		this.remainingVolume = qs.remainingVolume;
		this.cancelledVolume = qs.cancelledVolume;
		this.side = qs.side;
		this.id = qs.userName + qs.productSymbol + System.nanoTime();
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
	
	public void setCancelledVolume(int newCancelledVolume){
		cancelledVolume = newCancelledVolume;
	}
	
	public void setRemainingVolume(int newRemainingVolume){
		//originalVolume = remainingVolume;
		remainingVolume = newRemainingVolume;
		
	}
	
	public String getUser(){
		return userName;
	}
	
	public String getSide(){//BookSide?
		switch(side){
		case BUY:
			return "BUY";
		case SELL:
			return "SELL";
		}
		return "";		
	}
	
	public boolean isQuote(){
		return true;
	}
	
	public String getId(){
		return id;
	}
	
	public String toString(){
		return this.price.toString() + " x " 
				+ (this.originalVolume- this.cancelledVolume)
				+ "[" + this.getId() + "]" ;
	}
}
