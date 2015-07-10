package com.globalways.csscli.ui.stock;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.StockSummaryEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.StockManager;
import com.globalways.csscli.tools.QRCodeTools;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.BaseFragment;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.ui.product.ProductScanCodeActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

public class StockOverviewFragment extends BaseFragment implements OnClickListener,OnRefreshListener<ListView> {
	
	public static final int CODE_SCAN_BAR_REQUEST = 1;
	
	private View fragmentView;
	private Button btnSearchProudct;
	private TextView tvStockTotal, tvStockFund;
	private EditText etBarcode;
	private ImageView ivScan;
	private ListView lvStockProductList;
	private PullToRefreshListView refreshListView;
	private StockProductListAdapter mStockProductListAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.stock_overview_fragment, container, false);
		initView();
		initData();
		return fragmentView;
	}
	
	private void initView(){
		//search area
		btnSearchProudct = (Button) fragmentView.findViewById(R.id.btnSearchProduct);
		btnSearchProudct.setOnClickListener(this);
		etBarcode = (EditText) fragmentView.findViewById(R.id.etBarCode);
		ivScan = (ImageView) fragmentView.findViewById(R.id.imageScanBarCode);
		ivScan.setOnClickListener(this);
		//list
		refreshListView = (PullToRefreshListView) fragmentView.findViewById(R.id.refreshListView);
		refreshListView.setMode(Mode.BOTH);
		refreshListView.setOnRefreshListener(this);
		lvStockProductList = refreshListView.getRefreshableView();
		mStockProductListAdapter = new StockProductListAdapter(getActivity());
		lvStockProductList.setAdapter(mStockProductListAdapter);
		//text view
		tvStockTotal = (TextView) fragmentView.findViewById(R.id.tvStockTotal);
		tvStockFund = (TextView) fragmentView.findViewById(R.id.tvStockFund);
		
	}
	
	private void initData(){
		loadStockProducts(true);
		loadStockSummary();
	}

	
	private void loadStockSummary(){
		StockManager.getInstance().getStockSummary(new ManagerCallBack<StockSummaryEntity>() {

			@Override
			public void onSuccess(StockSummaryEntity returnContent) {
				super.onSuccess(returnContent);
				tvStockTotal.setText("库存数量: " +returnContent.getStock_total());
				tvStockFund.setText("占用资金: " +Tool.fenToYuan(returnContent.getStock_fund()));
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
			}
			
		});
	}
	
	/**
	 * 加载商品列表以及总览信息
	 * @param isReload
	 */
	private void loadStockProducts(boolean isReload){
		StockManager.getInstance().loadStockProducts(mStockProductListAdapter.getPage(isReload), 10, new ManagerCallBack<StockSummaryEntity>() {

			@Override
			public void onSuccess(StockSummaryEntity returnContent) {
				super.onSuccess(returnContent);
				mStockProductListAdapter.setData(returnContent.getProducts());
				refreshListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				refreshListView.onRefreshComplete();
				UITools.ToastMsg(getActivity(), msg);
			}
			
		});
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSearchProduct:
			UITools.ToastMsg(getActivity(), etBarcode.getText().toString());
			break;
		case R.id.imageScanBarCode:
			UITools.jumpProductScanCodeActivity(getActivity(), CODE_SCAN_BAR_REQUEST,
					ProductScanCodeActivity.OperationType.GET_CODE);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CODE_SCAN_BAR_REQUEST:
			if(data == null)
				return;
			if (new QRCodeTools().isBarCode(data.getStringExtra(ProductScanCodeActivity.KEY_SCAN_RESULT))) {
				String result = data.getStringExtra(ProductScanCodeActivity.KEY_SCAN_RESULT);
				etBarcode.setText(result);
				etBarcode.setSelection(result.length());
			} else {
				UITools.ToastMsg(getActivity(), "扫描条码错误,请确认条形码");
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadStockProducts(refreshListView.isHeaderShown());
	}
	
}
