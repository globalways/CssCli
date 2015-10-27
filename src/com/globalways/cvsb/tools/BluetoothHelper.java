package com.globalways.cvsb.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class BluetoothHelper {
	
		// 自动配对设置Pin值
		public static boolean setPin(BluetoothDevice device, String pin){
			Method autoBondMethod = null;
			Boolean result =  false;
			Log.i("yangping.wang", "try set pin");
				try {
					autoBondMethod = device.getClass().getMethod("setPin", byte[].class);
					result = (Boolean) autoBondMethod.invoke(device,  pin.getBytes());
				} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
					Log.i("yangping.wang", "set pin failed");
				}
			Log.i("yangping.wang", "set pin success");
			return result;
		}
		
		// 开始配对
		public static boolean createBond(BluetoothDevice device){
			Boolean returnValue = false;
			try {
				Method createBondMethod = device.getClass().getMethod("createBond");
				returnValue = (Boolean) createBondMethod.invoke(device);
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
			return returnValue.booleanValue();
		}

		public static boolean setPairingConfirmation(BluetoothDevice device){
			
			Boolean returnValue = null;
			Log.i("yangping.wang", "try setPairingConfirmation");
			try {
				Method createBondMethod = device.getClass().getMethod("setPairingConfirmation",boolean.class);
				returnValue = (Boolean) createBondMethod.invoke(device, true);
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				Log.i("yangping.wang", "try setPairingConfirmation failed");
			}
			Log.i("yangping.wang", "try setPairingConfirmation success");
			return returnValue.booleanValue();
		}
		
		public static boolean cancelPairingUserInput(BluetoothDevice device){
			
			Boolean returnValue = null;
			Log.i("yangping.wang", "try cancelPairingUserInput");
			try {
				Method createBondMethod = device.getClass().getDeclaredMethod("cancelPairingUserInput");
				returnValue = (Boolean) createBondMethod.invoke(device);
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				Log.i("yangping.wang", "try cancelPairingUserInput failed");
			}
			Log.i("yangping.wang", "try cancelPairingUserInput success");
			return returnValue.booleanValue();
		}
		
		/**
		 * 设置pin码，并且自动确认
		 * @param device
		 * @param pin
		 * @return
		 */
		public static boolean setPinAndComfirm(BluetoothDevice device,String pin){
			return setPin(device, pin) && setPairingConfirmation(device);
		}
		
		public static boolean removeBond(BluetoothDevice device){
			Boolean returnValue = null;
			Log.i("yangping.wang", "try removeBond");
			try {
				Method createBondMethod = device.getClass().getMethod("removeBond");
				returnValue = (Boolean) createBondMethod.invoke(device);
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				Log.i("yangping.wang", "try removeBond failed");
			}
			Log.i("yangping.wang", "try removeBond success");
			return returnValue.booleanValue();
		}
	 
	    // 取消配对
		public static boolean cancelBondProcess(BluetoothDevice device)
	    {
	        Boolean returnValue = null;
			try {
				Method createBondMethod = device.getClass().getMethod("cancelBondProcess");
				returnValue = (Boolean) createBondMethod.invoke(device);
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
	        return returnValue.booleanValue();
	    }
}
