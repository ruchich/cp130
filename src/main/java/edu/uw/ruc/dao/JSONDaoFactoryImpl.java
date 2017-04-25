package edu.uw.ruc.dao;



import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

/**
 * Created by chq-ruchic on 4/17/2017.
 */
public final class JSONDaoFactoryImpl implements DaoFactory {
    /**
     * Instantiates an instance of JSONAccountDaoImpl
     * @return a new an instance of JSONAccountDaoImpl
     * @throws DaoFactoryException
     */
   public AccountDao getAccountDao() throws DaoFactoryException {
       return new JSONAccountDaoImpl();
   }
}
