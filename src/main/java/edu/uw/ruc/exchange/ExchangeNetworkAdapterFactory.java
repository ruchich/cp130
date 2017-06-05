package edu.uw.ruc.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.NetworkExchangeAdapterFactory;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ruc.account.AccountFactoryImpl;
import edu.uw.ruc.account.AccountImpl;

public class ExchangeNetworkAdapterFactory implements NetworkExchangeAdapterFactory {
	/** This class' logger. */
    static final Logger log = LoggerFactory.getLogger(AccountFactoryImpl.class);
	@Override
	public ExchangeAdapter newAdapter(StockExchange exchange,
			String multicastIP, int multicastPort, int commandPort) {
		
		ExchangeAdapter	newAdapter = null;
		try {
			newAdapter = new ExchangeNetworkAdapter(exchange,multicastIP,multicastPort, commandPort  );
	                if(log.isInfoEnabled()){
	                    log.info(String.format("Created ExchangeNetworkAdapter");
	                }
	    } catch (final AccountException e) {
	        final String  msg = String.format("ExchangeNetworkAdapter instantiation failed");
	        log.warn(msg, e);
	    }

	    return newAdapter;
	}
		
		return null;
	}

}
