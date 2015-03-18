package com.globalways.csscli.ui.cashier;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.http.manager.ProductManager.GetProductType;
import com.globalways.csscli.tools.PicassoImageLoader;
import com.globalways.csscli.ui.BaseFragment;

public class CashierQRCodeFragment extends BaseFragment implements OnClickListener {

	private long storeid = 46758;
	private ProductEntity productEntity;

	private View layoutView;
	private ImageView imageProductAva;
	private TextView textProductName, textProductPrice, textNumber;
	private Button btnLess, btnAdd, btnAddCashier;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.cashier_qrcode_fragment, container, false);
		}
		return layoutView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();

		productEntity = new ProductEntity();
		productEntity.setProduct_name("美味辣条");
		productEntity.setProduct_price(300);
		productEntity.setProduct_unit("袋");
		productEntity.setShoppingNumber(5);
		refreshView();
	}

	/**
	 * 扫码成功后调用这个方法，获取商品的详细信息
	 * 
	 * @param type
	 *            二维码还是条形码
	 * @param productCode
	 *            二维码或者条形码的内容
	 */
	private void loadProductDetail(GetProductType type, String productCode) {
		ProductManager.getInstance().getProductDetail(storeid, type, productCode, new ManagerCallBack<ProductEntity>() {
			@Override
			public void onSuccess(ProductEntity returnContent) {
				super.onSuccess(returnContent);
				productEntity = returnContent;
				refreshView();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void refreshView() {
		textNumber.setText("1");
		new PicassoImageLoader(getActivity()).showImage(productEntity.getProduct_avatar(), R.drawable.logo,
				R.drawable.logo, imageProductAva);
		textProductName.setText(productEntity.getProduct_name());
		textProductPrice.setText("￥ " + productEntity.getProduct_price() / 100.00 + " / "
				+ productEntity.getProduct_unit() + "  ");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textNumber:
			if (productEntity != null) {
				showNumberDialog();
			}
			break;
		case R.id.btnLess:
			if (productEntity != null) {
				String text1 = textNumber.getText().toString().trim();
				if (Integer.valueOf(textNumber.getText().toString().trim()) > 1) {
					textNumber.setText(Integer.valueOf(text1) - 1 + "");
				}
				btnLess.setEnabled(!textNumber.getText().toString().trim().equals("1"));
			}
			break;
		case R.id.btnAdd:
			if (productEntity != null) {
				textNumber.setText(Integer.valueOf(textNumber.getText().toString().trim()) + 1 + "");
				btnLess.setEnabled(!textNumber.getText().toString().trim().equals("1"));
			}
			break;
		case R.id.btnAddCashier:
			if (productEntity != null) {
				((CashierActivity) getActivity()).addCashierProduct(productEntity);
			}
			reset();
			break;
		}
	}

	private void reset() {
		productEntity = null;
		textNumber.setText("1");
		textProductName.setText("");
		textProductPrice.setText("");
		// productAva.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.logo));
	}

	private void initView() {
		imageProductAva = (ImageView) layoutView.findViewById(R.id.imageProductAva);
		textProductName = (TextView) layoutView.findViewById(R.id.textProductName);
		textProductPrice = (TextView) layoutView.findViewById(R.id.textProductPrice);
		textNumber = (TextView) layoutView.findViewById(R.id.textNumber);
		textNumber.setOnClickListener(this);

		btnLess = (Button) layoutView.findViewById(R.id.btnLess);
		btnLess.setOnClickListener(this);
		btnAdd = (Button) layoutView.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
		btnAddCashier = (Button) layoutView.findViewById(R.id.btnAddCashier);
		btnAddCashier.setOnClickListener(this);
	}

	/** 弹出输入数量的对话框 */
	@SuppressLint("InflateParams")
	private void showNumberDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.cashier_list_item_edittextview, null);
		final EditText editText = (EditText) dialogView.findViewById(R.id.editDialogNumber);
		final Button btnDialogLess = (Button) dialogView.findViewById(R.id.btnDialogLess);
		final Button btnDialogAdd = (Button) dialogView.findViewById(R.id.btnDialogAdd);
		editText.setText(textNumber.getText().toString());
		editText.setSelection(editText.getText().toString().trim().length());
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = editText.getText().toString().trim();
				if (text != null && !text.isEmpty()) {
					if (text.startsWith("0") && text.length() > 1) {
						text = text.substring(1);
						editText.setText(text);
					}
				} else {
					editText.setText("1");
				}
				btnDialogLess.setEnabled(!editText.getText().toString().trim().equals("1"));
				editText.setSelection(editText.getText().toString().trim().length());
			}
		});
		if (editText.getText().toString().trim().equals("1")) {
			btnDialogLess.setEnabled(false);
		}
		btnDialogLess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = editText.getText().toString().trim();
				if (!text.equals("1")) {
					editText.setText(Integer.valueOf(editText.getText().toString().trim()) - 1 + "");
				}
			}
		});
		btnDialogAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editText.setText(Integer.valueOf(editText.getText().toString().trim()) + 1 + "");
			}
		});
		builder.setView(dialogView);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				productEntity.setShoppingNumber(Integer.valueOf(editText.getText().toString().trim()));
				textNumber.setText(editText.getText().toString());
			}
		});
		builder.create().show();
	}

}
