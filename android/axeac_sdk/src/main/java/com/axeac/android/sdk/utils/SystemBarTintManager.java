package com.axeac.android.sdk.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;

import java.lang.reflect.Method;

/**
 * describe:Class to manage status and navigation bar tint effects when using KitKat
 *          translucent system UI modes.
 *
 * 描述：使用KitKat半透明系统UI模式时，用这个类来管理状态和导航栏色调效果。
 *
 */
public class SystemBarTintManager {

    static {
        /*
           Android allows a system property to override the presence of the navigation bar.
           Used by the emulator.
           See https://github.com/android/platform_frameworks_base/blob/master/policy/src/com/android/internal/policy/impl/PhoneWindowManager.java#L1076

           Android允许系统属性覆盖导航栏
           由仿真器使用
      参见https://github.com/android/platform_frameworks_base/blob/master/policy/src/com/android/internal/policy/impl/PhoneWindowManager.java#L1076
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
                sNavBarOverride = null;
            }
        }
    }


    /**
     * describe:The default system bar tint color value.
     * 描述：默认的系统栏色调值
     */
    public static final int DEFAULT_TINT_COLOR = 0x99000000;

    private static String sNavBarOverride;

    private final SystemBarConfig mConfig;
    private boolean mStatusBarAvailable;
    private boolean mNavBarAvailable;
    private boolean mStatusBarTintEnabled;
    private boolean mNavBarTintEnabled;
    private View mStatusBarTintView;
    private View mNavBarTintView;

    /**
     * describe:Constructor. Call this in the host activity onCreate method after its
     *          content view has been set. You should always create new instances when
     *          the host activity is recreated.
     *
     * 描述：构造函数。内容视图设置后调用activity的onCreate方法。当activity重新创建时，
     *      你应该创建一个新的实例。
     *
     * @param activity The host activity.
     */
    @TargetApi(19)
    public SystemBarTintManager(Activity activity) {

        Window win = activity.getWindow();
        ViewGroup decorViewGroup = (ViewGroup) win.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // check theme attrs
            // 检查主题attrs
            int[] attrs = {android.R.attr.windowTranslucentStatus,
                    android.R.attr.windowTranslucentNavigation};
            TypedArray a = activity.obtainStyledAttributes(attrs);
            try {
                mStatusBarAvailable = a.getBoolean(0, false);
                mNavBarAvailable = a.getBoolean(1, false);
            } finally {
                a.recycle();
            }

            // check window flags
            // 检查window标志
            WindowManager.LayoutParams winParams = win.getAttributes();
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if ((winParams.flags & bits) != 0) {
                mStatusBarAvailable = true;
            }
            bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            if ((winParams.flags & bits) != 0) {
                mNavBarAvailable = true;
            }
        }

        mConfig = new SystemBarConfig(activity, mStatusBarAvailable, mNavBarAvailable);
        // device might not have virtual navigation keys
        // 设备可能没有虚拟导航键
        if (!mConfig.hasNavigtionBar()) {
            mNavBarAvailable = false;
        }

        if (mStatusBarAvailable) {
            setupStatusBarView(activity, decorViewGroup);
        }
        if (mNavBarAvailable) {
            setupNavBarView(activity, decorViewGroup);
        }

    }

