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
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.StatBuyEntity;
import com.globalways.csscli.http.manager.ManagerCallBack2;
import com.globalways.csscli.http.manager.StatisticsManager;

public class StatisticsBuyFragment extends Fragment {

	private View layoutView;
	private WebView webViewStatistics;
	private ListView listRecord;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.statistics_fragment, container, false);
		}
		initView();
		loadData();
		return layoutView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void loadData() {
		StatisticsManager.getInstance().getBuyStat(new ManagerCallBack2<StatBuyEntity, String>() {
			@Override
			public void onSuccess(StatBuyEntity returnContent, final String params) {
				super.onSuccess(returnContent, params);
				webViewStatistics.setWebViewClient(new WebViewClient() {
					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);
						// 在网页加载完成之后,再调用网页上的javascript方法
						webViewStatistics.loadUrl("javascript:setSeriesData(" + params + ")");
					}

				});

				webViewStatistics.loadUrl("file:///android_asset/echarts/statPurchase.html");
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
		webViewStatistics = (WebView) layoutView.findViewById(R.id.webViewStatistics);
		// 因为要调用页面的javascript,所以要启用javascript支持
		webViewStatistics.getSettings().setJavaScriptEnabled(true);
		// 在我实现的过程中,没有设置setWebChromeClient时无法调用页面的javascript
		webViewStatistics.setWebChromeClient(new WebChromeClient());

	}

}
