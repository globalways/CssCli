package com.globalways.csscli.ui.cashier;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.BaseFragment;

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
	private EditText editApr, editDesc;
	private Button btnCancel, btnNext;

	/** Step one */
	private TextView textSignNum, textSignTotalPrice2, textKeepChange;
	private EditText editCash;
	private Button btnCancelSign, btnComfirm;

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

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnCancel:
			break;
		case R.id.btnNext:
			break;
		case R.id.btnCancelSign:
			break;
		case R.id.btnComfirm:
			break;
		}
	}

	private void toSignOrder() {
	}

	private void initView() {
		textTotalPrice = (TextView) layoutView.findViewById(R.id.textTotalPrice);
		textSignTotalPrice1 = (TextView) layoutView.findViewById(R.id.textSignTotalPrice1);
		editApr = (EditText) layoutView.findViewById(R.id.editApr);
		editDesc = (EditText) layoutView.findViewById(R.id.editDesc);
		btnCancel = (Button) layoutView.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		btnNext = (Button) layoutView.findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);

		textSignNum = (TextView) layoutView.findViewById(R.id.textSignNum);
		textSignTotalPrice2 = (TextView) layoutView.findViewById(R.id.textSignTotalPrice2);
		textKeepChange = (TextView) layoutView.findViewById(R.id.textKeepChange);
		editCash = (EditText) layoutView.findViewById(R.id.editCash);
		btnCancelSign = (Button) layoutView.findViewById(R.id.btnCancelSign);
		btnCancelSign.setOnClickListener(this);
		btnComfirm = (Button) layoutView.findViewById(R.id.btnComfirm);
		btnComfirm.setOnClickListener(this);
	}

}
