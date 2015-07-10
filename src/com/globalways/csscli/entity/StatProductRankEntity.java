package com.globalways.csscli.entity;

import java.util.List;

public class StatProductRankEntity {
	/**
	 * 总销售数量
	 */
	private String sales_count;
	
	/**
	 * 总采购金额
	 */
	private int purchase_total;
	
	/**
	 * 总销售金额
	 */
	private int sales_total;
	private List<StatProductRankItemEntity> ranks;
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
	public List<StatProductRankItemEntity> getRanks() {
		return ranks;
	}
	public void setRanks(List<StatProductRankItemEntity> ranks) {
		this.ranks = ranks;
	}
}
