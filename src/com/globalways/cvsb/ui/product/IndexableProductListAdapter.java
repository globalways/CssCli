package com.globalways.cvsb.ui.product;

import java.util.List;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.tools.PicassoImageLoader;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.view.IndexItemInterface;
import com.globalways.cvsb.view.IndexableListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 带索引列表的ListView Adapter
 * @author wyp
 *
 */
public class IndexableProductListAdapter extends IndexableListAdapter
{

	private PicassoImageLoader imageLoader;
	private Context context;
	private int selectedItem = 1;
	public IndexableProductListAdapter(Context _context, int _resource,
			List<IndexItemInterface> _items)
	{
		super(_context, _resource, _items);
		imageLoader = new PicassoImageLoader(_context);
		this.context = _context;
	}
	
	public void setSelect(int postion){
		selectedItem = postion;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemView mItemView = null;
		IndexItemInterface item = getItem(position);
		
		if (convertView == null){
			mItemView = new ItemView();
			convertView = LayoutInflater.from(context).inflate(resource, parent, false);
			findView(mItemView, convertView);
			convertView.setTag(mItemView);
		}else{
			mItemView = (ItemView) convertView.getTag();
		}

		showSectionViewIfFirstItem(convertView, item, position);
		
		ProductEntity e = (ProductEntity)item;
		String ava = e.getProduct_avatar();
		if (ava == null || ava.isEmpty()) {
			mItemView.productAva.setImageResource(R.drawable.logo);
		} else {
			imageLoader.showListRoundImage(e.getProduct_avatar(), R.drawable.logo, R.drawable.logo,
					mItemView.productAva);
		}
		mItemView.productName.setText(e.getProduct_name());
		mItemView.productTag.setText(e.getProduct_tag());
		//单体型销量是整数
		if(ProductType.codeOf(e.getProduct_type()).equals(ProductType.DANTI)){
			mItemView.productSales.setText("销量: " + ((int) e.getSales_cnt()) + " "+ e.getProduct_unit());
		}else{
			mItemView.productSales.setText("销量: " + (e.getSales_cnt()) + " "+ e.getProduct_unit());
		}
		mItemView.productPrice.setText("￥ " + Tool.fenToYuan(e.getProduct_retail_price()) + " / "
				+ e.getProduct_unit() + "  ");
		return convertView;
	}
	
	private class ItemView {
		ImageView productAva;
		TextView productName, productTag, productSales, productPrice;
	}

	public void findView(ItemView itemView, View convertView) {
		itemView.productAva = (ImageView) convertView.findViewById(R.id.productAva);
		itemView.productName = (TextView) convertView.findViewById(R.id.productName);
		itemView.productTag = (TextView) convertView.findViewById(R.id.productTag);
		itemView.productSales = (TextView) convertView.findViewById(R.id.productSales);
		itemView.productPrice = (TextView) convertView.findViewById(R.id.productPrice);
	}
}
