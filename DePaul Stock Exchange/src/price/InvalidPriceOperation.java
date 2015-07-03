package price;

// General Exception thrown under the Price package if a wrong value is passed
public class InvalidPriceOperation extends Exception{
	InvalidPriceOperation(String s){
		super(s);
	}
}