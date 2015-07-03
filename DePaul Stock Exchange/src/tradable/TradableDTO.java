package tradable;

import price.Price;
import tradable.Order.BookSide;

public class TradableDTO implements Comparable<TradableDTO>{
	public String product;
	public Price price;
	public int originalVolume;
	public int remainingVolume;
	public int cancelledVolume;
	public String user;
	public BookSide side;
	public boolean isQuote;
	public String id;

	/*
	 *    constructor TradableDTO(String, Price, int, int, int, String, String, boolean, String) is undefined
	 */
	public TradableDTO(String product, Price price, int originalVolume, int remainingVolume,
			int cancelledVolume, String user, String side, boolean isQuote, String id ){
		this.product = product;
		this.price = price;
		this.originalVolume = originalVolume;
		this.remainingVolume = remainingVolume;
		this.cancelledVolume = cancelledVolume;
		this.user = user;
		
		switch(side){
		case "BUY":
			this.side = BookSide.BUY;
			break;
		case "SELL":
			this.side = BookSide.SELL;
			break;
		}
		
		this.isQuote = isQuote;
		this.id = id;
	}

	public int compareTo(TradableDTO t){
		if(this.price.getValue() > t.price.getValue())
			return 1;
		else if(this.price.getValue() < t.price.getValue())
			return -1;
		else
			return 0;
	}		
	
	public String toString(){
		StringBuilder strout = new StringBuilder();
		strout.append("Product: " + this.product + " Price: " + this.price.toString()
				+ " OriginalVolume: " + this.originalVolume 
				+ ", RemainingVolume: " + this.remainingVolume
				+ ", CancelledVolume: " + this.cancelledVolume
				+ ", User: " + user
				+ ", Side: ");
		
		switch(side){
		case BUY:
			strout.append("BUY");
			break;
		case SELL:
			strout.append("SELL");
			break;
		}
		
		strout.append(", isQuote: " + isQuote + ", id: "+ id);
		return strout.toString();
	}

}
