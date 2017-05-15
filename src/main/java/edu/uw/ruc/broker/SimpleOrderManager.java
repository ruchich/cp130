package edu.uw.ruc.broker;

import java.util.Comparator;
import java.util.function.*;

import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;

public class SimpleOrderManager implements OrderManager{

	
	private String mStockTickerSymbol;
		private OrderQueue<Integer,Order> StopBuyOrderQueue;
	
	private OrderQueue<Integer,Order> StopSellOrderQueue;
	
	public SimpleOrderManager(String stockTickerSymbol){
		mStockTickerSymbol = mStockTickerSymbol;
	}
	
	public SimpleOrderManager(String stockTickerSymbol,final int price){
		this(stockTickerSymbol);
		// Create the stop buy order queue and associated pieces
		
		StopBuyOrderQueue = new SimpleOrderQueue<>(price,
				(t,o) -> o.getPrice()<= t,
				Comparator.comparing(StopBuyOrder::getPrice)
				.thenComparing(StopBuyOrder::compareTo));
		
		
		StopSellOrderQueue = new SimpleOrderQueue<>(price,
				(t,o) -> o.getPrice()< t,
				Comparator.comparing(StopBuyOrder::getPrice)
				.thenComparing(StopBuyOrder::compareTo).reversed());
	}
			
	
	
	
	
	/**
	 * Respond to a stock price adjustment by setting threshold on dispatch filters.
	 * @param price - price of the stock
	 */

	
	public void adjustPrice(int price) {
		
	}

}
