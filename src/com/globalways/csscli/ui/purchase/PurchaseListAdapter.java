package com.globalways.csscli.ui.purchase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.PurchaseEntity;
import com.globalways.csscli.tools.Tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PurchaseListAdapter extends BaseAdapter {

	private static final int INIT_PAGE = 1;
	private int next_page = INIT_PAGE;
	private List<PurchaseEntity> list;
	private Context context;
	private LayoutInflater mInflater;
	
	public PurchaseListAdapter(Context context, List<PurchaseEntity> list) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	public PurchaseListAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(this.context);
		list = new ArrayList<PurchaseEntity>();
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

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		PurchaseEntity pe = list.get(position);
		if(convertView == null)
		{
			convertView = mInflater.inflate(R.layout.purchase_fragment_list_item, parent, false);
		}
		((TextView)convertView.findViewById(R.id.tv_purchaseBatchNumber)).setText(pe.getBatch_id());
		((TextView)convertView.findViewById(R.id.tv_purchaseDate)).setText(Tool.formatDate(Long.valueOf(pe.getPurchase_time() * 1000)));
		((TextView)convertView.findViewById(R.id.tv_purchaseAmount)).setText(pe.getProducts_count()+"");
		((TextView)convertView.findViewById(R.id.tv_purchaseTotal)).setText(Tool.fenToYuan(pe.getPurchase_amount()));
		
		return convertView;
	}

	public void setData(boolean isInit, List<PurchaseEntity> list)
	{
		if(isInit)
		{
			setList(list);
		}else{
			this.list.addAll(list);
		}
		next_page++;
		notifyDataSetChanged();
	}
	public void setList(List<PurchaseEntity> list) {
		this.list = list;
	}

	public List<PurchaseEntity> getList() {
		return list;
	}

	public int getNext_page(boolean isReload) {
		if(isReload)
			this.next_page = INIT_PAGE;
		return next_page;
	}

}
