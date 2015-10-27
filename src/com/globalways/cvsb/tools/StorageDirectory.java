package com.globalways.cvsb.tools;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.os.Environment;

/**
 * 获取路径工具类
 * 
 * @author James.Fan
 *
 */
public class StorageDirectory {

	public static String getImageDirectory() {
		// 设置照片的保存路径
		String strFilePath = null;
		try {
			// 创建一个位于SD卡上的文件
			StringBuilder sb = new StringBuilder();
			sb.append(externalMemoryAvailable() ? Environment.getExternalStorageDirectory().getPath() : Environment
					.getDataDirectory().getPath());
			sb.append("/GlobalWays/css/image/");
			File dir = new File(sb.toString());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File picFile = File.createTempFile(getDate(), ".jpg", dir);
			strFilePath = picFile.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strFilePath;
	}

	public static String getLogDirectory() {
		String strFilePath = null;
		StringBuilder sb = new StringBuilder();
		sb.append(externalMemoryAvailable() ? Environment.getExternalStorageDirectory().getPath() : Environment
				.getDataDirectory().getPath());
		sb.append("/GlobalWays/css/crash/");
		File dir = new File(sb.toString());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		strFilePath = dir.getPath();
		return strFilePath;
	}

	public static String getUpdateDirectory() {
		String strFilePath = null;
		StringBuilder sb = new StringBuilder();
		sb.append(externalMemoryAvailable() ? Environment.getExternalStorageDirectory().getPath() : Environment
				.getDataDirectory().getPath());
		sb.append("/GlobalWays/css/update/");
		File dir = new File(sb.toString());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		strFilePath = dir.getPath();
		return strFilePath;
	}

	private static boolean externalMemoryAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取系统时间
	 *
	 * @return
	 */
	private static String getDate() {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR); // 获取年份
		int month = ca.get(Calendar.MONTH); // 获取月份
		int day = ca.get(Calendar.DATE); // 获取日
		int hour = ca.get(Calendar.HOUR); // 获取小时
		int minute = ca.get(Calendar.MINUTE); // 获取分
		int second = ca.get(Calendar.SECOND); // 获取秒
		int millisecond = ca.get(Calendar.MILLISECOND); // 获取毫秒
		String date = "" + year + (month + 1) + day + hour + minute + second + millisecond;
		return date;
	}

}
