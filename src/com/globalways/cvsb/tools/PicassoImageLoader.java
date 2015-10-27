package com.globalways.cvsb.tools;

import java.io.File;
import java.lang.ref.SoftReference;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Picasso图片加载器
 * 
 * @author James
 *
 */
public class PicassoImageLoader implements ImageLoaderDao {
	private SoftReference<Context> mContext = null;

	public PicassoImageLoader(Context context) {
		mContext = new SoftReference<Context>(context);
	}

	@Override
	public void showImage(String uri, int placeholderResId, int errorResId, ImageView view) {
		if (null != mContext && null != uri && !uri.isEmpty() && getKeysByStr(uri).length > 0) {
			Picasso.with(mContext.get()).load(getUrlByKey(getKeysByStr(uri)[0])).placeholder(placeholderResId)
					.error(errorResId).into(view);
		}
	}

	@Override
	public void showImage(String uri, int targetWidth, int targetHeight, int placeholderResId, int errorResId,
			ImageView view) {
		if (null != mContext && null != uri && !uri.isEmpty() && getKeysByStr(uri).length > 0) {
			Picasso.with(mContext.get()).load(getUrlByKey(targetWidth, targetHeight, getKeysByStr(uri)[0]))
					.resize(targetWidth, targetHeight).centerCrop().placeholder(placeholderResId).error(errorResId)
					.into(view);
		}
	}

	@Override
	public void showListRoundImage(String uri, int placeholderResId, int errorResId, ImageView view) {
		if (null != mContext && null != uri && !uri.isEmpty() && getKeysByStr(uri).length > 0) {
			Picasso.with(mContext.get()).load(getUrlByKey(200, 200, getKeysByStr(uri)[0])).resize(200, 200)
					.centerCrop().placeholder(placeholderResId).error(errorResId)
					.transform(new RoundedCornerTransformation(60)).into(view);
		}
	}

	@Override
	public void showRoundCornerImage(String uri, int size, int placeholderResId, int errorResId, ImageView view) {
		if (null != mContext && null != uri && !uri.isEmpty() && getKeysByStr(uri).length > 0) {
			Picasso.with(mContext.get()).load(getUrlByKey(size, size, getKeysByStr(uri)[0])).resize(size, size)
					.centerCrop().placeholder(placeholderResId).error(errorResId)
					.transform(new RoundedCornerTransformation(size)).into(view);
		}
	}

	@Override
	public void showImage(File image, int targetWidth, int targetHeight, int placeholderResId, int errorResId,
			ImageView view) {
		if (null != image && image.exists()) {
			Picasso.with(mContext.get()).load(image).resize(targetWidth, targetHeight).centerCrop()
					.placeholder(placeholderResId).error(errorResId).into(view);
		}
	}

	@Override
	public void showImage(File image, int placeholderResId, int errorResId, ImageView view) {
		if (null != image && image.exists()) {
			Picasso.with(mContext.get()).load(image).placeholder(placeholderResId).error(errorResId).into(view);
		}
	}

	private String[] getKeysByStr(String str) {
		String[] strs = str.split(",");
		return strs;
	}

	private static final String QINIU_URL = "http://7tea1q.com1.z0.glb.clouddn.com/";
	private static final String IMAGEVIEW_KEY = "imageView";

	private String getUrlByKey(String key) {
		StringBuilder sb = new StringBuilder();
		sb.append(QINIU_URL).append(key);
		return sb.toString();
	}

	private String getUrlByKey(int targetWidth, int targetHeight, String key) {
		StringBuilder sb = new StringBuilder();
		sb.append(QINIU_URL).append(key).append("?").append(IMAGEVIEW_KEY).append("/2/w/").append(targetWidth)
				.append("/h/").append(targetHeight);
		return sb.toString();
	}
}
