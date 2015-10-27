package com.globalways.cvsb.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author James
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = CrashHandler.class.getSimpleName();

	/** 系统默认的UncaughtException处理类 **/
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandler实例 **/
	private static CrashHandler crashInstance;
	/** 程序的Context对象 **/
	private Context mContext;
	/** 用来存储设备信息和异常信息 **/
	private Map<String, String> infos = new HashMap<String, String>();
	/** 是否保存异常信息 **/
	private boolean IS_SAVE_EXCEPTION_INFO = true;

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (crashInstance == null) {
			crashInstance = new CrashHandler();
		}
		return crashInstance;
	}

	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// 崩溃类异常
			try {
				/*
				 * Intent intent = new Intent(BroadcastReceiverAction.EXIT_APP);
				 * mContext.sendBroadcast(intent);
				 */
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			// 退出程序
			exceptionExit();
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				SharedPreferencesHelper.getInstance(mContext).saveErrorFlag();
				Toast.makeText(mContext, "出现未知错误，程序即将退出！", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		if (IS_SAVE_EXCEPTION_INFO) {
			saveCrashInfo2File(ex);
		}
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.e(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 异常退出程序
	 */
	private void exceptionExit() {
		/*
		 * try { // 正常退出 ExitApplication.getInstance().exit(); } catch
		 * (Exception e) { e.printStackTrace(); } // 强制退出
		 * WelcomeActivity.self.finish();
		 */
		/*
		 * Intent intent = new Intent(BroadcastReceiverAction.EXIT_APP);
		 * mContext.sendBroadcast(intent);
		 */

		// 退出程序
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

	/**
	 * 保存错误日志文件
	 * 
	 * @param ex
	 * @return
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		sb.append("*************************************************************************************************************************\n");
		sb.append("Exception Time = " + DateFormatConfig.SDF_YMDHMS.format(new Date()) + "\n");
		// 保存设备参数信息
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result + "\n");
		Log.e(TAG, sb.toString());
		try {
			String fileName = "error.txt";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory().toString() + FilePath.BASE_PATH
						+ FilePath.EXCEPTION + "/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
					dir.setWritable(true);
				}
				FileOutputStream fos = new FileOutputStream(path + fileName, true);// /true表示可以追加文件内容
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
}