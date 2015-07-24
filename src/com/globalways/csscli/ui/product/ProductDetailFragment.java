package com.globalways.csscli.ui.product;

import java.math.BigDecimal;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.alertdialogpro.AlertDialogPro;
import com.globalways.csscli.R;
import com.globalways.csscli.android.encoding.EncodingHandler;
import com.globalways.csscli.entity.ProductCategoryEntity;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.tools.InputVali;
import com.globalways.csscli.tools.QRCodeTools;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.BaseFragment;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.view.ClearableEditText;
import com.globalways.csscli.view.CommonDialogManager;
import com.globalways.csscli.view.NoScrollGridView;
import com.globalways.csscli.view.SimpleProgressDialog;
import com.google.zxing.WriterException;

/**
 * 商品详情fragment，可以查看或修改商品信息
 * 
 * @author James
 *
 */
public class ProductDetailFragment extends BaseFragment implements OnClickListener {

	private View layoutView;
	private TextView editProductType, tvProductCategory, tvProductCategoryID;;
	private ClearableEditText editName, editBrand, editPrice, editOriginalPrice, editRetailApr, editUnit, editApr, editTag, editStock, editStockLimit,
			editDesc;
	private Button btnUpdate, btnReset;
	private CheckBox checkBoxRecommend, checkBoxLock;
	private NoScrollGridView gridProductPic;
	private ProductPicAdapter picAdapter;
	
	// 分类dialog
	private ProductCategorySelectionAdapter mProductCategorySelectionAdapter;
	private ProductCategoryEntity currentParent = new ProductCategoryEntity(-1);
	private TextView tvCurrentCategory, tvCurrentLevel;
	private Button btnLevelUp;
	private int currentLevel = 1;
	private ListView categoryList;
	private AdapterView.OnItemClickListener categoryClick;
	private AlertDialogPro categoriesDialog;
	
	
	// private Spinner spinnerPurchase;
	private ScrollView scrollViewProductDetail;
	private ImageView imageQRCode;
	
	private TextWatcher retailAprWatcher, priceWatcher;

