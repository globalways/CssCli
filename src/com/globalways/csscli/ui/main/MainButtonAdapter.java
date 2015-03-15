package com.globalways.csscli.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.globalways.csscli.R;

public class MainButtonAdapter extends BaseAdapter {

	private String[] array = null;
	private Context context;

	public MainButtonAdapter(Context context) {
		super();
		this.context = context;
	}

	public void setData(String[] array) {
		this.array = array;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (null != array) {
			return array.length;
		}
		return 0;
	}

	@Override
	public String getItem(int position) {
		if (null != array) {
			return array[position];
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
			convertView = LayoutInflater.from(context).inflate(R.layout.main_button, null);
			mItemView = new ItemView();
			findView(mItemView, convertView);
			convertView.setTag(mItemView);
		} else {
			mItemView = (ItemView) convertView.getTag();
		}
		mItemView.textMain.setText(array[position]);
		return convertView;
	}

	private class ItemView {
		TextView textMain;
	}

	public void findView(ItemView itemView, View convertView) {
		itemView.textMain = (TextView) convertView.findViewById(R.id.textMain);
	}
}
