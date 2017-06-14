package edu.uw.ruc.exchange;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ruc.broker.SimpleBroker;

public class ExchangeNetworkAdapter implements ExchangeAdapter {

	 
   
    /** the exchange used by the broker*/
	private StockExchange realExchange;
	
	/**This Class's logger*/
	private static final Logger logger = LoggerFactory.getLogger(ExchangeNetworkAdapter.class);
	
	/** The event socket*/
	 private MulticastSocket eventSocket;
	 
	 /** Datagram packet used to multicast events*/
	 
	 private DatagramPacket datagramPacket;
	 
	 /** Thread for accepting the connections to the commandlistener*/
	 private CommandListener cmdListener;
	

	/**
     * Constructor.
     *
     * @param exchng - the exchange used to service the network requests

     * @param multicastIP - the ip address used to propagate price changes

	 * @param multicastPort - the ip port used to propagate price changes

     * @param commandPort - the ports for listening for commands
     * @param port the port to connect to
     * @throws: UnknownHostException - if unable to resolve multicast IP address

     * @throws: SocketException - if an error occurs on a socket operation
     */
	
	public  ExchangeNetworkAdapter(StockExchange exchg,  String multicastIP,
            int multicastPort, int commandPort) throws UnknownHostException, SocketException{
		
		realExchange = exchg;
		
		final InetAddress multicastGroup = InetAddress.getByName(multicastIP);
		final byte [] buf = {};
		datagramPacket = new DatagramPacket(buf, 0, multicastGroup,multicastPort);
		
		try{
			eventSocket = new MulticastSocket();
			eventSocket.setTimeToLive(2);
			if(logger.isInfoEnabled()){
				logger.info("Multicasting events to:" + multicastIP + ":" + multicastPort);
			}
		}catch (IOException ex){
			logger.error("Event socket initilzation failed", ex);
			
		}
		cmdListener = new CommandListener (commandPort, exchg);
		Executors.newSingleThreadExecutor().execute(cmdListener);
		realExchange.addExchangeListener(this);
		
	}
	
	@Override
	public void exchangeOpened(ExchangeEvent event) {
		logger.info("#### Echange Opened ###");
		  
	}

	@Override
	public void exchangeClosed(ExchangeEvent event) {
		logger.info("#### Exchange Closed ###");
		   
	      
	}

		
	
/**
 * Processes price change events.
 * event - the event to be processed
 */
	@Override
	public void priceChanged(ExchangeEvent event) {
		
		      
		}


	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