	private ProductEntity entity;
	/** 进度条 **/
	private SimpleProgressDialog mSimpleProgressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.product_detail_fragment, container, false);
		}
		return layoutView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	public void setEntity(ProductEntity entity) {
		if (entity != null) {
			this.entity = entity;
			refreshView();
			scrollViewProductDetail.setScrollY(0);
			picAdapter.setData(entity.getProduct_avatar());
		}
	}

	private void refreshView() {
		if (entity != null) {
			editName.setText(entity.getProduct_name());
			editBrand.setText(entity.getProduct_brand());
			editPrice.setText(Tool.fenToYuan(entity.getProduct_retail_price()));
			editRetailApr.setText(String.valueOf(entity.getProduct_retail_apr()));
			editOriginalPrice.setText(Tool.fenToYuan(entity.getProduct_original_price()));
			editProductType.setText(ProductType.codeOf(entity.getProduct_type()).toString());
			getCategory(entity.getProduct_category());
			editUnit.setText(entity.getProduct_unit());
			editApr.setText(entity.getProduct_apr() + "");
			editTag.setText(entity.getProduct_tag());
			editStock.setText(((int) entity.getStock_cnt()) + "");
			editStockLimit.setText(((int) entity.getStock_limit()) + "");
			editDesc.setText(entity.getProduct_desc());

			checkBoxRecommend.setChecked(entity.getIs_recommend() == 1 ? true : false);
			checkBoxLock.setChecked(entity.getStatus() == 1 ? true : false);

			try {
				// 根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
				Bitmap qrCodeBitmap = EncodingHandler.createQRCode(QRCodeTools.PRODUCT + entity.getProduct_qr(), 350);
				imageQRCode.setImageBitmap(qrCodeBitmap);
			} catch (WriterException e) {
				e.printStackTrace();
				Toast.makeText(getActivity(), "生成二维码失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * 根据分类ID获取商品分类信息
	 * @param cid
	 */
	private void getCategory(final int cid){
		if(cid < 1){
			tvProductCategory.setText("未分类");
			tvProductCategoryID.setText(String.valueOf(cid));
			return;
		}
		ProductManager.getInstance().getCategory(cid, new ManagerCallBack<ProductCategoryEntity>() {

			@Override
			public void onSuccess(ProductCategoryEntity returnContent) {
				super.onSuccess(returnContent);
				tvProductCategory.setText(String.valueOf(returnContent.getName()));
				tvProductCategoryID.setText(String.valueOf(cid));
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnUpdate:
			updateOrAdd();
			break;
		case R.id.btnReset:
			refreshView();
			break;
		case R.id.tvProductCategory:
			showCategoriesDialog();
			break;
		}
	}
	
	@SuppressLint("NewApi")
	private void showCategoriesDialog(){
		final AlertDialogPro.Builder builer = CommonDialogManager.createDialogBuilder(getActivity());
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.product_category_selection_dialog, null);
		btnLevelUp = (Button) dialogView.findViewById(R.id.btnLevelUp);
		tvCurrentCategory = (TextView) dialogView.findViewById(R.id.tvCurrentCategory);
		tvCurrentLevel = (TextView) dialogView.findViewById(R.id.tvCurrentLevel);
		categoryList = (ListView) dialogView.findViewById(R.id.lvCategoryList);
		mProductCategorySelectionAdapter = new ProductCategorySelectionAdapter(getActivity());
		categoryList.setAdapter(mProductCategorySelectionAdapter);
		
		categoryClick = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				currentParent = (ProductCategoryEntity) mProductCategorySelectionAdapter.getItem(position);
				loadCategoryChildren(currentParent.getId());
				currentLevel++;
				tvCurrentCategory.setText(currentParent.getName());
				tvCurrentLevel.setText(currentLevel+" 级分类");
				if(currentLevel == 3){
					categoryList.setOnItemClickListener(null);
				}
				if(currentLevel > 1){
					btnLevelUp.setVisibility(View.VISIBLE);
				}
				
			}
		};
		categoryList.setOnItemClickListener(categoryClick);
		
		btnLevelUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//当前显示为一级分类,不能返回到上一级
				if(currentParent.getId() == -1){
					return;
				}
				currentParent = mProductCategorySelectionAdapter.getEntityById(currentParent.getParent_id());
				currentLevel--;
				loadCategoryChildren(currentParent.getId());
				tvCurrentCategory.setText(currentParent.getName());
				builer.setTitle(currentParent.getName());
				tvCurrentLevel.setText(currentLevel+" 级分类");
				if(currentLevel == 1){
					btnLevelUp.setVisibility(View.INVISIBLE);
				}
				if(currentLevel < 3){
					categoryList.setOnItemClickListener(categoryClick);
				}
			}
		});
		tvCurrentCategory.setText(currentParent.getName());
		tvCurrentLevel.setText(currentLevel+" 级分类");
		categoriesDialog = builer.setTitle(currentParent.getName()).
		setView(dialogView).
		setNegativeButton("取消", null).
		create();
		categoriesDialog.show();
		loadCategoryChildren(-1);
	}
	
	public class ProductCategorySelectionAdapter extends ProductCategoryAdapter {

		public ProductCategorySelectionAdapter(Context context) {
			super(context);
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.product_category_selection_list_item, parent,false);
			}
			((TextView)convertView.findViewById(R.id.tvCategoryName)).setText(list.get(position).getName());
			((Button)convertView.findViewById(R.id.btnSelect)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					tvProductCategory.setText(list.get(position).getName());
					tvProductCategoryID.setText(String.valueOf(list.get(position).getId()));
					categoriesDialog.dismiss();
					currentLevel = 1;
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 加载指定分类的子分类
	 * @param whose
	 */
	private void loadCategoryChildren(int whose){
		ProductManager.getInstance().loadCategoryChildren(whose, new ManagerCallBack<List<ProductCategoryEntity>>() {

			@Override
			public void onSuccess(List<ProductCategoryEntity> returnContent) {
				super.onSuccess(returnContent);
				mProductCategorySelectionAdapter.setList(returnContent);
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				mProductCategorySelectionAdapter.setList(null);
				UITools.ToastMsg(getActivity(), msg);
			}
			
		});
	}
	

	/**
	 * 修改商品信息
	 */
	private void updateOrAdd() {
		String product_name = editName.getText().toString().trim();
		if (product_name == null || product_name.isEmpty()) {
			Toast.makeText(getActivity(), "请输入商品名", Toast.LENGTH_SHORT).show();
			return;
		}
		String product_brand = editBrand.getText().toString().trim();
		if (product_brand == null || product_brand.isEmpty()) {
			Toast.makeText(getActivity(), "请输入商品品牌", Toast.LENGTH_SHORT).show();
			return;
		}
		String product_desc = editDesc.getText().toString().trim();

		// 价格
		String str_price = editPrice.getText().toString().trim();
		if (str_price == null || str_price.isEmpty()) {
			UITools.ToastMsg(getActivity(), "请输入商品价格");
			return;
		}
		long product_retail_price = Tool.yuanToFen(str_price);
		
		// add by wyp
		//类型
		
		String str_product_type = editProductType.getText().toString().trim();
		if (str_product_type == null || str_product_type.isEmpty()) {
			UITools.ToastMsg(getActivity(), "请选择商品类型");
			return;
		}
		
		// 原价
		String str_original_price = editOriginalPrice.getText().toString().trim();
		if (str_original_price == null || str_original_price.isEmpty()) {
			UITools.ToastMsg(getActivity(), "请输入商品原价");
			return;
		}
		long product_original_price = Tool.yuanToFen(str_original_price);
		
		// 折扣
		String str_retail_apr = editRetailApr.getText().toString().trim();
		if (str_retail_apr == null || str_retail_apr.isEmpty()) {
			UITools.ToastMsg(getActivity(), "请输入折扣率");
			return;
		}
		
		//分类
		int product_category_id = Integer.parseInt(tvProductCategoryID.getText().toString());
		// end

		// 单位
		String product_unit = editUnit.getText().toString().trim();
		if (product_unit == null || product_unit.isEmpty()) {
			Toast.makeText(getActivity(), "请输入商品单位", Toast.LENGTH_SHORT).show();
			return;
		}

		// 库存
		String stock = editStock.getText().toString().trim();
		double stock_cnt = 0;
		if (stock != null && !stock.isEmpty()) {
			stock_cnt = Double.valueOf(stock);
		}
		String product_tag = editTag.getText().toString().trim();

		mSimpleProgressDialog.showDialog();
		ProductManager.getInstance().updateOrAdd(false, null, null, product_name, product_brand,
				entity.getProduct_qr(), entity.getProduct_bar(), product_desc, product_retail_price,str_retail_apr,product_category_id, product_original_price,ProductType.nameOf(str_product_type).getCode(), product_unit, stock_cnt,
				checkBoxRecommend.isChecked(), checkBoxLock.isChecked(), product_tag, new ManagerCallBack<String>() {
					@Override
					public void onSuccess(String returnContent) {
						super.onSuccess(returnContent);
						mSimpleProgressDialog.cancleDialog();
						AlertDialog.Builder builder = new Builder(getActivity());
						builder.setMessage("修改成功！");
						builder.setTitle("提示！");
						builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						builder.create().show();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						mSimpleProgressDialog.cancleDialog();
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void initView() {
		editName = (ClearableEditText) layoutView.findViewById(R.id.editName);
		editBrand = (ClearableEditText) layoutView.findViewById(R.id.editBrand);
		editProductType = (TextView) layoutView.findViewById(R.id.editProductType);
		editProductType.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Builder builder = CommonDialogManager.createDialogBuilder(getActivity());
				builder.setItems(new String[]{"单体型","称重型"},new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							editProductType.setText(ProductType.DANTI.toString());
							break;
						case 1:
							editProductType.setText(ProductType.CHENGZHONG.toString());
							break;
						default:
							break;
						}
					}
				}).setTitle("选择商品类型").create().show();
				
				
			}
		});
		
		editPrice = (ClearableEditText) layoutView.findViewById(R.id.editPrice);
		editPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
					priceWatcher = new TextWatcher() {
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							String str_original_price;
							String str_price;
							String str_retail_apr;
							str_original_price = editOriginalPrice.getText().toString();
							if( !InputVali.isFloatAsFen(str_original_price) )
							{
								UITools.ToastMsg(getActivity(), "请正确输入原价");
								return;
							}
							if(Float.parseFloat(str_original_price.trim()) == 0)
							{
								UITools.ToastMsg(getActivity(), "原价不能为0");
								return;
							}
							if(s.toString().equals(""))
							{
								editRetailApr.setPrefix("");
								return;
							}else{
								editRetailApr.setPrefix("0.");
							}
							str_price =String.valueOf(s);
							if(Tool.compare(str_price, str_original_price) == 1)
							{
								editPrice.setText(String.valueOf(str_original_price));
								editPrice.setSelection(String.valueOf(str_original_price).length());
								str_price = str_original_price;
							}
							
							str_retail_apr  = Tool.div(str_price, str_original_price, Tool.RETAIL_APR_SCALE);
							if(!str_retail_apr.contains("0.") )
							{
								editRetailApr.setPrefix("");
							}else{
								editRetailApr.setPrefix("0.");
								//去掉小数点后面无效的0
								str_retail_apr = String.valueOf(new BigDecimal(str_retail_apr).doubleValue());
							}
							editRetailApr.setText(str_retail_apr.replace("0.", ""));
						}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after) {
							
						}
						
						@Override
						public void afterTextChanged(Editable s) {
							
						}
					};
					editPrice.addTextChangedListener(priceWatcher);
					editRetailApr.removeTextChangedListener(retailAprWatcher);
				}
			}
		});
		
		
