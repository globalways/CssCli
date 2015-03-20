package com.globalways.csscli.ui.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.globalways.csscli.R;
import com.globalways.csscli.tools.PicassoImageLoader;

/**
 * 照片列表adapter
 * 
 * @author James
 *
 */
public class GalleryAlbumPicAdapter extends BaseAdapter {

	private List<GalleryPicEntity> list = new ArrayList<GalleryPicEntity>();
	private Context context;

	public GalleryAlbumPicAdapter(Context context) {
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
			} else {
				// 如果缩略图不存在，就需要生成一张缩略图显示
				if (null != imageItem.imagePath && !"".equals(imageItem.imagePath)
						&& new File(imageItem.imagePath).exists()) {
					new PicassoImageLoader(context).showImage(new File(imageItem.imagePath), 100, 100,
							R.drawable.default_pic_photo, R.drawable.default_pic_photo, holder.ivThumbnail);
				}
			}
		}
	}

	class ViewHolder {
		ImageView ivThumbnail, ivCover, ivSelected;
	}

	private void findView(ViewHolder holder, View convertView) {
		holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
		holder.ivCover = (ImageView) convertView.findViewById(R.id.ivCover);
		holder.ivCover.setVisibility(View.GONE);
		holder.ivSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
		holder.ivSelected.setVisibility(View.GONE);
	}
}
