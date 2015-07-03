package tradable;

import messages.InvalidInputError;
import price.Price;

public interface Tradable {

	//Returns the product symbol (i.e., IBM, GOOG, AAPL, etc.) that the Tradable works with.
	String getProduct();
	
	//Returns the price of the Tradable.
	Price getPrice();
	
	//Returns the original volume (i.e., the original quantity) of the Tradable.
	int getOriginalVolume();
	
	//Returns the remaining volume (i.e., the remaining quantity) of the Tradable.
	int getRemainingVolume();
	
	//Returns the cancelled volume (i.e., the cancelled quantity) of the Tradable.
	int getCancelledVolume();
	
	//Sets the Tradable’s cancelled quantity to the value
	//passed in. This method should throw an exception if the value is invalid (i.e., if the value is negative, or if the
	//requested cancelled volume plus the current remaining volume exceeds the original volume).
	void setCancelledVolume(int newCancelledVolume) throws InvalidTradableValue;
	
	//Sets the Tradable’s remaining quantity to the value
	//passed in. This method should throw an exception if the value is invalid (i.e., if the value is negative, or if the
	//requested remaining volume plus the current cancelled volume exceeds the original volume).
	void setRemainingVolume(int newRemainingVolume) throws InvalidTradableValue;
	
	void setUserName(String userName) throws InvalidInputError;
	
	void setProduct(String productSymbol) throws InvalidInputError;
	
	void setPrice(Price orderPrice) throws InvalidInputError;
	
	void setOriginalVolume(int originalVolume) throws InvalidTradableValue;
	
	//Returns the User id associated with the Tradable.
	String getUser();
	
	//Returns the side (“BUY”/”SELL”) of the Tradable.
	String getSide();
	
	//Returns true if the Tradable is part of a Quote, returns false if not (i.e., false if it’s part of
	//an order)
	boolean isQuote();
	
	//Returns the Tradable “id” – the value each tradable is given once it is received by the system.
	String getId();
}
