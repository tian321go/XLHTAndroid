package com.axeac.android.sdk.scanner.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.scanner.camera.CameraManager;

/**
 * describe:The view is a layer of views that overlays the preview view of the camera. Scanning
 *          <br>area composition principle, in fact, in the preview view to draw four mask layer,
 *          <br>the middle part of the leave behind to remain transparent, and draw a laser line,
 *          <br>in fact, the line is to show it, and scan function has nothing to do.
 * <br>该视图是覆盖在相机的预览视图之上的一层视图。扫描区构成原理，其实是在预览视图上画四块遮罩层，
 * <br>中间留下的部分保持透明，并画上一条激光线，实际上该线条就是展示而已，与扫描功能没有任何关系。
 * @author axeac
 * @version 1.0.0
 */
public final class ViewfinderView extends View {

	// describe:The time to refresh the interface
	/**
	 * 刷新界面的时间
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	private int CORNER_PADDING;

	// describe:The width of the middle line in the scan box
	/**
	 * 扫描框中的中间线的宽度
	 */
	private static int MIDDLE_LINE_WIDTH;

	// describe:The clearance between the middle line of the scan frame and the scan frame
	/**
	 * 扫描框中的中间线的与扫描框左右的间隙
	 */
	private static int MIDDLE_LINE_PADDING;

	// describe:The middle of the scan frame is refreshed each time the distance is moved
	/**
	 * 扫描框的中间线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 10;

	// describe:Paintbrush object reference
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	// describe:The top position of the middle slip line
	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;

	// describe:The bottom position of the middle slip line
	/**
	 * 中间滑动线的最底端位置
	 */
	private int slideBottom;

	private static final int MAX_RESULT_POINTS = 20;

	private Bitmap resultBitmap;

	// describe:the color of the mask
	/**
	 * 遮掩层的颜色
	 */
	private final int maskColor;
	private final int resultColor;

	private final int resultPointColor;
	private List<ResultPoint> possibleResultPoints;

	private List<ResultPoint> lastPossibleResultPoints;

	// describe:Drawing controls for the first time
	/**
	 * 第一次绘制控件
	 */
	boolean isFirst = true;

	private CameraManager cameraManager;

	// This constructor is used when the class is built from an XML resource.
	/**
	 * 当从XML资源构建类时使用此构造函数
	 * */
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		CORNER_PADDING = dip2px(context, 0.0F);
		MIDDLE_LINE_PADDING = dip2px(context, 20.0F);
		MIDDLE_LINE_WIDTH = dip2px(context, 3.0F);

		// Open anti-aliasing
		// 开启反锯齿
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		Resources resources = getResources();

