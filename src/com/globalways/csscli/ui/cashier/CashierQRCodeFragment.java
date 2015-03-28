package com.globalways.csscli.ui.cashier;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.globalways.csscli.tools.PicassoImageLoader;
import com.globalways.csscli.ui.BaseFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

/**
 * @author James
 *
 */
public class CashierQRCodeFragment extends BaseFragment implements OnClickListener, SurfaceHolder.Callback,
		ScanCodeInterface {
	private static final String TAG = CashierQRCodeFragment.class.getSimpleName();

	private ProductEntity productEntity;

	private View layoutView;
	private ImageView imageProductAva;
	private TextView textProductName, textProductPrice, textNumber;
	private Button btnLess, btnAdd, btnAddCashier, btnOrder;

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

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.cashier_qrcode_fragment, container, false);
		}
		// cameraView.setLayoutParams(new
		// LinearLayout.LayoutParams(cameraAreaWidth, cameraAreaHeight));

		FrameLayout cameraView = (FrameLayout) layoutView.findViewById(R.id.camera_view);
		Point screen = new Point();
		getActivity().getWindowManager().getDefaultDisplay().getRealSize(screen);
		double tmp = (double) screen.y / (double) screen.x;
		int cameraAreaWidth = cameraView.getLayoutParams().width;
		int cameraAreaHeight = (int) (cameraAreaWidth * tmp);
		cameraView.getLayoutParams().height = cameraAreaHeight;
		cameraView.getLayoutParams().width = cameraAreaWidth;
		return layoutView;
	}

	public void setTotalPrice(long totalPrice) {
		if (totalPrice > 0) {
			btnOrder.setText("商品总价：￥" + totalPrice / 100.00);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// scanner
		inactivityTimer = new InactivityTimer(this.getActivity());
		beepManager = new BeepManager(this.getActivity());
		ambientLightManager = new AmbientLightManager(this.getActivity());
		hasSurface = false;
		PreferenceManager.setDefaultValues(this.getActivity(), R.xml.preferences, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		refreshView();
	}

	/**
	 * 处理扫描获取到的数据
	 * 
	 * @param result
	 */
	public void handleResult(Result result) {
		MyLog.d(TAG, result.getText());
		addCashierProduct();
		loadProductDetail(result.getText());
		// 重置扫码
		restartPreviewAfterDelay(1000L);
	}

	/**
	 * 订单结束后调用
	 */
	public void orderFinish() {
		btnOrder.setText("去结算");
	}

	/**
	 * 扫码成功后调用这个方法，获取商品的详细信息
	 * 
	 * @param type
	 *            二维码还是条形码
	 * @param productCode
	 *            二维码或者条形码的内容
	 */
	private void loadProductDetail(String productCode) {
		ProductManager.getInstance().getProductDetail(MyApplication.getStoreid(), productCode,
				new ManagerCallBack<ProductEntity>() {
					@Override
					public void onSuccess(ProductEntity returnContent) {
						super.onSuccess(returnContent);
						productEntity = returnContent;
						productEntity.setShoppingNumber(1);
						refreshView();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void refreshView() {
		if (productEntity != null) {
			textNumber.setText("1");
			new PicassoImageLoader(getActivity()).showImage(productEntity.getProduct_avatar(), R.drawable.logo,
					R.drawable.logo, imageProductAva);
			textProductName.setText(productEntity.getProduct_name());
			textProductPrice.setText("￥ " + productEntity.getProduct_price() / 100.00 + " / "
					+ productEntity.getProduct_unit() + "  ");
		}
	}

	private void addCashierProduct() {
		if (productEntity != null) {
			((CashierActivity) getActivity()).addCashierProduct(productEntity);
			reset();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textNumber:
			if (productEntity != null) {
				showNumberDialog();
			}
			break;
		case R.id.btnLess:
			if (productEntity != null) {
				String text1 = textNumber.getText().toString().trim();
				if (Integer.valueOf(textNumber.getText().toString().trim()) > 1) {
					productEntity.setShoppingNumber(Integer.valueOf(text1) - 1);
					textNumber.setText(productEntity.getShoppingNumber() + "");
				}
				btnLess.setEnabled(!textNumber.getText().toString().trim().equals("1"));
			}
			break;
		case R.id.btnAdd:
			if (productEntity != null) {
				productEntity.setShoppingNumber(productEntity.getShoppingNumber() + 1);
				textNumber.setText(productEntity.getShoppingNumber() + "");
				btnLess.setEnabled(!textNumber.getText().toString().trim().equals("1"));
			}
			break;
		case R.id.btnAddCashier:
			addCashierProduct();
			break;
		case R.id.btnSingle:
			if (productEntity != null) {
				addCashierProduct();
			} else {
				if (((CashierActivity) getActivity()).getCount() > 0) {
					((CashierActivity) getActivity()).showSignDialog();
				}
			}
			break;
		}
	}

	private void reset() {
		productEntity = null;
		textNumber.setText("1");
		textProductName.setText("");
		textProductPrice.setText("");
		imageProductAva.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.logo));
	}

	private void initView() {
		imageProductAva = (ImageView) layoutView.findViewById(R.id.imageProductAva);
		textProductName = (TextView) layoutView.findViewById(R.id.textProductName);
		textProductPrice = (TextView) layoutView.findViewById(R.id.textProductPrice);
		textNumber = (TextView) layoutView.findViewById(R.id.textNumber);
		textNumber.setOnClickListener(this);

		btnLess = (Button) layoutView.findViewById(R.id.btnLess);
		btnLess.setOnClickListener(this);
		btnAdd = (Button) layoutView.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
		btnAddCashier = (Button) layoutView.findViewById(R.id.btnAddCashier);
		btnAddCashier.setOnClickListener(this);
		btnOrder = (Button) layoutView.findViewById(R.id.btnSingle);
		btnOrder.setOnClickListener(this);
	}

	/** 弹出输入数量的对话框 */
	@SuppressLint("InflateParams")
	private void showNumberDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.cashier_list_item_edittextview, null);
		final EditText editText = (EditText) dialogView.findViewById(R.id.editDialogNumber);
		final Button btnDialogLess = (Button) dialogView.findViewById(R.id.btnDialogLess);
		final Button btnDialogAdd = (Button) dialogView.findViewById(R.id.btnDialogAdd);
		editText.setText(textNumber.getText().toString());
		editText.setSelection(editText.getText().toString().trim().length());
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = editText.getText().toString().trim();
				if (text != null && !text.isEmpty()) {
					if (text.startsWith("0") && text.length() > 1) {
						text = text.substring(1);
						editText.setText(text);
					}
				} else {
					editText.setText("1");
				}
				btnDialogLess.setEnabled(!editText.getText().toString().trim().equals("1"));
				editText.setSelection(editText.getText().toString().trim().length());
			}
		});
		if (editText.getText().toString().trim().equals("1")) {
			btnDialogLess.setEnabled(false);
		}
		btnDialogLess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = editText.getText().toString().trim();
				if (!text.equals("1")) {
					editText.setText(Integer.valueOf(editText.getText().toString().trim()) - 1 + "");
				}
			}
		});
		btnDialogAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editText.setText(Integer.valueOf(editText.getText().toString().trim()) + 1 + "");
			}
		});
		builder.setView(dialogView);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (productEntity != null) {
					productEntity.setShoppingNumber(Integer.valueOf(editText.getText().toString().trim()));
				}
				textNumber.setText(editText.getText().toString());
			}
		});
		builder.create().show();
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
		cameraManager = new CameraManager(this.getActivity().getApplication());

		viewfinderView = (ViewfinderView) this.getActivity().findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		SurfaceView surfaceView = (SurfaceView) this.getActivity().findViewById(R.id.preview_view);
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
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
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
		return getActivity();
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
			SurfaceView surfaceView = (SurfaceView) this.getActivity().findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton("OK", new FinishListener(this.getActivity()));
		builder.setOnCancelListener(new FinishListener(this.getActivity()));
		builder.show();
	}
}
