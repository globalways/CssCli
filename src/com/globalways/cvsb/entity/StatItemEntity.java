package com.globalways.cvsb.entity;


public class StatItemEntity {

	/** 日期 */
	private String date;
	/** 金额 */
	private double amount;
	/** 优惠 */
	private double discount;
	/** 数量 */
	private int count;

	public StatItemEntity() {
		super();
	}

	public StatItemEntity(String date, double amount, int discount, int count) {
		super();
		this.date = date;
		this.amount = amount;
		this.discount = discount;
		this.count = count;
	}

	@Override
	public String toString() {
		return "StatSellItemEntity [date=" + date + ", amount=" + amount + ", discount=" + discount + ", count=" + count
				+ "]";
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
