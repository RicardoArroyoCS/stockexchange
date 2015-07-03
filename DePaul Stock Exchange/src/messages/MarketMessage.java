package messages;

public class MarketMessage {
	private enum state {	CLOSED, PREOPEN, OPEN	}
	private state mktstate;
	
	public MarketMessage(String s) throws InvalidInputError
	{
		setState(s);
	}
	
	public String getState(){
		return mktstate.name();
	}
	
	private void setState(String s) throws InvalidInputError
	{
		if(!s.equals("CLOSED") && !s.equals("PREOPEN") && !s.equals("OPEN") )
			throw new InvalidInputError("Invalid side");

		switch(s.toUpperCase()){
		case "CLOSED":
			this.mktstate = state.CLOSED;
			break;
		case "PREOPEN":
			this.mktstate = state.PREOPEN;
			break;
		case "OPEN":
			this.mktstate = state.OPEN;
			break;
		}
	}
	
	public String toString(){
		return "Current Market Status: " + getState();
	}
}
