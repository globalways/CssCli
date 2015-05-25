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

	private List<SupplierEntity> list;
	private LayoutInflater mInflater;
	private Context context;
	
	public SuppliersListAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(this.context);
		list = new ArrayList<SupplierEntity>();
	}
	
	public void updateData(List<SupplierEntity> list)
	{
		this.list = list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
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

}
