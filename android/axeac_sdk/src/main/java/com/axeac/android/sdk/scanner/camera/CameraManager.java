/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axeac.android.sdk.scanner.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.zxing.PlanarYUVLuminanceSource;

import java.io.IOException;

/**
 * describe:This class encapsulates all the services of the camera and is the only class in the app that deals with the camera
 * <br>该类封装了相机的所有服务并且是该app中唯一与相机打交道的类
 * @author axeac
 * @version 1.0.0
 */
public final class CameraManager {

	private static final String TAG = CameraManager.class.getSimpleName();

	private static final int MIN_FRAME_WIDTH = 240;

	private static final int MAX_FRAME_WIDTH = 1200;

	private final Context context;

	private final CameraConfigurationManager configManager;

	private Camera camera;

	private AutoFocusManager autoFocusManager;

	private Rect framingRect;

	private Rect framingRectInPreview;

	private boolean initialized;

	private boolean previewing;

	private int requestedFramingRectWidth;

	private int requestedFramingRectHeight;

	// describe:Preview frames are delivered here, which we pass on to the registered
	//          handler. Make sure to clear the handler so it will only receive one message.
	/**
	 * 预览框在这传递给已注册的handler。及时清除handler确保它只接收一条消息。
	 */
	private final PreviewCallback previewCallback;

	public CameraManager(Context context) {
		this.context = context;
		this.configManager = new CameraConfigurationManager(context);
		previewCallback = new PreviewCallback(configManager);
	}

	// describe:Opens the camera driver and initializes the hardware parameters.
	/**
	 * 打开相机驱动程序并初始化硬件参数。
	 * @param holder
	 * The surface object which the camera will draw preview frames into.
	 * <br>相机将绘制预览帧的表面对象。
	 *
	 * @throws IOException
	 * Indicates the camera driver failed to open.
	 * <br>指示相机驱动程序未能打开。
	 */
	public synchronized void openDriver(SurfaceHolder holder)
			throws IOException {
		Camera theCamera = camera;
		if (theCamera == null) {
			// Get the camera on the back of the phone
			// 获取手机背面的摄像头
			theCamera = OpenCameraInterface.open();
			if (theCamera == null) {
				throw new IOException();
			}
			camera = theCamera;
		}

		// Set camera preview view
		// 设置摄像头预览view
		theCamera.setPreviewDisplay(holder);

		if (!initialized) {
			initialized = true;
			configManager.initFromCameraParameters(theCamera);
			if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
				setManualFramingRect(requestedFramingRectWidth,
						requestedFramingRectHeight);
				requestedFramingRectWidth = 0;
				requestedFramingRectHeight = 0;
			}
		}

