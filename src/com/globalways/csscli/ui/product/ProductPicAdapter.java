package com.globalways.csscli.ui.product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.globalways.csscli.R;
import com.globalways.csscli.tools.PicassoImageLoader;

public class ProductPicAdapter extends BaseAdapter {

	private String[] pics;
	private PicassoImageLoader imagerLoader;
	private Context context;

	public ProductPicAdapter(Context context) {
		this.context = context;
		imagerLoader = new PicassoImageLoader(context);
	}

	public void setData(String productAva) {
		if (productAva != null && !productAva.isEmpty()) {
			pics = productAva.split(",");
		} else {
			pics = null;
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (pics == null) {
			return 0;
		}
		return pics.length;
	}

	@Override
	public String getItem(int position) {
		if (pics == null) {
			return null;
		}
		return pics[position];
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
			convertView = LayoutInflater.from(context).inflate(R.layout.product_select_image_item, null);
			mItemView = new ItemView();
			findView(mItemView, convertView);
			convertView.setTag(mItemView);
		} else {
			mItemView = (ItemView) convertView.getTag();
		}
		imagerLoader.showImage(pics[position], 300, 300, R.drawable.logo, R.drawable.logo, mItemView.imageSelect);
		return convertView;
	}

	class ItemView {
		ImageView imageSelect;
	}

	private void findView(ItemView holder, View convertView) {
		holder.imageSelect = (ImageView) convertView.findViewById(R.id.imageSelect);
	}

}
