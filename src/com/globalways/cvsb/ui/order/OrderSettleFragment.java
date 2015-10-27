package com.globalways.cvsb.ui.order;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.globalways.cvsb.R;
import com.globalways.cvsb.Config;
import com.globalways.cvsb.entity.OrderEntity;
import com.globalways.cvsb.entity.SettleAuditEntity;
import com.globalways.cvsb.entity.SettleEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.ManagerCallBack2;
import com.globalways.cvsb.http.manager.OrderManager;
import com.globalways.cvsb.http.manager.SettleManager;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.BaseFragment;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.view.MyDialogManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.mechat.mechatlibrary.MCClient;
import com.mechat.mechatlibrary.MCOnlineConfig;
import com.mechat.mechatlibrary.MCUserConfig;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * 工单管理
 * 
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月24日 下午3:01:28
 */
public class OrderSettleFragment extends BaseFragment
		implements OnRefreshListener<ListView>, OnClickListener, OnItemClickListener, OnDateSetListener {

	private View fragmentView;
	private View viewSettleDetail, viewSettleNew;

	// settle list
	private PullToRefreshListView refreshListView;
	private ListView settlesListView;
	private TextView tvSettlesNum, btnToNewSettle;
	private Button btnSettleStatus;
	private SettleListAdapter mSettleListAdapter;
	private SettleStatus currentStatus;

	// settle detail
	private SettleEntity currentSettle;
	private TextView tvSettleNo, tvSettleStatus, tvApplyType, tvApplyTime, tvEndTime, tvBankAccountName, tvBankAccount,
			tvComment, tvLateFee, tvSerialAmount, tvCashAmount, tvOnlineAmount, tvServiceFee, tvReturnAmount, tvPayTime,
			tvReceiveTime, tvCloseTime;
	private Button btnPay, btnReceive, btnShowOrders, btnAuditAuto, btnCloseSettle, btnDiscuss;

	// settle new
	private static final String NEW_SETTLE_ENDDATE = "new_settle_end_date";
	private Button btnEndTime, btnNewSettle;
	private EditText etComments;
	private Calendar endDate;
	private DatePickerDialog datePickerDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragmentView == null) {
			fragmentView = inflater.inflate(R.layout.order_settle_fragment, container, false);
		}
		initView();
		refreshListView.setRefreshing();
		loadSettles(true);
		return fragmentView;
	}

	private void initView() {
		// views
		viewSettleDetail = fragmentView.findViewById(R.id.viewSettleDetail);
		viewSettleNew = fragmentView.findViewById(R.id.viewSettleNew);
		// settle list
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
		// settle detail
		tvSettleNo = (TextView) fragmentView.findViewById(R.id.tvSettleNo);
		tvSettleStatus = (TextView) fragmentView.findViewById(R.id.tvSettleStatus);
		tvApplyType = (TextView) fragmentView.findViewById(R.id.tvApplyType);
		tvApplyTime = (TextView) fragmentView.findViewById(R.id.tvApplyTime);
		tvEndTime = (TextView) fragmentView.findViewById(R.id.tvEndTime);
		tvBankAccountName = (TextView) fragmentView.findViewById(R.id.tvBankAccountName);
		tvBankAccount = (TextView) fragmentView.findViewById(R.id.tvBankAccount);

		tvSerialAmount = (TextView) fragmentView.findViewById(R.id.tvSerialAmount);
		tvCashAmount = (TextView) fragmentView.findViewById(R.id.tvCashAmount);
		tvOnlineAmount = (TextView) fragmentView.findViewById(R.id.tvOnlineAmount);
		tvServiceFee = (TextView) fragmentView.findViewById(R.id.tvServiceFee);
		tvReturnAmount = (TextView) fragmentView.findViewById(R.id.tvReturnAmount);
		tvComment = (TextView) fragmentView.findViewById(R.id.tvComment);
		tvLateFee = (TextView) fragmentView.findViewById(R.id.tvLateFee);

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
		btnDiscuss = (Button) fragmentView.findViewById(R.id.btnDiscuss);
		btnDiscuss.setOnClickListener(this);

		// settle new
		btnEndTime = (Button) fragmentView.findViewById(R.id.btnEndTime);
		btnEndTime.setOnClickListener(this);
		btnNewSettle = (Button) fragmentView.findViewById(R.id.btnNewSettle);
		btnNewSettle.setOnClickListener(this);
		etComments = (EditText) fragmentView.findViewById(R.id.etComments);
		
		endDate = Calendar.getInstance();
		btnEndTime.setText(Tool.formatDate(endDate.getTimeInMillis()));
	}

	/**
	 * 显示工单详情
	 * 
	 * @param e
	 */
	private void showSettle(SettleEntity e) {
		currentSettle = e;
		viewSettleNew.setVisibility(View.GONE);
		viewSettleDetail.setVisibility(View.VISIBLE);

		tvSettleNo.setText(e.getSerial_no());
		tvSettleStatus.setText(SettleStatus.valueOf(e.getStatus()).getDesc());

		tvApplyType.setText(SettleApplyType.valueOf(e.getApply_type()).getDesc());
		tvApplyTime.setText(Tool.formatDateTime(e.getApply_time() * 1000));
		tvEndTime.setText(Tool.formatDate(e.getEnd_time() * 1000));
		tvComment.setText(e.getComment());

		tvBankAccountName.setText(e.getBank_account_name());
		tvBankAccount.setText(e.getBank_account());

		tvSerialAmount.setText("￥" + Tool.fenToYuan(e.getSerial_amount()));
		tvCashAmount.setText("￥" + Tool.fenToYuan(e.getCash_amount()));
		tvOnlineAmount.setText("￥" + Tool.fenToYuan(e.getOnline_amount()));
		tvServiceFee.setText("￥" + Tool.fenToYuan(e.getService_fee()));
		tvReturnAmount.setText("￥" + Tool.fenToYuan(e.getReturn_amount()));
		tvLateFee.setText("￥" + Tool.fenToYuan(e.getLate_fee()));

		tvPayTime.setText(Tool.formatDateTime(e.getPay_time() * 1000));
		tvReceiveTime.setText(Tool.formatDateTime(e.getReceived_time() * 1000));
		tvCloseTime.setText(Tool.formatDateTime(e.getClose_time() * 1000));

		if (e.getReturn_amount() < 0 && e.getStatus() == SettleStatus.AUDIT_COMPLETE.getCode()) {
			btnPay.setVisibility(View.VISIBLE);
		} else {
			btnPay.setVisibility(View.INVISIBLE);
		}

		if (e.getReturn_amount() > 0 && e.getStatus() == SettleStatus.PAYED.getCode()) {
			btnReceive.setVisibility(View.VISIBLE);
		} else {
			btnReceive.setVisibility(View.INVISIBLE);
		}

		if (e.getApply_type() == SettleApplyType.STORE.getCode() && e.getStatus() == SettleStatus.RECEIVED.getCode()) {
			btnCloseSettle.setVisibility(View.VISIBLE);
		} else {
			btnCloseSettle.setVisibility(View.INVISIBLE);
		}
		//
		//
		// if(e.getReturn_amount() < 0 && e.getStatus() <
		// SettleStatus.PAYED.getCode()){
		// btnPay.setVisibility(View.VISIBLE);
		// btnReceive.setVisibility(View.INVISIBLE);
		// }else if( e.getReturn_amount() > 0 && e.getStatus() <
		// SettleStatus.RECEIVED.getCode()){
		// btnReceive.setVisibility(View.VISIBLE);
		// btnPay.setVisibility(View.INVISIBLE);
		// }else if(e.getReturn_amount() == 0){
		// btnReceive.setVisibility(View.INVISIBLE);
		// btnPay.setVisibility(View.INVISIBLE);
		// }
		//
		// if(!SettleStatus.valueOf(e.getStatus()).equals(SettleStatus.CLOSE)){
		// btnCloseSettle.setVisibility(View.VISIBLE);
		// }else{
		// btnCloseSettle.setVisibility(View.INVISIBLE);
		// btnReceive.setVisibility(View.INVISIBLE);
		// btnPay.setVisibility(View.INVISIBLE);
		// }
	}

	private void showSettleStatus() {
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
				// case R.id.menuSettleWatting:
				// btnSettleStatus.setText(getString(R.string.order_settle_status_watting));
				// currentStatus = SettleStatus.WATTING;
				// break;
				case R.id.menuSettleSubmit:
					btnSettleStatus.setText(getString(R.string.order_settle_status_created));
					currentStatus = SettleStatus.SUBMIT;
					break;
				case R.id.menuSettleAudit:
					btnSettleStatus.setText(getString(R.string.order_settle_status_audit));
					currentStatus = SettleStatus.AUDIT;
					break;
				case R.id.menuSettleAuditComplete:
					btnSettleStatus.setText(getString(R.string.order_settle_status_audit_complete));
					currentStatus = SettleStatus.AUDIT_COMPLETE;
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
	 * 
	 * @param list
	 *            订单列表
	 * @param count
	 *            订单统计
	 */
	private void showSettleOrders(List<OrderEntity> list, int count) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.order_settle_fragment_order_list, null);
		Builder builder = MyDialogManager.builder(getActivity());
		OrderListAdapter adapter = new OrderListAdapter(getActivity());
		ListView mListView = (ListView) view.findViewById(R.id.lvSettleOrders);
		mListView.setAdapter(adapter);
		adapter.setData(true, list);
		builder.setView(view).setTitle(tvSettleNo.getText()).setNegativeButton("返回", null).create().show();
	}

	private void loadSettleOrders(String sno) {
		SettleManager.getInstance().loadSettleOrders(sno, new ManagerCallBack2<List<OrderEntity>, Integer>() {
			@Override
			public void onSuccess(List<OrderEntity> returnContent, Integer params) {
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
	 * 
	 * @param isReload
	 */
	private void loadSettles(final boolean isReload) {
		SettleManager.getInstance().loadSettles(currentStatus, mSettleListAdapter.getNext_page(isReload), 10,
				new ManagerCallBack2<List<SettleEntity>, Integer>() {
					@Override
					public void onSuccess(List<SettleEntity> returnContent, Integer params) {
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
	private void paySettle() {
		SettleManager.getInstance().paySettle(String.valueOf(tvSettleNo.getText()), new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				SettleManager.getInstance().getSettle(String.valueOf(tvSettleNo.getText()),
						new ManagerCallBack<SettleEntity>() {
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

	private void receiveSettle() {
		SettleManager.getInstance().receiveSettle(tvSettleNo.getText().toString(), new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				SettleManager.getInstance().getSettle(tvSettleNo.getText().toString(),
						new ManagerCallBack<SettleEntity>() {
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

	private void closeSettle() {
		if (currentSettle == null)
			return;
		if (SettleApplyType.valueOf(currentSettle.getApply_type()).equals(SettleApplyType.HUANTU)) {
			UITools.ToastMsg(getActivity(), "该结算工单不是由您发起，不能完成工单");
			return;
		}
		SettleManager.getInstance().closeSettle(currentSettle.getSerial_no(), new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				SettleManager.getInstance().getSettle(tvSettleNo.getText().toString(),
						new ManagerCallBack<SettleEntity>() {
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
	private void showAutoAuditResult() {

		SettleManager.getInstance().auditAuto(tvSettleNo.getText().toString(),
				new ManagerCallBack<SettleAuditEntity>() {
					@Override
					public void onSuccess(SettleAuditEntity returnContent) {
						super.onSuccess(returnContent);

						// views
						View dialogView = LayoutInflater.from(getActivity())
								.inflate(R.layout.order_settle_audit_auto_result, null);
						((TextView) dialogView.findViewById(R.id.tvSettleNo)).setText(returnContent.getSerial_no());
						((TextView) dialogView.findViewById(R.id.tvSerialAmount))
								.setText("￥" + Tool.fenToYuan(returnContent.getSerial_amount()));
						((TextView) dialogView.findViewById(R.id.tvCashAmount))
								.setText("￥" + Tool.fenToYuan(returnContent.getCash_amount()));
						((TextView) dialogView.findViewById(R.id.tvOnlineAmount))
								.setText("￥" + Tool.fenToYuan(returnContent.getOnline_amount()));
						((TextView) dialogView.findViewById(R.id.tvServiceFee))
								.setText("￥" + Tool.fenToYuan(returnContent.getService_fee()));
						((TextView) dialogView.findViewById(R.id.tvBankAccountName))
								.setText(returnContent.getBank_account_name());
						((TextView) dialogView.findViewById(R.id.tvBankAccount))
								.setText(returnContent.getBank_account());
						((TextView) dialogView.findViewById(R.id.tvReturnAmount))
								.setText("￥" + Tool.fenToYuan(returnContent.getReturn_amount()));
						((TextView) dialogView.findViewById(R.id.tvComment)).setText(returnContent.getComment());

						// create dialog
						Builder builder = MyDialogManager.builder(getActivity());
						builder.setTitle("审计结果");
						builder.setView(dialogView).setNegativeButton("返回", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						builder.create().show();

					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						UITools.ToastMsg(getActivity(), msg);
					}
				});

	}

	/**
	 * 新建工单
	 */
	private void newSettle() {
		if (endDate == null) {
			UITools.ToastMsg(getActivity(), "请选择截止时间");
			return;
		}
		String end_date = Tool.formatDate(endDate.getTimeInMillis());
		String comment = etComments.getText().toString();
		SettleManager.getInstance().newSettle(end_date, comment, new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				MyDialogManager.showNotice(getActivity(), "创建工单成功！");
				etComments.setText("");
				// 重新加载工单列表
				loadSettles(true);
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
			}
		});
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadSettles(refreshListView.isHeaderShown());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// to new settle
		case R.id.btnToNewSettle:
			viewSettleDetail.setVisibility(View.GONE);
			viewSettleNew.setVisibility(View.VISIBLE);
			break;
		// select time
		case R.id.btnEndTime:
			datePickerDialog = DatePickerDialog.newInstance(this, endDate.get(Calendar.YEAR),
					endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), false);
			datePickerDialog.setYearRange(2015, 2020);
			datePickerDialog.show(getFragmentManager(), NEW_SETTLE_ENDDATE);
			break;
		// new settle
		case R.id.btnNewSettle:
			newSettle();
			break;

		case R.id.btnPay:
			paySettle();
			break;
		case R.id.btnReceive:
			receiveSettle();
			break;
		case R.id.btnCloseSettle:
			closeSettle();
			break;
		case R.id.btnAuditAuto:
			showAutoAuditResult();
			break;
		case R.id.btnSettleStatus:
			showSettleStatus();
			break;
		case R.id.btnShowOrders:
			loadSettleOrders(tvSettleNo.getText().toString());
			break;
		case R.id.btnDiscuss:
			openDiscussActivity(tvSettleNo.getText().toString());
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		showSettle(mSettleListAdapter.getList().get(position - 1));
	}

	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
		switch (datePickerDialog.getTag()) {
		case NEW_SETTLE_ENDDATE:
			endDate.set(year, month, day, 0, 0, 0);
			btnEndTime.setText(Tool.formatDate(endDate.getTimeInMillis()));
			break;

		default:
			break;
		}
	}

	private void openDiscussActivity(String settleNo) {
		// 设置用户上线参数
		MCOnlineConfig onlineConfig = new MCOnlineConfig();
		onlineConfig.setChannel(Config.MEIQIA_PLATFORM_INNER_NAME); // 设置渠道
		onlineConfig.setSpecifyGroup("工单异议"); // 设置指定分组

		MCUserConfig mcUserConfig = new MCUserConfig();
		Map<String, String> userInfoExtra = MyApplication.getmContext().getUserInfoExtra();
		userInfoExtra.put("工单号", settleNo);
		userInfoExtra.put("咨询意向", "工单协商");
		mcUserConfig.setUserInfo(getActivity(), MyApplication.getmContext().getUserInfo(), userInfoExtra, null);

		// 启动对话界面
		MCClient.getInstance().startMCConversationActivity(onlineConfig);
	}
}
