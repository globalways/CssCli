package com.globalways.csscli.view;

import java.lang.ref.SoftReference;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.view.BottomMenuItemView.MyOnClickListener;
import com.globalways.csscli.view.MenuItemEntity.Type;

/**
 * 底部弹出菜单列表
 * 
 * @author James.Fan
 */
public class BottomMenuDialog<T> extends Dialog implements android.view.View.OnClickListener, AnimationListener {
	private static final String TAG = BottomMenuDialog.class.getSimpleName();
	private SoftReference<Context> mContext = null;
	private TextView tvCancel, textAlert;
	private LinearLayout llMenuContainer, llMain;
	Animation translateAnimIn, translateAnimOut;
	/** 单项点击事件监听 **/
	private OnItemClickListener<T> mOnItemClickListener;
	private List<MenuItemEntity> menuList;

	public BottomMenuDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
		mContext = new SoftReference<Context>(context);
		setContentView(R.layout.popupwindow_bottom_menu_main);
		LayoutParams lp = getWindow().getAttributes();
		lp.gravity = Gravity.BOTTOM;
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.MATCH_PARENT;
		initView();
		initListener();
	}

	private void initView() {
		textAlert = (TextView) findViewById(R.id.textAlert);
		tvCancel = (TextView) findViewById(R.id.tvCancel);
		llMenuContainer = (LinearLayout) findViewById(R.id.llMenuContainer);
		llMain = (LinearLayout) findViewById(R.id.llMain);
		translateAnimIn = AnimationUtils.loadAnimation(mContext.get(), R.anim.menu_btn_set_in);
		translateAnimIn.setFillAfter(true);
		translateAnimOut = AnimationUtils.loadAnimation(mContext.get(), R.anim.menu_btn_set_out);
		translateAnimOut.setFillAfter(true);
		translateAnimOut.setAnimationListener(this);
	}

	private void initListener() {
		tvCancel.setOnClickListener(this);
		textAlert.setOnClickListener(this);
	}

	/**
	 * 设置显示内容
	 * 
	 * @param text
	 */
	public void setTextAlert(CharSequence text) {
		textAlert.setText(text);
	}

	/**
	 * 设置字体颜色
	 * 
	 * @param colorResId
	 *            颜色资源ID
	 */
	public void setTextAlertColor(int colorResId) {
		textAlert.setTextColor(mContext.get().getResources().getColor(colorResId));
	}

	/**
	 * 设置菜单项点击事件
	 * 
	 * @param onItemClickListener
	 */
	public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	/**
	 * 设置菜单列表
	 * 
	 * @param menuList
	 */
	public void setMenuList(List<MenuItemEntity> menuList) {
		this.menuList = menuList;
	}

	/**
	 * 取消菜单Dialog
	 */
	private void cancleDialog() {
		llMain.clearAnimation();
		llMain.setAnimation(translateAnimOut);
		llMain.startAnimation(translateAnimOut);
	}

	/**
	 * 显示弹窗列表界面
	 */
	public void showDialog(final T prams) {
		llMenuContainer.removeAllViews();
		if (null != menuList && menuList.size() > 0) {
			int startIndex = 0, endIndex = 0;
			if (menuList.size() == 1) {
				final MenuItemEntity menuItem = menuList.get(0);
				llMenuContainer.addView(new BottomMenuItemView(mContext.get()).setMenuEntity(menuItem)
						.setType(Type.SINGLE).setMyOnClickListener(new MyOnClickListener() {
							@Override
							public void onClick() {
								if (null != mOnItemClickListener) {
									mOnItemClickListener.onItemClick(menuItem.menuId, prams);
									cancel();
								}
							}
						}));
			} else {
				startIndex = 0;
				endIndex = menuList.size() - 1;
				for (int i = 0; i < menuList.size(); i++) {
					final MenuItemEntity menuItem = menuList.get(i);
					if (i == startIndex) {
						llMenuContainer.addView(new BottomMenuItemView(mContext.get()).setMenuEntity(menuItem)
								.setType(Type.TOP).setMyOnClickListener(new MyOnClickListener() {
									@Override
									public void onClick() {
										if (null != mOnItemClickListener) {
											mOnItemClickListener.onItemClick(menuItem.menuId, prams);
											cancel();
										}
									}
								}));
					} else if (i == endIndex) {
						llMenuContainer.addView(new BottomMenuItemView(mContext.get()).setMenuEntity(menuItem)
								.setType(Type.BOTTOM).setMyOnClickListener(new MyOnClickListener() {
									@Override
									public void onClick() {
										if (null != mOnItemClickListener) {
											mOnItemClickListener.onItemClick(menuItem.menuId, prams);
											cancel();
										}
									}
								}));
					} else {
						llMenuContainer.addView(new BottomMenuItemView(mContext.get()).setMenuEntity(menuItem)
								.setType(Type.MIDDLE).setMyOnClickListener(new MyOnClickListener() {
									@Override
									public void onClick() {
										if (null != mOnItemClickListener) {
											mOnItemClickListener.onItemClick(menuItem.menuId, prams);
											cancel();
										}
									}
								}));
					}
				}
			}
		}
		setCanceledOnTouchOutside(true);
		setCancelable(true);
		llMain.clearAnimation();
		llMain.setAnimation(translateAnimIn);
		llMain.startAnimation(translateAnimIn);
		show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvCancel:
			cancleDialog();
			break;
		case R.id.textAlert:
			cancleDialog();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "keyCode = " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancleDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public interface OnItemClickListener<T> {
		/**
		 * 菜单列表单项点击事件回调
		 * 
		 * @param menuId
		 *            菜单ID
		 * @param prams
		 *            创建菜单时传递的参数
		 */
		public void onItemClick(int menuId, T prams);
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		Log.d(TAG, "退出动画已结束");
		cancel();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}
}