		Camera.Parameters parameters = theCamera.getParameters();
		String parametersFlattened = parameters == null ? null : parameters
				.flatten();
		try {
			configManager.setDesiredCameraParameters(theCamera, false);
		}
		catch (RuntimeException re) {
			// Camera Driver failed
			// 打开相机驱动失败
			Log.w(TAG,
					"Camera rejected parameters. Setting only minimal safe-mode parameters");
			Log.i(TAG, "Resetting to saved camera params: "
					+ parametersFlattened);
			// Reset:
			// 复位：
			if (parametersFlattened != null) {
				parameters = theCamera.getParameters();
				parameters.unflatten(parametersFlattened);
				try {
					theCamera.setParameters(parameters);
					configManager.setDesiredCameraParameters(theCamera, true);
				}
				catch (RuntimeException re2) {
					// Well, darn. Give up
					// 好吧，放弃了
					Log.w(TAG,
							"Camera rejected even safe-mode parameters! No configuration");
				}
			}
		}

	}

	public synchronized boolean isOpen() {
		return camera != null;
	}

	// describe:Closes the camera driver if still in use.
	/**
	 * 如果相机驱动还在运行，关闭它
	 */
	public synchronized void closeDriver() {
		if (camera != null) {
			camera.release();
			camera = null;
			/*
			  Make sure to clear these each time we close the camera, so that any scanning rect
			  requested by intent is forgotten.

			  每次关闭摄像头，确保清除这些。
			 */

			framingRect = null;
			framingRectInPreview = null;
		}
	}

	// describe:Asks the camera hardware to begin drawing preview frames to the screen.
	/**
	 * 告诉相机硬件开始将预览帧绘制到屏幕上。
	 */
	public synchronized void startPreview() {
		Camera theCamera = camera;
		if (theCamera != null && !previewing) {
			/*
			  Starts capturing and drawing preview frames to the screen
			  Preview will not actually start until a surface is supplied with
			  setPreviewDisplay(SurfaceHolder) or setPreviewTexture(SurfaceTexture).

			  开始捕捉并将预览帧绘制到屏幕上，直到表面提供setpreviewdisplay（SurfaceHolder）
			  或setpreviewtexture（SurfaceTexture）,预览才会真正开始。
			 */

			theCamera.startPreview();

			previewing = true;
			autoFocusManager = new AutoFocusManager(context, camera);
		}
	}

	// describe:Tells the camera to stop drawing preview frames.
	/**
	 * 告诉相机停止绘制预览帧
	 */
	public synchronized void stopPreview() {
		if (autoFocusManager != null) {
			autoFocusManager.stop();
			autoFocusManager = null;
		}
		if (camera != null && previewing) {
			camera.stopPreview();
			previewCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	// describe：Sets whether or not to turn on the flash
	/**
	 * 设置是否开启闪光灯
	 */
	public synchronized void setTorch(boolean newSetting) {
		if (newSetting != configManager.getTorchState(camera)) {
			if (camera != null) {
				if (autoFocusManager != null) {
					autoFocusManager.stop();
				}
				configManager.setTorch(camera, newSetting);
				if (autoFocusManager != null) {
					autoFocusManager.start();
				}
			}
		}
	}

	// describe:Two bind operations: <br/>
	//          1: bind the handler to the callback function; <br/>
	//          2: bind the camera to the callback function; <br/>
	//         To sum up, the function's function is to call the hander to send
	//         the incoming message when the camera preview interface is ready
	/**
	 *两个绑定操作：<br/>
	 * <br>1：将handler与回调函数绑定；<br/>
	 * <br>2：将相机与回调函数绑定<br/>
	 * <br>综上，该函数的作用是当相机的预览界面准备就绪后就会调用hander向其发送传入的message
	 *
	 * @param handler
	 * The handler to send the message to.
	 * <br>发送消息的handler
	 *
	 * @param message
	 * The what field of the message to be sent.
	 * <br>需要发送消息的字段
	 */
	public synchronized void requestPreviewFrame(Handler handler, int message) {
		Camera theCamera = camera;
		if (theCamera != null && previewing) {
			previewCallback.setHandler(handler, message);
			// bind the camera to the callback function,When the preview screen is ready,
			// the Camera.PreviewCallback.onPreviewFrame will be called back
			// 绑定相机回调函数，当预览界面准备就绪后会回调Camera.PreviewCallback.onPreviewFrame
			theCamera.setOneShotPreviewCallback(previewCallback);
		}
	}

	// describe:Calculates the framing rect which the UI should draw to show the user
	//          where to place the barcode. This target helps with alignment as well as
	//          forces the user to hold the device far enough away to ensure the image
	//          will be in focus.
	/**
	 * 计算UI应绘制的框架矩形，以向用户显示放置条形码的位置。该目标有助于对齐，
	 * <br>并强制用户将设备保持足够远，以确保图像处于关注状态。
	 *
	 * @return The rectangle to draw on screen in window coordinates.
	 * <br>根据窗口坐标在屏幕上绘制的矩形
	 */
	public synchronized Rect getFramingRect() {
		if (framingRect == null) {
			if (camera == null) {
				return null;
			}
			Point screenResolution = configManager.getScreenResolution();
			if (screenResolution == null) {
				return null;
			}

			int width = findDesiredDimensionInRange(screenResolution.x,
					MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
			// Set the scan box to a square
			// 将扫描框设置成一个正方形
			int height = width;

			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);

		}

		return framingRect;
	}

	// describe:Target 5/8 of each dimension
	/**
	 * 目标为每个尺寸的5/8<br/>
	 * <br>计算结果在hardMin~hardMax之间
	 *
	 * @param resolution
	 * @param hardMin
	 * @param hardMax
	 * @return
	 */
	private static int findDesiredDimensionInRange(int resolution, int hardMin,
												   int hardMax) {
		int dim = 5 * resolution / 8;
		if (dim < hardMin) {
			return hardMin;
		}
		if (dim > hardMax) {
			return hardMax;
		}
		return dim;
	}

	// describe:Like {@link #getFramingRect} but coordinates are in terms of the preview
	//         frame, not UI / screen.
	/**
	 * 像{@link #getFramingRect}坐标是根据预览框，不是UI或者屏幕
	 */
	public synchronized Rect getFramingRectInPreview() {
		if (framingRectInPreview == null) {
			Rect framingRect = getFramingRect();
			if (framingRect == null) {
				return null;
			}
			Rect rect = new Rect(framingRect);
			Point cameraResolution = configManager.getCameraResolution();
			Point screenResolution = configManager.getScreenResolution();
			if (cameraResolution == null || screenResolution == null) {
				return null;
			}
			rect.left = rect.left * cameraResolution.y / screenResolution.x;
			rect.right = rect.right * cameraResolution.y / screenResolution.x;
			rect.top = rect.top * cameraResolution.x / screenResolution.y;
			rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
			framingRectInPreview = rect;
		}

		return framingRectInPreview;
	}

	// describe:Allows third party apps to specify the scanning rectangle dimensions,
	//          rather than determine them automatically based on screen resolution.
	/**
	 * 允许第三方应用程序指定扫描矩形尺寸，而不是根据屏幕分辨率自动确定它们。
	 * @param width
	 * The width in pixels to scan.
	 * <br>扫描像素宽度
	 *
	 * @param height
	 * The height in pixels to scan.
	 * <br>扫描像素高度
	 */
	public synchronized void setManualFramingRect(int width, int height) {
		if (initialized) {
			Point screenResolution = configManager.getScreenResolution();
			if (width > screenResolution.x) {
				width = screenResolution.x;
			}
			if (height > screenResolution.y) {
				height = screenResolution.y;
			}
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
			Log.d(TAG, "Calculated manual framing rect: " + framingRect);
			framingRectInPreview = null;
		}
		else {
			requestedFramingRectWidth = width;
			requestedFramingRectHeight = height;
		}
	}

	// describe:A factory method to build the appropriate LuminanceSource object based on
	//         the format of the preview buffers, as described by Camera.Parameters.
	/**
	 * 基于Camera.Parameters描述的基于预览缓冲区的格式的工厂方法来构建适当的LuminanceSource对象。
	 * @param data
	 * A preview frame.
	 * <br>预览框架
	 * @param width
	 * The width of the image.
	 * <br>图片宽度
	 * @param height
	 * The height of the image.
	 * <br>图片高度
	 *@return A PlanarYUVLuminanceSource instance.
	 * <br>一个PlanarYUVLuminanceSource实例
	 */
	public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data,
														 int width, int height) {
		Rect rect = getFramingRectInPreview();
		if (rect == null) {
			return null;
		}
		return new PlanarYUVLuminanceSource(data, width, height, rect.left,
				rect.top, rect.width(), rect.height(), false);
	}

	// describe:focus on small
	/**
	 * 放小焦点
	 */
	public void zoomOut() {
		if (camera != null && camera.getParameters().isZoomSupported()) {

			Camera.Parameters parameters = camera.getParameters();
			if (parameters.getZoom() <= 0) {
				return;
			}

			parameters.setZoom(parameters.getZoom() - 1);
			camera.setParameters(parameters);

		}
	}

	// describe:focus on zoom
	/**
	 * 焦点放大
	 */
	public void zoomIn() {
		if (camera != null && camera.getParameters().isZoomSupported()) {

			Camera.Parameters parameters = camera.getParameters();
			if (parameters.getZoom() >= parameters.getMaxZoom()) {
				return;
			}

			parameters.setZoom(parameters.getZoom() + 1);
			camera.setParameters(parameters);

		}
	}

	// describe:zoom
	/**
	 * 缩放
	 * @param scale
	 * 缩放比例
	 */
	public void setCameraZoom(int scale) {
		if (camera != null && camera.getParameters().isZoomSupported()
				&& scale <= camera.getParameters().getMaxZoom() && scale >= 0) {

			Camera.Parameters parameters = camera.getParameters();

			parameters.setZoom(scale);
			camera.setParameters(parameters);

		}
	}
}
