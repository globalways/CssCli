package com.globalways.cvsb.ui.purchase;

import java.util.ArrayList;
import java.util.List;

import com.alertdialogpro.AlertDialogPro;
import com.alertdialogpro.AlertDialogPro.Builder;
import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.entity.PurchaseEntity;
import com.globalways.cvsb.entity.PurchaseGoodsEntity;
import com.globalways.cvsb.entity.SupplierEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.ManagerCallBack2;
import com.globalways.cvsb.http.manager.ProductManager;
import com.globalways.cvsb.http.manager.PurchaseManager;
import com.globalways.cvsb.http.manager.SupplierManager;
import com.globalways.cvsb.tools.InputVali;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.QRCodeTools;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.ui.gallery.GalleryActivity;
import com.globalways.cvsb.ui.gallery.GalleryPicEntity;
import com.globalways.cvsb.ui.gallery.GalleryPicPreviewActivity;
import com.globalways.cvsb.ui.product.ProductAddNewActivity;
import com.globalways.cvsb.ui.product.ProductScanCodeActivity;
import com.globalways.cvsb.ui.product.ProductSelectionActivity;
import com.globalways.cvsb.view.BottomMenuDialog;
import com.globalways.cvsb.view.ClearableEditText;
import com.globalways.cvsb.view.MenuItemEntity;
import com.globalways.cvsb.view.MyDialogManager;
import com.globalways.cvsb.view.SimpleProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView.Validator;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PurchaseFragment extends Fragment implements OnClickListener, OnItemClickListener,OnRefreshListener<ListView> {

	
	private static final int CODE_SELECT_IMAGE = 11;
	private static final int CODE_SCAN_BAR_REQUEST = 12;
	private View contentView, viewPurchaseDetail;
	private Button btnSaveNewPurchaseRecord, btnScanGoods, btnManualSelect;
	private EditText etBatchId, etOutId;
	private ClearableEditText etTotal;
	private TextView tvSelectSupplier, btnToNewPurchaseRecord, tvPurchaseCounts, tvPurchaseGoodsDetailUnit, tvNoPurchasePic;
	private LinearLayout llNewPurchaseView;
	private ListView lvPurchaseList, lvPurhcaseGoodsList, lvPurchaseGoodsListFromWeb;
	private PullToRefreshListView refreshListView;
	private GridView mGridViewNewPurchase, mGridViewPurchaseDetail;
	private PurchasePicAdapter mPurchasePicAdapter;
	private PurchaseListAdapter mPurchaseListAdapter;
	private PurchaseGoodsListAdapter mPurchaseGoodsListAdapter;
	private SelectPicAdapter mSelectPicAdapter;
	private ArrayList<GalleryPicEntity> selectedImageList;
	private ArrayList<String> selectedImagePathArray;
	/**
	 * 当前批次号
	 */
	private String mCurrentBatchId;
	
	/**
	 * 当前进货单货品总价 单位分
	 */
	private long mTotal = 0;
	private SupplierEntity supplier;
	
	// goods display entity
	private List<PurchaseGoodsEntity> mPurchaseGoodsEntities = new ArrayList<PurchaseGoodsEntity>();
	
	// goods detail dialog
	private AlertDialogPro mPurchaseGoodsDialog, mSupplierListDialog;
	private EditText etGoodsCount, etGoodsPrice;
	
	// common process dialog
	private AlertDialog mSavingPurchaseProcessDialog;
	
	/** 菜单dialog */
	private BottomMenuDialog<Integer> mBottomMenuDialog;
	private List<MenuItemEntity> menuList;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.purchase_fragment, container,false);
		initView();
		return contentView;
	}
	
	private void initView()
	{
		// new purchase pic&goods area
		llNewPurchaseView = (LinearLayout) contentView.findViewById(R.id.llNewPurchaseView);
		//purchase detail
		viewPurchaseDetail = contentView.findViewById(R.id.viewPurchaseDetail);
		
		//TextView
		tvPurchaseCounts = (TextView) contentView.findViewById(R.id.tvPurchaseCounts);
		btnToNewPurchaseRecord = (TextView) contentView.findViewById(R.id.btnToNewPurchaseRecord);
		tvNoPurchasePic = (TextView) contentView.findViewById(R.id.tvNoPurchasePic);
		btnToNewPurchaseRecord.setOnClickListener(this);
		
		// Buttons
		btnSaveNewPurchaseRecord = (Button) contentView.findViewById(R.id.btnSaveNewPurchaseRecord);
		btnSaveNewPurchaseRecord.setOnClickListener(this);
		btnScanGoods = (Button) contentView.findViewById(R.id.btnScanGoods);
		btnScanGoods.setOnClickListener(this);
		btnManualSelect = (Button) contentView.findViewById(R.id.btnManualSelect);
		btnManualSelect.setOnClickListener(this);
		//EditText
		etBatchId = (EditText) contentView.findViewById(R.id.et_batch_id);
		etOutId = (EditText) contentView.findViewById(R.id.et_out_id);
		etTotal = (ClearableEditText) contentView.findViewById(R.id.et_total);
		etTotal.setSubfix("元");
		
		// purchases list view
		refreshListView = (PullToRefreshListView) contentView.findViewById(R.id.refreshListView);
		refreshListView.setOnRefreshListener(this);
		refreshListView.setMode(Mode.BOTH);
		lvPurchaseList = refreshListView.getRefreshableView();
		//lvPurchaseList = (ListView) contentView.findViewById(R.id.lv_purchase_list);
		mPurchaseListAdapter = new PurchaseListAdapter(getActivity());
		lvPurchaseList.setAdapter(mPurchaseListAdapter);
		lvPurchaseList.setOnItemClickListener(this);
		//purchase detail images gridview
		mGridViewPurchaseDetail = (GridView) contentView.findViewById(R.id.gvPurchaseWebAvatars);
		mPurchasePicAdapter = new PurchasePicAdapter(getActivity());
		mGridViewPurchaseDetail.setAdapter(mPurchasePicAdapter);
		
		
		//purchase goods list
		lvPurhcaseGoodsList = (ListView) contentView.findViewById(R.id.lv_purchase_detail_goods);
		mPurchaseGoodsListAdapter = new PurchaseGoodsListAdapter(getActivity());
		lvPurhcaseGoodsList.setAdapter(mPurchaseGoodsListAdapter);
		
		lvPurchaseGoodsListFromWeb = (ListView) contentView.findViewById(R.id.lvPurchaseGoodsFromWeb);
		
		//new purchase images gridview
		mGridViewNewPurchase = (GridView) contentView.findViewById(R.id.gv_purchase_avatars);
		mSelectPicAdapter = new SelectPicAdapter(getActivity());
		mGridViewNewPurchase.setAdapter(mSelectPicAdapter);
		mGridViewNewPurchase.setOnItemClickListener(this);
		mGridViewNewPurchase.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (0 != position) {
					initMenuView();
					mBottomMenuDialog.showDialog(position);
					return true;
				}
				return false;
			}
		});
		selectedImageList = new ArrayList<GalleryPicEntity>();
		selectedImageList.add(new GalleryPicEntity());
		setGridView(selectedImageList.size());
		mSelectPicAdapter.setData(selectedImageList);
		
		refreshListView.setRefreshing();
		loadPurchaseList(true,false);

		btnToNewPurchaseRecord.callOnClick();
	}
	
	
	/**
	 * 初始化移除菜单view
	 * 
	 * @param isWeb
	 *            当前调用该方法的是网络图片列表，还是本地图片列表
	 *            <br/>
	 *            copy from {@link ProductAddNewActivity} by wyp
	 */
	private void initMenuView() {
		if (menuList == null) {
			menuList = new ArrayList<MenuItemEntity>();
			MenuItemEntity menuItemEntity1 = new MenuItemEntity(1, "移除这张照片", MenuItemEntity.Color.ALERT);
			menuList.add(menuItemEntity1);
			mBottomMenuDialog = new BottomMenuDialog<Integer>(getActivity());
			mBottomMenuDialog.setMenuList(menuList);
		}
		mBottomMenuDialog.setOnItemClickListener(new BottomMenuDialog.OnItemClickListener<Integer>() {
			@Override
			public void onItemClick(int menuId, Integer prams) {
				if (selectedImageList != null && selectedImageList.size() > 0) {
					selectedImageList.remove(prams);
					mSelectPicAdapter.remove(prams);
					selectedImagePathArray.remove(prams-1);
					//删除图片后重新设置gridview宽度
					setGridView(selectedImageList.size());
				}
			}
		});
	}
	
	/**
	 *动态设置 GridView 列数和宽度
	 *@author wyp
	 */
	private void setGridView(int itemAmount)
	{
		//item宽度120dp
		int length = 120;
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		//item间隔为10dp
		int gridviewWidth = (int) (itemAmount * (length + 10) * density);
		int itemWidth = (int) (length * density);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
		mGridViewNewPurchase.setLayoutParams(params); // 重点
		mGridViewNewPurchase.setColumnWidth(itemWidth); // 重点
		mGridViewNewPurchase.setStretchMode(GridView.NO_STRETCH);
		mGridViewNewPurchase.setNumColumns(itemAmount);
	}
	
	/**
	 * 加载采购清单
	 * @param isRefresh 是否是第一次加载
	 * @param isShowFirst 是否显示第一个item详细信息
	 */
	private void loadPurchaseList(final boolean isRefresh, final boolean isShowFirst)
	{
		PurchaseManager.getInstance().getPurchaseList(mPurchaseListAdapter.getNext_page(isRefresh),PurchaseActivity.DEFAUL_PAGE_SIZE,
				new ManagerCallBack2<List<PurchaseEntity>,Integer>() {
			
			@Override
			public void onSuccess(List<PurchaseEntity> returnContent, Integer params) {
				super.onSuccess(returnContent,params);
				mPurchaseListAdapter.setData(isRefresh, returnContent);;
				mPurchaseListAdapter.notifyDataSetChanged();
				tvPurchaseCounts.setText(String.valueOf(params));
				refreshListView.onRefreshComplete();
				if(isShowFirst){
					PurchaseEntity e = (PurchaseEntity) mPurchaseListAdapter.getItem(0);
					showPurchase(e);
					lvPurchaseList.setItemChecked(1, true);
				}
			}
			
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
				refreshListView.onRefreshComplete();
			}
			
		});
	}
	
	/**
	 * load purchase products info
	 * @param batchid
	 */
	private void loadPurchaseProducts(String batchid)
	{
		PurchaseManager.getInstance().getPurchaseProducts(batchid, new ManagerCallBack<List<PurchaseGoodsEntity>>() {
			@Override
			public void onSuccess(List<PurchaseGoodsEntity> returnContent) {
				super.onSuccess(returnContent);
				lvPurchaseGoodsListFromWeb.setAdapter(mPurchaseGoodsListAdapter);
				mPurchaseGoodsListAdapter.updateData(returnContent);
				mPurchaseGoodsListAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	
	/**
	 * 货品信息dialog
	 * @param entity
	 * @param positiveListener
	 * @return
	 */
	private Dialog getGoodEditDialog(PurchaseGoodsEntity entity, DialogInterface.OnClickListener positiveListener)
	{
		//views 
		final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.purchase_fragment_goods_detail_dialog, null);
		etGoodsCount = (EditText) dialogView.findViewById(R.id.etGoodsCount);
		etGoodsPrice = (EditText) dialogView.findViewById(R.id.etGoodsPrice);
		tvSelectSupplier = (TextView) dialogView.findViewById(R.id.tvSelectSupplier);
		tvSelectSupplier.setOnClickListener(this);
		tvPurchaseGoodsDetailUnit = (TextView) dialogView.findViewById(R.id.tvUnit);
		String str_count = tvPurchaseGoodsDetailUnit.getText().toString()+entity.getProduct_unit();
		tvPurchaseGoodsDetailUnit.setText(str_count);
		
		Builder builder = MyDialogManager.builder(getActivity());
		mPurchaseGoodsDialog = builder.setTitle(entity.getProduct_name())
		.setView(dialogView)
		.setPositiveButton("确定", positiveListener)
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		mPurchaseGoodsDialog.setCanceledOnTouchOutside(false);
		return mPurchaseGoodsDialog;
	}
	
	/**
	 * 计算当前货物清单总价
	 */
	private void generateGoodsTotal()
	{
		mTotal = 0;
		for(PurchaseGoodsEntity e : mPurchaseGoodsEntities)
		{
			mTotal = Tool.mulAsLong(String.valueOf(e.getPurchase_count()), String.valueOf(e.getPurchase_price())) + mTotal;
		}
	}
	
	/**
	 * 添加商品到货物列表
	 * @param scanResult
	 */
	private void addProductToGoodsList(final String scanResult)
	{
		final SimpleProgressDialog mSimpleProgressDialog;
		mSimpleProgressDialog = new SimpleProgressDialog(getActivity(), true);
		mSimpleProgressDialog.setText("正在加载数据……").showDialog();
		ProductManager.getInstance().getProductDetail(MyApplication.getStoreid(), scanResult,
				new ManagerCallBack<ProductEntity>() {
					@Override
					public void onSuccess(final ProductEntity returnContent) {
						super.onSuccess(returnContent);
						mSimpleProgressDialog.cancleDialog();
						PurchaseGoodsEntity entity = new PurchaseGoodsEntity();
						entity.setProduct_name(returnContent.getProduct_name());
						entity.setProduct_qr(returnContent.getProduct_qr());
						entity.setProduct_unit(returnContent.getProduct_unit());
						final int index = mPurchaseGoodsEntities.indexOf(entity);
						
						// if goods list contains scanned product
						if(index >= 0)
						{
							entity = mPurchaseGoodsEntities.get(index);
							
							getGoodEditDialog(entity,new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									PurchaseGoodsEntity entity = mPurchaseGoodsEntities.get(index);
									entity.setPurchase_count(etGoodsCount.getText().toString());
									entity.setPurchase_price(Tool.yuanToFen(etGoodsPrice.getText().toString()));
									entity.setSupplier(supplier);
									
									//update list
									mPurchaseGoodsListAdapter.updateData(mPurchaseGoodsEntities);
									
									//update total price
									generateGoodsTotal();
									etTotal.setText(Tool.fenToYuan(mTotal));
									mPurchaseGoodsDialog.dismiss();
								}
							}  );
							//change current supplier
							supplier = entity.getSupplier();
							tvSelectSupplier.setText(supplier.getName());
							etGoodsCount.setText(entity.getPurchase_count());
							etGoodsPrice.setText(Tool.fenToYuan(entity.getPurchase_price()));
							
						}else{
							//默认没有供应商
							supplier = null;
							getGoodEditDialog(entity, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									
									
									PurchaseGoodsEntity entity = new PurchaseGoodsEntity();
									entity.setProduct_qr(returnContent.getProduct_qr());
									entity.setProduct_name(returnContent.getProduct_name());
									entity.setProduct_unit(returnContent.getProduct_unit());
									
									//货品数量
									String count = etGoodsCount.getText().toString();
									if(!InputVali.isFloatValue(count))
									{
										UITools.ToastMsg(getActivity(), "货品数量有误");
										return;
									}
									entity.setPurchase_count(count);
									
									//进货价
									String price = etGoodsPrice.getText().toString();
									if(!InputVali.isFloatAsFen(price))
									{
										UITools.ToastMsg(getActivity(), "进货价有误");
										return;
									}
									entity.setPurchase_price(Tool.yuanToFen(price));
									entity.setSupplier(supplier);
									
									//add to list
									mPurchaseGoodsEntities.add(entity);
									mPurchaseGoodsListAdapter.updateData(mPurchaseGoodsEntities);
									
									//update total price
									generateGoodsTotal();
									etTotal.setText(String.valueOf(Tool.fenToYuan(mTotal)));
									
									mPurchaseGoodsDialog.dismiss();
								}
							} );
						}
						mPurchaseGoodsDialog.show();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						UITools.ToastMsg(getActivity(), msg);
						mSimpleProgressDialog.cancleDialog();
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage("商品尚未入库，可以入库！");
						builder.setTitle("提示！");
						builder.setPositiveButton("入库", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								UITools.jumpProductAddNewActivity(getActivity(),
										ProductAddNewActivity.ScanStep.SCAN_FIRST,
										ProductAddNewActivity.ScanProductExist.NOT_EXIST, scanResult);
								getActivity().finish();
							}
						});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						builder.create().show();
					}
				});
	}
	
	/**
	 * save a new purchase
	 */
	private void savePurchase()
	{
		if(mCurrentBatchId == null || mCurrentBatchId.isEmpty())
		{
			UITools.ToastMsg(getActivity(), "请先点击【新增】按钮生成批次号");
			return;
		}
		
		if(mPurchaseGoodsEntities.size() == 0)
		{
			UITools.ToastMsg(getActivity(), "采购商品列表为空，请先添加商品");
			return;
		}
		
		mSavingPurchaseProcessDialog = MyDialogManager.buildProgress(getActivity());
		mSavingPurchaseProcessDialog.setMessage(getString(R.string.purchase_saving));
		mSavingPurchaseProcessDialog.setCanceledOnTouchOutside(false);
		mSavingPurchaseProcessDialog.show();
		String out_id = etOutId.getText().toString();
		
		PurchaseManager.getInstance().savePurchase(mCurrentBatchId, out_id, selectedImageList, mPurchaseGoodsEntities, new ManagerCallBack<String>() {

			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				mSavingPurchaseProcessDialog.dismiss();
				UITools.ToastMsg(getActivity(), getString(R.string.purchase_save_success));
				btnSaveNewPurchaseRecord.setVisibility(View.INVISIBLE);
				loadPurchaseList(true,true);
			}
			
			@Override
			public void onProgress(int progress) {
				if(progress == 0){
					mSavingPurchaseProcessDialog.setMessage("准备上传图片");
				}else{
					mSavingPurchaseProcessDialog.setMessage("正在上传图片  "+progress+"%");
				}
			}


			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				mSavingPurchaseProcessDialog.dismiss();
				UITools.ToastMsg(getActivity(), msg);
			}
		});
	}
	
	/**
	 * to new a purchase
	 */
	private void toNewPurchase()
	{
		mTotal = 0;
		mCurrentBatchId = Tool.generateBatchId();
		etBatchId.setText(mCurrentBatchId);
		etTotal.setText(Tool.fenToYuan(mTotal));
		etOutId.setText("");
		
		//reset datas
		mPurchaseGoodsEntities.clear();
		mPurchaseGoodsListAdapter.updateData(mPurchaseGoodsEntities);
		selectedImageList.clear();
		selectedImageList.add(new GalleryPicEntity());
		setGridView(selectedImageList.size());
		mSelectPicAdapter.setData(selectedImageList);
		
		//switch view
		viewPurchaseDetail.setVisibility(View.GONE);
		llNewPurchaseView.setVisibility(View.VISIBLE);
		btnSaveNewPurchaseRecord.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 查看某一采购信息
	 * @param entity
	 */
	private void showPurchase(PurchaseEntity entity){
		//switch view
		viewPurchaseDetail.setVisibility(View.VISIBLE);
		llNewPurchaseView.setVisibility(View.GONE);
		
		//set data
		if(entity.getPurchase_avatar() != null && !entity.getPurchase_avatar().isEmpty()){
			tvNoPurchasePic.setVisibility(View.GONE);
			mGridViewPurchaseDetail.setVisibility(View.VISIBLE);
			mPurchasePicAdapter.setData(entity.getPurchase_avatar());
		}else{
			//提示没有图片
			mGridViewPurchaseDetail.setVisibility(View.GONE);
			tvNoPurchasePic.setVisibility(View.VISIBLE);
		}
		etTotal.setText(Tool.fenToYuan(entity.getPurchase_amount()));
		etBatchId.setText(entity.getBatch_id());
		etOutId.setText(entity.getOut_id());
		//加载货物清单
		loadPurchaseProducts(entity.getBatch_id());
	}
	
	/**
	 * show suppliers
	 */
	private void showSupplierList() {
		
		SupplierManager.getInstance().loadSuppliers(new ManagerCallBack<List<SupplierEntity>>() {

			@Override
			public void onSuccess(final List<SupplierEntity> returnContent) {
				super.onSuccess(returnContent);
				final String[] supplierArray = new String[returnContent.size()];
				for(int i=0;i<returnContent.size();i++)
				{
					supplierArray[i] = returnContent.get(i).getName();
				}
				
				Builder builder = MyDialogManager.builder(getActivity());
				mSupplierListDialog = builder.setTitle("选择供应商")
				.setItems(supplierArray,new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						tvSelectSupplier.setText(supplierArray[which]);
						mSupplierListDialog.hide();
						mPurchaseGoodsDialog.show();
						supplier = returnContent.get(which);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSupplierListDialog.hide();
						mPurchaseGoodsDialog.show();
					}
				}).create();
				
				mPurchaseGoodsDialog.hide();
				
				mSupplierListDialog.getWindow().getAttributes().windowAnimations = R.style.AnimationSecondLevelDialog;
				mSupplierListDialog.setCanceledOnTouchOutside(false);
				mSupplierListDialog.show();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnToNewPurchaseRecord:
			toNewPurchase();
			break;
		case R.id.btnSaveNewPurchaseRecord:
			savePurchase();
			break;
		case R.id.btnScanGoods:
			if(mCurrentBatchId == null || mCurrentBatchId.isEmpty()){
				UITools.ToastMsg(getActivity(), "请先点击【新增】按钮生成批次号");
				return;
			}
			//scan product
			Intent intent = new Intent(getActivity(), ProductScanCodeActivity.class);
			intent.putExtra(ProductScanCodeActivity.KEY_OPERATION_TYPE, ProductScanCodeActivity.OperationType.GET_CODE);
			startActivityForResult(intent, CODE_SCAN_BAR_REQUEST);
			break;
		case R.id.btnManualSelect:
			if(mCurrentBatchId == null || mCurrentBatchId.isEmpty()){
				UITools.ToastMsg(getActivity(), "请先点击【新增】按钮生成批次号");
				return;
			}
			//手动选择商品
			startActivityForResult(new Intent(getActivity(),ProductSelectionActivity.class), ProductSelectionActivity.SELECT_RPODUCT_REQUEST);
			break;
		case R.id.tvSelectSupplier:
			showSupplierList();
			break;
		default:
			break;
		}
	}
	


	

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (FragmentActivity.RESULT_OK == resultCode) {
			switch (requestCode) {
			case CODE_SELECT_IMAGE:
				// 获取选择到的照片
				selectedImagePathArray = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				selectedImageList = new ArrayList<GalleryPicEntity>();
				selectedImageList.add(new GalleryPicEntity());
				for(String str : selectedImagePathArray){
					GalleryPicEntity e = new GalleryPicEntity();
					e.imagePath = str;
					selectedImageList.add(e);
				}
				setGridView(selectedImageList.size());
				mSelectPicAdapter.setData(selectedImageList);
				break;
			case CODE_SCAN_BAR_REQUEST:
				addProductToGoodsList(data.getExtras().getString(ProductScanCodeActivity.KEY_SCAN_RESULT));
				break;
			case ProductSelectionActivity.SELECT_RPODUCT_REQUEST:
				ProductEntity e = (ProductEntity)data.getExtras().getSerializable(ProductSelectionActivity.DATA);
				addProductToGoodsList(QRCodeTools.PRODUCT+e.getProduct_qr());
				break;
			}
		}
	}

	/**
	 * purchase list 和新增 purchase GridView 共用 
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		// 点击了 purchase list item
		if(parent instanceof ListView)
		{
			PurchaseEntity entity = mPurchaseListAdapter.getList().get(position - 1);
			showPurchase(entity);
			
		}
		// 点击了 新增purchase的GridView item
		else if(parent instanceof GridView)
		{
			GalleryPicEntity imageItem = selectedImageList.get(position);
			if (null != imageItem) {
				// 点击添加图片按钮
				if (Tool.isEmpty(imageItem.imagePath)) {
					Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
					intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
					intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
					intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
					if(selectedImagePathArray != null && selectedImagePathArray.size()>0){
	                    intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, selectedImagePathArray);
	                }
					startActivityForResult(intent, CODE_SELECT_IMAGE);
				} else {
					if (null != selectedImagePathArray && selectedImagePathArray.size() > 0) {
						Intent intent = new Intent(getActivity(), GalleryPicPreviewActivity.class);
						Bundle extras = new Bundle();
						extras.putSerializable(GalleryPicPreviewActivity.KEY_SELECTED_IMAGE, selectedImagePathArray);
						intent.putExtras(extras);
						intent.putExtra(GalleryPicPreviewActivity.KEY_SELECTED_INDEX, position-1 );
						startActivity(intent);
					}
				}
			}
		}
		
		
		
		
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadPurchaseList(refreshListView.isHeaderShown(),false);
	}
}
