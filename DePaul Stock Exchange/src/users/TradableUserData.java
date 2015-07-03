package users;

import tradable.Order.BookSide;

public class TradableUserData {
	private String userName;
	private String symbol;
	private BookSide side;
	private String id;
	
	public TradableUserData(String userIn, String symbolIn, BookSide sideIn, String idIn){
		setUser(userIn);
		setSymbol(symbolIn);
		setSide(sideIn);
		setId(idIn);
	}
	
	
	/* Modifiers */
	private void setUser(String u){
		userName = u;
	}
	
	private void setSymbol(String s){
		symbol = s;
	}
	
	private void setSide(BookSide s){
		switch(s){
		case BUY:
			side = BookSide.BUY;
			break;
		case SELL:
			side = BookSide.SELL;
			break;
		}
	}
	
	private void setId(String idIn){
		id = idIn;
	}

	/* Accessors */
	public String getUserName(){
		return userName;
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	public BookSide getSide(){
		return side;
	}
	
	public String getId(){
		return id;
	}
	
	public String toString(){
		String toReturn = "User " + userName + "," + side.name() + " " + symbol + "(" + id + ")";
		
		return toReturn;
	}
}
