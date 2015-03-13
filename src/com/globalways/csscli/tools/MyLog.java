package com.globalways.csscli.tools;

import com.globalways.csscli.Config;


/**
 * 打印日志
 * 
 * @author James
 *
 */
public class MyLog {

	/** 调试信息打印 **/
	private static boolean D_SHOW_LOG = Config.DEBUG;
	/** 错误信息打印 **/
	private static boolean E_SHOW_LOG = Config.DEBUG;
	/** 警告信息打印 **/
	private static boolean W_SHOW_LOG = Config.DEBUG;

	/**
	 * debug logs
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg) {
		if (D_SHOW_LOG) {
			android.util.Log.d(tag, msg);
		}
	}

	/**
	 * error logs
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, String msg) {
		if (E_SHOW_LOG) {
			android.util.Log.e(tag, msg);
		}
	}

	/**
	 * warning logs
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void w(String tag, String msg) {
		if (W_SHOW_LOG) {
			android.util.Log.w(tag, msg);
		}
	}
}
