package com.globalways.csscli.entity;

public class StatBuyItemEntity {

	private String date;
	private double amount;
	private int count;

	public StatBuyItemEntity() {
		super();
	}

	public StatBuyItemEntity(String date, double amount, int count) {
		super();
		this.date = date;
		this.amount = amount;
		this.count = count;
	}

	@Override
	public String toString() {
		return "StatBuyItemEntity [date=" + date + ", amount=" + amount + ", count=" + count + "]";
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
