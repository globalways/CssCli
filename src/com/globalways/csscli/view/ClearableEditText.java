package com.globalways.csscli.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
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
 * @since 6.16  增加前缀 by wyp
 */
public class ClearableEditText extends EditText implements TextWatcher, android.view.View.OnTouchListener {

	final String tag = "ViewClearableSerchEditText";
	
	private String mPrefixContent = "";
	private String mSubfixContent = "";
	private int mPrefixColor = -1;
	private Rect mPrefixRect = new Rect();
	private Rect mSubfixRect = new Rect();
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
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(!mPrefixContent.equals(""))
		{
			getPaint().getTextBounds(mPrefixContent, 0, mPrefixContent.length(), mPrefixRect);
			mPrefixRect.right += getPaint().measureText(" ");
		}
		if(!mSubfixContent.isEmpty())
		{
			getPaint().getTextBounds(mSubfixContent, 0, mSubfixContent.length(), mSubfixRect);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(!mPrefixContent.equals(""))
		{
			if(mPrefixColor != -1)
				getPaint().setColor(mPrefixColor);
			canvas.drawText(mPrefixContent, super.getCompoundPaddingLeft(), getBaseline(), getPaint());
		}
		if(!mSubfixContent.isEmpty()){
			float xSubfix = getPaddingLeft() + getPaint().measureText(getText().toString() + mPrefixContent +" ");
			canvas.drawText(mSubfixContent, xSubfix, getBaseline(), getPaint());
		}
	}
	
	@Override
	public int getCompoundPaddingLeft() {
		return super.getCompoundPaddingLeft() + mPrefixRect.width();
	}
	
	public void setPrefix(String prefix)
	{
		this.mPrefixContent = prefix;
	}
	public void setPrefixColor(int color)
	{
		this.mPrefixColor = color;
	}
	
	public void setSubfix(String subfix)
	{
		this.mSubfixContent = subfix;
	}
	
}
