package com.globalways.cvsb.ui.purchase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.PurchaseEntity;
import com.globalways.cvsb.tools.Tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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
		((TextView)convertView.findViewById(R.id.tv_purchaseDate)).setText(Tool.formateDateTimeByFormat(Long.valueOf(pe.getPurchase_time() * 1000), "MM/dd HH:mm"));
		((TextView)convertView.findViewById(R.id.tv_purchaseAmount)).setText(pe.getProducts_count()+"");
		((TextView)convertView.findViewById(R.id.tv_purchaseTotal)).setText("￥"+ Tool.fenToYuan(pe.getPurchase_amount()));
		
		return convertView;
	}

	public void setData(boolean isInit, List<PurchaseEntity> list)
	{
		if(isInit){
			setList(list);
		}else{
			this.list.addAll(list);
		}
		if(list.size() > 0){
			next_page++;
		}
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
