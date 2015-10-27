package com.globalways.cvsb.entity;

import com.globalways.cvsb.tools.Tool;

public class StatProductEntity {
	private String product_qr;
	private String product_name;
	private String product_unit;
	private int store_id;
	private String store_name;
	private String ht_store_id;
	private String sales_count;
	private int maori;
	private String maori_yuan;
	private int maori_apr;
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
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public String getHt_store_id() {
		return ht_store_id;
	}
	public void setHt_store_id(String ht_store_id) {
		this.ht_store_id = ht_store_id;
	}
	public String getSales_count() {
		return sales_count;
	}
	public void setSales_count(String sales_count) {
		this.sales_count = sales_count;
	}
	public int getMaori() {
		return maori;
	}
	public void setMaori(int maori) {
		this.maori = maori;
		setMaori_yuan(Tool.fenToYuan(maori));
	}
	public String getMaori_yuan() {
		return maori_yuan;
	}
	public void setMaori_yuan(String maori_yuan) {
		this.maori_yuan = maori_yuan;
	}
	public int getMaori_apr() {
		return maori_apr;
	}
	public void setMaori_apr(int maori_apr) {
		this.maori_apr = maori_apr;
	}
}
