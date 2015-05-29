package com.globalways.csscli.ui.purchase;

import java.util.List;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.SupplierEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.SupplierManager;
import com.globalways.csscli.ui.product.ProductActivity;
import com.globalways.csscli.view.CommonDialogManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SupplierFragment extends Fragment implements OnClickListener,OnItemClickListener,OnRefreshListener<ListView> {

	private View fragmentView;
	private Button btnToNewSupplier, btnSaveSupplier, btnToDeleteSupplier;
	/**
	 * suppliers list
	 */
	private ListView mListView;
	private AlertDialog mCommonProccesDialog;
	private PullToRefreshListView refreshListView;
	private SuppliersListAdapter mSuppliersListAdapter;
	private EditText etSupplierName, etContactName, etContactPhone, etSupplierAddress, etComment;
	/**
	 * current suppliers list
	 */
	private List<SupplierEntity> currentList;
	private SupplierEntity currentEntity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.purchase_supplier_fragment, container, false);
		initView();
		initData();
		return fragmentView;
	}
	
	private void initView()
	{
		//Buttons
		btnToNewSupplier = (Button) fragmentView.findViewById(R.id.btnToNewSupplier);
		btnToNewSupplier.setOnClickListener(this);
		btnSaveSupplier = (Button) fragmentView.findViewById(R.id.btnSaveSupplier);
		btnSaveSupplier.setOnClickListener(this);
		btnToDeleteSupplier = (Button) fragmentView.findViewById(R.id.btnToDeleteSupplier);
		btnToDeleteSupplier.setOnClickListener(this);
		//EditTexts
		etSupplierName = (EditText) fragmentView.findViewById(R.id.etSupplierName);
		etComment = (EditText) fragmentView.findViewById(R.id.etComment);
		etContactName = (EditText) fragmentView.findViewById(R.id.etContactsName);
		etContactPhone	= (EditText) fragmentView.findViewById(R.id.etContactsPhone);
		etSupplierAddress = (EditText) fragmentView.findViewById(R.id.etSupplierAddress);
		
		
		//ListView
		refreshListView = (PullToRefreshListView) fragmentView.findViewById(R.id.refreshListView);
		refreshListView.setOnRefreshListener(this);
		refreshListView.setMode(Mode.BOTH);
		
		//mListView = (ListView) fragmentView.findViewById(R.id.lvSuppliersList);
		mListView = refreshListView.getRefreshableView();
		mSuppliersListAdapter = new SuppliersListAdapter(getActivity());
		mListView.setAdapter(mSuppliersListAdapter);
		mListView.setOnItemClickListener(this);
		
		mCommonProccesDialog = CommonDialogManager.createProgressDialog(getActivity());
	}
	
	private void initData()
	{
		SupplierManager.getInstance().loadSuppliers(new ManagerCallBack<List<SupplierEntity>>() {

			@Override
			public void onSuccess(List<SupplierEntity> returnContent) {
				super.onSuccess(returnContent);
				currentList = returnContent;
				mSuppliersListAdapter.updateData(returnContent);
				refreshListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				refreshListView.onRefreshComplete();
			}
			
			
		});
	}

	
	private void saveOrUpdate()
	{
		
		SupplierEntity entity;
		if(currentEntity == null)
		{
			entity = new SupplierEntity();
			mCommonProccesDialog.setMessage("保存中");
		}else
		{
			entity = currentEntity;
			mCommonProccesDialog.setMessage("更新数据");
		}
		entity.setName(etSupplierName.getText().toString());
		entity.setAddress(etSupplierAddress.getText().toString());
		entity.setContact(etContactName.getText().toString());
		entity.setTel(etContactPhone.getText().toString());
		entity.setComment(etComment.getText().toString());
		mCommonProccesDialog.show();
		SupplierManager.getInstance().saveSupplier(entity, new ManagerCallBack<String>() {

			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				mCommonProccesDialog.dismiss();
				Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				mCommonProccesDialog.dismiss();
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
			
		});
			
		
	}
	
	private void deleteEntity(SupplierEntity entity)
	{
		SupplierManager.getInstance().deleteSupplier(entity, new ManagerCallBack<String>() {

			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				clearCurrentEntity();
				Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
				refreshListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				refreshListView.onRefreshComplete();
			}
		});
	}
	
	private void detailEntity(SupplierEntity entity)
	{
		etComment.setText(entity.getComment());
		etContactName.setText(entity.getContact());
		etContactPhone.setText(entity.getTel());
		etSupplierAddress.setText(entity.getAddress());
		etSupplierName.setText(entity.getName());
		currentEntity = entity;
		
		btnToDeleteSupplier.setVisibility(View.VISIBLE);
	}
	
	private void clearCurrentEntity()
	{
		etComment.setText("");
		etContactName.setText("");
		etContactPhone.setText("");
		etSupplierAddress.setText("");
		etSupplierName.setText("");
		btnToDeleteSupplier.setVisibility(View.GONE);
		currentEntity = null;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnToNewSupplier:
			clearCurrentEntity();
			break;
		case R.id.btnSaveSupplier:
			saveOrUpdate();
			break;
		case R.id.btnToDeleteSupplier:
			deleteEntity(currentEntity);
			break;
		default:
			break;
		}
		
	}

	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(null != currentList && currentList.size() != 0)
			detailEntity(currentList.get(position-1));
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		initData();
	}

}
