package book;

public class TradeProcessorFactory {
	public static TradeProcessorFactory currentInstance = new TradeProcessorFactory();
	
	public static TradeProcessorFactory getInstance(){
		return currentInstance;
	}
	
	public TradeProcessor makeTradeProcessor(ProductBookSide pb){
		return new TradeProcessorPriceTimeImpl(pb);
	}
}
