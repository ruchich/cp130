package test;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ruc.account.AccountImpl;

public class AccountTest {
	 String acctName = "ABC tech";
	 byte[] passwordHash = new byte[]{5,2,3,1,4};
	 int balance = 800000;
	 
	AccountImpl acct = null;
	
	public AccountTest() throws AccountException
	{
		acct = new AccountImpl(acctName,  passwordHash,  balance);
	}
         

	@Test
	public void testGetName() {
		String name = acct.getName();
		Assert.assertEquals(acctName, name);
	}

	@Test
	public void testSetName()throws AccountException {
		acct.setName("abcdefghi");
		String name = acct.getName();
		Assert.assertEquals("abcdefghi", name);
	}

	@Test
	public void testGetPasswordHash() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPasswordHash() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBalance() {
		int bal = acct.getBalance();
		Assert.assertEquals(800000, bal);
	}
	

	@Test
	public void testSetBalance() {
		acct.setBalance(900000);
		int actualbal = acct.getBalance();
		Assert.assertEquals(900000, actualbal);
	}

	@Test
	public void testGetFullName() {
		fail("Not yet implemented");
		
	}

	@Test
	public void testSetFullName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAddress() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetAddress() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPhone() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPhone() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetEmail() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetEmail() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCreditCard() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCreditCard() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterAccountManager() {
		fail("Not yet implemented");
	}

	@Test
	public void testReflectOrder() {
		fail("Not yet implemented");
	}

}
