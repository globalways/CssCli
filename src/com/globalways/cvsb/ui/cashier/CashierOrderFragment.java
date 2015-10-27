package com.globalways.cvsb.ui.cashier;

import java.lang.reflect.Field;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;
import com.globalways.cvsb.R;
import com.globalways.cvsb.android.encoding.EncodingHandler;
import com.globalways.cvsb.entity.OrderEntity;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.OrderManager;
import com.globalways.cvsb.tools.BillPrinter;
import com.globalways.cvsb.tools.PollingUtils;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.BaseFragment;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.ui.order.OrderActivity;
import com.globalways.cvsb.ui.order.OrderStatus;
import com.globalways.cvsb.view.ClearableEditText;
import com.globalways.cvsb.view.MyDialogManager;
import com.globalways.cvsb.view.SimpleProgressDialog;
import com.google.zxing.WriterException;

/**
 * 订单结算
 * 
 * @author James.Fan
 *
 */
public class CashierOrderFragment extends BaseFragment implements OnClickListener {

	
	//test
	Runnable mRunnable = null;
	
	//订单轮询
	private OrderReceiver mOrderReceiver;
	private IntentFilter mIntentFilter;
	
	private View layoutView;
	private View viewStepOne, viewStepTwo;
	/** Step one */
	private TextView textTotalPrice, textSignTotalPrice1;
	private ClearableEditText editApr, editDesc;
	private Button btnCancel, btnNext, btnRemoveOdd;

	/** Step two */
	private TextView textSignNum, textSignTotalPrice2, textKeepChange;
	private ClearableEditText editCash;
	private Button btnCancelSign, btnComfirm,btnConfirmQrPay, btnShowQRCode, btnAlipayQR, btnHuanTuQR;
	private View viewStepTwoForm, viewOnlinePaySucees, viewQRCodeArea ;
	//支付宝支付二维码
	private Bitmap qrAlipayBitmap;
	
