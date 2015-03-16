package com.globalways.csscli.http;

import java.util.Map;

/**
 * Http请求接口定义
 * 
 * @author James
 *
 */
public interface HttpClientDao {
	/**
	 * 发送Get请求
	 * 
	 * @param url
	 *            请求url
	 * @param flag
	 * @param httpClientUtilCallBack
	 *            <String> 请求结果回调
	 */
	public void sendGetRequest(String url, long flag, Map<String, Object> params,
			HttpClientUtilCallBack<String> httpClientUtilCallBack);

	/**
	 * 发送Post请求
	 * 
	 * @param url
	 *            请求url
	 * @param flag
	 * @param params
	 *            请求参数，如果没有，设为null
	 * @param httpClientUtilCallBack
	 *            <String> 请求结果回调
	 */
	public void sendPostRequest(String url, long flag, Map<String, Object> params,
			HttpClientUtilCallBack<String> httpClientUtilCallBack);

	/**
	 * 上传文件
	 * 
	 * @param fileLocalPath
	 *            需要上传的文件路径
	 * @param fileServerPath
	 *            文件保存的服务器端路径
	 * @param flag
	 * @param httpClientUtilCallBack
	 *            <String> 请求结果回调
	 */
	public void uploadFile(String[] fileLocalPath, String fileServerPath, long flag, Map<String, Object> params,
			HttpClientUtilCallBack<String> httpClientUtilCallBack);

	/**
	 * 下载文件
	 * 
	 * @param fileLocalPath
	 *            文件保存路径
	 * @param fileServerPath
	 *            文件的在服务器上的http路径
	 * @param flag
	 * @param httpClientUtilCallBack
	 *            <File> 请求结果回调
	 */
	public void downloadFile(String fileLocalPath, String fileServerPath, long flag,
			HttpClientUtilCallBack<String> httpClientUtilCallBack);

	/** 部分错误码 **/
	public enum ErrorCode {
		EXCEPTION(-10000, "访问服务器错误"), FILE_NOT_EXIST(-10001, "文件不存在"), PATH_EXCEPTION(-10002, "文件路径错误"), RESOLVE_EXCEPTION(
				-10003, "解析数据异常");

		private ErrorCode(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public int code() {
			return code;
		}

		public String msg() {
			return msg;
		}

		private int code;
		private String msg;
	}

	/**
	 * Http请求回调
	 */
	public abstract class HttpClientUtilCallBack<T> {
		/**
		 * 请求成功
		 * 
		 * @param url
		 *            请求url
		 * @param flag
		 * @param returnContent
		 *            请求返回结果
		 */
		public void onSuccess(String url, long flag, T returnContent) {
		};

		/**
		 * 请求失败
		 * 
		 * @param url
		 *            请求url
		 * @param flag
		 * @param errorCode
		 *            错误码(com.blueocean.youpea.http.dao.HttpClientDao.ErrorCode)
		 */
		public void onFailure(String url, long flag, ErrorCode errorCode) {
		};

		/**
		 * 进度条更新
		 * 
		 * @param url
		 *            请求url
		 * @param flag
		 * @param progress
		 *            进度条[1-100]
		 */
		public void onProgress(String url, long flag, int progress) {
		};
	}
}
