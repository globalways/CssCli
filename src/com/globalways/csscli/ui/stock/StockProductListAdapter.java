package com.globalways.csscli.ui.stock;

import java.util.List;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.StockProductEntity;
import com.globalways.csscli.tools.Tool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StockProductListAdapter extends BaseAdapter {

	private static final int INIT_PAGE = 1;
	private int page = INIT_PAGE;
	private List<StockProductEntity> list;
	private Context context;
	public StockProductListAdapter(Context context) {
		this.context = context;
	}
	
	public int getPage(boolean isReload) {
		if(isReload){
			page = INIT_PAGE;
		}
		return page;
	}
	
	public void setData(List<StockProductEntity> list){
		if(null == this.list){
			this.list = list;
		}else{
			this.list.addAll(list);
		}
		page++;
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
		if(null != list)
			return list.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemView mItemView;
		if(null == convertView)
		{
			mItemView = new ItemView();
			convertView = LayoutInflater.from(context).inflate(R.layout.stock_overview_list_item, parent, false);
			mItemView.tvBarcode = (TextView) convertView.findViewById(R.id.tvBarcode);
			mItemView.tvProductName = (TextView) convertView.findViewById(R.id.tvProductName);
			mItemView.tvPurchasePrice = (TextView) convertView.findViewById(R.id.tvPurchasePrice);
			mItemView.tvRetailPrice = (TextView) convertView.findViewById(R.id.tvRetailPrice);
			mItemView.tvSalesCount = (TextView) convertView.findViewById(R.id.tvSalesCount);
			mItemView.tvStockCount = (TextView) convertView.findViewById(R.id.tvStockCount);
			convertView.setTag(mItemView);
		}
		mItemView = (ItemView) convertView.getTag();
		mItemView.tvBarcode.setText(list.get(position).getProduct_bar());
		mItemView.tvProductName.setText(list.get(position).getProduct_name());
		mItemView.tvPurchasePrice.setText(Tool.fenToYuan(list.get(position).getPurchase_price()));
		mItemView.tvRetailPrice.setText(Tool.fenToYuan(list.get(position).getProduct_retail_price()));
		mItemView.tvSalesCount.setText(String.valueOf(list.get(position).getSales_count()));
		mItemView.tvStockCount.setText(String.valueOf(list.get(position).getStock_count()));
		return convertView;
	}
	
	private class ItemView{
		private TextView tvBarcode, tvProductName, tvPurchasePrice, tvRetailPrice, tvSalesCount, tvStockCount;
	}

}
