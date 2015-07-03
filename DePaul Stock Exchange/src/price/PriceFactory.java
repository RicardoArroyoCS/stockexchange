package price;

import java.util.HashMap;

public class PriceFactory {
	private static HashMap<Long, Price> priceHolder = new HashMap<Long, Price>();
	private static Price mktPrice = new Price();
	
	//Creates a (limit) price object representing the value held in the
	//provided String value. *
	public static Price makeLimitPrice(String value){
		 double val =  Math.round((Double.parseDouble(value.replaceAll("[$,]", ""))) * 100.0);
		 long valLong = (long)val;
		 
		 if(priceHolder.containsKey(valLong))
			 return priceHolder.get(valLong);
		 else{
			 Price price = new Price(valLong);
			 priceHolder.put(valLong, price);
			 return price;
		 }
	}
	
	//Creates a (limit) price object representing the value held in the
	//provided long value. This long value is the price in cents. A value of 1499 represents a price of $14.99.
	public static Price makeLimitPrice(long value){
		 if(priceHolder.containsKey(value))
			 return priceHolder.get(value);
		 else{
			 Price price = new Price(value);
			 priceHolder.put(value, price);
			 return price;
		 }
	}
	
	//Creates a market price object.
	public static Price makeMarketPrice(){
		return mktPrice;
	}
}