		// the color of the mask
		// 遮掩层颜色
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);

		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new ArrayList<ResultPoint>(5);
		lastPossibleResultPoints = null;

	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (cameraManager == null) {
			return;
		}
		Rect frame = cameraManager.getFramingRect();
		if (frame == null) {
			return;
		}

		// Draw the cover layer
		// 绘制遮掩层
		drawCover(canvas, frame);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			// 绘制扫描结果的图
			paint.setAlpha(0xA0);
			canvas.drawBitmap(resultBitmap, null, frame, paint);
		}
		else {

			// Draw the corner on the edge of the scan frame
			// 画扫描框边上的角
			drawRectEdges(canvas, frame);

			// Draw a scan line
			// 绘制扫描线
			drawScanningLine(canvas, frame);

			List<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			}
			else {
				possibleResultPoints = new ArrayList<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}

			// Only refresh the contents of the scan box, other places do not refresh
			// 只刷新扫描框的内容，其他地方不刷新
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);

		}
	}

	// describe:draw a scan line
	/**
	 * 绘制扫描线
	 * @param canvas
	 * Canvas对象
	 * @param frame
	 * scan frame
	 * <br>扫描框
	 */
	private void drawScanningLine(Canvas canvas, Rect frame) {

		// Initialize the top and bottom of the middle line slideable
		// 初始化中间线滑动的最上边和最下边
		if (isFirst) {
			isFirst = false;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}

		// Draw the middle of the line, refresh the interface each time, the middle of the line down to move SPEEN_DISTANCE
		// 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
		slideTop += SPEEN_DISTANCE;
		if (slideTop >= slideBottom) {
			slideTop = frame.top;
		}

		// Draw lines from picture resources
		// 从图片资源画扫描线
		Rect lineRect = new Rect();
		lineRect.left = frame.left + MIDDLE_LINE_PADDING;
		lineRect.right = frame.right - MIDDLE_LINE_PADDING;
		lineRect.top = slideTop;
		lineRect.bottom = (slideTop + MIDDLE_LINE_WIDTH);
		canvas.drawBitmap(((BitmapDrawable) getResources()
						.getDrawable(R.drawable.axeac_scan_laser)).getBitmap(), null,
				lineRect, paint);

	}

	// describe:draw the cover layer
	/**
	 * 绘制遮掩层
	 * @param canvas
	 * Canvas对象
	 * @param frame
	 * Rect对象
	 */
	private void drawCover(Canvas canvas, Rect frame) {

		// get the width and height of the screen
		// 获取屏幕的宽和高
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		// 绘制外部（即框架直角外侧）变暗
		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		/*
           Draw the shadow part of the outside of the frame, a total of four parts,
           scan the top of the screen to the top of the screen, below the scan box
           to the bottom of the screen, scan the left side of the screen to the left
           of the screen, right side of the frame to the right of the screen

		   画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
		   扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
		 */
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);
	}

	// describe:Depict the four corners of the square
	/**
	 * 描绘方形的四个角
	 * @param canvas
	 * Canvas对象
	 * @param frame
	 * Rect对象
	 */
	private void drawRectEdges(Canvas canvas, Rect frame) {

		paint.setColor(Color.WHITE);
		paint.setAlpha(OPAQUE);

		Resources resources = getResources();
		/**
		 * describe:These resources can be managed with the cache, do not need to refresh each new
		 * 描述：这些资源可以用缓存进行管理，不需要每次刷新都新建
		 */
		Bitmap bitmapCornerTopleft = BitmapFactory.decodeResource(resources,
				R.drawable.axeac_scan_corner_top_left);
		Bitmap bitmapCornerTopright = BitmapFactory.decodeResource(resources,
				R.drawable.axeac_scan_corner_top_right);
		Bitmap bitmapCornerBottomLeft = BitmapFactory.decodeResource(resources,
				R.drawable.axeac_scan_corner_bottom_left);
		Bitmap bitmapCornerBottomRight = BitmapFactory.decodeResource(
				resources, R.drawable.axeac_scan_corner_bottom_right);

		canvas.drawBitmap(bitmapCornerTopleft, frame.left + CORNER_PADDING,
				frame.top + CORNER_PADDING, paint);
		canvas.drawBitmap(bitmapCornerTopright, frame.right - CORNER_PADDING
						- bitmapCornerTopright.getWidth(), frame.top + CORNER_PADDING,
				paint);
		canvas.drawBitmap(bitmapCornerBottomLeft, frame.left + CORNER_PADDING,
				2 + (frame.bottom - CORNER_PADDING - bitmapCornerBottomLeft
						.getHeight()), paint);
		canvas.drawBitmap(bitmapCornerBottomRight, frame.right - CORNER_PADDING
				- bitmapCornerBottomRight.getWidth(), 2 + (frame.bottom
				- CORNER_PADDING - bitmapCornerBottomRight.getHeight()), paint);

		bitmapCornerTopleft.recycle();
		bitmapCornerTopleft = null;
		bitmapCornerTopright.recycle();
		bitmapCornerTopright = null;
		bitmapCornerBottomLeft.recycle();
		bitmapCornerBottomLeft = null;
		bitmapCornerBottomRight.recycle();
		bitmapCornerBottomRight = null;

	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	// describe:Draw a bitmap with the result points highlighted instead of the live
	//          scanning display.
	/**
	 * 绘制一个位图，结果点突出显示，而不是实时扫描显示。
	 * @param barcode
	 * An image of the decoded barcode.
	 * <br>解码条形码之后的图像
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoints;
		synchronized (points) {
			points.add(point);
			int size = points.size();
			if (size > MAX_RESULT_POINTS) {
				// trim it
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}

	// describe:dp to px
	/**
	 * dp转px
	 * @param context
	 * Context对象
	 * @param dipValue
	 * @return
	 */
	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

}
