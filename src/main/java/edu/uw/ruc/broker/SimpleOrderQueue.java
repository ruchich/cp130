package edu.uw.ruc.broker;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.BiPredicate;

import edu.uw.ext.framework.broker.*;
import edu.uw.ext.framework.order.*;
/**
 * A simple OrderQueue implementation backed by a TreeSet.
 * @author chq-ruchic
 *
 * @param <T>- the dispatch threshold type
 * @param <E> - the type of order contained in the queue
 * @param <threshold>
 */
public final class SimpleOrderQueue <T,E extends Order> implements 
OrderQueue<T,E>{
	
	T threshold ;
	BiPredicate<T, E> filter;
	Comparator<E> cmp;
	boolean thresholdMet;
	E order;
	
	TreeSet<E> simpleOrder = new TreeSet<E>();
	
	/**
	 * Constructor
	 * @param threshold - the initial threshold
	 * @param filter  - the dispatch filter used to control dispatching from this queue
	 */
	public SimpleOrderQueue(T threshold,
			 java.util.function.BiPredicate<T,E> filter){
		this.threshold = threshold;
		this.filter = filter;
	}
	/**
	 * Constructor
	 * @param threshold - the initial threshold
	 * @param filter  - the dispatch filter used to control dispatching from this queue
	 * @param cmp - Comparator to be used for ordering
	 */
	
	public SimpleOrderQueue(T threshold,
			 java.util.function.BiPredicate<T,E> filter,Comparator<E> cmp){
		this.threshold = threshold;
		this.filter = filter;
		this.cmp = cmp;
	}
	
	/**
	 * Adds the specified order to the queue. Subsequent to adding the order dispatches any dispatchable orders.
	 * @param order - the order to be added to the queue
	 */
	public void enqueue(E order){
		simpleOrder.add(order);
	// check if order is dispatchable
		dispatchOrders();
		
	}
	
	/**
	 * Removes the highest dispatchable order in the queue. If there are orders in the queue but they do not meet the dispatch threshold order will not be removed and null will be returned.
	 */
	public E dequeue(){
		 order = simpleOrder.first();
		thresholdMet = filter.test(threshold, order);
		if(thresholdMet==true){
			simpleOrder.remove(order);
			return order;
		}
			return null;
		}
		
	
	public void dispatchOrders(){
		 order = dequeue();
		//callback registered
		
	}
	
	/**
	 * Registers the callback to be used during order processing.
	 * @param proc - the callback to be registered
	 */
	
	public void setOrderProcessor(java.util.function.Consumer<E> proc){
		
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
