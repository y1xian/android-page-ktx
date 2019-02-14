package com.yyxnb.yyxarch.utils.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.yyxnb.yyxarch.utils.ScreenUtils;

/**
 * Description: BaseSheetDialog
 *
 * @author : yyx
 * @date ：2018/11/18
 */
public abstract class BaseSheetDialog<T extends BaseSheetDialog> extends BottomSheetDialogFragment {

    private static final float DEFAULT_DIM = 0.5f;
    private static final int DEFAULT_WH = ViewGroup.LayoutParams.MATCH_PARENT;

    private String mTag = "BaseSheetDialog";

    @LayoutRes
    private int mLayoutRes;

    //点击外部是否可取消
    private boolean mIsCancelOnTouchOutside = true;

    // 顶部向下偏移量
    private int mTopOffset = 0;
    // 折叠的高度
    private int mPeekHeight = 0;
    // 默认折叠高度 屏幕60%
    @FloatRange(from = 0f, to = 1.0f)
    private float mDefaultHeight = 0.6f;

    //设置阴影透明度 默认0.5f
    @FloatRange(from = 0f, to = 1.0f)
    private float mDimAmount = DEFAULT_DIM;

    private boolean mIsKeyboardEnable = true;

    private boolean mIsTransparent = true;

    private boolean mIsBehavior = true;

    private int mHeight = DEFAULT_WH;

    private int mWidth = DEFAULT_WH;

    @StyleRes
    private int mAnimationStyle;

    private int mSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;

    // 初始为展开状态
    private int mState = ViewPagerBottomSheetBehavior.STATE_EXPANDED;

    private ViewPagerBottomSheetBehavior<FrameLayout> mBehavior;

