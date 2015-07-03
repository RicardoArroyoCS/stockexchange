package book;

import java.util.HashMap;

import tradable.InvalidTradableValue;
import tradable.Tradable;
import messages.FillMessage;
import messages.InvalidInputError;

public interface TradeProcessor {
		
	HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidInputError, InvalidTradableValue;

}
