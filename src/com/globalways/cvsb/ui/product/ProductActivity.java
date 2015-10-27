package com.globalways.cvsb.ui.product;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.ProductManager;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.MyLog;
import com.globalways.cvsb.ui.BaseFragmentActivity;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.view.IndexItemInterface;
import com.globalways.cvsb.view.IndexableListViewImpl;
import com.globalways.cvsb.view.TextWatcherDelay;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

/**
 * 商铺商品管理
 * 
 * @author james
 *
 */
public class ProductActivity extends BaseFragmentActivity
		implements OnClickListener, OnRefreshListener<ListView>, OnItemClickListener {
	private static final String TAG = ProductActivity.class.getSimpleName();

	private static final int CODE_SCAN_REQUEST = 11;

	private TextView textCenter;
	private ImageButton imgBtnLeft, imgBtnRight, imgBtnRight1;
	//禁用pulltorefreshlistview
//	private PullToRefreshListView refreshListView;
//	private ListView listView;
//	private ProductListAdapter productListAdapter;
	private ProductDetailFragment productDetailFragment;
	private ProductCategoryManagerFragment productCategoryManagerFragment;
	private View layoutContainer;

	//带索引列表
	private IndexableProductListAdapter mIndexableProductListAdapter;
	private IndexableListViewImpl indexableListView;
	private EditText searchBox;
	private Object searchLock = new Object();
	boolean inSearchMode = false;
	private List<IndexItemInterface> allProudcts;
	private List<IndexItemInterface> filterList = new ArrayList<IndexItemInterface>();
	private SearchListTask curSearchTask = null;
	private int currentItemPostion = 0;
	private IndexItemInterface currentItem;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_split_screen);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
			showTitileMenu();
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
//		refreshListView.setRefreshing();
		loadProductList(true);
	}

	/** PullToRefreshListView的下拉或上拉监听接口 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//		loadProductList(refreshListView.isHeaderShown());
	}

	/** 如果单击Product列表中的单项，就将该position的实体传递给detail类，detail类刷新界面 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MyLog.d(TAG, "ProductList click position: " + position);
//		productDetailFragment.setEntity(productListAdapter.getItemByPosition(position - 1));
//		productListAdapter.setChooseItem(position - 1);
//		view.setSelected(true);
		List<IndexItemInterface> searchList = inSearchMode ? filterList : allProudcts;
		productDetailFragment.setEntity((ProductEntity)searchList.get(position));
		showDetailFragment();
		currentItemPostion = position;
		currentItem = mIndexableProductListAdapter.getItem(position);
	}

	/** 
	 * 禁用PullToRefreshListView后变更为加载所有商品
	 */
	//加载ProductList数据，isRefresh为true时，刷新；isRefresh为false时，加载更多
	private void loadProductList(final boolean isRefresh) {
		ProductManager.getInstance().loadAllProductList(MyApplication.getStoreid()/*productListAdapter.getPage(isRefresh)*/,
				new ManagerCallBack<List<ProductEntity>>() {
					@Override
					public void onSuccess(List<ProductEntity> returnContent) {
						super.onSuccess(returnContent);
//						productListAdapter.setData(isRefresh, returnContent);
//						if (isRefresh)
//							productListAdapter.setChooseItem(0);
						layoutContainer.setVisibility(View.VISIBLE);
//						productDetailFragment.setEntity(productListAdapter.getItemByPosition(0));
//						refreshListView.onRefreshComplete();
						
						// indexable listview
						allProudcts = new ArrayList<IndexItemInterface>();
						for (ProductEntity e : returnContent) {
							allProudcts.add(e);
						}
						mIndexableProductListAdapter = new IndexableProductListAdapter(ProductActivity.this, R.layout.product_list_item,
								allProudcts);
						productDetailFragment.setEntity((ProductEntity)allProudcts.get(0));
						indexableListView.setFastScrollEnabled(true);
						indexableListView.setAdapter(mIndexableProductListAdapter);
						
						currentItem = mIndexableProductListAdapter.getItem(0);
						indexableListView.setItemChecked(0, true);
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						UITools.ToastMsg(ProductActivity.this, msg);
//						refreshListView.onRefreshComplete();
					}
				});
	}

	/** 初始化UI、设置监听 */
	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setVisibility(View.VISIBLE);
		imgBtnLeft.setOnClickListener(this);

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("商品管理");

		imgBtnRight = (ImageButton) findViewById(R.id.imgBtnRight);
		imgBtnRight.setImageDrawable(getResources().getDrawable(R.drawable.icon_list));
		imgBtnRight.setVisibility(View.VISIBLE);
		imgBtnRight.setOnClickListener(this);
		imgBtnRight1 = (ImageButton) findViewById(R.id.imgBtnRight1);
		imgBtnRight1.setVisibility(View.VISIBLE);
		imgBtnRight1.setOnClickListener(this);

