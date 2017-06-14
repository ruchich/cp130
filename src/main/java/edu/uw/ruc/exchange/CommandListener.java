package edu.uw.ruc.exchange;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.StockExchange;

public class CommandListener implements Runnable {

	/** This classes logger */
    private static final Logger logger =
                         LoggerFactory.getLogger(CommandListener.class);

    /** the command port*/
	private int commandPort;
	
	/** Flag indicating if thread is currently listening */
	
	private volatile boolean listening = true;
	
	/** The socket the thread listens to*/
	private ServerSocket servSock;
	
	/** The real exchange this adapter delegates to*/
	private StockExchange realExchange;
	
	/**Executor used to request the client requests*/
	
	private ExecutorService requestExecutor = Executors.newCachedThreadPool();

/** Constructor*/
	
	public CommandListener(final int commandPort, final StockExchange realExchange){
		this.commandPort = commandPort;
		this.realExchange = realExchange;
	}
	
	/** accepts connection and creates a Commandexecutor command*/
	
	public void run(){
		try{
			if(logger.isInfoEnabled()){
				logger.info(String.format("Server Ready, accepting connections on port: %s", commandPort));
			}
			
			servSock = new ServerSocket(commandPort);
			while(listening){
				Socket sock = null;
				try{
					sock = servSock.accept();
					if(logger.isInfoEnabled()){
						logger.info(String.format(" Accepted connection: %s:%d", sock.getLocalAddress(),sock.getLocalPort()));
					
				}
			}catch(final SocketException ex){
				if(servSock!= null && !servSock.isClosed()){
					logger.warn("Error accepting connection", ex);
				}
			}
				if ( sock==null){
					continue;
				}
				requestExecutor.execute(new CommandHandler(sock, realExchange));
		}
	}catch(final IOException ex){
		logger.info("Server Error: ", ex);
	}finally{
		terminate();
	}
}


/** Terminates this thread gracefully*/

public void terminate(){
	listening = false;
	try{
		if(servSock!= null && !servSock.isClosed()){
			logger.info(" Closing server socket");
			servSock.close();
		}
		servSock = null;
		if(!requestExecutor.isShutdown()){
			requestExecutor.shutdown();
			requestExecutor.awaitTermination(1L,  TimeUnit.SECONDS);
		}
	}catch ( final InterruptedException iex){
		logger.info("Interrupted awaiting executor termination.", iex);
	}catch (final IOException ex){
		logger.info("Error closing Listen Socket.", ex);
	}
}
}