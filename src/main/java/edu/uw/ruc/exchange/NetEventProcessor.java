package edu.uw.ruc.exchange;

import static edu.uw.ruc.exchange.ProtocolConstants.CLOSED_EVNT;
import static edu.uw.ruc.exchange.ProtocolConstants.OPEN_EVNT;
import static edu.uw.ruc.exchange.ProtocolConstants.PRICE_CHANGE_EVNT;
import static edu.uw.ruc.exchange.ProtocolConstants.PRICE_CHANGE_EVNT_TICKER_ELEMENT;
import static edu.uw.ruc.exchange.ProtocolConstants.PRICE_CHANGE_EVNT_PRICE_ELEMENT;
import static edu.uw.ruc.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.ruc.exchange.ProtocolConstants.EVENT_ELEMENT;
import static edu.uw.ruc.exchange.ProtocolConstants.ENCODING;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;

public class NetEventProcessor implements Runnable {

	/** Buffer Size */
	private static final int BUFFER_SIZE = 1024;

	/** This class logger */
	private static final Logger logger = LoggerFactory
			.getLogger(NetEventProcessor.class);

	/** the event muticast adddress */
	private String eventIpAddress;

	/** the event muticast port */
	private int eventPort;

	/** the event listener list */
	private EventListenerList listenerList = new EventListenerList();

	/** constructor */

	NetEventProcessor(String eventIpAddress, int eventPort) {
		this.eventIpAddress = eventIpAddress;
		this.eventPort = eventPort;
	}

	/** continously accepsta and processes marketand price chnage event */

	public void run(){
		 try( MulticastSocket eventSocket = new  MulticastSocket(eventPort )){
			 final InetAddress eventGroup  = InetAddress.getByName(eventIpAddress);
			 eventSocket.joinGroup(eventGroup);
			 if(logger.isInfoEnabled()){
				 logger.info("Receivieng events from:" + eventIpAddress + ":" + eventPort );
			 }
			 
			 final byte[]buf = new byte[BUFFER_SIZE];
			 DatagramPacket packet = new DatagramPacket(buf, buf.length);
                     while(true){
                    	 eventSocket.receive(packet);
                    	 final String msg = new String(packet.getData(), packet.getOffset(), packet.getLength(), ENCODING);
                    	 final String [] elements = msg.split(ELEMENT_DELIMITER);
                    	 final String eventType = elements[EVENT_ELEMENT];
                    	 switch(eventType){
                    	 case OPEN_EVNT:
                    		 fireListeners(ExchangeEvent.newOpenedEvent(this));
                    		 break;
		
                    	 case CLOSED_EVNT:
                    		 fireListeners(ExchangeEvent.newClosedEvent(this));
                    		 break;
		
                    		 case PRICE_CHANGE_EVNT:
		 final String ticker = elements[PRICE_CHANGE_EVNT_TICKER_ELEMENT];
		 final String priceStr = elements[PRICE_CHANGE_EVNT_PRICE_ELEMENT];
		 int price = -1;
		 try{
			 price = Integer.parseInt(priceStr);
		 }catch(final NumberFormatException ex){
			 logger.warn(String.format("String to int conversion failed", ex));
		 }
		fireListeners(ExchangeEvent.newPriceChangedEvent(this,  ticker, price));
		break;
		
		default:
			break;
	}
}
		 }catch(final IOException ex){
			 logger.warn("Server Error:", ex);
		 }
		 logger.warn("Done Processing events");
	 }

	/**
	 * Adds a exchange listener.
	 *
	 * @param l
	 *            the listener to add
	 */
	public synchronized void addExchangeListener(final ExchangeListener l) {
		listenerList.add(ExchangeListener.class, l);
	}

	/**
	 * Removes a exchange listener.
	 *
	 * @param l
	 *            the listener to remove
	 */
	public synchronized void removeExchangeListener(final ExchangeListener l) {
		listenerList.remove(ExchangeListener.class, l);
	}

	/** fires an exchange event */

	private void fireListeners(final ExchangeEvent evnt) {
		ExchangeListener[] listeners;
		listeners = listenerList.getListeners(ExchangeListener.class);

		for (ExchangeListener listener : listeners) {
			switch (evnt.getEventType()) {
			case OPENED:
				listener.exchangeOpened(evnt);
				break;

			case CLOSED:
				listener.exchangeClosed(evnt);
				break;

			case PRICE_CHANGED:
				listener.priceChanged(evnt);
				break;

			default:
				logger.warn("Attempted to fire an unknown exchange event: "
						+ evnt.getEventType());
				break;
			}
		}
	}

}
