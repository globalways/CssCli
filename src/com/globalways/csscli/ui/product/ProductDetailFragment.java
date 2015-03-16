package com.globalways.csscli.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.ui.BaseFragment;

/**
 * 商品详情fragment，可以查看或修改商品信息
 * 
 * @author James
 *
 */
public class ProductDetailFragment extends BaseFragment implements OnClickListener {

	private View layoutView;
	private EditText editName, editPrice, editUnit, editApr, editTag, editStock, editStockLimit, editDesc;
	private Button btnUpdate, btnReset, btnDelete, btnQRCode, btnAva;
	private CheckBox checkBoxRecommend, checkBoxLock;
	private Spinner spinnerPurchase;
	private ScrollView scrollViewProductDetail;

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
		resetView(entity);
		scrollViewProductDetail.setScrollY(0);
	}

	private void resetView(ProductEntity entity) {
		editName.setText(entity.getProduct_name());
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnUpdate:
			break;
		case R.id.btnReset:
			break;
		case R.id.btnDelete:
			break;
		case R.id.btnQRCode:
			break;
		case R.id.btnAva:
			break;
		}
	}

	private void initView() {
		editName = (EditText) layoutView.findViewById(R.id.editName);
		editPrice = (EditText) layoutView.findViewById(R.id.editPrice);
		editUnit = (EditText) layoutView.findViewById(R.id.editUnit);
		editApr = (EditText) layoutView.findViewById(R.id.editApr);
		editTag = (EditText) layoutView.findViewById(R.id.editTag);
		editStock = (EditText) layoutView.findViewById(R.id.editStock);
		editStockLimit = (EditText) layoutView.findViewById(R.id.editStockLimit);
		editDesc = (EditText) layoutView.findViewById(R.id.editDesc);

		btnUpdate = (Button) layoutView.findViewById(R.id.btnUpdate);
		btnUpdate.setOnClickListener(this);
		btnReset = (Button) layoutView.findViewById(R.id.btnReset);
		btnReset.setOnClickListener(this);
		btnDelete = (Button) layoutView.findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(this);
		btnQRCode = (Button) layoutView.findViewById(R.id.btnQRCode);
		btnQRCode.setOnClickListener(this);
		btnAva = (Button) layoutView.findViewById(R.id.btnAva);
		btnAva.setOnClickListener(this);

		checkBoxRecommend = (CheckBox) layoutView.findViewById(R.id.checkBoxRecommend);
		checkBoxLock = (CheckBox) layoutView.findViewById(R.id.checkBoxLock);

		spinnerPurchase = (Spinner) layoutView.findViewById(R.id.spinnerPurchase);
		scrollViewProductDetail = (ScrollView) layoutView.findViewById(R.id.scrollViewProductDetail);
	}
}
