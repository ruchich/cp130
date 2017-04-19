package edu.uw.ruc.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * class for seralizing instances of classes that implement Address interface
 * Created by chq-ruchic on 4/19/2017.
 */
public final class AddressSer {
    /**
     * Name for the streetAddress property
     */
    private static final String STREET_ADDRESS_PROP_NAME = "streetAddress";

    /**
     * Name for the city property
     */
    private static final String STREET_CITY_PROP_NAME = "city";

    /**
     * Name for the state property
     */
    private static final String STREET_STATE_PROP_NAME = "state";

    /**
     * Name for the zipCode property
     */
    private static final String STREET_ZIP_CODE_PROP_NAME = "zipCode";

    /**
     * write to a property file
     */
    public static void write(final OutputStream out, final Address addr) throws AccountException {

        final Properties props = new Properties();
        if (addr != null) {
            String tmp;
            tmp = addr.getStreetAddress();
            if (tmp != null) {
                props.put(STREET_ADDRESS_PROP_NAME, tmp);
            }
            tmp = addr.getCity();
            if (tmp != null) {
                props.put(STREET_CITY_PROP_NAME, tmp);
            }
            tmp = addr.getState();
            if (tmp != null) {
                props.put(STREET_STATE_PROP_NAME, tmp);
            }
            tmp = addr.getZipCode();
            if (tmp != null) {
                props.put(STREET_ZIP_CODE_PROP_NAME, tmp);
            }
        }
        try {
            props.store(out, "Address data");
        } catch (final IOException ex) {
            throw new AccountException(" Failed to write address data");
        }
    }


    /**
     * reading from a property file
     */
    public static Address read(final InputStream in) throws AccountException {
        final Properties props = new Properties();
        try (ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {
            props.load(in);

            final Address addr = appContext.getBean(Address.class);
            addr.setStreetAddress(props.getProperty(STREET_ADDRESS_PROP_NAME));
            addr.setCity(props.getProperty(STREET_CITY_PROP_NAME));
            addr.setState(props.getProperty(STREET_STATE_PROP_NAME));
            addr.setZipCode(props.getProperty(STREET_ZIP_CODE_PROP_NAME));
            return addr;
        } catch (final BeansException ex) {
            throw new AccountException("Unable to create address instance", ex);
        } catch (final IOException ex) {
            throw new AccountException("Unable to read property file", ex);

        }
    }
}
