package edu.uw.ruc.exchange;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ruc.broker.SimpleBroker;

public class ExchangeNetworkAdapter implements ExchangeAdapter {

	 /** The server multicast address. */
    private String multicastAddress;

    /** The server multicast port. */
    private int multicastPort;
   
    /** the exchange used by the broker*/
	private StockExchange stockExchange;
	
	/**This Class's logger*/
	private static final Logger logger = LoggerFactory.getLogger(ExchangeNetworkAdapter.class);
	
	 /** Milliseconds per second. */
    private static final int ONE_SECOND = 1000;
	
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
	
	public void ExchangeNetworkAdapter(StockExchange exchg,  String multicastAddress,
            int multicastPort, int commandPort) throws UnknownHostException, SocketException{
		this.multicastAddress = multicastAddress;
		this.multicastPort = multicastPort;
		stockExchange = exchg;
		exchg.addExchangeListener(this);
		
	}
	
	@Override
	public void exchangeOpened(ExchangeEvent event) {
		logger.info("#### Market Opened ###");
		   DatagramSocket datagramSocket = null;

	        try {
	            datagramSocket = new DatagramSocket();
	            String localAddr = datagramSocket.getLocalAddress().getHostAddress();
	            int localPort = datagramSocket.getLocalPort();
	            InetAddress group = InetAddress.getByName(multicastAddress);
		        byte[] buf = new byte[256];
	            DatagramPacket packet = new DatagramPacket(buf, buf.length,
				                                          group, multicastPort);
	            System.out.println("Server ready...");

	            while (true) {
	                String priceChanged = String.format("PRICE_CHANGE:%S : %d", event.getTicker(), event.getPrice());

	                System.out.println("Sending: " + priceChanged + ", [" + localAddr + ":" + localPort + " -> " + multicastAddress + ":" + multicastPort +"]");
	                byte[] bytes = priceChanged.getBytes();
	                packet.setData(bytes);
	                packet.setLength(bytes.length);
	                datagramSocket.send(packet);
	                Thread.sleep(ONE_SECOND);
	            }
	        } catch (IOException ex) {
	            System.out.println("Server error: " + ex);
	        } catch (InterruptedException ex) {
	            System.out.println("Server error: " + ex);
	        } finally {
	            if (datagramSocket != null) {
	                datagramSocket.close();
	            }
	        }
	      
	}

	@Override
	public void exchangeClosed(ExchangeEvent event) {
		// TODO Auto-generated method stub
		
	}
/**
 * Processes price change events.
 * event - the event to be processed
 */
	@Override
	public void priceChanged(ExchangeEvent event) {
		if(logger.isInfoEnabled()){
			logger.info(String.format("Processing price change[%s:%d]",
					event.getTicker(), event.getPrice()));
		}
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
