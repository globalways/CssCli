package com.globalways.csscli.ui.product;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.ui.BaseFragment;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.view.ClearableEditText;
import com.globalways.csscli.view.NoScrollGridView;
import com.globalways.csscli.view.SimpleProgressDialog;

/**
 * 商品详情fragment，可以查看或修改商品信息
 * 
 * @author James
 *
 */
public class ProductDetailFragment extends BaseFragment implements OnClickListener {

	private View layoutView;
	private ClearableEditText editName, editBrand, editPrice, editUnit, editApr, editTag, editStock, editStockLimit,
			editDesc;
	private Button btnUpdate, btnReset;
	private CheckBox checkBoxRecommend, checkBoxLock;
	private NoScrollGridView gridProductPic;
	private ProductPicAdapter picAdapter;
	// private Spinner spinnerPurchase;
	private ScrollView scrollViewProductDetail;

	private ProductEntity entity;
	/** 进度条 **/
	private SimpleProgressDialog mSimpleProgressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.product_detail_fragment, container, false);
		}
		return layoutView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	public void setEntity(ProductEntity entity) {
		if (entity != null) {
			this.entity = entity;
			refreshView();
			scrollViewProductDetail.setScrollY(0);
			picAdapter.setData(entity.getProduct_avatar());
		}
	}

	private void refreshView() {
		if (entity != null) {
			editName.setText(entity.getProduct_name());
			editBrand.setText(entity.getProduct_brand());
			editPrice.setText(entity.getProduct_price() + "");
			editUnit.setText(entity.getProduct_unit());
			editApr.setText(entity.getProduct_apr() + "");
			editTag.setText(entity.getProduct_tag());
			editStock.setText(((int) entity.getStock_cnt()) + "");
			editStockLimit.setText(((int) entity.getStock_limit()) + "");
			editDesc.setText(entity.getProduct_desc());

			checkBoxRecommend.setChecked(entity.getIs_recommend() == 1 ? true : false);
			checkBoxLock.setChecked(entity.getStatus() == 1 ? true : false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnUpdate:
			updateOrAdd();
			break;
		case R.id.btnReset:
			refreshView();
			break;
		}
	}

	/**
	 * 修改商品信息
	 */
	private void updateOrAdd() {
		String product_name = editName.getText().toString().trim();
		if (product_name == null || product_name.isEmpty()) {
			Toast.makeText(getActivity(), "请输入商品名", Toast.LENGTH_SHORT).show();
			return;
		}
		String product_brand = editBrand.getText().toString().trim();
		if (product_brand == null || product_brand.isEmpty()) {
			Toast.makeText(getActivity(), "请输入商品品牌", Toast.LENGTH_SHORT).show();
			return;
		}
		String product_desc = editDesc.getText().toString().trim();

		// 价格
		String price = editPrice.getText().toString().trim();
		if (price == null || price.isEmpty()) {
			UITools.ToastMsg(getActivity(), "请输入商品价格");
			return;
		}
		int product_price = Integer.valueOf(price);

		// 单位
		String product_unit = editUnit.getText().toString().trim();
		if (product_unit == null || product_unit.isEmpty()) {
			Toast.makeText(getActivity(), "请输入商品单位", Toast.LENGTH_SHORT).show();
			return;
		}

		// 库存
		String stock = editStock.getText().toString().trim();
		double stock_cnt = 0;
		if (stock != null && !stock.isEmpty()) {
			stock_cnt = Double.valueOf(stock);
		}
		String product_tag = editTag.getText().toString().trim();

		mSimpleProgressDialog.showDialog();
		ProductManager.getInstance().updateOrAdd(false, null, null, product_name, product_brand,
				entity.getProduct_qr(), entity.getProduct_bar(), product_desc, product_price, product_unit, stock_cnt,
				checkBoxRecommend.isChecked(), checkBoxLock.isChecked(), product_tag, new ManagerCallBack<String>() {
					@Override
					public void onSuccess(String returnContent) {
						super.onSuccess(returnContent);
						mSimpleProgressDialog.cancleDialog();
						AlertDialog.Builder builder = new Builder(getActivity());
						builder.setMessage("修改成功！");
						builder.setTitle("提示！");
						builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						builder.create().show();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						mSimpleProgressDialog.cancleDialog();
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void initView() {
		editName = (ClearableEditText) layoutView.findViewById(R.id.editName);
		editBrand = (ClearableEditText) layoutView.findViewById(R.id.editBrand);
		editPrice = (ClearableEditText) layoutView.findViewById(R.id.editPrice);
		UITools.editNumLimit(editPrice);
		editUnit = (ClearableEditText) layoutView.findViewById(R.id.editUnit);
		editApr = (ClearableEditText) layoutView.findViewById(R.id.editApr);
		editTag = (ClearableEditText) layoutView.findViewById(R.id.editTag);
		editStock = (ClearableEditText) layoutView.findViewById(R.id.editStock);
		UITools.editNumDoubleLimit(editStock);
		editStockLimit = (ClearableEditText) layoutView.findViewById(R.id.editStockLimit);
		editDesc = (ClearableEditText) layoutView.findViewById(R.id.editDesc);

		btnUpdate = (Button) layoutView.findViewById(R.id.btnUpdate);
		btnUpdate.setOnClickListener(this);
		btnReset = (Button) layoutView.findViewById(R.id.btnReset);
		btnReset.setOnClickListener(this);

		checkBoxRecommend = (CheckBox) layoutView.findViewById(R.id.checkBoxRecommend);
		checkBoxLock = (CheckBox) layoutView.findViewById(R.id.checkBoxLock);
		gridProductPic = (NoScrollGridView) layoutView.findViewById(R.id.gridProductPic);
		picAdapter = new ProductPicAdapter(getActivity());
		gridProductPic.setAdapter(picAdapter);

		// spinnerPurchase = (Spinner)
		// layoutView.findViewById(R.id.spinnerPurchase);
		scrollViewProductDetail = (ScrollView) layoutView.findViewById(R.id.scrollViewProductDetail);

		mSimpleProgressDialog = new SimpleProgressDialog(getActivity(), true);
		mSimpleProgressDialog.setText("正在修改信息...");
	}
}
