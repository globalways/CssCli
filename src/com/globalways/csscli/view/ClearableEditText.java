package com.globalways.csscli.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.globalways.csscli.R;

/**
 * 有清空按钮的查询搜索输入框
 * 
 * @author James
 * 
 */
public class ClearableEditText extends EditText implements TextWatcher, android.view.View.OnTouchListener {

	final String tag = "ViewClearableSerchEditText";
	// 清空按钮
	final Drawable clearButton = getResources().getDrawable(R.drawable.ic_clear_text);

	public ClearableEditText(Context context) {
		this(context, null);
		init();
	}

	public ClearableEditText(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.editTextStyle);
		init();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void init() {
		setOnTouchListener(this);
		clearButton.setBounds(0, 0, clearButton.getIntrinsicWidth(), clearButton.getIntrinsicHeight());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		setClearBtnVisible(s.length() > 0);
	}

	/**
	 * 设置清空按钮显示或隐藏
	 * 
	 * @param visible
	 */
	private void setClearBtnVisible(boolean visible) {
		if (visible) {
			setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], clearButton,
					getCompoundDrawables()[3]);
		} else {
			setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], null, getCompoundDrawables()[3]);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Is there an X showing?
		if (getCompoundDrawables()[2] == null) {
			return false;
		}
		// Only do this for up touches
		if (event.getAction() != MotionEvent.ACTION_UP) {
			return false;
		}
		// Is touch on our clear button?
		if (event.getX() > getWidth() - getPaddingRight() - clearButton.getIntrinsicWidth() - 15) {
			setText("");
			setClearBtnVisible(false);
			return true;
		}
		return false;
	}
}