//		UITools.editNumLimit(editPrice);
		
		
		editOriginalPrice = (ClearableEditText) layoutView.findViewById(R.id.editOriginalPrice);
		
		
		editRetailApr = (ClearableEditText) layoutView.findViewById(R.id.editRetailApr);
		editRetailApr.setPrefix("0.");
		editRetailApr.setPrefixColor(getResources().getColor(R.color.base_black_333333));
		editRetailApr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
					retailAprWatcher = new TextWatcher() {
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
						}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after) {
							
						}
						
						@Override
						public void afterTextChanged(Editable s) {
							String str_retail_apr;
							String str_original_price;
							str_original_price = editOriginalPrice.getText().toString();
							if(!InputVali.isFloatAsFen(str_original_price))
								UITools.ToastMsg(getActivity(), "请正确输入原价");
							if(s.toString().equals(""))
							{
								editPrice.setText(String.valueOf(str_original_price));
								return;
							}
							str_retail_apr ="0."+s.toString();
							String str_price  = Tool.mul(str_retail_apr, str_original_price);
							editPrice.setText(Tool.formatYuan(str_price));
						}
					};
					editRetailApr.addTextChangedListener(retailAprWatcher);
					editPrice.removeTextChangedListener(priceWatcher);
				}
			}
		});
		//product category 
		tvProductCategory = (TextView) layoutView.findViewById(R.id.tvProductCategory);
		tvProductCategoryID = (TextView) layoutView.findViewById(R.id.tvProductCategoryID);
		tvProductCategory.setOnClickListener(this);
		
		editUnit = (ClearableEditText) layoutView.findViewById(R.id.editUnit);
		editApr = (ClearableEditText) layoutView.findViewById(R.id.editApr);
		editTag = (ClearableEditText) layoutView.findViewById(R.id.editTag);
		editStock = (ClearableEditText) layoutView.findViewById(R.id.editStock);
		UITools.editNumDoubleLimit(editStock);
		editStockLimit = (ClearableEditText) layoutView.findViewById(R.id.editStockLimit);
		editDesc = (ClearableEditText) layoutView.findViewById(R.id.editDesc);

		btnUpdate = (Button) layoutView.findViewById(R.id.btnUpdate);
		btnUpdate.setOnClickListener(this);
		btnReset = (Button) layoutView.findViewById(R.id.btnReset);
		btnReset.setOnClickListener(this);

		imageQRCode = (ImageView) layoutView.findViewById(R.id.imageQRCode);

		checkBoxRecommend = (CheckBox) layoutView.findViewById(R.id.checkBoxRecommend);
		checkBoxLock = (CheckBox) layoutView.findViewById(R.id.checkBoxLock);
		gridProductPic = (NoScrollGridView) layoutView.findViewById(R.id.gridProductPic);
		picAdapter = new ProductPicAdapter(getActivity());
		gridProductPic.setAdapter(picAdapter);

		// spinnerPurchase = (Spinner)
		// layoutView.findViewById(R.id.spinnerPurchase);
		scrollViewProductDetail = (ScrollView) layoutView.findViewById(R.id.scrollViewProductDetail);

		mSimpleProgressDialog = new SimpleProgressDialog(getActivity(), true);
		mSimpleProgressDialog.setText("正在修改信息...");
	}
}
