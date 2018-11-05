/*
 * Copyright (C) 2012 ZXing authors
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

import android.hardware.Camera;

/**
 * describe:This category is used to detect the number of cameras on the phone,
 *          <br>if there are two cameras, then take the back of the camera
 * <br>该类用于检测手机上摄像头的个数，如果有两个摄像头，则取背面的摄像头
 * @author axeac
 * @version 1.0.0
 */
public final class OpenCameraInterface {

	private static final String TAG = OpenCameraInterface.class.getName();

	private OpenCameraInterface() {
	}

	// describe:Opens a rear-facing camera with {@link Camera#open(int)}, if one exists,
	//          or opens camera 0.
	/**
	 * 使用{@link Camera＃open（int）}打开背面照相机，如果只有一个存在，则打开相机0。
	 */
	public static Camera open() {

		int numCameras = Camera.getNumberOfCameras();
		if (numCameras == 0) {
			return null;
		}

		int index = 0;
		while (index < numCameras) {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(index, cameraInfo);
			// CAMERA_FACING_BACK：
			// 手机背面的摄像头
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				break;
			}
			index++;
		}

		Camera camera;
		if (index < numCameras) {
			camera = Camera.open(index);
		} else {
			camera = Camera.open(0);
		}

		return camera;
	}

}
