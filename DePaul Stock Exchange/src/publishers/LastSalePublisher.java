package publishers;

import java.util.HashMap;
import java.util.HashSet;

import price.Price;
import users.User;

public class LastSalePublisher {
	private static LastSalePublisher ourInstance;
	private  HashMap<String, HashSet<User> > userList = new HashMap<String,  HashSet<User> >();

	public static LastSalePublisher getInstance() throws Exception {
		if (ourInstance == null)
			ourInstance = new LastSalePublisher();

		return ourInstance;
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
	
	public synchronized void publishLastSale(String product, Price p, int v){

//		for loop or for each loop?
//		o Call the User object’s “acceptLastSale” method passing the following 3 parameters:
//			-The String stock symbol passed into “publishLastSale” (i.e., the product).
//			-The last sale price passed into “publishLastSale” (If this is null, a Price object
//			 representing $0.00 should be used, do NOT send null to the users)
//			-The last sale volume passed into “publishLastSale”.
//			-Then, after the “for” loop, make a call to the TickerPublisher’s “publishTicker” method, passing it
//			 the “product” symbol String, and the Price “p” that were passed into this method.
		if(userList.get(product) != null){
			HashSet<User> temp = new HashSet<User>(userList.get(product));
			
			for(User u : temp){
				u.acceptLastSale(product, p, v);
			}
		}
		TickerPublisher.getInstance().publishTicker(product, p);
	}

}
