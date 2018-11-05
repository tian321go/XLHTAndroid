package com.axeac.android.sdk.customview;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.axeac.android.sdk.R;

/**
 * 自定义加载视图
 * */
public class LoadingViewLayout {

    private Context mContext;
    private LinearLayout mBackgroundViews;
    private ProgressBar pb_loading;
    private TextView tv_error;
    private ImageView img_empty;

    private ViewGroup mLoadingView;
    private View mContentView;
    private LayoutInflater mInflater;
    private boolean mViewsAdded;
    private OnClickListener mErrorButtonClickListener;

    // ---------------------------
    // static variables
    // 静态变量
    // ---------------------------

    // describe:The empty state
    /**
     * 空状态
     */
    public final static int TYPE_EMPTY = 1;
    // describe:The loading state
    /**
     * 加载状态
     */
    public final static int TYPE_LOADING = 2;
    // describe:The error state
    /**
     * 错误状态
     */
    public final static int TYPE_ERROR = 3;
    // describe:The content view state
    /**
     * 内容视图状态
     */
    public final static int TYPE_CONTENT_VIEW_STATE = 4;

    public final static int TYPE_EMPTY_ONLY_TEXT = 5;

    public final static int TYPE_ERROR_NO_IMAGE = 6;


    private int mEmptyType = TYPE_LOADING;

    // ---------------------------
    // getters and setters
    // getters和setters方法
    // ---------------------------

    // describe:Gets the loading layout
    /**
     * 获取加载布局
     * @return
     * 加载布局
     */
    public ViewGroup getLoadingView() {
        return mLoadingView;
    }

    // describe:Sets loading layout
    /**
     * 设置加载布局
     * @param loadingView
     * the layout to be shown when the content view is loading
     * 当内容视图加载时显示加载布局
     */
    public void setLoadingView(ViewGroup loadingView) {
        this.mLoadingView = loadingView;
    }

    // describe:Sets loading layout resource
    /**
     * 设置加载布局资源
     * @param res
     * the resource of the layout to be shown when the content view is loading
     * 当内容视图加载时显示布局资源
     */
    public void setLoadingViewRes(int res) {
        this.mLoadingView = (ViewGroup) mInflater.inflate(res, null);
    }

    // describe:Gets the content view for which this library is being used
    /**
     * 获取正在使用该库的内容视图
     * @return
     * 内容视图
     */
    public View getMainView() {
        return mContentView;
    }

    // describe:Sets the list view for which this library is being used
    /**
     * 设置正在使用该库的内容视图
     * @param contentView
     * 内容视图
     */
    public void setViewGroup(View contentView) {
        this.mContentView = contentView;
    }

    // describe:Sets the OnClickListener to EmptyView
    /**
     * 设置空视图的点击事件
     * @param emptyButtonClickListener
     */
    public void setErrorButtonClickListener(
            OnClickListener emptyButtonClickListener) {
        this.mErrorButtonClickListener = emptyButtonClickListener;
    }


    private void changeEmptyType() {
        changeEmptyType(mContext.getResources().getString(R.string.axeac_loadingView_empty));
    }

    private void changeEmptyType(int res) {
        changeEmptyType(mContext.getResources().getString(res));
    }

