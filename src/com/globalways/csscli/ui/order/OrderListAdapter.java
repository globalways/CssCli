package com.globalways.csscli.ui.order;

import java.util.List;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.OrderEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderListAdapter extends BaseAdapter {

	private Context context;
	private List<OrderEntity> list;
	public OrderListAdapter(Context context) {
		this.context = context;
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
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.order_fragment_list_item, parent, false);
			mItemView = new ItemView();
			mItemView.tvOrderID = (TextView) convertView.findViewById(R.id.tvOrderID);
			mItemView.tvOrderStatus = (TextView) convertView.findViewById(R.id.tvOrderStatus);
			mItemView.tvOrderTime = (TextView) convertView.findViewById(R.id.tvOrderTime);
			mItemView.tvOrderTotal = (TextView) convertView.findViewById(R.id.tvOrderTotal);
			convertView.setTag(mItemView);
		}
		mItemView = (ItemView) convertView.getTag();
		mItemView.tvOrderID.setText(list.get(position).getOrder_id());
		mItemView.tvOrderStatus.setText("status");
		mItemView.tvOrderTime.setText("time");
		mItemView.tvOrderTotal.setText("total price");
		return convertView;
	}
	
	private class ItemView{
		TextView tvOrderID,tvOrderTime,tvOrderTotal,tvOrderStatus;
	}

}
