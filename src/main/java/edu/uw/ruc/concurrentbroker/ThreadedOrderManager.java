package edu.uw.ruc.concurrentbroker;

import java.util.Comparator;
import java.util.function.Consumer;

import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import edu.uw.ruc.concurrentbroker.ThreadedOrderQueue;

public final class ThreadedOrderManager implements OrderManager{

	/** Symbol of the stock */
	private String stockTickerSymbol;
	
	/** Queue for stop buy orders*/
	protected OrderQueue<Integer,StopBuyOrder> stopBuyOrderQueue;
	
	/** Queue for stop sell orders*/
	protected OrderQueue<Integer,StopSellOrder> stopSellOrderQueue;
	
	/**Constructor used by sub classes*/
	public ThreadedOrderManager(String stockTickerSymbol){
		this.stockTickerSymbol = stockTickerSymbol;
	}
	
	/**Constructor*/
	
	public ThreadedOrderManager(final String stockTickerSymbol,final int price){
		this(stockTickerSymbol);
		// Create the stop buy order queue and associated pieces
		
		stopBuyOrderQueue = new ThreadedOrderQueue<>(stockTickerSymbol + "-StopBuy", price,
				(t,o) -> o.getPrice()<= t,
				Comparator.comparing(StopBuyOrder::getPrice)
				.thenComparing(StopBuyOrder::compareTo));
		
		
		stopSellOrderQueue = new ThreadedOrderQueue<>(stockTickerSymbol + "-StopSell",price,
				(t,o) -> o.getPrice()>= t,
				Comparator.comparing(StopSellOrder::getPrice).reversed()
				.thenComparing(StopSellOrder::compareTo));
	}
			
	
	
	
	
	/**
	 * Respond to a stock price adjustment by setting threshold on dispatch filters.
	 * @param price - new price of the stock
	 */

	
	public void adjustPrice(int price) {
		stopBuyOrderQueue.setThreshold(price);
		
		stopSellOrderQueue.setThreshold(price);
	}
	
	/**Queue a stop buy order./
	 * order - the order to be queued
	 */
	public final void queueOrder(StopBuyOrder order){
		stopBuyOrderQueue.enqueue(order);
	}
	
	/**Queue a stop sell order./
	 * order - the order to be queued
	 */
	public final void queueOrder(StopSellOrder order){
		stopSellOrderQueue.enqueue(order);
	}
	
	/**Registers the processor to be used during buy order processing. This will be passed on to the order queues as the dispatch callback.
	 * @ param processor - the callback to be registered
	 */
	public final void setBuyOrderProcessor(final Consumer<StopBuyOrder> processor){
		stopBuyOrderQueue.setOrderProcessor(processor);
	}

	/**Registers the processor to be used during sell order processing. This will be passed on to the order queues as the dispatch callback.
	 * @ param processor - the callback to be registered
	 */
	public final void setSellOrderProcessor(final Consumer<StopSellOrder> processor){
		stopSellOrderQueue.setOrderProcessor(processor);
	}
	
	/**
	 * Gets the stock ticker symbol for the stock managed by this stock manager.
	 * @return the stock ticker symbol
	 */
	
	public final String getSymbol(){
		return stockTickerSymbol;
	}
		
	
}

