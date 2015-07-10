package com.globalways.csscli.ui.product;

import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;
import com.alertdialogpro.AlertDialogPro.Builder;
import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductCategoryEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.ui.BaseFragment;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.view.ClearableEditText;
import com.globalways.csscli.view.CommonDialogManager;

public class ProductCategoryManagerFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnItemLongClickListener{
	
	private static final int MAX_LEVEL = 3;
	
	private View fragmentView;
	private TextView tvCurrentCategory, tvCurrentLevel;
	private ClearableEditText etNewCategoryName;
	private ListView lvCategoryList;
	private ProductCategoryAdapter mProductCategoryAdapter;
	private Button btnNewCategory, btnLevelUp;
	//默认parent_id为-1
	private ProductCategoryEntity currentParent = new ProductCategoryEntity(-1);
	private int currentLevel = 1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(fragmentView == null){
			fragmentView = inflater.inflate(R.layout.product_category_management_fragment, container,false);
		}
		initView();
		return  fragmentView;
	}
	
	private void initView(){
		btnNewCategory = (Button) fragmentView.findViewById(R.id.btnNewCategory);
		btnNewCategory.setOnClickListener(this);
		btnLevelUp = (Button) fragmentView.findViewById(R.id.btnLevelUp);
		btnLevelUp.setOnClickListener(this);
		
		etNewCategoryName = (ClearableEditText) fragmentView.findViewById(R.id.etNewCategoryName);
		tvCurrentCategory = (TextView) fragmentView.findViewById(R.id.tvCurrentCategory);
		tvCurrentLevel = (TextView) fragmentView.findViewById(R.id.tvCurrentLevel);
		tvCurrentCategory.setText(currentParent.getName());
		tvCurrentLevel.setText(currentLevel+" 级分类");
		
		
		lvCategoryList = (ListView) fragmentView.findViewById(R.id.lvCategoryList);
		mProductCategoryAdapter = new ProductCategoryAdapter(getActivity());
		lvCategoryList.setAdapter(mProductCategoryAdapter);
		lvCategoryList.setOnItemClickListener(this);
		lvCategoryList.setOnItemLongClickListener(this);
		loadCategoryChildren(currentParent.getId());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNewCategory:
			if(etNewCategoryName.getText().toString().isEmpty()){
				UITools.ToastMsg(getActivity(), "请先输入分类名称");
				return;
			}
			newCategory(currentParent.getId(), etNewCategoryName.getText().toString());
			break;
		case R.id.btnLevelUp:
			//当前显示为一级分类,不能返回到上一级
			if(currentParent.getId() == -1){
				return;
			}
			currentParent = mProductCategoryAdapter.getEntityById(currentParent.getParent_id());
			currentLevel--;
			loadCategoryChildren(currentParent.getId());
			tvCurrentCategory.setText(currentParent.getName());
			tvCurrentLevel.setText(currentLevel+" 级分类");
			if(currentLevel == 1){
				btnLevelUp.setVisibility(View.INVISIBLE);
			}
			if(currentLevel < MAX_LEVEL){
				lvCategoryList.setOnItemClickListener(this);
			}
			break;
		default:
			break;
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
				mProductCategoryAdapter.setList(returnContent);
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				mProductCategoryAdapter.setList(null);
				UITools.ToastMsg(getActivity(), msg);
			}
			
		});
	}
	
	private void newCategory(final int parent_id, String name){
		ProductManager.getInstance().newCategory(parent_id, name, new ManagerCallBack<String>() {

			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				UITools.ToastMsg(getActivity(), "新增分类成功");
				loadCategoryChildren(parent_id);
				etNewCategoryName.getText().clear();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
			}
		});
	}
	
	private void deleteCategory(int cid){
		ProductManager.getInstance().deleteCategory(cid, new ManagerCallBack<String>() {

			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				//重新加载分类列表
				loadCategoryChildren(currentParent.getId());
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
			}
			
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		currentParent = (ProductCategoryEntity) mProductCategoryAdapter.getItem(position);
		loadCategoryChildren(currentParent.getId());
		currentLevel++;
		tvCurrentCategory.setText(currentParent.getName());
		tvCurrentLevel.setText(currentLevel+" 级分类");
		if(currentLevel == MAX_LEVEL){
			lvCategoryList.setOnItemClickListener(null);
		}
		if(currentLevel > 1){
			btnLevelUp.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final ProductCategoryEntity item = (ProductCategoryEntity) mProductCategoryAdapter.getItem(position);
		AlertDialogPro.Builder builder = CommonDialogManager.createDialogBuilder(getActivity()); 
		builder.setTitle("选择删除分类: "+item.getName())
		.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteCategory(item.getId());
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create().show();
		return true;
	}
	
}
