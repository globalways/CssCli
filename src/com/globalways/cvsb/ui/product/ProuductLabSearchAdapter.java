package com.globalways.cvsb.ui.product;

import java.util.List;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.ProductLabEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 条形码搜索平台商品库结果列表
 * @author wyp
 *
 */
public class ProuductLabSearchAdapter extends BaseAdapter{

	private List<ProductLabEntity> list;
	private String highLight;
	private Context contex;
	public ProuductLabSearchAdapter(Context context) {
		this.contex = context;
	}

	public void setData(List<ProductLabEntity> list, String barcode){
		this.list = list;
		this.highLight = barcode;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		if(list != null)
			return list.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(list != null)
			return list.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ProductLabEntity e = list.get(position);
		ItemView mItemView = null;
		if(convertView == null){
			convertView = LayoutInflater.from(contex).inflate(R.layout.product_addnew_search_result_item, null);
			mItemView = new ItemView();
			mItemView.tvProductName = (TextView) convertView.findViewById(R.id.tvProductName);
			mItemView.tvBar1 = (TextView) convertView.findViewById(R.id.tvBar1);
			mItemView.tvBar2 = (TextView) convertView.findViewById(R.id.tvBar2);
			mItemView.tvBar3 = (TextView) convertView.findViewById(R.id.tvBar3);
			convertView.setTag(mItemView);
		}else{
			mItemView = (ItemView) convertView.getTag();
		}
		mItemView.tvProductName.setText(e.getProduct_name());
		String[] strs = e.getProduct_bar().split(highLight,2);
		mItemView.tvBar1.setText(strs[0]);
		mItemView.tvBar2.setText(highLight);
		mItemView.tvBar3.setText(strs[1]);
		return convertView;
	}
	
	private class ItemView{
		private TextView tvProductName,tvBar1,tvBar2,tvBar3;
	}
	
}
