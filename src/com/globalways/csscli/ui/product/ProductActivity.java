package com.globalways.csscli.ui.product;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.tools.MyLog;
import com.globalways.csscli.ui.BaseFragmentActivity;
import com.globalways.csscli.ui.UITools;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 商铺商品管理
 * 
 * @author james
 *
 */
public class ProductActivity extends BaseFragmentActivity implements OnClickListener, OnRefreshListener<ListView>,
		OnItemClickListener {
	private static final String TAG = ProductActivity.class.getSimpleName();

	private static final int CODE_SCAN_REQUEST = 11;

	private TextView textCenter;
	private ImageButton imgBtnLeft, imgBtnRight, imgBtnRight1;

	private PullToRefreshListView refreshListView;
	private ListView listView;
	private ProductListAdapter productListAdapter;
	private ProductDetailFragment productDetailFragment;
	private View layoutContainer;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_split_screen);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();

		// 初始化完成后加载StoreList数据
		reloadData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnLeft:
			finish();
			break;
		case R.id.imgBtnRight:
			UITools.jumpProductAddNewActivity(this, ProductAddNewActivity.ScanStep.INFO_FIRST,
					ProductAddNewActivity.ScanProductExist.NOT_EXIST, null);
			break;
		case R.id.imgBtnRight1:
			UITools.jumpProductScanCodeActivity(this, CODE_SCAN_REQUEST,
					ProductScanCodeActivity.OperationType.SCAN_PRODUCT);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CODE_SCAN_REQUEST:
				break;
			}
		}
	}

	public void reloadData() {
		refreshListView.setRefreshing();
		loadProductList(true);
	}

	/** PullToRefreshListView的下拉或上拉监听接口 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadProductList(refreshListView.isHeaderShown());
	}

	/** 如果单击Product列表中的单项，就将该position的实体传递给detail类，detail类刷新界面 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MyLog.d(TAG, "ProductList click position: " + position);
		productDetailFragment.setEntity(productListAdapter.getItemByPosition(position - 1));
		productListAdapter.setChooseItem(position - 1);
	}

	/** 加载ProductList数据，isRefresh为true时，刷新；isRefresh为false时，加载更多 */
	private void loadProductList(final boolean isRefresh) {
		ProductManager.getInstance().getProductList(MyApplication.getStoreid(), productListAdapter.getPage(isRefresh),
				new ManagerCallBack<List<ProductEntity>>() {
					@Override
					public void onSuccess(List<ProductEntity> returnContent) {
						super.onSuccess(returnContent);
						productListAdapter.setData(isRefresh, returnContent);
						layoutContainer.setVisibility(View.VISIBLE);
						productDetailFragment.setEntity(productListAdapter.getItemByPosition(0));
						refreshListView.onRefreshComplete();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						Toast.makeText(ProductActivity.this, msg, Toast.LENGTH_SHORT).show();
						refreshListView.onRefreshComplete();
					}
				});
	}

	/** 初始化UI、设置监听 */
	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setVisibility(View.VISIBLE);
		imgBtnLeft.setOnClickListener(this);

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("商铺商品管理");

		imgBtnRight = (ImageButton) findViewById(R.id.imgBtnRight);
		imgBtnRight.setImageDrawable(getResources().getDrawable(R.drawable.icon_add));
		imgBtnRight.setVisibility(View.VISIBLE);
		imgBtnRight.setOnClickListener(this);
		imgBtnRight1 = (ImageButton) findViewById(R.id.imgBtnRight1);
		imgBtnRight1.setVisibility(View.VISIBLE);
		imgBtnRight1.setOnClickListener(this);

		refreshListView = (PullToRefreshListView) findViewById(R.id.refreshListView);
		refreshListView.setOnRefreshListener(this);
		refreshListView.setMode(Mode.BOTH);
		listView = refreshListView.getRefreshableView();
		productListAdapter = new ProductListAdapter(this);
		listView.setAdapter(productListAdapter);
		listView.setOnItemClickListener(this);

		layoutContainer = findViewById(R.id.layoutContainer);
		layoutContainer.setVisibility(View.INVISIBLE);

		productDetailFragment = new ProductDetailFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.layoutContainer, productDetailFragment)
				.show(productDetailFragment).commit();
	}

}
