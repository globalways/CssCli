package com.globalways.cvsb.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * 
 * 延迟判断textchange事件
 * @author wyp
 */
public class TextWatcherDelay implements TextWatcher {

	public static final String DATA = "data";
	public static final int MSG_WHAT = 4313;
	private Handler handler;
	private long dalay = 600;
	private long after;
	private Thread t;
	private String input;
	private Runnable runnable  = new Runnable() {
		public void run() {
			Looper.prepare();
			while(true){
				if((System.currentTimeMillis() - after)>dalay){
					Message ms = new Message();
					ms.what = MSG_WHAT;
					Bundle b  = new Bundle();
					b.putCharSequence(DATA, input);
					ms.setData(b);
					handler.sendMessage(ms);
					t = null;
					break;
				}
			}
			Looper.loop();
		}
	};
	
	/**
	 * 
	 * @param handler 事件触发处理，默认延迟600毫秒
	 */
	public TextWatcherDelay(Handler handler) {
		this.handler = handler;
	}
	/**
	 * 延迟判断textchange事件
	 * @param handler 事件触发处理
	 * @param delay 延迟时间，单位毫秒
	 */
	public TextWatcherDelay(Handler handler,long delay) {
		this.handler = handler;
		this.dalay = delay;
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		input = s.toString();
	}

	@Override
	public void afterTextChanged(Editable s) {
		after = System.currentTimeMillis();
		if(t == null)
		{
			t = new Thread(runnable);
			t.start();
		}

	}

}
