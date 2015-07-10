package com.globalways.csscli.entity;

import java.util.List;

public class StockSummaryEntity {
	private String stock_total;
	private int stock_fund;
	private List<StockProductEntity> products;
	public String getStock_total() {
		return stock_total;
	}
	public void setStock_total(String stock_total) {
		this.stock_total = stock_total;
	}
	public int getStock_fund() {
		return stock_fund;
	}
	public void setStock_fund(int stock_fund) {
		this.stock_fund = stock_fund;
	}
	public List<StockProductEntity> getProducts() {
		return products;
	}
	public void setProducts(List<StockProductEntity> products) {
		this.products = products;
	}
}
