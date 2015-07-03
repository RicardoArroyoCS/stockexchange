package messages;

import price.Price;
import tradable.Order.BookSide;

public class FillMessage implements Comparable<FillMessage> {
	private Message message;
	
	public FillMessage(String user, String product, Price price, int volume, String details, BookSide side, 
			String id) throws InvalidInputError{
		message =  MessageFactory.makeMessage(user, product, price, volume, details, side, id);
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
	
	public void setFillVolume(int i) throws InvalidInputError{
		getMessage().setVolume(i);
	}
	
	public void setDetails(String s) throws InvalidInputError{
		getMessage().setDetails(s);
	}
	
	/*
	 * Methods
	 */
	@Override
	public int compareTo(FillMessage fm) {
		long thisPrice = getMessage().getPrice().getValue();
		long thatPrice = fm.getMessage().getPrice().getValue();
		
		if(thatPrice > thisPrice)
			return -1;
		else if(thatPrice < thisPrice)
			return 1;
		return 0;
	}
	
	public String toString(){
		String str = "User: " + getMessage().getUser() + 
				", Product: " + getMessage().getProduct() +
				", Fill Price: " + getMessage().getPrice().toString() +
				", Fill Volume: " + getMessage().getVolume() +
				", Details: " + getMessage().getDetails() +
				", Side: " + getMessage().getSide();
		return str;
	}

}