    /**
     * describe:Enable tinting of the system status bar.
     *          If the platform is running Jelly Bean or earlier, or translucent system
     *          UI modes have not been enabled in either the theme or via window flags,
     *          then this method does nothing.
     *
     * 描述：启用系统状态栏的着色
     *      如果平台运行的是Jelly Bean或更早版本，或是半透明的系统UI模式尚未
     *      在主题或窗口标志中启用，那么这个方法什么都不做。
     *
     * @param enabled
     * True to enable tinting, false to disable it (default).
     * true为启用，false为禁用（默认）
     */
    public void setStatusBarTintEnabled(boolean enabled) {
        mStatusBarTintEnabled = enabled;
        if (mStatusBarAvailable) {
            mStatusBarTintView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * describe:Enable tinting of the system navigation bar.
     *          If the platform does not have soft navigation keys, is running Jelly Bean
     *          or earlier, or translucent system UI modes have not been enabled in either
     *           the theme or via window flags, then this method does nothing.
     *
     * 描述：启用系统导航栏的着色。
     *      如果平台没有软导航键，则运行Jelly Bean或更早版本，或半透明的系统UI模式尚未
     *      启用主题或通过窗口标志，那么这个方法什么都不做。
     *
     * @param enabled
     * True to enable tinting, false to disable it (default).
     * true为启用，false为禁用（默认）
     */
    public void setNavigationBarTintEnabled(boolean enabled) {
        mNavBarTintEnabled = enabled;
        if (mNavBarAvailable) {
            mNavBarTintView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * describe:Apply the specified color tint to all system UI bars.
     * 描述：将指定的颜色色调应用于所有系统UI栏。
     *
     * @param color
     * The color of the background tint.
     * 背景色的颜色
     *
     */
    public void setTintColor(int color) {
        setStatusBarTintColor(color);
        setNavigationBarTintColor(color);
    }

    /**
     * describe:Apply the specified drawable or color resource to all system UI bars.
     * 描述：将指定的drawable或颜色资源应用于所有系统UI栏。
     *
     * @param res
     * The identifier of the resource.
     * 资源的标识符
     */
    public void setTintResource(int res) {
        setStatusBarTintResource(res);
        setNavigationBarTintResource(res);
    }

    /**
     * describe:Apply the specified drawable to all system UI bars.
     * 描述：将指定的drawable应用于所有系统UI栏。
     *
     * @param drawable
     * The drawable to use as the background, or null to remove it.
     * 用作背景的drawable，为空时删除。
     */
    public void setTintDrawable(Drawable drawable) {
        setStatusBarTintDrawable(drawable);
        setNavigationBarTintDrawable(drawable);
    }

    /**
     * describe:Apply the specified alpha to all system UI bars.
     * 描述：将指定的Alpha应用于所有系统UI栏。
     *
     * @param alpha
     * The alpha to use
     * 使用的alpha
     */
    public void setTintAlpha(float alpha) {
        setStatusBarAlpha(alpha);
        setNavigationBarAlpha(alpha);
    }

    /**
     * describe:Apply the specified color tint to the system status bar.
     * 描述：将指定的颜色色调应用于系统状态栏。
     *
     * @param color
     * The color of the background tint.
     * 背景色的颜色
     */
    public void setStatusBarTintColor(int color) {
        if (mStatusBarAvailable) {
            mStatusBarTintView.setBackgroundColor(color);
        }
    }

    /**
     * describe:Apply the specified drawable or color resource to the system status bar.
     * 描述：将指定的drawable或颜色资源应用于系统状态栏。
     *
     * @param res
     * The identifier of the resource.
     * 资源标识符
     */
    public void setStatusBarTintResource(int res) {
        if (mStatusBarAvailable) {
            mStatusBarTintView.setBackgroundResource(res);
        }
    }

    /**
     * describe:Apply the specified drawable to the system status bar.
     * 描述：将指定的drawable应用于系统状态栏。
     *
     * @param drawable
     * The drawable to use as the background, or null to remove it.
     * 用作背景的drawable，为空时删除。
     */
    @SuppressWarnings("deprecation")
    public void setStatusBarTintDrawable(Drawable drawable) {
        if (mStatusBarAvailable) {
            mStatusBarTintView.setBackgroundDrawable(drawable);
        }
    }

    /**
     * describe:Apply the specified alpha to the system status bar.
     * 描述：将指定的Alpha应用于系统状态栏。
     *
     * @param alpha
     * The alpha to use
     * 使用的alpha
     */
    @TargetApi(11)
    public void setStatusBarAlpha(float alpha) {
        if (mStatusBarAvailable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mStatusBarTintView.setAlpha(alpha);
        }
    }

    /**
     * describe:Apply the specified color tint to the system navigation bar.
     * 描述：将指定的颜色色调应用于系统导航栏。
     *
     * @param color
     * The color of the background tint.
     * 背景色的颜色
     */
    public void setNavigationBarTintColor(int color) {
        if (mNavBarAvailable) {
            mNavBarTintView.setBackgroundColor(color);
        }
    }

    /**
     * describe:Apply the specified drawable or color resource to the system navigation bar.
     * 描述：将指定的drawable或颜色资源应用于系统导航栏。
     *
     * @param res
     * The identifier of the resource.
     * 资源的标识符
     */
    public void setNavigationBarTintResource(int res) {
        if (mNavBarAvailable) {
            mNavBarTintView.setBackgroundResource(res);
        }
    }

    /**
     * describe:Apply the specified drawable to the system navigation bar.
     * 描述：将指定的drawable应用于系统导航栏。
     *
     * @param drawable
     * The drawable to use as the background, or null to remove it.
     * 用作背景的drawable，为空时删除
     */
    @SuppressWarnings("deprecation")
    public void setNavigationBarTintDrawable(Drawable drawable) {
        if (mNavBarAvailable) {
            mNavBarTintView.setBackgroundDrawable(drawable);
        }
    }

    /**
     * desccribe:Apply the specified alpha to the system navigation bar.
     * 描述：将指定的Alpha应用于系统导航栏。
     *
     * @param alpha
     * The alpha to use
     * 使用的alpha
     */
    @TargetApi(11)
    public void setNavigationBarAlpha(float alpha) {
        if (mNavBarAvailable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mNavBarTintView.setAlpha(alpha);
        }
    }

    /**
     * describe:Get the system bar configuration.
     * 描述：获取系统栏配置。
     *
     * @return
     * The system bar configuration for the current device configuration.
     * 当前设备配置的系统栏配置
     */
    public SystemBarConfig getConfig() {
        return mConfig;
    }

    /**
     * describe:Is tinting enabled for the system status bar?
     * 描述：是否为系统状态栏启用着色
     *
     * @return
     * True if enabled, False otherwise.
     * 如果启用为true，否则为false
     */
    public boolean isStatusBarTintEnabled() {
        return mStatusBarTintEnabled;
    }

    /**
     * describe:Is tinting enabled for the system navigation bar?
     * 描述：是否为系统导航栏启用着色
     *
     * @return
     * True if enabled, False otherwise.
     * 如果启用为true，否则为false
     */
    public boolean isNavBarTintEnabled() {
        return mNavBarTintEnabled;
    }

    private void setupStatusBarView(Context context, ViewGroup decorViewGroup) {
        mStatusBarTintView = new View(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mConfig.getStatusBarHeight());
        params.gravity = Gravity.TOP;
        if (mNavBarAvailable && !mConfig.isNavigationAtBottom()) {
            params.rightMargin = mConfig.getNavigationBarWidth();
        }
        mStatusBarTintView.setLayoutParams(params);
        mStatusBarTintView.setBackgroundColor(DEFAULT_TINT_COLOR);
        mStatusBarTintView.setVisibility(View.GONE);
        decorViewGroup.addView(mStatusBarTintView);
    }

    private void setupNavBarView(Context context, ViewGroup decorViewGroup) {
        mNavBarTintView = new View(context);
        LayoutParams params;
        if (mConfig.isNavigationAtBottom()) {
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mConfig.getNavigationBarHeight());
            params.gravity = Gravity.BOTTOM;
        } else {
            params = new LayoutParams(mConfig.getNavigationBarWidth(), ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.RIGHT;
        }
        mNavBarTintView.setLayoutParams(params);
        mNavBarTintView.setBackgroundColor(DEFAULT_TINT_COLOR);
        mNavBarTintView.setVisibility(View.GONE);
        decorViewGroup.addView(mNavBarTintView);
    }

    /**
     * describe:Class which describes system bar sizing and other characteristics for the current
     *          device configuration.
     *
     * 描述：描述当前设备配置的系统条尺寸和其他特性的类。
     */
    public static class SystemBarConfig {

        private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
        private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
        private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
        private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
        private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";

        private final boolean mTranslucentStatusBar;
        private final boolean mTranslucentNavBar;
        private final int mStatusBarHeight;
        private final int mActionBarHeight;
        private final boolean mHasNavigationBar;
        private final int mNavigationBarHeight;
        private final int mNavigationBarWidth;
        private final boolean mInPortrait;
        private final float mSmallestWidthDp;

        private SystemBarConfig(Activity activity, boolean translucentStatusBar, boolean traslucentNavBar) {
            Resources res = activity.getResources();
            mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
            mSmallestWidthDp = getSmallestWidthDp(activity);
            mStatusBarHeight = getInternalDimensionSize(res, STATUS_BAR_HEIGHT_RES_NAME);
            mActionBarHeight = getActionBarHeight(activity);
            mNavigationBarHeight = getNavigationBarHeight(activity);
            mNavigationBarWidth = getNavigationBarWidth(activity);
            mHasNavigationBar = (mNavigationBarHeight > 0);
            mTranslucentStatusBar = translucentStatusBar;
            mTranslucentNavBar = traslucentNavBar;
        }

        @TargetApi(14)
        private int getActionBarHeight(Context context) {
            int result = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                TypedValue tv = new TypedValue();
                context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
                result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
            }
            return result;
        }

        @TargetApi(14)
        private int getNavigationBarHeight(Context context) {
            Resources res = context.getResources();
            int result = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (hasNavBar(context)) {
                    String key;
                    if (mInPortrait) {
                        key = NAV_BAR_HEIGHT_RES_NAME;
                    } else {
                        key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
                    }
                    return getInternalDimensionSize(res, key);
                }
            }
            return result;
        }

        @TargetApi(14)
        private int getNavigationBarWidth(Context context) {
            Resources res = context.getResources();
            int result = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (hasNavBar(context)) {
                    return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
                }
            }
            return result;
        }

        @TargetApi(14)
        private boolean hasNavBar(Context context) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
            if (resourceId != 0) {
                boolean hasNav = res.getBoolean(resourceId);
                // check override flag (see static block)
                // 检查覆盖标志（见静态块）
                if ("1".equals(sNavBarOverride)) {
                    hasNav = false;
                } else if ("0".equals(sNavBarOverride)) {
                    hasNav = true;
                }
                return hasNav;
            } else {
                // fallback
                // 倒退
                return !ViewConfiguration.get(context).hasPermanentMenuKey();
            }
        }

        private int getInternalDimensionSize(Resources res, String key) {
            int result = 0;
            int resourceId = res.getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
            return result;
        }

        @SuppressLint("NewApi")
        private float getSmallestWidthDp(Activity activity) {
            DisplayMetrics metrics = new DisplayMetrics();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            } else {
                // TODO this is not correct, but we don't really care pre-kitkat
                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            }
            float widthDp = metrics.widthPixels / metrics.density;
            float heightDp = metrics.heightPixels / metrics.density;
            return Math.min(widthDp, heightDp);
        }

