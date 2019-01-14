package com.yyxnb.yyxarch.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.yyxnb.yyxarch.utils.ActivityStack;
import com.yyxnb.yyxarch.utils.FragmentUtils;

import java.util.concurrent.TimeUnit;


/**
 * Description:
 *
 * @author : yyx
 * @date : 2018/6/10
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private int generateViewId = android.view.View.generateViewId();

    private FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLifecycle().addObserver(new Java8Observer());

        if (initLayoutResID() == 0) {
            mFragmentContainer = new FrameLayout(this);
            mFragmentContainer.setId(generateViewId);
            setContentView(mFragmentContainer);
        } else {
            setContentView(initLayoutResID());
        }

        initView(savedInstanceState);

        initViewObservable();

        ActivityStack.getInstance().addActivity(this);
    }

    public FrameLayout getFragmentContainer() {
        return mFragmentContainer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().finishActivity(this);
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    protected abstract int initLayoutResID();

    /**
     * 初始化
     */
    public void initView(Bundle savedInstanceState) {
    }

    /**
     * 初始化界面观察者的监听
     * 接收数据结果
     */
    public void initViewObservable() {
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

    public void startFragment(BaseFragment fragment, int containerViewId) {
        FragmentUtils.add(getSupportFragmentManager(), fragment, containerViewId);
    }
}
