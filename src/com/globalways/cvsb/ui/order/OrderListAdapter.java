package com.globalways.cvsb.ui.order;

import java.util.List;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.OrderEntity;
import com.globalways.cvsb.tools.PagedList;
import com.globalways.cvsb.tools.Tool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderListAdapter extends BaseAdapter implements PagedList<OrderEntity>{

	private int next_page = INIT_PAGE;
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
		mItemView.tvOrderStatus.setText(OrderStatus.valueOf(list.get(position).getLast_status()).name);
//		mItemView.tvOrderTime.setText(Tool.formatDate(list.get(position).getOrder_time()*1000));
		mItemView.tvOrderTime.setText(Tool.formateDateTimeByFormat(list.get(position).getOrder_time()*1000, "MM/dd HH:mm"));
		mItemView.tvOrderTotal.setText("￥"+Tool.fenToYuan(list.get(position).getOrder_amount()));
		return convertView;
	}
	
	private class ItemView{
		TextView tvOrderID,tvOrderTime,tvOrderTotal,tvOrderStatus;
	}

	@Override
	public void setData(boolean isInit, List<OrderEntity> list) {
		if(isInit)
		{
			setList(list);
		}else{
			this.list.addAll(list);
		}
		next_page ++;
		notifyDataSetChanged();
	}
	@Override
	public int getNext_page(boolean isReload) {
		if(isReload)
			this.next_page = INIT_PAGE;
		return next_page;
	}
	public void setList(List<OrderEntity> list) {
		this.list = list;
	}

}
