package com.globalways.csscli.ui.product;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.ui.BaseActivity;
import com.globalways.csscli.ui.gallery.GalleryActivity;
import com.globalways.csscli.ui.gallery.GalleryPicEntity;
import com.globalways.csscli.ui.gallery.GalleryPicPreviewActivity;
import com.globalways.csscli.view.NoScrollGridView;

/**
 * 添加商品弹出层
 * 
 * @author James
 *
 */
public class ProductAddNewActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	/** 跳转相册选择照片的requestCode */
	private static final int CODE_SELECT_IMAGE = 11;

	private TextView textLeft, textCenter, textRight;
	private EditText editName, editBrand, editPrice, editUnit, editApr, editTag, editStock, editStockLimit, editDesc;
	private CheckBox checkBoxRecommend, checkBoxLock;

	private NoScrollGridView gridViewPic;
	private ProductSelectPicAdapter picAdapter;
	private ArrayList<GalleryPicEntity> selectedImageList;

	// private Spinner spinnerPurchase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_addnew_fragment);
		initView();
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
		String product_avatar = null;
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
		ProductManager.getInstance().addProduct(MyApplication.getStoreid(), product_name, product_brand, product_bar,
				product_desc, product_avatar, product_price, product_unit, product_apr, stock_cnt,
				checkBoxRecommend.isChecked(), checkBoxLock.isChecked(), stock_limit, product_tag, purchase_channel,
				new ManagerCallBack<String>() {
					@Override
					public void onSuccess(String returnContent) {
						super.onSuccess(returnContent);
						AlertDialog.Builder builder = new Builder(ProductAddNewActivity.this);
						builder.setMessage("修改成功！");
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

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (CODE_SELECT_IMAGE == requestCode && RESULT_OK == resultCode) {
			// 获取选择到的照片
			selectedImageList = (ArrayList<GalleryPicEntity>) data
					.getSerializableExtra(GalleryActivity.KEY_SELECTED_IMAGE);
			selectedImageList.add(new GalleryPicEntity());
			picAdapter.setData(selectedImageList);
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
		editStock = (EditText) findViewById(R.id.editStock);
		editStockLimit = (EditText) findViewById(R.id.editStockLimit);
		editDesc = (EditText) findViewById(R.id.editDesc);

		checkBoxRecommend = (CheckBox) findViewById(R.id.checkBoxRecommend);
		checkBoxLock = (CheckBox) findViewById(R.id.checkBoxLock);

		// spinnerPurchase = (Spinner) findViewById(R.id.spinnerPurchase);

		gridViewPic = (NoScrollGridView) findViewById(R.id.gridViewPic);
		picAdapter = new ProductSelectPicAdapter(this);
		gridViewPic.setAdapter(picAdapter);
		gridViewPic.setOnItemClickListener(this);
		selectedImageList = new ArrayList<GalleryPicEntity>();
		selectedImageList.add(new GalleryPicEntity());
		picAdapter.setData(selectedImageList);
	}

}
