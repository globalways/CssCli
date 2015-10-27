package com.globalways.cvsb.entity;

import com.google.gson.annotations.Expose;

/**
 * 结算工单
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月22日 上午2:58:38
 */
public class SettleEntity {
	@Expose
	private int apply_type;
	@Expose
	private long apply_time;
	@Expose
	private String serial_no;
	@Expose
	private long end_time;
	@Expose
	private int store_id;
	@Expose
	private long audit_time;
	@Expose
	private long audit_done_time;
	@Expose
	private String bank_account_name;
	@Expose
	private String bank_account;
	@Expose
	private long pay_time;
	@Expose
	private long received_time;
	@Expose
	private long close_time;
	@Expose
	private int status;
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
	@Expose
	private int late_fee;
	public int getApply_type() {
		return apply_type;
	}
	public void setApply_type(int apply_type) {
		this.apply_type = apply_type;
	}
	public long getApply_time() {
		return apply_time;
	}
	public void setApply_time(long apply_time) {
		this.apply_time = apply_time;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public long getEnd_time() {
		return end_time;
	}
	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	public long getAudit_time() {
		return audit_time;
	}
	public void setAudit_time(long audit_time) {
		this.audit_time = audit_time;
	}
	public long getAudit_done_time() {
		return audit_done_time;
	}
	public void setAudit_done_time(long audit_done_time) {
		this.audit_done_time = audit_done_time;
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
	public long getPay_time() {
		return pay_time;
	}
	public void setPay_time(long pay_time) {
		this.pay_time = pay_time;
	}
	public long getReceived_time() {
		return received_time;
	}
	public void setReceived_time(long received_time) {
		this.received_time = received_time;
	}
	public long getClose_time() {
		return close_time;
	}
	public void setClose_time(long close_time) {
		this.close_time = close_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public int getLate_fee() {
		return late_fee;
	}
	public void setLate_fee(int late_fee) {
		this.late_fee = late_fee;
	}
}
