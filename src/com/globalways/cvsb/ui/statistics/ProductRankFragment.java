package com.globalways.cvsb.ui.statistics;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.StatProductRankEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.StatisticsManager;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.BaseFragment;
import com.globalways.cvsb.ui.UITools;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ProductRankFragment extends BaseFragment implements OnRefreshListener<ListView>, OnDateSetListener, OnClickListener {
	
	private final static String START_DATE = "start_date";
	private final static String END_DATE = "end_date";
	
	private View fragmentView;
	private ProductRankListAdapter mProductRankListAdapter;
	private ListView productRankList;
	private PullToRefreshListView refreshListView;
	private TextView tvSalesCountsAll, tvPurchaseTotalAll, tvSalesTotalAll;
	private Button btnTimeStart, btnTimeEnd, btnSelectTimeUnit;
	Calendar calendar = Calendar.getInstance();
    DatePickerDialog datePickerDialog;
    Calendar start = Tool.dayStart(Calendar.getInstance());
    Calendar end  = Tool.dayEnd(Calendar.getInstance());
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(fragmentView == null)
			fragmentView = inflater.inflate(R.layout.statistics_product_rank_fragment, container, false);
		initView();
		initData();
		return fragmentView;
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadProductRankInfo(refreshListView.isHeaderShown());
	}
	
	
	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year,
			int month, int day) {
		switch (datePickerDialog.getTag()) {
		case START_DATE:
			start.clear();
			start.set(year, month, day);
			if(start.after(end))
				return;
			btnTimeStart.setText(Tool.formatDate(start.getTimeInMillis()));
			break;
		case END_DATE:
			end.set(year, month, day);
			if(end.before(start))
				return;
			btnTimeEnd.setText(Tool.formatDate(end.getTimeInMillis()));
			break;
		default:
			break;
		}
		//refreshListView.setRefreshing();
		loadProductRankInfo(true);
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnTimeStart:
			datePickerDialog.show(this.getFragmentManager(), START_DATE);
			break;
		case R.id.btnTimeEnd:
			datePickerDialog.show(this.getFragmentManager(), END_DATE);
			break;
		default:
			break;
		}
	}
	
	private void initView(){
		
		tvSalesCountsAll = (TextView) fragmentView.findViewById(R.id.tvSalesCountAll);
		tvSalesTotalAll = (TextView) fragmentView.findViewById(R.id.tvSalesTotalAll);
		tvPurchaseTotalAll = (TextView) fragmentView.findViewById(R.id.tvPurchaseTotalAll);
		
		btnSelectTimeUnit = (Button) fragmentView.findViewById(R.id.btnSelectTimeUnit);
		btnTimeStart = (Button) fragmentView.findViewById(R.id.btnTimeStart);
		btnTimeStart.setOnClickListener(this);
		btnTimeStart.setText(Tool.formatDate(start.getTimeInMillis()));
		btnTimeEnd = (Button) fragmentView.findViewById(R.id.btnTimeEnd);
		btnTimeEnd.setOnClickListener(this);
		btnTimeEnd.setText(Tool.formatDate(end.getTimeInMillis()));
		
		refreshListView = (PullToRefreshListView) fragmentView.findViewById(R.id.refreshListView);
		refreshListView.setMode(Mode.BOTH);
		refreshListView.setOnRefreshListener(this);
		productRankList = refreshListView.getRefreshableView();
		mProductRankListAdapter = new ProductRankListAdapter(getActivity());
		productRankList.setAdapter(mProductRankListAdapter);
	}
	
	private void initData(){
		//refreshListView.setRefreshing();
		loadProductRankInfo(true);
		datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH),false);
		datePickerDialog.setYearRange(2015, 2020);
	}
	
	/**
	 * 统计多个店铺,本店铺和子店铺
	 */
	public void onStoreIDChanged(){
		loadProductRankInfo(true);
	}
	
	/**
	 * 加载商品排行信息
	 */
	private void loadProductRankInfo(final boolean isReload)
	{
		if(StatisticsActivity.toStatStoreIds.size() == 0){
			StatisticsActivity.toStatStoreIds.add(MyApplication.getStoreid());
		}
		StatisticsManager.getInstance().getProductRank(StatisticsActivity.toStatStoreIds,mProductRankListAdapter.getPage(isReload), start.getTimeInMillis(),
				end.getTimeInMillis(), 0, new ManagerCallBack<StatProductRankEntity>() {

			@Override
			public void onSuccess(StatProductRankEntity returnContent) {
				super.onSuccess(returnContent);
				mProductRankListAdapter.setData(isReload,returnContent.getRanks());
				tvSalesCountsAll.setText(returnContent.getSales_count());
//				tvSalesTotalAll.setText(String.valueOf(returnContent.getSales_total()));
				tvSalesTotalAll.setText("￥"+String.valueOf(Tool.fenToYuan(returnContent.getSales_total())));
//				tvPurchaseTotalAll.setText(String.valueOf(returnContent.getPurchase_total()));
				tvPurchaseTotalAll.setText("￥"+String.valueOf(Tool.fenToYuan(returnContent.getPurchase_total())));
				refreshListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
				refreshListView.onRefreshComplete();
			}
			
		});
	}
}
