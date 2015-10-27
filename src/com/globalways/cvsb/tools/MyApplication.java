package com.globalways.cvsb.tools;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import cn.jpush.android.api.JPushInterface;
import im.fir.sdk.FIR;

import java.util.HashMap;
import java.util.Map;

import com.globalways.cvsb.Config;
import com.globalways.cvsb.entity.StoreEntity;
import com.globalways.cvsb.entity.UserEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.StoreManager;
import com.globalways.cvsb.ui.AppManager;
import com.globalways.cvsb.ui.UITools;
import com.mechat.mechatlibrary.MCClient;
import com.mechat.mechatlibrary.MCUserConfig;
import com.mechat.mechatlibrary.callback.OnInitCallback;

/**
 * @author James
 *
 */
public class MyApplication extends Application {
	public static MyApplication mContext;
	private static long storeid = 46758;
	private static StoreEntity currentEntity = new StoreEntity();
	private static UserEntity userEntity;
	public static PackageInfo mPackageInfo;
	
	// 美恰相关
	private static Map<String, String> userInfo;
	private static Map<String, String> userInfoExtra;

	public Map<String, String> getUserInfo() {
		if (userInfo==null) {
			userInfo = new HashMap<String, String>();
		}
		return userInfo;
	}

	public void setUserInfo(Map<String, String> userInfo) {
		MyApplication.userInfo = userInfo;
	}

	public Map<String, String> getUserInfoExtra() {
		if (userInfoExtra==null) {
			userInfoExtra = new HashMap<String, String>();
		}
		return userInfoExtra;
	}

	public void setUserInfoExtra(Map<String, String> userInfoExtra) {
		MyApplication.userInfoExtra = userInfoExtra;
	}

	public static MyApplication getmContext() {
		return mContext;
	}

	public static void setmContext(MyApplication mContext) {
		MyApplication.mContext = mContext;
	}

	@Override
	public void onCreate() {
		//bughd
		FIR.init(this);
		super.onCreate();
		mContext = this;
		// 保存漰溃日志信息
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());

		// 初始化美洽SDK
		MCClient.init(this, Config.MEIQIA_KEY, new OnInitCallback() {
			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				// Success
//				Toast.makeText(getApplicationContext(), "init MCSDK success", Toast.LENGTH_SHORT).show();
				MyLog.d("美恰", "初始化美恰SDK成功:"+ response);
			}

			@Override
			public void onFailed(String response) {
				// TODO Auto-generated method stub
				// Failed
//				Toast.makeText(getApplicationContext(), "init MCSDK failed " + response, Toast.LENGTH_SHORT).show();
				MyLog.d("美恰", "初始化美恰SDK失败:" + response);
			}
		});
		
		// 初始化jpush
		JPushInterface.setDebugMode(false);
	    JPushInterface.init(this);
	    
	    try {
			mPackageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			UITools.ToastMsg(this, "无法获取当前版本信息");
			e.printStackTrace();
		}
	    
	}

	public static long getStoreid() {
		return storeid;
	}

	public static void setStoreid(final long storeid) {
		MyApplication.storeid = storeid;
		StoreManager.getInstance().getStoreInfo(storeid, new ManagerCallBack<StoreEntity>() {
			@Override
			public void onSuccess(final StoreEntity entity) {
				super.onSuccess(entity);
				setCurrentEntity(entity);
				JPushUtil.getInstance().setAlias(String.valueOf(storeid));
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				setCurrentEntity(null);
			}
		});
	}

	public static UserEntity getUserEntity() {
		if (userEntity == null) {
			AppManager.getAppManager().finishAllActivity();
			return null;
		}
		return userEntity;
	}

	public static void setUserEntity(UserEntity userEntity) {
		MyApplication.userEntity = userEntity;
	}

	public static StoreEntity getCurrentEntity() {
		if (currentEntity == null) {
			AppManager.getAppManager().finishAllActivity();
			return null;
		}
		return currentEntity;
	}
	
	public static void setCurrentEntity(StoreEntity storeEntity) {
		MyApplication.currentEntity = storeEntity;
		if (storeEntity!= null) {
			initMeiqia();	
		}
	}
	

	private static void initMeiqia() {
		String storeName = MyApplication.getCurrentEntity().getStore_name();
		String storeId = String.valueOf(MyApplication.getCurrentEntity().getStore_id());
		String storePhone = MyApplication.getCurrentEntity().getStore_phone();
		String storeEmail = MyApplication.getCurrentEntity().getStore_email();
		String storeAddr = MyApplication.getCurrentEntity().getStore_address();
		String htStoreId = MyApplication.getCurrentEntity().getHt_store_id();
		String bankAccountName = MyApplication.getCurrentEntity().getBank_account_name();
		String bankAccount = MyApplication.getCurrentEntity().getBank_account();

		// 更新用户信息，可选.
		if (userInfo == null) {
			userInfo = new HashMap<String, String>();
		}
		userInfo.put(MCUserConfig.PersonalInfo.REAL_NAME, storeName == null ? "" : storeName);
		userInfo.put(MCUserConfig.PersonalInfo.APP_USER_ID, storeId == null ? "" : storeId);
		userInfo.put(MCUserConfig.Contact.TEL, storePhone == null ? "" : storePhone);
		userInfo.put(MCUserConfig.Contact.EMAIL, storeEmail == null ? "" : storeEmail);
		userInfo.put(MCUserConfig.Contact.ADDRESS, storeAddr == null ? "" : storeAddr);

		if (userInfoExtra == null) {
			userInfoExtra = new HashMap<String, String>();
		}
		userInfoExtra.put("环途连锁编号", htStoreId == null ? "" : htStoreId);
		userInfoExtra.put("银行账户名", bankAccountName == null ? "" : bankAccountName);
		userInfoExtra.put("银行账户", bankAccount == null ? "" : bankAccount);

		MyLog.d("userinfo", userInfo.toString());
		MyLog.d("userInfoExtra", userInfoExtra.toString());
	}
}