package com.globalways.cvsb.ui.purchase;

import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.globalways.cvsb.R;
import com.globalways.cvsb.tools.PicassoImageLoader;
import com.globalways.cvsb.ui.product.ProductPicAdapter;

/**
 * copy from {@link ProductPicAdapter}
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年5月11日 下午9:52:30
 */
public class PurchasePicAdapter extends BaseAdapter {

	private ArrayList<String> pics;
	private PicassoImageLoader imagerLoader;
	private Context context;

	public PurchasePicAdapter(Context context) {
		this.context = context;
		imagerLoader = new PicassoImageLoader(context);
	}

	public void setData(String productAva) {
		if (productAva != null && !productAva.isEmpty()) {
			pics = new ArrayList<String>();
			pics.addAll(Arrays.asList(productAva.split(",")));
		} else {
			pics = null;
		}
		notifyDataSetChanged();
	}

	/**
	 * 获取照片列表
	 * 
	 * @return
	 */
	public ArrayList<String> getPicList() {
		if (pics != null && pics.size() > 0) {
			return pics;
		}
		return null;
	}

	/**
	 * 移除指定项
	 * 
	 * @param position
	 */
	public void remove(int position) {
		if (pics != null && pics.size() > 0) {
			pics.remove(position);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (pics == null) {
			return 0;
		}
		return pics.size();
	}

	@Override
	public String getItem(int position) {
		if (pics == null) {
			return null;
		}
		return pics.get(position);
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
		imagerLoader.showImage(pics.get(position), 300, 300, R.drawable.logo, R.drawable.logo, mItemView.imageSelect);
		return convertView;
	}

	class ItemView {
		ImageView imageSelect;
	}

	private void findView(ItemView holder, View convertView) {
		holder.imageSelect = (ImageView) convertView.findViewById(R.id.imageSelect);
	}

}
