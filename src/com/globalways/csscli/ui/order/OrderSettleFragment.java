package com.globalways.csscli.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.SettleEntity;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.BaseFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * 工单管理
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月24日 下午3:01:28
 */
public class OrderSettleFragment extends BaseFragment implements OnRefreshListener<ListView>, OnClickListener {
	
	private View fragmentView;
	private View viewSettleDetail, viewSettleNew;
	
	//settle list
	private PullToRefreshListView refreshListView;
	private ListView settlesListView;
	private TextView tvSettlesNum, btnToNewSettle;
	private Button btnSettleStatus;
	
	//settle detail
	private TextView tvSettleNo, tvSettleStatus, tvApplyType, tvApplyTime, tvEndTime, tvBankAccountName, tvBankAccount,
	tvPayTime, tvReceiveTime, tvCloseTime;
	private Button btnPay, btnReceive, btnShowOrders, btnAuditAuto, btnClose;
	
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
		btnSettleStatus = (Button) fragmentView.findViewById(R.id.btnSettleStatus);
		btnSettleStatus.setOnClickListener(this);
		tvSettlesNum = (TextView) fragmentView.findViewById(R.id.tvSettlesNum);
		btnToNewSettle = (TextView) fragmentView.findViewById(R.id.btnToNewSettle);
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
		btnShowOrders = (Button) fragmentView.findViewById(R.id.btnShowOrders);
		btnShowOrders.setOnClickListener(this);
		btnAuditAuto = (Button) fragmentView.findViewById(R.id.btnAuditAuto);
		btnAuditAuto.setOnClickListener(this);
		btnClose = (Button) fragmentView.findViewById(R.id.btnClose);
		btnClose.setOnClickListener(this);
		//settle new
		btnEndTime = (Button) fragmentView.findViewById(R.id.btnEndTime);
		btnNewSettle = (Button) fragmentView.findViewById(R.id.btnNewSettle);
		etComments = (EditText) fragmentView.findViewById(R.id.etComments);
	}

	private void showSettle(SettleEntity e){
		tvSettleNo.setText(e.getSerial_no());
		tvSettleStatus.setText(SettleStatus.valueOf(e.getStatus()).desc);
		tvApplyType.setText(e.getApply_type()+"");
		tvApplyTime.setText(Tool.formatDate(e.getApply_time()*1000));
		tvEndTime.setText(Tool.formatDate(e.getEnd_time()*1000));
		tvBankAccountName.setText(e.getBank_account_name());
		tvBankAccount.setText(e.getBank_account());
		tvPayTime.setText(Tool.formatDate(e.getPay_time()*1000));
		tvReceiveTime.setText(Tool.formatDate(e.getReceived_time()*1000));
		tvCloseTime.setText(Tool.formatDate(e.getClose_time() * 1000));
	}
	
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
