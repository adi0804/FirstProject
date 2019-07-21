package com.optum.adt.models;

import java.util.List;

public class TransactionResponseModel {

	private String transaction;
	private String messageType;
	private List<TransactionObjectModel> transactionObjectModelList;

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	public List<TransactionObjectModel> getTransactionObjectModelList() {
		return transactionObjectModelList;
	}

	public void setTransactionObjectModelList(
			List<TransactionObjectModel> transactionObjectModelList) {
		this.transactionObjectModelList = transactionObjectModelList;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	@Override
	public String toString() {
		return "TransactionResponseModel [transaction=" + transaction
				+ ", transactionObjectModelList=" + transactionObjectModelList
				+ "]";
	}

}
