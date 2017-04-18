package edu.uw.ruc;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;

/**
 * Created by chq-ruchic on 4/17/2017.
 */
public class AccountManagerImpl implements AccountManager {



    public void persist(Account account)
            throws AccountException{

    }
   public Account getAccount(String accountName)
            throws AccountException{
        Account account;

       return account;
    }
    public void deleteAccount(String accountName)
            throws AccountException{

    }
    public Account createAccount(String accountName,
                          String password,
                          int balance)
            throws AccountException{

    }
    public boolean validateLogin(String accountName,
                          String password)
            throws AccountException{

    }

    public void close()
            throws AccountException{

    }

}
