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

public class CashierListAdapter extends BaseAdapter {

	private List<ProductEntity> list = null;
	private PicassoImageLoader imageLoader;
	private Context context;

	public CashierListAdapter(Context context) {
		super();
		this.context = context;
		imageLoader = new PicassoImageLoader(context);
	}

	public void setData(List<ProductEntity> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public void addItem(ProductEntity entity) {
		if (list == null) {
			list = new ArrayList<ProductEntity>();
		}
		list.add(entity);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ItemView mItemView;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.cashier_list_item, null);
			mItemView = new ItemView();
			findView(mItemView, convertView);
			convertView.setTag(mItemView);
		} else {
			mItemView = (ItemView) convertView.getTag();
		}
		final ProductEntity entity = list.get(position);
		imageLoader.showListRoundImage(entity.getProduct_avatar(), R.drawable.logo, R.drawable.logo,
				mItemView.productAva);
		mItemView.productName.setText(entity.getProduct_name());
		mItemView.productPrice.setText("￥ " + entity.getProduct_price() / 100.00 + " / " + entity.getProduct_unit()
				+ "  ");
		mItemView.textNumber.setText(entity.getShoppingNumber() + "");
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
						mItemView.textNumber.setText(editText.getText().toString());
					}
				});
				builder.create().show();
			}
		});
		if (mItemView.textNumber.getText().toString().trim().equals("1")) {
			mItemView.btnLess.setEnabled(false);
		}
		mItemView.btnLess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = mItemView.textNumber.getText().toString().trim();
				if (!text.equals("1")) {
					list.get(position).setShoppingNumber(entity.getShoppingNumber() - 1);
					mItemView.textNumber.setText(list.get(position).getShoppingNumber() + "");
				}
			}
		});
		mItemView.btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list.get(position).setShoppingNumber(entity.getShoppingNumber() + 1);
				mItemView.textNumber.setText(list.get(position).getShoppingNumber() + "");
			}
		});
		mItemView.textDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(context);
				builder.setTitle("提示!");
				builder.setMessage("您正在删除改商品");
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						list.remove(position);
						notifyDataSetChanged();
					}
				});
				builder.create().show();
			}
		});
		return convertView;
	}

	private class ItemView {
		ImageView productAva;
		TextView productName, productPrice, textNumber, textDelete;
		Button btnLess, btnAdd;
	}

	public void findView(ItemView itemView, View convertView) {
		itemView.productAva = (ImageView) convertView.findViewById(R.id.productAva);
		itemView.productName = (TextView) convertView.findViewById(R.id.productName);
		itemView.productPrice = (TextView) convertView.findViewById(R.id.productPrice);
		itemView.textNumber = (TextView) convertView.findViewById(R.id.textNumber);
		itemView.textDelete = (TextView) convertView.findViewById(R.id.textDelete);
		itemView.btnLess = (Button) convertView.findViewById(R.id.btnLess);
		itemView.btnAdd = (Button) convertView.findViewById(R.id.btnAdd);
	}

}