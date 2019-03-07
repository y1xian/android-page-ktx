package com.yyxnb.yyxarch.utils

import android.app.Activity
import java.util.*


/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * @author yyx
 */
object ActivityStack {

    private var activityStack: Stack<Activity>? = null

    /**
     * 获取当前Activity栈中元素个数
     */
    val count: Int
        get() = activityStack!!.size

    /**
     * 添加Activity到栈
     */
    fun addActivity(activity: Activity) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(activity)
    }

    /**
     * 获取当前Activity（栈顶Activity）
     */
    fun topActivity(): Activity? {
        if (activityStack == null) {
            throw NullPointerException(
                    "Activity stack is Null,your Activity must extend BaseActivity")
        }
        return if (activityStack!!.isEmpty()) {
            null
        } else activityStack!!.lastElement()
    }

    /**
     * 获取当前Activity（栈顶Activity） 没有找到则返回null
     */
    fun findActivity(cls: Class<*>): Activity? {
        var activity: Activity? = null
        for (aty in activityStack!!) {
            if (aty.javaClass == cls) {
                activity = aty
                break
            }
        }
        return activity
    }

    /**
     * 结束当前Activity（栈顶Activity）
     */
    fun finishActivity() {
        val activity = activityStack!!.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity(重载)
     */
    fun finishActivity(activity: Activity?) {
        var activity = activity
        if (activity != null) {
            activityStack!!.remove(activity)
            // activity.finish();//此处不用finish
            activity = null
        }
    }

    /**
     * 结束指定的Activity(重载)
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack!!) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     *
     * @param cls
     */
    fun finishOthersActivity(cls: Class<*>) {
        for (activity in activityStack!!) {
            if (activity.javaClass != cls) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        var i = 0
        val size = activityStack!!.size
        while (i < size) {
            if (null != activityStack!![i]) {
                activityStack!![i].finish()
            }
            i++
        }
        activityStack!!.clear()
    }


    /**
     * 应用程序退出
     */
    fun appExit() {
        try {
            finishAllActivity()
            //退出JVM(java虚拟机),释放所占内存资源,0表示正常退出(非0的都为异常退出)
            System.exit(0)
            //从操作系统中结束掉当前程序的进程
            android.os.Process.killProcess(android.os.Process.myPid())
        } catch (e: Exception) {
            System.exit(-1)
        }

    }


}