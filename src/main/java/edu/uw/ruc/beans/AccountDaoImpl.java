package edu.uw.ruc.beans;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.dao.AccountDao;

/**
 * Created by chq-ruchic on 4/17/2017.
 */
public class AccountDaoImpl implements AccountDao {


    /**
     * Default constructor
     */
    public AccountDaoImpl(){}

   public Account getAccount(String accountName){

   }

    public void setAccount(Account account)
            throws AccountException{

    }

  public  void deleteAccount(String accountName)
            throws AccountException{

  }
    public void reset()
            throws AccountException{

    }

   public void close()
            throws AccountException{

    }
}
