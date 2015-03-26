package com.globalways.csscli.ui;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.globalways.csscli.ui.account.LoginActivity;
import com.globalways.csscli.ui.cashier.CashierActivity;
import com.globalways.csscli.ui.main.MainActivity;
import com.globalways.csscli.ui.product.ProductActivity;

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
}
