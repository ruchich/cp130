package test;

import edu.uw.ext.framework.account.Account;
		import edu.uw.ext.framework.account.AccountFactory;
		import edu.uw.ext.framework.account.Address;
		import edu.uw.ext.framework.account.CreditCard;
		import edu.uw.ext.framework.dao.AccountDao;
		import edu.uw.ext.framework.dao.DaoFactory;
		import edu.uw.ruc.account.*;
		import edu.uw.ruc.dao.*;
		import java.util.Arrays;
		import org.junit.After;
		import org.junit.Assert;
		import org.junit.Before;
		import org.junit.Test;
		import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class DaoTest {
	private static final String BEDROCK_CITY = "Bedrock";
	private static final String ARIZONA_STATE_CODE = "AZ";
	private static final String BEDROCK_ZIP = "86046";
	private static final String FLINTSTONE_PHONE_NUMBER = "(123) 567-8900";
	private static final String FLINTSTONE_STREET_ADDRESS = "101 Stoney Lane";
	private static final String FRED_ACCOUNT_NAME = "fflintstone";
	private static final byte[] FRED_ACCOUNT_PASSWD = new byte[]{112, 97, 115, 115, 119, 111, 114, 100};
	private static final int FRED_ACCOUNT_INIT_BALANCE = 100000;
	private static final String FRED_ACCOUNT_FULL_NAME = "Fred Flintstone";
	private static final String FRED_EMAIL_ADDRESS = "fred@slate-rock.com";
	private static final String FRED_CREDIT_CARD_ISSUER = "Marble Stone Bank";
	private static final String FRED_CREDIT_CARD_HOLDER = "Fredrick Flintstone";
	private static final String FRED_CREDIT_CARD_NUM = "1234-5678-9012-3456";
	private static final String FRED_CREDIT_CARD_EXPIRES = "03/05";
	private static final String WILMA_ACCOUNT_NAME = "wflintstone";
	private static final byte[] WILMA_ACCOUNT_PASSWD = new byte[]{112, 97, 115, 115, 119, 111, 114, 100, 50};
	private static final int WILMA_ACCOUNT_INIT_BALANCE = 500000;
	private static final String WILMA_ACCOUNT_FULL_NAME = "Wilma Flintstone";
	private static final String WILMA_EMAIL_ADDRESS = "wilma@yabadabado.com";
	private static final String WILMA_CREDIT_CARD_ISSUER = "Granite Bank";
	private static final String WILMA_CREDIT_CARD_HOLDER = "W Flintstone";
	private static final String WILMA_CREDIT_CARD_NUM = "5678-9012-3456-1234";
	private static final String WILMA_CREDIT_CARD_EXPIRES = "02/06";
	private static final String BARNEY_ACCOUNT_NAME = "b_rubble";
	private static final byte[] BARNEY_ACCOUNT_PASSWD = new byte[]{112, 97, 115, 115, 119, 111, 114, 100, 51};
	private static final int BARNEY_ACCOUNT_INIT_BALANCE = 200000;
	private static final String BARNEY_ACCOUNT_FULL_NAME = "Barney Rubble";
	private static final String BARNEY_PHONE_NUMBER = "(987) 654-3210";
	private static final String BARNEY_EMAIL_ADDRESS = "barney@slate-rock.com";
	private static final String BARNEY_STREET_ADDRESS = "103 Stoney Lane";
	private static final String BARNEY_CREDIT_CARD_ISSUER = "First Rock Bank";
	private static final String BARNEY_CREDIT_CARD_HOLDER = "Barney Rubble";
	private static final String BARNEY_CREDIT_CARD_NUM = "7890-1234-5678-9000";
	private static final String BARNEY_CREDIT_CARD_EXPIRES = "04/06";
	private static final String MR_SLATE_ACCOUNT_NAME = "mr_slate";
	private static final byte[] MR_SLATE_ACCOUNT_PASSWD = new byte[]{13, 10, 9, 44, 32, 61, 58, 34, 46};
	private static final int MR_SLATE_ACCOUNT_INIT_BALANCE = 500000;
	private static final String MR_SLATE_ACCOUNT_FULL_NAME = "Mr. Slate";
	private static final String MR_SLATE_PHONE_NUMBER = "(210) 987-6543";
	private static final String MR_SLATE_EMAIL_ADDRESS = "boss@slate-rock.com";
	private Account fredAcct;
	private Account wilmaAcct;
	private Account barneyAcct;
	private Account mrSlateAcct;
	private AccountFactory accountFactory;
	private DaoFactory daoFactory;
	private ClassPathXmlApplicationContext appContext;
	private AccountDao dao;

	public DaoTest() {
	}

	@Before
	public void setUp() throws Exception {
		this.appContext = new ClassPathXmlApplicationContext("context.xml");
		this.accountFactory = (AccountFactory)this.appContext.getBean("AccountFactory", AccountFactoryImpl.class);
		this.daoFactory = (DaoFactory)this.appContext.getBean("DaoFactory", DaoFactory.class);
		this.accountFactory = (AccountFactory)this.appContext.getBean("AccountFactory", AccountFactoryImpl.class);
		if(this.accountFactory == null) {
			throw new Exception("Unable to create account factory!");
		} else {
			this.fredAcct = this.accountFactory.newAccount("fflintstone", FRED_ACCOUNT_PASSWD, 100000);
			if(this.fredAcct == null) {
				throw new Exception("Factory unable to create account!");
			} else {
				this.fredAcct.setFullName("Fred Flintstone");
				this.fredAcct.setPhone("(123) 567-8900");
				this.fredAcct.setEmail("fred@slate-rock.com");
				Address addr = (Address)this.appContext.getBean("Address", AddressImpl.class);
				addr.setStreetAddress("101 Stoney Lane");
				addr.setCity("Bedrock");
				addr.setState("AZ");
				addr.setZipCode("86046");
				this.fredAcct.setAddress(addr);
				CreditCard card = (CreditCard)this.appContext.getBean("CreditCard", CreditCardImpl.class);
				card.setIssuer("Marble Stone Bank");
				card.setHolder("Fredrick Flintstone");
				card.setAccountNumber("1234-5678-9012-3456");
				card.setExpirationDate("03/05");
				this.fredAcct.setCreditCard(card);
				this.wilmaAcct = this.accountFactory.newAccount("wflintstone", WILMA_ACCOUNT_PASSWD, 500000);
				this.wilmaAcct.setFullName("Wilma Flintstone");
				this.wilmaAcct.setPhone("(123) 567-8900");
				this.wilmaAcct.setEmail("wilma@yabadabado.com");
				addr = (Address)this.appContext.getBean("Address", AddressImpl.class);
				addr.setStreetAddress("101 Stoney Lane");
				addr.setCity("Bedrock");
				addr.setState("AZ");
				addr.setZipCode("86046");
				this.wilmaAcct.setAddress(addr);
				card = (CreditCard)this.appContext.getBean("CreditCard", CreditCardImpl.class);
				card.setIssuer("Granite Bank");
				card.setHolder("W Flintstone");
				card.setAccountNumber("5678-9012-3456-1234");
				card.setExpirationDate("02/06");
				this.wilmaAcct.setCreditCard(card);
				this.barneyAcct = this.accountFactory.newAccount("b_rubble", BARNEY_ACCOUNT_PASSWD, 200000);
				this.barneyAcct.setFullName("Barney Rubble");
				this.barneyAcct.setPhone("(987) 654-3210");
				this.barneyAcct.setEmail("barney@slate-rock.com");
				addr = (Address)this.appContext.getBean("Address", AddressImpl.class);
				addr.setStreetAddress("103 Stoney Lane");
				addr.setCity("Bedrock");
				addr.setState("AZ");
				addr.setZipCode("86046");
				this.barneyAcct.setAddress(addr);
				card = (CreditCard)this.appContext.getBean("CreditCard", CreditCardImpl.class);
				card.setIssuer("First Rock Bank");
				card.setHolder("Barney Rubble");
				card.setAccountNumber("7890-1234-5678-9000");
				card.setExpirationDate("04/06");
				this.barneyAcct.setCreditCard(card);
				this.mrSlateAcct = this.accountFactory.newAccount("mr_slate", MR_SLATE_ACCOUNT_PASSWD, 500000);
				this.mrSlateAcct.setFullName("Mr. Slate");
				this.mrSlateAcct.setPhone("(210) 987-6543");
				this.mrSlateAcct.setEmail("boss@slate-rock.com");
				this.dao = this.daoFactory.getAccountDao();
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		if(this.appContext != null) {
			this.appContext.close();
		}

		if(this.dao != null) {
			this.dao.close();
		}

	}

	private void compareAccounts(Account expected, Account actual) {
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertTrue(Arrays.equals(expected.getPasswordHash(), actual.getPasswordHash()));
		Assert.assertEquals((long)expected.getBalance(), (long)actual.getBalance());
		Assert.assertEquals(expected.getFullName(), actual.getFullName());
		Assert.assertEquals(expected.getPhone(), actual.getPhone());
		Assert.assertEquals(expected.getEmail(), actual.getEmail());
		Address expectedAddr = expected.getAddress();
		Address actualAddr = actual.getAddress();
		if(expectedAddr == null) {
			Assert.assertNull("Expected the Address to be null", actualAddr);
		} else {
			Assert.assertEquals(expectedAddr.getStreetAddress(), actualAddr.getStreetAddress());
			Assert.assertEquals(expectedAddr.getCity(), actualAddr.getCity());
			Assert.assertEquals(expectedAddr.getState(), actualAddr.getState());
			Assert.assertEquals(expectedAddr.getZipCode(), actualAddr.getZipCode());
		}

		CreditCard expectedCc = expected.getCreditCard();
		CreditCard actualCc = actual.getCreditCard();
		if(expectedCc == null) {
			Assert.assertNull("Expected the CreditCard to be null", actualCc);
		} else {
			Assert.assertEquals(expectedCc.getIssuer(), actualCc.getIssuer());
			Assert.assertEquals(expectedCc.getHolder(), actualCc.getHolder());
			Assert.assertEquals(expectedCc.getAccountNumber(), actualCc.getAccountNumber());
			Assert.assertEquals(expectedCc.getExpirationDate(), actualCc.getExpirationDate());
		}

	}

	@Test
	public void testSetGet() throws Exception {
		this.dao.setAccount(this.fredAcct);
		this.dao.setAccount(this.wilmaAcct);
		this.dao.setAccount(this.barneyAcct);
		this.dao.setAccount(this.mrSlateAcct);
		Account acct = this.dao.getAccount("fflintstone");
		this.compareAccounts(this.fredAcct, acct);
		acct = this.dao.getAccount("wflintstone");
		this.compareAccounts(this.wilmaAcct, acct);
		acct = this.dao.getAccount("b_rubble");
		this.compareAccounts(this.barneyAcct, acct);
		acct = this.dao.getAccount("mr_slate");
		this.compareAccounts(this.mrSlateAcct, acct);
		this.fredAcct.setAddress((Address)null);
		this.dao.setAccount(this.fredAcct);
		acct = this.dao.getAccount("fflintstone");
		Assert.assertNull(acct.getAddress());
		this.fredAcct.setCreditCard((CreditCard)null);
		this.dao.setAccount(this.fredAcct);
		acct = this.dao.getAccount("fflintstone");
		Assert.assertNull(acct.getCreditCard());
	}

	@Test
	public void testReload() throws Exception {
		this.dao.setAccount(this.fredAcct);
		this.dao.setAccount(this.wilmaAcct);
		this.dao.setAccount(this.barneyAcct);
		this.dao.setAccount(this.mrSlateAcct);
		this.dao = this.daoFactory.getAccountDao();
		Account acct = this.dao.getAccount("fflintstone");
		this.compareAccounts(this.fredAcct, acct);
		acct = this.dao.getAccount("wflintstone");
		this.compareAccounts(this.wilmaAcct, acct);
		acct = this.dao.getAccount("b_rubble");
		this.compareAccounts(this.barneyAcct, acct);
		acct = this.dao.getAccount("mr_slate");
		this.compareAccounts(this.mrSlateAcct, acct);
		Assert.assertTrue(!this.fredAcct.getAddress().equals(this.barneyAcct.getAddress()));
		Assert.assertTrue(!this.fredAcct.getCreditCard().equals(this.barneyAcct.getCreditCard()));
	}

	@Test
	public void testDelete() throws Exception {
		this.dao.setAccount(this.fredAcct);
		this.dao.setAccount(this.wilmaAcct);
		this.dao.setAccount(this.barneyAcct);
		this.dao.setAccount(this.mrSlateAcct);
		Account acct = this.dao.getAccount("wflintstone");
		this.compareAccounts(this.wilmaAcct, acct);
		this.dao.deleteAccount("wflintstone");
		acct = this.dao.getAccount("wflintstone");
		Assert.assertNull(acct);
	}

	@Test
	public void testReset() throws Exception {
		this.dao.setAccount(this.fredAcct);
		this.dao.setAccount(this.wilmaAcct);
		this.dao.setAccount(this.barneyAcct);
		this.dao.setAccount(this.mrSlateAcct);
		this.dao.getAccount("wflintstone");
		this.dao.reset();
		Account acct = this.dao.getAccount("fflintstone");
		Assert.assertNull(acct);
		acct = this.dao.getAccount("wflintstone");
		Assert.assertNull(acct);
		acct = this.dao.getAccount("b_rubble");
		Assert.assertNull(acct);
		acct = this.dao.getAccount("mr_slate");
		Assert.assertNull(acct);
	}

	@Test
	public void testClose() throws Exception {
		this.dao.setAccount(this.fredAcct);
		this.dao.setAccount(this.wilmaAcct);
		this.dao.setAccount(this.barneyAcct);
		this.dao.setAccount(this.mrSlateAcct);
		this.dao.getAccount("wflintstone");
	}
}
