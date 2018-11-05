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

import android.content.SharedPreferences;

import com.axeac.android.sdk.scanner.common.Config;


/**
 * describe:Enumerates settings of the prefernce controlling the front light.
 * <br>列举控制前灯的偏好设置
 * @author axeac
 * @version 1.0.0
 */
public enum FrontLightMode {

	//describe:Always on.
	/**
	 * 一直开启
	 * */
	ON,
	// describe:On only when ambient light is low.
	/**
	 * 仅在光线不足时开启
	 *  */
	AUTO,
	// describe:Always off.
	/**
	 * 一直关闭
	 * */
	OFF;

	private static FrontLightMode parse(String modeString) {
		return modeString == null ? OFF : valueOf(modeString);
	}

	public static FrontLightMode readPref(SharedPreferences sharedPrefs) {
		return parse(sharedPrefs.getString(
				Config.KEY_FRONT_LIGHT_MODE, null));
	}

}
