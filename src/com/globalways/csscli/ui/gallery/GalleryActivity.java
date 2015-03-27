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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.tools.MyLog;
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

	/** 选择多张还是单张照片 */
	public static final String KEY_JUMP_PURPOSE = "Purpose";
	/** 已经选择的照片 */
	public static final String KEY_SELECTED_IMAGE = "SelectImage";
	/** 相册 */
	public static final String KEY_IMAGE_ALBUM = "ImageAblum";
	/** 选择照片的requestCode */
	private static final int CODE_SELECT_IMAGE = 10;

	private TextView textCenter;
	private ImageButton imgBtnLeft, imgBtnRight;

	private GridView gridViewGallery;
	private GalleryAlbumAdapter mGalleryAdapter;
	private List<GalleryAlbumEntity> imageBucketList = null;
	private GetDataTask mGetDataTask;
	/** 进度条 **/
	private SimpleProgressDialog mSimpleProgressDialog;

	/** 存放已选择的图片key:imageBucketId,value:对应相册所选的图片列表 **/
	private HashMap<String, HashMap<Long, GalleryPicEntity>> selectedImageItem = new HashMap<String, HashMap<Long, GalleryPicEntity>>();
	private Purpose mPurpose = Purpose.SIGNE_PIC;

	/** 跳转到相册的目的 */
	public enum Purpose {
		/** 获取多张照片 **/
		MULTI_PIC(1),
		/** 获取单张照片 **/
		SIGNE_PIC(2);
		private Purpose(int type) {
			this.type = type;
		}

		private int type;

		public int getType() {
			return type;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_activity);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		getIntentArgs(getIntent());
		initView();
	}

	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setOnClickListener(this);
		imgBtnLeft.setVisibility(View.VISIBLE);
		
		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("相册");
		
		imgBtnRight = (ImageButton) findViewById(R.id.imgBtnRight);
		imgBtnRight.setOnClickListener(this);
		imgBtnRight.setVisibility(View.VISIBLE);
		
		imgBtnRight.setVisibility(mPurpose == Purpose.MULTI_PIC ? View.VISIBLE : View.GONE);
		gridViewGallery = (GridView) findViewById(R.id.gvGallery);
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
			mPurpose = intent.getIntExtra(KEY_JUMP_PURPOSE, Purpose.SIGNE_PIC.getType()) == Purpose.SIGNE_PIC.getType() ? Purpose.SIGNE_PIC
					: Purpose.MULTI_PIC;
			switch (mPurpose) {
			case MULTI_PIC:
				List<GalleryPicEntity> selectedImageList = (ArrayList<GalleryPicEntity>) intent
						.getSerializableExtra(KEY_SELECTED_IMAGE);
				if (null != selectedImageList && selectedImageList.size() > 0) {
					for (int i = 0; i < selectedImageList.size(); i++) {
						GalleryPicEntity j = selectedImageList.get(i);
						if (null != j && null != j.imageBucketId) {
							// 存放已选择的图片key:imageBucketId,value:对应相册所选的图片列表
							// HashMap<String, HashMap<Long,ImageItem>>
							// selectedImageItem
							HashMap<Long, GalleryPicEntity> selectedBucketImgs = selectedImageItem.get(j.imageBucketId);
							if (null == selectedBucketImgs) {
								selectedBucketImgs = new HashMap<Long, GalleryPicEntity>();
							}
							selectedBucketImgs.put(j.imageId, j);
							selectedImageItem.put(j.imageBucketId, selectedBucketImgs);
						}
					}
				}
				break;
			case SIGNE_PIC:
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
			extras.putSerializable(KEY_SELECTED_IMAGE, selectedImageList);
			intent.putExtras(extras);
			setResult(RESULT_OK, intent);
			finish();
		} else {
			Toast.makeText(this, "请选择图片", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnLeft:
			goBack();
			break;
		case R.id.imgBtnRight:
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
				case MULTI_PIC:
					intent = new Intent(GalleryActivity.this, GalleryAlbumMultiPicActivity.class);
					extras = new Bundle();
					extras.putSerializable(KEY_IMAGE_ALBUM, imageBucket);
					extras.putSerializable(KEY_SELECTED_IMAGE, selectedImageItem);
					intent.putExtras(extras);
					startActivityForResult(intent, CODE_SELECT_IMAGE);
					break;
				case SIGNE_PIC:
					intent = new Intent(GalleryActivity.this, GalleryAlbumSiglePicActivity.class);
					extras = new Bundle();
					extras.putSerializable(KEY_IMAGE_ALBUM, imageBucket);
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
		MyLog.d(TAG, "[requestCode, resultCode]=[" + requestCode + "," + resultCode + "]");
		if (CODE_SELECT_IMAGE == requestCode) {
			if (RESULT_OK == resultCode) {
				GalleryAlbumEntity imageBucket = (GalleryAlbumEntity) data.getSerializableExtra(KEY_IMAGE_ALBUM);
				@SuppressWarnings("unchecked")
				HashMap<Long, GalleryPicEntity> selectedImageItemList = (HashMap<Long, GalleryPicEntity>) data
						.getSerializableExtra(KEY_SELECTED_IMAGE);
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
