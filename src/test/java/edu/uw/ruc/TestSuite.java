package edu.uw.ruc;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.uw.ruc.*;
import edu.uw.ruc.account.*;
import test.AccountManagerTest;
import test.AccountTest;
import test.DaoTest;


import static org.junit.Assert.*;

import org.junit.Test;

@RunWith(Suite.class)
@Suite.SuiteClasses({AccountTest.class, AccountManagerTest.class, DaoTest.class})


public class TestSuite {

	
	}




