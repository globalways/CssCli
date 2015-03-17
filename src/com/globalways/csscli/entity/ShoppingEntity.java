package com.globalways.csscli.entity;

public class ShoppingEntity {

	private int shoppingNumber;

	private long store_id;
	private String product_brand;
	private String product_name;
	private String product_bar;
	private String product_avatar;
	private long product_price;
	private String product_unit;
	private long product_apr;
	private double stock_cnt;

	public ShoppingEntity() {
		super();
	}

	public ShoppingEntity(int shoppingNumber, long store_id, String product_brand, String product_name,
			String product_bar, String product_avatar, long product_price, String product_unit, long product_apr,
			double stock_cnt) {
		super();
		this.shoppingNumber = shoppingNumber;
		this.store_id = store_id;
		this.product_brand = product_brand;
		this.product_name = product_name;
		this.product_bar = product_bar;
		this.product_avatar = product_avatar;
		this.product_price = product_price;
		this.product_unit = product_unit;
		this.product_apr = product_apr;
		this.stock_cnt = stock_cnt;
	}

	@Override
	public String toString() {
		return "ShoppingEntity [shoppingNumber=" + shoppingNumber + ", store_id=" + store_id + ", product_brand="
				+ product_brand + ", product_name=" + product_name + ", product_bar=" + product_bar
				+ ", product_avatar=" + product_avatar + ", product_price=" + product_price + ", product_unit="
				+ product_unit + ", product_apr=" + product_apr + ", stock_cnt=" + stock_cnt + "]";
	}

	public int getShoppingNumber() {
		return shoppingNumber;
	}

	public void setShoppingNumber(int shoppingNumber) {
		this.shoppingNumber = shoppingNumber;
	}

	public long getStore_id() {
		return store_id;
	}

	public void setStore_id(long store_id) {
		this.store_id = store_id;
	}

	public String getProduct_brand() {
		return product_brand;
	}

	public void setProduct_brand(String product_brand) {
		this.product_brand = product_brand;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getProduct_bar() {
		return product_bar;
	}

	public void setProduct_bar(String product_bar) {
		this.product_bar = product_bar;
	}

	public String getProduct_avatar() {
		return product_avatar;
	}

	public void setProduct_avatar(String product_avatar) {
		this.product_avatar = product_avatar;
	}

	public long getProduct_price() {
		return product_price;
	}

	public void setProduct_price(long product_price) {
		this.product_price = product_price;
	}

	public String getProduct_unit() {
		return product_unit;
	}

	public void setProduct_unit(String product_unit) {
		this.product_unit = product_unit;
	}

	public long getProduct_apr() {
		return product_apr;
	}

	public void setProduct_apr(long product_apr) {
		this.product_apr = product_apr;
	}

	public double getStock_cnt() {
		return stock_cnt;
	}

	public void setStock_cnt(double stock_cnt) {
		this.stock_cnt = stock_cnt;
	}
}
