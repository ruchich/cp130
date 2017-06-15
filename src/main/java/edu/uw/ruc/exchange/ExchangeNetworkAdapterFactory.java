package edu.uw.ruc.exchange;

import java.net.SocketException;
import java.net.UnknownHostException;

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
    static final Logger log = LoggerFactory.getLogger(ExchangeNetworkAdapterFactory.class);
	
    /**Constructor*/
    public ExchangeNetworkAdapterFactory(){}
    
    
    /**
     * Instantiates an ExchangeNetworkAdapter.
     * @param exchange - the underlying real exchange

	* @param multicastIP - the multicast ip address used to distribute events

	* @param multicastPort - the port used to distribute events

	* @param commandPort - the listening port to be used to accept command requests

    * @return a newly instantiated ExchangeNetworkAdapter, or null if instantiation fails
    */
    
    @Override
	public ExchangeAdapter newAdapter(StockExchange exchange,
			String multicastIP, int multicastPort, int commandPort) {
		
		ExchangeAdapter	newAdapter = null;
	
			try {
				newAdapter = new ExchangeNetworkAdapter(exchange,multicastIP,multicastPort, commandPort  );
				if(log.isInfoEnabled()){
                    log.info(String.format("Created ExchangeNetworkAdapter"));
                }
			} catch (UnknownHostException | SocketException e) {
				
				final String  msg = String.format("ExcahngeAdapter instantiation  failed for, IP '%s' and port %s", multicastIP, multicastPort);
		        log.warn(msg, e);
		    }
			
	                
		    return newAdapter;
	}
		
	

}
