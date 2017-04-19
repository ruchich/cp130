package edu.uw.ruc.account;

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
    private byte[] passwordHash;


    /**
     * Full Name of the account holder
     */
    private String fullName;


    /**
     * balance of the account in cents
     */
    private  int balance = Integer.MIN_VALUE;

    /**
     * Address for the account
     */
    private Address address;


    /**
     * account phone number
     */
    private String phone;

    /**
     * email address for the account
     */
    private String email;

    /** Account's CreditCard card */
    private CreditCard card;

    /** the account manager */
    private  AccountManager acctMgr;


    /**the order to be reflected in the account */
    private  Order order;

    /** the price the order was executed at */
    private  int executionPrice;


    /** This class' logger.*/
  private  static final Logger log = LoggerFactory.getLogger(AccountImpl.class);

    /** the min. allowed account length*/
    private  static final int MIN_ACCT_LEN  = 8;

    /** the min.allowed acct balance*/
    private static final int MIN_ACCT_BALANCE = 100_000;

    /** No argument constructor */
    public AccountImpl() {
    }

    /**
     *
     * @param acctName
     * @param passwordHash
     * @param balance
     * @throws AccountException if the acct name is too short or balance too low
     */
     public AccountImpl( final String acctName, final byte[] passwordHash, final int balance)throws AccountException{
         if(balance< MIN_ACCT_BALANCE){
             final String msg  = String.format("Account creation failed for ,"+ acctName, balance);
             log.warn(msg);
             throw new AccountException(msg);
         }
         setName(acctName);
         setPasswordHash(passwordHash);
         this.balance = balance;
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
    public void setName(String acctName) throws AccountException{
        if(acctName==null || acctName.length()<MIN_ACCT_LEN ){
            final String msg = String.format("Account name '%s' is unaccetable" , acctName);
            log.warn(msg);
            throw new AccountException(msg);
        }
        this.acctName = acctName;
    }

    /**
     * Gets the hashed password.
     * @return the hashed password
     */
    public byte[] getPasswordHash(){

        byte[]copy = null;
        if(passwordHash!=null){
            copy = new byte[passwordHash.length];
            System.arraycopy(passwordHash,0,copy,0,passwordHash.length);
        }
        return copy;
    }

    /**
     * Sets the hashed password.
     * @param passwordHash  - the value to be st for the password hash
     */
    public void setPasswordHash(byte[] passwordHash){
        byte[]copy = null;
        if(passwordHash!=null){
            copy = new byte[passwordHash.length];
            System.arraycopy(passwordHash,0,copy,0,passwordHash.length);
        }

        this.passwordHash = copy;
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
        if(acctMgr ==null){
            acctMgr = m;
        }else{
            log.info("Attempting to set the account manager");

        }
    }

    /**
     * Incorporates the effect of an order in the balance.
     * @param  order - the order to be reflected in the account
     * @param executionPrice - the price the order was executed at
     */
   public void reflectOrder(Order order,int executionPrice){
       try{
           balance += order.valueOfOrder(executionPrice);
           if (acctMgr != null){
               acctMgr.persist(this);
           }else{
               log.error("Account manager has not been intialized", new Exception());
           }
       } catch (final AccountException ex){
           log.error(String.format("Failed to persist account %s ", ex));
       }

    }
}
