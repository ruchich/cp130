package edu.uw.ruc.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.util.Properties;

/**
 * class for seralizing instances of classes that implement credit card interface
 * Created by chq-ruchic on 4/19/2017.
 */
public final class CreditCardSer {
    /**
     * constant to be written to represent a null string
     */
    private static final String NULL_STR = "<null>";

    /**
     * Utility class-disable constructor
     */
    private CreditCardSer() {

    }

    ;

    public static void write(final OutputStream out, final CreditCard cc) {
        final PrintWriter wtr = new PrintWriter(out);

        if (cc != null) {
            String s;
            s = cc.getIssuer();
            wtr.println(s == null ? NULL_STR : s);
            cc.getType();
            wtr.println(s == null ? NULL_STR : s);
            cc.getHolder();
            wtr.println(s == null ? NULL_STR : s);
            cc.getAccountNumber();
            wtr.println(s == null ? NULL_STR : s);
            cc.getExpirationDate();
            wtr.println(s == null ? NULL_STR : s);
        }
        wtr.flush();
    }

    /**
     * reading from a property file
     */
    public static CreditCard read(final InputStream in) throws AccountException {
        final BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        try (ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {

            final CreditCard cc = appContext.getBean(CreditCard.class);

            String tmp = null;
            tmp = rdr.readLine();
            cc.setIssuer((NULL_STR.equals(tmp)) ? null : tmp);
            tmp = rdr.readLine();
            cc.setType((NULL_STR.equals(tmp)) ? null : tmp);
            tmp = rdr.readLine();
            cc.setHolder((NULL_STR.equals(tmp)) ? null : tmp);
            tmp = rdr.readLine();
            cc.setAccountNumber((NULL_STR.equals(tmp)) ? null : tmp);
            tmp = rdr.readLine();
            cc.setExpirationDate((NULL_STR.equals(tmp)) ? null : tmp);
            return cc;
        } catch (final IOException ex) {
            throw new AccountException("Unable to read property file", ex);
        }
    }
}