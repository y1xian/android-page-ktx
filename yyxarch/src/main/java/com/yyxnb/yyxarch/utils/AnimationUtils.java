package com.yyxnb.yyxarch.utils;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Description: 动画工具
 *
 * @author : yyx
 * @date : 2018/7/16
 */
public class AnimationUtils {

    //从上面显示隐藏view动画
    public static TranslateAnimation getHiddenTopAction(){
        TranslateAnimation mAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mAction.setDuration(300);
        return mAction;
    }
    public static TranslateAnimation getShowTopAction(){
        TranslateAnimation mAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                0f);
        mAction.setDuration(300);
        return mAction;
    }

    //从下面显示隐藏view动画
    public TranslateAnimation getHiddenBottomAction(){
        TranslateAnimation mAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        mAction.setDuration(300);
        return mAction;
    }
    public TranslateAnimation getShowBottomAction(){
        TranslateAnimation mAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0f);
        mAction.setDuration(300);
        return mAction;
    }

    //从左边显示隐藏view动画
    public TranslateAnimation getHiddenLeftAction(){
        TranslateAnimation mAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0f);
        mAction.setDuration(300);
        return mAction;
    }
    public TranslateAnimation getShowLeftAction(){
        TranslateAnimation mAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f);
        mAction.setDuration(300);
        return mAction;
    }

    //从右边显示隐藏view动画
    public TranslateAnimation getHiddenRightAction(){
        TranslateAnimation mAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0f);
        mAction.setDuration(300);
        return mAction;
    }
    public TranslateAnimation getShowRightAction(){
        TranslateAnimation mAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f);
        mAction.setDuration(300);
        return mAction;
    }

    //显示隐藏动画 淡入淡出
    public AlphaAnimation getHiddenAnimation(){

        AlphaAnimation mAction = new AlphaAnimation(1.0f, 0f);
        mAction.setDuration(300);
        return mAction;
    }
    public AlphaAnimation getShowAnimation(){

        AlphaAnimation mAction = new AlphaAnimation(0f, 1.0f);
        mAction.setDuration(300);
        return mAction;
    }

    //旋转动画 顺时针
    public ObjectAnimator getClockwiseAnimator(){
        @SuppressLint("ObjectAnimatorBinding")
        ObjectAnimator mAction = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        mAction.setDuration(300);
         return mAction;
    }

    //旋转动画 逆时针
    public ObjectAnimator getAntiClockwiseAnimator(){
        @SuppressLint("ObjectAnimatorBinding")
        ObjectAnimator mAction = ObjectAnimator.ofFloat(this, "rotation", 3600f, 0f);
        mAction.setDuration(300);
        return mAction;
    }
}
