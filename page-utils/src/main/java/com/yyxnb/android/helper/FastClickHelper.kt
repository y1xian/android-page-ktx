package com.yyxnb.android.helper

/**
 * 快速点击帮助类
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/9/6
 */
object FastClickHelper {
    // 两次点击间隔不能少于500毫秒
    const val MIN_CLICK_DELAY_TIME = 500L

    private val records: MutableMap<String, Long> = HashMap()

    private var lastClickTime: Long = 0

    /**
     * 无id管理
     *
     * @param delayTime Long 间隔时间
     * @return Boolean 是否处于快速点击状态
     */
    fun isFastClick(delayTime: Long = MIN_CLICK_DELAY_TIME): Boolean {
        var b = false
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime >= delayTime) {
            b = true
            lastClickTime = curClickTime
        }
        return b
    }

    /**
     * 根据id管理
     *
     * @param id Long id
     * @param delayTime Long 间隔时间
     * @return Boolean 是否处于快速点击状态
     */
    fun isFastClickId(id: Long, delayTime: Long = MIN_CLICK_DELAY_TIME): Boolean {
        if (records.size > 100) {
            records.clear()
        }
        var flag = false
        val key = id.toString()
        var lastOnClickTime = records[key]
        val curClickTime = System.currentTimeMillis()
        if (lastOnClickTime == null) {
            lastOnClickTime = 0L
        }
        if (curClickTime - lastOnClickTime >= delayTime) {
            flag = true
            records[key] = curClickTime
        }
        return flag
    }
}