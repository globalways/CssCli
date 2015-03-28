package com.globalways.csscli.ui.cashier;

import java.util.List;

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
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.OrderEntity;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.OrderManager;
import com.globalways.csscli.ui.BaseFragment;
import com.globalways.csscli.view.ClearableEditText;
import com.globalways.csscli.view.SimpleProgressDialog;

/**
 * 订单结算
 * 
 * @author James.Fan
 *
 */
public class CashierOrderFragment extends BaseFragment implements OnClickListener {

	private View layoutView;
	private View viewStepOne, viewStepTwo;
	/** Step one */
	private TextView textTotalPrice, textSignTotalPrice1;
	private ClearableEditText editApr, editDesc;
	private Button btnCancel, btnNext;

	/** Step one */
	private TextView textSignNum, textSignTotalPrice2, textKeepChange;
	private ClearableEditText editCash;
	private Button btnCancelSign, btnComfirm;

	/** 进度条 **/
	private SimpleProgressDialog mSimpleProgressDialog;

	private long totalPrice;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.cashier_order_fragment, container, false);
		}
		return layoutView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	public void setOrderData(List<ProductEntity> cashierList, long totalPrice) {
		viewStepOne.setVisibility(View.VISIBLE);
		viewStepTwo.setVisibility(View.GONE);
		this.cashierList = cashierList;
		this.totalPrice = totalPrice;
		textTotalPrice.setText("￥" + totalPrice / 100.00);
		textSignTotalPrice1.setText("￥" + totalPrice / 100.00);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnCancel:
			((CashierActivity) getActivity()).hideSignDialog(false);
			break;
		case R.id.btnNext:
			toSignOrder();
			break;
		case R.id.btnCancelSign:
			toCancelOrder();
			break;
		case R.id.btnComfirm:
			toFinishOrder();
			break;
		}
	}

	private List<ProductEntity> cashierList;
	private OrderEntity orderEntity;;

	/** 创建订单 */
	private void toSignOrder() {
		long discount_amount = 0;
		String text = editApr.getText().toString().trim();
		if (text != null && !text.isEmpty()) {
			discount_amount = (long) (Float.valueOf(text) * 100);
		}
		String desc = editDesc.getText().toString().trim();
		if (cashierList == null || cashierList.size() <= 0) {
			Toast.makeText(getActivity(), "购物车信息有误", Toast.LENGTH_SHORT).show();
			return;
		}
		mSimpleProgressDialog.showDialog();
		OrderManager.getInstance().toSignOrder(cashierList, discount_amount, desc, new ManagerCallBack<OrderEntity>() {
			@Override
			public void onSuccess(OrderEntity returnContent) {
				super.onSuccess(returnContent);
				refreshView(returnContent);
				mSimpleProgressDialog.cancleDialog();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				mSimpleProgressDialog.cancleDialog();
			}
		});
	}

	private void refreshView(OrderEntity returnContent) {
		orderEntity = returnContent;
		if (orderEntity != null) {
			viewStepOne.setVisibility(View.GONE);
			viewStepTwo.setVisibility(View.VISIBLE);

			textSignNum.setText(orderEntity.getOrder_id());
			textSignTotalPrice2.setText("￥" + orderEntity.getOrder_amount() / 100.00);
		} else {
			Toast.makeText(getActivity(), "订单信息有误", Toast.LENGTH_SHORT).show();
		}
	}

	/** 完成订单 */
	private void toFinishOrder() {
		if (orderEntity == null) {
			Toast.makeText(getActivity(), "订单信息有误", Toast.LENGTH_SHORT).show();
			return;
		}
		mSimpleProgressDialog.setText("正在完成订单....");
		OrderManager.getInstance().updateSignStatus(orderEntity.getOrder_id(), new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				mSimpleProgressDialog.cancleDialog();
				((CashierActivity) getActivity()).hideSignDialog(true);
				Toast.makeText(getActivity(), "订单已完成", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				mSimpleProgressDialog.cancleDialog();
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/** 取消订单 */
	private void toCancelOrder() {
		if (orderEntity == null) {
			Toast.makeText(getActivity(), "订单信息有误", Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder = new Builder(getActivity());
		final EditText editText = new EditText(getActivity());
		builder.setMessage("取消原因");
		builder.setView(editText);
		editText.setText("客户不买了");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mSimpleProgressDialog.setText("正在取消订单....");
				OrderManager.getInstance().cancelSignStatus(orderEntity.getOrder_id(),
						editText.getText().toString().trim(), new ManagerCallBack<String>() {
							@Override
							public void onSuccess(String returnContent) {
								super.onSuccess(returnContent);
								mSimpleProgressDialog.cancleDialog();
								((CashierActivity) getActivity()).hideSignDialog(true);
								Toast.makeText(getActivity(), "取消成功", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onFailure(int code, String msg) {
								super.onFailure(code, msg);
								mSimpleProgressDialog.cancleDialog();
								Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
							}
						});
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}

	private void initView() {
		viewStepOne = (View) layoutView.findViewById(R.id.viewStepOne);
		viewStepOne.setVisibility(View.VISIBLE);
		viewStepTwo = (View) layoutView.findViewById(R.id.viewStepTwo);
		viewStepTwo.setVisibility(View.GONE);

		textTotalPrice = (TextView) layoutView.findViewById(R.id.textTotalPrice);
		textSignTotalPrice1 = (TextView) layoutView.findViewById(R.id.textSignTotalPrice1);
		editApr = (ClearableEditText) layoutView.findViewById(R.id.editApr);
		editApr.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = editApr.getText().toString().trim();
				if (text != null && !text.isEmpty()) {
					if (text.startsWith("00") || text.startsWith("01") || text.startsWith("02")
							|| text.startsWith("03") || text.startsWith("04") || text.startsWith("05")
							|| text.startsWith("06") || text.startsWith("07") || text.startsWith("08")
							|| text.startsWith("09")) {
						text = text.substring(1);
						editApr.setText(text);
						editApr.setSelection(editApr.getText().toString().trim().length());
					}
					if (text.startsWith(".")) {
						text = text.replace(".", "0.");
						editApr.setText(text);
						editApr.setSelection(editApr.getText().toString().trim().length());
					}
					long price = (long) (Float.valueOf(text) * 100);
					if (price > totalPrice) {
						price = totalPrice;
						editApr.setText(price / 100.00 + "");
						editApr.setSelection(editApr.getText().toString().length());
					}
					textSignTotalPrice1.setText("￥" + (totalPrice - price) / 100.00);
				}
			}
		});

		editDesc = (ClearableEditText) layoutView.findViewById(R.id.editDesc);
		btnCancel = (Button) layoutView.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		btnNext = (Button) layoutView.findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);

		textSignNum = (TextView) layoutView.findViewById(R.id.textSignNum);
		textSignTotalPrice2 = (TextView) layoutView.findViewById(R.id.textSignTotalPrice2);
		textKeepChange = (TextView) layoutView.findViewById(R.id.textKeepChange);
		editCash = (ClearableEditText) layoutView.findViewById(R.id.editCash);
		editCash.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = editCash.getText().toString().trim();
				if (text != null && !text.isEmpty()) {
					if (text.startsWith("00") || text.startsWith("01") || text.startsWith("02")
							|| text.startsWith("03") || text.startsWith("04") || text.startsWith("05")
							|| text.startsWith("06") || text.startsWith("07") || text.startsWith("08")
							|| text.startsWith("09")) {
						text = text.substring(1);
						editCash.setText(text);
						editCash.setSelection(editCash.getText().toString().trim().length());
					}
					if (text.startsWith(".")) {
						text = text.replace(".", "0.");
						editCash.setText(text);
						editCash.setSelection(editCash.getText().toString().trim().length());
					}
					long price = (long) (Float.valueOf(text) * 100);
					if (price > orderEntity.getOrder_amount()) {
						textKeepChange.setText("￥" + (price - orderEntity.getOrder_amount()) / 100.0);
					}
				}
			}
		});

		btnCancelSign = (Button) layoutView.findViewById(R.id.btnCancelSign);
		btnCancelSign.setOnClickListener(this);
		btnComfirm = (Button) layoutView.findViewById(R.id.btnComfirm);
		btnComfirm.setOnClickListener(this);

		mSimpleProgressDialog = new SimpleProgressDialog(getActivity(), true);
		mSimpleProgressDialog.setText("正在为您创建订单....");
	}

}
