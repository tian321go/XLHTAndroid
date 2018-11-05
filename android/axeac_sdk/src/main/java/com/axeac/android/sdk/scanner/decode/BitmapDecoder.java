package com.axeac.android.sdk.scanner.decode;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;
import java.util.Vector;

/**
 * describe：decode from bitmap
 * <br>从bitmap解码
 * @author axeac
 * @version 1.0.0
 */
public class BitmapDecoder {

	MultiFormatReader multiFormatReader;

	public BitmapDecoder(Context context) {

		multiFormatReader = new MultiFormatReader();

		// Decode parameters
		/**
		 * 解码的参数
		 * */
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(
				2);
		// The type of encoding that can be parsed
		// 可以解析的编码类型
		Vector<BarcodeFormat> decodeFormats = new Vector<>();
		if (decodeFormats == null || decodeFormats.isEmpty()) {
			decodeFormats = new Vector<BarcodeFormat>();

			// Here is the type that can be scanned, which I have chosen to support
			// 这里设置可扫描的类型，这里选择了都支持
			decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		}
		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

		// Sets the continuation character encoding format to UTF8
		// 设置继续的字符编码格式为UTF8
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

		// Set the parsing configuration parameters
		// 设置解析配置参数
		multiFormatReader.setHints(hints);

	}

	// describe:Get the analytical results
	/**
	 * 获取解码结果
	 * @param bitmap
	 * Bitmap对象
	 * @return
	 */
	public Result getRawResult(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		try {
			return multiFormatReader.decodeWithState(new BinaryBitmap(
					new HybridBinarizer(new BitmapLuminanceSource(bitmap))));
		}
		catch (NotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}
