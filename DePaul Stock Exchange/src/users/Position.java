package users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import price.InvalidPriceOperation;
import price.Price;
import price.PriceFactory;
import tradable.Order.BookSide;

public class Position {
	HashMap<String, Integer> holdings = new HashMap<String, Integer>();
	Price accountCosts = PriceFactory.makeLimitPrice((long)0);
	HashMap<String, Price> lastSale = new HashMap<String, Price>();
	
	public void updatePosition(String product, Price price, BookSide side, int volume) throws InvalidPriceOperation{
		System.out.printf("Product: %s Price: %s BookSide: %s Volume: %d", product, price.toString(),
				side.name(), volume);
		int adjustedVolume = 0;
		
		if(side.name().equals("BUY"))
			adjustedVolume = volume;
		else
			adjustedVolume -= volume;
		
		if(!holdings.containsKey(product))
			holdings.put(product, adjustedVolume);
		else{
			int currentVolume = holdings.get(product);
			currentVolume += adjustedVolume;
			
			if(currentVolume == 0){
				holdings.remove(product);
			}
			else{
				holdings.put(product, currentVolume);
			}
			System.out.println(" Current Volume: " + currentVolume);
		}
		
		long totalPriceVal = price.getValue() * volume;
		Price totalPrice = PriceFactory.makeLimitPrice(totalPriceVal);
		
		if(side.name().equals("BUY")){
			accountCosts = accountCosts.subtract(totalPrice);
		}
		else{
			accountCosts = accountCosts.add(totalPrice);
		}
		System.out.printf(" adjusted volume: %d  account costs %s \n", adjustedVolume,  accountCosts.toString());
	}
	
	public void updateLastSale(String product, Price price){
		lastSale.put(product, price);
	}

	public int getStockPositionVolume(String product){
		if(!holdings.containsKey(product))
			return 0;
		
		return holdings.get(product);
	}
	
	public ArrayList<String> getHoldings(){
		ArrayList<String> h = new ArrayList<String>(holdings.keySet());
		Collections.sort(h);
		
		return h;
	}
	
	public Price getStockPositionValues(String product){
		if(!holdings.containsKey(product))
			return PriceFactory.makeLimitPrice((long)0);
		Price currentPrice = lastSale.get(product);
		
		if(currentPrice == null)
			currentPrice = PriceFactory.makeLimitPrice((long)0);
		
		return	PriceFactory.makeLimitPrice( (currentPrice.getValue() * holdings.get(product)) );
	}
	
	public Price getAccountCosts(){
		return accountCosts;
	}
	
	public Price getAllStockValue() throws InvalidPriceOperation{
		Price totalValue = PriceFactory.makeLimitPrice((long)0);
		for(String product : holdings.keySet()){
			totalValue = totalValue.add(getStockPositionValues(product));
		}
		return totalValue;
	}
	
	public Price getNetAccountValue() throws InvalidPriceOperation{
		return getAllStockValue().add(accountCosts);
	}
}
