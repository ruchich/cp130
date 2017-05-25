package edu.uw.ruc.concurrentbroker;

import java.util.HashMap;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import edu.uw.ruc.concurrentbroker.ThreadedBroker;
import edu.uw.ruc.concurrentbroker.ThreadedOrderManager;
import edu.uw.ruc.concurrentbroker.ThreadedOrderQueue;

public class ThreadedBroker implements Broker, ExchangeListener{

	/**THis Class's logger*/
	private static final Logger logger = LoggerFactory.getLogger(ThreadedBroker.class);
	
	/** This broker's name*/
	private String name;
	
	
	/** The Broker's account manager*/
	private AccountManager accountManager;
	
	/** the exchange used by the broker*/
	private StockExchange stockExchange;
	
	/** the set of order manager used by the broker*/
	private HashMap<String, OrderManager> orderManagerMap;
	
	/** the market order queue*/
	protected OrderQueue<Boolean,Order> marketOrders;
	
	
	
	/** Constructor for sub classes*/
	protected ThreadedBroker( final String brokerName, 
							final StockExchange exchg,
							final AccountManager acctMgr){
		name = brokerName;
		accountManager = acctMgr;
		stockExchange = exchg;
	}
	
	/** Constructor*/
	public ThreadedBroker( final String brokerName, final AccountManager acctMgr, final StockExchange exchg){
		this(brokerName, exchg,acctMgr );
		
		//crete the market order queue and order processor*/
		final ThreadedOrderQueue<Boolean, Order> marketQueue;
		marketQueue = new ThreadedOrderQueue<>("MARKET", stockExchange.isOpen(),
													(Boolean t, Order o)->t);
		marketQueue.setPriority(Thread.MAX_PRIORITY);
		
		marketOrders = marketQueue;
		Consumer<Order>stockTrader = (order) -> {
			logger.info(String.format("Executing - %s", order));
			final int sharePrice = stockExchange.executeTrade(order);
			try{
				final Account acct = accountManager.getAccount(order.getAccountId());
				acct.reflectOrder(order, sharePrice);
				logger.info(String.format("New balance - %d", acct.getBalance()));
			}catch( final AccountException ex){
				logger.error(String.format("Unable to update account, %s", order.getAccountId()));
			}
		};
		marketOrders.setOrderProcessor(stockTrader);
		
		//create the order manager
		initializeOrderManagers();
		stockExchange.addExchangeListener(this);
	}
	

	/** initialize order manager*/
	protected final void initializeOrderManagers(){
		orderManagerMap = new HashMap<>();
		final Consumer<StopBuyOrder> moveBuy2MarketProc = (StopBuyOrder order) -> marketOrders.enqueue(order);
		final Consumer<StopSellOrder> moveSell2MarketProc = (StopSellOrder order) -> marketOrders.enqueue(order);
		for(String ticker : stockExchange.getTickers()){
			final int currPrice = stockExchange.getQuote(ticker).getPrice();
			final OrderManager orderMgr = createOrderManager(ticker, currPrice);
			orderMgr.setBuyOrderProcessor(moveBuy2MarketProc);
			orderMgr.setSellOrderProcessor(moveSell2MarketProc);
			orderManagerMap.put(ticker, orderMgr);
			if(logger.isInfoEnabled()){
				logger.info(String.format("Initialized order manager for '%s' @ %d",ticker,currPrice));
				
			}
			
		}
	}
	
	/** create an appropriate order manager for this order.*/
	
	protected OrderManager createOrderManager( final String ticker, final int initialPrice){
	return new ThreadedOrderManager(ticker, initialPrice);
}
	/**
	 * upon the opening of exchnage sets the market dispatch filter threshold and porcesses any available order
	 * @param event
	 */
	
	public synchronized final void priceChanged(final ExchangeEvent event){
		checkInvariants();
		if(logger.isInfoEnabled()){
			logger.info(String.format("Processing price change[%s:%d]",
					event.getTicker(), event.getPrice()));
		}
		OrderManager orderMgr;
		
		orderMgr = orderManagerMap.get(event.getTicker());
		if(orderMgr !=null){
			orderMgr.adjustPrice(event.getPrice());
	}
	}

	/**
	 * upon the opening of exchnage sets the market dispatch filter threshold and porcesses any available order
	 * @param event
	 */
	
