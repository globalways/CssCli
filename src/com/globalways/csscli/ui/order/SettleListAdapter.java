package com.globalways.csscli.ui.order;

import java.util.List;

import com.globalways.csscli.entity.SettleEntity;
import com.globalways.csscli.tools.PageList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SettleListAdapter extends BaseAdapter implements PageList<SettleEntity>{

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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setData(boolean isInit, List<SettleEntity> list) {
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
	public List<SettleEntity> getList() {
		return list;
	}
	public void setList(List<SettleEntity> list) {
		this.list = list;
	}

}
