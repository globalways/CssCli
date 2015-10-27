package com.globalways.cvsb.ui.product;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.bean.Image;

import com.alertdialogpro.AlertDialogPro;
import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.ProductCategoryEntity;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.entity.ProductLabEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.ManagerCallBack2;
import com.globalways.cvsb.http.manager.ProductManager;
import com.globalways.cvsb.tools.InputVali;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.QRCodeTools;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.BaseActivity;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.ui.gallery.GalleryActivity;
import com.globalways.cvsb.ui.gallery.GalleryPicEntity;
import com.globalways.cvsb.ui.gallery.GalleryPicPreviewActivity;
import com.globalways.cvsb.view.BottomMenuDialog;
import com.globalways.cvsb.view.ClearableEditText;
import com.globalways.cvsb.view.MenuItemEntity;
import com.globalways.cvsb.view.MyDialogManager;
import com.globalways.cvsb.view.SimpleProgressDialog;
import com.globalways.cvsb.view.TextWatcherDelay;

/**
 * 添加商品弹出层
 * 
 * @author James
 *
 */
public class ProductAddNewActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

	/** 跳转相册选择照片的requestCode */
	private static final int CODE_SELECT_IMAGE = 11;
	private static final int CODE_SCAN_BAR_REQUEST = 12;
	public static final String KEY_FIRST_STEP = "firstStep";
	public static final String KEY_PRODUCT_CODE = "productCode";
	public static final String KEY_PRODUCT_EXIST = "productExist";
	private ProductType productType = ProductType.DANTI;
	private int productIsExist = ScanProductExist.EXIST;

	public class ScanProductExist {
		/** 扫描的商品Code是否已经入库 */
		public static final int EXIST = 22;
		public static final int NOT_EXIST = 23;
	}

	private String productCode;
	private String productQr;

	public class ScanStep {
		/** 先扫码 */
		public static final int SCAN_FIRST = 1;
		/** 先填信息 */
		public static final int INFO_FIRST = 2;
	}

	private TextView textCenter, editProductType, tvProductCategory, tvProductCategoryID;
	private ImageButton imgBtnLeft, imgBtnRight;
	private ClearableEditText editName, editBrand, editPrice, editOriginalPrice, editRetailApr, editUnit, editApr, editTag, editCode, editStock,
			editStockLimit, editDesc;
	private CheckBox checkBoxRecommend, checkBoxLock;
	private ImageView imageScanBarCode;
	private ListView lvSearchResult;
	private ProuductLabSearchAdapter mSearchAdapter;
	private PopupWindow mPopupWindow;

	private GridView gridViewPic, gridViewWebPic;
	private ProductSelectPicAdapter picAdapter;
	private ProductPicAdapter picWebAdapter;
	private ArrayList<GalleryPicEntity> selectedImageList;
	private ArrayList<String> selectImagePathArray;
	
	private TextWatcher retailAprWatcher, priceWatcher;

	// 分类dialog
	private ProductCategorySelectionAdapter mProductCategorySelectionAdapter;
	private ProductCategoryEntity currentParent = new ProductCategoryEntity(-1);
	private TextView tvCurrentCategory, tvCurrentLevel;
	private Button btnLevelUp;
	private int currentLevel = 1;
	private ListView categoryList;
	private AdapterView.OnItemClickListener categoryClick;
	private AlertDialogPro categoriesDialog;
	
	//菜单dialog
	private BottomMenuDialog<Integer> mBottomMenuDialog;
	private List<MenuItemEntity> menuList;
	
	//价格处更新view
	private Handler handler;
	public static final int PRICE_INPUT_OK = 1;
	public static final int APR_INPUT_OK = 2;
	public static final String MSG_DATA = "msg_data";
	public static final long INPUT_DELAY = 600;

	// private Spinner spinnerPurchase;
	/** 进度条 **/
	private SimpleProgressDialog mSimpleProgressDialog;
	private AlertDialog mProgressDialog;
	private AlertDialogPro.Builder mDialogBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_addnew_fragment);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
		initData();
	}

	private void initData() {
		if (getIntent().getIntExtra(KEY_FIRST_STEP, ScanStep.INFO_FIRST) == ScanStep.SCAN_FIRST) {
			productCode = getIntent().getStringExtra(KEY_PRODUCT_CODE);
			productIsExist = getIntent().getIntExtra(KEY_PRODUCT_EXIST, ScanProductExist.EXIST);
			if (productIsExist == ScanProductExist.EXIST) {
				textCenter.setText("修改商品信息");
				loadProductDetail(productCode);
			} else {
				findViewById(R.id.viewWebPic).setVisibility(View.GONE);
			}
			if (productCode != null) {
				editCode.setText(productCode);
			}
		} else {
			findViewById(R.id.viewWebPic).setVisibility(View.GONE);
		}
		
		handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case PRICE_INPUT_OK:
					priceChangedAction(msg.getData().getCharSequence(MSG_DATA).toString());
					break;
				case APR_INPUT_OK:
					aprChangedAction(msg.getData().getCharSequence(MSG_DATA).toString());
					break;

				default:
					break;
				}
			};
		};
		
		priceWatcher = new TextWatcher() {
			private String input_data;
			private long after_input;
			private Thread t;
			private Runnable input_action = new Runnable() {
				public void run() {
					Looper.prepare();
					while(true){
						if((System.currentTimeMillis() - after_input) > INPUT_DELAY){
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putCharSequence(MSG_DATA, input_data);
							msg.setData(data);
							msg.what = PRICE_INPUT_OK;
							handler.sendMessage(msg);
							t = null;
							Looper.myLooper().quit();
							break;
						}
					}
					Looper.loop();
				}
			};
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {input_data = s.toString();}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				after_input = System.currentTimeMillis();
				if(t == null){
					t = new Thread(input_action);
					t.start();
				}
			}
		};
		
		retailAprWatcher = new TextWatcher() {
			private String input_data;
			private long after_input;
			private Thread t;
			private Runnable input_action = new Runnable() {
				public void run() {
					Looper.prepare();
					while(true){
						if((System.currentTimeMillis() - after_input) > INPUT_DELAY){
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putCharSequence(MSG_DATA, input_data);
							msg.setData(data);
							msg.what = APR_INPUT_OK;
							handler.sendMessage(msg);
							t = null;
							Looper.myLooper().quit();
							break;
						}
					}
					Looper.loop();
				}
			};
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {input_data = s.toString();}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				after_input = System.currentTimeMillis();
				if(t == null){
					t = new Thread(input_action);
					t.start();
				}
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case CODE_SELECT_IMAGE:
				// 获取选择到的照片
				selectImagePathArray = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				selectedImageList = new ArrayList<GalleryPicEntity>();
				selectedImageList.add(new GalleryPicEntity());
				for(String str : selectImagePathArray){
					GalleryPicEntity e = new GalleryPicEntity();
					e.imagePath = str;
					selectedImageList.add(e);
				}
//				selectedImageList = (ArrayList<GalleryPicEntity>) data
//						.getSerializableExtra(GalleryActivity.KEY_SELECTED_IMAGE);
				picAdapter.setData(selectedImageList);
				break;
			case CODE_SCAN_BAR_REQUEST:
				if (new QRCodeTools().isBarCode(data.getStringExtra(ProductScanCodeActivity.KEY_SCAN_RESULT))) {
					productCode = data.getStringExtra(ProductScanCodeActivity.KEY_SCAN_RESULT);
					editCode.setText(productCode);
				} else {
					Toast.makeText(this, "Scan bar code error", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	}

	/**
	 * 扫码成功后调用这个方法，获取商品的详细信息
	 * 
	 * @param type
	 *            二维码还是条形码
	 * @param productCode
	 *            二维码或者条形码的内容
	 */
	private void loadProductDetail(String productCode) {
		if (productCode == null) {
			return;
		}
		/*mSimpleProgressDialog.setText("正在加载数据……");
		mSimpleProgressDialog.showDialog();*/
		mProgressDialog.setMessage("正在加载数据");
		mProgressDialog.show();
		ProductManager.getInstance().getProductDetail(MyApplication.getStoreid(), productCode,
				new ManagerCallBack<ProductEntity>() {
					@Override
					public void onSuccess(ProductEntity returnContent) {
						super.onSuccess(returnContent);
						refreshView(returnContent);
						/*mSimpleProgressDialog.cancleDialog();*/
						mProgressDialog.dismiss();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						Toast.makeText(ProductAddNewActivity.this, msg, Toast.LENGTH_SHORT).show();
						/*mSimpleProgressDialog.cancleDialog();*/
						mProgressDialog.dismiss();
					}
				});
	}

	private void refreshView(ProductEntity entity) {
		editName.setText(entity.getProduct_name());
		editBrand.setText(entity.getProduct_brand());
		editOriginalPrice.setText(Tool.fenToYuan(entity.getProduct_original_price()));
		editPrice.setText(Tool.fenToYuan(entity.getProduct_retail_price()));
		editRetailApr.setText(String.valueOf(entity.getProduct_retail_apr()));
		getCategory(entity.getProduct_category());
		editUnit.setText(entity.getProduct_unit());
		editApr.setText(entity.getProduct_apr() + "");
		editTag.setText(entity.getProduct_tag());
		editStock.setText(entity.getStock_cnt() + "");
		editStockLimit.setText(entity.getStock_limit() + "");
		editDesc.setText(entity.getProduct_desc());

		editCode.setText(entity.getProduct_bar());
		editCode.setEnabled(false);
		imageScanBarCode.setEnabled(false);
		productQr = entity.getProduct_qr();

		checkBoxRecommend.setChecked(entity.getStatus() == 1);
		checkBoxLock.setChecked(entity.getIs_recommend() == 1);
		picWebAdapter.setData(entity.getProduct_avatar());
	}
	
	/**
	 * 根据分类ID获取商品分类信息
	 * @param cid
	 */
	private void getCategory(final int cid){
		ProductManager.getInstance().getCategory(cid, new ManagerCallBack<ProductCategoryEntity>() {

			@Override
			public void onSuccess(ProductCategoryEntity returnContent) {
				super.onSuccess(returnContent);
				tvProductCategory.setText(String.valueOf(returnContent.getName()));
				tvProductCategoryID.setText(String.valueOf(cid));
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnLeft:
			finish();
			break;
		case R.id.imgBtnRight:
			updateOrAdd(findViewById(R.id.viewWebPic).getVisibility() == View.GONE);
			break;
		case R.id.imageScanBarCode:
			UITools.jumpProductScanCodeActivity(this, CODE_SCAN_BAR_REQUEST,
					ProductScanCodeActivity.OperationType.GET_CODE);
			break;
		case R.id.tvProductCategory:
			showCategoriesDialog();
			break;
		case R.id.tvClose:
			if(mPopupWindow.isShowing())
				mPopupWindow.dismiss();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		GalleryPicEntity imageItem = selectedImageList.get(position);
		if (null != imageItem) {
			// 点击添加图片按钮
			if (Tool.isEmpty(imageItem.imagePath)) {
				//modify by wyp
				//使用新的图片选择器 MultiImageSelectorActivity(https://github.com/lovetuzitong/MultiImageSelector)  
				Intent intent = new Intent(this, MultiImageSelectorActivity.class);
				// whether show camera
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
				// max select image amount
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
				// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
				if(selectImagePathArray != null && selectImagePathArray.size()>0){
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, selectImagePathArray);
                }
				startActivityForResult(intent, CODE_SELECT_IMAGE);
				
//				Intent intent = new Intent(this, GalleryActivity.class);
//				intent.putExtra(GalleryActivity.KEY_JUMP_PURPOSE, GalleryActivity.Purpose.MULTI_PIC.getType());
//				Bundle extras = new Bundle();
//				extras.putSerializable(GalleryActivity.KEY_SELECTED_IMAGE, selectedImageList);
//				intent.putExtras(extras);
//				startActivityForResult(intent, CODE_SELECT_IMAGE);
				
				//end
			} else {
				if (null != selectImagePathArray && selectImagePathArray.size() > 0) {
					Intent intent = new Intent(this, GalleryPicPreviewActivity.class);
					Bundle extras = new Bundle();
					extras.putSerializable(GalleryPicPreviewActivity.KEY_SELECTED_IMAGE, selectImagePathArray);
					intent.putExtras(extras);
					intent.putExtra(GalleryPicPreviewActivity.KEY_SELECTED_INDEX, position-1 );
					startActivity(intent);
				}
			}
		}
	}

	/**
	 * 添加或修改商品信息
	 * 
	 * @param isAdd
	 */
	private void updateOrAdd(final boolean isAdd) {
		String product_name = editName.getText().toString().trim();
		if (product_name == null || product_name.isEmpty()) {
			Toast.makeText(this, "请输入商品名", Toast.LENGTH_SHORT).show();
			return;
		}
		String product_brand = editBrand.getText().toString().trim();
// 品牌不是必须
//		if (product_brand == null || product_brand.isEmpty()) {
//			Toast.makeText(this, "请输入商品品牌", Toast.LENGTH_SHORT).show();
//			return;
//		}
		String product_bar = editCode.getText().toString().trim();
		String product_desc = editDesc.getText().toString().trim();

		// 价格
		String str_price = editPrice.getText().toString().trim();
		if (str_price == null || str_price.isEmpty()) {
			UITools.ToastMsg(this, "请输入商品价格");
			return;
		}
		long product_retail_price = Tool.yuanToFen(str_price);
		// add by wyp
		// 原价
		String str_original_price = editOriginalPrice.getText().toString().trim();
		if (str_original_price == null || str_original_price.isEmpty()) {
			UITools.ToastMsg(this, "请输入商品原价");
			return;
		}
		long product_original_price = Tool.yuanToFen(str_original_price);
		
		// 折扣
		String str_retail_apr = editRetailApr.getText().toString().trim();
		if (str_retail_apr == null || str_retail_apr.isEmpty()) {
			UITools.ToastMsg(this, "请输入折扣率");
			return;
		}
		
		//分类
		int product_category_id = -1;
		if(!tvProductCategoryID.getText().toString().isEmpty()){
			product_category_id = Integer.parseInt(tvProductCategoryID.getText().toString());
		}
		// end
		
		// 单位
		String product_unit = editUnit.getText().toString().trim();
		if (product_unit == null || product_unit.isEmpty()) {
			Toast.makeText(this, "请输入商品单位", Toast.LENGTH_SHORT).show();
			return;
		}

		// 库存
		String stock = editStock.getText().toString().trim();
		double stock_cnt = 0;
		if (stock != null && !stock.isEmpty()) {
			stock_cnt = Double.valueOf(stock);
		}
		String product_tag = editTag.getText().toString().trim();
		
		//使用 MyDialogManager
		/*mSimpleProgressDialog.setText("正在上传，请稍后...");
		mSimpleProgressDialog.showDialog();*/
		mProgressDialog.setMessage("正在保存信息，请稍后");
		mProgressDialog.show();
		ProductManager.getInstance().updateOrAdd(isAdd, selectedImageList, isAdd ? null : picWebAdapter.getPicList(),
				product_name, product_brand, isAdd ? null : productQr, product_bar, product_desc, product_retail_price,
						str_retail_apr,product_category_id, product_original_price,productType.getCode(),
				product_unit, stock_cnt, checkBoxRecommend.isChecked(), checkBoxLock.isChecked(), product_tag,
				new ManagerCallBack<String>() {
					@Override
					public void onSuccess(String returnContent) {
						super.onSuccess(returnContent);
						/*mSimpleProgressDialog.cancleDialog();*/
						mProgressDialog.dismiss();
						mDialogBuilder.setMessage(isAdd ? "添加成功！" : "修改成功！");
						mDialogBuilder.setTitle("提示！");
						mDialogBuilder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ProductAddNewActivity.this.finish();
							}
						});
						mDialogBuilder.create().show();
					}
					
					@Override
					public void onProgress(int progress) {
						if(progress == 0){
							mProgressDialog.setMessage("准备上传图片");
						}else{
							mProgressDialog.setMessage("正在上传图片  "+progress+"%");
						}
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						/*mSimpleProgressDialog.cancleDialog();*/
						mProgressDialog.dismiss();
						UITools.ToastMsg(ProductAddNewActivity.this, msg);
					}
				});
	}

	/**
	 * 初始化移除菜单view
	 * 
	 * @param isWeb
	 *            当前调用该方法的是网络图片列表，还是本地图片列表
	 */
	private void initMenuView(final boolean isWeb) {
		if (menuList == null) {
			menuList = new ArrayList<MenuItemEntity>();
			MenuItemEntity menuItemEntity1 = new MenuItemEntity(1, "移除这张照片", MenuItemEntity.Color.ALERT);
			menuList.add(menuItemEntity1);
			mBottomMenuDialog = new BottomMenuDialog<Integer>(this);
			mBottomMenuDialog.setMenuList(menuList);
		}
		mBottomMenuDialog.setOnItemClickListener(new BottomMenuDialog.OnItemClickListener<Integer>() {
			@Override
			public void onItemClick(int menuId, Integer prams) {
				if (isWeb) {
					if (picWebAdapter.getCount() > 0) {
						picWebAdapter.remove(prams);
					}
				} else {
					if (selectedImageList != null && selectedImageList.size() > 0) {
						selectedImageList.remove(prams);
						picAdapter.remove(prams);
						//图片选择 add by wyp
						selectImagePathArray.remove(prams.intValue()-1);
						//end
					}
				}
			}
		});
	}

	/**
	 * 价格变化处理
	 * @param new_price 
	 *    		新的价格
	 */
	private void priceChangedAction(String new_price)
	{
		if(!InputVali.isFloatAsFen(new_price)){
			editRetailApr.setText("");
		}
		else{
			String str_original_price;
			String str_price;
			String str_retail_apr;
			str_original_price = editOriginalPrice.getText().toString();
			if(!InputVali.isFloatAsFen(str_original_price))
			{
				UITools.ToastMsg(ProductAddNewActivity.this, "请正确输入原价");
				return;
			}
			str_price = new_price;
			//商品价格大于或者等于原价
			if(Tool.compare(str_price, str_original_price) >= 0)
			{
				editPrice.setText(String.valueOf(str_original_price));
				editPrice.setSelection(String.valueOf(str_original_price).length());
				str_price = str_original_price;
			}
			str_retail_apr  = Tool.div(str_price, str_original_price, Tool.RETAIL_APR_SCALE);
			editRetailApr.setText(Tool.mul(str_retail_apr, String.valueOf(100)).replace(".00", ""));
		}
		
	}
	/**
	 *  折扣变化
	 * @param new_apr
	 */
	private void aprChangedAction(String new_apr)
	{
		String str_retail_apr;
		String str_original_price;
		str_original_price = editOriginalPrice.getText().toString();
		if(!InputVali.isFloatAsFen(str_original_price)){
			UITools.ToastMsg(ProductAddNewActivity.this, "请正确输入商品原价");
			return;
		}
		if (!InputVali.isLegalApr(new_apr)) {
			UITools.ToastMsg(ProductAddNewActivity.this, "请正确输入折扣率");
		}
		if(new_apr.isEmpty()){
			editRetailApr.setText("");
			editPrice.setText("");
		}else{
			str_retail_apr = Tool.div(new_apr, String.valueOf(100), Tool.RETAIL_APR_SCALE);
			String str_price  = Tool.mul(str_retail_apr, str_original_price);
			editPrice.setText(Tool.formatYuan(str_price));
		}
	}
	
	@SuppressLint("NewApi")
	private void showCategoriesDialog(){
		final AlertDialogPro.Builder builer = MyDialogManager.builder(this);
		View dialogView = LayoutInflater.from(this).inflate(R.layout.product_category_selection_dialog, null);
		currentLevel = 1;
		btnLevelUp = (Button) dialogView.findViewById(R.id.btnLevelUp);
		tvCurrentCategory = (TextView) dialogView.findViewById(R.id.tvCurrentCategory);
		tvCurrentLevel = (TextView) dialogView.findViewById(R.id.tvCurrentLevel);
		categoryList = (ListView) dialogView.findViewById(R.id.lvCategoryList);
		mProductCategorySelectionAdapter = new ProductCategorySelectionAdapter(this);
		categoryList.setAdapter(mProductCategorySelectionAdapter);
		
		categoryClick = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				currentParent = (ProductCategoryEntity) mProductCategorySelectionAdapter.getItem(position);
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
				currentParent = mProductCategorySelectionAdapter.getEntityById(currentParent.getParent_id());
				currentLevel--;
				loadCategoryChildren(currentParent.getId());
				tvCurrentCategory.setText(currentParent.getName());
				builer.setTitle(currentParent.getName());
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
		categoriesDialog = builer.setTitle(currentParent.getName()).
		setView(dialogView).
		setNegativeButton("取消", null).
		create();
		categoriesDialog.show();
		loadCategoryChildren(-1);
	}
	
	/**
	 * 加载指定分类的子分类
	 * @param whose
	 */
	private void loadCategoryChildren(int whose){
		ProductManager.getInstance().loadCategoryChildren(true,whose, new ManagerCallBack<List<ProductCategoryEntity>>() {

			@Override
			public void onSuccess(List<ProductCategoryEntity> returnContent) {
				super.onSuccess(returnContent);
				mProductCategorySelectionAdapter.setList(returnContent);
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				mProductCategorySelectionAdapter.setList(null);
				UITools.ToastMsg(ProductAddNewActivity.this, msg);
			}
			
		});
	}
	
	View popupWindowView;
	/**
	 * 显示平台商品库按条码搜索结果
	 * @param barcode
	 */
	@SuppressLint("InflateParams")
	private void showBarcodeSearchResult(final String barcode){
		if(popupWindowView == null)
			popupWindowView = LayoutInflater.from(this).inflate(R.layout.product_addnew_search_result_pouwindow, null);
		if(lvSearchResult == null)
			lvSearchResult = (ListView) popupWindowView.findViewById(R.id.lvSearchResult);
		TextView tvClose = (TextView) popupWindowView.findViewById(R.id.tvClose);
		tvClose.setOnClickListener(this);
		lvSearchResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final ProductLabEntity e = (ProductLabEntity) mSearchAdapter.getItem(position);
				mDialogBuilder.setTitle("提示！").setMessage("将使用云端信息【"+e.getProduct_name()+"】填充商品信息，稍后可以自行修改")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						editCode.setText(e.getProduct_bar());
						editCode.setSelection(e.getProduct_bar().length());
						editName.setText(e.getProduct_name());
						editBrand.setText(e.getProduct_brand());
						editOriginalPrice.setText(e.getProduct_price()==0?"":Tool.fenToYuan(e.getProduct_price()));
						editUnit.setText(e.getProduct_unit());
						editDesc.setText(Tool.isEmpty(e.getProduct_loc())?"":"产地:"+e.getProduct_loc());
						editDesc.append(Tool.isEmpty(e.getProduct_spec())?"":"规格："+e.getProduct_spec());
						mPopupWindow.dismiss();
					}
				}).setNegativeButton("取消", null).create().show();
			
			}
		});
		
		if(mSearchAdapter == null)
			mSearchAdapter = new ProuductLabSearchAdapter(this);
		lvSearchResult.setAdapter(mSearchAdapter);
		if(mPopupWindow == null){
			mPopupWindow = new PopupWindow(popupWindowView);
			mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);
			mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		}
		if(barcode == null || barcode.isEmpty()){
			mSearchAdapter.setData(null,null);
			if(mPopupWindow.isShowing())
				mPopupWindow.dismiss();
		}else{
			ProductManager.getInstance().searchByBarcode(barcode, new ManagerCallBack2<List<ProductLabEntity>, Integer>() {
				@Override
				public void onSuccess(List<ProductLabEntity> returnContent, Integer params) {
					super.onSuccess(returnContent, params);
					//如果没有内容或者是上一次商品内容不显示popupwindow
					if(returnContent == null || returnContent.size() == 0 || returnContent.size() == 1 && returnContent.get(0).getProduct_name().equals(editName.getText().toString())){
						if(mPopupWindow.isShowing())
							mPopupWindow.dismiss();
					}
					else{
						mSearchAdapter.setData(returnContent,barcode);
						mPopupWindow.showAsDropDown(editCode);
					}
				}
				@Override
				public void onFailure(int code, String msg) {
					super.onFailure(code, msg);
					UITools.ToastMsg(ProductAddNewActivity.this, msg);
					mPopupWindow.dismiss();
				}
			});
		}
		
		
	}
	
	
	public class ProductCategorySelectionAdapter extends ProductCategoryAdapter {

		public ProductCategorySelectionAdapter(Context context) {
			super(context);
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.product_category_selection_list_item, parent,false);
			}
			ProductCategoryEntity c = list.get(position);
			((TextView)convertView.findViewById(R.id.tvCategoryName)).setText(c.getName());
			Button btnSelect = (Button)convertView.findViewById(R.id.btnSelect);
			if(c.getLevel() <3){
				btnSelect.setVisibility(View.GONE);
			}else{
				btnSelect.setVisibility(View.VISIBLE);
				btnSelect.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						tvProductCategory.setText(list.get(position).getName());
						tvProductCategoryID.setText(String.valueOf(list.get(position).getId()));
						categoriesDialog.dismiss();
						currentLevel = 1;
					}
				});
			}
			return convertView;
		}
	}
	
	
	@SuppressLint("HandlerLeak")
	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setVisibility(View.VISIBLE);
		imgBtnLeft.setOnClickListener(this);

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("添加新商品");

		imgBtnRight = (ImageButton) findViewById(R.id.imgBtnRight);
		imgBtnRight.setVisibility(View.VISIBLE);
		imgBtnRight.setOnClickListener(this);

		editName = (ClearableEditText) findViewById(R.id.editName);
		editBrand = (ClearableEditText) findViewById(R.id.editBrand);
		
		tvProductCategory = (TextView) findViewById(R.id.tvProductCategory);
		tvProductCategory.setOnClickListener(this);
		tvProductCategoryID = (TextView) findViewById(R.id.tvProductCategoryID);
		
		editProductType = (TextView) findViewById(R.id.editProductType);
		editProductType.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Builder builder = MyDialogManager.builder(ProductAddNewActivity.this);
				builder.setItems(new String[]{"单体型","称重型"},new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							productType = ProductType.DANTI;
							editProductType.setText(productType.toString());
							break;
						case 1:
							productType = ProductType.CHENGZHONG;
							editProductType.setText(productType.toString());
							break;
						default:
							break;
						}
					}
				}).setTitle("选择商品类型").create().show();
				
				
			}
		});
		
		
		editPrice = (ClearableEditText) findViewById(R.id.editPrice);
		editPrice.setSubfix("元");
		editPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					editPrice.addTextChangedListener(priceWatcher);
					editRetailApr.removeTextChangedListener(retailAprWatcher);
				}else{
					editPrice.removeTextChangedListener(priceWatcher);
				}
			}
		});
		
		
