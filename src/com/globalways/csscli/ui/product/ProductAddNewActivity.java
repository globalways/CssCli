package com.globalways.csscli.ui.product;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.tools.QRCodeTools;
import com.globalways.csscli.ui.BaseActivity;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.ui.gallery.GalleryActivity;
import com.globalways.csscli.ui.gallery.GalleryPicEntity;
import com.globalways.csscli.ui.gallery.GalleryPicPreviewActivity;
import com.globalways.csscli.view.BottomMenuDialog;
import com.globalways.csscli.view.ClearableEditText;
import com.globalways.csscli.view.MenuItemEntity;
import com.globalways.csscli.view.SimpleProgressDialog;

/**
 * 添加商品弹出层
 * 
 * @author James
 *
 */
public class ProductAddNewActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	/** 跳转相册选择照片的requestCode */
	private static final int CODE_SELECT_IMAGE = 11;
	private static final int CODE_SCAN_BAR_REQUEST = 12;
	public static final String KEY_FIRST_STEP = "firstStep";
	public static final String KEY_PRODUCT_CODE = "productCode";
	public static final String KEY_PRODUCT_EXIST = "productExist";
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

	private TextView textCenter;
	private ImageButton imgBtnLeft, imgBtnRight;
	private ClearableEditText editName, editBrand, editPrice, editUnit, editApr, editTag, editCode, editStock,
			editStockLimit, editDesc;
	private CheckBox checkBoxRecommend, checkBoxLock;
	private ImageView imageScanBarCode;

	private GridView gridViewPic, gridViewWebPic;
	private ProductSelectPicAdapter picAdapter;
	private ProductPicAdapter picWebAdapter;
	private ArrayList<GalleryPicEntity> selectedImageList;

	/** 菜单dialog */
	private BottomMenuDialog<Integer> mBottomMenuDialog;
	private List<MenuItemEntity> menuList;

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
		editPrice.setText(entity.getProduct_price() + "");
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
		String price = editPrice.getText().toString().trim();
		if (price == null || price.isEmpty()) {
			UITools.ToastMsg(this, "请输入商品价格");
			return;
		}
		int product_price = Integer.valueOf(price);

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
				product_name, product_brand, isAdd ? null : productQr, product_bar, product_desc, product_price,
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
		editPrice = (ClearableEditText) findViewById(R.id.editPrice);
		UITools.editNumLimit(editPrice);
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
