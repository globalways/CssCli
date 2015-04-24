package com.globalways.csscli.entity;

import java.util.List;

public class StatEntity {

	private List<StatItemEntity> stat_items;
	/** 总销售金额 */
	private double total_amount;
	/** 总优惠金额 */
	private double total_discount;
	/** 总销售数量 */
	private int total_count;

	public StatEntity() {
		super();
	}

	public StatEntity(List<StatItemEntity> stat_items, double total_amount, double total_discount,
			int total_count) {
		super();
		this.stat_items = stat_items;
		this.total_amount = total_amount;
		this.total_discount = total_discount;
		this.total_count = total_count;
	}

	@Override
	public String toString() {
		return "StatSellEntity [stat_items=" + stat_items.toString() + ", total_amount=" + total_amount
				+ ", total_discount=" + total_discount + ", total_count=" + total_count + "]";
	}

	public List<StatItemEntity> getStat_items() {
		return stat_items;
	}

	public void setStat_items(List<StatItemEntity> stat_items) {
		this.stat_items = stat_items;
	}

	public double getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}

	public double getTotal_discount() {
		return total_discount;
	}

	public void setTotal_discount(double total_discount) {
		this.total_discount = total_discount;
	}

	public int getTotal_count() {
		return total_count;
	}

	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}
}
