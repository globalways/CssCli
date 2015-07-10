package com.globalways.csscli.ui.statistics;

import java.util.Calendar;
import java.util.List;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.globalways.csscli.R;
import com.globalways.csscli.entity.StatProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.StatisticsManager;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.BaseFragment;
import com.globalways.csscli.ui.UITools;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ProductCompareFragment extends BaseFragment implements OnClickListener, OnDateSetListener {
	
	private static final int SALES_COUNT = 1;
	private static final int MAORI = 2;
	private static final int MAORI_APR = 3;
	private int currentCompareType = SALES_COUNT;
	private final static String START_DATE = "start_date";
	private final static String END_DATE = "end_date";
	private View fragmentView;
	private String qrs;
	private WebView wvCompareTable;
	private ListView lvSelectedProductList;
	private ProductCompareListAdapter mProductCompareListAdapter;
	private Button btnDateStart, btnDateEnd, btnFinish, btnCompareType;
	private DatePickerDialog datePickerDialog;
	private Calendar calendar,start,end;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		qrs = getArguments().getString("product_qrs");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null ==  fragmentView){
			fragmentView = inflater.inflate(R.layout.statistics_product_compare_fragment, container, false);
		}
		initData();
		initView();
		return fragmentView;
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initView(){
		//web view
		wvCompareTable = (WebView) fragmentView.findViewById(R.id.wvCompareTable);
		wvCompareTable.getSettings().setJavaScriptEnabled(true);
		wvCompareTable.setWebChromeClient(new WebChromeClient());
		wvCompareTable.loadUrl("file:///android_asset/echarts/statProductCompare.html");
		//product list
		lvSelectedProductList = (ListView) fragmentView.findViewById(R.id.lvSelectedProduct);
		mProductCompareListAdapter = new ProductCompareListAdapter(getActivity());
		lvSelectedProductList.setAdapter(mProductCompareListAdapter);
		//Buttons
		btnDateStart = (Button) fragmentView.findViewById(R.id.btnDateStart);
		btnDateStart.setText(Tool.formatDate(start.getTimeInMillis()));
		btnDateEnd = (Button) fragmentView.findViewById(R.id.btnDateEnd);
		btnDateEnd.setText(Tool.formatDate(end.getTimeInMillis()));
		btnFinish = (Button) fragmentView.findViewById(R.id.btnFinish);
		btnCompareType = (Button) fragmentView.findViewById(R.id.btnCompareType);
		btnCompareType.setText(getCompareTypeName(currentCompareType));
		btnCompareType.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
		btnDateEnd.setOnClickListener(this);
		btnDateStart.setOnClickListener(this);
		//DateTimePicker
		datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH),false);
		datePickerDialog.setYearRange(2015, 2020);
	}
	
	private void initData(){
		calendar = Calendar.getInstance();
		start = Calendar.getInstance();
		end = Calendar.getInstance();
		loadCompareData();
	}
	
	/**
	 * 刷新webview图表 
	 * @param list
	 */
	private void refreshWebViewTable(final List<StatProductEntity> list){
		
		wvCompareTable.reload();
		wvCompareTable.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				
				JsonObject e = new JsonObject();
				e.addProperty("type_name", getCompareTypeName(currentCompareType));
				e.addProperty("type", currentCompareType);
				e.addProperty("series", new Gson().toJson(list));
				view.loadUrl("javascript:setSeriesData(" + e.toString() + ")");
			}
		});
	}
	
	/**
	 * 加载对比数据
	 */
	private void loadCompareData(){
		StatisticsManager.getInstance().compareProduct(qrs, start.getTimeInMillis()/1000, end.getTimeInMillis()/1000, new ManagerCallBack<List<StatProductEntity>>() {

			@Override
			public void onSuccess(final List<StatProductEntity> returnContent) {
				super.onSuccess(returnContent);
				mProductCompareListAdapter.setList(returnContent);
				refreshWebViewTable(returnContent);
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
			}
			
		});
	}
	
	/**
	 * 获取对比类型名称
	 * @param compareid
	 * @return
	 */
	private String getCompareTypeName(int compareid){
		switch (compareid) {
		case SALES_COUNT:
			return getString(R.string.statistics_product_compare_type_salescount);
		case MAORI:
			return getString(R.string.statistics_product_compare_type_maori);
		case MAORI_APR:
			return getString(R.string.statistics_product_compare_type_maoriapr);
		default:
			return null;
		}
	}
	
	
	private void showCompareTypeMenu()
	{
		PopupMenu popup = new PopupMenu(getActivity(), btnCompareType);
		popup.getMenuInflater().inflate(R.menu.statistics_product_compare_type, popup.getMenu());
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menuSalesCount:
					btnCompareType.setText(getString(R.string.statistics_product_compare_type_salescount));
					currentCompareType = SALES_COUNT;
					break;
				case R.id.menuMaori:
					btnCompareType.setText(getString(R.string.statistics_product_compare_type_maori));
					currentCompareType = MAORI;
					break;
				case R.id.menuMaoriApr:
					btnCompareType.setText(getString(R.string.statistics_product_compare_type_maoriapr));
					currentCompareType = MAORI_APR;
					break;
				default:
					return false;
				}
				refreshWebViewTable(mProductCompareListAdapter.getList());
				return true;
			}
		});
		popup.show();
	}
	
	/**
	 * 获取选择商品fragment
	 * @return
	 */
	private ProductFilterFragment getFilterFragment()
	{
		List<Fragment> list = getFragmentManager().getFragments();
		for(int i=0;i<list.size();i++)
			if(list.get(i) instanceof ProductFilterFragment)
				return (ProductFilterFragment) list.get(i);
		return null;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDateStart:
			datePickerDialog.show(getFragmentManager(), START_DATE);
			break;
		case R.id.btnDateEnd:
			datePickerDialog.show(getFragmentManager(), END_DATE);
			break;
		case R.id.btnFinish:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
			ft.hide(this).show(getFilterFragment()).commit();
			//((StatisticsActivity)getActivity()).switchFragment(3);
			break;
		case R.id.btnCompareType:
			showCompareTypeMenu();
			break;
		default:
			break;
		}
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
			btnDateStart.setText(Tool.formatDate(start.getTimeInMillis()));
			break;
		case END_DATE:
			end.set(year, month, day, 24, 00, 00);
			if(end.before(start))
				return;
			btnDateEnd.setText(Tool.formatDate(end.getTimeInMillis()));
			break;
		default:
			break;
		}
		
		loadCompareData();
	}
}
