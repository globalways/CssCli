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
import android.widget.EditText;
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
import com.globalways.csscli.ui.gallery.GalleryActivity;
import com.globalways.csscli.ui.gallery.GalleryPicEntity;
import com.globalways.csscli.ui.gallery.GalleryPicPreviewActivity;
import com.globalways.csscli.view.BottomMenuDialog;
import com.globalways.csscli.view.MenuItemEntity;
import com.globalways.csscli.view.NoScrollGridView;
import com.globalways.csscli.view.SimpleProgressDialog;

/**
 * 添加商品弹出层
 * 
 * @author James
 *
 */
public class ProductAddNewActivity extends BaseActivity implements OnClickListener, OnItemClickListener,
		OnItemLongClickListener {

	/** 跳转相册选择照片的requestCode */
	private static final int CODE_SELECT_IMAGE = 11;
	private static final int CODE_SCAN_REQUEST = 12;
	private static final int CODE_SCAN_BAR_REQUEST = 13;
	public static final String KEY_FIRST_STEP = "firstStep";
	public static final String KEY_SCAN_RESULT = "scanResult";
	private ScanStep scanStep;
	private static boolean isJumped = false;

	public enum ScanStep {
		/** 先扫码 */
		SCAN_FIRST(1),
		/** 先填信息 */
		INFO_FIRST(2);
		private ScanStep(int step) {
			this.step = step;
		}

		private int step;

		public int getStep() {
			return step;
		}
	}

	private TextView textLeft, textCenter, textRight;
	private EditText editName, editBrand, editPrice, editUnit, editApr, editTag, editCode, editStock, editStockLimit,
			editDesc;
	private CheckBox checkBoxRecommend, checkBoxLock;
	private ImageView imageScanBarCode;

	private NoScrollGridView gridViewPic;
	private ProductSelectPicAdapter picAdapter;
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
		initView();
		initData();
	}

	private void initData() {
		scanStep = getIntent().getIntExtra(KEY_FIRST_STEP, ScanStep.INFO_FIRST.getStep()) == ScanStep.INFO_FIRST
				.getStep() ? ScanStep.INFO_FIRST : ScanStep.SCAN_FIRST;
		if (scanStep == ScanStep.SCAN_FIRST && !isJumped) {
			isJumped = true;
			startActivityForResult(new Intent(this, ProductScanCodeActivity.class), CODE_SCAN_REQUEST);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			isJumped = false;
			switch (requestCode) {
			case CODE_SELECT_IMAGE:
				// 获取选择到的照片
				selectedImageList = (ArrayList<GalleryPicEntity>) data
						.getSerializableExtra(GalleryActivity.KEY_SELECTED_IMAGE);
				selectedImageList.add(new GalleryPicEntity());
				picAdapter.setData(selectedImageList);
				break;
			case CODE_SCAN_REQUEST:
				// 扫描完成后，加载商品信息，如果是条形码，则将结果填充到对应输入框内
				String productCode = data.getStringExtra(KEY_SCAN_RESULT);
				Toast.makeText(this, productCode, Toast.LENGTH_SHORT).show();
				if (new QRCodeTools().isBarCode(productCode)) {
					editCode.setText(data.getStringExtra(KEY_SCAN_RESULT));
				}
				loadProductDetail(productCode);
				break;
			case CODE_SCAN_BAR_REQUEST:
				if (new QRCodeTools().isBarCode(data.getStringExtra(KEY_SCAN_RESULT))) {
					editCode.setText(data.getStringExtra(KEY_SCAN_RESULT));
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
//		mSimpleProgressDialog.setText("正在加载数据……");
//		mSimpleProgressDialog.showDialog();
		ProductManager.getInstance().getProductDetail(MyApplication.getStoreid(), productCode,
				new ManagerCallBack<ProductEntity>() {
					@Override
					public void onSuccess(ProductEntity returnContent) {
						super.onSuccess(returnContent);
						refreshView(returnContent);
//						mSimpleProgressDialog.cancleDialog();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						Toast.makeText(ProductAddNewActivity.this, msg, Toast.LENGTH_SHORT).show();
//						mSimpleProgressDialog.cancleDialog();
					}
				});
	}

	private void refreshView(ProductEntity entity) {
		editName.setText(entity.getProduct_name());
		editPrice.setText(entity.getProduct_price() + "");
		editUnit.setText(entity.getProduct_unit());
		editApr.setText(entity.getProduct_apr() + "");
		editTag.setText(entity.getProduct_tag());
		editStock.setText(entity.getStock_cnt() + "");
		editStockLimit.setText(entity.getStock_limit() + "");
		editDesc.setText(entity.getProduct_desc());

		checkBoxRecommend.setChecked(entity.getStatus() == 1);
		checkBoxLock.setChecked(entity.getIs_recommend() == 1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textleft:
			finish();
			break;
		case R.id.textRight:
			addProduct();
			break;
		case R.id.imageScanBarCode:
			startActivityForResult(new Intent(this, ProductScanCodeActivity.class), CODE_SCAN_BAR_REQUEST);
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

	private void addProduct() {
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
		String product_bar = null;
		String product_desc = editDesc.getText().toString().trim();
		int product_price = Integer.valueOf(editPrice.getText().toString().trim());
		String product_unit = editUnit.getText().toString().trim();
		if (product_unit == null || product_unit.isEmpty()) {
			Toast.makeText(this, "请输入商品单位", Toast.LENGTH_SHORT).show();
			return;
		}
		// int product_apr =
		// Integer.valueOf(editApr.getText().toString().trim());
		int product_apr = 100;
		int stock_cnt = Integer.valueOf(editStock.getText().toString().trim());
		// int stock_limit =
		// Integer.valueOf(editStockLimit.getText().toString().trim());
		int stock_limit = 0;
		String product_tag = editTag.getText().toString().trim();
		long purchase_channel = 0;
		mSimpleProgressDialog.setText("正在为您创建商品....");
		mSimpleProgressDialog.showDialog();
		ProductManager.getInstance().addProduct(selectedImageList, MyApplication.getStoreid(), product_name,
				product_brand, product_bar, product_desc, product_price, product_unit, product_apr, stock_cnt,
				checkBoxRecommend.isChecked(), checkBoxLock.isChecked(), stock_limit, product_tag, purchase_channel,
				new ManagerCallBack<String>() {
					@Override
					public void onSuccess(String returnContent) {
						super.onSuccess(returnContent);
						mSimpleProgressDialog.cancleDialog();
						AlertDialog.Builder builder = new Builder(ProductAddNewActivity.this);
						builder.setMessage("添加成功！");
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
					public void onProgress(int progress) {
						super.onProgress(progress);
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						mSimpleProgressDialog.cancleDialog();
						Toast.makeText(ProductAddNewActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				});
	}

	public void resetView() {
		editName.setText("");
		editPrice.setText("");
		editUnit.setText("");
		editApr.setText("");
		editTag.setText("");
		editStock.setText("");
		editStockLimit.setText("");
		editDesc.setText("");

		checkBoxRecommend.setChecked(false);
		checkBoxLock.setChecked(true);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (menuList == null) {
			initMenuView();
		}
		mBottomMenuDialog.showDialog(position);
		return true;
	}

	/** 初始化菜单view */
	private void initMenuView() {
		menuList = new ArrayList<MenuItemEntity>();
		MenuItemEntity menuItemEntity1 = new MenuItemEntity(1, "移除这张照片", MenuItemEntity.Color.ALERT);
		menuList.add(menuItemEntity1);
		mBottomMenuDialog = new BottomMenuDialog<Integer>(this);
		mBottomMenuDialog.setMenuList(menuList);
		mBottomMenuDialog.setOnItemClickListener(new BottomMenuDialog.OnItemClickListener<Integer>() {
			@Override
			public void onItemClick(int menuId, Integer prams) {
				if (selectedImageList != null && selectedImageList.size() > 0) {
					selectedImageList.remove(prams);
					picAdapter.remove(prams);
				}
			}
		});
	}

	private void initView() {
		textLeft = (TextView) findViewById(R.id.textleft);
		textLeft.setText("返回");
		textLeft.setVisibility(View.VISIBLE);
		textLeft.setOnClickListener(this);

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("添加新商品");
		textCenter.setVisibility(View.VISIBLE);

		textRight = (TextView) findViewById(R.id.textRight);
		textRight.setText("完成");
		textRight.setVisibility(View.VISIBLE);
		textRight.setOnClickListener(this);

		editName = (EditText) findViewById(R.id.editName);
		editBrand = (EditText) findViewById(R.id.editBrand);
		editPrice = (EditText) findViewById(R.id.editPrice);
		editUnit = (EditText) findViewById(R.id.editUnit);
		editApr = (EditText) findViewById(R.id.editApr);
		editTag = (EditText) findViewById(R.id.editTag);
		editCode = (EditText) findViewById(R.id.editCode);
		editStock = (EditText) findViewById(R.id.editStock);
		editStockLimit = (EditText) findViewById(R.id.editStockLimit);
		editDesc = (EditText) findViewById(R.id.editDesc);

		checkBoxRecommend = (CheckBox) findViewById(R.id.checkBoxRecommend);
		checkBoxLock = (CheckBox) findViewById(R.id.checkBoxLock);

		imageScanBarCode = (ImageView) findViewById(R.id.imageScanBarCode);
		imageScanBarCode.setOnClickListener(this);

		// spinnerPurchase = (Spinner) findViewById(R.id.spinnerPurchase);

		gridViewPic = (NoScrollGridView) findViewById(R.id.gridViewPic);
		picAdapter = new ProductSelectPicAdapter(this);
		gridViewPic.setAdapter(picAdapter);
		gridViewPic.setOnItemClickListener(this);
		gridViewPic.setOnItemLongClickListener(this);
		selectedImageList = new ArrayList<GalleryPicEntity>();
		selectedImageList.add(new GalleryPicEntity());
		picAdapter.setData(selectedImageList);

		mSimpleProgressDialog = new SimpleProgressDialog(this, true);
		mSimpleProgressDialog.setText("正在为您创建商品....");
	}
}