        /**
         * describe:Should a navigation bar appear at the bottom of the screen in the current
         *          device configuration? A navigation bar may appear on the right side of
         *          the screen in certain configurations.
         *
         * 描述：当前设备配置中屏幕底部是否会出现导航栏？ 在某些配置中，导航栏可能会出现在屏幕的右侧。
         *
         * @return
         * True if navigation should appear at the bottom of the screen, False otherwise.
         * 如果导航应出现在屏幕底部，则为true，否则为false。
         */
        public boolean isNavigationAtBottom() {
            return (mSmallestWidthDp >= 600 || mInPortrait);
        }

        /**
         * describe:Get the height of the system status bar.
         * 描述：获取系统状态栏的高度。
         *
         * @return
         * The height of the status bar (in px).
         * 状态栏的高度（以px为单位）。
         */
        public int getStatusBarHeight() {
            return mStatusBarHeight;
        }

        /**
         * describe:Get the height of the action bar.
         * 描述：获取actionBar的高度
         *
         * @return
         * The height of the action bar (in px).
         * actionBar的高度（以px为单位）
         */
        public int getActionBarHeight() {
            return mActionBarHeight;
        }

        /**
         * describe:Does this device have a system navigation bar?
         * 描述：此设备是否具有系统导航栏？
         *
         * @return
         * True if this device uses soft key navigation, False otherwise.
         * 如果此设备使用软键导航，则为true，否则为false。
         */
        public boolean hasNavigtionBar() {
            return mHasNavigationBar;
        }

