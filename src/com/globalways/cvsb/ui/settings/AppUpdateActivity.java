package com.globalways.cvsb.ui.settings;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.cvsb.R;
import com.globalways.cvsb.http.manager.AppManager;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.tools.MyLog;
import com.globalways.cvsb.tools.StorageDirectory;
import com.globalways.cvsb.ui.BaseActivity;
import com.globalways.cvsb.ui.UITools;

/**
 * 
 * app新版本下载界面
 * 
 * @author James.Fan
 *
 */
public class AppUpdateActivity extends BaseActivity implements OnClickListener {

	public static String KEY_DOWNLOAD_URL = "url";
	private String url;
	private String localPath;

	private TextView textCenter, textDownLoadProcess;
	private Button btnRetry, btnOpen;
	private ProgressBar progressBarDownload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_app_update_activity);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
		initData();
	}

	private void initData() {
		url = getIntent().getStringExtra(KEY_DOWNLOAD_URL);
		if (url == null || url.isEmpty()) {
			UITools.jumpMainActivity(this);
			AppUpdateActivity.this.finish();
		}
		toDownLoad();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRetry:
			btnRetry.setEnabled(false);
			btnOpen.setEnabled(false);
			toDownLoad();
			break;
		case R.id.btnOpen:
			if (localPath != null) {
				installApk(localPath);
			}
			break;
		}
	}

	private void toDownLoad() {
		textDownLoadProcess.setText("已下载：" + 0 + "%");
		localPath = StorageDirectory.getUpdateDirectory() + "/CssCli.apk";
		new AppManager().downloadApk(localPath, url, new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				Toast.makeText(AppUpdateActivity.this, "已下载成功，点击打开来开始安装！", Toast.LENGTH_LONG).show();
				textDownLoadProcess.setText("已完成");
				btnOpen.setEnabled(true);
			}

			@Override
			public void onProgress(int progress) {
				super.onProgress(progress);
				MyLog.d("progress", "progress: " + progress);
				textDownLoadProcess.setText("已下载：" + progress + "%");
				progressBarDownload.setProgress(progress);
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				Toast.makeText(AppUpdateActivity.this, "下载失败，请重试！", Toast.LENGTH_LONG).show();
				btnRetry.setEnabled(true);
			}
		});
	}

	/**
	 * 安装APK
	 */
	private void installApk(String localPath) {
		File file = new File(localPath);
		if (file.exists()) {
			Uri uri = Uri.fromFile(file); // 获取文件的Uri
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			installIntent.setDataAndType(uri, "application/vnd.android.package-archive");// 设置intent的数据类型
			startActivity(installIntent);
		} else {
			Toast.makeText(AppUpdateActivity.this, "没找到下载文件！", Toast.LENGTH_LONG).show();
		}
	}

	private void initView() {
		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("新版本更新");

		textDownLoadProcess = (TextView) findViewById(R.id.textDownLoadProcess);
		btnRetry = (Button) findViewById(R.id.btnRetry);
		btnRetry.setOnClickListener(this);
		btnRetry.setEnabled(false);
		btnOpen = (Button) findViewById(R.id.btnOpen);
		btnOpen.setOnClickListener(this);
		btnOpen.setEnabled(false);
		progressBarDownload = (ProgressBar) findViewById(R.id.progressBarDownload);
	}

}
