package com.globalways.csscli.tools;

import java.io.File;

import android.widget.ImageView;

/**
 * 图片加载器接口
 * 
 * @author James
 *
 */
public interface ImageLoaderDao {

	/**
	 * 加载图片到指定View
	 * 
	 * @param uri
	 * @param placeholderDrawable
	 * @param errorDrawable
	 * @param view
	 */
	public void showImage(String uri, int placeholderResId, int errorResId, ImageView view);

	/**
	 * 加载图片到指定View
	 * 
	 * @param uri
	 * @param targetWidth
	 * @param targetHeight
	 * @param placeholderDrawable
	 * @param errorDrawable
	 * @param view
	 */
	public void showImage(String uri, int targetWidth, int targetHeight, int placeholderResId, int errorResId,
			ImageView view);

	public void showListRoundImage(String uri, int placeholderResId, int errorResId, ImageView view);

	/**
	 * 加载图片圆角转换后显示到指定View，会正方形图片
	 * 
	 * @param uri
	 * @param size
	 * @param placeholderDrawable
	 * @param errorDrawable
	 * @param view
	 */
	public void showRoundCornerImage(String uri, int size, int placeholderResId, int errorResId, ImageView view);

	/**
	 * 显示本地图片，需要Picsso生成缩略图
	 * 
	 * @param image
	 * @param targetWidth
	 * @param targetHeight
	 * @param placeholderResId
	 * @param errorResId
	 * @param view
	 */
	public void showImage(File image, int targetWidth, int targetHeight, int placeholderResId, int errorResId,
			ImageView view);

	/**
	 * 显示本地图片，不需要Picsso生成缩略图
	 * 
	 * @param image
	 * @param placeholderResId
	 * @param errorResId
	 * @param view
	 */
	public void showImage(File image, int placeholderResId, int errorResId, ImageView view);
}
