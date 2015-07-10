package com.globalways.csscli.entity;

public class StatProductRankItemEntity {
	private int rank;
	private String product_qr;
	private String product_name;
	private String product_unit;
	private String sales_count;
	private int purchase_total;
	private int sales_total;
	private int sales_maori_total;
	private String current_stock;
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getProduct_qr() {
		return product_qr;
	}
	public void setProduct_qr(String product_qr) {
		this.product_qr = product_qr;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getProduct_unit() {
		return product_unit;
	}
	public void setProduct_unit(String product_unit) {
		this.product_unit = product_unit;
	}
	public String getSales_count() {
		return sales_count;
	}
	public void setSales_count(String sales_count) {
		this.sales_count = sales_count;
	}
	public int getPurchase_total() {
		return purchase_total;
	}
	public void setPurchase_total(int purchase_total) {
		this.purchase_total = purchase_total;
	}
	public int getSales_total() {
		return sales_total;
	}
	public void setSales_total(int sales_total) {
		this.sales_total = sales_total;
	}
	public int getSales_maori_total() {
		return sales_maori_total;
	}
	public void setSales_maori_total(int sales_maori_total) {
		this.sales_maori_total = sales_maori_total;
	}
	public String getCurrent_stock() {
		return current_stock;
	}
	public void setCurrent_stock(String current_stock) {
		this.current_stock = current_stock;
	}
}
