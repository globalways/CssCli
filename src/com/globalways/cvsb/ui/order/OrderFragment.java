package com.globalways.cvsb.ui.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro.Builder;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.OrderEntity;
import com.globalways.cvsb.entity.OrderProductsEntity;
import com.globalways.cvsb.entity.OrderEntity.PayChannel;
import com.globalways.cvsb.entity.OrderEntity.PayType;
import com.globalways.cvsb.http.manager.ManagerCallBack2;
import com.globalways.cvsb.http.manager.OrderManager;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.BaseFragment;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.view.MyDialogManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

public class OrderFragment extends BaseFragment implements OnClickListener, OnDateSetListener, OnRefreshListener<ListView>, OnItemClickListener {
	private View fragmentView;
	private final static String START_DATE = "start_date";
	private final static String END_DATE = "end_date";
	private List<OrderStatus> selectedStatus = new ArrayList<OrderStatus>();
	private OrderStatus[] allStatusList = OrderStatus.values();
	//order list
	private Button btnOrderStatus, btnTimeStart, btnTimeEnd;
	private PullToRefreshListView refreshListView;
	private ListView mListView;
	private OrderListAdapter mOrderListAdapter;
	private TextView tvOrderNum;
	
	private Calendar start = Tool.dayStart(Calendar.getInstance());
    private Calendar end  = Tool.dayEnd(Calendar.getInstance());
    private DatePickerDialog datePickerDialog;
	
