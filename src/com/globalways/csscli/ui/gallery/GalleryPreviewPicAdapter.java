package com.globalways.csscli.ui.gallery;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.globalways.csscli.R;
import com.globalways.csscli.tools.PicassoImageLoader;

/**
 * 相册照片预览Adapter
 * 
 * @author James
 *
 */
public class GalleryPreviewPicAdapter extends PagerAdapter {
	private static final String TAG = GalleryPreviewPicAdapter.class.getSimpleName();

	private SoftReference<Context> mContext = null;
	private List<String> list;

	public GalleryPreviewPicAdapter(Context context) {
		mContext = new SoftReference<Context>(context);
	}

	public void setData(List<String> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	/**
	 * 检查图片大小，如果过大，就进行压缩
	 * 
	 * @param imagePath
	 * @param targetWH
	 * @return 如果需要压缩，返回true，否则返回false
	 */
	private boolean checkImageSize(String imagePath, int[] targetWH) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		BitmapFactory.decodeFile(imagePath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		if (w > 1024 || h > 1024) {
			Log.d(TAG, "需要压缩的图片:" + imagePath + "|[w,h]=[" + w + "," + h + "]");
			float scale = 0f;
			float scaleX = 1024 / (float) w;
			float scaleY = 1024 / (float) h;
			scale = scaleX > scaleY ? scaleY : scaleX;

			int targetWidth = (int) (w * scale);
			int targetHeight = (int) (h * scale);
			targetWH[0] = targetWidth;
			targetWH[1] = targetHeight;
			return true;
		}
		return false;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		String imagePath = list.get(position);
		PhotoView photoView = null;
		Log.d(TAG, "图片地址:" + imagePath);
		if (null != imagePath && new File(imagePath).exists()) {
			photoView = new PhotoView(mContext.get());
			int targetWH[] = new int[2];
			if (checkImageSize(imagePath, targetWH)) {
				new PicassoImageLoader(mContext.get()).showImage(new File(imagePath), targetWH[0], targetWH[1],
						R.drawable.default_pic_photo, R.drawable.default_pic_photo, photoView);
			} else {
				new PicassoImageLoader(mContext.get()).showImage(new File(imagePath), R.drawable.default_pic_photo,
						R.drawable.default_pic_photo, photoView);
			}
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public int getCount() {
		return list.size();
	}
}
