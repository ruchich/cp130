package edu.uw.ruc.exchange;

import java.io.IOException;
import static edu.uw.ruc.exchange.ProtocolConstants.ENCODING;
import static edu.uw.ruc.exchange.ProtocolConstants.PRICE_CHANGE_EVNT;
import static edu.uw.ruc.exchange.ProtocolConstants.OPEN_EVNT;
import static edu.uw.ruc.exchange.ProtocolConstants.CLOSED_EVNT;
import static edu.uw.ruc.exchange.ProtocolConstants.ELEMENT_DELIMITER;

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
	/** exchnge is opening*/
	@Override
	public void exchangeOpened(ExchangeEvent event) {
		logger.info("#### Exchange Opened ###");
		  try{
			  sendMulticastEvent(OPEN_EVNT);
		  }catch( final IOException ex){
			  logger.error("Error Joining price change group:", ex);
		  }
	}
	/** THe exchange has closed- notify clients*/

	@Override
	public void exchangeClosed(ExchangeEvent event) {
		logger.info("#### Exchange Closed ###");
		try{
			  sendMulticastEvent(CLOSED_EVNT);
		  }catch( final IOException ex){
			  logger.error("Error Joining price change group:", ex);
		  }
	}
	      
	

		
	
/**
 * Processes price change events.
 * event - the event to be processed
 */
	@Override
	public void priceChanged(ExchangeEvent event) {
		
		 final String symbol = event.getTicker();
		 final int price = event.getPrice();
		 final String msg = String.join(ELEMENT_DELIMITER,PRICE_CHANGE_EVNT,symbol,
				 Integer.toString(price));
		 logger.info(msg);
		 try{
			 sendMulticastEvent(msg);
		 }catch(final IOException ex){
			  logger.error("Error multicasting price change ", ex);
		  }
		 
		}

	/**sending multicast event*/
	
	private synchronized void sendMulticastEvent(final String msg)throws IOException{
		final byte[] buf = msg.getBytes(ENCODING);
		datagramPacket.setData(buf);
		datagramPacket.setLength(buf.length);
		eventSocket.send(datagramPacket);
		
	}
/** close the adapter*/
	@Override
	public void close() {
		realExchange.removeExchangeListener(this);
		cmdListener.terminate();
		eventSocket.close();
		
	}

}
