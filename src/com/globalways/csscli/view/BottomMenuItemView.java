package com.globalways.csscli.view;

import java.lang.ref.SoftReference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.view.MenuItemEntity.Type;

/**
 * 底部弹出菜单单项布局
 * 
 * @author James.Fan
 *
 */
public class BottomMenuItemView extends LinearLayout {
	private TextView textMenu, textBottomLine;
	private MenuItemEntity mMenuItemEntity;
	private MyOnClickListener mMyOnClickListener;
	private SoftReference<Context> mContext = null;

	public BottomMenuItemView(Context context) {
		super(context);
		initView(context);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mMyOnClickListener) {
					mMyOnClickListener.onClick();
				}
			}
		});
	}

	public BottomMenuItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void initView(Context context) {
		mContext = new SoftReference<Context>(context);
		LayoutInflater.from(context).inflate(R.layout.popupwindow_bottom_menu_item, this, true);
		textMenu = (TextView) findViewById(R.id.textMenu);
		textBottomLine = (TextView) findViewById(R.id.textBottomLine);
	}

	public BottomMenuItemView setMyOnClickListener(MyOnClickListener myOnClickListener) {
		this.mMyOnClickListener = myOnClickListener;
		return this;
	}

	/**
	 * 设置MenuItemEntity
	 * 
	 * @param menuItemEntity
	 */
	public BottomMenuItemView setMenuEntity(MenuItemEntity menuItemEntity) {
		if (null != menuItemEntity) {
			mMenuItemEntity = menuItemEntity;
			textMenu.setText(mMenuItemEntity.menuText);
			textMenu.setTextColor(mContext.get().getResources().getColor(menuItemEntity.color.getColorId()));
		}
		return this;
	}

	public BottomMenuItemView setType(Type type) {
		switch (type) {
		case TOP:
			textMenu.setBackgroundResource(R.color.selector_menu_btn_top_bg);
			textBottomLine.setVisibility(View.VISIBLE);
			break;
		case MIDDLE:
			textMenu.setBackgroundResource(R.color.selector_menu_btn_middle_bg);
			textBottomLine.setVisibility(View.VISIBLE);
			break;
		case BOTTOM:
			textMenu.setBackgroundResource(R.color.selector_menu_btn_bottom_bg);
			textBottomLine.setVisibility(View.GONE);
			break;
		case SINGLE:
			textMenu.setBackgroundResource(R.color.selector_menu_btn_single_bg);
			textBottomLine.setVisibility(View.GONE);
			break;
		}
		return this;
	}

	public interface MyOnClickListener {
		public void onClick();
	}
}
