package com.axeac.android.sdk.scanner.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.axeac.android.sdk.scanner.common.Config;

/**
 * describe:Camera parameter settings class
 * <br>摄像头参数的设置类
 * @author axeac
 * @version 1.0.0
 */
final class CameraConfigurationManager {

	private static final String TAG = "CameraConfiguration";

	// describe:This is bigger than the size of a small screen, which is still supported.
	//          The routine below will still select the default (presumably 480x320) size for these.
	// 			This prevents accidental selection of very low resolution on some devices.

	/**
	 * 支持小尺寸屏幕
	 * <br>下面程序依然会选择默认的尺寸（大概是480*320）
	 * <br>这可以防止在某些设备上意外选择非常低的分辨率
	 * */
	private static final int MIN_PREVIEW_PIXELS = 480 * 320;
	private static final double MAX_ASPECT_DISTORTION = 0.15;

	private final Context context;
	// describe:Screen resolution
	/**
	 * 屏幕分辨率
	 */
	private Point screenResolution;

	// describe:camera resolution
	/**
	 * 相机分辨率
	 */
	private Point cameraResolution;

	CameraConfigurationManager(Context context) {
		this.context = context;
	}

	// describe:Reads, one time, values from the camera that are needed by the app.
	/**
	 * 应用程序从相机读取一次所需要的值
	 */
	void initFromCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point theScreenResolution = new Point();

		theScreenResolution = getDisplaySize(display);

		screenResolution = theScreenResolution;
		Log.i(TAG, "Screen resolution: " + screenResolution);

