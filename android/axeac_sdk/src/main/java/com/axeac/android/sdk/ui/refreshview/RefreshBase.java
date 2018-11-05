package com.axeac.android.sdk.ui.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.axeac.android.sdk.R;

public abstract class RefreshBase<T extends View> extends LinearLayout {

	final class SmoothScrollRunnable implements Runnable {

		private static final int ANIMATION_DURATION_MS = 190;
		private static final int ANIMATION_FPS = 1000 / 60;

		private final Interpolator mInterpolator;
		private final int mScrollToY;
		private final int mScrollFromY;
		private final Handler mHandler;

		private boolean mContinueRunning = true;
		private long mStartTime = -1;
		private int mCurrentY = -1;

		public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
			mHandler = handler;
			mScrollFromY = fromY;
			mScrollToY = toY;
			mInterpolator = new AccelerateDecelerateInterpolator();
		}

		@Override
		public void run() {
			/**
			 * describe:Only set mStartTime if this is the first time we're starting, else actually calculate the Y delta
			 * 描述：如果是第一次启动，仅设置mStartTime。否则计算Y增量
			 */
			if (mStartTime == -1) {
				mStartTime = System.currentTimeMillis();
			} else {
				/**
				 * describe:We do all calculations in long to reduce software float calculations.
				 * 			We use 1000 as it gives us good accuracy and small rounding errors.
				 *
				 * 描述：我们用long来做所有的计算，减少float计算，并使用1000，可以更准确、误差小
				 */
				long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / ANIMATION_DURATION_MS;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math.round((mScrollFromY - mScrollToY) * mInterpolator.getInterpolation(normalizedTime / 1000f));
				mCurrentY = mScrollFromY - deltaY;
				setHeaderScroll(mCurrentY);
			}
			// If we're not at the target Y, keep going...
			// 根据条件判断，是否继续
			if (mContinueRunning && mScrollToY != mCurrentY) {
				mHandler.postDelayed(this, ANIMATION_FPS);
			}
		}

