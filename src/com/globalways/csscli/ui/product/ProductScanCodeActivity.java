package com.globalways.csscli.ui.product;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

import com.globalways.csscli.R;
import com.globalways.csscli.android.AmbientLightManager;
import com.globalways.csscli.android.BeepManager;
import com.globalways.csscli.android.CaptureActivityHandler;
import com.globalways.csscli.android.FinishListener;
import com.globalways.csscli.android.InactivityTimer;
import com.globalways.csscli.android.IntentSource;
import com.globalways.csscli.android.PreferencesActivity;
import com.globalways.csscli.android.ScanCodeInterface;
import com.globalways.csscli.android.ViewfinderView;
import com.globalways.csscli.android.camera.CameraManager;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.ProductManager;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.tools.MyLog;
import com.globalways.csscli.ui.BaseNoTitleActivity;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.view.SimpleProgressDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

public class ProductScanCodeActivity extends BaseNoTitleActivity implements ScanCodeInterface, SurfaceHolder.Callback {
	private static final String TAG = ProductScanCodeActivity.class.getSimpleName();

	// scanner variable
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
	private AmbientLightManager ambientLightManager;
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	private boolean hasSurface;
	private IntentSource source;
	private Result savedResultToShow;

	private SimpleProgressDialog mSimpleProgressDialog;

	public static final String KEY_SCAN_RESULT = "scanResult";

	public static final String KEY_OPERATION_TYPE = "operationType";
	private int operationType = OperationType.GET_CODE;

	public class OperationType {
		/** 扫码商品二维码或条形，扫码完成后查询该商品是否已经入库，已入库就进入修改页面；未入库提示添加 */
		public static final int SCAN_PRODUCT = 0;
		/** 扫码获取商品的条形码，获取成功后，直接intent返回数据 */
		public static final int GET_CODE = 1;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_scan_code_activity);
		// 布局内容会从view以下开始
		findViewById(R.id.camera_view).setFitsSystemWindows(true);
		mTintManager.setStatusBarDarkMode(false, this);

		operationType = getIntent().getIntExtra(KEY_OPERATION_TYPE, OperationType.GET_CODE);

		findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				ProductScanCodeActivity.this.finish();
			}
		});

		// scanner
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);
		hasSurface = false;
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	/**
	 * 处理扫描获取到的数据
	 * 
	 * @param result
	 */
	public void handleResult(Result result) {
		MyLog.d(TAG, result.getText());
		switch (operationType) {
		case OperationType.GET_CODE:
			setResult(RESULT_OK, new Intent().putExtra(KEY_SCAN_RESULT, result.getText()));
			ProductScanCodeActivity.this.finish();
			break;
		case OperationType.SCAN_PRODUCT:
			loadProductDetail(result.getText());
			break;
		}
		restartPreviewAfterDelay(1000L);
	}

	/**
	 * 扫码成功后调用这个方法，获取商品的详细信息
	 * 
	 * @param type
	 *            二维码还是条形码
	 * @param productCode
	 *            二维码或者条形码的内容
	 */
	private void loadProductDetail(final String productCode) {
		mSimpleProgressDialog = new SimpleProgressDialog(this, true);
		mSimpleProgressDialog.setText("正在加载数据……").showDialog();
		ProductManager.getInstance().getProductDetail(MyApplication.getStoreid(), productCode,
				new ManagerCallBack<ProductEntity>() {
					@Override
					public void onSuccess(ProductEntity returnContent) {
						super.onSuccess(returnContent);
						mSimpleProgressDialog.cancleDialog();
						UITools.ToastMsg(ProductScanCodeActivity.this, "onSuccess");
						UITools.jumpProductAddNewActivity(ProductScanCodeActivity.this,
								ProductAddNewActivity.ScanStep.SCAN_FIRST,
								ProductAddNewActivity.ScanProductExist.EXIST, productCode);
						ProductScanCodeActivity.this.finish();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						UITools.ToastMsg(ProductScanCodeActivity.this, msg);
						mSimpleProgressDialog.cancleDialog();
						AlertDialog.Builder builder = new Builder(ProductScanCodeActivity.this);
						builder.setMessage("商品尚未入库，可以入库！");
						builder.setTitle("提示！");
						builder.setPositiveButton("入库", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								UITools.jumpProductAddNewActivity(ProductScanCodeActivity.this,
										ProductAddNewActivity.ScanStep.SCAN_FIRST,
										ProductAddNewActivity.ScanProductExist.NOT_EXIST, productCode);
								ProductScanCodeActivity.this.finish();
							}
						});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								setResult(RESULT_CANCELED);
								ProductScanCodeActivity.this.finish();
							}
						});
						builder.create().show();
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(this.getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
		}
		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);
		inactivityTimer.onResume();
		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;
	}

	/*
	 * ****************************************
	 * scanner function
	 * 
	 * ****************************************
	 */
	/**
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			// Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			// "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 *
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	@Override
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();
		// ResultHandler resultHandler =
		// ResultHandlerFactory.makeResultHandler(this, rawResult);

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			// historyManager.addHistoryItem(rawResult, resultHandler);
			// Then not from history, so beep/vibrate and we have an image to
			// draw on
			beepManager.playBeepSoundAndVibrate();
			// drawResultPoints(barcode, scaleFactor, rawResult);
		}

		switch (source) {
		/*
		 * case NATIVE_APP_INTENT: case PRODUCT_SEARCH_LINK:
		 * handleDecodeExternally(rawResult, resultHandler, barcode); break;
		 * case ZXING_LINK: if (scanFromWebPageManager == null ||
		 * !scanFromWebPageManager.isScanFromWebPage()) {
		 * handleDecodeInternally(rawResult, resultHandler, barcode); } else {
		 * handleDecodeExternally(rawResult, resultHandler, barcode); } break;
		 */
		case NONE:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if (fromLiveScan && prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
				/*
				 * Toast.makeText(getApplicationContext(),
				 * getResources().getString(R.string.msg_bulk_mode_scanned) +
				 * " (" + rawResult.getText() + ')', Toast.LENGTH_SHORT).show();
				 */
				// Wait a moment or else it will scan the same barcode
				// continuously about 3 times
				// restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
			} else {
				// change by yangping.wang
				// handleDecodeInternally(rawResult, resultHandler, barcode);
				// handleResultTest(rawResult, resultHandler, barcode);
				handleResult(rawResult);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	@Override
	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	public CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public Handler getHandler() {
		return handler;
	}

	@Override
	public Context getContext() {
		return this;
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton("OK", new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}
}
