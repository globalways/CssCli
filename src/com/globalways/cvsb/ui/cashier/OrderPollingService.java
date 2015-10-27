package com.globalways.cvsb.ui.cashier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.cvsb.Config;
import com.globalways.cvsb.entity.OrderEntity;
import com.globalways.cvsb.http.HttpApi;
import com.globalways.cvsb.http.HttpCode;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.OrderManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 订单结果轮询Service
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月14日 下午9:02:26
 */
public class OrderPollingService extends Service {
	
	public static final String ACTION = "com.globalways.csscli.ui.cashier.OrderPollingService";
	private String order_id;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		try {
			this.order_id = intent.getExtras().getString("order_id");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new PollingThread().start();
	}
	int count = 0;
	class PollingThread extends Thread {
		@Override
		public void run() {
			count++;
			if(count % 5 == 0){
//				sendBroadcast();
			}
			Intent i = new Intent(ACTION);
			i.putExtra("status", OrderManager.getInstance().getOrderWithoutAsyc(order_id).getLast_status());
			sendBroadcast(i);
		}
	}
	
	
	
}
