package com.globalways.csscli.ui.gallery;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.BaseActivity;
import com.globalways.csscli.view.SimpleProgressDialog;

/**
 * 单张图片选择界面
 * 
 * @author Administrator
 *
 */
public class GalleryAlbumSiglePicActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	private static final String tag = GalleryAlbumSiglePicActivity.class.getSimpleName();
	private GalleryAlbumEntity imageBucket;
	private GridView gvAlbum;
	private TextView tvleft, tvCenter;
	private SimpleProgressDialog mSimpleProgressDialog;
	private List<GalleryPicEntity> imageList = null;
	private GalleryAlbumPicAdapter albumAdapter;
	private GetDataTask mGetDataTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_album_pic_choose_activity);
		getIntentAgrs(getIntent());
		initView();
		initListener();
	}

	private void initView() {
		gvAlbum = (GridView) findViewById(R.id.gvAlbum);
		tvleft = (TextView) findViewById(R.id.textleft);
		tvleft.setText("返回");
		tvleft.setVisibility(View.VISIBLE);
		tvCenter = (TextView) findViewById(R.id.textCenter);
		tvCenter.setText("选择头像");
		tvCenter.setVisibility(View.VISIBLE);
	}

	private void initListener() {
		gvAlbum.setOnItemClickListener(this);
		tvleft.setOnClickListener(this);
	}

	private void getIntentAgrs(Intent intent) {
		imageBucket = (GalleryAlbumEntity) intent.getSerializableExtra("ImageBucket");
	}

	/**
	 * 数据源发生改变，更新Adapter
	 * 
	 * @param list
	 */
	private void dataChanged(List<GalleryPicEntity> list) {
		if (null == albumAdapter) {
			albumAdapter = new GalleryAlbumPicAdapter(this);
			albumAdapter.setData(list);
			gvAlbum.setAdapter(albumAdapter);
		} else {
			albumAdapter.setData(list);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getIntentAgrs(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// UIPage.CURRENT_ACTIVITY = UIPage.AlbumSiglePicChooseActivity;
		if (null != imageBucket) {
			mSimpleProgressDialog = new SimpleProgressDialog(this, true);
			mSimpleProgressDialog.setText("正在加载....").showDialog();
			mGetDataTask = new GetDataTask();
			mGetDataTask.execute(imageBucket.imageBucketId);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// UIPage.LAST_ACTIVITY = UIPage.AlbumSiglePicChooseActivity;
	}

	private void goBack() {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textleft:
			goBack();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mGetDataTask && AsyncTask.Status.FINISHED != mGetDataTask.getStatus()) {
			mGetDataTask.cancel(true);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		GalleryPicEntity imageItem = imageList.get(position);
		if (null == imageItem) {
			return;
		}
		if (null != imageItem.imagePath && !"".equals(imageItem.imagePath) && new File(imageItem.imagePath).exists()) {
//			Intent intent = new Intent(this, FaceImgCutterActivity.class);
//			intent.putExtra("imagePath", imageItem.imagePath);
//			startActivity(intent);
		} else {
			Toast.makeText(this, "对不起，所选图片不存在！", Toast.LENGTH_LONG).show();
		}
	}

	private class GetDataTask extends AsyncTask<String, Void, List<GalleryPicEntity>> {

		@Override
		protected List<GalleryPicEntity> doInBackground(String... params) {
			return GalleryHelper.getInstance().getImageListByBucket(params[0]);
		}

		@Override
		protected void onPostExecute(List<GalleryPicEntity> result) {
			Log.d(tag, "onPostExecute");
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
}
