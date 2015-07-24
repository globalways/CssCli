package com.globalways.csscli.ui.cashier;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.tools.PicassoImageLoader;
import com.globalways.csscli.ui.product.ProductType;

/**
 * 收银台购物车列表adapter
 * 
 * @author James
 * 
 * changed by wyp 2015.6.24
 *
 */
public class CashierListAdapter extends BaseAdapter {

	private List<ProductEntity> list = new ArrayList<ProductEntity>();
	private PicassoImageLoader imageLoader;
	private Context context;
	private int last_position = -1;

	public CashierListAdapter(Context context) {
		super();
		this.context = context;
		imageLoader = new PicassoImageLoader(context);
	}

	public void setData(List<ProductEntity> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	/** 向收银台添加商品 */
	public void addItem(ProductEntity entity) {
//		if (list == null) {
//			list = new ArrayList<ProductEntity>();
//		}
		if (list.size() > 0) {
			boolean isAdd = false;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getProduct_qr().equals(entity.getProduct_qr())) {
					list.get(i).setShoppingNumber(list.get(i).getShoppingNumber() + entity.getShoppingNumber());
					isAdd = true;
					break;
				}
			}
			if (!isAdd) {
				list.add(entity);
			}
		} else {
			list.add(entity);
		}
		refreshTotalPrice();
		notifyDataSetChanged();
		Log.i("yangping.wang", "add item,current size:"+list.size());
	}

	/** 清空收银台列表 */
	public void clear() {
		list = null;
		notifyDataSetChanged();
	}

	/**
	 * 获取收银台商品列表
	 * 
	 * @return
	 */
	public List<ProductEntity> getCashierList() {
		if (list != null) {
			return list;
		}
		return null;
	}

	/** 刷新总价 */
	private void refreshTotalPrice() {
		((CashierActivity) context).setTotalPrice(getTotalPrice());
	}

	/** 获取总价 */
	public long getTotalPrice() {
		int totalPrice = 0;
		for (int i = 0; i < list.size(); i++) {
			totalPrice += (list.get(i).getShoppingNumber() * list.get(i).getProduct_retail_price());
		}
		return totalPrice;
	}

	/** 根据position获取商品实体 */
	public ProductEntity getItemByPosition(int position) {
		if (null != list) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public int getCount() {
		if(list != null){
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ItemView mItemView;
		if (convertView == null) {
			mItemView = new ItemView();
			convertView = LayoutInflater.from(context).inflate(R.layout.cashier_list_item,parent, false);
			findView(mItemView, convertView);
			convertView.setTag(mItemView);
		} else {
			mItemView = (ItemView) convertView.getTag();
		}
		setViewData(position,mItemView);
		return convertView;
	}

	private class ItemView {
		ImageView productAva;
		TextView productName, productPrice, textNumber, textDelete,tvStockWarning;
		Button btnLess, btnAdd;
	}

	public void findView(ItemView itemView, View convertView) {
		itemView.productAva = (ImageView) convertView.findViewById(R.id.productAva);
		itemView.productName = (TextView) convertView.findViewById(R.id.productName);
		itemView.productPrice = (TextView) convertView.findViewById(R.id.productPrice);
		itemView.textNumber = (TextView) convertView.findViewById(R.id.textNumber);
		itemView.textDelete = (TextView) convertView.findViewById(R.id.textDelete);
		itemView.tvStockWarning = (TextView) convertView.findViewById(R.id.tvStockWarning);
		itemView.btnLess = (Button) convertView.findViewById(R.id.btnLess);
		itemView.btnAdd = (Button) convertView.findViewById(R.id.btnAdd);
	}
	
	private void setViewData(final int position,final ItemView mItemView)
	{
		final ProductEntity entity = list.get(position);
		//库存不足提醒
		if(entity.getStock_cnt() <= entity.getStock_limit() || entity.getShoppingNumber() >= entity.getStock_cnt())
		{
			String warning = "";
			//单体型商品显示为整数
			if( ProductType.codeOf(entity.getProduct_type()) == ProductType.DANTI ){
				warning = context.getString(R.string.cashier_shoplistitem_stockwarning)+" "+(int)entity.getStock_cnt();
			}else{
				warning = context.getString(R.string.cashier_shoplistitem_stockwarning)+" "+entity.getStock_cnt();
			}
			mItemView.tvStockWarning.setText(warning);
			mItemView.tvStockWarning.setVisibility(View.VISIBLE);
		}else{
			mItemView.tvStockWarning.setText("");
			mItemView.tvStockWarning.setVisibility(View.INVISIBLE);
		}
		//购物车数量超过库存数目
		if(entity.getShoppingNumber() >= entity.getStock_cnt())
		{
			entity.setShoppingNumber((int)entity.getStock_cnt());
		}
		
		if(!entity.getProduct_avatar().isEmpty())
		{
			imageLoader.showListRoundImage(entity.getProduct_avatar(), R.drawable.logo, R.drawable.logo,
					mItemView.productAva);
		}else{
			mItemView.productAva.setImageResource(R.drawable.logo);
		}
		mItemView.productName.setText(entity.getProduct_name());
		mItemView.productPrice.setText("￥ " + entity.getProduct_retail_price() / 100.00 + " / " + entity.getProduct_unit()
				+ "  ");
		mItemView.textNumber.setText(String.valueOf(entity.getShoppingNumber()));
		mItemView.textNumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(context);
				View dialogView = LayoutInflater.from(context).inflate(R.layout.cashier_list_item_edittextview, null);
				final EditText editText = (EditText) dialogView.findViewById(R.id.editDialogNumber);
				final Button btnDialogLess = (Button) dialogView.findViewById(R.id.btnDialogLess);
				final Button btnDialogAdd = (Button) dialogView.findViewById(R.id.btnDialogAdd);
				editText.setText(mItemView.textNumber.getText().toString());
				editText.setSelection(editText.getText().toString().trim().length());
				editText.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						String text = editText.getText().toString().trim();
						if (text != null && !text.isEmpty()) {
							if (text.startsWith("0") && text.length() > 1) {
								text = text.substring(1);
								editText.setText(text);
							}
						} else {
							editText.setText("1");
						}
						btnDialogLess.setEnabled(!editText.getText().toString().trim().equals("1"));
						editText.setSelection(editText.getText().toString().trim().length());
					}
				});
				if (editText.getText().toString().trim().equals("1")) {
					btnDialogLess.setEnabled(false);
				}
				btnDialogLess.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String text = editText.getText().toString().trim();
						if (!text.equals("1")) {
							editText.setText(Integer.valueOf(editText.getText().toString().trim()) - 1 + "");
						}
					}
				});
				btnDialogAdd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						editText.setText(Integer.valueOf(editText.getText().toString().trim()) + 1 + "");
					}
				});
				builder.setView(dialogView);
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						list.get(position).setShoppingNumber(
								Integer.valueOf(editText.getText().toString().toString().trim()));
						notifyDataSetChanged();
					}
				});
				builder.create().show();
			}
		});
		if (mItemView.textNumber.getText().toString().trim().equals("1")) {
			mItemView.btnLess.setEnabled(false);
		}else{
			mItemView.btnLess.setEnabled(true);
		}
		mItemView.btnLess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = mItemView.textNumber.getText().toString().trim();
				if (!text.equals("1")) {
					list.get(position).setShoppingNumber(entity.getShoppingNumber() - 1);
					refreshTotalPrice();
					notifyDataSetChanged();
				}
			}
		});
		mItemView.btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list.get(position).setShoppingNumber(entity.getShoppingNumber() + 1);
				refreshTotalPrice();
				notifyDataSetChanged();
			}
		});
		mItemView.textDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(context);
				builder.setTitle("提示!");
				builder.setMessage("您正在删除商品");
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						list.remove(position);
						refreshTotalPrice();
						notifyDataSetChanged();
					}
				});
				builder.create().show();
			}
		});
		
		Log.i("yangping.wang", entity.getProduct_name()+":"+entity.getStock_cnt()+"-"
				+entity.getStock_limit()+" position:"+position+" avatar:"+entity.getProduct_avatar()
				+" itemView-name:"+mItemView.productName.getText()
						);
	}

}
