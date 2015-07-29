package com.globalways.csscli.ui.order;

import java.util.List;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.SettleEntity;
import com.globalways.csscli.tools.PagedList;
import com.globalways.csscli.tools.Tool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SettleListAdapter extends BaseAdapter implements PagedList<SettleEntity>{

	private Context context;
	private List<SettleEntity> list;
	private int next_page = INIT_PAGE;
	public SettleListAdapter(Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.order_settle_fragment_list_item, parent, false);
			mItemView = new ItemView();
			mItemView.tvEndTime = (TextView) convertView.findViewById(R.id.tvEndTime);
			mItemView.tvSettleAmount = (TextView) convertView.findViewById(R.id.tvSettleAmount);
			mItemView.tvSettleNo = (TextView) convertView.findViewById(R.id.tvSettleNo);
			mItemView.tvSettleStatus = (TextView) convertView.findViewById(R.id.tvSettleStatus);
			convertView.setTag(mItemView);
		}
		mItemView = (ItemView) convertView.getTag();
		mItemView.tvSettleNo.setText(list.get(position).getSerial_no());
		mItemView.tvEndTime.setText(Tool.formatDate(list.get(position).getEnd_time()*1000));
		mItemView.tvSettleAmount.setText(String.valueOf(list.get(position).getSerial_amount()));
		mItemView.tvSettleStatus.setText(SettleStatus.valueOf(list.get(position).getStatus()).getDesc());
		return convertView;
	}
	
	private class ItemView{
		private TextView tvSettleNo, tvEndTime, tvSettleAmount, tvSettleStatus;
	}
	
	@Override
	public void setData(boolean isInit, List<SettleEntity> list) {
		if(isInit){
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
	public List<SettleEntity> getList() {
		return list;
	}
	public void setList(List<SettleEntity> list) {
		this.list = list;
	}

}
