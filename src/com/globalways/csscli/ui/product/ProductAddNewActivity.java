package com.globalways.csscli.ui.product;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductCategoryEntity;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.tools.InputVali;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.tools.QRCodeTools;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.BaseActivity;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.ui.gallery.GalleryActivity;
import com.globalways.csscli.ui.gallery.GalleryPicEntity;
import com.globalways.csscli.ui.gallery.GalleryPicPreviewActivity;
import com.globalways.csscli.view.BottomMenuDialog;
import com.globalways.csscli.view.ClearableEditText;
import com.globalways.csscli.view.CommonDialogManager;
import com.globalways.csscli.view.MenuItemEntity;
import com.globalways.csscli.view.SimpleProgressDialog;

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

	private TextView textCenter, editProductType, tvProductCategory;
	private ImageButton imgBtnLeft, imgBtnRight;
	private ClearableEditText editName, editBrand, editPrice, editOriginalPrice, editRetailApr, editUnit, editApr, editTag, editCode, editStock,
			editStockLimit, editDesc;
	private CheckBox checkBoxRecommend, checkBoxLock;
	private ImageView imageScanBarCode;

	private GridView gridViewPic, gridViewWebPic;
	private ProductSelectPicAdapter picAdapter;
	private ProductPicAdapter picWebAdapter;
	private ArrayList<GalleryPicEntity> selectedImageList;
	
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
				selectedImageList = (ArrayList<GalleryPicEntity>) data
						.getSerializableExtra(GalleryActivity.KEY_SELECTED_IMAGE);
				selectedImageList.add(new GalleryPicEntity());
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
		mSimpleProgressDialog.setText("正在加载数据……");
		mSimpleProgressDialog.showDialog();
		ProductManager.getInstance().getProductDetail(MyApplication.getStoreid(), productCode,
				new ManagerCallBack<ProductEntity>() {
					@Override
					public void onSuccess(ProductEntity returnContent) {
						super.onSuccess(returnContent);
						refreshView(returnContent);
						mSimpleProgressDialog.cancleDialog();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						Toast.makeText(ProductAddNewActivity.this, msg, Toast.LENGTH_SHORT).show();
						mSimpleProgressDialog.cancleDialog();
					}
				});
	}

	private void refreshView(ProductEntity entity) {
		editName.setText(entity.getProduct_name());
		editBrand.setText(entity.getProduct_brand());
		editPrice.setText(Tool.fenToYuan(entity.getProduct_retail_price()));
		editRetailApr.setText(String.valueOf(entity.getProduct_retail_apr()));
		editOriginalPrice.setText(Tool.fenToYuan(entity.getProduct_original_price()));
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
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		GalleryPicEntity imageItem = selectedImageList.get(position);
		if (null != imageItem) {
			// 点击添加图片按钮
			if (imageItem.imageId <= 0) {
				Intent intent = new Intent(this, GalleryActivity.class);
				intent.putExtra(GalleryActivity.KEY_JUMP_PURPOSE, GalleryActivity.Purpose.MULTI_PIC.getType());
				Bundle extras = new Bundle();
				extras.putSerializable(GalleryActivity.KEY_SELECTED_IMAGE, selectedImageList);
				intent.putExtras(extras);
				startActivityForResult(intent, CODE_SELECT_IMAGE);
			} else {
				if (null != selectedImageList && selectedImageList.size() > 0) {
					ArrayList<String> imagePathList = new ArrayList<String>();
					for (int i = 0; i < selectedImageList.size(); i++) {
						GalleryPicEntity ii = selectedImageList.get(i);
						if (null != ii.imageBucketId && !"".equals(ii.imageBucketId)) {
							imagePathList.add(ii.imagePath);
						}
					}
					Intent intent = new Intent(this, GalleryPicPreviewActivity.class);
					Bundle extras = new Bundle();
					extras.putSerializable(GalleryPicPreviewActivity.KEY_SELECTED_IMAGE, imagePathList);
					intent.putExtras(extras);
					intent.putExtra(GalleryPicPreviewActivity.KEY_SELECTED_INDEX, position);
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
		if (product_brand == null || product_brand.isEmpty()) {
			Toast.makeText(this, "请输入商品品牌", Toast.LENGTH_SHORT).show();
			return;
		}
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
		String product_category = tvProductCategory.getText().toString();
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

		mSimpleProgressDialog.setText("正在上传，请稍后...");
		mSimpleProgressDialog.showDialog();
		ProductManager.getInstance().updateOrAdd(isAdd, selectedImageList, isAdd ? null : picWebAdapter.getPicList(),
				product_name, product_brand, isAdd ? null : productQr, product_bar, product_desc, product_retail_price,
						str_retail_apr,product_category, product_original_price,productType.getCode(),
				product_unit, stock_cnt, checkBoxRecommend.isChecked(), checkBoxLock.isChecked(), product_tag,
				new ManagerCallBack<String>() {
					@Override
					public void onSuccess(String returnContent) {
						super.onSuccess(returnContent);
						mSimpleProgressDialog.cancleDialog();
						AlertDialog.Builder builder = new Builder(ProductAddNewActivity.this);
						builder.setMessage(isAdd ? "添加成功！" : "修改成功！");
						builder.setTitle("提示！");
						builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ProductAddNewActivity.this.finish();
							}
						});
						builder.create().show();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						mSimpleProgressDialog.cancleDialog();
						Toast.makeText(ProductAddNewActivity.this, msg, Toast.LENGTH_SHORT).show();
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
		if(!InputVali.isFloatAsFen(new_price))
		{
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
			UITools.ToastMsg(ProductAddNewActivity.this, "请正确输入原价_apr");
			return;
		}
		if(new_apr.isEmpty())
		{
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
		final AlertDialogPro.Builder builer = CommonDialogManager.createDialogBuilder(this);
		View dialogView = LayoutInflater.from(this).inflate(R.layout.product_category_selection_dialog, null);
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
		ProductManager.getInstance().loadCategoryChildren(whose, new ManagerCallBack<List<ProductCategoryEntity>>() {

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
	
	
	public class ProductCategorySelectionAdapter extends ProductCategoryAdapter {

		public ProductCategorySelectionAdapter(Context context) {
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
					tvProductCategory.setText(list.get(position).getName());
					categoriesDialog.dismiss();
					currentLevel = 1;
				}
			});
			return convertView;
		}
	}
	
	
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
		
		editProductType = (TextView) findViewById(R.id.editProductType);
		editProductType.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Builder builder = CommonDialogManager.createDialogBuilder(ProductAddNewActivity.this);
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
		editPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					editPrice.addTextChangedListener(priceWatcher);
					editRetailApr.removeTextChangedListener(retailAprWatcher);
				}
			}
		});
		
		
//		UITools.editNumLimit(editPrice);
		
		
		editOriginalPrice = (ClearableEditText) findViewById(R.id.editOriginalPrice);
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
				}
			}
		});
		
		
		editUnit = (ClearableEditText) findViewById(R.id.editUnit);
		editApr = (ClearableEditText) findViewById(R.id.editApr);
		editTag = (ClearableEditText) findViewById(R.id.editTag);
		editCode = (ClearableEditText) findViewById(R.id.editCode);
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
				if (picAdapter.getCount() - 1 != position) {
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

		mSimpleProgressDialog = new SimpleProgressDialog(this, true);
		mSimpleProgressDialog.setText("正在为您创建商品....");
	}
}
