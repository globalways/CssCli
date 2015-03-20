package com.globalways.csscli.ui.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.globalways.csscli.R;
import com.globalways.csscli.tools.PicassoImageLoader;

/**
 * 相册列表adapter
 * 
 * @author James
 *
 */
public class GalleryAlbumAdapter extends BaseAdapter {
	private List<GalleryAlbumEntity> list = new ArrayList<GalleryAlbumEntity>();
	private HashMap<String, HashMap<Long, GalleryPicEntity>> selectedImageItem;
	private Context context;

	public GalleryAlbumAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<GalleryAlbumEntity> list, HashMap<String, HashMap<Long, GalleryPicEntity>> selectedImageItem) {
		this.selectedImageItem = selectedImageItem;
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
		GalleryAlbumEntity imageBucket = this.list.get(position);
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.gallery_album_list_item, null);
			holder = new ViewHolder();
			findView(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.llBottom.getBackground().setAlpha(150);
		holder.tvSelectedCount.getBackground().setAlpha(100);
		showData(imageBucket, holder);
		return convertView;
	}

	/**
	 * 显示数据
	 * 
	 * @param imageBucket
	 * @param holder
	 */
	private void showData(GalleryAlbumEntity imageBucket, ViewHolder holder) {
		if (null != imageBucket) {
			// 如果缩略图存在，就直接显示缩略图
			if (null != imageBucket.coverThumbnailPath && !"".equals(imageBucket.coverThumbnailPath)) {
				new PicassoImageLoader(context).showImage(new File(imageBucket.coverThumbnailPath),
						R.drawable.default_pic_photo, R.drawable.default_pic_photo, holder.ivThumbnail);
			} else {// 如果缩略图不存在，就需要生成一张缩略图显示
				if (null != imageBucket.coverOriginalPath && !"".equals(imageBucket.coverOriginalPath)
						&& new File(imageBucket.coverOriginalPath).exists()) {
					new PicassoImageLoader(context).showImage(new File(imageBucket.coverOriginalPath), 100, 100,
							R.drawable.default_pic_photo, R.drawable.default_pic_photo, holder.ivThumbnail);
				}
			}
			holder.tvBucketName.setText(imageBucket.bucketName);
			holder.tvBucketImageCount.setText(Integer.toString(imageBucket.imageCount));
			holder.tvSelectedCount.setVisibility(View.GONE);
			if (null != selectedImageItem && selectedImageItem.size() > 0) {
				HashMap<Long, GalleryPicEntity> selectedImageItemList = selectedImageItem.get(imageBucket.imageBucketId);
				if (null != selectedImageItemList && selectedImageItemList.size() > 0) {
					holder.tvSelectedCount.setText(Integer.toString(selectedImageItemList.size()));
					holder.tvSelectedCount.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	class ViewHolder {
		ImageView ivThumbnail;
		LinearLayout llBottom;
		TextView tvBucketName, tvBucketImageCount, tvSelectedCount;
	}

	private void findView(ViewHolder holder, View convertView) {
		holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
		holder.tvBucketName = (TextView) convertView.findViewById(R.id.tvBucketName);
		holder.tvBucketImageCount = (TextView) convertView.findViewById(R.id.tvBucketImageCount);
		holder.llBottom = (LinearLayout) convertView.findViewById(R.id.llBottom);
		holder.tvSelectedCount = (TextView) convertView.findViewById(R.id.tvSelectedCount);
	}
}
