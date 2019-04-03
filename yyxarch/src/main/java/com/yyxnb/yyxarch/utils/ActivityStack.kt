package com.yyxnb.yyxarch.utils

import android.app.Activity
import java.lang.ref.WeakReference
import java.util.*


/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * @author yyx
 */
object ActivityStack {

    private var mActivityStack: Stack<WeakReference<Activity>>? = null

    /**
     * 获取当前Activity栈中元素个数
     */
    val count: Int
        get() = mActivityStack!!.size

    /**
     * 添加Activity到栈
     * @param activity
     */
    fun addActivity(activity: Activity) {
        if (mActivityStack == null) {
            mActivityStack = Stack()
        }
        mActivityStack!!.add(WeakReference(activity))
    }

    /**
     * 检查弱引用是否释放，若释放，则从栈中清理掉该元素
     */
    fun checkWeakReference() {
        if (mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack!!.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val temp = activityReference.get()
                if (temp == null) {
                    it.remove()
                }
            }
        }
    }

    /**
     * 获取当前Activity（栈中最后一个压入的）
     * @return
     */
    fun currentActivity(): Activity? {
        checkWeakReference()
        return if (mActivityStack != null && !mActivityStack!!.isEmpty()) {
            mActivityStack!!.lastElement().get()
        } else null
    }

    /**
     * 关闭当前Activity（栈中最后一个压入的）
     */
    fun finishActivity() {
        val activity = currentActivity()
        if (activity != null) {
            finishActivity(activity)
        }
    }

    /**
     * 关闭指定的Activity
     * @param activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null && mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack!!.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val temp = activityReference.get()
                // 清理掉已经释放的activity
                if (temp == null) {
                    it.remove()
                    continue
                }
                if (temp === activity) {
                    it.remove()
                }
            }
            activity.finish()
        }
    }

    /**
     * 关闭指定类名的所有Activity
     * @param cls
     */
    fun finishActivity(cls: Class<*>) {
        if (mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack!!.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val activity = activityReference.get()
                // 清理掉已经释放的activity
                if (activity == null) {
                    it.remove()
                    continue
                }
                if (activity.javaClass == cls) {
                    it.remove()
                    activity.finish()
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        if (mActivityStack != null) {
            for (activityReference in mActivityStack!!) {
                val activity = activityReference.get()
                activity?.finish()
            }
            mActivityStack!!.clear()
        }
    }

    /**
     * 退出应用程序
     */
    fun exitApp() {
        try {
            finishAllActivity()
            // 退出JVM,释放所占内存资源,0表示正常退出
            System.exit(0)
            // 从系统中kill掉应用程序
            android.os.Process.killProcess(android.os.Process.myPid())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}