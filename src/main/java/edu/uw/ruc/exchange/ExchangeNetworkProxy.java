package edu.uw.ruc.exchange;

import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.Order;

public class ExchangeNetworkProxy implements StockExchange{

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getTickers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StockQuote getQuote(String ticker) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addExchangeListener(ExchangeListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeExchangeListener(ExchangeListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int executeTrade(Order order) {
		// TODO Auto-generated method stub
		return 0;
	}

}
