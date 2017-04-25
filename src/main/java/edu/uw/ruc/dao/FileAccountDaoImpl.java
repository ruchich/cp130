package edu.uw.ruc.dao;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;


import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ruc.account.AccountImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chq-ruchic on 4/17/2017.
 */
public final class FileAccountDaoImpl implements AccountDao {

    /** This class' logger.*/
    private  static final Logger log = LoggerFactory.getLogger(FileAccountDaoImpl.class);

    /** name of the file holding the acct data*/
    private  static final String ACCOUNT_FILENAME = "account.dat";

    /** name of the file holding the address data*/
    private  static final String ADDRESS_FILENAME = "address.properties";

    /** name of the file holding the credit card data*/
    private  static final String CREDITCARD_FILENAME = "creditcard.text";


    /**The accounts directory*/
    private final File accountsDir = new File("target", "accounts");

    /**account for this class*/

    Account account;

    /**
     * Default constructor
     */
    public FileAccountDaoImpl(){}

   // public FileAccountDaoImpl(Account account){ this.account = account;}

    public Account getAccount(String accountName) {
        Account account = null;
        FileInputStream in = null;
        final File accountDir = new File(accountsDir, accountName);

        if (accountDir.exists() && accountDir.isDirectory()) {
            try {
                File infile = new File(accountDir, ACCOUNT_FILENAME);
                in = new FileInputStream(infile);
                account = AccountSer.read(in);
                in.close();

                infile = new File(accountDir, ADDRESS_FILENAME);
                if (infile.exists()) {
                    in = new FileInputStream(infile);
                    final Address address = AddressSer.read(in);
                    in.close();
                    account.setAddress(address);

                }

                infile = new File(accountDir, CREDITCARD_FILENAME);
                if (infile.exists()) {
                    in = new FileInputStream(infile);
                    final CreditCard creditCard = CreditCardSer.read(in);
                    in.close();
                    account.setCreditCard(creditCard);
                }
            } catch (final IOException ex) {
                log.warn(String.format("Unable to access or read account data,"), ex);
            } catch (final AccountException ex) {
                log.warn(String.format("Unable to process account file for account"), ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.warn(" Attempt to close stream failed.", e);
                    }
                }
            }

        }
        return account;
    }

    public void setAccount(Account account)
            throws AccountException {
        FileOutputStream out = null;
        try {
            final File accountDir = new File(accountsDir, account.getName());

            final Address address = account.getAddress();
            final CreditCard card = account.getCreditCard();
            deleteFile(accountDir);
            if(!accountDir.exists()){
                final boolean success = accountDir.mkdir();
                if(!success){
                    throw new AccountException(String.format("Unable to create account directory, %s", accountDir));
                }
            }

            File outFile = new File(accountDir, ACCOUNT_FILENAME);
            out = new FileOutputStream(outFile);
            AccountSer.write(out,account);
            out.close();

            if(address!=null){
                outFile = new File(accountDir, ADDRESS_FILENAME);
                out = new FileOutputStream(outFile);
                AddressSer.write(out, address);
                out.close();
            }
            if(card!=null){
                outFile = new File(accountDir,CREDITCARD_FILENAME);
                out = new FileOutputStream(outFile);
                CreditCardSer.write(out, card);
                out.close();
            }
        } catch (final IOException ex){
            throw new AccountException("Unable to store account(s).", ex);
        }finally {
            if(out !=null){
                try{
                    out.close();
                } catch (IOException e){
                    log.warn("Attempt to close the streams failed", e);
                }
            }
        }
    }

    /**
     * Remove the account
     * @param accountName
     * @throws AccountException
     */

    public  void deleteAccount(String accountName)
            throws AccountException{
        deleteFile(new File(accountsDir,accountName));

    }

    /**
     * Removes all  the accounts
     * @throws AccountException
     */
    public void reset()
            throws AccountException{
        deleteFile(accountsDir);
    }

    /**
     * utility method to delete afile or directory, directory contents and directory itself
     * @param file the account file or directory to delete
     */

    private void deleteFile(final File file){
        if(file.exists()){
            if(file.isDirectory()){
                final File [] files = file.listFiles();

                for(File currFile : files){
                    deleteFile(currFile);
                }
            }
            if (!file.delete()) {
                log.warn(String.format("Filed deletion failed, %s", file));
            }
        }
    }
    public void close()
            throws AccountException{
        //np

    }


}
