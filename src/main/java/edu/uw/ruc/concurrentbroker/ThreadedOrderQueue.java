package edu.uw.ruc.concurrentbroker;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.Order;

/**
 * A simple OrderQueue implementation backed by a TreeSet.
 * @author chq-ruchic
 *
 * @param <T>- the dispatch threshold type
 * @param <E> - the type of order contained in the queue
 * @param <threshold>
 */
public class ThreadedOrderQueue<T,E extends Order> implements 
OrderQueue<T,E>, Runnable{
	/** The current Threshold*/
	private T threshold ;
	
	/** thread responsible for dispatching orders*/
		private Thread dispatchThread;
	
	/** lock used to control access to the queue*/
		private final ReentrantLock queueLock = new ReentrantLock();
		
	/**Condition used to initiate processing orders*/
		
		private final Condition dispatchCondition = queueLock.newCondition();
		
	/** filter used to determine id order is dispatchable*/
	private BiPredicate<T, E> filter;
	
	/** Order Processor used to process the dispatchable order */
	private Consumer<E> orderProcessor;
	
	/** Tree Data Structure*/
	private TreeSet<E> queue;

	
	/** lock used to control access to the processor*/
	private final ReentrantLock processorLock = new ReentrantLock(); ;
	
	/**
	 * Constructor
	 * @param threshold - the initial threshold
	 * @param filter  - the dispatch filter used to control dispatching from this queue
	 */
	public ThreadedOrderQueue(final String name,final T threshold,
			 final BiPredicate<T,E> filter){
		queue = new TreeSet<>();
		this.threshold = threshold;
		this.filter = filter;
		startDispatchThread(name);
	}
	/**
	 * Constructor
	 * @param threshold - the initial threshold
	 * @param filter  - the dispatch filter used to control dispatching from this queue
	 * @param cmp - Comparator to be used for ordering
	 */
	
	public ThreadedOrderQueue(final String name,final T threshold,
			 final BiPredicate<T,E> filter,final Comparator<E> cmp){
		queue = new TreeSet<>(cmp);
		this.threshold = threshold;
		this.filter = filter;
		startDispatchThread(name);
		
	}
	/**
	 * sets the priority of the order queue
	 */
	public void setPriority(final int priority){
		dispatchThread.setPriority(priority);
	}
	
	/**
	 * creates and starts a dispatch thread
	 */
	
	private void startDispatchThread(final String name){
		dispatchThread = new Thread(this, name + "-OrderDispatchThread");
		dispatchThread.setDaemon(true);
		dispatchThread.start();
	}
	/**
	 * Adds the specified order to the queue. Subsequent to adding the order dispatches any dispatchable orders.
	 * @param order - the order to be added to the queue
	 */
	public void enqueue(final E order){
		queueLock.lock();
		try{
		queue.add(order);
		}finally{
			queueLock.unlock();
		}
		dispatchOrders();
		
	}
	
	/**
	 * Removes the highest dispatchable order in the queue. If there are orders in the queue but they do not meet the dispatch threshold order will not be removed and null will be returned.
	 */
	@Override
	public E dequeue(){
		E order = null;
		
		queueLock.lock();
		try{
		if(!queue.isEmpty()){
				
		 order = queue.first();
		 		 
		if((filter !=null)&& filter.test(threshold, order)){
			order = null;
			}else{
			
			queue.remove(order);
			}
		}
	} finally{
			queueLock.unlock();
		}return order;
	}
		
	
	@Override
	public void dispatchOrders(){
		E order;
		queueLock.lock();
		try{
			dispatchCondition.signal();
				
	} finally{
		queueLock.unlock();
		
			}
		}
			
	/** Dispatch orders as long as there are dispatchable orders available
	 * 
	 */
	
	public void run(){
		while(true){
			E order;
			queueLock.lock();
			try{
				while((order= dequeue())== null){
					try{
						dispatchCondition.await();
					} catch(final InterruptedException  iex){
						final Logger log = LoggerFactory.getLogger(this.getClass());
						log.info("Order queue interrupted while waiting");
					}
				}
			}finally{
				queueLock.unlock();
			}
			
			processorLock.lock();
			try{
				if (orderProcessor !=null){
					orderProcessor.accept(order);
				}
			}finally{
			processorLock.unlock();
			}
			
		}
	}
	
	/**
	 * Registers the callback to be used during order processing.
	 * @param proc - the callback to be registered
	 */
	
	public void setOrderProcessor(final Consumer<E> proc){
		processorLock.lock();
		try {		
			orderProcessor = proc;
		}finally{
			processorLock.unlock();
		}
	}
	
	/**
	 * Adjusts the threshold and dispatches orders.
	 */
	
	public final void setThreshold(T threshold){
		this.threshold = threshold;
		dispatchOrders();
	}
	/**
	 * Obtains the current threshold value.
	 */
	public final T getThreshold(){
		return threshold;
	}
}

