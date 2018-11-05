package com.axeac.android.sdk.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * describe:Custom view as the screen painter
 * <br>自定义view，作为屏幕画板
 * @author axeac
 * @version 1.0.0
 */
@SuppressLint("ClickableViewAccessibility")
public class PaintingView extends View {
    private  static final String TAG=PaintingView.class.getSimpleName();
    private Context mContext;
    private float mX;
    private float mY;
    private final Paint mGesturePaint = new Paint();
    private final Path mPath = new Path();
    private Canvas cacheCanvas;
    private Bitmap cachebBitmap;
    /**
     * 是否触摸了屏幕
     * <br>默认值false
     * */
    private boolean isTouched = false;
    /**
     * 画笔宽度
     * <br>默认值：10
     * */
    private int mPaintWidth = 10;
    /**
     * 笔的颜色值
     * <br>默认值：BLACK
     * */
    private int mPenColor = Color.BLACK;
    /**
     * 画板颜色值
     * <br>默认值：WHITE
     * */
    private int mBackColor= Color.WHITE;
    public PaintingView(Context context) {
        super(context);
        init(context);
    }

    public PaintingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaintingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Style.STROKE);
        mGesturePaint.setStrokeWidth(mPaintWidth);
        mGesturePaint.setColor(mPenColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cachebBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        cacheCanvas = new Canvas(cachebBitmap);
        cacheCanvas.drawColor(mBackColor);
        isTouched=false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(mPath, mGesturePaint);
                mPath.reset();
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(cachebBitmap, 0, 0, mGesturePaint);
        // draw a figure with multiple points through the canvas
        // 通过画布绘制多点形成的图形
        canvas.drawPath(mPath, mGesturePaint);
    }

    // Called when the finger points down the screen
    // 手指点下屏幕时调用
    private void touchDown(MotionEvent event) {
        // Resets the plotted line, that is, the path drawn before hiding
        // 重置绘制路线，即在隐藏之前绘制的轨迹
        mPath.reset();
        float x = event.getX();
        float y = event.getY();
        mX = x;
        mY = y;
        // mPath move to the drawing starting point of the drawing
        // mPath移动到绘制的绘制起点
        mPath.moveTo(x, y);
    }

    // Called when the finger slide on the screen
    /**
     * 手指在屏幕上滑动时调用
     * */
    private void touchMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final float previousX = mX;
        final float previousY = mY;
        //Distance between two points
        //两点间距离
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        if (dx >= 3 || dy >= 3) {
            // Set the Bessel curve's operating point as the starting point and half of the finish point
            // 设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;
            mPath.quadTo(previousX, previousY, cX, cY);
            mX = x;
            mY = y;
        }
    }

    // clear drawing board
    /**
     * 清除画板
     * */
    public void clear() {
        if (cacheCanvas != null) {
            isTouched = false;
            mGesturePaint.setColor(mPenColor);
            cacheCanvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR);
            mGesturePaint.setColor(mPenColor);
            invalidate();
        }
    }

    public void save(String dir,String path) throws IOException {
        save(dir,path, false, 0);
    }

    /**
     * describe:Save your picture to local files
     * <br>将手绘出的图片保存到本地文件
     * @param dir
     * 本地文件夹路径
     * @param path
     * 文件路径
     * @param clearBlank
     * 是否清除边界空白
     * @param blank
     * 边界值
     * */
    public void save(String dir,String path, boolean clearBlank, int blank) throws IOException {
        Bitmap bitmap=cachebBitmap;
        File destDir = new File(dir);
        if (!destDir.exists()){
            destDir.mkdirs();
        }
        if (clearBlank) {
            bitmap = clearBlank(bitmap, blank);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }

            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
            outputStream.close();
        }
    }

    // Progressive scanning,clear boundary blank
    /**
     * 逐行扫描 清除边界空白
     * @param bp
     * Bitmap对象
     * @param blank
     * 边界值
     * */
    private Bitmap clearBlank(Bitmap bp, int blank) {
        int HEIGHT = bp.getHeight();
        int WIDTH = bp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        for (int y = 0; y < HEIGHT; y++) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int x = WIDTH - 1; x > 0; x--) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
        bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top);
    }

    public boolean getTouched() {
        return isTouched;
    }
}
