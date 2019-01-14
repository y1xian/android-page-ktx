package com.yyxnb.yyxarch.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.yyxnb.yyxarch.ContainerActivity;

import java.util.concurrent.TimeUnit;

import static com.yyxnb.yyxarch.common.Config.BUNDLE;
import static com.yyxnb.yyxarch.common.Config.FRAGMENT;


/**
 * Description:
 *
 * @author : yyx
 * @date ：2018/6/10
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    protected FragmentActivity mActivity;

    private final String TAG = getClass().getSimpleName();

    private View mRootView;
    //是否可见状态
    private boolean mIsVisible;
    //标志位，View已经初始化完成
    private boolean mIsPrepared;
    //是否第一次加载
    private boolean mIsFirstVisible = true;

    public BaseFragment() {
        getLifecycle().addObserver(new Java8Observer());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.size() > 0) {
            initVariables(bundle);
        }
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null == mRootView) {
            mIsFirstVisible = true;
            mRootView = inflater.inflate(initLayoutResID(), container, false);
            mIsPrepared = true;
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewData(savedInstanceState);
    }


    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (null == mRootView) {
            return;
        }
        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInVisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (null == mRootView) {
            return;
        }
        if (!hidden) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInVisible();
        }
    }

    /**
     * 被ViewPager移出的Fragment 下次显示时会从getArguments()中重新获取数据
     * 所以若需要刷新被移除Fragment内的数据需要重新put数据 eg:
     * Bundle args = getArguments();
     * if (args != null) {
     * args.putParcelable(KEY, info);
     * }
     */
    public void initVariables(Bundle bundle) {}

    /**
     * 当界面可见时的操作
     */
    protected void onVisible() {
        initLazyLoadView();
    }

    /**
     * 当界面不可见时的操作
     */
    protected void onInVisible() {
    }

    /**
     * 数据懒加载
     */
    protected void initLazyLoadView() {
        if (!mIsPrepared || !mIsVisible || mIsFirstVisible) {
            return;
        }
        mIsFirstVisible = false;
    }

    @Override
    public void onClick(View v) {
        RxView.clicks(v).throttleFirst(1, TimeUnit.SECONDS).subscribe(o -> onClickWidget(v));
    }

    /*
      防止快速点击
    */
    public void onClickWidget(View v) {

    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(mActivity, clz);
        if (null != bundle) {
            intent.putExtra(BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : com.yyxnb.yyxarch.base.BaseFragment
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param fragment 规范名 : Fragment.newInstance()
     */
    public void startContainerActivity(BaseFragment fragment) {
        startContainerActivity(fragment, null);
    }

    /**
     * 跳转容器页面
     *
     * @param fragment 规范名 : Fragment.newInstance()
     * @param bundle   跳转所携带的信息
     */
    public void startContainerActivity(BaseFragment fragment, Bundle bundle) {
        startContainerActivity(fragment.getClass().getCanonicalName(), bundle);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : com.yyxnb.yyxarch.base.BaseFragment
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(mActivity, ContainerActivity.class);
        intent.putExtra(FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转容器页面
     *
     * @param requestCode requestCode
     * @param fragment    规范名 : Fragment.newInstance()
     */
    public void startContainerActivityForResult(int requestCode, BaseFragment fragment) {
        startContainerActivityForResult(requestCode, fragment.getClass().getCanonicalName(), null);
    }

    /**
     * 跳转容器页面
     *
     * @param requestCode   requestCode
     * @param canonicalName 规范名 : com.yyxnb.yyxarch.base.BaseFragment
     */
    public void startContainerActivityForResult(int requestCode, String canonicalName) {
        startContainerActivityForResult(requestCode, canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param requestCode   requestCode
     * @param canonicalName 规范名 : com.yyxnb.yyxarch.base.BaseFragment
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivityForResult(int requestCode, String canonicalName, Bundle bundle) {
        Intent intent = new Intent(mActivity, ContainerActivity.class);
        intent.putExtra(FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(BUNDLE, bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract @LayoutRes
    int initLayoutResID();

    /**
     * 初始化控件
     */
    public void initView(Bundle savedInstanceState) {
    }

    /**
     * 初始化复杂数据
     */
    public void initViewData(Bundle savedInstanceState) {
    }

    /**
     * 回调网络数据
     */
    public void initViewObservable() {
    }


    public View getRootView() {
        return mRootView;
    }

    public boolean isBackPressed() {
        return false;
    }

    protected <T> T fv(@IdRes int resid) {
        return (T) mRootView.findViewById(resid);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsPrepared = false;
    }

}
