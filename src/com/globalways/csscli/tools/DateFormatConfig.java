package com.globalways.csscli.tools;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 日期显示格式
 * 
 * @author James
 *
 */
public class DateFormatConfig {

	/** yyyy-MM-dd HH:mm:ss **/
	public static SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
	/** yyyy-MM-dd HH:mm **/
	public static SimpleDateFormat SDF_YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
	/** HH:mm **/
	public static SimpleDateFormat SDF_HM = new SimpleDateFormat("HH:mm", Locale.CHINESE);
	/** MM月dd日 **/
	public static SimpleDateFormat SDF_MD = new SimpleDateFormat("MM月dd日", Locale.CHINESE);
	/** dd MMM yyyy,HH:mm:ss **/
	public static SimpleDateFormat SDF_YMDHMS_EN = new SimpleDateFormat("dd MMM yyyy,HH:mm:ss", Locale.ENGLISH);
	/** HH:mm **/
	public static SimpleDateFormat SDF_HM_EN = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
	/** dd MMM (MMMM表示全拼，MMM表示缩写) **/
	public static SimpleDateFormat SDF_MD_EN = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
	/** dd MMM, yyyy **/
	public static SimpleDateFormat SDF_BIRTHDAY_EN = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
	/** yyyy-MM-dd **/
	public static SimpleDateFormat SDF_YMD = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
}
