package edu.uw.ruc.beans;

import edu.uw.ext.framework.account.*;
import edu.uw.ext.framework.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chq-ruchic on 4/17/2017.
 */
public class AccountImpl implements Account {
    /**
     * Name of the account
     */
    private String acctName;


    /**
     * the password hash
     */
    byte[] passwordHash;


    /**
     * Full Name of the account holder
     */
    private String fullName;


    /**
     * balance of the account in cents
     */
    int balance;

    /**
     * Address for the account
     */
    Address address;


    /**
     * account phone number
     */
    String phone;

    /**
     * email address for the account
     */
    String email;

    /**
     * Account's CreditCard card
     */
    CreditCard card;

    /**
     * the account manager
     */
    AccountManager m;


    /**
     *the order to be reflected in the account
     */
    Order order;

    /**
     * the price the order was executed at
     */
    int executionPrice;


    /**
     * This class' logger.
     */
    static final Logger log = LoggerFactory.getLogger(AccountImpl.class);

    /**
     * No argument constructor
     */
    public AccountImpl() {
    }


    /**
     * Get the account name.
     * @return the name of the account
     */
   public String getName(){
        return acctName;
    }


    /**
     *Sets the account name. This operation is not generally used but is provided for JavaBean conformance.
     * @param acctName - the value to be set for the account name
     * @throws AccountException - if the account name is unacceptable
     */
    public void setName(String acctName)
            throws AccountException{
        this.acctName = acctName;
    }

    /**
     * Gets the hashed password.
     * @return the hashed password
     */
    public byte[] getPasswordHash(){
        return passwordHash;
    }

    /**
     * Sets the hashed password.
     * @param passwordHash  - the value to be st for the password hash
     */
    public void setPasswordHash(byte[] passwordHash){
        this.passwordHash = passwordHash;
    }


    /**
     *Gets the account balance, in cents.
     * @return - the current balance of the account
     */
    public int getBalance(){
        return balance;
    }

    /**
     * Sets the account balance.
     * @param balance - the value to set the balance to in cents
     */
    public void setBalance(int balance){
        this.balance = balance;
    }

    /**
     * Gets the full name of the account holder.
     * @return the account holders full name
     */
   public String getFullName(){
        return  fullName;
    }

    /**
     * Sets the full name of the account holder.
     * @param fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the account address.
     * @return the accounts address
     */
   public Address getAddress(){
        return  address;
    }

    /**
     * Sets the account address
     * @param address - the address for the account
     */
    public void setAddress(Address address){
        this.address = address;
    }

    /**
     * Gets the phone number.
     * @return the phone number
     */

    public String getPhone(){
        return phone;
    }

    /**
     * Sets the account phone number.
     * @param phone - value for the account phone number
     */
    public void setPhone(String phone){
        this.phone = phone;
    }

    /**
     * Gets the email address.
     * @return the email address
     */
    @Override
    public String getEmail() {
        return email;
    }


    /**
     * Sets the account email address.
     * @param email
     */
    @Override
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Gets the account credit card.
     * @return the credit card
     */
    public CreditCard getCreditCard() {
        return card;
    }


    /**
     * Sets the account credit card.
     * @param card
     */
    public void setCreditCard(CreditCard card) {
        this.card = card;
    }


    /**
     * Sets the account manager responsible for persisting/managing this account
     * @param m
     */
   public void registerAccountManager(AccountManager m){

    }

    /**
     * Incorporates the effect of an order in the balance.
     * @param  order - the order to be reflected in the account
     * @param executionPrice - the price the order was executed at
     */
   public void reflectOrder(Order order,int executionPrice){

    }
}
