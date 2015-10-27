package com.globalways.cvsb.ui.settings;

import com.globalways.cvsb.R;
import com.globalways.cvsb.Config;
import com.globalways.cvsb.http.manager.AppManager;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.BaseFragment;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.view.MyDialogManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import im.fir.sdk.FIR;
import im.fir.sdk.callback.VersionCheckCallback;
import im.fir.sdk.version.AppVersion;

public class UpdateFragment extends BaseFragment implements OnClickListener {
	private View fragmentView;
	private Button btnToUpdate;
	private TextView tvVersion;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(fragmentView == null){
			fragmentView = inflater.inflate(R.layout.settings_update_fragment, container, false);
		}
		initView();
		return fragmentView;
	}
	
	private void initView(){
		btnToUpdate = (Button) fragmentView.findViewById(R.id.btnToUpdate);
		btnToUpdate.setOnClickListener(this);
		tvVersion = (TextView) fragmentView.findViewById(R.id.tvVersion);
		tvVersion.setText("当前版本：V"+MyApplication.mPackageInfo.versionName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnToUpdate:
			toCheckVersionFIR(getActivity());
			break;

		default:
			break;
		}
	}
	
	/**
	 * 从平台获取版本信息
	 */
	private void toCheckVersion() {
		new AppManager().toCheckVersion(getActivity(), new ManagerCallBack<String>() {
			@Override
			public void onSuccess(final String url) {
				super.onSuccess(url);
				AlertDialog.Builder builder = MyDialogManager.builder(getActivity());
				builder.setMessage("当前有版本更新！会有新的功能开放哦！");
				builder.setTitle("提示！");
				builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						openUrl(url);
					}
				});
				builder.setNegativeButton("取消",null);
				builder.create().show();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(getActivity(), "当前是最新版本");
			}
		});
	}
	
	/**
	 * 检查FIR版本
	 */
	public void toCheckVersionFIR(final Context context){
		final AlertDialog dialog = MyDialogManager.buildProgress(context);
		dialog.setMessage("正在获取最新版本");
		FIR.checkForUpdateInFIR(Config.FIR_APPID , new VersionCheckCallback() {

			@Override
			public void onError(Exception arg0) {
				UITools.ToastMsg(context, "无法获取最新版本");
			}

			@Override
			public void onFail(String arg0, int arg1) {
				UITools.ToastMsg(context, "无法获取最新版本");
				dialog.dismiss();
			}

			@Override
			public void onFinish() {
				dialog.dismiss();
			}

			@Override
			public void onStart() {
				dialog.show();
			}

			@Override
			public void onSuccess(final AppVersion arg0, boolean arg1) {
				dialog.dismiss();
				if(arg0.getVersionName().equals(MyApplication.mPackageInfo.versionName) && arg0.getVersionCode()==MyApplication.mPackageInfo.versionCode){
					UITools.ToastMsg(context, "当前是最新版本");
					return;
				}else{
					StringBuilder sb = new StringBuilder();
					sb.append("版本:").append(arg0.getVersionName()).append("\n").
					append("版本号:").append(arg0.getVersionCode()).append("\n");
					if(!Tool.isEmpty(arg0.getChangeLog()))
					  sb.append("更新日志:").append(arg0.getChangeLog()).append("\n");
					
					AlertDialog.Builder builder = MyDialogManager.builder(context);
					builder.setMessage(sb);
					builder.setTitle("发现新版本！");
					builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Uri uri = Uri.parse(arg0.getUpdateUrl());  
							Intent it = new Intent(Intent.ACTION_VIEW, uri);  
							context.startActivity(it);
						}
					});
					builder.setNegativeButton("取消",null);
					builder.create().show();
				}
			}
		
		});
	}
	
	private void openUrl(String url){
		
	}
}