        /**
         * describe:Get the height of the system navigation bar.
         * 描述：获取系统导航栏的高度。
         *
         * @return
         * The height of the navigation bar (in px). If the device does not have
         * soft navigation keys, this will always return 0.
         * 导航栏的高度（以px为单位）。如果设备没有软导航键，这将始终返回0。
         */
        public int getNavigationBarHeight() {
            return mNavigationBarHeight;
        }

        /**
         * describe:Get the width of the system navigation bar when it is placed vertically on the screen.
         * 描述：当系统导航栏垂直放置在屏幕上时，获取它的宽度。
         *
         * @return
         * The width of the navigation bar (in px). If the device does not have
         * soft navigation keys, this will always return 0.
         * 导航栏的宽度（以px为单位）。 如果设备没有软导航键，则始终返回0。
         */
        public int getNavigationBarWidth() {
            return mNavigationBarWidth;
        }

        /**
         * describe:Get the layout inset for any system UI that appears at the top of the screen.
         * 描述：获取显示在屏幕顶部的任何系统UI的布局插图。
         *
         * @param withActionBar
         * True to include the height of the action bar, False otherwise.
         * true为包括动作栏的高度，否则为False。
         *
         * @return
         * The layout inset (in pixels).
         */
        public int getPixelInsetTop(boolean withActionBar) {
            return (mTranslucentStatusBar ? mStatusBarHeight : 0) + (withActionBar ? mActionBarHeight : 0);
        }

        /**
         * describe:Get the layout inset for any system UI that appears at the bottom of the screen.
         * 描述：获取显示在屏幕底部的任何系统UI的布局插图。
         * @return The layout inset (in pixels).
         */
        public int getPixelInsetBottom() {
            if (mTranslucentNavBar && isNavigationAtBottom()) {
                return mNavigationBarHeight;
            } else {
                return 0;
            }
        }

        /**
         * describe:Get the layout inset for any system UI that appears at the right of the screen.
         * 描述：获取显示在屏幕右侧的任何系统UI的布局插图。
         *
         * @return
         * The layout inset (in pixels).
         */
        public int getPixelInsetRight() {
            if (mTranslucentNavBar && !isNavigationAtBottom()) {
                return mNavigationBarWidth;
            } else {
                return 0;
            }
        }

    }

}
