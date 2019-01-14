package com.yyxnb.yyxarch.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.yyxnb.yyxarch.AppUtils;


/**
 * 自定义Toast
 * @author yyx
 */
public class ToastUtils {

    private static Context mContext= AppUtils.getContext();

    /** Toast对象 */
    private static Toast mToast = null ;

    private ToastUtils() {
    }

    public static void normal( @NonNull String message) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        } else {
            //如果当前Toast没有消失， 直接显示内容，不需要重新设置
            mToast.setText(message);
        }
        mToast.show();
    }


}
