package com.example.basemodel.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.example.basemodel.R

/**
 * Created by liub on 2018/7/16 17:22
 *
 */
class StatusBarUtils {
    val DEFAULT_STATUS_BAR_ALPHA = 0

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    fun setColor(activity: Activity, @ColorInt color: Int) {
        setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA)
    }

    fun setWhiteStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (setStatusBarLightMode(activity) == 0) {
                activity.window.statusBarColor = -0x1000000
            } else {
                activity.window.statusBarColor = -0x1
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val decorView = activity.window.decorView as ViewGroup
            val count = decorView.childCount
            if (count > 0 && decorView.getChildAt(count - 1) is StatusBarView) {
                decorView.getChildAt(count - 1).setBackgroundDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.shadow_status_bar))
            } else {
                val statusView = createGradientStatusBarView(activity)
                decorView.addView(statusView)
            }
            setRootView(activity)
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */

    fun setColor(activity: Activity, @ColorInt color: Int, statusBarAlpha: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor = calculateStatusColor(color, statusBarAlpha)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val decorView = activity.window.decorView as ViewGroup
            val count = decorView.childCount
            if (count > 0 && decorView.getChildAt(count - 1) is StatusBarView) {
                decorView.getChildAt(count - 1).setBackgroundColor(calculateStatusColor(color, statusBarAlpha))
            } else {
                val statusView = createStatusBarView(activity, color, statusBarAlpha)
                decorView.addView(statusView)
            }
            setRootView(activity)
        }
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTranslucentForImageView(activity: Activity, statusBarAlpha: Int, needOffsetView: View?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        setTransparentForWindow(activity)
        addTranslucentView(activity, statusBarAlpha)
        if (needOffsetView != null) {
            val layoutParams = needOffsetView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0, getStatusBarHeight(activity), 0, 0)
        }
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    fun FlymeSetStatusBarLightMode(window: Window?, dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                        .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (dark) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    fun MIUISetStatusBarLightMode(window: Window?, dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                }
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    fun setStatusBarLightMode(activity: Activity): Int {
        var result = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                MIUISetStatusBarLightMode(activity.window, true)
                result = 3
            } else if (MIUISetStatusBarLightMode(activity.window, true)) {
                result = 1
            } else if (FlymeSetStatusBarLightMode(activity.window, true)) {
                result = 2
            }
        }
        return result
    }

    /**
     * 设置透明
     */
    fun setTransparentForWindow(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 添加半透明矩形条
     *
     * @param activity       需要设置的 activity
     * @param statusBarAlpha 透明值
     */
    private fun addTranslucentView(activity: Activity, statusBarAlpha: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return
        }
        val contentView = activity.findViewById<View>(android.R.id.content) as ViewGroup
        if (contentView.childCount > 1) {
            contentView.getChildAt(1).setBackgroundDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.shadow_status_bar))
        } else {
            contentView.addView(createGradientStatusBarView(activity))
        }
    }


    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private fun calculateStatusColor(@ColorInt color: Int, alpha: Int): Int {
        //        float a = 1 - alpha / 255f;
        val red = color shr 16 and 0xff
        val green = color shr 8 and 0xff
        val blue = color and 0xff
        //        red = (int) (red * a + 0.5);
        //        green = (int) (green * a + 0.5);
        //        blue = (int) (blue * a + 0.5);
        return alpha shl 24 or (red shl 16) or (green shl 8) or blue
    }

    /**
     * 设置根布局参数
     */
    private fun setRootView(activity: Activity) {
        val rootView = (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        rootView.fitsSystemWindows = true
        rootView.clipToPadding = true
    }

    /**
     * 创建半透明矩形 View
     *
     * @param alpha 透明值
     * @return 半透明 View
     */
    private fun createTranslucentStatusBarView(activity: Activity, alpha: Int): StatusBarView {
        // 绘制一个和状态栏一样高的矩形
        val statusBarView = StatusBarView(activity)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
        statusBarView.layoutParams = params
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0))
        return statusBarView
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    private fun createStatusBarView(activity: Activity, @ColorInt color: Int, alpha: Int): StatusBarView {
        // 绘制一个和状态栏一样高的矩形
        val statusBarView = StatusBarView(activity)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
        statusBarView.layoutParams = params
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha))
        return statusBarView
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @return 状态栏矩形条
     */
    private fun createGradientStatusBarView(activity: Activity): StatusBarView {
        // 绘制一个和状态栏一样高的矩形
        val statusBarView = StatusBarView(activity)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
        statusBarView.layoutParams = params
        statusBarView.setBackgroundDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.shadow_status_bar))
        return statusBarView
    }
}