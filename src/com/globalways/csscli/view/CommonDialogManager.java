package com.globalways.csscli.view;

import android.app.AlertDialog;
import android.content.Context;

import com.alertdialogpro.AlertDialogPro;
import com.alertdialogpro.ProgressDialogPro;
import com.globalways.csscli.R;

/**
 * 通用对话框管理器
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年5月19日 下午7:01:18
 */
public class CommonDialogManager {
	
	/**
	 * common dialog base theme
	 * 
	 */
	private static int mTheme = R.style.CommonDialogTheme;
	
	/**
	 * create common dialog
	 */
	public static AlertDialogPro.Builder createDialogBuilder(Context context)
	{
		return new AlertDialogPro.Builder(context, mTheme);
	}
	
	/**
	 * common progress dialog
	 */
	public static AlertDialog createProgressDialog(Context context)
	{
		return new ProgressDialogPro(context, mTheme);
	}
}