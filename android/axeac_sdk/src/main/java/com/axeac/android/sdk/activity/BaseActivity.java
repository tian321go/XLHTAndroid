package com.axeac.android.sdk.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.application.BaseApplication;
import com.axeac.android.sdk.customview.MToolbar;
import com.axeac.android.sdk.utils.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends AppCompatActivity {

    /**
     * 对话框文本
     * */
    private String mProgressMessage;
    private View initView;
    // 启动页面显示时间
    private long mDuration = 1000 * 2;
    // 启动页面显示间隔时间, 间隔内不出现启动页面
    private long mInterval = 1000 * 20;
    // 上次显示启动页面的时间
    private long mLastTime = System.currentTimeMillis();
    private CountDownTimer mTimer;
    /**
     * 对话框对象
     * */
    public ProgressDialog mProgressDialog;
    public static BaseActivity act;

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        // 记录退到后台的时间, 以便后续比对
        mLastTime = System.currentTimeMillis();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 判断是否从后台恢复, 且时间间隔符合要求, 显示启动页面
        boolean isFromBackToFront = BaseApplication.sAppState == BaseApplication.STATE_BACK_TO_FRONT;
        if (isFromBackToFront&&canShowView()) {
            showView();
        }
    }

    /**
     * 显示启动页面
     */
    private void showView() {
        // 显示启动页面
        createView();
        mTimer.cancel();
        mTimer.start();

    }

    /**
     * 判断两次时间是否大于规定的间隔
     *
     * @return true大于间隔, 否则false
     */
    private boolean canShowView() {
        return System.currentTimeMillis() - mLastTime > mInterval;
    }

    private void createView() {
        setTransStatus(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.x = 0;
        params.y = 0;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindowManager().addView(initView, params);
    }

    private void init(){
        initView = View.inflate(this,R.layout.axeac_startup,null);
        mTimer = new CountDownTimer(mDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getWindowManager().removeViewImmediate(initView);
                        }
                    }, 100);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        mProgressMessage = getString(R.string.axeac_login_loading);
        Log.d("ClassName", getClass().getName());
        init();
    }

    // describe:display loading dialog
    /**
     * 显示加载框
     * @param message
     * 加载框文字
     */
    public void showProgressDialog(String message) {
        if (!TextUtils.isEmpty(message)) {
            mProgressMessage = message;
        }
        if (!isFinishing())
            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    setProgressDialog();
                    mProgressDialog.show();
                }
            });
    }

    // describe:create loading dialog
    /**
     * 创建加载框
     * */
    public void setProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(mProgressMessage);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
    }

    // describe:show loading dialog
    /**
     * 显示进度框.
     */
    public void showProgressDialog() {
        if (!isFinishing())
            showProgressDialog(null);
    }

    // describe:remove loading dialog
    /**
     * 移除进度框.
     */
    public void removeProgressDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    // describe:show Toast hint
    /**
     * toast提示
     * @param resId
     * 资源id
     */
    public void showToast(int resId) {
        showToast(this.getResources().getText(resId).toString());
    }

    // describe:show Toast hint
    /**
     * toast提示
     * @param text
     * 展示文字
     */
    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, "" + text, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }


    protected MToolbar toolbar;
    protected TextView toolbarTitle;
    public static RelativeLayout backPage;
    public static RelativeLayout moreBtn;

    // describe:initialize Toolbar
    /**
     * 初始化toolbar
     */
    protected void initToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (MToolbar) v;
            setSupportActionBar(toolbar);
            toolbarTitle = (TextView) v.findViewById(R.id.layout_title);
            backPage = (RelativeLayout) v.findViewById(R.id.layout_button);
            moreBtn = (RelativeLayout) v.findViewById(R.id.layout_morebutton);
            setTitle("");
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    // describe:set title when title changes
    /**
     * 标题改变时，设置标题
     * @param title
     * 标题文本
     * @param color
     * 文本颜色值
     * */
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar();
    }

    // describe:Immersive status bar
    /**
     * 沉浸式状态栏
     * @param activity
     * Activity对象
     * @param color
     * 颜色值
     */
    public static void setStatusBarTint(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);
    }

    // describe:Set translucent
    /**
     * 设置半透明
     * @param activity
     * Activity对象
     * @param on
     * */
    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    // describe:Set translucent
    /**
     * 设置透明
     * @param activity
     * Activity对象
     * */
    public void setTransStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.background));
        }
    }

    // describe:jump activity
    /**
     * activity跳转
     * @param cls
     * 跳转对象
     * */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
    }

    // describe:hide edit menu
    /**
     * 隐藏编辑菜单
     * */
    protected void hiddenEditMenu() {
        if (toolbar != null)
            if (null != toolbar.getMenu()) {
                for (int i = 0; i < toolbar.getMenu().size(); i++) {
                    toolbar.getMenu().getItem(i).setVisible(false);
                    toolbar.getMenu().getItem(i).setEnabled(false);
                }
            }
    }

    // describe:show edit menu
    /**
     * 显示编辑菜单
     * */
    protected void showEditMenu() {
        if (toolbar != null)
            if (null != toolbar.getMenu()) {
                for (int i = 0; i < toolbar.getMenu().size(); i++) {
                    toolbar.getMenu().getItem(i).setVisible(true);
                    toolbar.getMenu().getItem(i).setEnabled(true);
                }
            }
    }

    // describe:show dialog
    /**
     * 显示对话框
     * @param title
     * 标题文本
     * @param msg
     * 信息文本
     * @param mOkOnClickListener
     * 确定按钮监听事件
     * */
    public void showDialog(String title, String msg,
                           DialogInterface.OnClickListener mOkOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.axeac_msg_confirm, mOkOnClickListener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    // describe:show dialog
    /**
     * 显示对话框
     * @param title
     * 标题文本
     * @param msg
     * 信息文本
     * @param mOkOnClickListener
     * 确定按钮监听事件
     * @param mCancelOnClickListener
     * 取消按钮监听事件
     * */
    public void showDialog(String title, String msg,
                           DialogInterface.OnClickListener mOkOnClickListener,DialogInterface.OnClickListener mCancelOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.axeac_msg_confirm, mOkOnClickListener);
        builder.setNegativeButton(R.string.axeac_msg_cancel, mCancelOnClickListener);
        builder.create().show();
    }

    // describe:show dialog
    /**
     * 显示对话框
     * @param title
     * 标题文本
     * @param msg
     * 消息文本
     * @param mOkOnClickListener
     * 确定按钮监听事件
     * @param mCancelOnClickListener
     * 取消按钮监听事件
     * */
    public void showDialog(int title, int msg,
                           DialogInterface.OnClickListener mOkOnClickListener,DialogInterface.OnClickListener mCancelOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.axeac_msg_confirm, mOkOnClickListener);
        builder.setNegativeButton(R.string.axeac_msg_cancel, mCancelOnClickListener);
        builder.create().show();
    }

}
