package messages;

import price.Price;
import tradable.Order.BookSide;

public class MessageFactory {
	private static Message message;
	
	public static Message makeMessage(String user, String product, Price price, int volume, 
			String details, BookSide side, String id) throws InvalidInputError{
		message =  new MessageImpl(user, product, price, volume, details, side, id);
		return message;
	}
}