	//小票打印机
	private BillPrinter mBillPrinter;
	
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
		mBillPrinter = new BillPrinter(getActivity());
		initView();
	}

	public void setOrderData(List<ProductEntity> cashierList, long totalPrice) {
		viewStepOne.setVisibility(View.VISIBLE);
		viewStepTwo.setVisibility(View.GONE);
		this.cashierList = cashierList;
		this.totalPrice = totalPrice;
		textTotalPrice.setText("￥" + Tool.fenToYuan(totalPrice));
		textSignTotalPrice1.setText("￥" + Tool.fenToYuan(totalPrice));
		//优惠&备注
		editApr.setText("");
		editDesc.setText("");
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
			toFinishOrder(true);
			break;
		case R.id.btnComfirmQRPay:
			toFinishOrder(false);
			break;
		case R.id.btnRemoveOdd:
			//去零
			removeOdd();
			break;
		case R.id.btnShowQRCode:
			//扫码支付或者现金支付
			if(viewStepTwoForm.isShown()){
				
				//qr code
				Bitmap qrAlipay = null;
				Bitmap qrHuantu = null;
				//获取支付宝二维码
				if(qrAlipayBitmap == null){
					OrderManager.getInstance().getAlipayQRCode(orderEntity.getOrder_id(), new ManagerCallBack<String>() {
						@Override
						public void onSuccess(String returnContent) {
							super.onSuccess(returnContent);
							try {
								qrAlipayBitmap = EncodingHandler.createQRCode(returnContent, 350);
								btnAlipayQR.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(getResources(),qrAlipayBitmap), null, null);
							} catch (WriterException e) {
								e.printStackTrace();
							}
						}
					});
				}
				
				try {
					qrHuantu = EncodingHandler.createQRCode(orderEntity.getOrder_id(), 350);
				} catch (WriterException e) {
					e.printStackTrace();
				}
				btnHuanTuQR.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(getResources(),qrHuantu), null, null);
				btnAlipayQR.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(getResources(),qrAlipayBitmap), null, null);
				viewStepTwoForm.setVisibility(View.GONE);
				viewQRCodeArea.setVisibility(View.VISIBLE);
				btnShowQRCode.setText("现金支付");
				//注册receiver
				mOrderReceiver = new OrderReceiver();
				mIntentFilter = new IntentFilter(OrderPollingService.ACTION);
				getActivity().registerReceiver(mOrderReceiver,mIntentFilter);
				PollingUtils.startPollingService(getActivity(), 5, OrderPollingService.class, OrderPollingService.ACTION,orderEntity.getOrder_id());
				
			}else{
				viewStepTwoForm.setVisibility(View.VISIBLE);
				viewQRCodeArea.setVisibility(View.GONE);
				btnShowQRCode.setText("扫码支付");
				getActivity().unregisterReceiver(mOrderReceiver);
				PollingUtils.stopPollingService(getActivity(), OrderPollingService.class, OrderPollingService.ACTION);
			}
			break;
		}
	}

	private List<ProductEntity> cashierList;
	private OrderEntity orderEntity;

	/**
	 * 去零
	 */
	private void removeOdd(){
		String total = textTotalPrice.getText().toString();
		String odd = null;
		if(total.contains(".")){
			odd = total.substring(total.indexOf("."),total.length());
			editApr.setText("0"+odd);
		}
	}
	
	/** 创建订单 */
	private void toSignOrder() {
		long discount_amount = 0;
		String text = editApr.getText().toString().trim();
		if (text != null && !text.isEmpty()) {
			discount_amount = (long) (Float.valueOf(text) * 100);
		}
		String desc = editDesc.getText().toString().trim();
		if (cashierList == null || cashierList.size() <= 0) {
			UITools.ToastMsg(getActivity(), "购物车信息有误");
			return;
		}
		mSimpleProgressDialog.showDialog();
		OrderManager.getInstance().toSignOrder(cashierList, discount_amount, desc, new ManagerCallBack<OrderEntity>() {
			@Override
			public void onSuccess(OrderEntity returnContent) {
				super.onSuccess(returnContent);
				showOrderStepTwo(returnContent);
				mSimpleProgressDialog.cancleDialog();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
				mSimpleProgressDialog.cancleDialog();
			}
		});
	}

	public void showOrderStepTwo(OrderEntity returnContent) {
		orderEntity = returnContent;
		if (orderEntity != null) {
			viewStepOne.setVisibility(View.GONE);
			viewStepTwo.setVisibility(View.VISIBLE);
			//二维码
			if(viewQRCodeArea.isShown()){
				viewQRCodeArea.setVisibility(View.GONE);
			}
			//在线支付成功
			if(viewOnlinePaySucees.isShown()){
				viewOnlinePaySucees.setVisibility(View.GONE);
			}
			if(!viewStepTwoForm.isShown()){
				viewStepTwoForm.setVisibility(View.VISIBLE);
			}
			if(!btnShowQRCode.isShown()){
				btnShowQRCode.setVisibility(View.VISIBLE);
			}
			textSignNum.setText(orderEntity.getOrder_id());
			textSignTotalPrice2.setText("￥" + Tool.fenToYuan(orderEntity.getOrder_amount()));
		} else {
			UITools.ToastMsg(getActivity(), "订单信息有误");
		}
		
	}

	
	AlertDialogPro dialog;
	Handler handler;
	/** 完成订单
	 *  @param isCash 是否是现金收银; 现金收银为true; 在线付款为false 
	 */
	private void toFinishOrder(boolean isCash) {
		if (orderEntity == null) {
			UITools.ToastMsg(getActivity(), "订单信息有误");
			return;
		}
		mSimpleProgressDialog.setText("正在完成订单....");
		OrderManager.getInstance().updateSignStatus(isCash,orderEntity.getOrder_id(), new ManagerCallBack<String>() {
			@SuppressLint("HandlerLeak")
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				handler = new Handler(){
					public void handleMessage(Message msg) {
						if(msg.what == 0){
							Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
							btnPositive.setText("打印完成");
						}else{
							
						}
					};
				};
				mSimpleProgressDialog.cancleDialog();
				if(getActivity() instanceof CashierActivity){
					((CashierActivity) getActivity()).hideSignDialog(true);
					CashierActivity.isBlockBackKey = false;
					//打印小票
					final AlertDialogPro.Builder builder = MyDialogManager.builder(getActivity());
					builder.setPositiveButton("打印", null).
					setNegativeButton("不打印", null);
					dialog = builder.create();
					dialog.setCanceledOnTouchOutside(false);
					dialog.setTitle("提示");
					dialog.setMessage("订单已完成，是否打印票据？");
					dialog.show();
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(((Button)v).getText().equals("打印完成")){
								dialog.dismiss();
							}else{
								((Button)v).setText("正在打印");
								handler.sendEmptyMessage(0);
								new Handler().post(new Runnable() {
									@Override
									public void run() {
										mBillPrinter.printCashier(orderEntity,cashierList);
									}
								});
							}
						}
					});
				}else if(getActivity() instanceof OrderActivity){
					((OrderActivity) getActivity()).hidePayDialog();
				}
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				mSimpleProgressDialog.cancleDialog();
				UITools.ToastMsg(getActivity(), msg);
			}
		});
	}

	/** 取消订单 */
	private void toCancelOrder() {
		if (orderEntity == null) {
			UITools.ToastMsg(getActivity(), "订单信息有误");
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
								if(getActivity() instanceof CashierActivity){
									((CashierActivity) getActivity()).hideSignDialog(true);
									CashierActivity.isBlockBackKey = false;
								}else if(getActivity() instanceof OrderActivity){
									((OrderActivity) getActivity()).hidePayDialog();
								}
								UITools.ToastMsg(getActivity(), "取消成功");
							}

							@Override
							public void onFailure(int code, String msg) {
								super.onFailure(code, msg);
								mSimpleProgressDialog.cancleDialog();
								UITools.ToastMsg(getActivity(), msg);
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
		viewStepTwoForm = layoutView.findViewById(R.id.viewStepTwoForm);
		viewOnlinePaySucees = layoutView.findViewById(R.id.rlOnlinePaySucces);
		btnShowQRCode = (Button) layoutView.findViewById(R.id.btnShowQRCode);
		btnShowQRCode.setOnClickListener(this);
		btnRemoveOdd = (Button) layoutView.findViewById(R.id.btnRemoveOdd);
		btnRemoveOdd.setOnClickListener(this);
		
		//二维码
		viewQRCodeArea = layoutView.findViewById(R.id.viewQRCodeArea);
		btnAlipayQR = (Button) layoutView.findViewById(R.id.btnAlipayQR);
		btnHuanTuQR = (Button) layoutView.findViewById(R.id.btnHuanTuQR);
		
		btnConfirmQrPay = (Button) layoutView.findViewById(R.id.btnComfirmQRPay);
		btnConfirmQrPay.setOnClickListener(this);
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
				} else {
					textSignTotalPrice1.setText("￥" + totalPrice / 100.00);
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
	
	/**
	 * 扫码支付完成结果
	 * @author wyp E-mail:onebyte@qq.com
	 * @version Time: 2015年7月14日 下午9:22:05
	 */
	private class OrderReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//refresh ui
			if(OrderStatus.valueOf(intent.getExtras().getInt("status")).equals(OrderStatus.PAY_SUCCESS)){
				//用户付款成功
				viewOnlinePaySucees.setVisibility(View.VISIBLE);
				viewQRCodeArea.setVisibility(View.GONE);
				//不能返回现金支付
				btnShowQRCode.setVisibility(View.GONE);
				CashierActivity.isBlockBackKey = true;
				PollingUtils.stopPollingService(getActivity(), OrderPollingService.class, OrderPollingService.ACTION);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(viewStepTwoForm != null && !viewStepTwoForm.isShown() && orderEntity != null){
			mOrderReceiver = new OrderReceiver();
			mIntentFilter = new IntentFilter(OrderPollingService.ACTION);
			getActivity().registerReceiver(mOrderReceiver,mIntentFilter);
			PollingUtils.startPollingService(getActivity(), 5, OrderPollingService.class, OrderPollingService.ACTION, orderEntity.getOrder_id());
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mOrderReceiver != null ){
			try {
				getActivity().unregisterReceiver(mOrderReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		PollingUtils.stopPollingService(getActivity(), OrderPollingService.class, OrderPollingService.ACTION);
	}
	

}