	//detail
	private TextView tvOrderID, tvBuyer, tvCard, tvOrderLastStatus, tvPaymentType, tvPaymentChannel,
	tvOrderAmount, tvDiscountAmount, tvOrderCreateTime, tvProductsCount, tvSettleNo, tvSettleStatus;
	private Button btnShowProducts, btnToPayOrder;
	private View viewOrderDetail;
	private OrderEntity currentEntity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null ==  fragmentView){
			fragmentView = inflater.inflate(R.layout.order_fragment, container, false);
		}
		initView();
		return fragmentView;
	}
	
	private void initView(){
		//order list
		btnOrderStatus = (Button) fragmentView.findViewById(R.id.btnOrderStatus);
		btnOrderStatus.setOnClickListener(this);
		btnTimeStart = (Button) fragmentView.findViewById(R.id.btnTimeStart);
		btnTimeStart.setText(Tool.formatDate(start.getTimeInMillis()));
		btnTimeStart.setOnClickListener(this);
		btnTimeEnd = (Button) fragmentView.findViewById(R.id.btnTimeEnd);
		btnTimeEnd.setText(Tool.formatDate(end.getTimeInMillis()));
		btnTimeEnd.setOnClickListener(this);
		
		refreshListView = (PullToRefreshListView) fragmentView.findViewById(R.id.refreshListView);
		refreshListView.setMode(Mode.BOTH);
		refreshListView.setOnRefreshListener(this);
		mListView = refreshListView.getRefreshableView();
		mOrderListAdapter = new OrderListAdapter(getActivity());
		mListView.setAdapter(mOrderListAdapter);
		mListView.setOnItemClickListener(this);
		tvOrderNum = (TextView) fragmentView.findViewById(R.id.tvOrderNum);
		
		//detail
		tvOrderID = (TextView) fragmentView.findViewById(R.id.tvOrderID);
		tvBuyer = (TextView) fragmentView.findViewById(R.id.tvBuyer);
		tvCard = (TextView) fragmentView.findViewById(R.id.tvCard);
		tvOrderLastStatus = (TextView) fragmentView.findViewById(R.id.tvOrderLastStatus);
		tvPaymentType = (TextView) fragmentView.findViewById(R.id.tvPaymentType);
		tvPaymentChannel = (TextView) fragmentView.findViewById(R.id.tvPaymentChannel);
		tvOrderAmount = (TextView) fragmentView.findViewById(R.id.tvOrderAmount);
		tvDiscountAmount = (TextView) fragmentView.findViewById(R.id.tvDiscountAmount);
		tvOrderCreateTime = (TextView) fragmentView.findViewById(R.id.tvOrderCreateTime);
		tvProductsCount = (TextView) fragmentView.findViewById(R.id.tvProductsCount);
		tvSettleNo = (TextView) fragmentView.findViewById(R.id.tvSettleNo);
		tvSettleStatus =  (TextView) fragmentView.findViewById(R.id.tvSettleStatus);
		btnShowProducts = (Button) fragmentView.findViewById(R.id.btnShowProducts);
		btnShowProducts.setOnClickListener(this);
		btnToPayOrder = (Button) fragmentView.findViewById(R.id.btnToPayOrder);
		btnToPayOrder.setOnClickListener(this);
		
		viewOrderDetail = fragmentView.findViewById(R.id.viewOrderDetail);
		
		refreshListView.setRefreshing();
		loadOrders(Arrays.asList(allStatusList),true);
		datePickerDialog = DatePickerDialog.newInstance(this, start.get(Calendar.YEAR), start.get(Calendar.MONTH), 
				start.get(Calendar.DAY_OF_MONTH),false);
		datePickerDialog.setYearRange(2015, 2020);
		
	}
	
	private void loadOrders(List<OrderStatus> statusArray, final boolean isReload){
		OrderManager.getInstance().loadOrders(mOrderListAdapter.getNext_page(isReload), 10,start.getTimeInMillis()/1000,end.getTimeInMillis()/1000,statusArray, new ManagerCallBack2<List<OrderEntity>, Integer>() {
			@Override
			public void onSuccess(List<OrderEntity> returnContent,
					Integer params) {
				super.onSuccess(returnContent, params);
				mOrderListAdapter.setData(isReload, returnContent);
				tvOrderNum.setText(String.valueOf(params));
				refreshListView.onRefreshComplete();
			}
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
				refreshListView.onRefreshComplete();
			}
		});
	}
	
	@SuppressLint("NewApi")
	private void loadOrderProducts(String oid){
		if(oid == null || oid.isEmpty()){
			UITools.ToastMsg(getActivity(), "获取订单号错误");
			return;
		}
		UITools.ToastMsg(getActivity(), "加载商品列表");
		OrderManager.getInstance().loadOrderProuducts(oid, new ManagerCallBack2<List<OrderProductsEntity>, Integer>() {
			@Override
			public void onSuccess(List<OrderProductsEntity> returnContent,
					Integer params) {
				super.onSuccess(returnContent, params);
				View view = LayoutInflater.from(getActivity()).inflate(R.layout.order_fragment_products, null);
				ListView productsList = (ListView) view.findViewById(R.id.lvOrderProudcuts);
				ProductListAdapter mProductListAdapter = new ProductListAdapter(returnContent);
				productsList.setAdapter(mProductListAdapter);
				Builder builder = MyDialogManager.builder(getActivity());
				builder.setView(view).setTitle(tvOrderID.getText()).
				setPositiveButton("返回", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).create().show();
			}
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), msg);
			}
		});
	}
	
	
	private void showOrderStatus(){
		if(selectedStatus.size() == 0){
			selectedStatus.addAll(Arrays.asList(allStatusList));
		}
		Builder builder = MyDialogManager.builder(getActivity());
		String[] statusName = new String[allStatusList.length];
		boolean[] checked = new boolean[allStatusList.length];
		for(int i=0;i<allStatusList.length;i++	){
			statusName[i] = allStatusList[i].name;
			if(selectedStatus.contains(allStatusList[i])){
				checked[i] = true;
			}else  checked[i] = false;
		}
		builder.setTitle("选择订单状态");
		builder.setMultiChoiceItems(statusName, checked, new DialogInterface.OnMultiChoiceClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				if(isChecked && !selectedStatus.contains(allStatusList[which])){
					selectedStatus.add(allStatusList[which]);
				}else selectedStatus.remove(allStatusList[which]);
			}
			
		}).
		setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadOrders(selectedStatus, true);
			}
		}).setNegativeButton("返回", null).create().show();
	}
	
	private void showOrderDetail(OrderEntity e){
		if(!viewOrderDetail.isShown()){
			viewOrderDetail.setVisibility(View.VISIBLE);
		}
		if(OrderStatus.valueOf(e.getLast_status()).equals(OrderStatus.WATTING_FOR_PAY)){
			btnToPayOrder.setVisibility(View.VISIBLE);
		}else{
			btnToPayOrder.setVisibility(View.INVISIBLE);
		}
		currentEntity = e;
		tvOrderID.setText(e.getOrder_id());
		tvBuyer.setText( e.getBuyer());
		tvCard.setText(e.getCard());
		tvOrderLastStatus.setText(OrderStatus.valueOf(e.getLast_status()).name);
		tvPaymentType.setText(PayType.valueOf(e.getPayment_type()).name);
		tvPaymentChannel.setText(PayChannel.valueOf(e.getOnline_channel(), e.getPayment_type()).channelName);
		tvOrderAmount.setText("￥"+Tool.fenToYuan(e.getOrder_amount()));
		tvDiscountAmount.setText("￥"+Tool.fenToYuan(e.getDiscount_amount()));
		tvOrderCreateTime.setText(Tool.formatDateTime(e.getOrder_time()*1000));
		tvProductsCount.setText(e.getProducts_count());
		tvSettleNo.setText(e.getSettle_serial_no());
		tvSettleStatus.setText(SettleStatus.valueOf(e.getSettle_status()).getDesc());
	}
	
	/**
	 * 支付完成，刷新UI
	 */
	public void paySuccess(){
		currentEntity.setLast_status(OrderStatus.COMPLETE.code);
		mOrderListAdapter.notifyDataSetChanged();
		showOrderDetail(currentEntity);
	}
	private class ProductListAdapter extends BaseAdapter{
		private List<OrderProductsEntity> list;
		
		public ProductListAdapter(List<OrderProductsEntity> list) {
			this.list = list;
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
			if(list != null)
				return list.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.order_fragment_products_list_item, null);
			}
			((TextView)convertView.findViewById(R.id.tvProductName)).setText(list.get(position).getProduct_name());
			((TextView)convertView.findViewById(R.id.tvProductPrice)).setText(Tool.fenToYuan(list.get(position).getProduct_price()));
			((TextView)convertView.findViewById(R.id.tvProductCount)).setText(String.valueOf(list.get(position).getProduct_count()) + " " +list.get(position).getProduct_unit());
			return convertView;
		}
		
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOrderStatus:
			showOrderStatus();
			break;
		case R.id.btnToPayOrder:
			((OrderActivity)getActivity()).showPayDialog(currentEntity);
			break;
		case R.id.btnShowProducts:
			loadOrderProducts(tvOrderID.getText().toString());
			break;
		case R.id.btnTimeStart:
			datePickerDialog.show(this.getFragmentManager(), START_DATE);
			break;
		case R.id.btnTimeEnd:
			datePickerDialog.show(this.getFragmentManager(), END_DATE);
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year,
			int month, int day) {
		switch (datePickerDialog.getTag()) {
		case START_DATE:
			start.set(year, month, day);
			if(start.after(end))
				return;
			btnTimeStart.setText(Tool.formatDate(start.getTimeInMillis()));
			break;
		case END_DATE:
			end.set(year, month, day);
			if(end.before(start))
				return;
			btnTimeEnd.setText(Tool.formatDate(end.getTimeInMillis()));
			break;
		default:
			break;
		}
		loadOrders(selectedStatus, true);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadOrders(selectedStatus, refreshListView.isHeaderShown());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		showOrderDetail(((OrderEntity)mOrderListAdapter.getItem(position-1)));;
	}
}
