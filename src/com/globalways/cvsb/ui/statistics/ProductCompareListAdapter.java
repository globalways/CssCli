package com.globalways.cvsb.ui.statistics;

import java.util.List;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.StatProductEntity;
import com.globalways.cvsb.tools.Tool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProductCompareListAdapter extends BaseAdapter {

	private List<StatProductEntity>	list;
	private Context context;
	public ProductCompareListAdapter(Context context) {
		this.context = context;
	}
	
	
	@Override
	public int getCount() {
		if(list != null)
			return list.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(list != null){
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
		ItemView mItemView;
		String productUnit = list.get(position).getProduct_unit();
		String storeHuantuId = list.get(position).getHt_store_id();
		if (!storeHuantuId.isEmpty()) {
			storeHuantuId = "(环途连锁" + storeHuantuId + ")";
		}
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.statistics_product_compare_list_item, null);
			mItemView = new ItemView();
			mItemView.tvMaori = (TextView) convertView.findViewById(R.id.tvMaori);
			mItemView.tvMaoriApr =  (TextView) convertView.findViewById(R.id.tvMaoriApr);
			mItemView.tvProName = (TextView) convertView.findViewById(R.id.tvProName);
//			mItemView.tvProUnit = (TextView) convertView.findViewById(R.id.tvProUnit);
			mItemView.tvSalesCount = (TextView) convertView.findViewById(R.id.tvSalesCount);
			mItemView.tvStoreName = (TextView) convertView.findViewById(R.id.tvStoreName);
			convertView.setTag(mItemView);
		}
		mItemView = (ItemView) convertView.getTag();
		mItemView.tvMaori.setText("￥"+String.valueOf(Tool.fenToYuan(list.get(position).getMaori())));
		mItemView.tvMaoriApr.setText(String.valueOf(list.get(position).getMaori_apr()) + "%");
		mItemView.tvProName.setText(list.get(position).getProduct_name());
//		mItemView.tvProUnit.setText(list.get(position).getProduct_unit());
		mItemView.tvSalesCount.setText(list.get(position).getSales_count() + productUnit);
		mItemView.tvStoreName.setText(list.get(position).getStore_name() + storeHuantuId);
		return convertView;
	}


	public void setList(List<StatProductEntity> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	private class ItemView{
//		private TextView tvProName,tvProUnit,tvStoreName,tvSalesCount,tvMaori,tvMaoriApr;
		private TextView tvProName,tvStoreName,tvSalesCount,tvMaori,tvMaoriApr;
	}
	public List<StatProductEntity> getList() {
		return list;
	}

}
