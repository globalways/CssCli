package com.globalways.csscli.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.StatProductRankItemEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProductRankListAdapter extends BaseAdapter {
	
	private static final int INIT_PAGE = 1;
	private int page = INIT_PAGE;
	private Context context;
	private List<StatProductRankItemEntity> list;
	
	public ProductRankListAdapter(Context context) {
		this.context = context;
	}
	public void setData(boolean isReload,List<StatProductRankItemEntity> list)
	{
		if(isReload)
		{
			this.list = list;
			page = INIT_PAGE;
		}
		else{
			this.list.addAll(list);
		}
		page++;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if(list == null){
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if(list != null)
			return list.get(position);
		else return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemView mItemView;
		StatProductRankItemEntity e = list.get(position);
		if(null == convertView)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.statistics_product_rank_list_item, parent, false);
			mItemView = new ItemView();
			mItemView.tvRankNum = (TextView) convertView.findViewById(R.id.tvRankNum);
			mItemView.tvProductName = (TextView) convertView.findViewById(R.id.tvProductName);
			mItemView.tvSalesCount = (TextView) convertView.findViewById(R.id.tvSalesCount);
			mItemView.tvSalesTotal = (TextView) convertView.findViewById(R.id.tvSalesTotal);
			mItemView.tvPurchaseTotal = (TextView) convertView.findViewById(R.id.tvPurchaseTotal);
			mItemView.tvMaoriTotal = (TextView) convertView.findViewById(R.id.tvMaoriTotal);
			mItemView.tvStock = (TextView) convertView.findViewById(R.id.tvStock);
			convertView.setTag(mItemView);
		}else{
			mItemView = (ItemView) convertView.getTag();
		}
		mItemView.tvRankNum.setText(String.valueOf(e.getRank()));
		mItemView.tvMaoriTotal.setText(String.valueOf(e.getSales_maori_total()));
		mItemView.tvProductName.setText(e.getProduct_name());
		mItemView.tvPurchaseTotal.setText(String.valueOf(e.getPurchase_total()));
		mItemView.tvSalesCount.setText(e.getSales_count());
		mItemView.tvSalesTotal.setText(String.valueOf(e.getSales_total()));
		mItemView.tvStock.setText(e.getCurrent_stock());
		return convertView;
	}
	
	public class ItemView{
		public TextView tvRankNum, tvProductName, tvSalesCount, tvPurchaseTotal, tvSalesTotal, tvMaoriTotal, tvStock;
	}

	public int getPage(boolean isReload) {
		if(isReload)
			page = INIT_PAGE;
		return page;
	}

}
