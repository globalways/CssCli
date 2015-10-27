package com.globalways.cvsb.ui.purchase;

import java.util.ArrayList;
import java.util.List;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.PurchaseGoodsEntity;
import com.globalways.cvsb.tools.Tool;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PurchaseGoodsListAdapter extends BaseAdapter {

	public List<PurchaseGoodsEntity> list;
	private LayoutInflater mInflater;
	private Context context;
	
	public PurchaseGoodsListAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(this.context);
		list = new ArrayList<PurchaseGoodsEntity>();
	}
	
	public void updateData(List<PurchaseGoodsEntity> list)
	{
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		PurchaseGoodsEntity pge = list.get(position);
		if(null == convertView)
		{
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.purchase_fragment_goods_list_item, parent, false);
			holder.tvGoodsName = ((TextView) convertView.findViewById(R.id.goods_name));
			holder.tvGoodsCounts = ((TextView) convertView.findViewById(R.id.goods_counts));
			holder.tvGoodsPrice = ((TextView) convertView.findViewById(R.id.goods_price));
			holder.tvGoodsSupplier = ((TextView) convertView.findViewById(R.id.goods_supplier));
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		String productUnit = pge.getProduct_unit()==null?"":pge.getProduct_unit();
		holder.tvGoodsCounts.setText(String.valueOf(pge.getPurchase_count())+productUnit);
		holder.tvGoodsName.setText(pge.getProduct_name());
		holder.tvGoodsPrice.setText("￥"+Tool.fenToYuan(pge.getPurchase_price()));
		String supplier_name =  pge.getSupplier_name();
		if(supplier_name == null || supplier_name.isEmpty()){
			supplier_name = pge.getSupplier() == null? "无" :pge.getSupplier().getName();
		}
		holder.tvGoodsSupplier.setText(supplier_name);
		return convertView;
	}
	
	public class ViewHolder
	{
		public TextView tvGoodsName;
		public TextView tvGoodsCounts;
		public TextView tvGoodsPrice;
		public TextView tvGoodsSupplier;
	}

}
