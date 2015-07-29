package com.globalways.csscli.entity;

/**
 * 商品分类结构
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月27日 下午3:24:07
 */
public class ProductCategoryEntity {
	private int id;
	private int parent_id;
	private String name;
	private int store_id;
	
	public ProductCategoryEntity(int id) {
		this.id = id;
		this.name = "商品分类";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParent_id() {
		return parent_id;
	}
	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
}
