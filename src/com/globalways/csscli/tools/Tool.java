package com.globalways.csscli.tools;

import android.annotation.SuppressLint;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Tool {

	/** 是否已出现异常 **/
	public static boolean IS_ERROR = false;

	/**
	 * 文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		boolean isExist = false;
		if (null != filePath && !"".equals(filePath)) {
			File file = new File(filePath);
			if (null != file && file.exists()) {
				isExist = true;
			}
		}
		return isExist;
	}

	/**
	 * 文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String[] filePath) {
		boolean isExist = true;
		if (null != filePath && !"".equals(filePath) && filePath.length > 0) {
			for (int i = 0; i < filePath.length; i++) {
				File file = new File(filePath[i]);
				if (null == file || !file.exists()) {
					isExist = false;
				}
			}
		} else {
			isExist = false;
		}
		return isExist;
	}

	/**
	 * 向SD卡写入数据
	 * 
	 * @param bytes
	 * @param path
	 * @param isAppend
	 */
	public static void saveBytes2SDcardFile(byte[] bytes, String path, boolean isAppend) {
		try {
			File file = new File(path);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
				dir.setWritable(true);
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(path, isAppend);
			fos.write(bytes);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 产生进货单批次号
	 * @author wyp
	 */
	@SuppressLint("SimpleDateFormat")
	public static String generateBatchId()
	{
		Calendar todayStart = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		int year = todayStart.get(Calendar.YEAR);
		int month = todayStart.get(Calendar.MONTH);
		int day = todayStart.get(Calendar.DAY_OF_MONTH);
		todayStart.set(year,month ,day , 0, 0, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		
		StringBuilder sb = new StringBuilder(new SimpleDateFormat("yyyyMMdd").format(todayStart.getTime()));
		sb.append(now.getTimeInMillis() - todayStart.getTimeInMillis());
		return sb.toString();
	}
}
