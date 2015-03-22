package com.globalways.csscli.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.globalways.csscli.android.camera.CameraManager;
import com.google.zxing.Result;

public interface ScanCodeInterface {
	void drawViewfinder();

	CameraManager getCameraManager();

	Handler getHandler();

	void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor);

	Context getContext();

	ViewfinderView getViewfinderView();
}
