package edu.uw.ruc.broker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerFactory;
import edu.uw.ext.framework.exchange.StockExchange;

public final class SimpleBrokerFactory implements BrokerFactory{
	
	/**
	 * Instantitaes a new simpleBroker
	 * @param name the broker's name
	 * @param acctMgr the account manager to be used by the broker
	 * @param ecch the exchange to be uised by the broker
	 * @return a newly created simple broker instance
	 */
	
	public Broker newBroker( final String name, final AccountManager acctMgr,
								final StockExchange exch){
		return new SimpleBroker(name, acctMgr, exch);
	}

}
