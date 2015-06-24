package com.globalways.csscli.ui.purchase;

import java.util.ArrayList;
import java.util.List;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.SupplierEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SuppliersListAdapter extends BaseAdapter {

	private static final int INIT_PAGE = 1;
	private int next_page = INIT_PAGE;
	private List<SupplierEntity> list;
	private LayoutInflater mInflater;
	private Context context;
	
	public SuppliersListAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(this.context);
		list = new ArrayList<SupplierEntity>();
	}
	
	public void updateData(boolean isInit, List<SupplierEntity> list)
	{
		if(isInit)
		{
			this.list = list;
		}else{
			this.list.addAll(list);
		}
		next_page++;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public SupplierEntity getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		SupplierEntity se = list.get(position);
		if(null == convertView)
		{
			convertView = mInflater.inflate(R.layout.purchase_supplier_list_item, parent, false);
		}
		((TextView) convertView.findViewById(R.id.supplier_name)).setText(se.getName());
		((TextView) convertView.findViewById(R.id.supplier_contacts_name)).setText(se.getContact());
		((TextView) convertView.findViewById(R.id.supplier_contacts_phone)).setText(se.getTel());
		((TextView) convertView.findViewById(R.id.supplier_address)).setText(se.getAddress());
			
		return convertView;
	}

	public int getNext_page(boolean isReload) {
		if(isReload)
			this.next_page = INIT_PAGE;
		return next_page;
	}

}
