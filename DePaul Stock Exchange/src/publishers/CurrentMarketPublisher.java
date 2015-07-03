package publishers;

import java.util.HashMap;
import java.util.HashSet;

import users.User;

public class CurrentMarketPublisher {

	private static CurrentMarketPublisher ourInstance;
	private HashMap<String, HashSet<User> > UserList = new HashMap<String,  HashSet<User> >();
	
	public static CurrentMarketPublisher getInstance() {
		if (ourInstance == null)
			ourInstance = new CurrentMarketPublisher();

		return ourInstance;
	}

	//Debugging
	public void printSubscribe(String product){
		System.out.println("Printing Subscrubed List");
		if(UserList.get(product)!= null){
			for(User u : UserList.get(product)){
				System.out.println(u.getUserName());
			}
		}
	}
	
	public synchronized void subscribe(User u, String product) throws AlreadySubscribedException{
		HashSet<User> temp;		
		if(UserList.containsKey(product) && UserList.get(product).contains(u) ) {
			throw new AlreadySubscribedException("User is already subscribed");
		}
		else {
			if(UserList.containsKey(product)){
				//temp =  deepCopy(UserList.get(product));
				temp =  new HashSet<User>(UserList.get(product));
				temp.add(u);
				UserList.put(product, temp);
			}
			else{
				temp = new HashSet<User>();
				temp.add(u);
				UserList.put(product, temp);
			}
		}
	}

	public synchronized void unSubscribe(User u, String product) throws NotSubscribedException{
		HashSet<User> temp;	
		
		if (!UserList.get(product).contains(u)) {
			throw new NotSubscribedException("User is not subscribed");
		} else {
			//temp = deepCopy( UserList.get(product));
			temp =  new HashSet<User>(UserList.get(product));
			temp.remove(u);
			UserList.put(product, temp);
		}
	}

	public synchronized void publishCurrentMarket(MarketDataDTO md) {
		//-Call the User object’s “acceptCurrentMarket” method passing the following 5 parameters:
		//---The String stock symbol taken from the MarketDataDTO passed in.
		//---The BUY side price taken from the MarketDataDTO passed in. (If this is null, a Price
		//   object representing $0.00 should be used, do NOT send null to the users)
		//   ^^^ need null object design pattern
		//---The BUY side volume taken from the MarketDataDTO passed in.
		//---The SELL side price taken from the MarketDataDTO passed in. (If this is null, a Price
		//   object representing $0.00 should be used, do NOT send null to the users)
	    //   ^^^ need null object design pattern
		//---The SELL side volume taken from the MarketDataDTO passed in.
		
		if(UserList.get(md.product) != null){
			HashSet<User> temp = new HashSet<User>(UserList.get(md.product));
			
			for(User u : UserList.get(md.product) ){
				u.acceptCurrentMarket(md.product, md.buyPrice, md.buyVolume,
						md.sellPrice, md.sellVolume);
			}
		}

	}

}
