package com.globalways.csscli.ui.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import com.globalways.csscli.tools.MyApplication;

/**
 * 用来获取相册及相册内图片
 * 
 * @author James
 *
 */
public class GalleryHelper {
	private static final String TAG = GalleryHelper.class.getSimpleName();
	private static GalleryHelper mGalleryHelper = null;
	private static ContentResolver mContentResolver;

	/** 存放所有缩略图信息 **/
	@SuppressLint("UseSparseArrays")
	static Map<Long, String> mThumnailPathMap = new HashMap<Long, String>();

	private GalleryHelper() {
		mContentResolver = MyApplication.mContext.getContentResolver();
		mThumnailPathMap = getAllThumnailList();
	}

	public synchronized static GalleryHelper getInstance() {
		if (null == mGalleryHelper) {
			mGalleryHelper = new GalleryHelper();
		}
		return mGalleryHelper;
	}

	/**
	 * 获取所有缩略图列表
	 * 
	 * @return
	 */
	@SuppressLint("UseSparseArrays")
	private synchronized Map<Long, String> getAllThumnailList() {
		Map<Long, String> thumnailPathMap = new HashMap<Long, String>();
		String projection[] = new String[] { Thumbnails.IMAGE_ID, Thumbnails.DATA };
		Cursor cursor = mContentResolver.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
		if (null != cursor && cursor.moveToFirst()) {
			do {
				long thumnailId = cursor.getLong(cursor.getColumnIndex(Thumbnails.IMAGE_ID));
				String thumnailPath = cursor.getString(cursor.getColumnIndex(Thumbnails.DATA));
				if (null != thumnailPath && new File(thumnailPath).exists()) {
					thumnailPathMap.put(thumnailId, thumnailPath);
				}
			} while (cursor.moveToNext());
		}
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
		}
		return thumnailPathMap;
	}

	/**
	 * 获取相册列表
	 * 
	 * @return
	 */
	public synchronized List<GalleryAlbumEntity> getImageBucket() {
		List<GalleryAlbumEntity> imageBucketList = null;
		String projection[] = new String[] { Media._ID, Media.BUCKET_ID, Media.BUCKET_DISPLAY_NAME, Media.DATA,
				"COUNT(*) as count" };
		String selection = "0==0) GROUP BY(" + Media.BUCKET_ID;
		Cursor cursor = mContentResolver.query(Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
		if (null != cursor && cursor.moveToFirst()) {
			imageBucketList = new ArrayList<GalleryAlbumEntity>();
			do {
				GalleryAlbumEntity imageBucket = new GalleryAlbumEntity();
				imageBucket.imageBucketId = cursor.getString(cursor.getColumnIndex(Media.BUCKET_ID));
				imageBucket.bucketName = cursor.getString(cursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME));
				long imgId = cursor.getLong(cursor.getColumnIndex(Media._ID));
				imageBucket.coverOriginalPath = cursor.getString(cursor.getColumnIndex(Media.DATA));
				imageBucket.coverThumbnailPath = mThumnailPathMap.get(imgId);
				imageBucket.imageCount = cursor.getInt(cursor.getColumnIndex("count"));
				imageBucketList.add(imageBucket);
				Log.d(TAG, "[imageBucketId,bucketName,count]=[" + imageBucket.imageBucketId + ","
						+ imageBucket.bucketName + "," + imageBucket.imageCount + "]");
				Log.d(TAG, "[imgId,coverThumbnailPath]=[" + imgId + "," + imageBucket.coverThumbnailPath + "]");
			} while (cursor.moveToNext());
		}
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
		}
		return imageBucketList;
	}

	/**
	 * 获取指定相册中的图片列表
	 * 
	 * @param imageBucketId
	 * @return
	 */
	public synchronized List<GalleryPicEntity> getImageListByBucket(String imageBucketId) {
		List<GalleryPicEntity> list = null;
		String projection[] = new String[] { Media._ID, Media.DATA, Media.SIZE };
		String selection = Media.BUCKET_ID + "=?";
		Cursor cursor = mContentResolver.query(Media.EXTERNAL_CONTENT_URI, projection, selection,
				new String[] { imageBucketId }, null);
		if (null != cursor && cursor.getCount() > 0) {
			list = new ArrayList<GalleryPicEntity>();
			if (cursor.moveToFirst()) {
				do {
					GalleryPicEntity imageItem = new GalleryPicEntity();
					imageItem.imageBucketId = imageBucketId;
					imageItem.imageId = cursor.getLong(cursor.getColumnIndex(Media._ID));
					imageItem.imagePath = cursor.getString(cursor.getColumnIndex(Media.DATA));
					imageItem.thumbnailPath = mThumnailPathMap.get(imageItem.imageId);
					imageItem.imageSize = cursor.getLong(cursor.getColumnIndex(Media.SIZE));
					list.add(imageItem);
				} while (cursor.moveToNext());
			}
		}
		if (null != cursor && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	/**
	 * 计算压缩比例
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		Log.d(TAG, "原图的宽高：=[" + width + "," + height + "]");
		Log.d(TAG, "期望压缩后的宽高：[reqWidth,reqHeight]=[" + reqWidth + "," + reqHeight + "]");
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			Log.d(TAG, "[widthRatio,heightRatio]=[" + widthRatio + "," + heightRatio + "]");
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			inSampleSize = heightRatio > widthRatio ? widthRatio : heightRatio;
		}
		return inSampleSize;
	}

	/**
	 * 根据UI的宽高，解码出一个合适的Bitmap
	 * 
	 * @param origenalBitmapUrl
	 *            原图路径
	 * @param sdwidth
	 *            UI宽度
	 * @param sdheight
	 *            UI高度
	 * @param scaleExponential
	 *            原图和新图宽高比所用的N次方指数 规律：新图的宽高 * (0.75*2的(scaleExponential-1)次方) =
	 *            原图宽高 另外options.inSampleSize必须为2的n次方才能压图生效
	 * @return
	 */
	public Bitmap decodeSampleFromSD(String origenalBitmapUrl, int sdwidth, int sdheight, int scaleExponential[]) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(origenalBitmapUrl, options);
		options.inSampleSize = calculateInSampleSize(options, sdwidth, sdheight);
		Log.d(TAG, "options.inSampleSize1 = " + options.inSampleSize);
		if (options.inSampleSize > 1) {
			options.inSampleSize = (int) Math.pow(2, options.inSampleSize);
		}
		scaleExponential[0] = options.inSampleSize;

		Log.d(TAG, "options.inSampleSize2 = " + options.inSampleSize);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		options.inPurgeable = true;// 同时设置才会有效
		options.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		Bitmap result = BitmapFactory.decodeFile(origenalBitmapUrl, options);
		Log.d(TAG, "生成后的图宽高=[" + result.getWidth() + "," + result.getHeight() + "]");
		return result;
	}

	/**
	 * 获取原图和新图宽高比所用的N次方指数
	 * 
	 * @param inSampleSize
	 * @return
	 */
	public int getScaleExponential(int inSampleSize) {
		boolean flag = true;
		int exponential = 0;
		int startIndex = 0;
		int endIndex = 0;
		while (flag && exponential < 5) {
			endIndex += (int) Math.pow(2, exponential);
			if (inSampleSize >= startIndex && inSampleSize <= endIndex) {
				flag = false;
				System.out.println("[inSampleSize,startIndex,endIndex,exponential] = [" + inSampleSize + ","
						+ startIndex + "," + endIndex + "," + exponential + "]");
				break;
			}
			startIndex = endIndex + 1;
			exponential++;
		}
		return exponential;
	}
}