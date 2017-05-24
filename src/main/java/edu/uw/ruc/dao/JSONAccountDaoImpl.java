package edu.uw.ruc.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ruc.account.AccountImpl;
import edu.uw.ruc.account.AddressImpl;
import edu.uw.ruc.account.CreditCardImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.module.*;

public class JSONAccountDaoImpl implements AccountDao {

	/** This class' logger. */
	private static final Logger log = LoggerFactory
			.getLogger(FileAccountDaoImpl.class);

	/** name of the file holding the acct data */
	// private static final String ACCOUNT_FILENAME = "account.dat";

	/** The accounts directory */
	private final File accountsDir = new File("target", "accounts");

	/** account for this class */

	Account account;

	private ObjectMapper mapper;

	/**
	 * Default constructor
	 */

	public JSONAccountDaoImpl() {
		SimpleModule module = new SimpleModule();
		module.addAbstractTypeMapping(Account.class, AccountImpl.class);
		module.addAbstractTypeMapping(Address.class, AddressImpl.class);
		module.addAbstractTypeMapping(CreditCard.class, CreditCardImpl.class);
		mapper = new ObjectMapper();
		mapper.registerModule(module);
		if(!accountsDir.exists())
		{
			accountsDir.mkdirs();
		}
	}

	public Account getAccount(String accountName) {
		Account account = null;
		FileInputStream in = null;
		String accountFileName = accountName + ".json";
	//	final File accountDir = new File(accountsDir, accountName);

		  // if (accountDir.exists() && accountDir.isDirectory()) {
			try {
				File infile = new File(accountsDir, accountFileName);
				if(!infile.exists())
				{
					return null; 
				}
				//in = new FileInputStream(infile);

				account = mapper.readValue(infile, Account.class);

				//in.close();
			} catch (final IOException ex) {
				log.warn(
						String.format("Unable to access or read account data,"),
						ex);

			} 
				
				
		return account;
	}

	public void setAccount(Account account)
            throws AccountException {
       
        try {
        String accountFileName = account.getName() +".json";
       
             File outFile = new File(accountsDir, accountFileName);
            if(!accountsDir.exists()){
                final boolean success = accountsDir.mkdirs();
                if(!success){
                    throw new AccountException(String.format("Unable to create account directory, %s", accountsDir));
                }
            }
                if (outFile.exists()){
                	boolean deleted = outFile.delete();
                	if(!deleted){
                		log.warn(String.format("Unable to delete account file,%s, overwriting" + accountsDir.getAbsolutePath()));
                	}
                }
            
            mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, account);
           
            
} catch (final IOException ex){
    throw new AccountException("Unable to store account(s).", ex);
}
    }


	/**
	 * Remove the account
	 * 
	 * @param accountName
	 * @throws AccountException
	 */

	public void deleteAccount(String accountName) throws AccountException {
		String accountFileName = accountName +".json";
	       
        File outFile = new File(accountsDir, accountFileName);
        if(outFile.exists() && !outFile.delete()){
        	log.warn("File deletion failed," + outFile.getAbsolutePath());
        }

	}

	/**
	 * Removes all the accounts
	 * 
	 * @throws AccountException
	 */
	public void reset() throws AccountException {
		deleteFile(accountsDir);
	}

	/**
	 * utility method to delete afile or directory, directory contents and
	 * directory itself
	 * 
	 * @param file
	 *            the account file or directory to delete
	 */

	private void deleteFile(final File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				final File[] files = file.listFiles();

				for (File currFile : files) {
					deleteFile(currFile);
				}
			}
			if (!file.delete()) {
				log.warn(String.format("Filed deletion failed, %s", file));
			}
		}
	}

	public void close() throws AccountException {
		// no op;

	}

}
