package edu.uw.ruc.beans;

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
  //  this.accountName = accountName;
  //  this.hashedPassword = hashedPassword;
   // this.initialBalance = initialBalance;
Account account = new AccountImpl();
    try {
        account.setName(accountName);
    } catch (AccountException e) {
        e.printStackTrace();
    }
    account.setPasswordHash(hashedPassword);
    account.setBalance(initialBalance);
    return account;
}
}