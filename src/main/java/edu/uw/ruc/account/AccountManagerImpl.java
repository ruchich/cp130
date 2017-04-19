package edu.uw.ruc.account;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.dao.AccountDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Created by chq-ruchic on 4/17/2017.
 */


public final class AccountManagerImpl implements AccountManager {

    /**the account DAO to use*/
    private AccountDao dao;

    /** This class' logger.*/
    private  static final Logger log = LoggerFactory.getLogger(AccountManagerImpl.class);

    /** Character encoding to use to convert strings to?from byte array*/
    private static final String ENCODING = "ISO-8859-1";

    /** The hashing Algorithm*/

    private  static final String ALGORITHM = "SHA1";


    /** the factory to use creating the account*/
    AccountFactory accountFactory;


    /** @param dao the dao to use for persistence */
    public AccountManagerImpl(final AccountDao dao) {
        this.dao = dao;
        try (ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("Context.xml")) {
            accountFactory = appContext.getBean(AccountFactory.class);
        } catch (final BeansException ex) {
            log.error("Unable to create account manager.", ex);
        }
    }


    /**
     * used to persist an account
     * @param account
     * @throws AccountException if operation fails
     */
    public void persist(final Account account)
            throws AccountException{
        dao.setAccount(account);

    }

    /**
     * Look up an account based on user name
     * @param accountName
     * @return the account if located
     * @throws AccountException if operation failed
     */
    public Account getAccount(String accountName)
            throws AccountException{
        final Account account = dao.getAccount(accountName);
        if(account != null){
            account.registerAccountManager(this);
        }
        return account;
    }

    /**
     * Remove the account
     * @param accountName
     * @throws AccountException
     */
    public void deleteAccount(String accountName)
            throws AccountException{
        final Account account = dao.getAccount(accountName);
        if(account != null){
            dao.deleteAccount(accountName);
        }

    }

    /**
     * Creates an account
     * @param accountName
     * @param password
     * @param balance
     * @return
     * @throws AccountException
     */
    public Account createAccount(String accountName,
                                 String password,
                                 int balance)
            throws AccountException{
        if(dao.getAccount(accountName)== null){
            final byte[] passwordHash = hashPassword(password);
            final Account acct = accountFactory.newAccount(accountName,passwordHash,balance);
            acct.registerAccountManager(this);
            persist(acct);
            return acct;
        }else{
            throw new AccountException("Account name alrady in use.");
        }
    }

    /**
     * validates login
     * @param accountName
     * @param password
     * @return
     * @throws AccountException
     */
    public boolean validateLogin(String accountName,
                                 String password)
            throws AccountException{
        boolean valid = false;
        final Account account = getAccount(accountName);
        if(account!= null){
            final byte[] passwordHash = hashPassword(password);
            valid = MessageDigest.isEqual(account.getPasswordHash(), passwordHash);
        }
        return  valid;
    }


    private byte[] hashPassword(final String password)throws AccountException{
        try{
            final MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(password.getBytes(ENCODING));
            return md.digest();
        }catch (final NoSuchAlgorithmException e){
            throw new AccountException("Unable to find hash algorithm");
        }catch (final UnsupportedEncodingException e){
            throw new AccountException(String.format("Unable to find encoding standard"));
        }
    }
    public void close()
            throws AccountException{
        dao.close();
        dao = null;
    }

}