package publishers;

import price.Price;

public class MarketDataDTO {
	public String product;
	public Price buyPrice;
	public int buyVolume;
	public Price sellPrice;
	public int sellVolume;
	
	public MarketDataDTO(String productIn, Price buyPriceIn, int buyVolumeIn,
			Price sellPriceIn, int sellVolumeIn) {
		product = productIn;
		buyPrice = buyPriceIn;
		buyVolume = buyVolumeIn;
		sellPrice = sellPriceIn;
		sellVolume = sellVolumeIn;
	}
	
	/*
	 * Debugging
	 */
	public String toString(){
	/*	
		String str = "Product: " + this.product
				+ ", Buy Price: " + this.buyPrice.toString() +
				" , Buy Volume: " + this.buyVolume + 
				" , Sell Price: " + this.sellPrice.toString() + 
				" , Sell Volume: " + this.sellVolume; */
		String str = this.product + " " + this.buyVolume + "@" + this.buyPrice.toString() + 
		" x " + this.sellVolume + "@" + this.sellPrice.toString();
		return str;
	}

}
