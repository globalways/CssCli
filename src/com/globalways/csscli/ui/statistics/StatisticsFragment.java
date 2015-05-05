package com.globalways.csscli.ui.statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.globalways.csscli.R;
import com.globalways.csscli.entity.StatEntity;
import com.globalways.csscli.http.manager.ManagerCallBack2;
import com.globalways.csscli.http.manager.StatisticsManager;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.tools.MyLog;

public class StatisticsFragment extends Fragment implements OnClickListener, OnDateSetListener {
	private static final String TAG = StatisticsFragment.class.getSimpleName();

	private static final String KEY_GET_START_TIME = "startTime";
	private static final String KEY_GET_END_TIME = "endTime";

	private View layoutView;
	private WebView webViewStatistics;

	private Button btnStartDate, btnEndDate;
	private String startDate, endDate;

	private TextView textDiscount, textTotalAmount, textTotalDiscount, textTotalCount;
	private ListView listRecord;
	private StatisticsListAdapter listAdapter;

	public static final String KEY_STAT_TYPE = "statType";
	private int statType = StatType.TYPE_BUY;

	public class StatType {
		public static final int TYPE_BUY = 0;
		public static final int TYPE_SELL = 1;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		statType = getArguments().getInt(KEY_STAT_TYPE);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		endDate = format.format(new Date());
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		ca.add(Calendar.DAY_OF_MONTH, -1);
		startDate = format.format(ca.getTime());
		MyLog.d(TAG, "startDate: " + startDate + " endDate: " + endDate);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.statistics_fragment, container, false);
		}
		initView();
		return layoutView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData();
	}

	private void loadData() {
		if(StatisticsActivity.toStatStoreIds.size() == 0)
		{
			StatisticsActivity.toStatStoreIds.add(MyApplication.getStoreid());
		}
		StatisticsManager.getInstance().getStat(statType == StatType.TYPE_SELL, startDate, endDate, StatisticsActivity.toStatStoreIds ,
				new ManagerCallBack2<StatEntity, String>() {
					@Override
					public void onSuccess(StatEntity returnContent, final String params) {
						super.onSuccess(returnContent, params);
						webViewStatistics.setWebViewClient(new WebViewClient() {
							@Override
							public void onPageFinished(WebView view, String url) {
								super.onPageFinished(view, url);
								// 在网页加载完成之后,再调用网页上的javascript方法
								webViewStatistics.loadUrl("javascript:setSeriesData(" + params + ")");
							}

						});

						switch (statType) {
						case StatType.TYPE_BUY:
							webViewStatistics.loadUrl("file:///android_asset/echarts/statPurchase.html");
							textTotalDiscount.setVisibility(View.GONE);
							break;
						case StatType.TYPE_SELL:
							webViewStatistics.loadUrl("file:///android_asset/echarts/statOrder.html");
							textTotalDiscount.setText("总优惠：" + returnContent.getTotal_discount());
							break;
						}
						listAdapter.setData(returnContent.getStat_items());
						textTotalAmount.setText("总金额：" + returnContent.getTotal_amount());
						textTotalCount.setText("总销量：" + returnContent.getTotal_count());
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
					}
				});
	}

	private DatePickerDialog datePickerDialog = null;

	@Override
	public void onClick(View v) {
		Calendar calendar = Calendar.getInstance();
		if (datePickerDialog == null) {
			datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
		}
		datePickerDialog.setVibrate(true);
		datePickerDialog.setYearRange(2014, 2015);
		datePickerDialog.setCloseOnSingleTapDay(true);
		switch (v.getId()) {
		case R.id.btnStartDate:
			datePickerDialog.setFirstDayOfWeek(1);
			datePickerDialog.show(getActivity().getSupportFragmentManager(), KEY_GET_START_TIME);
			break;
		case R.id.btnEndDate:
			datePickerDialog.show(getActivity().getSupportFragmentManager(), KEY_GET_END_TIME);
			break;
		}
	}

	@Override
	public void onDateSet(DatePickerDialog arg0, int year, int month, int day) {
		StringBuilder sb = new StringBuilder();
		sb.append(year).append("-");
		// DatePicker库有点小问题，月份总是比实际小一个月00-11
		month++;
		sb.append(month < 10 ? "0" + month : month).append("-");
		sb.append(day < 10 ? "0" + day : day);
		switch (arg0.getTag()) {
		case KEY_GET_START_TIME:
			startDate = sb.toString();
			btnStartDate.setText(startDate + " 开始");
			break;
		case KEY_GET_END_TIME:
			endDate = sb.toString();
			btnEndDate.setText(endDate + " 结束");
			break;
		}
		loadData();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		listRecord = (ListView) layoutView.findViewById(R.id.listRecord);
		listAdapter = new StatisticsListAdapter(getActivity(), statType);
		listRecord.setAdapter(listAdapter);

		btnStartDate = (Button) layoutView.findViewById(R.id.btnStartDate);
		btnStartDate.setOnClickListener(this);
		btnStartDate.setText(startDate + " 开始");
		btnEndDate = (Button) layoutView.findViewById(R.id.btnEndDate);
		btnEndDate.setOnClickListener(this);
		btnEndDate.setText(endDate + " 结束");

		textDiscount = (TextView) layoutView.findViewById(R.id.textDiscount);
		textTotalAmount = (TextView) layoutView.findViewById(R.id.textTotalAmount);
		textTotalDiscount = (TextView) layoutView.findViewById(R.id.textTotalDiscount);
		textTotalCount = (TextView) layoutView.findViewById(R.id.textTotalCount);

		if (statType == StatType.TYPE_BUY) {
			textDiscount.setVisibility(View.GONE);
		}

		webViewStatistics = (WebView) layoutView.findViewById(R.id.webViewStatistics);
		// 因为要调用页面的javascript,所以要启用javascript支持
		webViewStatistics.getSettings().setJavaScriptEnabled(true);
		// 在我实现的过程中,没有设置setWebChromeClient时无法调用页面的javascript
		webViewStatistics.setWebChromeClient(new WebChromeClient());

	}
	
	/**
	 * 统计多个子店铺
	 * @author wyp
	 */
	public void onStoreIDChanged()
	{
		loadData();
	}

}
