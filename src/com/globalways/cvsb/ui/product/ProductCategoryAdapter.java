package com.globalways.cvsb.ui.product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.ProductCategoryEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProductCategoryAdapter extends BaseAdapter {

	List<ProductCategoryEntity> list;
	Context context;
	//保存所有的分类
	private HashSet<ProductCategoryEntity> set = new HashSet<ProductCategoryEntity>();
	public ProductCategoryAdapter(Context context) {
		this.context = context;
	}
	
	public void addItem(ProductCategoryEntity entity){
		if(null == this.list){
			this.list = new ArrayList<ProductCategoryEntity>();
		}
		this.list.add(entity);
		set.add(entity);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if(null != list)
			return list.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(null != list){
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.product_category_management_list_item, parent, false);
		}
		((TextView)convertView.findViewById(R.id.tvCategoryName)).setText(list.get(position).getName());
		return convertView;
	}

	public void setList(List<ProductCategoryEntity> list) {
		if(null == list){
			this.list = new ArrayList<ProductCategoryEntity>();
		}else{
			this.list = list;
			set.addAll(list);
		}
		notifyDataSetChanged();
	}

	public List<ProductCategoryEntity> getList() {
		return list;
	}
	
	/**
	 * 根据指定的 cid 获取分类实体
	 * @param cid
	 * @return
	 */
	public ProductCategoryEntity getEntityById(int cid){
		if(cid == -1)
			return new ProductCategoryEntity(-1);
		else{
			Iterator<ProductCategoryEntity> i = set.iterator();
			while(i.hasNext()){
				
				ProductCategoryEntity entity = i.next();
				if(entity.getId() == cid)
					return entity;
			}
			return null;
		}
	}

}
