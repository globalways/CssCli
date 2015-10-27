package com.globalways.cvsb.ui.statistics;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.StatItemEntity;

public class StatisticsListAdapter extends BaseAdapter {

	private List<StatItemEntity> list = null;
	private Context context;
	private int statType = StatisticsFragment.StatType.TYPE_BUY;

	public StatisticsListAdapter(Context context, int statType) {
		super();
		this.context = context;
		this.statType = statType;
	}

	public void setData(List<StatItemEntity> list) {
		this.list = list;
		notifyDataSetChanged();
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
		if (null != list) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ItemView mItemView;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.statistics_list_item, null);
			mItemView = new ItemView();
			findView(mItemView, convertView);
			convertView.setTag(mItemView);
		} else {
			mItemView = (ItemView) convertView.getTag();
		}
		
		mItemView.textDate.setText(list.get(position).getDate());
		mItemView.textAmount.setText("￥"+list.get(position).getAmount() + "");
		mItemView.textCount.setText(list.get(position).getCount() + "");
		switch (statType) {
		case StatisticsFragment.StatType.TYPE_BUY:
			mItemView.textDiscount.setVisibility(View.GONE);
			break;
		case StatisticsFragment.StatType.TYPE_SELL:
			mItemView.textDiscount.setVisibility(View.VISIBLE);
			mItemView.textDiscount.setText("￥"+list.get(position).getDiscount() + "");
			break;
		}
		return convertView;
	}

	private class ItemView {
		TextView textDate, textAmount, textDiscount, textCount;
	}

	public void findView(ItemView itemView, View convertView) {
		itemView.textDate = (TextView) convertView.findViewById(R.id.textDate);
		itemView.textAmount = (TextView) convertView.findViewById(R.id.textAmount);
		itemView.textDiscount = (TextView) convertView.findViewById(R.id.textDiscount);
		itemView.textCount = (TextView) convertView.findViewById(R.id.textCount);
	}
}
