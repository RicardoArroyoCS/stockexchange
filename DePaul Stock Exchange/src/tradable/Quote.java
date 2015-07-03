package tradable;

import messages.InvalidInputError;
import price.Price;

public class Quote {
	private String userName;
	private String productSymbol;
	private QuoteSide buy;
	private QuoteSide sell;
	private int sellVolume;
	private int buyVolume;
	
	/* Constructor */
	public Quote(String userName, String productSymbol, Price buyPrice, 
			int buyVolume, Price sellPrice, int sellVolume) throws InvalidTradableValue, InvalidInputError{
		this.userName = userName;
		this.productSymbol = productSymbol;
		this.buy = new QuoteSide(userName, productSymbol, buyPrice, buyVolume, QuoteSide.BookSide.BUY);
		this.sell = new QuoteSide(userName, productSymbol, sellPrice, sellVolume, QuoteSide.BookSide.SELL);
		if(sellVolume <0)
			throw new InvalidTradableValue("Value is negative.");
		this.sellVolume = sellVolume;
		this.buyVolume = buyVolume;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getProduct(){
		return productSymbol;
	}
	
	public QuoteSide getQuoteSide(String sideIn) throws InvalidInputError, InvalidTradableValue{
		switch(sideIn){
		case "BUY":
			return new QuoteSide(buy);
		case "SELL":
			return new QuoteSide(sell);
		}
		return buy;
	}
	
	public String toString(){
		return userName + " quote " + productSymbol +" " + buy.getPrice().toString() + " x " + buy.getOriginalVolume()
				+  "(Original Vol: "+ buy.getOriginalVolume() + ", CXL'D Vol: " + buy.getCancelledVolume() + ")" 
				+ "[" + buy.getId() + "]"  
				+ " - " + sell.getPrice().toString() + " x " + sell.getOriginalVolume()
				+ "(Original Vol: " + sell.getOriginalVolume() + ". CXL'D Vol: " + sell.getCancelledVolume() + ")"
				+ "[" + sell.getId() + "]"  ;
	}
}
