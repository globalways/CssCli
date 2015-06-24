package com.globalways.csscli.entity;

import com.google.gson.annotations.Expose;

public class PurchaseEntity {

	@Expose
	private String batch_id;
	@Expose
	private String comment;
	@Expose
	private String out_id;
	@Expose
	private String products_count;
	@Expose
	private long purchase_amount;
	
	
	@Expose
	private String purchase_avatar;
	
	/**
	 * 距1970 秒
	 */
	@Expose
	private long purchase_time;
	@Expose
	private long store_id;
	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getOut_id() {
		return out_id;
	}
	public void setOut_id(String out_id) {
		this.out_id = out_id;
	}
	public String getProducts_count() {
		return products_count;
	}
	public void setProducts_count(String products_count) {
		this.products_count = products_count;
	}
	public long getPurchase_amount() {
		return purchase_amount;
	}
	public void setPurchase_amount(long purchase_amount) {
		this.purchase_amount = purchase_amount;
	}
	public String getPurchase_avatar() {
		return purchase_avatar;
	}
	public void setPurchase_avatar(String purchase_avatar) {
		this.purchase_avatar = purchase_avatar;
	}
	public long getPurchase_time() {
		return purchase_time;
	}
	public void setPurchase_time(long purchase_time) {
		this.purchase_time = purchase_time;
	}
	public long getStore_id() {
		return store_id;
	}
	public void setStore_id(long store_id) {
		this.store_id = store_id;
	}
	
}
