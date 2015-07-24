package com.globalways.csscli.entity;

import com.google.gson.annotations.Expose;

public class SettleAuditEntity {
	@Expose
	private String serial_no;
	@Expose
	private String bank_account_name;
	@Expose
	private String bank_account;
	@Expose
	private int serial_amount;
	@Expose
	private int cash_amount;
	@Expose
	private int online_amount;
	@Expose
	private int service_fee;
	@Expose
	private int return_amount;
	@Expose
	private String comment;
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public String getBank_account_name() {
		return bank_account_name;
	}
	public void setBank_account_name(String bank_account_name) {
		this.bank_account_name = bank_account_name;
	}
	public String getBank_account() {
		return bank_account;
	}
	public void setBank_account(String bank_account) {
		this.bank_account = bank_account;
	}
	public int getSerial_amount() {
		return serial_amount;
	}
	public void setSerial_amount(int serial_amount) {
		this.serial_amount = serial_amount;
	}
	public int getCash_amount() {
		return cash_amount;
	}
	public void setCash_amount(int cash_amount) {
		this.cash_amount = cash_amount;
	}
	public int getOnline_amount() {
		return online_amount;
	}
	public void setOnline_amount(int online_amount) {
		this.online_amount = online_amount;
	}
	public int getService_fee() {
		return service_fee;
	}
	public void setService_fee(int service_fee) {
		this.service_fee = service_fee;
	}
	public int getReturn_amount() {
		return return_amount;
	}
	public void setReturn_amount(int return_amount) {
		this.return_amount = return_amount;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
