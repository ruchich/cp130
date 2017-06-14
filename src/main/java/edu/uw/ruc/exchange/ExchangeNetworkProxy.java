package edu.uw.ruc.exchange;


import static edu.uw.ruc.exchange.ProtocolConstants.GET_STATE_CMD;
import static edu.uw.ruc.exchange.ProtocolConstants.OPEN_STATE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

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
    
    

    /** the command Ip address*/
	private String mCmdIpAddress;

	/** the command port*/
	private int mCmdPort;
	
	/** The event processor, propagates theevents to registered listners*/
	private NetEventProcessor eventProcessor;

    
    public ExchangeNetworkProxy( String eventIpAddress, int eventPort, String cmdIpAddress, int cmdPort)  {
    	
    	mCmdIpAddress = cmdIpAddress;
    	mCmdPort = cmdPort;
    	eventProcessor = new NetEventProcessor(eventIpAddress, eventPort);
    	Executors.newSingleThreadExecutor().execute(eventProcessor);
    	}


    
    
    /**
     * The current state of the exchange.
     *
     * @return true if the exchange is open
     */
	@Override
	public boolean isOpen() {
		final String response = sendTcpCmd(GET_STATE_CMD);
		
		//parse response
		final boolean state = OPEN_STATE.equals(response);
		return state;
	}

	@Override
	public String[] getTickers() {
		final String response = sendTcpCmd(GET_TICKERS_CMD);
		
		//parse response
		final String[] tickers = response.split(ELEMENT_DELIMITER);
		return tickers;
	}

	@Override
	public StockQuote getQuote(String ticker) {
		final String cmd = String.join(ELEMENT_DELIMITER, GET_QUOTE_CMD,tickers);
		final String response = sendTcpCmd(cmd);
		int price = INVALID_STOCK;
		try{
			price = Integer.parseInt(response);
		
	}catch (final NumberFormatException ex){
		logger.warn(String.format("String to int conversion failed: '%s'", response), ex);
	}

	StockQuote quote = null;
	
	if(price>=0){
		quote = new StockQuote(ticker, price);
	}
	return quote;
	}

	@Override
	public int executeTrade(Order order) {
		final String orderType = (order.isBuyOrder())? BUY_ORDER: SELL_ORDER;
		final String cmd = String.join(ELEMENT_DELIMITER,EXECUTE_TRADE_CMD,orderType,
										order.getAccountId(),order.getStockTicker(),
										Integer.toString(order.getNumberOfShares());
		final String response = sendTcpCmd(cmd);
		int executionPrice = 0;
		try{
			executionPrice = Integer.parseInt(response);
					
		}catch(final NumberFormatException ex){
		logger.warn(String.format("String to int conversion failed: '%s'", response), ex);
	}
		return executionPrice;
	}
	
	/**
     * Initialize the exchange by reading the exchange file, fires the exchange
     * open event then instructs the adjuster to begin adjusting prices.
     */
    public synchronized void open() {
        openState = true;

       
    
       // ExchangeEvent evnt = ExchangeEvent.newOpenedEvent(this);
       // fireExchangeEvent(evnt);
    }

    /**
     * Fires the exchange closing event, instructs the adjuster to stop
     * adjusting prices and then fires the exchange closed event.
     */
    public synchronized void close() {
        

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
    
    /**sends tcp command*/
    private String sendTcpCmd(final String cmd){
    	
    	PrintWriter prntWrtr = null;
    	BufferedReader br = null;
    	String response = "";
    	
    	try{ Socket sock = new Socket(mCmdIpAddress, mCmdPort);
    	if(logger.isInfoEnabled()){
			logger.info(String.format("Connected to server: %s:%d", sock.getLocalAddress(), sock.getLocalPort()));
		}
		final InputStream inStrm = sock.getInputStream();
		final Reader rdr = new InputStreamReader(inStrm, ENCODING);
		br = new BufferedReader(rdr);
		
		
		final OutputStream outStrm = sock.getOutputStream();
		final Writer wrtr = new OutputStreamWriter(outStrm, ENCODING);
    	 prntWrtr = new PrintWriter (wrtr, true);
    	 prntWrtr.println(cmd);
    	 response = br.readLine();
    	}catch(final IOException ex){
    		logger.warn("Error sending commad to exchange", ex);
    	}
    	return response;
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
