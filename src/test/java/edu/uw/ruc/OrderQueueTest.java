package edu.uw.ruc;

import java.util.function.BiPredicate;

import test.AbstractOrderQueueTest;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.broker.DispatchFilter;
import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import edu.uw.ruc.broker.SimpleOrderQueue;
import edu.uw.ruc.broker.StopBuyOrderComparator;
import edu.uw.ruc.broker.StopSellOrderComparator;

/**
 * Concrete subclass of AbstractQueueTest, provides implementations of the 
 * createStopBuyOrderQueue, createStopSellOrderQueue and createAnyOrderQueue
 * methods which create instances of "my" OrderQueue implementation class, using
 * "my" Comparator implementations.
 */
public class OrderQueueTest extends AbstractOrderQueueTest {
	 /**
     * Creates an instance of "my" OrderQueue implementation class, using
     * an instance of "my" implementation of Comparator that is intended to
     * order StopBuyOrders.
     *
     * @param filter the OrderDispatch filter to be used
     * 
     * @return a new OrderQueue instance
     */
	@Override
	protected final OrderQueue<Integer,StopBuyOrder> createStopBuyOrderQueue(
                        final DispatchFilter<Integer, StopBuyOrder> filter) {
        
        return new SimpleOrderQueue<>(0, filter, new StopBuyOrderComparator());
    }

    /**
     * Creates an instance of "my" OrderQueue implementation class, using
     * an instance of "my" implementation of Comparator that is intended to
     * order StopSellOrders.
     *
     * @param filter the OrderDispatch filter to be used
     * 
     * @return a new OrderQueue instance
     */
	@Override
    protected final OrderQueue<Integer,StopSellOrder> createStopSellOrderQueue(
                          final DispatchFilter<Integer, StopSellOrder> filter) {
       
        return new SimpleOrderQueue<>(0, filter, new StopSellOrderComparator());
    }
    
    /**
     * Creates an instance of "my" OrderQueue implementation class, the queue
     * will order the Orders according to their natural ordering.
     *
     * @param filter the OrderDispatch filter to be used
     * 
     * @return a new OrderQueue instance
     */
    @Override
    protected final OrderQueue<Boolean,Order> createAnyOrderQueue(
                            final DispatchFilter<Boolean, Order> filter) {
        
        return new SimpleOrderQueue<Boolean, Order>(true, filter);
    }

	@Override
	protected OrderQueue<Integer, StopBuyOrder> createStopBuyOrderQueue(
			BiPredicate<Integer, StopBuyOrder> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected OrderQueue<Integer, StopSellOrder> createStopSellOrderQueue(
			BiPredicate<Integer, StopSellOrder> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected OrderQueue<Boolean, Order> createAnyOrderQueue(
			BiPredicate<Boolean, Order> filter) {
		// TODO Auto-generated method stub
		return null;
	}
}
