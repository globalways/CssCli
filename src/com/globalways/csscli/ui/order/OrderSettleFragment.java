package com.globalways.csscli.ui.order;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro.Builder;
import com.globalways.csscli.R;
import com.globalways.csscli.entity.OrderEntity;
import com.globalways.csscli.entity.SettleEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ManagerCallBack2;
import com.globalways.csscli.http.manager.OrderManager;
import com.globalways.csscli.http.manager.SettleManager;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.BaseFragment;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.view.CommonDialogManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * 工单管理
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月24日 下午3:01:28
 */
public class OrderSettleFragment extends BaseFragment implements OnRefreshListener<ListView>, OnClickListener, OnItemClickListener {
	
	private View fragmentView;
	private View viewSettleDetail, viewSettleNew;
	
	//settle list
	private PullToRefreshListView refreshListView;
	private ListView settlesListView;
	private TextView tvSettlesNum, btnToNewSettle;
	private Button btnSettleStatus;
	private SettleListAdapter mSettleListAdapter;
	private SettleStatus currentStatus;
	
	//settle detail
	private TextView tvSettleNo, tvSettleStatus, tvApplyType, tvApplyTime, tvEndTime, tvBankAccountName, tvBankAccount,
	tvPayTime, tvReceiveTime, tvCloseTime;
	private Button btnPay, btnReceive, btnShowOrders, btnAuditAuto, btnCloseSettle;
	
