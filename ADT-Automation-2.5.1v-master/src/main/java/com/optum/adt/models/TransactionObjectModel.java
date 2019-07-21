package com.optum.adt.models;

public class TransactionObjectModel {

	private String hl7fieldValue;
	private String flatFileFiledValue;
	private String status;

	public String getHl7fieldValue() {
		return hl7fieldValue;
	}

	public void setHl7fieldValue(String hl7fieldValue) {
		this.hl7fieldValue = hl7fieldValue;
	}

	public String getFlatFileFiledValue() {
		return flatFileFiledValue;
	}

	public void setFlatFileFiledValue(String flatFileFiledValue) {
		this.flatFileFiledValue = flatFileFiledValue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "TransactionObjectModel [hl7fieldValue=" + hl7fieldValue
				+ ", flatFileFiledValue=" + flatFileFiledValue + ", status="
				+ status + "]";
	}

}
