package com.globalways.csscli.ui.gallery;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.BaseActivity;

/**
 * 图片预览界面
 * 
 * @author James
 *
 */
public class GalleryPicPreviewActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {
	private static final String TAG = GalleryPicPreviewActivity.class.getSimpleName();

	/** 已经选择的照片 */
	public static final String KEY_SELECTED_IMAGE = "SelectImage";
	/** 当前是已经选择的照片中的第几个 */
	public static final String KEY_SELECTED_INDEX = "SelectedIndex";

	private TextView tvCenter, textIndex;
	private ImageButton imgBtnLeft;
	private GalleryPreviewPicAdapter mImagePagerAdapter;
	private MyViewPager myViewPage;
	/** 预览图片地址集合 **/
	private ArrayList<String> imagePathList = null;
	private int initIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_image_list_preview_activity);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
		getIntentArgs(getIntent());
	}

	private void initView() {
		textIndex = (TextView) findViewById(R.id.textIndex);
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setVisibility(View.VISIBLE);
		imgBtnLeft.setOnClickListener(this);

		tvCenter = (TextView) findViewById(R.id.textCenter);
		tvCenter.setText("图片预览");
		tvCenter.setVisibility(View.VISIBLE);

		myViewPage = (MyViewPager) findViewById(R.id.myViewPage);
		myViewPage.setOnPageChangeListener(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getIntentArgs(intent);
	}

	/**
	 * 获取Intent参数
	 * 
	 * @param intent
	 */
	@SuppressWarnings("unchecked")
	private void getIntentArgs(Intent intent) {
		if (null != intent) {
			imagePathList = (ArrayList<String>) intent.getSerializableExtra(KEY_SELECTED_IMAGE);
			initIndex = intent.getIntExtra(KEY_SELECTED_INDEX, 0);
			if (null != imagePathList && imagePathList.size() > 0) {
				Log.d(TAG, "图片列表：" + imagePathList.size());
				dataChanged(imagePathList);
			}
		}
	}

	/**
	 * 数据源发生改变，更新Adapter
	 * 
	 * @param list
	 */
	private void dataChanged(ArrayList<String> list) {
		if (null == mImagePagerAdapter) {
			mImagePagerAdapter = new GalleryPreviewPicAdapter(this);
			mImagePagerAdapter.setData(list);
			myViewPage.setAdapter(mImagePagerAdapter);
		} else {
			mImagePagerAdapter.setData(list);
		}
		myViewPage.setCurrentItem(initIndex);
		textIndex.setText((initIndex + 1) + "/" + list.size());
	}

	/**
	 * 返回
	 */
	private void goBack() {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 点击了返回按键
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			goBack();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnLeft:
			goBack();
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		Log.d(TAG, "onPageSelected__arg0 = " + arg0);
		if (null != imagePathList) {
			textIndex.setText((arg0 + 1) + "/" + imagePathList.size());
		}
	}
}
