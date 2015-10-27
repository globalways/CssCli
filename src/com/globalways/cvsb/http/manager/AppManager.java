package com.globalways.cvsb.http.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.globalways.cvsb.Config;
import com.globalways.cvsb.http.HttpApi;
import com.globalways.cvsb.http.HttpUtils;
import com.globalways.cvsb.http.HttpClientDao.ErrorCode;
import com.globalways.cvsb.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.cvsb.tools.MyLog;

/**
 * app版本管理等
 * 
 * @author James.Fan
 *
 */
public class AppManager {

	/**
	 * 检查版本更新
	 * 
	 * @param callBack
	 */
	public void toCheckVersion(Context context, final ManagerCallBack<String> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(HttpApi.P_CHANNEL, "gws");
		params.put(HttpApi.P_APP_NAME, "css");
		params.put(HttpApi.P_VERSION, Config.APP_VERSION);
		HttpUtils.getInstance().sendPostRequest(HttpApi.VERSION_CHECK, 1, params, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == 2) {
						if (null != callBack) {
							callBack.onSuccess(jsonObject.getJSONObject(Config.BODY).getString("url"));
						}
					} else {
						if (null != callBack) {
							callBack.onFailure(code, jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, long flag, ErrorCode errorCode) {
				super.onFailure(url, flag, errorCode);
				if (null != callBack) {
					callBack.onFailure(errorCode.code(), errorCode.msg());
				}
			}
		});
	}

	private ManagerCallBack<String> callBack;
	private Task task;

	/**
	 * 下载新版本apk
	 * 
	 * @param localPath
	 * @param url
	 * @param callBack
	 */
	public void downloadApk(String localPath, String url, final ManagerCallBack<String> callBack) {
		this.callBack = callBack;
		if (task != null) {
			task.cancel(true);
		}
		task = new Task();
		task.execute(url, localPath);
	}

	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			task.onProgressUpdate(msg.what);
		}
	};

	private class Task extends AsyncTask<String, Integer, Integer> {

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (callBack != null) {
				callBack.onProgress(values[0]);
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			int result = 1;
			String uri = params[0];
			String localPath = params[1];

			// 确保文件保存的父目录已存在
			File file = new File(localPath);
			// 如果此文件已存在，就先删除，在下载
			if (null != file && file.exists()) {
				file.delete();
			}
			File directory = new File(file.getParent());
			if (!directory.exists()) {
				directory.mkdirs();
				directory.setWritable(true);
			}
			MyLog.d("AppManager", "downloadFile localPath: " + localPath + " , uri: " + uri);
			int progress = 0;
			try {
				// 构造URL
				URL url = new URL(uri);
				// 打开连接
				URLConnection con = url.openConnection();
				// 获得文件的长度
				int contentLength = con.getContentLength();
				System.out.println("长度 :" + contentLength);
				// 输入流
				InputStream is = con.getInputStream();
				// 1K的数据缓冲
				byte[] bs = new byte[1024];
				// 读取到的数据长度
				int len;
				int length = 0;
				// 输出的文件流
				OutputStream os = new FileOutputStream(file);
				// 开始读取
				while ((len = is.read(bs)) != -1) {
					os.write(bs, 0, len);
					length += len;
					int progres = (int) (((float) length / (float) contentLength) * 100.0);
					if (progres != progress) {
						progress = progres;
						// onProgressUpdate(progress);
						Message message = myHandler.obtainMessage(progress);
						message.sendToTarget();
					}
				}
				// 完毕，关闭所有链接
				os.close();
				is.close();
				result = 0;
			} catch (Exception e) {
				e.printStackTrace();
				result = 1;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == 0) {
				if (callBack != null) {
					callBack.onSuccess(null);
				}
			} else {
				if (callBack != null) {
					callBack.onFailure(1, "下载错误");
				}
			}
		}
	}

}
