package edu.uw.ruc.exchange;

import java.io.IOException;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.exchange.SimpleExchange;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.Order;

public class ExchangeNetworkProxy implements StockExchange{
	
	/** This classes logger */
    private static final Logger logger =
                         LoggerFactory.getLogger(ExchangeNetworkProxy.class);

    /** The event listener list for the exchange */
    private EventListenerList listenerList = new EventListenerList();
    
    /** The current open/closed state of the exchange */
    private boolean openState;
    
    private String mIpAddress; 
    private int mPort;

    
    public ExchangeNetworkProxy( String ipAddress, int port ) { mIpAddress = ipAddress; mPort = port; }


    
    
    /**
     * The current state of the exchange.
     *
     * @return true if the exchange is open
     */
	@Override
	public boolean isOpen() {
		
		return openState;
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
	public int executeTrade(Order order) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
     * Initialize the exchange by reading the exchange file, fires the exchange
     * open event then instructs the adjuster to begin adjusting prices.
     */
    public synchronized void open() {
        openState = true;

        priceAdjuster.startAdjusting();

        ExchangeEvent evnt = ExchangeEvent.newOpenedEvent(this);
        fireExchangeEvent(evnt);
    }

    /**
     * Fires the exchange closing event, instructs the adjuster to stop
     * adjusting prices and then fires the exchange closed event.
     */
    public synchronized void close() {
        priceAdjuster.stopAdjusting();

        openState = false;
        ExchangeEvent evnt = ExchangeEvent.newClosedEvent(this);
        fireExchangeEvent(evnt);
    }

   

    /**
     * Adds a exchange listener.
     *
     * @param l the listener to add
     */
    public synchronized void addExchangeListener(final ExchangeListener l) {
        listenerList.add(ExchangeListener.class, l);
    }

    /**
     * Removes a exchange listener.
     *
     * @param l the listener to remove
     */
    public synchronized void removeExchangeListener(final ExchangeListener l) {
        listenerList.remove(ExchangeListener.class, l);
    }

    /**
     * Fires a exchange event.
     *
     * @param evnt the event to be fired
     */
    private void fireExchangeEvent(final ExchangeEvent evnt) {
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
