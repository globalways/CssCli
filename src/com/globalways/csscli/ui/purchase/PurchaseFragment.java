package com.globalways.csscli.ui.purchase;

import java.util.ArrayList;
import java.util.List;

import com.alertdialogpro.AlertDialogPro;
import com.alertdialogpro.AlertDialogPro.Builder;
import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.entity.PurchaseEntity;
import com.globalways.csscli.entity.PurchaseGoodsEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.http.manager.PurchaseManager;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.ui.gallery.GalleryActivity;
import com.globalways.csscli.ui.gallery.GalleryPicEntity;
import com.globalways.csscli.ui.gallery.GalleryPicPreviewActivity;
import com.globalways.csscli.ui.product.ProductAddNewActivity;
import com.globalways.csscli.ui.product.ProductScanCodeActivity;
import com.globalways.csscli.view.BottomMenuDialog;
import com.globalways.csscli.view.CommonDialogManager;
import com.globalways.csscli.view.MenuItemEntity;
import com.globalways.csscli.view.SimpleProgressDialog;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class PurchaseFragment extends Fragment implements OnClickListener, OnItemClickListener {

	
	private static final int CODE_SELECT_IMAGE = 11;
	private static final int CODE_SCAN_BAR_REQUEST = 12;
	private View contentView;
	private Button btnToNewPurchaseRecord, btnSaveNewPurchaseRecord, btnAddGoods;
	private EditText etBatchId, etOutId, etTotal;
	
	private LinearLayout llNewPurchaseView;
	private ListView lvPurchaseList, lvPurhcaseGoodsList;
	private GridView mGridViewNewPurchase, mGridViewPurchaseDetail;
	private PurchasePicAdapter mPurchasePicAdapter;
	private PurchaseListAdapter mPurchaseListAdapter;
	private PurchaseGoodsListAdapter mPurchaseGoodsListAdapter;
	private SelectPicAdapter mSelectPicAdapter;
	private ArrayList<GalleryPicEntity> selectedImageList;
	/**
	 * 当前批次号
	 */
	private String mCurrentBatchId;
	
	/**
	 * 总价
	 */
	private float mTotal = 0f;
	
	// goods display entity
	private List<PurchaseGoodsEntity> mPurchaseGoodsEntities = new ArrayList<PurchaseGoodsEntity>();
	
	// goods detail dialog
	private AlertDialogPro mGoodsEditDialog;
	private EditText etGoodsCount, etGoodsTotal;
	
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
		
		// Buttons
		btnToNewPurchaseRecord = (Button) contentView.findViewById(R.id.btnToNewPurchaseRecord);
		btnToNewPurchaseRecord.setOnClickListener(this);
		btnSaveNewPurchaseRecord = (Button) contentView.findViewById(R.id.btnSaveNewPurchaseRecord);
		btnSaveNewPurchaseRecord.setOnClickListener(this);
		btnAddGoods = (Button) contentView.findViewById(R.id.btn_title_new_goods);
		btnAddGoods.setOnClickListener(this);
		//EditText
		etBatchId = (EditText) contentView.findViewById(R.id.et_batch_id);
		etOutId = (EditText) contentView.findViewById(R.id.et_out_id);
		etTotal = (EditText) contentView.findViewById(R.id.et_total);
		// purchases list view
		lvPurchaseList = (ListView) contentView.findViewById(R.id.lv_purchase_list);
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
		
		//new purchase images gridview
		mGridViewNewPurchase = (GridView) contentView.findViewById(R.id.gv_purchase_avatars);
		mSelectPicAdapter = new SelectPicAdapter(getActivity());
		mGridViewNewPurchase.setAdapter(mSelectPicAdapter);
		mGridViewNewPurchase.setOnItemClickListener(this);
		mGridViewNewPurchase.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (mSelectPicAdapter.getCount() - 1 != position) {
					initMenuView(false);
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
		loadPurchaseList();
	}
	
	
	/**
	 * 初始化移除菜单view
	 * 
	 * @param isWeb
	 *            当前调用该方法的是网络图片列表，还是本地图片列表
	 *            <br/>
	 *            copy from {@link ProductAddNewActivity} by wyp
	 */
	private void initMenuView(final boolean isWeb) {
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
				if (isWeb) {
//					if (picWebAdapter.getCount() > 0) {
//						picWebAdapter.remove(prams);
//					}
				} else {
					if (selectedImageList != null && selectedImageList.size() > 0) {
						selectedImageList.remove(prams);
						mSelectPicAdapter.remove(prams);
					}
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
		int length = 120;
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int gridviewWidth = (int) (itemAmount * (length + 4) * density);
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
	 */
	private void loadPurchaseList()
	{
		PurchaseManager.getInstance().getPurchaseList(MyApplication.getStoreid(), new ManagerCallBack<List<PurchaseEntity>>() {
			
			@Override
			public void onSuccess(List<PurchaseEntity> returnContent) {
				super.onSuccess(returnContent);
				mPurchaseListAdapter.setList(returnContent);
				mPurchaseListAdapter.notifyDataSetChanged();
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
		etGoodsTotal = (EditText) dialogView.findViewById(R.id.etGoodsTotal);
		 Builder builder = CommonDialogManager.createDialogBuilder(getActivity());
		mGoodsEditDialog = builder.setTitle(entity.getProduct_name())
		.setView(dialogView)
		.setPositiveButton("确定", positiveListener)
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		return mGoodsEditDialog;
	}
	
	/**
	 * 计算当前货物清单总价
	 */
	private void generateGoodsTotal()
	{
		mTotal = 0f;
		for(PurchaseGoodsEntity e : mPurchaseGoodsEntities)
		{
			mTotal = e.getAmount()*Float.parseFloat(e.getTotal()) + mTotal;
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
						entity.setQr(returnContent.getProduct_qr());
						final int index = mPurchaseGoodsEntities.indexOf(entity);
						
						// if goods list contains scaned product
						if(index >= 0)
						{
							entity = mPurchaseGoodsEntities.get(index);
							
							getGoodEditDialog(entity,new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									
									
									PurchaseGoodsEntity entity = mPurchaseGoodsEntities.get(index);
									entity.setAmount(Float.parseFloat(etGoodsCount.getText().toString()));
									entity.setTotal(etGoodsTotal.getText().toString());
									//update list
									mPurchaseGoodsListAdapter.updateData(mPurchaseGoodsEntities);
									
									//update total price
									generateGoodsTotal();
									etTotal.setText(String.valueOf(mTotal));
									mGoodsEditDialog.dismiss();
								}
							}  );
							etGoodsCount.setText(String.valueOf(entity.getAmount()));
							etGoodsTotal.setText(entity.getTotal());
							
						}else{
							getGoodEditDialog(entity, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									
									
									PurchaseGoodsEntity entity = new PurchaseGoodsEntity();
									entity.setQr(returnContent.getProduct_qr());
									entity.setProduct_name(returnContent.getProduct_name());
									entity.setAmount(Float.parseFloat(etGoodsCount.getText().toString()));
									entity.setTotal(etGoodsTotal.getText().toString());
									
									
									//add to list
									mPurchaseGoodsEntities.add(entity);
									mPurchaseGoodsListAdapter.updateData(mPurchaseGoodsEntities);
									
									//update total price
									generateGoodsTotal();
									etTotal.setText(String.valueOf(mTotal));
									
									mGoodsEditDialog.dismiss();
								}
							} );
						}
						mGoodsEditDialog.show();
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
		String out_id = etOutId.getText().toString();
		
		PurchaseManager.getInstance().savePurchase(mCurrentBatchId, out_id, selectedImageList, mPurchaseGoodsEntities, new ManagerCallBack<String>() {

			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * to new a purchase
	 */
	private void toNewPurchase()
	{
		mTotal = 0f;
		mCurrentBatchId = Tool.generateBatchId();
		etBatchId.setText(mCurrentBatchId);
		etTotal.setText(String.valueOf(mTotal));
		etOutId.setText(mCurrentBatchId);
		
		//reset datas
		mPurchaseGoodsEntities.clear();
		mPurchaseGoodsListAdapter.updateData(mPurchaseGoodsEntities);
		selectedImageList.clear();
		selectedImageList.add(new GalleryPicEntity());
		setGridView(selectedImageList.size());
		mSelectPicAdapter.setData(selectedImageList);
		
		//switch view
		mGridViewPurchaseDetail.setVisibility(View.GONE);
		llNewPurchaseView.setVisibility(View.VISIBLE);
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
		case R.id.btn_title_new_goods:
			//scan product
			Intent intent = new Intent(getActivity(), ProductScanCodeActivity.class);
			intent.putExtra(ProductScanCodeActivity.KEY_OPERATION_TYPE, ProductScanCodeActivity.OperationType.GET_CODE);
			startActivityForResult(intent, CODE_SCAN_BAR_REQUEST);
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
				selectedImageList = (ArrayList<GalleryPicEntity>) data
						.getSerializableExtra(GalleryActivity.KEY_SELECTED_IMAGE);
				selectedImageList.add(new GalleryPicEntity());
				setGridView(selectedImageList.size());
				mSelectPicAdapter.setData(selectedImageList);
				break;
			case CODE_SCAN_BAR_REQUEST:
				addProductToGoodsList(data.getExtras().getString(ProductScanCodeActivity.KEY_SCAN_RESULT));
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
			PurchaseEntity entity = mPurchaseListAdapter.getList().get(position);
			//switch view
			mGridViewPurchaseDetail.setVisibility(View.VISIBLE);
			llNewPurchaseView.setVisibility(View.GONE);
			
			//set data
			mPurchasePicAdapter.setData(entity.getPurchase_avatar());
			etTotal.setText(String.valueOf(entity.getPurchase_amount()));
			etBatchId.setText(entity.getBatch_id());
			etOutId.setText(entity.getOut_id());
		}
		// 点击了 新增purchase的GridView item
		else if(parent instanceof GridView)
		{
			GalleryPicEntity imageItem = selectedImageList.get(position);
			if (null != imageItem) {
				// 点击添加图片按钮
				if (imageItem.imageId <= 0) {
					Intent intent = new Intent(getActivity(), GalleryActivity.class);
					intent.putExtra(GalleryActivity.KEY_JUMP_PURPOSE, GalleryActivity.Purpose.MULTI_PIC.getType());
					Bundle extras = new Bundle();
					extras.putSerializable(GalleryActivity.KEY_SELECTED_IMAGE, selectedImageList);
					intent.putExtras(extras);
					startActivityForResult(intent, CODE_SELECT_IMAGE);
				} else {
					if (null != selectedImageList && selectedImageList.size() > 0) {
						ArrayList<String> imagePathList = new ArrayList<String>();
						for (int i = 0; i < selectedImageList.size(); i++) {
							GalleryPicEntity ii = selectedImageList.get(i);
							if (null != ii.imageBucketId && !"".equals(ii.imageBucketId)) {
								imagePathList.add(ii.imagePath);
							}
						}
						Intent intent = new Intent(getActivity(), GalleryPicPreviewActivity.class);
						Bundle extras = new Bundle();
						extras.putSerializable(GalleryPicPreviewActivity.KEY_SELECTED_IMAGE, imagePathList);
						intent.putExtras(extras);
						intent.putExtra(GalleryPicPreviewActivity.KEY_SELECTED_INDEX, position);
						startActivity(intent);
					}
				}
			}
		}
		
		
		
		
	}
}