		cameraResolution = findBestPreviewSizeValue(parameters,
				screenResolution);
		Log.i(TAG, "Camera resolution: " + cameraResolution);
	}

	@SuppressLint("NewApi")
	private Point getDisplaySize(final Display display) {
		final Point point = new Point();
		try {
			display.getSize(point);
		}
		catch (NoSuchMethodError ignore) {
			// Older device
			// 旧设备
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		return point;
	}

	void setDesiredCameraParameters(Camera camera, boolean safeMode) {
		Camera.Parameters parameters = camera.getParameters();

		if (parameters == null) {
			Log.w(TAG,
					"Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

		if (safeMode) {
			Log.w(TAG,
					"In camera config safe mode -- most settings will not be honored");
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		// Initialize flash
		// 初始化闪光灯
		initializeTorch(parameters, prefs, safeMode);

		// Auto focus is used by default
		// 默认使用自动对焦
		String focusMode = findSettableValue(
				parameters.getSupportedFocusModes(),
				Camera.Parameters.FOCUS_MODE_AUTO);

		// Maybe selected auto-focus but not available, so fall through here:
		// 也许选择自动对焦不可用，此时通过此方法
		if (!safeMode && focusMode == null) {
			focusMode = findSettableValue(parameters.getSupportedFocusModes(),
					Camera.Parameters.FOCUS_MODE_MACRO,
					Camera.Parameters.FOCUS_MODE_EDOF);
		}
		if (focusMode != null) {
			parameters.setFocusMode(focusMode);
		}

		if (prefs.getBoolean(Config.KEY_INVERT_SCAN, false)) {
			String colorMode = findSettableValue(
					parameters.getSupportedColorEffects(),
					Camera.Parameters.EFFECT_NEGATIVE);
			if (colorMode != null) {
				parameters.setColorEffect(colorMode);
			}
		}

		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		camera.setParameters(parameters);

		Camera.Parameters afterParameters = camera.getParameters();
		Camera.Size afterSize = afterParameters.getPreviewSize();
		if (afterSize != null
				&& (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
			Log.w(TAG, "Camera said it supported preview size "
					+ cameraResolution.x + 'x' + cameraResolution.y
					+ ", but after setting it, preview size is "
					+ afterSize.width + 'x' + afterSize.height);
			cameraResolution.x = afterSize.width;
			cameraResolution.y = afterSize.height;
		}

		camera.setDisplayOrientation(90);
	}

	Point getCameraResolution() {
		return cameraResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

	boolean getTorchState(Camera camera) {
		if (camera != null) {
			Camera.Parameters parameters = camera.getParameters();
			if (parameters != null) {
				String flashMode = camera.getParameters().getFlashMode();
				return flashMode != null
						&& (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) || Camera.Parameters.FLASH_MODE_TORCH
						.equals(flashMode));
			}
		}
		return false;
	}

	void setTorch(Camera camera, boolean newSetting) {
		Camera.Parameters parameters = camera.getParameters();
		doSetTorch(parameters, newSetting, false);
		camera.setParameters(parameters);
	}

	private void initializeTorch(Camera.Parameters parameters,
								 SharedPreferences prefs, boolean safeMode) {
		boolean currentSetting = FrontLightMode.readPref(prefs) == FrontLightMode.ON;
		doSetTorch(parameters, currentSetting, safeMode);
	}

	private void doSetTorch(Camera.Parameters parameters, boolean newSetting,
							boolean safeMode) {
		String flashMode;
		if (newSetting) {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(),
					Camera.Parameters.FLASH_MODE_TORCH,
					Camera.Parameters.FLASH_MODE_ON);
		}
		else {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(),
					Camera.Parameters.FLASH_MODE_OFF);
		}
		if (flashMode != null) {
			parameters.setFlashMode(flashMode);
		}
	}

	// describe:The most suitable preview interface size is calculated from the resolution supported by the camera
	/**
	 * 从相机支持的分辨率中计算出最适合的预览界面尺寸
	 * @param parameters
	 * Camera.Parameters对象
	 * @param screenResolution
	 * Point对象
	 * @return
	 * Point对象
	 */
	private Point findBestPreviewSizeValue(Camera.Parameters parameters,
										   Point screenResolution) {
		List<Camera.Size> rawSupportedSizes = parameters
				.getSupportedPreviewSizes();
		if (rawSupportedSizes == null) {
			Log.w(TAG,
					"Device returned no supported preview sizes; using default");
			Camera.Size defaultSize = parameters.getPreviewSize();
			return new Point(defaultSize.width, defaultSize.height);
		}

		// Sort by size, descending
		// 按照大小排序，降序
		List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(
				rawSupportedSizes);
		Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size a, Camera.Size b) {
				int aPixels = a.height * a.width;
				int bPixels = b.height * b.width;
				if (bPixels < aPixels) {
					return -1;
				}
				if (bPixels > aPixels) {
					return 1;
				}
				return 0;
			}
		});

		if (Log.isLoggable(TAG, Log.INFO)) {
			StringBuilder previewSizesString = new StringBuilder();
			for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
				previewSizesString.append(supportedPreviewSize.width)
						.append('x').append(supportedPreviewSize.height)
						.append(' ');
			}
			Log.i(TAG, "Supported preview sizes: " + previewSizesString);
		}

		double screenAspectRatio = (double) screenResolution.x
				/ (double) screenResolution.y;

		// Remove sizes that are unsuitable
		// 删除不合适的尺寸
		Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
		while (it.hasNext()) {
			Camera.Size supportedPreviewSize = it.next();
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
				it.remove();
				continue;
			}

			boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight
					: realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth
					: realHeight;

			double aspectRatio = (double) maybeFlippedWidth
					/ (double) maybeFlippedHeight;
			double distortion = Math.abs(aspectRatio - screenAspectRatio);
			if (distortion > MAX_ASPECT_DISTORTION) {
				it.remove();
				continue;
			}

			if (maybeFlippedWidth == screenResolution.x
					&& maybeFlippedHeight == screenResolution.y) {
				Point exactPoint = new Point(realWidth, realHeight);
				Log.i(TAG, "Found preview size exactly matching screen size: "
						+ exactPoint);
				return exactPoint;
			}
		}

		/*
		  If no exact match, use largest preview size. This was not a great
		  idea on older devices because of the additional computation needed.
		  We're likely to get here on newer Android 4+ devices, where the CPU
		  is much more powerful.

  		  如果没有精确计算，使用最大预览尺寸。但对于旧设备不是一个好的方法，因为需要
  		  额外的计算。大多使用在较新的cpu更好的Android 4+设备。
		 */

		if (!supportedPreviewSizes.isEmpty()) {
			Camera.Size largestPreview = supportedPreviewSizes.get(0);
			Point largestSize = new Point(largestPreview.width,
					largestPreview.height);
			Log.i(TAG, "Using largest suitable preview size: " + largestSize);
			return largestSize;
		}

		// If there is nothing at all suitable, return current preview size
		// 如果没有什么合适的，返回当前预览大小
		Camera.Size defaultPreview = parameters.getPreviewSize();
		Point defaultSize = new Point(defaultPreview.width,
				defaultPreview.height);
		Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);

		return defaultSize;
	}

	// describe:Search for desiredValues in supportedValues, and return null when you cannot find it
	/**
	 * 在supportedValues中寻找desiredValues，找不到则返回null
	 * @param supportedValues
	 * @param desiredValues
	 * @return string
	 */
	private static String findSettableValue(Collection<String> supportedValues,
											String... desiredValues) {
		Log.i(TAG, "Supported values: " + supportedValues);
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		Log.i(TAG, "Settable value: " + result);
		return result;
	}

}
