package com.globalways.cvsb.entity;

import com.google.gson.annotations.Expose;

/**
 * 平台商品库商品结构
 * @author wyp
 *
 */
public class ProductLabEntity {
	@Expose
	private String product_bar;
	@Expose
	private String product_name;
	@Expose
	private String product_unit;
	@Expose
	private String product_spec;
	/**
	 * 产地
	 */
	@Expose
	private String product_loc;
	/**
	 * 建议售价
	 */
	@Expose
	private long product_price;
	@Expose
	private String product_brand;
	public String getProduct_bar() {
		return product_bar;
	}
	public void setProduct_bar(String product_bar) {
		this.product_bar = product_bar;
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
	public String getProduct_spec() {
		return product_spec;
	}
	public void setProduct_spec(String product_spec) {
		this.product_spec = product_spec;
	}
	public String getProduct_loc() {
		return product_loc;
	}
	public void setProduct_loc(String product_loc) {
		this.product_loc = product_loc;
	}
	public long getProduct_price() {
		return product_price;
	}
	public void setProduct_price(long product_price) {
		this.product_price = product_price;
	}
	public String getProduct_brand() {
		return product_brand;
	}
	public void setProduct_brand(String product_brand) {
		this.product_brand = product_brand;
	}
}
