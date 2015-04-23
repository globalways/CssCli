package com.globalways.csscli.http.manager;

/**
 * Manager回调必须实现的接口
 * 
 * @author James
 *
 * @param <E>
 */
public abstract class ManagerCallBack2<E, F> {

	/**
	 * 请求成功
	 * 
	 * @param returnContent
	 *            请求返回结果
	 */
	public void onSuccess(E returnContent, F params) {
	};

	/**
	 * 请求失败
	 * 
	 * @param errorCode
	 *            错误信息
	 * @param msg
	 *            消息
	 */
	public void onFailure(int code, String msg) {
	};

	/**
	 * 进度监听
	 * 
	 * @param progress
	 *            进度条[1-100]
	 */
	public void onProgress(int progress) {
	};
}
