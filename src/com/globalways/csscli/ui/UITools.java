package com.globalways.csscli.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.globalways.csscli.ui.account.LoginActivity;
import com.globalways.csscli.ui.cashier.CashierActivity;
import com.globalways.csscli.ui.main.MainActivity;
import com.globalways.csscli.ui.order.OrderActivity;
import com.globalways.csscli.ui.product.ProductActivity;
import com.globalways.csscli.ui.product.ProductAddNewActivity;
import com.globalways.csscli.ui.product.ProductScanCodeActivity;
import com.globalways.csscli.ui.purchase.PurchaseActivity;
import com.globalways.csscli.ui.settings.AppUpdateActivity;
import com.globalways.csscli.ui.settings.SettingsActivity;
import com.globalways.csscli.ui.statistics.StatisticsActivity;
import com.globalways.csscli.ui.stock.StockActivity;
import com.globalways.csscli.view.ClearableEditText;

/**
 * UI工具类
 * 
 * @author James.Fan
 *
 */
public class UITools {

	/**
	 * 跳转到主页面
	 * 
	 * @param context
	 */
	public static void jumpMainActivity(Context context) {
		context.startActivity(new Intent(context, MainActivity.class));
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @param context
	 */
	public static void jumpLoginActivity(Context context) {
		context.startActivity(new Intent(context, LoginActivity.class));
	}

	/**
	 * 跳转到商品管理页面
	 * 
	 * @param context
	 */
	public static void jumpProductActivity(Context context) {
		context.startActivity(new Intent(context, ProductActivity.class));
	}

	/**
	 * 跳转到收银台页面
	 * 
	 * @param context
	 */
	public static void jumpCashierActivity(Context context) {
		context.startActivity(new Intent(context, CashierActivity.class));
	}

	/**
	 * 跳转到统计页面
	 * 
	 * @param context
	 */
	public static void jumpStatisticsActivity(Context context) {
		context.startActivity(new Intent(context, StatisticsActivity.class));
	}

	/**
	 * 进货渠道管理
	 * @param context
	 */
	public static void jumpPerchaseActivity(Context context){
		context.startActivity(new Intent(context,PurchaseActivity.class));
	}
	
	/**
	 * 订单管理
	 * @param context
	 */
	public static void jumpOrderActivity(Context context){
		context.startActivity(new Intent(context,OrderActivity.class));
	}
	
	/**
	 * 库存管理
	 * @param context
	 */
	public static void jumpStockActivity(Context context){
		context.startActivity(new Intent(context,StockActivity.class));
	}
	
	/**
	 * 系统设置
	 * @param context
	 */
	public static void jumpSettingsActivity(Context context){
		context.startActivity(new Intent(context,SettingsActivity.class));
	}
	
	/**
	 * 跳转扫码界面，扫描二维码或条形码
	 * 
	 * @param context
	 * @param requestCode
	 * @param operationType
	 *            必须是ProductScanCodeActivity类的内部类OperationType内的值，
	 *            默认值为OperationType.GET_CODE
	 */
	public static void jumpProductScanCodeActivity(Activity activity, int requestCode, int operationType) {
		Intent intent = new Intent(activity, ProductScanCodeActivity.class);
		intent.putExtra(ProductScanCodeActivity.KEY_OPERATION_TYPE, operationType);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 跳转到添加商品界面
	 * 
	 * @param context
	 * @param firstStep
	 *            从商品管理界面跳入时，fristStep=ProductAddNewActivity.ScanStep.INFO_FIRST
	 *            ，productCode=null；
	 *            从扫码界面跳入时，fristStep=ProductAddNewActivity.ScanStep
	 *            .SCAN_FIRST，productCode=扫描结果
	 * @param productCode
	 */
	public static void jumpProductAddNewActivity(Context context, int firstStep, int isExist, String productCode) {
		Intent intent = new Intent(context, ProductAddNewActivity.class);
		intent.putExtra(ProductAddNewActivity.KEY_FIRST_STEP, firstStep);
		if (firstStep == ProductAddNewActivity.ScanStep.SCAN_FIRST) {
			intent.putExtra(ProductAddNewActivity.KEY_PRODUCT_CODE, productCode);
			intent.putExtra(ProductAddNewActivity.KEY_PRODUCT_EXIST, isExist);
		}
		context.startActivity(intent);
	}

	/**
	 * 跳转到app下载界面
	 * 
	 * @param context
	 * @param url
	 *            下载地址
	 * @param version
	 */
	public static void jumpAppUpdateActivity(Context context, String url) {
		Intent intent = new Intent(context, AppUpdateActivity.class);
		intent.putExtra(AppUpdateActivity.KEY_DOWNLOAD_URL, url);
		context.startActivity(intent);
	}

	/**
	 * Toast消息提示
	 * <br>
	 * 超过8个字符则显示时间增加 by wyp
	 * @param context
	 * @param msg
	 */
	public static void ToastMsg(Context context, String msg) {
		if(msg == null || msg.isEmpty())
			return;
		int showingTime = Toast.LENGTH_SHORT;
		if(msg.length()>8)
			showingTime = Toast.LENGTH_LONG;
		Toast.makeText(context, msg, showingTime).show();
	}

	/**
	 * 添加数字限制
	 * 
	 * @param context
	 * @param editText
	 */
	public static void editNumLimit(final ClearableEditText editText) {
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
						editText.setSelection(editText.getText().toString().trim().length());
					}
				}
			}
		});
	}

	/**
	 * 添加数字double限制
	 * 
	 * @param context
	 * @param editText
	 */
	public static void editNumDoubleLimit(final ClearableEditText editText) {
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
					if (text.startsWith("00") || text.startsWith("01") || text.startsWith("02")
							|| text.startsWith("03") || text.startsWith("04") || text.startsWith("05")
							|| text.startsWith("06") || text.startsWith("07") || text.startsWith("08")
							|| text.startsWith("09")) {
						text = text.substring(1);
						editText.setText(text);
						editText.setSelection(editText.getText().toString().trim().length());
					}
					if (text.startsWith(".")) {
						text = text.replace(".", "0.");
						editText.setText(text);
						editText.setSelection(editText.getText().toString().trim().length());
					}
				}
			}
		});
	}
}
