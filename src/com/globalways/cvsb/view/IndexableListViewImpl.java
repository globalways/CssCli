package com.globalways.cvsb.view;

import com.globalways.cvsb.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class IndexableListViewImpl extends IndexableListView
{

	public IndexableListViewImpl(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void createScroller()
	{

		mScroller = new IndexScroller(getContext(), this);

		mScroller.setAutoHide(autoHide);

		// style 1
//		 mScroller.setShowIndexContainer(false);
//		 mScroller.setIndexPaintColor(Color.argb(255, 49, 64, 91));

		// style 2
		mScroller.setShowIndexContainer(true);
		mScroller.setIndexPaintColor(getResources().getColor(R.color.base_blue_1d90f4));
		mScroller.setIndexbarContainerBgColor(Color.WHITE);

		if (autoHide)
			mScroller.hide();
		else
			mScroller.show();

	}
}
