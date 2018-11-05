package com.axeac.android.sdk.scanner.decode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import com.axeac.android.sdk.activity.CaptureActivity;
import com.axeac.android.sdk.scanner.common.Config;

final class DecodeHandler extends Handler {

	private static final String TAG = DecodeHandler.class.getSimpleName();

	private final CaptureActivity activity;

	private final MultiFormatReader multiFormatReader;

	private boolean running = true;

	DecodeHandler(CaptureActivity activity, Map<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		if (!running) {
			return;
		}
		switch (message.what) {
			case Config.decode:
				decode((byte[]) message.obj, message.arg1, message.arg2);
				break;
			case Config.quit:
				running = false;
				Looper.myLooper().quit();
				break;
		}
	}

	// describe:Decode the data within the viewfinder rectangle, and time how long it
	//          took. For efficiency, reuse the same reader objects from one decode to
	//          the next.

	/**
	 * 解码取景器矩形中的数据，以及花费多长时间。 为了效率，将相同的读取对象从一个解码重用到下一个解码。
	 * @param data
	 * The YUV preview frame.
	 * <br>YUV预览框
	 * @param width
	 * The width of the preview frame.
	 * <br>预览框宽度
	 * @param height
	 * The height of the preview frame.
	 * <br>预览框高度
	 */
	private void decode(byte[] data, int width, int height) {
		long start = System.currentTimeMillis();
		Result rawResult = null;

		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width;
		width = height;
		height = tmp;

		PlanarYUVLuminanceSource source = activity.getCameraManager()
				.buildLuminanceSource(rotatedData, width, height);
		if (source != null) {
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			try {
				// The preview interface finally takes a bitmap and then decodes it
				// 预览界面最终取到的是个bitmap，然后对其进行解码
				rawResult = multiFormatReader.decodeWithState(bitmap);
			} catch (ReaderException re) {

			} finally {
				multiFormatReader.reset();
			}
		}

		Handler handler = activity.getHandler();
		if (rawResult != null) {
			// Don't log the barcode contents for security.
			// 为了安全起见，不要记录条形码内容
			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode in " + (end - start) + " ms");
			if (handler != null) {
				Message message = Message.obtain(handler,
						Config.decode_succeeded, rawResult);
				Bundle bundle = new Bundle();
				bundleThumbnail(source, bundle);
				message.setData(bundle);
				message.sendToTarget();
			}
		} else {
			if (handler != null) {
				Message message = Message.obtain(handler, Config.decode_failed);
				message.sendToTarget();
			}
		}
	}

	private static void bundleThumbnail(PlanarYUVLuminanceSource source,
										Bundle bundle) {
		int[] pixels = source.renderThumbnail();
		int width = source.getThumbnailWidth();
		int height = source.getThumbnailHeight();
		Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height,
				Bitmap.Config.ARGB_8888);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
		bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
		bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width
				/ source.getWidth());
	}

}
