package messages;

import price.Price;
import tradable.Order.BookSide;

public class CancelMessage implements Comparable<CancelMessage>{
	private Message message;
	
	public CancelMessage(String user, String product, Price price, int volume, String details, 
			BookSide side, String id) throws InvalidInputError {
		message =  MessageFactory.makeMessage(user, product, price, volume, details, side, id);
	}
	
	public CancelMessage(String user, String product, Price price, int volume, String details, 
			String side, String id) throws InvalidInputError {
		
		BookSide bside = BookSide.BUY;
		switch(side){
		case "BUY":
			bside = BookSide.BUY;
			break;
		case "SELL":
			bside = BookSide.SELL;
			break;
		}
		message =  MessageFactory.makeMessage(user, product, price, volume, details, bside, id);
	}
	
	private Message getMessage(){
		return message;
	}
	
	/*
	 * Accessors
	 */
	public String getUser(){
		return getMessage().getUser();
	}
	
	public String getProduct(){
		return getMessage().getProduct();
	}
	
	public Price getPrice(){
		return getMessage().getPrice();
	}
	
	public int getVolume(){
		return getMessage().getVolume();
	}
	public String getDetails(){
		return getMessage().getDetails();
	}
	public String getSide(){
		return getMessage().getSide();
	}
	
	public String getId(){
		return getMessage().getId();
	}
	
	/*
	 * Methods
	 */
	@Override
	public int compareTo(CancelMessage cm) {
		long thisPrice = getMessage().getPrice().getValue();
		long thatPrice = cm.getMessage().getPrice().getValue();
		
		if(thatPrice > thisPrice)
			return -1;
		else if(thatPrice < thisPrice)
			return 1;
		return 0;
	}
	
	public String toString(){
		String str = "USER: " + getMessage().getUser() + 
				", Product: " + getMessage().getProduct() +
				", Price: " + getMessage().getPrice().toString() +
				", Volume: " + getMessage().getVolume() +
				", Details: " + getMessage().getDetails() +
				", Side: " + getMessage().getSide();
		
		return str;
	}
}
