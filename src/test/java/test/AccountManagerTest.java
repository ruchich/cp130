package test;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.AccountManagerFactory;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ruc.account.*;
import edu.uw.ruc.dao.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class AccountManagerTest {
	private static Logger logger = LoggerFactory.getLogger(AccountManagerTest.class.getName());
	private static final String FRED_ACCOUNT_NAME = "fflintstone";
	private static final String BAD_ACCOUNT_NAME = "f_flintstone";
	private static final String FRED_GOOD_PASSWORD = "password2";
	private static final String FRED_BAD_PASSWORD = "password";
	private static final String WILMA_ACCOUNT_NAME = "wflintstone";
	private static final String WILMA_GOOD_PASSWORD = "pebbles";
	private static final String BETTY_ACCOUNT_NAME = "b_rubble";
	private static final int ONE_THOUSAND_DOLLARS_IN_CENTS = 100000;
	private static final int FIVE_THOUSAND_DOLLARS_IN_CENTS = 500000;
	private static final int TEST_EXECUTION_PRICE = 10000;
	private AccountManager accountManager;
	private AccountManagerFactory accountManagerFactory;
	private AccountDao dao;
	private ClassPathXmlApplicationContext appContext;

	public AccountManagerTest() {
	}

	@Before
	public void setUp() throws Exception {
		this.appContext = new ClassPathXmlApplicationContext("context.xml");
		this.accountManagerFactory = (AccountManagerFactory)this.appContext.getBean("AccountManagerFactory", AccountManagerFactoryImpl.class);
		this.setUpAccountManager();
		this.dao.reset();
		this.accountManager.createAccount("fflintstone", "password2", 100000);
		this.accountManager.createAccount("wflintstone", "pebbles", 500000);
	}

	@After
	public void tearDown() {
		if(this.appContext != null) {
			this.appContext.close();
		}

	}

	private void setUpAccountManager() throws Exception {
		DaoFactory fact = (DaoFactory)this.appContext.getBean("DaoFactory", FileDaoFactoryImpl.class);
		this.dao = fact.getAccountDao();
		this.accountManager = this.accountManagerFactory.newAccountManager(this.dao);
	}

	@Test
	public void testPersist() throws Exception {
		Account account = this.accountManager.getAccount("fflintstone");
		account.setBalance(500000);
		this.accountManager.persist(account);
		this.accountManager.close();
		this.setUpAccountManager();
		account = this.accountManager.getAccount("fflintstone");
		Assert.assertEquals(500000L, (long)account.getBalance());
	}

	@Test
	public void testValidate() throws Exception {
		Assert.assertTrue(this.accountManager.validateLogin("fflintstone", "password2"));
		Assert.assertTrue(!this.accountManager.validateLogin("fflintstone", "password"));
		Assert.assertTrue(!this.accountManager.validateLogin("f_flintstone", "password2"));
		Assert.assertTrue(this.accountManager.validateLogin("wflintstone", "pebbles"));
	}

	@Test
	public void testGetAccount() throws Exception {
		Account account = this.accountManager.getAccount("fflintstone");
		Assert.assertEquals("fflintstone", account.getName());
		Assert.assertEquals(100000L, (long)account.getBalance());
	}

	@Test
	public void testMultipleGetAccount() throws Exception {
		this.accountManager.createAccount("b_rubble", "pebbles", 100000);
		Account fredAccount = this.accountManager.getAccount("fflintstone");
		Assert.assertEquals("fflintstone", fredAccount.getName());
		Assert.assertEquals(100000L, (long)fredAccount.getBalance());
		Account barneyAccount = this.accountManager.getAccount("b_rubble");
		Assert.assertEquals("b_rubble", barneyAccount.getName());
		Assert.assertEquals(100000L, (long)barneyAccount.getBalance());
	}

	@Test
	public void testReloadFile() throws Exception {
		this.setUpAccountManager();
		Account account = this.accountManager.getAccount("fflintstone");
		Assert.assertEquals("fflintstone", account.getName());
		Assert.assertEquals(100000L, (long)account.getBalance());
	}

	@Test
	public void testDeleteAccount() throws Exception {
		this.accountManager.deleteAccount("fflintstone");
		Account account = this.accountManager.getAccount("fflintstone");
		Assert.assertEquals((Object)null, account);
	}

	@Test
	public void testCreateDuplicateUsername() throws Exception {
		try {
			this.accountManager.createAccount("fflintstone", "password2", 100000);
			Assert.fail("Should have thrown AccountException");
		} catch (AccountException var2) {
			logger.info("testCreateDuplicateUsername threw exception as expected");
		}

	}

	@Test
	public void testClose() throws Exception {
		this.accountManager.close();
	}

	@Test
	public void testRegistration() throws Exception {
		Account account = this.accountManager.getAccount("fflintstone");
		int balance = account.getBalance();
		MarketSellOrder order = new MarketSellOrder(account.getName(), 1, "BA");

		try {
			account.reflectOrder(order, 10000);
			Assert.assertEquals((long)(balance + 10000), (long)account.getBalance());
		} catch (NullPointerException var5) {
			logger.info("Account.reflectOrder() not yet implemented");
		}

	}
}
