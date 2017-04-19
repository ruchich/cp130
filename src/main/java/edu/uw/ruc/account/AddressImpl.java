package edu.uw.ruc.account;

import edu.uw.ext.framework.account.Address;

/**
 * Created by chq-ruchic on 4/17/2017.
 */
public class AddressImpl implements Address {

    /**
     * City in the Address
      */
    private String city;

    /**
     * State in the Address
     */
    private String state;

    /**
     * Street in the Address
     */
    private String  streetAddress;

    /**
     * Zipcode of the Address
     */
    private String zipCode;


    /**
     * Default constructor
     */
    public AddressImpl(){}


    /**
     * Gets the city.
     * @return the city
     */
    @Override
    public String getCity() {
        return city;
    }


    /**
     * Sets the city.
     * @param city
     */
    @Override
    public void setCity(String city) {
        this.city = city;
    }


    /**
     * Gets the state.
     * @return the state
     */
    @Override
    public String getState() {
        return state;
    }


    /**
     * Sets the state.
     * @param state
     */
    @Override
    public void setState(String state) {
        this.state = state;
    }


    /**
     * Gets the street address.
     * @return the street address
     */
    @Override
    public String getStreetAddress() {
        return streetAddress;
    }


    /**
     * Sets the street address.
     * @param streetAddress
     */
    @Override
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }


    /**
     * Gets the zip code
     * @return zip code
     */
    @Override
    public String getZipCode() {
        return zipCode;
    }


    /**
     * Sets the ZIP code.
     * @param zipCode
     */
    @Override
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Concatenates the street, city, state and zip properties into the standard one line postal format.
     * @return formatted string
     */
    public String toString(){
        return String.format("%s, %s,%s, %s", streetAddress,city,state,zipCode);
    }
}
