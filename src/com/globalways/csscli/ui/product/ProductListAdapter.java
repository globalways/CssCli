package com.globalways.csscli.ui.product;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.tools.PicassoImageLoader;

public class ProductListAdapter extends BaseAdapter {

	private static final int NEXT_PAGE = 1;

	private List<ProductEntity> list = null;
	private int page = NEXT_PAGE;
	private ProductEntity chooseProduct;
	private PicassoImageLoader imageLoader;
	private Context context;

	public ProductListAdapter(Context context) {
		super();
		this.context = context;
		imageLoader = new PicassoImageLoader(context);
	}

	public void setData(boolean isRefresh, List<ProductEntity> list) {
		if (isRefresh) {
			this.list = list;
			page = NEXT_PAGE;
			chooseProduct = list.get(0);
		} else {
			this.list.addAll(list);
		}
		page++;
		notifyDataSetChanged();
	}

	public int getPage(boolean isRefresh) {
		if (isRefresh) {
			page = NEXT_PAGE;
		}
		return page;
	}

	public void setChooseItem(int position) {
		chooseProduct = list.get(position);
		notifyDataSetChanged();
	}

	public ProductEntity getItemByPosition(int position) {
		if (null != list) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public int getCount() {
		if (null != list) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (null != list) {
			return list.get(position);
		}
		return null;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.product_list_item, null);
			mItemView = new ItemView();
			findView(mItemView, convertView);
			convertView.setTag(mItemView);
		} else {
			mItemView = (ItemView) convertView.getTag();
		}
		if (chooseProduct.getId() == list.get(position).getId()) {
			mItemView.viewItem.setBackgroundColor(context.getResources().getColor(R.color.base_gray_60));
		} else {
			mItemView.viewItem.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
		}
		imageLoader.showListRoundImage(list.get(position).getProduct_avatar(), R.drawable.logo, R.drawable.logo,
				mItemView.productAva);
		mItemView.productName.setText(list.get(position).getProduct_name());
		mItemView.productTag.setText(list.get(position).getProduct_tag());
		mItemView.productSales.setText("销量: " + ((int) list.get(position).getSales_cnt()) + " "
				+ list.get(position).getProduct_unit());
		mItemView.productPrice.setText("￥ " + list.get(position).getProduct_price() / 100.00 + " / "
				+ list.get(position).getProduct_unit() + "  ");
		return convertView;
	}

	private class ItemView {
		View viewItem;
		ImageView productAva;
		TextView productName, productTag, productSales, productPrice;
	}

	public void findView(ItemView itemView, View convertView) {
		itemView.viewItem = (View) convertView.findViewById(R.id.viewItem);
		itemView.productAva = (ImageView) convertView.findViewById(R.id.productAva);
		itemView.productName = (TextView) convertView.findViewById(R.id.productName);
		itemView.productTag = (TextView) convertView.findViewById(R.id.productTag);
		itemView.productSales = (TextView) convertView.findViewById(R.id.productSales);
		itemView.productPrice = (TextView) convertView.findViewById(R.id.productPrice);
	}
}
