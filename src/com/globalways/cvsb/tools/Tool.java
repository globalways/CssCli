package com.globalways.cvsb.tools;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Tool {

	/**
	 * 人民币元分进制
	 */
	public final static int FENYUAN = 100;
	/**
	 * 零售折扣小数点位数
	 */
	public final static int RETAIL_APR_SCALE = 2;
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
	
	////////////////////////////////  date time ///////////////////////////////////////////////////////
	
	/**
	 * 获取指定日期（天）的开始时间
	 * @param day
	 * @return
	 */
	public static Calendar dayStart(Calendar day){
		day.set(Calendar.HOUR_OF_DAY, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		return day;
	}
	/**
	 * 获取指定日期（天）的结束时间
	 * @param day
	 * @return
	 */
	public static Calendar dayEnd(Calendar day){
		day.set(Calendar.HOUR_OF_DAY, 23);
		day.set(Calendar.MINUTE, 59);
		day.set(Calendar.SECOND, 59);
		return day;
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
	
	/**
	 * 格式化输出日期
	 * @param datetime long型时间,单位毫秒
	 * @return
	 *      格式化后的字符串
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatDate(long datetime)
	{
		if(datetime < 1){
			return "0000-00-00";
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(datetime);
	}
	
	/**
	 * 格式化输出日期
	 * @param datetime long,毫秒
	 * @param format 时间格式
	 * @return
	 * 		格式化后的字符串
	 */
	public static String formateDateTimeByFormat(long datetime, String format) {
		if(datetime < 1){
			return "0000-00-00";
		}
		return new SimpleDateFormat(format).format(datetime);
	}
	
	/**
	 * 格式化输出日期时间
	 * @param datetime long型时间,单位毫秒
	 * @return
	 *      格式化后的字符串24小时制，例如 2015-08-20 22:32:35
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatDateTime(long datetime){
		if(datetime < 1){
			return "0000-00-00";
		}
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(datetime);
	}
	
	/**
	 * 格式化 人民币元
	 * @param 
	 * 		yuan 人民币元
	 * @return
	 * 		返回两位小数的{@link String}<br />example: formatYuan("124.315") == "124.32"
	 */
	public static String formatYuan(String yuan)
	{
		return new BigDecimal(yuan).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	/**
	 * 分转元,保留小数点后两位,四舍五入
	 * @author wyp
	 */
	public static String fenToYuan(long fen)
	{
		return Tool.div(String.valueOf(fen), String.valueOf(FENYUAN), 2);
	}
	/**
	 * 分转元
	 * @author wyp
	 */
	public static float fenToYuanFloat(long fen)
	{
		return Float.parseFloat(fenToYuan(fen));
	}
	/**
	 * 元转分
	 * @param yuan
	 * @return long型的分
	 */
	public static long yuanToFen(String yuan)
	{
		return Tool.mulAsLong(yuan, String.valueOf(100));
	}
	
	/**
	 * 精确浮点乘法计算
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return String类型的结果
	 */
	public static String mul(String v1, String v2)
	{ 
		BigDecimal b1 = new BigDecimal(v1); 
		BigDecimal b2 = new BigDecimal(v2); 
		return String.valueOf(b1.multiply(b2)); 
	}
	/**
	 * 精确浮点乘法计算
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return long类型的结果
	 */
	public static long mulAsLong(String v1, String v2)
	{ 
		BigDecimal b1 = new BigDecimal(v1); 
		BigDecimal b2 = new BigDecimal(v2); 
		return b1.multiply(b2).longValue(); 
	}
	
	/** 
	* 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。 
	* 
	* @param v1 
	*            被除数 
	* @param v2 
	*            除数 
	* @param scale 
	*            表示表示需要精确到小数点以后几位。 
	* @return 两个参数的商 String 
	*/ 
	public static String div(String v1, String v2, int scale) { 
		if (scale < 0) { 
		throw new IllegalArgumentException( 
		"The scale must be a positive integer or zero"); 
		} 
		BigDecimal divider = new BigDecimal(v1); 
		BigDecimal divided = new BigDecimal(v2); 
		return String.valueOf(divider.divide(divided, scale, BigDecimal.ROUND_HALF_UP)); 
	}
	
	/** 
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。 
	 * 
	 * @param v1 
	 *            被除数 
	 * @param v2 
	 *            除数 
	 * @param scale 
	 *            表示表示需要精确到小数点以后几位。 
	 * @return 两个参数的商 double 
	 */ 
	public static double divAsDouble(String v1, String v2, int scale) { 
		if (scale < 0) { 
			throw new IllegalArgumentException( 
					"The scale must be a positive integer or zero"); 
		} 
		BigDecimal divider = new BigDecimal(v1); 
		BigDecimal divided = new BigDecimal(v2); 
		return divider.divide(divided, scale, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	}
	
	/** 
	   * 提供精确的减法运算 
	   * @param v1 
	   * @param v2 
	   * @return 两个参数数学差，以字符串格式返回 
	   */  
	  public static String subtract(String v1, String v2)  
	  {  
	      BigDecimal b1 = new BigDecimal(v1);  
	      BigDecimal b2 = new BigDecimal(v2);  
	      return b1.subtract(b2).toString();  
	  }  
	  
	  /** 
	   * 提供精确的加法运算 
	   * @param v1   
	   * @param v2 
	   * @return 两个参数数学加和，以字符串格式返回 
	   */  
	  public static String add(String v1, String v2)  
	  {  
	      BigDecimal b1 = new BigDecimal(v1);  
	      BigDecimal b2 = new BigDecimal(v2);  
	      return b1.add(b2).toString();  
	  }  
	
	/**
	 * 比较两个数大小
	 * @param first 第一个参数
	 * @param second 第二个参数
	 * @return 1 if first > second, -1 if first < second, 0 if first == second.

	 */
	public static int compare(String first, String second)
	{
		return new BigDecimal(first).compareTo(new BigDecimal(second));
	}
	
	/**
	 * 简化字符串，以防止过长影响美观<br>
	 * before: xxxxxxxxxx
	 * after: xxxx...
	 * @param toHide 要缩短的字符串
	 * @param could 控件能够正常显示的字符个数
	 * @return 替换后的字符串
	 */
	public static String simpifyStr(String toHide, int could){
		if(toHide.length() > could){
			return toHide.substring(0, could-3)+"...";
		}
		return toHide;
	}
	/**
	 * 检查字符串是否为空
	 * @param str
	 * @return true 为空 ; false 不为空
	 */
	public static boolean isEmpty(String str){
		return str == null || str.isEmpty();
	}
	
}