    private void changeEmptyType(String tip) {

        if (!mViewsAdded) {
            mBackgroundViews = new LinearLayout(mContext);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mBackgroundViews.setGravity(Gravity.CENTER);
            mBackgroundViews.setLayoutParams(lp);
            mBackgroundViews.setOrientation(LinearLayout.VERTICAL);
            mBackgroundViews.removeAllViews();
            if (mLoadingView != null) {
                mBackgroundViews.addView(mLoadingView,lp);
            }
            mViewsAdded = true;
            ((ViewGroup) mContentView.getParent()).addView(mBackgroundViews);
        }

        if (mContentView != null) {
            switch (mEmptyType) {
                case TYPE_EMPTY_ONLY_TEXT:
                case TYPE_EMPTY:
                    mBackgroundViews.setVisibility(View.VISIBLE);
                    if (mLoadingView != null) {
                        pb_loading.setVisibility(View.GONE);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(tip);
                        img_empty.setVisibility(mEmptyType == TYPE_EMPTY ? View.VISIBLE : View.GONE);
                    }
                    if (mContentView != null) {
                        mContentView.setVisibility(View.GONE);
                        mContentView.setEnabled(false);
                    }
                    break;
                case TYPE_ERROR:
                    mBackgroundViews.setVisibility(View.VISIBLE);
                    mBackgroundViews.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (mErrorButtonClickListener != null) {
                                mErrorButtonClickListener.onClick(v);
                            }
                        }
                    });
                    if (mLoadingView != null) {
                        pb_loading.setVisibility(View.GONE);
                        img_empty.setVisibility(mEmptyType == TYPE_ERROR_NO_IMAGE ? View.GONE : View.VISIBLE);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(tip);
                    }
                    if (mContentView != null) {
                        mContentView.setVisibility(View.GONE);
                        mContentView.setEnabled(false);
                    }
                    break;
                case TYPE_LOADING:
                    mBackgroundViews.setVisibility(View.VISIBLE);
                    mBackgroundViews.setOnClickListener(null);
                    if (mLoadingView != null) {
                        tv_error.setVisibility(View.GONE);
                        img_empty.setVisibility(View.GONE);
                        pb_loading.setVisibility(View.VISIBLE);
                    }
                    if (mContentView != null) {
                        mContentView.setVisibility(View.GONE);
                        mContentView.setEnabled(false);
                    }
                    break;
                case TYPE_CONTENT_VIEW_STATE:
                    if (mContentView != null) {
                        if (mContentView.getVisibility() == View.VISIBLE) {
                            return;
                        }
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mContentView != null) {
                                mBackgroundViews.setVisibility(View.GONE);
                                mContentView.setVisibility(View.VISIBLE);
                                mContentView.setEnabled(true);
                            }
                        }
                    }, 500);
                    break;
                default:
                    break;
            }
        }
    }

    // describe:Set default values when the mLoadingView is null
    /**
     * 当mLoadingView为空时，设置默认值
     * */
    private void setDefaultValues() {
        if (mLoadingView == null) {
            mLoadingView = (ViewGroup) mInflater.inflate(
                    R.layout.axeac_layout_loading_view, null);
            pb_loading = (ProgressBar) mLoadingView
                    .findViewById(R.id.pb_loading);
            tv_error = (TextView) mLoadingView.findViewById(R.id.tv_error);
            img_empty = (ImageView) mLoadingView.findViewById(R.id.img_empty);

        }

    }


    // ---------------------------
    // public methods
    // public 方法
    // ---------------------------

    /**
     * 加载视图布局
     * @param context
     * the context (preferred context is any activity)
     * 首选的context是activity
     *
     * @param contentView
     * the contentView for which this library is being used, this
     * view can't be the root view of the hierarchy, It has to be a
     * view (Any LinearLayout, RelativeLayout, View, etc) inside the
     * root view
     * 正在使用的contentView，不能是根视图，它必须是根视图内的视图（AnyLineLayout，RelativeLayout，View等）
     */
    public LoadingViewLayout(Context context, View contentView) {
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = contentView;
        setDefaultValues();

    }

    // describe:Default empty interface, default images and text
    /**
     * 默认空界面，默认的图片和文字
     */
    public void showEmpty() {
        this.mEmptyType = TYPE_EMPTY;
        changeEmptyType();
    }

    // describe:Default text empty interface, no picture
    /**
     * 默认文字空界面，无图
     */
    public void showEmptyNoImage() {
        this.mEmptyType = TYPE_EMPTY_ONLY_TEXT;
        changeEmptyType();
    }

    // describe:Custom text of the empty interface, no picture
    /**
     * 自定义文字的空界面，无图
     *
     * @param res
     * 资源id
     */
    public void showEmptyNoImage(int res) {
        this.mEmptyType = TYPE_EMPTY_ONLY_TEXT;
        changeEmptyType(res);
    }

    public void showEmptyNoImage(String res) {
        this.mEmptyType = TYPE_EMPTY_ONLY_TEXT;
        changeEmptyType(res);
    }

    // describe:Custom picture, no text empty interface
    /**
     * 自定义的图片，无文字空界面
     * @param res
     */
    public void showEmptyWithImageNoText(int res) {
        this.mEmptyType = TYPE_EMPTY;
        img_empty.setImageResource(res);
        changeEmptyType(null);
    }

    public void showEmpty(int tip) {
        this.mEmptyType = TYPE_EMPTY;
        changeEmptyType(tip);
    }

    // describe:Shows loading layout when a long task is doing
    /**
     * 有加载任务的时候显示Loading
     */
    public void showLoading() {
        this.mEmptyType = TYPE_LOADING;
        changeEmptyType();
    }

    // describe:Shows error layout when is there an error
    /**
     * 出现错误时显示
     */
    public void showError() {
        this.mEmptyType = TYPE_ERROR;
        changeEmptyType(R.string.axeac_loadingView_error_msg);
    }

    // describe:Shows error layout when is there an error
    /**
     * 出现错误时显示
     */
    public void showError(int res) {
        this.mEmptyType = TYPE_ERROR;
        changeEmptyType(res);
    }

    // describe:Shows error layout when is there an error
    /**
     * 出现错误时显示
     */
    public void showErrorNoImage() {
        this.mEmptyType = TYPE_ERROR_NO_IMAGE;
        changeEmptyType(R.string.axeac_loadingView_error_msg);
    }

    // describe:Shows the content view and hides the others overlays
    /**
     * 显示contentView，隐藏其他叠加层
     */
    public void showContentView() {
        this.mEmptyType = TYPE_CONTENT_VIEW_STATE;
        changeEmptyType();
    }

}
