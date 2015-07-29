package com.globalways.csscli.ui.product;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductCategoryEntity;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.BaseActivity;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.ui.product.ProductDetailFragment.ProductCategorySelectionAdapter;

public class ProductSelectionActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	public static final int SELECT_RPODUCT_REQUEST = 2;
	public static final String DATA = "data";
	
	private TextView textCenter;
	private ImageButton imgBtnLeft;
	
	//category
	private Button btnLevelUp, btnNoCategory;
	private TextView tvCurrentCategory, tvCurrentLevel;
	private ListView categoryList;
	private CategorySelectionAdapter mCategorySelectionAdapter;
	private OnItemClickListener categoryClick;
	
	private ProductCategoryEntity currentParent = new ProductCategoryEntity(-1);
	private int currentLevel = 1;
	
	//product list
	private ListView lvProductList;
	private ProductListAdapter mProductListAdapter;
	
	//selected product
	private TextView tvProductName, tvProductPrice, tvProductType;
	private View viewSelectedProduct;
	private Button btnConfirmSelection;
	private ProductEntity currentSelectedProduct;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_selection_activity);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnLeft:
			finish();
			break;
		case R.id.btnConfirmSelection:
			Intent intent = new Intent();
			intent.putExtra(DATA, currentSelectedProduct);
			setResult(FragmentActivity.RESULT_OK, intent);
			finish();
			break;
		case R.id.btnNoCategory:
			loadCategoryProuducts(-1);
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		currentSelectedProduct = (ProductEntity)mProductListAdapter.getItem(position);
		
		
		viewSelectedProduct.setVisibility(View.VISIBLE);
		tvProductName.setText(currentSelectedProduct.getProduct_name());
		tvProductPrice.setText(Tool.fenToYuan(currentSelectedProduct.getProduct_retail_price()));
		tvProductType.setText(ProductType.codeOf(currentSelectedProduct.getProduct_type()).toString());
		
//		this.finish();
	}
	
	/** 初始化UI、设置监听 */
	private void initView() {
		
		//title bar
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setOnClickListener(this);
		imgBtnLeft.setVisibility(View.VISIBLE);
		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("商品选择");
		initCategoryView();
		
		lvProductList = (ListView) findViewById(R.id.lvProductsList);
		mProductListAdapter = new ProductListAdapter(this);
		lvProductList.setAdapter(mProductListAdapter);
		lvProductList.setOnItemClickListener(this);
		
		viewSelectedProduct = findViewById(R.id.viewSelectedProduct);
		tvProductName = (TextView) findViewById(R.id.tvProductName);
		tvProductPrice = (TextView) findViewById(R.id.tvProductPrice);
		tvProductType = (TextView) findViewById(R.id.tvProductType);
		btnConfirmSelection = (Button) findViewById(R.id.btnConfirmSelection);
		btnConfirmSelection.setOnClickListener(this);
		
	}
	
	
	private void initCategoryView(){
		btnNoCategory = (Button) findViewById(R.id.btnNoCategory);
		btnNoCategory.setOnClickListener(this);
		btnLevelUp = (Button) findViewById(R.id.btnLevelUp);
		tvCurrentCategory = (TextView) findViewById(R.id.tvCurrentCategory);
		tvCurrentLevel = (TextView) findViewById(R.id.tvCurrentLevel);
		categoryList = (ListView) findViewById(R.id.lvCategoryList);
		mCategorySelectionAdapter = new CategorySelectionAdapter(this);
		categoryList.setAdapter(mCategorySelectionAdapter);
		
		categoryClick = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				currentParent = (ProductCategoryEntity) mCategorySelectionAdapter.getItem(position);
				loadCategoryChildren(currentParent.getId());
				currentLevel++;
				tvCurrentCategory.setText(currentParent.getName());
				tvCurrentLevel.setText(currentLevel+" 级分类");
				if(currentLevel == 3){
					categoryList.setOnItemClickListener(null);
				}
				if(currentLevel > 1){
					btnLevelUp.setVisibility(View.VISIBLE);
				}
				
			}
		};
		categoryList.setOnItemClickListener(categoryClick);
		
		btnLevelUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//当前显示为一级分类,不能返回到上一级
				if(currentParent.getId() == -1){
					return;
				}
				currentParent = mCategorySelectionAdapter.getEntityById(currentParent.getParent_id());
				currentLevel--;
				loadCategoryChildren(currentParent.getId());
				tvCurrentCategory.setText(currentParent.getName());
				tvCurrentLevel.setText(currentLevel+" 级分类");
				if(currentLevel == 1){
					btnLevelUp.setVisibility(View.INVISIBLE);
				}
				if(currentLevel < 3){
					categoryList.setOnItemClickListener(categoryClick);
				}
			}
		});
		tvCurrentCategory.setText(currentParent.getName());
		tvCurrentLevel.setText(currentLevel+" 级分类");
		loadCategoryChildren(-1);
	}
     /**
	  * 分类列表adapter 
	  * @author wyp E-mail:onebyte@qq.com
	  * @version Time: 2015年7月28日 下午10:48:59
	  */
	 private class CategorySelectionAdapter extends ProductCategoryAdapter {

		public CategorySelectionAdapter(Context context) {
			super(context);
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.product_category_selection_list_item, parent,false);
			}
			((TextView)convertView.findViewById(R.id.tvCategoryName)).setText(list.get(position).getName());
			((Button)convertView.findViewById(R.id.btnSelect)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					loadCategoryProuducts(list.get(position).getId());
//					tvProductCategory.setText(list.get(position).getName());
//					tvProductCategoryID.setText(String.valueOf(list.get(position).getId()));
					currentLevel = 1;
				}
			});
			return convertView;
		}
	}
	
	
	/**
	 * 加载指定分类的子分类
	 * @param whose
	 */
	private void loadCategoryChildren(int whose){
		ProductManager.getInstance().loadCategoryChildren(whose, new ManagerCallBack<List<ProductCategoryEntity>>() {

			@Override
			public void onSuccess(List<ProductCategoryEntity> returnContent) {
				super.onSuccess(returnContent);
				mCategorySelectionAdapter.setList(returnContent);
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				mCategorySelectionAdapter.setList(null);
				UITools.ToastMsg(ProductSelectionActivity.this, msg);
			}
			
		});
	}
	
	/**
	 * 加载分类下商品列表
	 * @param cid
	 */
	private void loadCategoryProuducts(int cid){
		ProductManager.getInstance().loadCategoryProducts(cid, new ManagerCallBack<List<ProductEntity>>() {
			@Override
			public void onSuccess(List<ProductEntity> returnContent) {
				super.onSuccess(returnContent);
				mProductListAdapter.setData(true, returnContent);
			}
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(ProductSelectionActivity.this, msg);
			}
		});
	}
	
}
