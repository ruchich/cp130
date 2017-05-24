package edu.uw.ruc.broker;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

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
	
	/** filter used to determine id order is dispatchable*/
	private BiPredicate<T, E> filter;
	
	/** Order Processor used to process the dispatchable order */
	private Consumer<E> orderProcessor;
	
	/** Tree Data Structure*/
	private TreeSet<E> queue;
	
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
	/
	
	/**
	 * Adds the specified order to the queue. Subsequent to adding the order dispatches any dispatchable orders.
	 * @param order - the order to be added to the queue
	 */
	public void enqueue(final E order){
		queue.add(order);
		dispatchOrders();
		
	}
	
	/**
	 * Removes the highest dispatchable order in the queue. If there are orders in the queue but they do not meet the dispatch threshold order will not be removed and null will be returned.
	 */
	@Override
	public E dequeue(){
		E order = null;
		
		if(!queue.isEmpty()){
				
		 order = queue.first();
		 		 
		if(filter.test(threshold, order)){
			queue.remove(order);
			}else{
			order = null;
		}
		}
		return order;
	}
		
	
	@Override
	public void dispatchOrders(){
		E order;
		
		while((order = dequeue())!=null){
			if(orderProcessor != null){
				orderProcessor.accept(order);
			}
		}
				
	}
	
	/**
	 * Registers the callback to be used during order processing.
	 * @param proc - the callback to be registered
	 */
	
	public void setOrderProcessor(final Consumer<E> proc){
		orderProcessor = proc;
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

