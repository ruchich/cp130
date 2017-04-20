package test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;

import org.junit.Assert;
import org.junit.Test;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ruc.account.*;

public final class AccountTest {
	private static final String ACCT_NAME = "fflintstone";
	private static final String ALT_ACCT_NAME = "wflintstone";
	private static final String BAD_ACCT_NAME = "fstone";
	private static final byte[] PASSWORD_BYTES = new byte[]{112, 97, 115, 115, 119, 111, 114, 100};
	private static final byte[] PASSWORD_BYTES_UPDATED = new byte[]{98, 101, 116, 116, 101, 114, 112, 97, 115, 115, 119, 111, 114, 100};
	private static final int INIT_BALANCE = 100000;
	private static final int BAD_INIT_BALANCE = 10000;
	private static final String FULL_NAME = "Fred Flintstone";
	private static final String PHONE = "(123) 567-8900";
	private static final String EMAIL = "fred@slate-rock.com";
	private static final String STREET = "101 Stoney Lane";
	private static final String CITY = "Bedrock";
	private static final String STATE = "AZ";
	private static final String ZIP = "99012";
	private static final String ISSUER = "Marble Stone Bank";
	private static final String CARD_TYPE = "MasterRock";
	private static final String HOLDER = "Fredrick Flintstone";
	private static final String ACCT_NO = "1234-5678-9012-3456";
	private static final String EXPIRATION_DATE = "03/05";
	private AccountFactory accountFactory;
	private ClassPathXmlApplicationContext appContext;

	public AccountTest() {
	}

	@Before
	public void setUp() throws Exception {
		this.appContext = new ClassPathXmlApplicationContext("context.xml");
		this.accountFactory = (AccountFactory)this.appContext.getBean("AccountFactory", AccountFactoryImpl.class);
	}

	@After
	public void tearDown() {
		if(this.appContext != null) {
			this.appContext.close();
		}

	}

	@Test
	public void testGoodAccountCreation() throws Exception {
		Account acct = this.accountFactory.newAccount("fflintstone", PASSWORD_BYTES, 100000);
		acct.setFullName("Fred Flintstone");
		acct.setPhone("(123) 567-8900");
		acct.setEmail("fred@slate-rock.com");
		Address addr = (Address)this.appContext.getBean("Address", AddressImpl.class);
		addr.setStreetAddress("101 Stoney Lane");
		addr.setCity("Bedrock");
		addr.setState("AZ");
		addr.setZipCode("99012");
		acct.setAddress(addr);
		CreditCard card = (CreditCard)this.appContext.getBean("CreditCard", CreditCardImpl.class);
		card.setType("MasterRock");
		card.setIssuer("Marble Stone Bank");
		card.setHolder("Fredrick Flintstone");
		card.setAccountNumber("1234-5678-9012-3456");
		card.setExpirationDate("03/05");
		acct.setCreditCard(card);
		Assert.assertEquals("fflintstone", acct.getName());
		Assert.assertEquals(100000L, (long)acct.getBalance());
		Assert.assertEquals("Fred Flintstone", acct.getFullName());
		Assert.assertEquals("(123) 567-8900", acct.getPhone());
		Assert.assertEquals("fred@slate-rock.com", acct.getEmail());
		Address verifyAddr = acct.getAddress();
		Assert.assertEquals("101 Stoney Lane", verifyAddr.getStreetAddress());
		Assert.assertEquals("Bedrock", verifyAddr.getCity());
		Assert.assertEquals("AZ", verifyAddr.getState());
		Assert.assertEquals("99012", verifyAddr.getZipCode());
		Assert.assertNotNull(verifyAddr.toString());
		CreditCard verifyCard = acct.getCreditCard();
		Assert.assertEquals("MasterRock", verifyCard.getType());
		Assert.assertEquals("Marble Stone Bank", verifyCard.getIssuer());
		Assert.assertEquals("Fredrick Flintstone", verifyCard.getHolder());
		Assert.assertEquals("1234-5678-9012-3456", verifyCard.getAccountNumber());
		Assert.assertEquals("03/05", verifyCard.getExpirationDate());
		acct.setBalance(10000);
		Assert.assertEquals(10000L, (long)acct.getBalance());
		Assert.assertTrue(Arrays.equals(PASSWORD_BYTES, acct.getPasswordHash()));
		acct.setPasswordHash(PASSWORD_BYTES_UPDATED);
		Assert.assertTrue(Arrays.equals(PASSWORD_BYTES_UPDATED, acct.getPasswordHash()));
	}

	@Test
	public void testBadNameAccountCreation() throws Exception {
		Assert.assertNull(this.accountFactory.newAccount("fstone", PASSWORD_BYTES, 100000));
	}

	@Test
	public void testSetAccountName() throws Exception {
		Account acct = this.accountFactory.newAccount("fflintstone", PASSWORD_BYTES, 100000);

		try {
			acct.setName("fstone");
			Assert.fail("Shouldn\'t be able to set the name to \'fstone\'");
		} catch (AccountException var3) {
			acct.setName("wflintstone");
		}

	}

	@Test
	public void testBadBalanceAccountCreation() throws Exception {
		Assert.assertNull(this.accountFactory.newAccount("fflintstone", PASSWORD_BYTES, 10000));
	}
}
