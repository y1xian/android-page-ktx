package com.yyxnb.yyxarch.utils

import android.widget.Toast
import com.yyxnb.yyxarch.AppUtils


/**
 * 自定义Toast
 * @author yyx
 */
object ToastUtils {

    private val mContext = AppUtils.context

    /** Toast对象  */
    private var mToast: Toast? = null

    fun normal(message: String) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
        } else {
            //如果当前Toast没有消失， 直接显示内容，不需要重新设置
            mToast!!.setText(message)
        }
        mToast!!.show()
    }


}
