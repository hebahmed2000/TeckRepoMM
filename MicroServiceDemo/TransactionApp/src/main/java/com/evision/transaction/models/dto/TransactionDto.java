package com.evision.transaction.models.dto;

import java.io.Serializable;
import java.util.Date;

public class TransactionDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String cardNo;

    private String accountNo;

    private String transactionType;

    private Double amount;

    private Date transactionDate;

    public TransactionDto() {
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public TransactionDto(String transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionDto(Double amount) {
        this.amount = amount;
    }


    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
