package com.globalways.cvsb.tools;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.view.MyDialogManager;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PrinterBase {

	// 打印机配对pin
	private static final String PRINTER_PIN = "0000";
	private Context context;
	private BroadcastReceiver mReceiver;
	public static BluetoothDevice pairedBillPrinter;
	private BluetoothAdapter myBluetoothAdapter;
	private AlertDialog scanDialog;
	/**
	 * 扫描到的printer
	 */
	private BluetoothDevice foundPrinter = null;

	public PrinterBase(Context context) {
		this.context = context;
		this.mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						switch (device.getBluetoothClass().getMajorDeviceClass()) {
						case BluetoothClass.Device.Major.IMAGING:
							// 找到打印机(1664 printer)
							if (device.getBluetoothClass().getDeviceClass() == 1664) {
								scanDialog.dismiss();
								// 判断给定地址下的device是否已经配对
								if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
									// 发起匹配
									BluetoothHelper.createBond(device);
									foundPrinter = device;
								} else {
									pairedBillPrinter = device;
								}
							}
							break;
						default:
							break;
						}

					}
				} else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
					if (foundPrinter != null) {
						BluetoothHelper.setPinAndComfirm(foundPrinter, PRINTER_PIN);
						foundPrinter = null;
					} else {
						// 不知道如何取消配对对话框
						// BluetoothHelper.cancelBondProcess(lastUnpairDevice);
						// BluetoothHelper.cancelPairingUserInput(lastUnpairDevice);
						// BluetoothHelper.setPinAndComfirm(lastUnpairDevice,
						// PRINTER_PIN);
					}
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					if (foundPrinter == null) {
						scanDialog.dismiss();
						UITools.ToastMsg(context, "没有发现票据打印机");
					}
				} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					switch (device.getBondState()) {
					case BluetoothDevice.BOND_BONDING:
						MyLog.d("PrinterBase", "正在配对:" + device.getName());
						break;
					case BluetoothDevice.BOND_BONDED:
						MyLog.d("PrinterBase", "完成配对:" + device.getName());
						break;
					case BluetoothDevice.BOND_NONE:
						MyLog.d("PrinterBase", "取消配对:" + device.getName());
					default:
						break;
					}
				}
			}
		};
		if (pairedBillPrinter == null) {
			pairedBillPrinter = findPairedPrinter(context);
		}
	}

	public BroadcastReceiver getReceiver() {
		return this.mReceiver;
	}

	/**
	 * 配对连接测试
	 * 
	 * @param btDev
	 * @return true 成功连接 false 连接失败
	 */
	public static boolean connect(BluetoothDevice btDev) {
		boolean result = false;
		final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		UUID uuid = SPP_UUID;
		try {
			BluetoothSocket btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
			btSocket.connect();
			btSocket.close();
			result = true;
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查找已经配对的票据打印机
	 * 
	 * @return null 表示没有找到
	 */
	public BluetoothDevice findPairedPrinter(Context context) {
		if ((myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()) == null) {
			UITools.ToastMsg(context, "没有找到蓝牙适配器");
		}
		if (!myBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			context.startActivity(enableBtIntent);
		}
		Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() <= 0)
			return null;
		for (BluetoothDevice device : pairedDevices) {
			if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.IMAGING) {
				return device;
			}
		}
		return null;
	}

	/**
	 * 扫描并配对小票打印机
	 */
	public void scanAndPair() {

		scanDialog = MyDialogManager.buildProgress(context);
		scanDialog.setMessage("扫描中..\n提示:如果系统找到票据打印机，系统将自动配对，只需等待即可");
		scanDialog.setCanceledOnTouchOutside(false);
		scanDialog.show();
		if (myBluetoothAdapter == null) {
			myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		if (!myBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			context.startActivity(enableBtIntent);
		}
		myBluetoothAdapter.startDiscovery();

	}

	public Context getContext() {
		return this.context;
	}

	/**
	 * 打印机状态
	 * 
	 * @author SWPU
	 *
	 */
	public enum PrinterStatus {
		UNKNOWN(-1, "未知状态"), OK(0, "没有故障"), PAPER_ERROR(1, "纸舱盖打开"), NO_PAPER(2, "打印机缺纸"), HOT(4, "打印机打印头过热");
		private int code;
		private String desc;

		private PrinterStatus(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public static PrinterStatus codeOf(int code) {
			switch (code) {
			case 0:
				return PrinterStatus.OK;
			case 1:
				return PAPER_ERROR;
			case 2:
				return PrinterStatus.NO_PAPER;
			case 4:
				return HOT;
			default:
				return UNKNOWN;
			}
		}

		public String getDesc() {
			return desc;
		}

	}

}
