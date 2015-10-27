package com.globalways.cvsb.ui.stock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.StockSummaryEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.StockManager;
import com.globalways.cvsb.tools.QRCodeTools;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.BaseFragment;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.ui.product.ProductScanCodeActivity;
import com.globalways.cvsb.view.MyDialogManager;
import com.globalways.cvsb.view.TextWatcherDelay;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

public class StockOverviewFragment extends BaseFragment implements OnClickListener,OnRefreshListener<ListView> {
	
	public static final int CODE_SCAN_BAR_REQUEST = 1;
	
	private View fragmentView, viewSearchResult;
	private Button btnSearchProudct;
	private TextView tvStockTotal, tvStockFund, tvKeyword;
	private EditText etSearchBox;
	private ImageView ivScan, ivCloseSearchTitle;
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
		etSearchBox = (EditText) fragmentView.findViewById(R.id.etSearchBox);
		//单独设置hint字体大小
		SpannableString ss = new SpannableString(etSearchBox.getHint());
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12,true);
		ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		etSearchBox.setHint(new SpannedString(ss)); 
		
		etSearchBox.addTextChangedListener(new TextWatcherDelay(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				String data = msg.getData().getString(TextWatcherDelay.DATA);
				if(data==null || data.isEmpty()){
					closeSearch();
				}
			}
		}, 1000));
		ivScan = (ImageView) fragmentView.findViewById(R.id.imageScanBarCode);
		ivScan.setOnClickListener(this);
		//search title
		viewSearchResult = fragmentView.findViewById(R.id.viewSearchResult);
		tvKeyword = (TextView) fragmentView.findViewById(R.id.tvKeyword);
		ivCloseSearchTitle = (ImageView) fragmentView.findViewById(R.id.ivCloseSearchTitle);	
		ivCloseSearchTitle.setOnClickListener(this);
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
				tvStockTotal.setText("总库存数量: " +returnContent.getStock_total());
				tvStockFund.setText("总占用资金(元): ￥" +Tool.fenToYuan(returnContent.getStock_fund()));
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
		final AlertDialog dialog = MyDialogManager.buildProgress(getActivity());
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("加载数据中");
		dialog.show();
		StockManager.getInstance().loadStockProducts(mStockProductListAdapter.getPage(isReload), 10, new ManagerCallBack<StockSummaryEntity>() {

			@Override
			public void onSuccess(StockSummaryEntity returnContent) {
				super.onSuccess(returnContent);
				mStockProductListAdapter.setData(returnContent.getProducts());
				refreshListView.onRefreshComplete();
				dialog.dismiss();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				refreshListView.onRefreshComplete();
				dialog.dismiss();
				UITools.ToastMsg(getActivity(), msg);
			}
			
		});
	}
	
	
	private void loadSearchResult(final String keyword){
		final AlertDialog dialog = MyDialogManager.buildProgress(getActivity());
		dialog.setMessage("正在搜索【"+keyword+"】");
		dialog.show();
		StockManager.getInstance().searchProducts(keyword,1,Integer.MAX_VALUE, new ManagerCallBack<StockSummaryEntity>() {
			@Override
			public void onSuccess(StockSummaryEntity returnContent) {
				super.onSuccess(returnContent);
				viewSearchResult.setVisibility(View.VISIBLE);
				tvKeyword.setText(keyword);
				mStockProductListAdapter.clear();
				mStockProductListAdapter.setData(returnContent.getProducts());
				refreshListView.onRefreshComplete();
				refreshListView.setMode(Mode.DISABLED);
				dialog.dismiss();
			}
			
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				refreshListView.onRefreshComplete();
				UITools.ToastMsg(getActivity(), msg);
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * 关闭搜索
	 */
	private void closeSearch(){
		if(viewSearchResult.getVisibility() == View.VISIBLE){
			viewSearchResult.setVisibility(View.GONE);
		}
		etSearchBox.setText("");
		mStockProductListAdapter.reset();
		//启用refreshListView
		refreshListView.setMode(Mode.BOTH);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSearchProduct:
			loadSearchResult(etSearchBox.getText().toString());
			break;
		case R.id.imageScanBarCode:
			UITools.jumpProductScanCodeActivity(getActivity(), CODE_SCAN_BAR_REQUEST,
					ProductScanCodeActivity.OperationType.GET_CODE);
			break;
		case R.id.ivCloseSearchTitle:
			closeSearch();
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
				etSearchBox.setText(result);
				etSearchBox.setSelection(result.length());
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
