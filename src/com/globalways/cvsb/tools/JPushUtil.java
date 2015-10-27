package com.globalways.cvsb.tools;

import java.util.Set;
import android.annotation.SuppressLint;
import android.os.Handler;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

@SuppressLint("HandlerLeak")
public class JPushUtil {

	private static final String TAG = "JPUSH UTILS";
	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAG = 1002;

	private static JPushUtil jPushUtil;
	
	private JPushUtil() {
		
	}
	
	public static JPushUtil getInstance() {
		if (null == jPushUtil) {
			jPushUtil = new JPushUtil();
		}
		return jPushUtil;
	}
	
	public void setAlias(String alias) {
		// 调用 Handler 来异步设置别名
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
	}
	
	public void setTag(Set<String> tag) {
		// 调用 Handler 来异步设置标签
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAG, tag));
	}

	private final TagAliasCallback mTagAliasCallback = new TagAliasCallback() {
		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				MyLog.d(TAG, logs);
				// 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
				break;
			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				MyLog.d(TAG, logs);
				// 延迟 60 秒来调用 Handler 设置别名
				mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
				mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAG, tags), 1000 * 60);
				break;
			default:
				logs = "Failed with errorCode = " + code;
				MyLog.e(TAG, logs);
			}
		}
	};
	private final Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SET_ALIAS:
				MyLog.d(TAG, "Set alias in handler.");
				// 调用 JPush 接口来设置别名。
				JPushInterface.setAlias(MyApplication.getmContext(), (String) msg.obj, mTagAliasCallback);
				break;
			case MSG_SET_TAG:
				MyLog.d(TAG, "Set tag in handler.");
				// 调用 JPush 接口来设置标签
				JPushInterface.setTags(MyApplication.getmContext(), (Set<String>)msg.obj, mTagAliasCallback);
				break;
			default:
				MyLog.d(TAG, "Unhandled msg - " + msg.what);
			}
		}
	};
}
