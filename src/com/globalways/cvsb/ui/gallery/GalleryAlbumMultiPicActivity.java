package com.globalways.cvsb.ui.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.cvsb.R;
import com.globalways.cvsb.tools.PicassoImageLoader;
import com.globalways.cvsb.ui.BaseActivity;
import com.globalways.cvsb.view.SimpleProgressDialog;

/**
 * 多张图片选择界面
 * 
 * @author James
 *
 */
public class GalleryAlbumMultiPicActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	private static final String TAG = GalleryAlbumMultiPicActivity.class.getSimpleName();

	private TextView tvCenter;
	private ImageButton imgBtnLeft;

	private GridView gvAlbum;
	private GalleryAlbumEntity imageBucket;
	private GetDataTask mGetDataTask;
	private List<GalleryPicEntity> imageList = null;
	private AlbumAdapter albumAdapter;
	/** 进度条 **/
	private SimpleProgressDialog mSimpleProgressDialog;

	/** 存放已选择的图片key:imageBucketId,value:对应相册所选的图片列表(HashMap) **/
	private HashMap<String, HashMap<Long, GalleryPicEntity>> selectedImageItem = null;
	/** 当前相册中所选的图片列表key:imageId,value:ImageItem **/
	private HashMap<Long, GalleryPicEntity> selectedImageItemList = null;
	/** 图片最大选择数量 **/
	private int mMaxCount = 30;
	/** 其它相册共选择的图片数量 **/
	private int mOtherSelectedImageCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_album_pic_choose_activity);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		getIntentArgs(getIntent());
		initView();
		Log.d(TAG, "onCreate");
	}

	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setVisibility(View.VISIBLE);
		imgBtnLeft.setOnClickListener(this);
		tvCenter = (TextView) findViewById(R.id.textCenter);
		tvCenter.setVisibility(View.VISIBLE);
		gvAlbum = (GridView) findViewById(R.id.gvAlbum);
		gvAlbum.setOnItemClickListener(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getIntentArgs(intent);
	}

	/**
	 * 获取intent参数
	 * 
	 * @param intent
	 */
	@SuppressWarnings("unchecked")
	private void getIntentArgs(Intent intent) {
		imageBucket = (GalleryAlbumEntity) intent.getSerializableExtra(GalleryActivity.KEY_IMAGE_ALBUM);
		selectedImageItem = (HashMap<String, HashMap<Long, GalleryPicEntity>>) intent
				.getSerializableExtra(GalleryActivity.KEY_SELECTED_IMAGE);
	}

	/**
	 * 数据源发生改变，更新Adapter
	 * 
	 * @param list
	 */
	private void dataChanged(List<GalleryPicEntity> list) {
		if (null == albumAdapter) {
			albumAdapter = new AlbumAdapter(this);
			albumAdapter.setData(list);
			gvAlbum.setAdapter(albumAdapter);
		} else {
			albumAdapter.setData(list);
		}
	}

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onResume() {
		super.onResume();
		if (null != imageBucket) {
			selectedImageItemList = selectedImageItem.get(imageBucket.imageBucketId);
			if (null == selectedImageItemList) {
				selectedImageItemList = new HashMap<Long, GalleryPicEntity>();
			}
			if (selectedImageItemList.size() > 0) {
				tvCenter.setText(imageBucket.bucketName + "(" + selectedImageItemList.size() + ")");
			} else {
				tvCenter.setText(imageBucket.bucketName);
			}
		}
		// 计算出总共已选图片数量
		int totalCount = 0;
		for (String key : selectedImageItem.keySet()) {
			totalCount += selectedImageItem.get(key).size();
		}
		mOtherSelectedImageCount = totalCount - selectedImageItemList.size();
		mSimpleProgressDialog = new SimpleProgressDialog(this, true);
		mSimpleProgressDialog.setText("正在加载....").showDialog();
		mGetDataTask = new GetDataTask();
		mGetDataTask.execute(imageBucket.imageBucketId);
		Log.d(TAG, "onResume");
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != imageList && imageList.size() > 0) {
			GalleryPicEntity imageItem = imageList.get(position);
			if (null == imageItem) {
				return;
			}
			if (selectedImageItemList.containsKey(imageItem.imageId)) {
				selectedImageItemList.remove(imageItem.imageId);
			} else {
				if (mOtherSelectedImageCount + selectedImageItemList.size() >= mMaxCount) {
					Toast.makeText(GalleryAlbumMultiPicActivity.this, "图片最多只能选择" + mMaxCount + "张！", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				selectedImageItemList.put(imageItem.imageId, imageItem);
			}
			if (selectedImageItemList.size() > 0) {
				tvCenter.setText(imageBucket.bucketName + "(" + selectedImageItemList.size() + ")");
			} else {
				tvCenter.setText(imageBucket.bucketName);
			}
			AlbumAdapter.ViewHolder holder = (AlbumAdapter.ViewHolder) view.getTag();
			if (null != holder) {
				if (selectedImageItemList.containsKey(imageItem.imageId)) {
					holder.ivSelected.setVisibility(View.VISIBLE);
					holder.ivCover.setVisibility(View.VISIBLE);
					holder.ivCover.getBackground().setAlpha(150);
				} else {
					holder.ivSelected.setVisibility(View.GONE);
					holder.ivCover.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mGetDataTask && AsyncTask.Status.FINISHED != mGetDataTask.getStatus()) {
			mGetDataTask.cancel(true);
		}
	}

	/**
	 * 返回
	 */
	private void goBack() {
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putSerializable(GalleryActivity.KEY_IMAGE_ALBUM, imageBucket);
		extras.putSerializable(GalleryActivity.KEY_SELECTED_IMAGE, selectedImageItemList);
		intent.putExtras(extras);
		setResult(RESULT_OK, intent);
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

	private class GetDataTask extends AsyncTask<String, Void, List<GalleryPicEntity>> {

		@Override
		protected List<GalleryPicEntity> doInBackground(String... params) {
			return GalleryHelper.getInstance().getImageListByBucket(params[0]);
		}

		@Override
		protected void onPostExecute(List<GalleryPicEntity> result) {
			Log.d(TAG, "onPostExecute");
			if (null != mSimpleProgressDialog) {
				mSimpleProgressDialog.cancleDialog();
			}
			if (null != result) {
				imageList = result;
				dataChanged(imageList);
			}
			super.onPostExecute(result);
		}
	}

	private class AlbumAdapter extends BaseAdapter {
		private List<GalleryPicEntity> list = new ArrayList<GalleryPicEntity>();
		private Context context;

		public AlbumAdapter(Context context) {
			this.context = context;
		}

		public void setData(List<GalleryPicEntity> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			GalleryPicEntity imageItem = this.list.get(position);
			if (null == convertView) {
				convertView = LayoutInflater.from(context).inflate(R.layout.gallery_album_pic_list_item, null);
				holder = new ViewHolder();
				findView(holder, convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.ivCover.getBackground().setAlpha(150);
			showData(imageItem, holder);
			return convertView;
		}

		/**
		 * 显示数据
		 * 
		 * @param imageItem
		 * @param holder
		 */
		private void showData(GalleryPicEntity imageItem, ViewHolder holder) {
			if (null != imageItem) {
				// 如果缩略图存在，就直接显示缩略图
				if (null != imageItem.thumbnailPath && !"".equals(imageItem.thumbnailPath)) {
					new PicassoImageLoader(context).showImage(new File(imageItem.thumbnailPath),
							R.drawable.default_pic_photo, R.drawable.default_pic_photo, holder.ivThumbnail);
				} else {// 如果缩略图不存在，就需要生成一张缩略图显示
					if (null != imageItem.imagePath && !"".equals(imageItem.imagePath)
							&& new File(imageItem.imagePath).exists()) {
						new PicassoImageLoader(context).showImage(new File(imageItem.imagePath), 100, 100,
								R.drawable.default_pic_photo, R.drawable.default_pic_photo, holder.ivThumbnail);
					}
				}
				if (selectedImageItemList.containsKey(imageItem.imageId)) {
					holder.ivSelected.setVisibility(View.VISIBLE);
					holder.ivCover.setVisibility(View.VISIBLE);
				} else {
					holder.ivSelected.setVisibility(View.GONE);
					holder.ivCover.setVisibility(View.GONE);
				}
			}
		}

		class ViewHolder {
			ImageView ivThumbnail, ivCover, ivSelected;
		}

		private void findView(ViewHolder holder, View convertView) {
			holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
			holder.ivCover = (ImageView) convertView.findViewById(R.id.ivCover);
			holder.ivSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
		}
	}
}
