package price;

public class Price implements Comparable<Price> {
	private long priceValue;
	private boolean isMarket = false;
	
	/* Constructors */
	
	//Creates a Price object representing the provided value.
	//Remember, a long value passed in of 1499 represents $14.99, 12850 represents $128.50, etc.
	Price(long value){
		this.priceValue = value;
	}
	
	//Creates a Price object representing Market price.
	Price(){
		this.isMarket = true;
	}
	
	/* Price Math */
	
	public long getValue(){
		return this.priceValue;
	}
	
	//Add the value of the Price object passed in to the current Price object’s value and
	//return a new Price object representing that sum
	public Price add(Price p) throws InvalidPriceOperation{
		if(p == null || this == null || p.isMarket() || this.isMarket())
			throw new InvalidPriceOperation("Value is null or a Market Price");
		long sum = this.priceValue + p.getValue();	
		return PriceFactory.makeLimitPrice(sum);
	}
	
	//Subtract the value of the Price object passed in from the current Price object’s
	//value and return a new Price object representing that difference.
	public Price subtract(Price p) throws InvalidPriceOperation{
		if(p == null || this == null || p.isMarket() || this.isMarket())
			throw new InvalidPriceOperation("Value is null or a Market Price");		
		long diff = this.priceValue - p.getValue();	
		return PriceFactory.makeLimitPrice(diff);
	}
	
	//Multiply the value passed in by the current Price object’s value and returns a new
	//Price object representing that product.
	public Price multiply(int p) throws InvalidPriceOperation{
		if(this == null || this.isMarket())
			throw new InvalidPriceOperation("Value is null or a Market Price");		
		long product = this.priceValue * p;	
		return PriceFactory.makeLimitPrice(product);
	}
	
	
	/* Price Comparisons */
	@Override
	public int compareTo(Price p){
		if(this.getValue() > p.getValue())
			return 1;
		else if(this.getValue() < p.getValue())
			return -1;
		else
			return 0;
	}	
	//Return true if the current Price object is greater than or equal to the
	//Price object passed in.
	public boolean greaterOrEqual(Price p){
		if(this.getValue() >= p.getValue() && !this.isMarket() && !p.isMarket())
			return true;
		else
			return false;
	}
	
	//Return true if the current Price object is greater than the Price object
	//passed in.
	public boolean greaterThan(Price p){
		if(this.getValue() > p.getValue() && !this.isMarket() && !p.isMarket())
			return true;
		else
			return false;
	}
	
	//Return true if the current Price object is less than or equal to the Price
	//object passed in
	public boolean lessOrEqual(Price p){
		if(this.getValue() <= p.getValue() && !this.isMarket() && !p.isMarket())
			return true;
		else
			return false;
	}
	
	//Return true if the current Price object is greater than the Price object
	//passed in
	public boolean lessThan(Price p){
		if(this.getValue() < p.getValue() && !this.isMarket() && !p.isMarket())
			return true;
		else
			return false;
	}
	
	//Return true if the Price object passed in holds the same value as the current
	//Price object
	public boolean equals(Price p){
		if(this.getValue() == p.getValue() && !this.isMarket() && !p.isMarket())
			return true;
		else
			return false;
	}
	
	//Return true if the Price is a “market” price, return false if not.
	public boolean isMarket(){
		return isMarket;
	}
	
	//Return true if the Price is negative, return false if the Price is zero or positive. If
	//the Price is a “market” price, return false
	public boolean isNegative(){
		if(this.getValue() < 0 && !this.isMarket() )
			return true;
		else
			return false;
	}
	
	/* Utilities */
	
	//Return the String format of Price. Format requirements:
	public String toString(){
		if(this.isMarket())
			return "MKT";
		else{
			double price = this.getValue() / 100.0; 
			String s = String.format("$%,.2f", price);
			return s;
		}
	}

	
}
