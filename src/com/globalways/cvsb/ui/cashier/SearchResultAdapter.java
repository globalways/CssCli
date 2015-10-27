package com.globalways.cvsb.ui.cashier;

import java.util.List;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.tools.Tool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchResultAdapter extends BaseAdapter{
	
	private List<ProductEntity> list;
	private Context context;
	private String keyword;
	
	public SearchResultAdapter(Context context) {
		this.context = context;
	}
	
	public void setData(List<ProductEntity> list, String keyword){
		this.list = list;
		this.keyword = keyword;
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
		if(convertView == null){
			mItemView = new ItemView();
			convertView = LayoutInflater.from(context).inflate(R.layout.cashier_code_search_result_item, parent, false);
			mItemView.tvProductName = (TextView) convertView.findViewById(R.id.tvProductName);
			mItemView.tvProductPrice = (TextView) convertView.findViewById(R.id.tvProductPrice);
			mItemView.tvBar1 = (TextView) convertView.findViewById(R.id.tvBar1);
			mItemView.tvBar2 = (TextView) convertView.findViewById(R.id.tvBar2);
			mItemView.tvBar3 = (TextView) convertView.findViewById(R.id.tvBar3);
			mItemView.tvQr1 = (TextView) convertView.findViewById(R.id.tvQr1);
			mItemView.tvQr2 = (TextView) convertView.findViewById(R.id.tvQr2);
			mItemView.tvQr3 = (TextView) convertView.findViewById(R.id.tvQr3);
			convertView.setTag(mItemView);
		}else{
			mItemView = (ItemView) convertView.getTag();
		}
		ProductEntity e = list.get(position);
		mItemView.tvProductName.setText(Tool.simpifyStr(e.getProduct_name(), 9));
		mItemView.tvProductPrice.setText("￥"+Tool.fenToYuan(e.getProduct_retail_price())+"/"+e.getProduct_unit());
		//高亮关键字
		if(e.getProduct_bar().contains(keyword)){
			String[] strs = e.getProduct_bar().split(keyword,2);
			mItemView.tvBar1.setText(strs[0]);
			mItemView.tvBar2.setText(keyword);
			mItemView.tvBar3.setText(strs[1]);
			mItemView.tvQr1.setText(e.getProduct_qr());
			mItemView.tvQr2.setText("");
			mItemView.tvQr3.setText("");
		}else if(e.getProduct_qr().contains(keyword)){
			String[] strs = e.getProduct_qr().split(keyword,2);
			mItemView.tvQr1.setText(strs[0]);
			mItemView.tvQr2.setText(keyword);
			mItemView.tvQr3.setText(strs[1]);
			mItemView.tvBar1.setText(e.getProduct_bar());
			mItemView.tvBar2.setText("");
			mItemView.tvBar3.setText("");
		}
		return convertView;
	}
	
	private class ItemView{
		private TextView tvProductName,tvProductPrice,tvBar1,tvBar2,tvBar3,tvQr1,tvQr2,tvQr3;
	}

}
