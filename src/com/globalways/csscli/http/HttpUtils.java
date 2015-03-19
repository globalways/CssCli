package com.globalways.csscli.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.Header;

import com.globalways.csscli.tools.MyLog;
import com.globalways.csscli.tools.Tool;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Http请求接口实例
 * 
 * @author James
 *
 */
public class HttpUtils implements HttpClientDao {
	static final String TAG = HttpUtils.class.getSimpleName();

	private static HttpUtils mHttpUtils;
	// 超时时间10秒
	private static AsyncHttpClient mHttpUtil = new AsyncHttpClient(10 * 1000);
	/** 最大任务数 **/
	private final int mConfigRequestThreadPoolSize = 5;

	private HttpUtils() {
	}

	/**
	 * 获取HttpUtils实例
	 * 
	 * @return
	 */
	public static HttpUtils getInstance() {
		if (null == mHttpUtils) {
			mHttpUtils = new HttpUtils();
		}
		return mHttpUtils;
	}

	/**
	 * 发送Get请求
	 */
	public void sendGetRequest(final String url, final long flag, Map<String, Object> params,
			final HttpClientUtilCallBack<String> httpClientUtilCallBack) {
		RequestParams rp = new RequestParams();
		if (null != params) {
			for (String key : params.keySet()) {
				rp.put(key, params.get(key));
			}
		}
		//MyLog.d(TAG, "get RequestParams = " + params.toString());
		mHttpUtil.setTimeout(10 * 1000);
		mHttpUtil.get(url, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String response = null;
				try {
					response = new String(arg2, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					if (null != httpClientUtilCallBack) {
						httpClientUtilCallBack.onFailure(url, flag, ErrorCode.RESOLVE_EXCEPTION);
					}
					return;
				}
				MyLog.d(TAG, "GetRequest onSuccess = " + response);
				if (null != httpClientUtilCallBack) {
					httpClientUtilCallBack.onSuccess(url, flag, response);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				MyLog.d(TAG, "GetRequest onFailure = " + arg1 + "|" + arg3.getMessage());
				if (null != httpClientUtilCallBack) {
					httpClientUtilCallBack.onFailure(url, flag, ErrorCode.EXCEPTION);
				}
			}
		});
	}

	/**
	 * 发送post请求
	 */
	@Override
	public void sendPostRequest(final String url, final long flag, Map<String, Object> params,
			final HttpClientUtilCallBack<String> httpClientUtilCallBack) {
		// 组合参数,默认utf-8
		RequestParams rp = new RequestParams();
		if (null != params) {
			for (String key : params.keySet()) {
				rp.put(key, params.get(key));
			}
		}
		MyLog.d(TAG, "post RequestParams = " + params.toString());
		mHttpUtil.setTimeout(10 * 1000);
		// 超时时间3秒
		mHttpUtil.post(url, rp, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String response = null;
				try {
					response = new String(arg2, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					if (null != httpClientUtilCallBack) {
						httpClientUtilCallBack.onFailure(url, flag, ErrorCode.RESOLVE_EXCEPTION);
					}
					return;
				}
				MyLog.d(TAG, "PostRequest onSuccess: " + response);
				if (null != httpClientUtilCallBack) {
					httpClientUtilCallBack.onSuccess(url, flag, response);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				MyLog.d(TAG, "PostRequest onFailure: " + arg1 + "|" + arg3.getMessage());
				if (null != httpClientUtilCallBack) {
					httpClientUtilCallBack.onFailure(url, flag, ErrorCode.EXCEPTION);
				}
			}
		});
	}

