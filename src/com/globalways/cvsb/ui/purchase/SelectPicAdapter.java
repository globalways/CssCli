package com.globalways.cvsb.ui.purchase;

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

import com.globalways.cvsb.R;
import com.globalways.cvsb.tools.PicassoImageLoader;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.gallery.GalleryPicEntity;
import com.globalways.cvsb.ui.product.ProductSelectPicAdapter;

/**
 * copy from {@link ProductSelectPicAdapter}
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年5月7日 下午4:23:54
 */
public class SelectPicAdapter extends BaseAdapter {
	private List<GalleryPicEntity> list = new ArrayList<GalleryPicEntity>();
	private Context context;

	public SelectPicAdapter(Context context) {
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

	public void remove(int position) {
		list.remove(position);
		notifyDataSetChanged();
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
		final ItemView mItemView;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.purchase_fragment_goods_image_item, null);
			mItemView = new ItemView();
			findView(mItemView, convertView);
			convertView.setTag(mItemView);
		} else {
			mItemView = (ItemView) convertView.getTag();
		}
		showData(this.list.get(position), mItemView);
		return convertView;
	}

	/**
	 * 显示数据
	 * 
	 * @param imageItem
	 * @param itemView
	 */
	private void showData(GalleryPicEntity imageItem, ItemView itemView) {
		if (null != imageItem && !Tool.isEmpty(imageItem.imagePath)) {
			// 如果缩略图存在，就直接显示缩略图
			if (null != imageItem.thumbnailPath && !"".equals(imageItem.thumbnailPath)) {
				new PicassoImageLoader(context).showImage(new File(imageItem.thumbnailPath),
						R.drawable.default_pic_photo, R.drawable.default_pic_photo, itemView.imageSelect);
			} else {// 如果缩略图不存在，就需要生成一张缩略图显示
				if (null != imageItem.imagePath && !"".equals(imageItem.imagePath)
						&& new File(imageItem.imagePath).exists()) {
					new PicassoImageLoader(context).showImage(new File(imageItem.imagePath), 100, 100,
							R.drawable.default_pic_photo, R.drawable.default_pic_photo, itemView.imageSelect);
				}
			}
		} else {
			itemView.imageSelect.setImageResource(R.drawable.icon_add_pic);
		}
	}

	class ItemView {
		ImageView imageSelect;
	}

	private void findView(ItemView holder, View convertView) {
		holder.imageSelect = (ImageView) convertView.findViewById(R.id.imageSelect);
	}

}