		public void stop() {
			mContinueRunning = false;
			mHandler.removeCallbacks(this);
		}
	}

	// ===========================================================
	// Constants
	// 常量
	// ===========================================================

	private static final float FRICTION = 2.0f;

	private static final int PULL_TO_REFRESH = 0x0;
	private static final int RELEASE_TO_REFRESH = 0x1;
	private static final int RELEASE_TO_RELOAD = 0x2;
	private static final int REFRESHING = 0x3;
	private static final int MANUAL_REFRESHING = 0x4;

	public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
	public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
	public static final int MODE_BOTH = 0x3;

	// ===========================================================
	// Fields
	// 字段
	// ===========================================================

	private int mTouchSlop;

	private float mInitialMotionY;
	private float mLastMotionX;
	private float mLastMotionY;
	private boolean mIsBeingDragged = false;

	private int mState = PULL_TO_REFRESH;
	private int mMode = MODE_PULL_DOWN_TO_REFRESH;
	private int mCurrentMode;

	private boolean mDisableScrollingWhileRefreshing = true;

	public T mRefreshableView;
	private boolean mIsPullToRefreshEnabled = true;

	private LoadingLayout mHeaderLayout;
	private LoadingLayout mFooterLayout;
	private int mHeaderHeight;

	private final Handler mHandler = new Handler();

	private OnRefreshListener mOnRefreshListener;

	private SmoothScrollRunnable mCurrentSmoothScrollRunnable;

	private boolean autoLoad = true;

	public void setAutoLoad(boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	// ===========================================================
	// Constructors
	// 构造方法
	// ===========================================================

	public RefreshBase(Context ctx, String style) {
		super(ctx);
		init(ctx, style, null);
	}

	public RefreshBase(Context ctx, String style, int mode) {
		super(ctx);
		mMode = mode;
		init(ctx, style, null);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * describe:Get the Wrapped Refreshable View. Anything returned here has already been
	 * 			added to the content view.
	 *
	 * 描述：获取可刷新视图。 此处返回的任何内容已被添加到内容视图中。
	 *
	 * @return The View which is currently wrapped
	 */
	public final T getRefreshableView() {
		return mRefreshableView;
	}

	/**
	 * describe:Whether Pull-to-Refresh is enabled
	 * 描述：是否可以刷新
	 * @return enabled
	 */
	public final boolean isPullToRefreshEnabled() {
		return mIsPullToRefreshEnabled;
	}

	/**
	 * describe:Returns whether the widget has disabled scrolling on the Refreshable View
	 * 			while refreshing.
	 *
	 * 描述：返回在刷新时，小部件是否已禁用在可刷新视图上滚动
	 *
	 * @return true
	 * if the widget has disabled scrolling while refreshing
	 * 刷新时禁止滚动
	 */
	public final boolean isDisableScrollingWhileRefreshing() {
		return mDisableScrollingWhileRefreshing;
	}

	/**
	 * describe:Returns whether the Widget is currently in the Refreshing mState
	 * 描述：返回当前的Widget是否在刷新的mState中
	 * @return true if the Widget is currently refreshing
	 */
	public final boolean isRefreshing() {
		return mState == REFRESHING || mState == MANUAL_REFRESHING;
	}

	/**
	 * describe:By default the Widget disabled scrolling on the Refreshable View while
	 * 			refreshing. This method can change this behaviour.
	 *
	 * 描述：默认情况下，Widget在刷新时禁用了可刷新视图上的滚动。 这个方法可以设置它。
	 *
	 * @param disableScrollingWhileRefreshing
	 *            - true if you want to disable scrolling while refreshing
	 *            -如果要在刷新时禁用滚动传参数true
	 */
	public final void setDisableScrollingWhileRefreshing(boolean disableScrollingWhileRefreshing) {
		mDisableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
	}

	/**
	 * describe:Mark the current Refresh as complete. Will Reset the UI and hide the Refreshing View
	 *
	 * 描述：将当前刷新标记为完成。 将重置UI并隐藏刷新视图
	 */
	public final void onRefreshComplete() {
		if (mState != PULL_TO_REFRESH) {
			resetHeader();
		}
	}

	/**
	 * describe:Set OnRefreshListener for the Widget
	 *
	 * 描述：设置Widget的OnRefreshListener
	 *
	 * @param listener
	 * Listener to be used when the Widget is set to Refresh
	 * 当Widget设置为刷新时使用的listener
	 *
	 */
	public final void setOnRefreshListener(OnRefreshListener listener) {
		mOnRefreshListener = listener;
	}

	/**
	 * describe:A mutator to enable/disable Pull-to-Refresh for the current View
	 *
	 * 描述：是否启用当前视图的刷新功能
	 *
	 * @param enable Whether Pull-To-Refresh should be used
	 */
	public final void setPullToRefreshEnabled(boolean enable) {
		mIsPullToRefreshEnabled = enable;
	}

	/**
	 * describe:Set Text to show when the Widget is being pulled, and will refresh when released
	 *
	 * 描述：设置Widget被拖动时显示的文本，并在释放时刷新
	 *
	 * @param releaseLabel
	 * - String to display
	 * 展示的文字
	 */
	public void setReleaseLabel(String releaseLabel) {
		if (null != mHeaderLayout) {
			mHeaderLayout.setReleaseLabel(releaseLabel);
		}
		if (null != mFooterLayout) {
			mFooterLayout.setReleaseLabel(releaseLabel);
		}
	}

	/**
	 * describe:Set Text to show when the Widget is being Pulled
	 *
	 * 描述：设置当Widget被拉动时显示的文本
	 *
	 * @param pullLabel
	 * - String to display
	 * 展示的文本
	 */
	public void setPullLabel(String pullLabel) {
		if (null != mHeaderLayout) {
			mHeaderLayout.setPullLabel(pullLabel);
		}
		if (null != mFooterLayout) {
			mFooterLayout.setPullLabel(pullLabel);
		}
	}

	/**
	 * describe:Set Text to show when the Widget is refreshing
	 *
	 * 描述：设置当Widget刷新时显示的文字
	 *
	 * @param refreshingLabel
	 * - String to display
	 * 展示的文字
	 */
	public void setRefreshingLabel(String refreshingLabel) {
		if (null != mHeaderLayout) {
			mHeaderLayout.setRefreshingLabel(refreshingLabel);
		}
		if (null != mFooterLayout) {
			mFooterLayout.setRefreshingLabel(refreshingLabel);
		}
	}

	public final void setRefreshing() {
		setRefreshing(true);
	}

	/**
	 * describe:Sets the Widget to be in the refresh mState. The UI will be updated to
	 * 			show the 'Refreshing' view.
	 *
	 * 描述：将Widget设置为刷新mState。 显示“刷新”视图更新UI。
	 *
	 * @param doScroll
	 * - true if you want to force a scroll to the Refreshing view.
	 * - 如果要强制滚动到刷新视图，则为true
	 */
	public final void setRefreshing(boolean doScroll) {
		if (!isRefreshing()) {
			setRefreshingInternal(doScroll);
			mState = MANUAL_REFRESHING;
		}
	}

	public final boolean hasPullFromTop() {
		return mCurrentMode != MODE_PULL_UP_TO_REFRESH;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		if (!mIsPullToRefreshEnabled) {
			return false;
		}
		if (isRefreshing() && mDisableScrollingWhileRefreshing) {
			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
			return false;
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE: {
				if (mIsBeingDragged) {
					mLastMotionY = event.getY();
					pullEvent();
					return true;
				}
				break;
			}
			case MotionEvent.ACTION_DOWN: {
				if (isReadyForPull()) {
					mLastMotionY = mInitialMotionY = event.getY();
					return true;
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: {
				if (mIsBeingDragged) {
					mIsBeingDragged = false;
					if (mState == RELEASE_TO_REFRESH && null != mOnRefreshListener) {
						setRefreshingInternal(true);
						mOnRefreshListener.onRefresh();
					} else if (mState == RELEASE_TO_RELOAD && null != mOnRefreshListener) {
						setRefreshingInternal(true);
						mOnRefreshListener.onLoad();
					} else {
						smoothScrollTo(0);
					}
					return true;
				}
				break;
			}
		}
		return false;
	}

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event) {
		if (!autoLoad) {
			return false;
		}
		if (!mIsPullToRefreshEnabled) {
			return false;
		}
		if (isRefreshing() && mDisableScrollingWhileRefreshing) {
			return true;
		}
		final int action = event.getAction();
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			mIsBeingDragged = false;
			return false;
		}
		if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
			return true;
		}
		switch (action) {
			case MotionEvent.ACTION_MOVE:
				if (isReadyForPull()) {
					final float y = event.getY();
					final float dy = y - mLastMotionY;
					final float yDiff = Math.abs(dy);
					final float xDiff = Math.abs(event.getX() - mLastMotionX);
					if (yDiff > mTouchSlop && yDiff > xDiff) {
						if ((mMode == MODE_PULL_DOWN_TO_REFRESH ||
								mMode == MODE_BOTH) && dy >= 0.0001f && isReadyForPullDown()) {
							mLastMotionY = y;
							mIsBeingDragged = true;
							if (mMode == MODE_BOTH) {
								mCurrentMode = MODE_PULL_DOWN_TO_REFRESH;
							}
						} else if ((mMode == MODE_PULL_UP_TO_REFRESH ||
								mMode == MODE_BOTH) && dy <= 0.0001f && isReadyForPullUp()) {
							mLastMotionY = y;
							mIsBeingDragged = true;
							if (mMode == MODE_BOTH) {
								mCurrentMode = MODE_PULL_UP_TO_REFRESH;
							}
						}
					}
				}
				break;
			case MotionEvent.ACTION_DOWN:
				if (isReadyForPull()) {
					mLastMotionY = mInitialMotionY = event.getY();
					mLastMotionX = event.getX();
					mIsBeingDragged = false;
				}
				break;
		}
		return mIsBeingDragged;
	}

	/**
	 * describe:This is implemented by derived classes to return the created View. If you
	 * 			need to use a custom View (such as a custom ListView), override this
	 * 			method and return an instance of your custom class.
	 * 			Be sure to set the ID of the view in this method, especially if you're
	 * 			using a ListActivity or ListFragment.
	 *
	 * 描述：这是通过派生类来实现的，以返回创建的View。 如果您需要使用自定义View（例如自定义ListView），
	 *       请覆盖此方法并返回自定义类的实例。
	 *       确保使用此方法设置视图的ID，尤其是在使用ListActivity或ListFragment时。
	 *
	 * @param context Context to create view with
	 * @return New instance of the Refreshable View
	 */
	protected abstract T createRefreshableView(Context context);

	protected final int getCurrentMode() {
		return mCurrentMode;
	}

	protected final LoadingLayout getFooterLayout() {
		return mFooterLayout;
	}

	protected final LoadingLayout getHeaderLayout() {
		return mHeaderLayout;
	}

	protected final int getHeaderHeight() {
		return mHeaderHeight;
	}

	protected final int getMode() {
		return mMode;
	}

	/**
	 * describe:Implemented by derived class to return whether the View is in a mState
	 * 			where the user can Pull to Refresh by scrolling down.
	 *
	 * 描述：由派生类实现，返回View是否在mState中，用户可以通过向下滚动来拉动刷新。
	 *
	 * @return
	 * true if the View is currently the correct mState (for example, top of a ListView)
	 * 如果View当前是正确的mState（例如，ListView的顶部），则为true
	 */
	protected abstract boolean isReadyForPullDown();

	/**
	 * describe:Implemented by derived class to return whether the View is in a mState
	 * 			where the user can Pull to Refresh by scrolling up.
	 *
	 * 描述：由派生类实现，返回View是否在mState中，用户可以通过向上滚动来拉动刷新。
	 * @return
	 * true if the View is currently in the correct mState (for example, bottom of a ListView)
	 * 如果View当前处于正确的mState（例如，ListView的底部），则为true
	 */
	protected abstract boolean isReadyForPullUp();

	// ===========================================================
	// Methods
	// 方法
	// ===========================================================

	protected void resetHeader() {
		mState = PULL_TO_REFRESH;
		mIsBeingDragged = false;
		if (null != mHeaderLayout) {
			mHeaderLayout.reset();
		}
		if (null != mFooterLayout) {
			mFooterLayout.reset();
		}
		smoothScrollTo(0);
	}

	protected void setRefreshingInternal(boolean doScroll) {
		mState = REFRESHING;
		if (null != mHeaderLayout) {
			mHeaderLayout.refreshing();
		}
		if (null != mFooterLayout) {
			mFooterLayout.refreshing();
		}
		if (doScroll) {
			smoothScrollTo(mCurrentMode == MODE_PULL_DOWN_TO_REFRESH ? -mHeaderHeight : mHeaderHeight);
		}
	}

	protected final void setHeaderScroll(int y) {
		scrollTo(0, y);
	}

	protected final void smoothScrollTo(int y) {
		if (null != mCurrentSmoothScrollRunnable) {
			mCurrentSmoothScrollRunnable.stop();
		}
		if (getScrollY() != y) {
			mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(mHandler, getScrollY(), y);
			mHandler.post(mCurrentSmoothScrollRunnable);
		}
	}

	private void init(Context ctx, String style, AttributeSet attrs) {
		setOrientation(LinearLayout.VERTICAL);
		mTouchSlop = ViewConfiguration.getTouchSlop();
		// Refreshable View
		// 可刷新视图
		mRefreshableView = createRefreshableView(ctx);
		addView(mRefreshableView, new LayoutParams(
				LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 1.0f));
		// Loading View Strings
		// 设置Loading View字符串
		String pullLabel = ctx.getString(R.string.axeac_refresh_pull_label);
		String moreLabel = ctx.getString(R.string.axeac_refresh_more_label);
		String refreshingLabel = ctx.getString(R.string.axeac_refresh_refreshing_label);
		String releaseLabel = ctx.getString(R.string.axeac_refresh_release_label);
		// Add Loading Views
		//添加Loading View
		if (mMode == MODE_PULL_DOWN_TO_REFRESH || mMode == MODE_BOTH) {
			mHeaderLayout = new LoadingLayout(ctx, MODE_PULL_DOWN_TO_REFRESH,
					releaseLabel, pullLabel, refreshingLabel);
			addView(mHeaderLayout, 0, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			measureView(mHeaderLayout);
			mHeaderHeight = mHeaderLayout.getMeasuredHeight();
		}
		if (mMode == MODE_PULL_UP_TO_REFRESH || mMode == MODE_BOTH) {
			mFooterLayout = new LoadingLayout(ctx, MODE_PULL_UP_TO_REFRESH,
					releaseLabel, moreLabel, refreshingLabel);
			addView(mFooterLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			measureView(mFooterLayout);
			mHeaderHeight = mFooterLayout.getMeasuredHeight();
		}
		if (null != mHeaderLayout) {
			mHeaderLayout.setTextColor(Color.BLACK);
		}
		if (null != mFooterLayout) {
			mFooterLayout.setTextColor(Color.BLACK);
		}
		setBackgroundColor(Color.TRANSPARENT);
		mRefreshableView.setBackgroundColor(Color.TRANSPARENT);

		if (style.equals("LIST")) {
			// Hide Loading Views
			// 隐藏Loading View
			switch (mMode) {
				case MODE_BOTH:
					setPadding(0, -mHeaderHeight, 0, -mHeaderHeight);
					break;
				case MODE_PULL_UP_TO_REFRESH:
					setPadding(0, 0, 0, -mHeaderHeight);
					break;
				case MODE_PULL_DOWN_TO_REFRESH:
				default:
					setPadding(0, -mHeaderHeight, 0, 0);
					break;
			}
		} else {
			mHeaderLayout.setVisibility(View.GONE);
			mFooterLayout.setVisibility(View.GONE);
		}
		// If we're not using MODE_BOTH, then just set mCurrentMode to current mMode
		// 如果我们不使用MODE_BOTH，那么只需将mCurrentMode设置为当前mMode
		if (mMode != MODE_BOTH) {
			mCurrentMode = mMode;
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * describe:Actions a Pull Event
	 * 描述：拉动事件
	 * @return
	 * true if the Event has been handled, false if there has been no change
	 * 如果事件被handle，则为true，没有改变为false
	 */
	private boolean pullEvent() {
		final int newHeight;
		final int oldHeight = getScrollY();
		switch (mCurrentMode) {
			case MODE_PULL_UP_TO_REFRESH:
				newHeight = Math.round(Math.max(mInitialMotionY - mLastMotionY, 0) / FRICTION);
				break;
			case MODE_PULL_DOWN_TO_REFRESH:
			default:
				newHeight = Math.round(Math.min(mInitialMotionY - mLastMotionY, 0) / FRICTION);
				break;
		}
		setHeaderScroll(newHeight);
		if (newHeight != 0) {
			if (mState == PULL_TO_REFRESH && mHeaderHeight < Math.abs(newHeight)) {
				switch (mCurrentMode) {
					case MODE_PULL_UP_TO_REFRESH:
						mState = RELEASE_TO_RELOAD;
						mFooterLayout.releaseToRefresh();
						break;
					case MODE_PULL_DOWN_TO_REFRESH:
						mState = RELEASE_TO_REFRESH;
						mHeaderLayout.releaseToRefresh();
						break;
				}
				return true;
			} else if ((mState == RELEASE_TO_REFRESH || mState == RELEASE_TO_RELOAD) && mHeaderHeight >= Math.abs(newHeight)) {
				mState = PULL_TO_REFRESH;
				switch (mCurrentMode) {
					case MODE_PULL_UP_TO_REFRESH:
						mFooterLayout.pullToRefresh();
						break;
					case MODE_PULL_DOWN_TO_REFRESH:
						mHeaderLayout.pullToRefresh();
						break;
				}
				return true;
			}
		}
		return oldHeight != newHeight;
	}

	private boolean isReadyForPull() {
		switch (mMode) {
			case MODE_PULL_DOWN_TO_REFRESH:
				return isReadyForPullDown();
			case MODE_PULL_UP_TO_REFRESH:
				return isReadyForPullUp();
			case MODE_BOTH:
				return isReadyForPullUp() || isReadyForPullDown();
		}
		return false;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// 匿名内部类
	// ===========================================================

	public static interface OnRefreshListener {
		public void onRefresh();
		public void onLoad();
	}

	public static interface OnLastItemVisibleListener {
		public void onLastItemVisible();
	}

	@Override
	public void setLongClickable(boolean longClickable) {
		getRefreshableView().setLongClickable(longClickable);
	}
}