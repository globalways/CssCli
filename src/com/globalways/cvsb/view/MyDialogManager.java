package com.globalways.cvsb.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


import com.alertdialogpro.AlertDialogPro;
import com.alertdialogpro.ProgressDialogPro;
import com.globalways.cvsb.R;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.UITools;

/**
 * 通用对话框管理器
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年5月19日 下午7:01:18
 */
public class MyDialogManager {
	
	/**
	 * common dialog base theme
	 * 
	 */
	private static int mTheme = R.style.CommonDialogTheme;
	
	/**
	 * 创建普通对话框
	 */
	public static AlertDialogPro.Builder builder(Context context)
	{
		return new AlertDialogPro.Builder(context, mTheme);
	}
	
	/**
	 * 创建进程对话框
	 */
	public static AlertDialog buildProgress(Context context)
	{
		return new ProgressDialogPro(context, mTheme);
	}
	
	public static void showNotice(Context context,String msg){
		builder(context).setTitle("提示").setMessage(msg).setPositiveButton("确定", null).create().show();
	}
	
	/**
	 * 收银台编辑商品数量
	 * @param context
	 * @param orignal
	 * @param callBack
	 */
	@SuppressLint("InflateParams")
	public static void showNumEditFloat(final Context context,final CharSequence orignal, final DialogCallback<Float> callBack){
		AlertDialog.Builder builder = builder(context);
		builder.setTitle("编辑数量");
		View dialogView = LayoutInflater.from(context).inflate(R.layout.cashier_list_item_edittextview, null);
		final EditText editText = (EditText) dialogView.findViewById(R.id.editDialogNumber);
		final Button btnDialogLess = (Button) dialogView.findViewById(R.id.btnDialogLess);
		final Button btnDialogAdd = (Button) dialogView.findViewById(R.id.btnDialogAdd);
		editText.setText(orignal);
		editText.setSelection(editText.getText().toString().trim().length());
		
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(android.os.Message msg) {
				if(msg.what == TextWatcherDelay.MSG_WHAT)
				{
					String text = msg.getData().getCharSequence(TextWatcherDelay.DATA).toString().trim();
					if (text != null && !text.isEmpty()) {
						float newValue;
						try {
							newValue = Float.parseFloat(text);
						} catch (NumberFormatException e) {
							UITools.ToastMsg(context, "数字格式错误");
							return;
						}
						if (newValue > 0) {
							editText.setText(text);
						}else editText.setText("1");
					} else {
						editText.setText(orignal);
					}
					btnDialogLess.setEnabled(!editText.getText().toString().trim().equals("1"));
					editText.setSelection(editText.getText().toString().trim().length());
				}
			};
		};
		editText.addTextChangedListener(new TextWatcherDelay(handler, 1000));
		if (editText.getText().toString().trim().equals("1")) {
			btnDialogLess.setEnabled(false);
		}
		btnDialogLess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = editText.getText().toString().trim();
				float newValue = Float.parseFloat(text);
				if (newValue > 1) {
					String str = Tool.subtract(text, "1");
					editText.setText(str);
					editText.setSelection(str.length());
				}
			}
		});
		btnDialogAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newValue = Tool.add(editText.getText().toString().trim(), "1");
				editText.setText(newValue);
				editText.setSelection(newValue.length());
			}
		});
		builder.setView(dialogView);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(editText.getText().toString().isEmpty()){
					return;
				}
				callBack.onValueSet(Float.parseFloat(editText.getText().toString()));
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
	}
	
	/**
	 * 收银台编辑商品数量
	 * @param context
	 * @param orignal
	 * @param callBack
	 */
	@SuppressLint("InflateParams")
	public static void showNumEditInteger(final Context context,final CharSequence orignal, final DialogCallback<Integer> callBack){
		AlertDialog.Builder builder = builder(context);
		builder.setTitle("编辑数量");
		View dialogView = LayoutInflater.from(context).inflate(R.layout.cashier_list_item_edittextview, null);
		final EditText editText = (EditText) dialogView.findViewById(R.id.editDialogNumber);
		//整数不能输入小数点
		editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
		final Button btnDialogLess = (Button) dialogView.findViewById(R.id.btnDialogLess);
		final Button btnDialogAdd = (Button) dialogView.findViewById(R.id.btnDialogAdd);
		editText.setText(orignal);
		editText.setSelection(editText.getText().toString().trim().length());
		
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(android.os.Message msg) {
				if(msg.what == TextWatcherDelay.MSG_WHAT)
				{
					String text = msg.getData().getCharSequence(TextWatcherDelay.DATA).toString().trim();
					if (text != null && !text.isEmpty()) {
						int newValue = Integer.parseInt(text);
						//不可能出现这个错误
//						try {
//						} catch (NumberFormatException e) {
//							UITools.ToastMsg(context, "数字格式错误");
//							return;
//						}
						if (newValue > 0) {
							editText.setText(text);
						}else editText.setText("1");
					} else {
						editText.setText(orignal);
					}
					btnDialogLess.setEnabled(!editText.getText().toString().trim().equals("1"));
					editText.setSelection(editText.getText().toString().trim().length());
				}
			};
		};
		editText.addTextChangedListener(new TextWatcherDelay(handler, 1000));
		if (editText.getText().toString().trim().equals("1")) {
			btnDialogLess.setEnabled(false);
		}
		btnDialogLess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = editText.getText().toString().trim();
				float newValue = Float.parseFloat(text);
				if (newValue > 1) {
					String str = Tool.subtract(text, "1");
					editText.setText(str);
					editText.setSelection(str.length());
				}
			}
		});
		btnDialogAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newValue = Tool.add(editText.getText().toString().trim(), "1");
				editText.setText(newValue);
				editText.setSelection(newValue.length());
			}
		});
		builder.setView(dialogView);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(editText.getText().toString().isEmpty()){
					return;
				}
				callBack.onValueSet(Integer.parseInt(editText.getText().toString()));
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
	}
}