	public synchronized final void exchangeOpened(final ExchangeEvent event){
		checkInvariants();
		logger.info("#### Market Opened ###");
		marketOrders.setThreshold(Boolean.TRUE);
	}
	
	/**
	 * upon the opening of exchnage sets the market dispatch filter threshold and porcesses any available order
	 * @param event
	 */
	
	public synchronized final void exchangeClosed(final ExchangeEvent event){
		checkInvariants();
		marketOrders.setThreshold(Boolean.FALSE);
		logger.info("#### Market Closed ###");
	}
	
	/**
	 * Get the name of this broker.
	 * @return name of the broker
	 */
	public String getName(){
		return name;
		
	}
	/**
	 * create the account
	 * @param userName
	 * @param password
	 * @param balance
	 */
	public synchronized final Account createAccount(final String userName, final String password,
								final int balance)throws BrokerException{
		checkInvariants();
		try{
			return accountManager.createAccount(userName, password, balance);
			}catch(final AccountException e){
				throw new BrokerException("Unable to create account", e);
			}
		
	}
	
	/**
	 * delete the account
	 * @param userName
	 * @param password
	 * @param balance
	 */
	public synchronized final void deleteAccount(final String userName)throws BrokerException{
		checkInvariants();
		try{
			 accountManager.deleteAccount(userName);
			}catch(final AccountException e){
				throw new BrokerException("Unable to delete account", e);
			}
		
	}
	
	
	public synchronized final Account getAccount(final String userName,final String password )throws BrokerException{
		checkInvariants();
		try{
			
			if(accountManager.validateLogin(userName, password)){
				return accountManager.getAccount(userName);
			}else{
				throw new BrokerException("Invalid Username/password");
			}
		}catch(final AccountException e){
			throw new BrokerException("unable to access account.", e);
		}
	}
	
	/**
	 * Place a market buy order with the borker.
	 * @param order  the order being placed with the broker
	 */
	public synchronized final void placeOrder(final MarketBuyOrder order){
		checkInvariants();
		marketOrders.enqueue(order);
		
	}
	
	/**
	 * Place a market sell order with the borker.
	 * @param order  the order being placed with the broker
	 */
	public synchronized final void placeOrder(final MarketSellOrder order){
		checkInvariants();
		marketOrders.enqueue(order);
		
	}
	
	private synchronized OrderManager orderManagerLookup(final String ticker) throws
	  BrokerException{
		final OrderManager orderMgr = orderManagerMap.get(ticker);
		if (orderMgr == null){
			throw new BrokerException("Requested Stock, '%s' does not exist");
		}
		return orderMgr;
	}
	
	/**
	 * Place an order with the borker.
	 * @param order  the order being placed with the broker
	 */
	public synchronized final void placeOrder(final StopBuyOrder order)throws BrokerException{
		checkInvariants();
		orderManagerLookup(order.getStockTicker()).queueOrder(order);
	}
	
	
	/**
	 * Place an order with the borker.
	 * @param order  the order being placed with the broker
	 */
	public synchronized final void placeOrder(final StopSellOrder order)throws BrokerException{
		checkInvariants();
		orderManagerLookup(order.getStockTicker()).queueOrder(order);
	}
	
	/**
	 * Get a price quote for a stock.
	 * @param ticker - the stocks ticker symbol
	 * @return the stocks current price
	 */
	public synchronized final StockQuote requestQuote (final String symbol)
            throws BrokerException{
		checkInvariants();
		 final StockQuote quote = stockExchange.getQuote(symbol);
		 if(quote == null){
			 throw new BrokerException(String.format("Quote not available for '%s'.", symbol));
		 }
		 return quote;
	}
	
	/**
	 * Release broker resources
	 */
	public synchronized void close() 
			throws BrokerException{
		try{
			stockExchange.removeExchangeListener(this);
			accountManager.close();
			orderManagerMap = null;
		}catch (final AccountException e){
			throw new BrokerException("Attempt to close the broker failed", e);
		}
	}
		
		/**
		 * method to check if broker is properly inittialized, exception is thrown if broker 
		 * is in illegal state
		 */
		private void checkInvariants(){
			if(name==null||
			   accountManager ==null||
			   stockExchange== null ||
			   orderManagerMap == null ||
			   marketOrders == null){
				throw new IllegalStateException("Broker is not properly initialized");
			}
		}
}