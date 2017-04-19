package edu.uw.ruc.account;

import edu.uw.ext.framework.account.CreditCard;

/**
 * Created by chq-ruchic on 4/17/2017.
 * Implementing Class for interface CreditCard
 */
public class CreditCardImpl implements CreditCard {

  private  String accountNumber;
    private String expirationDate;
    private String holder;
    private String issuer;
    private String type;

    /**
     * Default constructor
     */
    public CreditCardImpl(){}

    /**
     * Gets the card account number.
     * @return the account number
     */
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the card account number.
     * @param accountNumber
     */

    @Override
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the card expiration date.
     * @return the expiration date
     */
    @Override
    public String getExpirationDate() {
        return expirationDate;
    }


    /**
     * Sets the card expiration date.
     * @param expirationDate  - the expiration date
     */
    @Override
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }


    /**
     * Gets the card holder's name.
     * @return
     */
    @Override
    public String getHolder() {
        return holder;
    }

    @Override
    public void setHolder(String holder) {
        this.holder = holder;
    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    @Override
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
