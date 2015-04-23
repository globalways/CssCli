package com.globalways.csscli.entity;

import java.util.List;

public class StatBuyEntity {

	private List<StatBuyItemEntity> stat_items;
	/** 采购总金额 */
	private double total_amount;
	/** 采购总数量 */
	private int total_count;

	public StatBuyEntity() {
		super();
	}

	public StatBuyEntity(List<StatBuyItemEntity> stat_items, double total_amount, int total_count) {
		super();
		this.stat_items = stat_items;
		this.total_amount = total_amount;
		this.total_count = total_count;
	}

	@Override
	public String toString() {
		return "StatBuyEntity [stat_items=" + stat_items.toString() + ", total_amount=" + total_amount
				+ ", total_count=" + total_count + "]";
	}

	public List<StatBuyItemEntity> getStat_items() {
		return stat_items;
	}

	public void setStat_items(List<StatBuyItemEntity> stat_items) {
		this.stat_items = stat_items;
	}

	public double getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}

	public int getTotal_count() {
		return total_count;
	}

	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}
}
