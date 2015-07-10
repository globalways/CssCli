package com.globalways.csscli.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.tools.PicassoImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProuductFilterSelectedAdapter extends BaseAdapter {

	private List<ProductEntity> list = null;
	private Context context;
	private PicassoImageLoader imageLoader;
	public ProuductFilterSelectedAdapter(Context context) {
		this.context = context;
		imageLoader = new PicassoImageLoader(context);
	}
	
	public boolean addData(ProductEntity entity){
		if(null == list){
			list = new ArrayList<ProductEntity>();
		}
		if(!list.contains(entity))
		{
			list.add(entity);
			notifyDataSetChanged();
			return true;
		}else return false;
	}
	
	@Override
	public int getCount() {
		if (null != list) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(list != null)
		{
			return list.get(position);
		}
		return null;
	}

	public void removeItem(int position){
		list.remove(position);
		notifyDataSetChanged();
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ItemView mItemView;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.product_list_item, null);
			mItemView = new ItemView();
			findView(mItemView, convertView);
			convertView.setTag(mItemView);
		} else {
			mItemView = (ItemView) convertView.getTag();
		}
		String ava = list.get(position).getProduct_avatar();
		if (ava == null || ava.isEmpty()) {
			mItemView.productAva.setImageResource(R.drawable.logo);
		} else {
			imageLoader.showListRoundImage(list.get(position).getProduct_avatar(), R.drawable.logo, R.drawable.logo,
					mItemView.productAva);
		}
		mItemView.productName.setText(list.get(position).getProduct_name());
		mItemView.productTag.setText(list.get(position).getProduct_tag());
		mItemView.productSales.setText("销量: " + ((int) list.get(position).getSales_cnt()) + " "
				+ list.get(position).getProduct_unit());
		mItemView.productPrice.setText("￥ " + list.get(position).getProduct_retail_price() / 100.00 + " / "
				+ list.get(position).getProduct_unit() + "  ");
		return convertView;
	}

	private class ItemView {
		View viewItem;
		ImageView productAva;
		TextView productName, productTag, productSales, productPrice;
	}

	public void findView(ItemView itemView, View convertView) {
		itemView.viewItem = (View) convertView.findViewById(R.id.viewItem);
		itemView.productAva = (ImageView) convertView.findViewById(R.id.productAva);
		itemView.productName = (TextView) convertView.findViewById(R.id.productName);
		itemView.productTag = (TextView) convertView.findViewById(R.id.productTag);
		itemView.productSales = (TextView) convertView.findViewById(R.id.productSales);
		itemView.productPrice = (TextView) convertView.findViewById(R.id.productPrice);
	}

	public List<ProductEntity> getList() {
		return list;
	}

}
