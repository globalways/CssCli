package com.globalways.csscli.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.globalways.csscli.ui.account.LoginActivity;
import com.globalways.csscli.ui.cashier.CashierActivity;
import com.globalways.csscli.ui.main.MainActivity;
import com.globalways.csscli.ui.product.ProductActivity;
import com.globalways.csscli.view.ClearableEditText;

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
	 * Toast消息提示
	 * 
	 * @param context
	 * @param msg
	 */
	public static void ToastMsg(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
