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
import android.widget.EditText;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.ui.BaseFragment;

/**
 * 添加商品弹出层
 * 
 * @author James
 *
 */
public class ProductAddNewFragment extends BaseFragment implements OnClickListener {

	private View layoutView;

	private EditText editName, editBrand, editPrice, editUnit, editApr, editTag, editStock, editStockLimit, editDesc;
	private Button btnCancel, btnAdd;
	private CheckBox checkBoxRecommend, checkBoxLock;
//	private Spinner spinnerPurchase;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.product_addnew_fragment, container, false);
		}
		return layoutView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAdd:
			addProduct();
			break;
		case R.id.btnCancel:
			((ProductActivity) getActivity()).hideAddProductFragment();
			break;
		}
	}

	private long storeid;

	public void setData(long storeid) {
		this.storeid = storeid;
	}

	private void addProduct() {
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
		String product_bar = null;
		String product_avatar = null;
		String product_desc = editDesc.getText().toString().trim();
		int product_price = Integer.valueOf(editPrice.getText().toString().trim());
		String product_unit = editUnit.getText().toString().trim();
		if (product_unit == null || product_unit.isEmpty()) {
			Toast.makeText(getActivity(), "请输入商品单位", Toast.LENGTH_SHORT).show();
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
		ProductManager.getInstance().addProduct(storeid, product_name, product_brand, product_bar, product_desc,
				product_avatar, product_price, product_unit, product_apr, stock_cnt, checkBoxRecommend.isChecked(),
				checkBoxLock.isChecked(), stock_limit, product_tag, purchase_channel, new ManagerCallBack<String>() {
					@Override
					public void onSuccess(String returnContent) {
						super.onSuccess(returnContent);
						AlertDialog.Builder builder = new Builder(getActivity());
						builder.setMessage("修改成功！");
						builder.setTitle("提示！");
						builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								((ProductActivity) getActivity()).hideAddProductFragment();
							}
						});
						builder.create().show();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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

	private void initView() {
		editName = (EditText) layoutView.findViewById(R.id.editName);
		editBrand = (EditText) layoutView.findViewById(R.id.editBrand);
		editPrice = (EditText) layoutView.findViewById(R.id.editPrice);
		editUnit = (EditText) layoutView.findViewById(R.id.editUnit);
		editApr = (EditText) layoutView.findViewById(R.id.editApr);
		editTag = (EditText) layoutView.findViewById(R.id.editTag);
		editStock = (EditText) layoutView.findViewById(R.id.editStock);
		editStockLimit = (EditText) layoutView.findViewById(R.id.editStockLimit);
		editDesc = (EditText) layoutView.findViewById(R.id.editDesc);

		btnAdd = (Button) layoutView.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
		btnCancel = (Button) layoutView.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);

		checkBoxRecommend = (CheckBox) layoutView.findViewById(R.id.checkBoxRecommend);
		checkBoxLock = (CheckBox) layoutView.findViewById(R.id.checkBoxLock);

//		spinnerPurchase = (Spinner) layoutView.findViewById(R.id.spinnerPurchase);
	}

}