	//settle new
	private Button btnEndTime, btnNewSettle;
	private EditText etComments;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(fragmentView == null){
			fragmentView = inflater.inflate(R.layout.order_settle_fragment, container, false);
		}
		initView();
		refreshListView.setRefreshing();
		loadSettles(true);
		return fragmentView;
	}
	
	private void initView(){
		//views
		viewSettleDetail = fragmentView.findViewById(R.id.viewSettleDetail);
		viewSettleNew = fragmentView.findViewById(R.id.viewSettleNew);
		//settle list
		refreshListView = (PullToRefreshListView) fragmentView.findViewById(R.id.refreshListView);
		refreshListView.setMode(Mode.BOTH);
		refreshListView.setOnRefreshListener(this);
		settlesListView = refreshListView.getRefreshableView();
		mSettleListAdapter = new SettleListAdapter(getActivity());
		settlesListView.setAdapter(mSettleListAdapter);
		settlesListView.setOnItemClickListener(this);
		btnSettleStatus = (Button) fragmentView.findViewById(R.id.btnSettleStatus);
		btnSettleStatus.setOnClickListener(this);
		tvSettlesNum = (TextView) fragmentView.findViewById(R.id.tvSettlesNum);
		btnToNewSettle = (TextView) fragmentView.findViewById(R.id.btnToNewSettle);
		btnToNewSettle.setOnClickListener(this);
		//settle detail
		tvSettleNo = (TextView) fragmentView.findViewById(R.id.tvSettleNo);
		tvSettleStatus = (TextView) fragmentView.findViewById(R.id.tvSettleStatus);
		tvApplyType = (TextView) fragmentView.findViewById(R.id.tvApplyType);
		tvApplyTime = (TextView) fragmentView.findViewById(R.id.tvApplyTime);
		tvEndTime = (TextView) fragmentView.findViewById(R.id.tvEndTime);
		tvBankAccountName = (TextView) fragmentView.findViewById(R.id.tvBankAccountName);
		tvBankAccount = (TextView) fragmentView.findViewById(R.id.tvBankAccount);
		tvPayTime = (TextView) fragmentView.findViewById(R.id.tvPayTime);
		tvReceiveTime = (TextView) fragmentView.findViewById(R.id.tvReceiveTime);
		tvCloseTime = (TextView) fragmentView.findViewById(R.id.tvCloseTime);
		btnPay = (Button) fragmentView.findViewById(R.id.btnPay);
		btnPay.setOnClickListener(this);
		btnReceive = (Button) fragmentView.findViewById(R.id.btnReceive);
		btnReceive.setOnClickListener(this);
		btnCloseSettle = (Button) fragmentView.findViewById(R.id.btnCloseSettle);
		btnCloseSettle.setOnClickListener(this);
		btnAuditAuto = (Button) fragmentView.findViewById(R.id.btnAuditAuto);
		btnAuditAuto.setOnClickListener(this);
		btnShowOrders = (Button) fragmentView.findViewById(R.id.btnShowOrders);
		btnShowOrders.setOnClickListener(this);
		
		//settle new
		btnEndTime = (Button) fragmentView.findViewById(R.id.btnEndTime);
		btnNewSettle = (Button) fragmentView.findViewById(R.id.btnNewSettle);
		etComments = (EditText) fragmentView.findViewById(R.id.etComments);
	}

	/**
	 * 显示工单详情
	 * @param e
	 */
	private void showSettle(SettleEntity e){
		viewSettleNew.setVisibility(View.GONE);
		viewSettleDetail.setVisibility(View.VISIBLE);
		
		tvSettleNo.setText(e.getSerial_no());
		tvSettleStatus.setText(SettleStatus.valueOf(e.getStatus()).getDesc());
		tvApplyType.setText(SettleApplyType.valueOf(e.getApply_type()).getDesc());
		tvApplyTime.setText(Tool.formatDate(e.getApply_time()*1000));
		tvEndTime.setText(Tool.formatDate(e.getEnd_time()*1000));
		tvBankAccountName.setText(e.getBank_account_name());
		tvBankAccount.setText(e.getBank_account());
		tvPayTime.setText(Tool.formatDate(e.getPay_time()*1000));
		tvReceiveTime.setText(Tool.formatDate(e.getReceived_time()*1000));
		tvCloseTime.setText(Tool.formatDate(e.getClose_time() * 1000));
		
		if(e.getReturn_amount() < 0){
			btnPay.setVisibility(View.VISIBLE);
			btnReceive.setVisibility(View.INVISIBLE);
		}else if( e.getReturn_amount() > 0){
			btnReceive.setVisibility(View.VISIBLE);
			btnPay.setVisibility(View.INVISIBLE);
		}else if(e.getReturn_amount() == 0){
			btnReceive.setVisibility(View.INVISIBLE);
			btnPay.setVisibility(View.INVISIBLE);
		}
		
		if(!SettleStatus.valueOf(e.getStatus()).equals(SettleStatus.CLOSE)){
			btnCloseSettle.setVisibility(View.VISIBLE);
		}else btnCloseSettle.setVisibility(View.INVISIBLE);
	}
	
	private void showSettleStatus()
	{
		PopupMenu popup = new PopupMenu(getActivity(), btnSettleStatus);
		popup.getMenuInflater().inflate(R.menu.order_settle_status, popup.getMenu());
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menuSettleAll:
					btnSettleStatus.setText(getString(R.string.order_settle_status_all));
					currentStatus = null;
					break;
				case R.id.menuSettleWatting:
					btnSettleStatus.setText(getString(R.string.order_settle_status_watting));
					currentStatus = SettleStatus.WATTING;
					break;
				case R.id.menuSettleSubmit:
					btnSettleStatus.setText(getString(R.string.order_settle_status_created));
					currentStatus = SettleStatus.SUBMIT;
					break;
				case R.id.menuSettleAudit:
					btnSettleStatus.setText(getString(R.string.order_settle_status_audit));
					currentStatus = SettleStatus.AUDIT;
					break;
				case R.id.menuSettlePayed:
					btnSettleStatus.setText(getString(R.string.order_settle_status_payed));
					currentStatus = SettleStatus.PAYED;
					break;
				case R.id.menuSettleReceived:
					btnSettleStatus.setText(getString(R.string.order_settle_status_received));
					currentStatus = SettleStatus.RECEIVED;
					break;
				case R.id.menuSettleClosed:
					btnSettleStatus.setText(getString(R.string.order_settle_status_closed));
					currentStatus = SettleStatus.CLOSE;
					break;
				default:
					return false;
				}
				loadSettles(true);
				return true;
			}
		});
		popup.show();
	}
	
	/**
	 * 显示订单列表dialog
	 * @param list 订单列表
	 * @param count 订单统计
	 */
	private void showSettleOrders(List<OrderEntity> list , int count){
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.order_settle_fragment_order_list, null);
		Builder builder = CommonDialogManager.createDialogBuilder(getActivity());
		OrderListAdapter adapter = new OrderListAdapter(getActivity());
		ListView mListView = (ListView) view.findViewById(R.id.lvSettleOrders);
		mListView.setAdapter(adapter);
		adapter.setData(true, list);
		builder.setView(view).setTitle(tvSettleNo.getText()).
		setNegativeButton("返回", null).create().show();
	}
	
	private void loadSettleOrders(String sno){
		SettleManager.getInstance().loadSettleOrders(sno, new ManagerCallBack2<List<OrderEntity>, Integer>() {
			@Override
			public void onSuccess(List<OrderEntity> returnContent,
					Integer params) {
				super.onSuccess(returnContent, params);
				showSettleOrders(returnContent, params);
			}
			
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
				UITools.ToastMsg(getActivity(), "没有订单");
			}
		});
	}
	
	/**
	 * 加载工单列表
	 * @param isReload
	 */
	private void loadSettles(final boolean isReload){
		SettleManager.getInstance().loadSettles(currentStatus,mSettleListAdapter.getNext_page(isReload), 10, new ManagerCallBack2<List<SettleEntity>, Integer>() {
			@Override
			public void onSuccess(List<SettleEntity> returnContent,
					Integer params) {
				super.onSuccess(returnContent, params);
				mSettleListAdapter.setData(isReload, returnContent);
				tvSettlesNum.setText(String.valueOf(params));
				refreshListView.onRefreshComplete();
				
			}
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
				mSettleListAdapter.setData(isReload, null);
				tvSettlesNum.setText("0");
				refreshListView.onRefreshComplete();
			}
		});
	}
	
	/**
	 * 打款
	 */
	private void paySettle(){
		SettleManager.getInstance().paySettle(String.valueOf(tvSettleNo.getText()), new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				SettleManager.getInstance().getSettle(String.valueOf(tvSettleNo.getText()), new ManagerCallBack<SettleEntity>() {
					@Override
					public void onSuccess(SettleEntity returnContent) {
						super.onSuccess(returnContent);
						showSettle(returnContent);
					}
					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						UITools.ToastMsg(getActivity(), msg);
					}
				});
			}
			
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
			}
		});
	}
	
	private void receiveSettle(){
		SettleManager.getInstance().receiveSettle(tvSettleNo.getText().toString(), new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				SettleManager.getInstance().getSettle(tvSettleNo.getText().toString(), new ManagerCallBack<SettleEntity>() {
					@Override
					public void onSuccess(SettleEntity returnContent) {
						super.onSuccess(returnContent);
						showSettle(returnContent);
					}
					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						UITools.ToastMsg(getActivity(), msg);
					}
				});
			}
			
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
			}
		});
	}
	
	private void closeSettle(){
		SettleManager.getInstance().closeSettle(tvSettleNo.getText().toString(), new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				SettleManager.getInstance().getSettle(tvSettleNo.getText().toString(), new ManagerCallBack<SettleEntity>() {
					@Override
					public void onSuccess(SettleEntity returnContent) {
						super.onSuccess(returnContent);
						showSettle(returnContent);
					}
					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						UITools.ToastMsg(getActivity(), msg);
					}
				});
			}
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
			}
		});
	}
	
	@SuppressLint("NewApi")
	private void showAutoAuditResult(){
		Builder builder = CommonDialogManager.createDialogBuilder(getActivity());
		builder.setTitle("审计结果");
		builder.setView(R.layout.order_settle_audit_auto_result).
		setNegativeButton("返回", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadSettles(refreshListView.isHeaderShown());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnToNewSettle:
			viewSettleDetail.setVisibility(View.GONE);
			viewSettleNew.setVisibility(View.VISIBLE);
			break;
		case R.id.btnPay : paySettle(); break;
		case R.id.btnReceive : receiveSettle(); break;
		case R.id.btnCloseSettle : closeSettle(); break;
		case R.id.btnAuditAuto : showAutoAuditResult(); break;
		case R.id.btnSettleStatus : showSettleStatus(); break;
		case R.id.btnShowOrders : loadSettleOrders(tvSettleNo.getText().toString()); break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		showSettle(mSettleListAdapter.getList().get(position-1));
	}
}
