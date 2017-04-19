package edu.uw.ruc.account;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing the interface AccountFactory
 */
public final class AccountFactoryImpl implements AccountFactory {
  /* Name of the account*/
   private String accountName;

    /* the hashed password of the account*/
   private byte[] hashedPassword;

    /* Initial Balance of the account*/
   private int initialBalance;

    /** This class' logger. */
    static final Logger log = LoggerFactory.getLogger(AccountFactoryImpl.class);
 /**
 * No argument constructor
 */
    public AccountFactoryImpl(){}
/**
 * Instantiates a new account instance.
 *@param accountName - the account name
 *@param hashedPassword - the password hash
 *@param initialBalance - the balance
 *@return the newly instantiated account, or null if unable to instantiate the account
 */
public Account newAccount(String accountName,
                          byte[] hashedPassword,
                          int initialBalance){

Account account = null;
    try {
        account = new AccountImpl(accountName,hashedPassword, initialBalance );
                if(log.isInfoEnabled()){
                    log.info(String.format("Created account: '%s', balanced = %d", accountName, initialBalance));
                }
    } catch (final AccountException e) {
        final String  msg = String.format("Account  creation failed for, account '%s' and balance %d", accountName, initialBalance);
        log.warn(msg, e);
    }

    return account;
}
}