package com.evision.file.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CsvTransactionParsingResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String verificationStatus;
	private List<TransactionDto> trxList = new ArrayList<>();
	private List<String> errorsList = new ArrayList<>();
	
	public List<TransactionDto> getTrxList() {
		return trxList;
	}
	public void setTrxList(List<TransactionDto> trxList) {
		this.trxList = trxList;
	}
	public List<String> getErrorsList() {
		return errorsList;
	}
	public void setErrorsList(List<String> errorsList) {
		this.errorsList = errorsList;
	}
	public String getVerificationStatus() {
		return verificationStatus;
	}
	public void setVerificationStatus(String verificationStatus) {
		this.verificationStatus = verificationStatus;
	}
	
}
