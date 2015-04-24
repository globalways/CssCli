package com.globalways.csscli.ui.statistics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.StatEntity;
import com.globalways.csscli.http.manager.ManagerCallBack2;
import com.globalways.csscli.http.manager.StatisticsManager;

public class StatisticsFragment extends Fragment {

	private View layoutView;
	private WebView webViewStatistics;

	private TextView textDiscount, textTotalAmount, textTotalDiscount, textTotalCount;
	private ListView listRecord;
	private StatisticsListAdapter listAdapter;

	public static final String KEY_STAT_TYPE = "statType";
	private int statType = StatType.TYPE_BUY;

	public class StatType {
		public static final int TYPE_BUY = 0;
		public static final int TYPE_SELL = 1;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		statType = getArguments().getInt(KEY_STAT_TYPE);
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
		StatisticsManager.getInstance().getStat(statType == StatType.TYPE_SELL,
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

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		listRecord = (ListView) layoutView.findViewById(R.id.listRecord);
		listAdapter = new StatisticsListAdapter(getActivity(), statType);
		listRecord.setAdapter(listAdapter);
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
}