    protected T self() {
        //noinspection unchecked
        return (T) this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //解决ViewPager + Fragment 无法滑动问题
        return new ViewPagerBottomSheetDialog(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(initLayoutId());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(mLayoutRes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        //这样设置高度才会正确展示
        view.setLayoutParams(new ViewGroup.LayoutParams(DEFAULT_WH, mHeight));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null || getDialog().getWindow() == null) {
            return;
        }
        AppCompatDialog dialog = (AppCompatDialog) getDialog();
        FrameLayout bottomSheet = dialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
        if (bottomSheet != null && mIsBehavior) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            mBehavior = ViewPagerBottomSheetBehavior.from(bottomSheet);
            if (mHeight <= 0) {
                //高度 = 屏幕高度 - 顶部向下偏移量
                layoutParams.height = ScreenUtils.INSTANCE.getScreenHeight() - mTopOffset;
                //如果顶部向下偏移量为0
                if (mTopOffset == 0) {
                    //如果默认高度为0
                    if (mPeekHeight == 0) {
                        //则 默认高度60% 且为折叠状态
                        mPeekHeight = (int) (ScreenUtils.INSTANCE.getScreenHeight() * mDefaultHeight);
                        mState = BottomSheetBehavior.STATE_COLLAPSED;
                    }
                } else {
                    //如果顶部向下偏移量不为0 ，则为高度 = 屏幕高度 - 顶部向下偏移量
                    mPeekHeight = layoutParams.height;
                }
            }
            mBehavior.setPeekHeight(mHeight > 0 ? mHeight : mPeekHeight);
            // 初始为展开
            mBehavior.setState(mState);
        }
        if (mIsTransparent) {
            bottomSheet.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        getDialog().setCanceledOnTouchOutside(mIsCancelOnTouchOutside);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        initWindowLayoutParams(window, layoutParams);
        window.setAttributes(layoutParams);
    }

    /**
     * 初始化 Window 和 LayoutParams 参数
     *
     * @param window
     * @param layoutParams
     */
    @SuppressLint("ResourceType")
    protected void initWindowLayoutParams(Window window, WindowManager.LayoutParams layoutParams) {
        if (mIsKeyboardEnable) {
            window.setSoftInputMode(mSoftInputMode);
        }

        if (mAnimationStyle > 0) {
            window.setWindowAnimations(mAnimationStyle);
        }

        //这样设置高度会发生布局错误
//        if (mHeight > 0 || mHeight == ViewGroup.LayoutParams.MATCH_PARENT || mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
//            layoutParams.height = mHeight;
//        }

        if (mWidth > 0 || mWidth == ViewGroup.LayoutParams.MATCH_PARENT || mWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.width = mWidth;
        }

        layoutParams.dimAmount = mDimAmount;
    }

    /**
     * 设置dialog布局
     *
     * @return
     */
    public abstract int initLayoutId();

    /**
     * 初始化 Views
     *
     * @param view
     */
    public abstract void initViews(View view);

    /**
     * 设置布局id
     *
     * @param layoutRes
     * @return
     */
    public T setLayoutRes(@LayoutRes int layoutRes) {
        this.mLayoutRes = layoutRes;
        return self();
    }

    /**
     * 设置背景是否透明
     *
     * @param mIsTransparent
     * @return
     */
    public T setIsTransparent(boolean mIsTransparent) {
        this.mIsTransparent = mIsTransparent;
        return self();
    }

    /**
     * 设置是否使用Behavior
     *
     * @param mIsBehavior
     * @return
     */
    public T setIsBehavior(boolean mIsBehavior) {
        this.mIsBehavior = mIsBehavior;
        return self();
    }

    /**
     * 判断是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    /**
     * 显示Dialog
     *
     * @param fragmentManager
     */
    public void show(FragmentManager fragmentManager) {
        if (!isAdded()) {
            show(fragmentManager, mTag);
        }
    }

    /**
     * 设置点击 Dialog 之外的地方是否消失
     *
     * @param isCancelOnTouchOutside
     * @return
     */
    public T setCancelOnTouchOutside(boolean isCancelOnTouchOutside) {
        this.mIsCancelOnTouchOutside = isCancelOnTouchOutside;
        return self();
    }

    /**
     * 设置顶部向下偏移量
     *
     * @param mTopOffset
     * @return
     */
    public T setTopOffset(int mTopOffset) {
        this.mTopOffset = mTopOffset;
        return self();
    }

    /**
     * 设置高度
     *
     * @param mPeekHeight
     * @return
     */
    public T setPeekHeight(int mPeekHeight) {
        this.mPeekHeight = mPeekHeight;
        return self();
    }

    /**
     * 设置默认高度
     *
     * @param mDefaultHeight
     * @return
     */
    public T setDefaultHeight(@FloatRange(from = 0f, to = 1.0f) float mDefaultHeight) {
        this.mDefaultHeight = mDefaultHeight;
        return self();
    }

    /**
     * 设置状态
     *
     * @param mState
     * @return
     */
    public T setState(int mState) {
        this.mState = mState;
        return self();
    }

    public ViewPagerBottomSheetBehavior<FrameLayout> getBehavior() {
        return mBehavior;
    }

    /**
     * 设置显示时的 Fragment Tag
     *
     * @param tag
     * @return
     */
    public T setFragmentTag(String tag) {
        this.mTag = tag;
        return self();
    }

    public String getFragmentTag() {
        return mTag;
    }

    /**
     * 设置阴影透明度
     *
     * @param dimAmount
     * @return
     */
    public T setDimAmount(@FloatRange(from = 0f, to = 1.0f) float dimAmount) {
        this.mDimAmount = dimAmount;
        return self();
    }

    /**
     * 设置 Dialog 高度
     *
     * @param height
     * @return
     */
    public T setHeight(int height) {
        this.mHeight = height;
        return self();
    }

    /**
     * 设置 Dialog 宽度
     *
     * @param width
     * @return
     */
    public T setWidth(int width) {
        this.mWidth = width;
        return self();
    }

    /**
     * 设置 Dialog 显示动画
     *
     * @param animationStyle
     * @return
     */
    public T setAnimationStyle(@StyleRes int animationStyle) {
        this.mAnimationStyle = animationStyle;
        return self();
    }

    /**
     * 设置是否支持弹出键盘调整位置
     *
     * @param keyboardEnable
     * @return
     */
    public T setKeyboardEnable(boolean keyboardEnable) {
        this.mIsKeyboardEnable = keyboardEnable;
        return self();
    }

    /**
     * 设置弹出键盘时调整方式
     *
     * @param inputMode
     * @return
     */
    public T setSoftInputMode(int inputMode) {
        this.mSoftInputMode = inputMode;
        return self();
    }
}