	/**
	 * 上传文件和其他数据
	 */
	@Override
	public void uploadFile(String[] fileLocalPath, final String fileServerPath, final long flag,
			Map<String, Object> params, final HttpClientUtilCallBack<String> httpClientUtilCallBack) {
		if (Tool.isFileExist(fileLocalPath)) {
			RequestParams rp = new RequestParams(); // 默认编码UTF-8
			if (null != params) {
				for (String key : params.keySet()) {
					rp.put(key, params.get(key));
				}
			}
			try {
				for (int i = 0; i < fileLocalPath.length; i++) {
					rp.put("filedata", new File(fileLocalPath[i]));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				if (null != httpClientUtilCallBack) {
					httpClientUtilCallBack.onFailure(fileServerPath, flag, ErrorCode.FILE_NOT_EXIST);
				}
				return;
			}
			MyLog.d(TAG, "uploadFile fileLocalPath: " + fileLocalPath.toString() + " , fileServerPath: "
					+ fileServerPath);
			mHttpUtil.setMaxConnections(mConfigRequestThreadPoolSize);
			mHttpUtil.setTimeout(100 * 1000);
			mHttpUtil.post(fileServerPath, rp, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					String response = null;
					try {
						response = new String(arg2, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						if (null != httpClientUtilCallBack) {
							httpClientUtilCallBack.onFailure(fileServerPath, flag, ErrorCode.RESOLVE_EXCEPTION);
						}
						return;
					}
					MyLog.d(TAG, "uploadFile onSuccess :" + response);
					if (null != httpClientUtilCallBack) {
						httpClientUtilCallBack.onSuccess(fileServerPath, flag, response);
					}
				}

				@Override
				public void onProgress(int bytesWritten, int totalSize) {
					super.onProgress(bytesWritten, totalSize);
					if (null != httpClientUtilCallBack) {
						httpClientUtilCallBack.onProgress(fileServerPath, flag,
								(int) (((double) bytesWritten / (double) totalSize) * 100));
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					MyLog.d(TAG, "uploadFile onFailure : " + arg1 + "|" + arg3.getMessage());
					arg3.printStackTrace();
					if (null != httpClientUtilCallBack) {
						httpClientUtilCallBack.onFailure(fileServerPath, flag, ErrorCode.EXCEPTION);
					}
				}
			});
		} else {
			MyLog.d(TAG, "uploadFile onFailure file not exist.");
			if (null != httpClientUtilCallBack) {
				httpClientUtilCallBack.onFailure(fileServerPath, flag, ErrorCode.FILE_NOT_EXIST);
			}
		}
	}

	/**
	 * 下载文件
	 */
	@Override
	public void downloadFile(final String localFilePath, final String fileServerPath, final long flag,
			final HttpClientUtilCallBack<String> httpClientUtilCallBack) {
		if (null != localFilePath && !"".equals(localFilePath) && null != fileServerPath && !"".equals(fileServerPath)) {
			// 确保文件保存的父目录已存在
			File file = new File(localFilePath);
			// 如果此文件已存在，就先删除，在下载
			if (null != file && file.exists()) {
				file.delete();
			}
			File directory = new File(file.getParent());
			if (!directory.exists()) {
				directory.mkdirs();
				directory.setWritable(true);
			}
			MyLog.d(TAG, "downloadFile localFilePath: " + localFilePath + " , fileServerPath: " + fileServerPath);
			mHttpUtil.setTimeout(10 * 1000);
			mHttpUtil.get(fileServerPath, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					MyLog.d(TAG, "downloadFile onSuccess  localFilePath: " + localFilePath + " , fileServerPath: "
							+ fileServerPath);
					Tool.saveBytes2SDcardFile(arg2, localFilePath, false);
					if (null != httpClientUtilCallBack) {
						httpClientUtilCallBack.onSuccess(fileServerPath, flag, null);
					}
				}

				@Override
				public void onProgress(int bytesWritten, int totalSize) {
					super.onProgress(bytesWritten, totalSize);
					if (null != httpClientUtilCallBack) {
						httpClientUtilCallBack.onProgress(fileServerPath, flag,
								(int) (((double) bytesWritten / (double) totalSize) * 100));
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					MyLog.d(TAG, "downloadFile onFailure = " + arg1 + "|" + arg3.getMessage());
					if (null != httpClientUtilCallBack) {
						httpClientUtilCallBack.onFailure(fileServerPath, flag, ErrorCode.EXCEPTION);
					}
				}
			});
		} else {
			if (null != httpClientUtilCallBack) {
				httpClientUtilCallBack.onFailure(fileServerPath, flag, ErrorCode.PATH_EXCEPTION);
			}
		}
	}

	/**
	 * this function make sure HttpUtils is singleton.
	 *
	 * @return
	 */
	private Object readResolve() {
		return mHttpUtils;
	}

}
