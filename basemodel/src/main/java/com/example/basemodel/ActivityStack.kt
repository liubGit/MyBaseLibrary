package com.example.basemodel

import android.app.Activity
import android.support.annotation.Nullable
import java.lang.Exception
import java.util.*

/**
 * Created by liub on 2018/7/16 11:14
 *
 */

@SuppressWarnings("WeakerAccess", "unused")
final class ActivityStack {
    private lateinit var instance: ActivityStack
    private var activityList = LinkedList<Activity>()

    private var islock: Boolean = false//锁定，主动销毁activity的时候，不执行
    @Synchronized
    fun getInstance(): ActivityStack {
        if (instance == null) {
            synchronized(this) {
                if (instance == null) {
                    instance = ActivityStack()
                }
            }
        }
        return instance
    }

    /**
     * 存放activity到list中
     */
    fun addActivity(activity: Activity) {
        activityList.add(activity)
    }

    /**
     * 是否存在目标class
     */
    fun hasActivity(tragetActivity: Class<Activity>): Boolean {
        activityList.forEach {
            if (tragetActivity == it.javaClass) {
                return true
            }
        }
        return false
    }

    /**
     * 从list中移除
     */
    fun removeActivity(activity: Activity) {
        if (!islock) {
            activityList.remove(activity)
        }
    }

    /**
     * 结束指定的activity
     */
    fun finish(cls: Class<Any>) {
        var temp = ArrayList<Activity>(1)
        activityList.forEach {
            if (cls == it.javaClass) {
                temp.add(it)
            }
        }
        temp.forEach {
            it.finish()
            activityList.remove(it)
        }
    }

    /**
     * 关闭全部actrivity
     */
    fun finishAllActivity() {
        islock = true
        try {
            activityList.forEach {
                it.finish()
            }
            activityList.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        islock = false
    }

    @Nullable
    fun getTopActivityClass(): Class<*>? {
        val topActivity = getTopActivity() ?: return null
        return topActivity.javaClass
    }

    @Nullable
    fun getTopActivity() : Activity? {
        var index = activityList.size - 1
        if (index < 0 || activityList.isEmpty()) {
            return null
        }
        return activityList[index]
    }

}