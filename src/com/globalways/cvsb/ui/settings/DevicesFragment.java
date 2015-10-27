package com.globalways.cvsb.ui.settings;

import java.io.IOException;
import java.util.UUID;

import com.globalways.cvsb.R;
import com.globalways.cvsb.tools.BillPrinter;
import com.globalways.cvsb.tools.PrinterBase;
import com.globalways.cvsb.ui.BaseFragment;
import com.globalways.cvsb.ui.UITools;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DevicesFragment extends BaseFragment implements OnClickListener {
	private View fragmentView;
	
	private PrinterBase mPrinterBase;
	//bill printer
	private BillPrinter mBillPrinter;
	private View viewBillPrinterDetail, viewBillPrinter;
	private TextView tvBillPrinterStatus;
	private Button btnConnectPrinter,btnPrintTest,btnConnectPrinterManual;
	private Handler printerCheckHandler;
	private Runnable checkThread;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragmentView == null) {
			fragmentView = inflater.inflate(R.layout.settings_devices_fragment, container, false);
		}
		initView();
		initData();
		return fragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		tvBillPrinterStatus.setText("查询..");
		if(printerCheckHandler == null)
			printerCheckHandler = new Handler();
		if(checkThread == null)
			checkThread = new Runnable() {
				
				@Override
				public void run() {
					PrinterBase.pairedBillPrinter = mPrinterBase.findPairedPrinter(getActivity());
					if(PrinterBase.pairedBillPrinter== null){
						tvBillPrinterStatus.setText("未连接");
					}else{
						tvBillPrinterStatus.setText("已连接");
					}
					printerCheckHandler.postDelayed(this, 2000);
				}
			};
		printerCheckHandler.postDelayed(checkThread, 1000);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(printerCheckHandler != null && checkThread != null){
			printerCheckHandler.removeCallbacks(checkThread);
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mPrinterBase.getReceiver());
	}
	
	private void initView() {
		//bill printer 票据打印机
		btnConnectPrinter = (Button) fragmentView.findViewById(R.id.btnConnectPrinterAuto);
		btnConnectPrinter.setOnClickListener(this);
		tvBillPrinterStatus = (TextView) fragmentView.findViewById(R.id.tvBillPrinterStatus);
		viewBillPrinterDetail = fragmentView.findViewById(R.id.viewBillPrinterDetail);
		viewBillPrinter = fragmentView.findViewById(R.id.viewBillPrinter);
		viewBillPrinter.setOnClickListener(this);
		btnConnectPrinterManual = (Button) fragmentView.findViewById(R.id.btnConnectPrinterManual);
		btnConnectPrinterManual.setOnClickListener(this);
		btnPrintTest = (Button) fragmentView.findViewById(R.id.btnPrintTest);
		btnPrintTest.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	private void initData() {
		
		mPrinterBase = new PrinterBase(getActivity());
		mBillPrinter = new BillPrinter(getActivity());
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		getActivity().registerReceiver(mPrinterBase.getReceiver(), filter);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.viewBillPrinter:
			viewBillPrinterDetail.setVisibility(View.VISIBLE);
			break;
		case R.id.btnConnectPrinterAuto:
			if (PrinterBase.pairedBillPrinter == null) {
				mPrinterBase.scanAndPair();
			}else{
				UITools.ToastMsg(getActivity(), "设备已连接，无需再次连接");
			}
			break;
		case R.id.btnConnectPrinterManual:
			getActivity().startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
			break;
		case R.id.btnPrintTest:
			if (PrinterBase.pairedBillPrinter == null) {
				UITools.ToastMsg(getActivity(), "请先连接打印机");
			}else{
				mBillPrinter.testPrint();
			}
			break;
		default:
			break;
		}

	}






}