//		refreshListView = (PullToRefreshListView) findViewById(R.id.refreshListView);
//		refreshListView.setOnRefreshListener(this);
//		refreshListView.setMode(Mode.BOTH);
//		listView = refreshListView.getRefreshableView();
//		productListAdapter = new ProductListAdapter(this);
//		listView.setAdapter(productListAdapter);
//		listView.setOnItemClickListener(this);

		// index listview test
		indexableListView = (IndexableListViewImpl) findViewById(R.id.listview);
		indexableListView.setOnItemClickListener(this);
		searchBox = (EditText) findViewById(R.id.input_search_query);
		searchBox.addTextChangedListener(new TextWatcherDelay(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String searchString = searchBox.getText().toString().trim().toUpperCase();
				if (curSearchTask != null && curSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
					try {
						curSearchTask.cancel(true);
					} catch (Exception e) {
						Log.i(TAG, "Fail to cancel running search task");
					}

				}
				curSearchTask = new SearchListTask();
				curSearchTask.execute(searchString);
			}
		}));

		layoutContainer = findViewById(R.id.layoutContainer);
		layoutContainer.setVisibility(View.INVISIBLE);

		showDetailFragment();
		// productDetailFragment = new ProductDetailFragment();
		// getSupportFragmentManager().beginTransaction().add(R.id.layoutContainer,
		// productDetailFragment)
		// .show(productDetailFragment).commit();
	}

	/**
	 * 商品列表搜索
	 * @author wyp
	 *
	 */
	private class SearchListTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			filterList.clear();
			String keyword = params[0];
			inSearchMode = (keyword.length() > 0);
			if (inSearchMode) {
				// get all the items matching this
				for (IndexItemInterface item : allProudcts) {
					boolean isPinyin = item.getItemForIndex().toUpperCase().indexOf(keyword) > -1;
					boolean isChinese = item.getDisplayInfo().indexOf(keyword) > -1;
					if (isPinyin || isChinese) { filterList.add(item);}
				}
			}
			return null;
		}
		protected void onPostExecute(String result) {
			synchronized (searchLock) {
				reList();
			}

		}
	}

	/**
	 * 刷新list
	 */
	public void reList(){
		if (inSearchMode) {
			mIndexableProductListAdapter = new IndexableProductListAdapter(ProductActivity.this, R.layout.product_list_item, filterList);
			mIndexableProductListAdapter.setInSearchMode(true);
			indexableListView.setInSearchMode(true);
			indexableListView.setAdapter(mIndexableProductListAdapter);
		} else {
			mIndexableProductListAdapter = new IndexableProductListAdapter(ProductActivity.this, R.layout.product_list_item,allProudcts);
			mIndexableProductListAdapter.setInSearchMode(false);
			indexableListView.setInSearchMode(false);
			indexableListView.setAdapter(mIndexableProductListAdapter);
		}
		//当商品名称修改后，重新获取商品位置
		currentItemPostion = mIndexableProductListAdapter.getPosition(currentItem);
		indexableListView.setItemChecked(currentItemPostion, true);
		indexableListView.post(new Runnable() {
			@Override
			public void run() {
				indexableListView.setSelection(currentItemPostion);
			}
		});
	}
	
	private void showDetailFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (productDetailFragment == null) {
			productDetailFragment = new ProductDetailFragment();
			ft.add(R.id.layoutContainer, productDetailFragment);
		}
		if (productCategoryManagerFragment != null) {
			ft.hide(productCategoryManagerFragment);
		}
		ft.show(productDetailFragment).commit();
	}

	private void showCategoryFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (null == productCategoryManagerFragment) {
			productCategoryManagerFragment = new ProductCategoryManagerFragment();
			ft.add(R.id.layoutContainer, productCategoryManagerFragment);
		}
		ft.hide(productDetailFragment);
		ft.show(productCategoryManagerFragment).commit();
		// 不选择任何商品
//		productListAdapter.setChooseItem(-1);

	}

	@SuppressLint("NewApi")
	private void showTitileMenu() {
		PopupMenu popupMenu = new PopupMenu(this, imgBtnRight);
		popupMenu.inflate(R.menu.product_title_menu);
		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				switch (arg0.getItemId()) {
				case R.id.menuAddProduct:
					UITools.jumpProductAddNewActivity(ProductActivity.this, ProductAddNewActivity.ScanStep.INFO_FIRST,
							ProductAddNewActivity.ScanProductExist.NOT_EXIST, null);
					break;
				case R.id.menuCategoryManager:
					showCategoryFragment();
					break;
				default:
					return false;
				}
				return true;
			}
		});

		// 使用反射，强制显示菜单图标
		try {
			Field field = popupMenu.getClass().getDeclaredField("mPopup");
			field.setAccessible(true);
			MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
			mHelper.setForceShowIcon(true);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		popupMenu.show();
	}

}
