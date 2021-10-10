package com.evision.file.model;

import java.io.Serializable;
import java.util.Date;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;

public class TransactionDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@CsvBindByPosition(position = 0,format = "0000-0000-0000-0000")
    private String cardNo;

    @CsvBindByPosition(position = 1,required = true)
    private String accountNo;

    @CsvBindByPosition(position = 2,required = true)
    private String transactionType;

    @CsvBindByPosition(position = 3,required = true)
    private Double amount;

    @CsvBindByPosition(position = 4,required = true)
    @CsvDate(value = "dd-MM-yyyy")
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
