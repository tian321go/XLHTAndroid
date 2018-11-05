package com.axeac.android.sdk.adapters;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
/**
 * 多点触控监听器
 * @author axeac
 * @version 1.0.0
 * */
public class MulitPointTouchListener implements View.OnTouchListener {

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	public ImageView image;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	/**
	 * 起始点
	 * */
	private PointF start = new PointF();
	/**
	 * 中间点
	 * */
	private PointF mid = new PointF();
	private float oldDist = 1f;

	public MulitPointTouchListener(ImageView image) {
		super();
		this.image = image;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.image.setScaleType(ImageView.ScaleType.MATRIX);
		ImageView view = (ImageView) v;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			Log.w("FLAG", "ACTION_DOWN");
			matrix.set(view.getImageMatrix());
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.w("FLAG", "ACTION_POINTER_DOWN");
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.w("FLAG", "ACTION_UP");
		case MotionEvent.ACTION_POINTER_UP:
			Log.w("FLAG", "ACTION_POINTER_UP");
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.w("FLAG", "ACTION_MOVE");
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		view.setImageMatrix(matrix);
		return true;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}