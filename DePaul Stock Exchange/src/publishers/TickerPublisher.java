package publishers;

import java.util.HashMap;
import java.util.HashSet;

import price.Price;
import users.User;

public class TickerPublisher {

	private  HashMap<String, HashSet<User> > userList = new HashMap<String,  HashSet<User> >();
	private static TickerPublisher ourInstance;
	private HashMap<String, Price> subscriptions = new HashMap<String, Price>();

	public static TickerPublisher getInstance() {
		if (ourInstance == null)
			ourInstance = new TickerPublisher();

		return ourInstance;
	}
	
	public synchronized void unSubscribe(User u, String product) throws NotSubscribedException{
		HashSet<User> temp;	
		
		if (!userList.get(product).contains(u)) {
			throw new NotSubscribedException("User is not subscribed");
		} else {
			temp =  new HashSet<User>(userList.get(product));
			temp.remove(u);
			userList.put(product, temp);
		}
	}	
	
	public synchronized void subscribe(User u, String product) throws AlreadySubscribedException{
		HashSet<User> temp;		
		
		if(userList.containsKey(product) && userList.get(product).contains(u)) {
			throw new AlreadySubscribedException("User is already subscribed");
		}
		else {
			if(userList.containsKey(product)){
				temp =  new HashSet<User>(userList.get(product));
				temp.add(u);
				userList.put(product, temp);
			}
			else{
				temp = new HashSet<User>();
				temp.add(u);
				userList.put(product, temp);
			}
		}
	}
	
	public synchronized void publishTicker(String product, Price p){
		
//		Determine if the new trade Price for the provided stock symbol (product) is greater than (up), less
//		than (down), or the same (equal) as the previous trade price seen for that stock. If the price has
//		moved up, the up-arrow character 'up arrow' ((char) 8593) should be sent to the user. If the price has
//		moved down, the down-arrow character ' down arrow' ((char) 8595) should be sent to the user. If the price
//		is the same as the previous price, the equals character ' =' should be sent to the user. If there is no
//		previous price for the specified stock, the space character ' ' should be sent to the user. These
//		symbols will be used in the next step.
		
//		Then, for each User object in the HashSet or ArrayList for the specified stock symbol (i.e., the
//		product), do the following:
		
//		-Call the User object’s “acceptTicker” method passing the following 3 parameters:
//		---The String stock symbol passed into “publishTicker” (i.e., the product).
//		---The price passed into “publishTicker” (If this is null, a Price object representing $0.00
//		   should be used, do NOT send null to the users)
//		---The characted previously determined that represents the movement of the stock
//		   (up arrow,down arrow,=,<space>).
		char symbol;
		
		if(subscriptions.containsKey(product)){
			if(subscriptions.get(product).greaterThan(p))
				symbol =  ((char)8595);
			else if(subscriptions.get(product).lessThan(p))
				symbol =  ((char)8593);
			else
				symbol = '=';
		}
		else{
			symbol = ' ';
		}
		if(userList.get(product) != null){
			for(User u : userList.get(product)){
				u.acceptTicker(product, p, symbol);
			}
		}
		subscriptions.put(product, p);
	}

}
