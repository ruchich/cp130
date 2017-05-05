package edu.uw.ruc.broker;

import edu.uw.ext.framework.order.PricedOrder;

public class StopSellOrderComparator {

	private int numberOfShares;
	private int orderId;
	private int mTargetPrice;

	public StopSellOrderComparator(){
		
	}	
	 /**
     * Compares this object with the argument.
     * Ordered by their target price in descending order (the highest target price is at the top of the queue),
     *  then the number of shares and lastly by the order of creation
     *
     * @param order - order to be compared to
     *
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object
     */

	public  int compareTo( PricedOrder order) {
		int diff = 0;
		int otherTargetPrice = order.getPrice();
        int otherNumberOfShares = order.getNumberOfShares();
        diff = mTargetPrice < otherTargetPrice ? -1
        		: mTargetPrice > otherTargetPrice ? 1 : 0;
	
        if (diff == 0){
        diff = numberOfShares > otherNumberOfShares ? -1
             : numberOfShares < otherNumberOfShares ? 1 : 0;
        }

        if (diff == 0) {
            int otherOrderId = order.getOrderId();
            diff = orderId < otherOrderId ? -1
                 : orderId > otherOrderId ? 1 : 0;
        }

        return diff;
    }
	
	
	
	
}
