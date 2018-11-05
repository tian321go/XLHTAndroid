package com.axeac.android.sdk.ui.refreshview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.axeac.android.sdk.R;

public class LoadingLayout extends FrameLayout {

	/**
	 * 旋转动画默认持续时间
	 * <br>默认值：150
	 * */
	private static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private final ImageView mRefreshImage;
	private final ProgressBar mRefreshProgress;
	private final TextView mRefreshText;
	/**
	 * 下拉文字
	 * */
	private String mPullLabel;
	/**
	 * 正在刷新文字
	 * */
	private String mRefreshingLabel;
	/**
	 * 松开后显示的文字
	 * */
	private String mReleaseLabel;
	/**
	 * 旋转动画
	 * */
	private final Animation mRotateAnimation;
	/**
	 * 反向旋转动画
	 * */
	private final Animation mResetRotateAnimation;

	public LoadingLayout(Context context, final int mode, String releaseLabel, String pullLabel, String refreshingLabel) {
		super(context);
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.axeac_htmllist_refresh, this);
		mRefreshText = (TextView) header.findViewById(R.id.refresh_text);
		mRefreshImage = (ImageView) header.findViewById(R.id.refresh_image);
		mRefreshProgress = (ProgressBar) header.findViewById(R.id.refresh_progress);

		final Interpolator interpolator = new LinearInterpolator();
		mRotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setFillAfter(true);

		mResetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mResetRotateAnimation.setInterpolator(interpolator);
		mResetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mResetRotateAnimation.setFillAfter(true);

		mReleaseLabel = releaseLabel;
		mPullLabel = pullLabel;
		mRefreshingLabel = refreshingLabel;
		
		switch (mode) {
		case RefreshBase.MODE_PULL_UP_TO_REFRESH:
            mRefreshImage.setImageResource(R.drawable.axeac_refresh_up_arrow);
			break;
		case RefreshBase.MODE_PULL_DOWN_TO_REFRESH:
        default:
            mRefreshImage.setImageResource(R.drawable.axeac_refresh_down_arrow);
			break;
		}
	}

	/**
	 * 重新刷新
	 * */
	public void reset() {
        mRefreshText.setText(mPullLabel);
		mRefreshImage.setVisibility(View.VISIBLE);
		mRefreshProgress.setVisibility(View.GONE);
	}

	/**
	 * 松开刷新
	 * */
	public void releaseToRefresh() {
        mRefreshText.setText(mReleaseLabel);
        mRefreshImage.clearAnimation();
		mRefreshImage.startAnimation(mRotateAnimation);
	}

	/**
	 * 设置下拉文字
	 * @param pullLabel
	 * 下拉文字
	 * */
	public void setPullLabel(String pullLabel) {
		mPullLabel = pullLabel;
	}

	/**
	 * 正在刷新
	 * */
	public void refreshing() {
        mRefreshText.setText(mRefreshingLabel);
		mRefreshImage.clearAnimation();
		mRefreshImage.setVisibility(View.INVISIBLE);
		mRefreshProgress.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置正在刷新文字
	 * @param refreshingLabel
	 * 正在刷新文字
	 * */
	public void setRefreshingLabel(String refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	/**
	 * 设置松开刷新文字
	 * @param releaseLabel
	 * 松开后显示的文字
	 * */
	public void setReleaseLabel(String releaseLabel) {
	    mReleaseLabel = releaseLabel;
    }

	/**
	 * 下拉刷新
	 * */
    public void pullToRefresh() {
		mRefreshText.setText(mPullLabel);
		mRefreshImage.clearAnimation();
		mRefreshImage.startAnimation(mResetRotateAnimation);
    }

	/**
	 * 设置文字颜色值
	 * @param color
	 * 文字颜色值
	 * */
    public void setTextColor(int color) {
		mRefreshText.setTextColor(color);
	}
}