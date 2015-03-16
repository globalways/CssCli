package com.globalways.csscli.view;

import java.lang.ref.SoftReference;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.tools.MyLog;

/**
 * 进度条
 * 
 * @author James
 *
 */
public class SimpleProgressDialog extends Dialog implements DialogInterface.OnCancelListener {
	private static final String tag = SimpleProgressDialog.class.getSimpleName();

	private SoftReference<Context> mContext = null;
	/** 旋转动画 **/
	private RotateAnimation rotateAnim;
	/** 进度背景图 **/
	private ImageView ivLoadingProgressBg;
	/** 进度显示的文字提示 **/
	private TextView textHint;
	/** 是否是用户手动关闭的 **/
	private boolean manual = true;

	/**
	 * 这里的Context需要使用Activity，因为如果用户手动cancel时，需要finish掉当前界面
	 * 
	 * @param context
	 */
	@SuppressWarnings("static-access")
	public SimpleProgressDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
		mContext = new SoftReference<Context>(context);
		setContentView(R.layout.view_simple_progress_dialog);
		getWindow().getAttributes().gravity = Gravity.CENTER;
		rotateAnim = (RotateAnimation) new AnimationUtils().loadAnimation(mContext.get(), R.anim.center_rotate);
		rotateAnim.setInterpolator(new LinearInterpolator());
		initView();
		setOnCancelListener(this);
	}

	private void initView() {
		ivLoadingProgressBg = (ImageView) findViewById(R.id.ivLoadingProgressBg);
		textHint = (TextView) findViewById(R.id.textHint);
	}

	/**
	 * 进度加载显示的内容
	 * 
	 * @param text
	 * @return
	 */
	public SimpleProgressDialog setText(String text) {
		textHint.setText(text);
		return this;
	}

	/**
	 * 显示进度条
	 */
	public void showDialog() {
		setCanceledOnTouchOutside(false);
		setCancelable(true);
		ivLoadingProgressBg.startAnimation(rotateAnim);
		show();
	}

	/**
	 * 取消进度条
	 */
	public void cancleDialog() {
		manual = false;
		ivLoadingProgressBg.clearAnimation();
		cancel();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		try {
			MyLog.d(tag, "是否是用户手动关闭的" + manual);
			if (manual && null != mContext.get()) {
				((Activity) mContext.get()).finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
