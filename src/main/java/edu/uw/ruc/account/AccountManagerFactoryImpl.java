package edu.uw.ruc.account;

import edu.uw.ext.framework.account.*;
import edu.uw.ext.framework.dao.AccountDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by chq-ruchic on 4/17/2017.
 */
public class AccountManagerFactoryImpl implements AccountManagerFactory {


    /**
     * Default Constructor
     */
    public void AccountManagerFactoryImpl(){}

/**
 *Instantiates a new account manager instance.
 * @return  a newly instantiated account manager
 */
public AccountManager newAccountManager(final AccountDao dao){
  return new AccountManagerImpl(dao);
}


}
