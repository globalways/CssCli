package com.globalways.cvsb.entity;

import com.google.gson.annotations.Expose;

/**
 * 订单管理，订单所含商品结构
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月27日 下午3:22:54
 */
public class OrderProductsEntity {
	@Expose
	private int id;
	@Expose
	private String order_id;
	@Expose
	private int product_count;
	@Expose
	private String product_name;
	@Expose
	private long product_price;
	@Expose
	private String product_qr;
	@Expose
	private String product_unit;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public int getProduct_count() {
		return product_count;
	}
	public void setProduct_count(int product_count) {
		this.product_count = product_count;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public long getProduct_price() {
		return product_price;
	}
	public void setProduct_price(long product_price) {
		this.product_price = product_price;
	}
	public String getProduct_qr() {
		return product_qr;
	}
	public void setProduct_qr(String product_qr) {
		this.product_qr = product_qr;
	}
	public String getProduct_unit() {
		return product_unit;
	}
	public void setProduct_unit(String product_unit) {
		this.product_unit = product_unit;
	}
	
}
