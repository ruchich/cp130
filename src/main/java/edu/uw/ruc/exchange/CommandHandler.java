package edu.uw.ruc.exchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ext.framework.order.Order;

public class CommandHandler implements Runnable {

	
	/** This classes logger */
    private static final Logger logger =
                         LoggerFactory.getLogger(CommandListener.class);

    /** The real exchange this adapter delegates to*/
	private StockExchange realExchange;
	
	/** The socket the command was received on*/
	private Socket socket;
	
	
	/**Constructor*/
	
	public CommandHandler(final Socket sock, final StockExchange realExchange){
		this.realExchange = realExchange;
		this.socket = sock;
	
	}
	
	
	/**process the command*/
	
	public void run(){
		
		try{
			final InputStream inStrm = socket.getInputStream();
			final Reader rdr = new InputStreamReader(inStrm, ENCODING);
			final BufferedReader br = new BufferedReader(rdr);
			
			
			final OutputStream outStrm = socket.getOutputStream();
			final Writer wrtr = new OutputStreamWriter(outStrm, ENCODING);
			final PrintWriter prntWrtr = new PrintWriter (wrtr,true);
			 String msg = br.readLine();
			 if(null == msg){
				 msg = "";
			 }
			 if(logger.isInfoEnabled()){
				 logger.info(String.format("Received command message: '%s'", msg));
			 }
			 final String []elements = msg.split(ELEMENT_DELIMITER);
			 final String cmd = elements[CMD_ELEMENT];
			 
			 //Dispatch command
			 
			 if(logger.isInfoEnabled()){
				 logger.info(String.format(" Processing command: '%s'", cmd));
		}
			 
			 switch(cmd){
			 case GET_STATE_CMD:
				 doGetState(prntWrtr);
				 break;
				 
			 case GET_TICKERS_CMD:
				 doGetTickers(prntWrtr);
				 break;	 
				 
				 
			 case GET_QUOTE_CMD:
				 doGetQuote(elements, prntWrtr);
				 break;	 
				 
			 case EXECUTE_TRADE_CMD:
				 doExecuteTrade(elements, prntWrtr);
				 break;	 
				 
				 default:
					 logger.error(String.format("Unrecognizable command: %s", cmd));
					 break;
			 }
	}catch (final IOException ex){
		logger.error("Error sending response", ex);
	}finally{
		try{
			if(socket!=null){
				socket.close();
				
			}
		}catch(final IOException ioex){
			logger.info("Error closing socket", ioex);
		}
	}
}
	
	/**Process the GET_STATE_CMD*/
	private void doGetState(final PrintWriter prntWrtr){
		final String response = realExchange.isOpen()? OPEN_STATE:CLOSED_STATE;
		prntWrtr.println(response);
	}
	
	
	/** process the GET_TICKERS_CMD*/
	
	private void doGetTickers(final PrintWriter prntWrtr){
		final String []tickers = realExchange.getTickers();
		final String tickersStr = String.join(ELEMENT_DELIMITER, tickers);
		prntWrtr.println(tickersStr);
	}
		
		/** process the GET_QUOTE_CMD*/
	private void doGetQuote(final String[] elements, final PrintWriter prntWrtr){
		final String ticker= elements[QUOTE_CMD_TICKER_ELEMENT];
		final StockQuote quote = realExchange.getQuote(ticker);
		int price = (quote ==null)? INVALID_STOCK:quote.getPrice();
		prntWrtr.println(price);
	}
	
	/** process the EXECUTE_TRADE_CMD*/
	
	private void doExecuteTrade(final String [] elements, final PrintWriter prntWrtr){
		
		if(realExchange.isOpen()){
			final String orderType = elements[EXECUTE_TRADE_CMD_TYPE_ELEMENT];
			final String acctId = elements [EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT];
			String ticker = elements [EXECUTE_TRADE_CMD_TICKER_ELEMENT];
			final String shares = elements [EXECUTE_TRADE_CMD_SHARES_ELEMENT];
			int qty =-1;
			try{
				qty = Integer.parseInt(shares);
			}catch (final NumberFormatException ex){
				logger.warn(String.format("String to int conversion failed: '%s'", shares));
			}
			Order order;
			
			if(BUY_ORDER.equals(orderType)){
				order =new MarketBuyOrder(acctId,qty, ticker);
			}else{
				order = new MarketSellOrder(acctId,qty, ticker);
			}
			int price = realExchange.executeTrade(order);
			prntWrtr.println(price);
			}else{
				prntWrtr.println(0);
			}
		}
	}
		
		

