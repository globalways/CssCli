package com.globalways.csscli.http.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.Config;
import com.globalways.csscli.http.HttpApi;
import com.globalways.csscli.http.HttpClientDao.ErrorCode;
import com.globalways.csscli.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.csscli.http.HttpCode;
import com.globalways.csscli.http.HttpUtils;
import com.globalways.csscli.tools.MyLog;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

public class ImageUpLoadManager {
	private static final String TAG = ImageUpLoadManager.class.getSimpleName();

	public ImageUpLoadManager() {
	}

	private List<String> qiniuList = new ArrayList<String>();;
	private boolean isGoOn = true;

	/**
	 * 上传图片到七牛服务器
	 * 
	 * @param path
	 *            path为图片在本地的绝对路径
	 * @param callBack
	 */
	public void upLoadImage(final String[] path, final ManagerCallBack<List<String>> callBack) {
		for (int i = 0; i < path.length; i++) {
			MyLog.d(TAG, "path: " + path[i].toString());
			if (!isGoOn) {
				return;
			}
			final int index = i;
			getUploadToken(null, new ManagerCallBack<String>() {
				@Override
				public void onSuccess(String token) {
					MyLog.d(TAG, "index token : " + index + ":" + token);
					UploadManager uploadManager = new UploadManager();
					uploadManager.put(path[index], null, token, new UpCompletionHandler() {
						@Override
						public void complete(String key, ResponseInfo info, JSONObject response) {
							try {
								qiniuList.add(response.getString(Config.QINIU_KEY));
								MyLog.d(TAG, "index qiniuRequest : " + index + " " + qiniuList.toString());
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (qiniuList.size() == path.length) {
								if (callBack != null) {
									callBack.onSuccess(qiniuList);
								}
							}
						}
					}, null);
				}

				@Override
				public void onFailure(int code, String msg) {
					super.onFailure(code, msg);
					isGoOn = true;
					if (callBack != null) {
						callBack.onFailure(code, msg);
					}
				}
			});
		}
	}

	/**
	 * 每张图片都需要到服务器获取一次token
	 * 
	 * @param key
	 *            更新图片需要把原图的key传递过去，新增为null
	 * @param callBack
	 */
	private void getUploadToken(String key, final ManagerCallBack<String> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		sb.append(Config.BUCKET);
		if (null != key) {
			sb.append(":");
			sb.append(key);
		}
		params.put("bucket", sb.toString());
		HttpUtils.getInstance().sendPostRequest(HttpApi.QINIU_GET_UPLOAD_TOKEN, 1, params,
				new HttpClientUtilCallBack<String>() {
					@Override
					public void onSuccess(String url, long flag, String returnContent) {
						super.onSuccess(url, flag, returnContent);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(returnContent);
							int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
							if (code == HttpCode.SUCCESS) {
								if (null != callBack) {
									callBack.onSuccess(jsonObject.getJSONObject(Config.BODY).getString("uptoken"));
								}
							} else {
								if (null != callBack) {
									callBack.onFailure(code,
											jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG));
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

}