//		UITools.editNumLimit(editPrice);
		
		
		editOriginalPrice = (ClearableEditText) findViewById(R.id.editOriginalPrice);
		editOriginalPrice.setSubfix("元");
		editOriginalPrice.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				editPrice.clearFocus();
				editRetailApr.clearFocus();
				editPrice.setText("");
				editRetailApr.setText("");
				
				editRetailApr.removeTextChangedListener(retailAprWatcher);
				editPrice.removeTextChangedListener(priceWatcher);
			}
		});
		
		editRetailApr = (ClearableEditText) findViewById(R.id.editRetailApr);
		editRetailApr.setSubfix("%");
		editRetailApr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					editRetailApr.addTextChangedListener(retailAprWatcher);
					editPrice.removeTextChangedListener(priceWatcher);
				}else{
					editRetailApr.removeTextChangedListener(retailAprWatcher);
				}
			}
		});
		
		
		editUnit = (ClearableEditText) findViewById(R.id.editUnit);
		editApr = (ClearableEditText) findViewById(R.id.editApr);
		editTag = (ClearableEditText) findViewById(R.id.editTag);
		
		//条码
		editCode = (ClearableEditText) findViewById(R.id.editCode);
		editCode.addTextChangedListener(new TextWatcherDelay(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				String barcode = msg.getData().getString(TextWatcherDelay.DATA);
				showBarcodeSearchResult(barcode);
			}
		}));
		
		editStock = (ClearableEditText) findViewById(R.id.editStock);
		UITools.editNumDoubleLimit(editStock);
		editStockLimit = (ClearableEditText) findViewById(R.id.editStockLimit);
		editDesc = (ClearableEditText) findViewById(R.id.editDesc);

		checkBoxRecommend = (CheckBox) findViewById(R.id.checkBoxRecommend);
		checkBoxLock = (CheckBox) findViewById(R.id.checkBoxLock);

		imageScanBarCode = (ImageView) findViewById(R.id.imageScanBarCode);
		imageScanBarCode.setOnClickListener(this);

		// spinnerPurchase = (Spinner) findViewById(R.id.spinnerPurchase);

		gridViewWebPic = (GridView) findViewById(R.id.gridViewWebPic);
		picWebAdapter = new ProductPicAdapter(this);
		gridViewWebPic.setAdapter(picWebAdapter);
		gridViewWebPic.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				initMenuView(true);
				mBottomMenuDialog.showDialog(position);
				return true;
			}
		});

		gridViewPic = (GridView) findViewById(R.id.gridViewPic);
		picAdapter = new ProductSelectPicAdapter(this);
		gridViewPic.setAdapter(picAdapter);
		gridViewPic.setOnItemClickListener(this);
		gridViewPic.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (0 != position) {
					initMenuView(false);
					mBottomMenuDialog.showDialog(position);
					return true;
				}
				return false;
			}
		});
		selectedImageList = new ArrayList<GalleryPicEntity>();
		selectedImageList.add(new GalleryPicEntity());
		picAdapter.setData(selectedImageList);

		/*mSimpleProgressDialog = new SimpleProgressDialog(this, true);
		mSimpleProgressDialog.setText("正在为您创建商品....");*/
		mProgressDialog = MyDialogManager.buildProgress(this);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mDialogBuilder = MyDialogManager.builder(this);
	}
}
