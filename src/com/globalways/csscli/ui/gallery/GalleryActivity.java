package com.globalways.csscli.ui.gallery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.tools.MyLog;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.BaseActivity;
import com.globalways.csscli.view.SimpleProgressDialog;

/**
 * 相册
 * 
 * @author James
 *
 */
public class GalleryActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	private static final String TAG = GalleryActivity.class.getSimpleName();

	private TextView textleft, textCenter, textRight;
	private GridView gridViewGallery;
	private GalleryAlbumAdapter mGalleryAdapter;
	private List<GalleryAlbumEntity> imageBucketList = null;
	private GetDataTask mGetDataTask;
	/** 进度条 **/
	private SimpleProgressDialog mSimpleProgressDialog;

	/** 存放已选择的图片key:imageBucketId,value:对应相册所选的图片列表 **/
	private HashMap<String, HashMap<Long, GalleryPicEntity>> selectedImageItem = new HashMap<String, HashMap<Long, GalleryPicEntity>>();
	private int mPurpose = Purpose.SIGNE_PIC;

	/** 跳转到相册的目的 */
	public static class Purpose {
		/** 获取多张照片 **/
		public static final int MULTI_PIC = 1;
		/** 获取单张照片 **/
		public static final int SIGNE_PIC = 2;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_activity);
		getIntentArgs(getIntent());
		initView();
		initListener();
	}

	private void initView() {
		textleft = (TextView) findViewById(R.id.textleft);
		textleft.setText("取消");
		textleft.setVisibility(View.VISIBLE);
		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("相册");
		textCenter.setVisibility(View.VISIBLE);
		textRight = (TextView) findViewById(R.id.textRight);
		textRight.setText("确定");
		switch (mPurpose) {
		case Purpose.MULTI_PIC:
			textRight.setVisibility(View.VISIBLE);
			break;
		}
		gridViewGallery = (GridView) findViewById(R.id.gvGallery);
	}

	private void initListener() {
		textleft.setOnClickListener(this);
		textRight.setOnClickListener(this);
		gridViewGallery.setOnItemClickListener(this);
	}

	/**
	 * 获取Intent参数
	 * 
	 * @param intent
	 */
	@SuppressLint("UseSparseArrays")
	@SuppressWarnings("unchecked")
	private void getIntentArgs(Intent intent) {
		if (null != intent) {
			mPurpose = intent.getIntExtra("purpose", Purpose.SIGNE_PIC);
			switch (mPurpose) {
			case Purpose.MULTI_PIC:
				ArrayList<GalleryPicEntity> selectedImageList = (ArrayList<GalleryPicEntity>) intent
						.getSerializableExtra("selectedImageList");
				if (null != selectedImageList && selectedImageList.size() > 0) {
					for (int i = 0; i < selectedImageList.size(); i++) {
						GalleryPicEntity ii = selectedImageList.get(i);
						if (null != ii && null != ii.imageBucketId) {
							// 存放已选择的图片key:imageBucketId,value:对应相册所选的图片列表
							// HashMap<String, HashMap<Long,ImageItem>>
							// selectedImageItem
							HashMap<Long, GalleryPicEntity> selectedBucketImgs = selectedImageItem.get(ii.imageBucketId);
							if (null == selectedBucketImgs) {
								selectedBucketImgs = new HashMap<Long, GalleryPicEntity>();
							}
							selectedBucketImgs.put(ii.imageId, ii);
							selectedImageItem.put(ii.imageBucketId, selectedBucketImgs);
						}
					}
				}
				break;
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getIntentArgs(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// UIPage.CURRENT_ACTIVITY = UIPage.GalleryActivity;
		mSimpleProgressDialog = new SimpleProgressDialog(this, true);
		mSimpleProgressDialog.setText("正在加载....").showDialog();
		mGetDataTask = new GetDataTask();
		mGetDataTask.execute();
	}

	/**
	 * 数据源发生改变，更新Adapter
	 * 
	 * @param list
	 */
	private void dataChanged(List<GalleryAlbumEntity> list) {
		if (null == mGalleryAdapter) {
			mGalleryAdapter = new GalleryAlbumAdapter(this);
			mGalleryAdapter.setData(list, selectedImageItem);
			gridViewGallery.setAdapter(mGalleryAdapter);
		} else {
			mGalleryAdapter.setData(list, selectedImageItem);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mGetDataTask && AsyncTask.Status.FINISHED != mGetDataTask.getStatus()) {
			mGetDataTask.cancel(true);
		}
	}

	private void goBack() {
		finish();
	}

	private void ok() {
		MyLog.d(TAG, "确认图片选择");
		if (getSelectedImageCount() > 0) {
			ArrayList<GalleryPicEntity> selectedImageList = new ArrayList<GalleryPicEntity>();
			for (String key : selectedImageItem.keySet()) {
				HashMap<Long, GalleryPicEntity> imageItems = selectedImageItem.get(key);
				for (Long imageId : imageItems.keySet()) {
					selectedImageList.add(imageItems.get(imageId));
				}
			}
			Intent intent = new Intent();
			Bundle extras = new Bundle();
			extras.putSerializable("selectedImageList", selectedImageList);
			intent.putExtras(extras);
			setResult(Tool.ResultCode.OK, intent);
			finish();
		} else {
			Toast.makeText(this, "请选择图片", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textleft:
			goBack();
			break;
		case R.id.textRight:
			ok();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != imageBucketList && imageBucketList.size() > 0) {
			GalleryAlbumEntity imageBucket = imageBucketList.get(position);
			if (null != imageBucket) {
				Intent intent = null;
				Bundle extras = null;
				switch (mPurpose) {
				case Purpose.MULTI_PIC:
					intent = new Intent(GalleryActivity.this, GalleryAlbumMultiPicActivity.class);
					extras = new Bundle();
					extras.putSerializable("ImageBucket", imageBucket);
					extras.putSerializable("selectedImageItem", selectedImageItem);
					intent.putExtras(extras);
					startActivityForResult(intent, RequestCode.CHOOSE_IMG);
					break;
				case Purpose.SIGNE_PIC:
					intent = new Intent(GalleryActivity.this, GalleryAlbumSiglePicActivity.class);
					extras = new Bundle();
					extras.putSerializable("ImageBucket", imageBucket);
					intent.putExtras(extras);
					startActivity(intent);
					break;
				}
			}
		}
	}

	/**
	 * 获取已选择图片总数
	 * 
	 * @return
	 */
	private int getSelectedImageCount() {
		int count = 0;
		for (String key : selectedImageItem.keySet()) {
			count += selectedImageItem.get(key).size();
		}
		return count;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MyLog.d(TAG, "[requestCode,resultCode]=[" + requestCode + "," + resultCode + "]");
		if (RequestCode.CHOOSE_IMG == requestCode) {
			if (Tool.ResultCode.OK == resultCode) {
				GalleryAlbumEntity imageBucket = (GalleryAlbumEntity) data.getSerializableExtra("ImageBucket");
				@SuppressWarnings("unchecked")
				HashMap<Long, GalleryPicEntity> selectedImageItemList = (HashMap<Long, GalleryPicEntity>) data
						.getSerializableExtra("selectedImageItemList");
				if (null != selectedImageItemList && selectedImageItemList.size() > 0) {
					selectedImageItem.put(imageBucket.imageBucketId, selectedImageItemList);
				} else {
					selectedImageItem.remove(imageBucket.imageBucketId);
				}
				dataChanged(imageBucketList);
				int imageSelectedCount = getSelectedImageCount();
				if (imageSelectedCount > 0) {
					textCenter.setText("相册(" + imageSelectedCount + ")");
				} else {
					textCenter.setText("相册");
				}
			}
		}
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

	class RequestCode {
		/** 选择图片 **/
		private static final int CHOOSE_IMG = 1;
	}

	/**
	 * 加载本地相册资源
	 */
	private class GetDataTask extends AsyncTask<Void, Void, List<GalleryAlbumEntity>> {

		@Override
		protected void onPostExecute(List<GalleryAlbumEntity> result) {
			if (null != mSimpleProgressDialog) {
				mSimpleProgressDialog.cancleDialog();
			}
			if (null != result) {
				imageBucketList = result;
				dataChanged(imageBucketList);
				MyLog.d(TAG, "获取的相册列表数量：" + result.size());
			}
			super.onPostExecute(result);
		}

		@Override
		protected List<GalleryAlbumEntity> doInBackground(Void... params) {
			return GalleryHelper.getInstance().getImageBucket();
		}
	}
}
