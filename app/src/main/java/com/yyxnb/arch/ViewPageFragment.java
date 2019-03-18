package com.yyxnb.arch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.yyxnb.yyxarch.base.BaseFragmentStatePagerAdapter;
import com.yyxnb.yyxarch.base.mvvm.BaseMvvmFragment;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager和Fragment的组合使用
 */
public class ViewPageFragment extends BaseMvvmFragment<TestViewModel> implements View.OnClickListener {

    /**
     * 四个导航按钮
     */
    private Button buttonOne;
    private Button buttonTwo;
    private Button buttonThree;

    /**
     * 作为页面容器的ViewPager
     */
    private ViewPager mViewPager;

    /**
     * 页面集合
     */
    private List<Fragment> fragmentList;

    /**
     * Fragment
     */
    private TestFragment testFragment;
    private OneFragment oneFragment;
    private TwoFragment twoFragment;

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_view_page;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        buttonOne = fv(R.id.btn_one);
        buttonTwo = fv(R.id.btn_two);
        buttonThree = fv(R.id.btn_three);

        mViewPager = fv(R.id.viewpager);

        buttonOne.setOnClickListener(this);
        buttonTwo.setOnClickListener(this);
        buttonThree.setOnClickListener(this);

    }

    @Override
    public void initViewData() {
        super.initViewData();

        fragmentList = new ArrayList<Fragment>();
        oneFragment = new OneFragment();
        twoFragment = new TwoFragment();
        testFragment = new TestFragment();

        fragmentList.add(oneFragment);
        fragmentList.add(twoFragment);
        fragmentList.add(testFragment);

        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mViewPager.setAdapter(new BaseFragmentStatePagerAdapter(getChildFragmentManager(),fragmentList));

//        LogUtils.INSTANCE.w(" " + isSingleFragment() + " , " + getClass().getSimpleName());
        LogUtils.INSTANCE.w("initViewData");
    }

    @Override
    public void onVisible() {
        super.onVisible();
        LogUtils.INSTANCE.w("onVisible");
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        LogUtils.INSTANCE.w("onInVisible");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public static ViewPageFragment newInstance() {

        Bundle args = new Bundle();

        ViewPageFragment fragment = new ViewPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
//                changeView(0);
                setResult(0x2);
                finish();
                break;
            case R.id.btn_two:
                changeView(1);
                break;
            case R.id.btn_three:
                changeView(2);
                break;
            default:
                break;
        }
    }

    //手动设置ViewPager要显示的视图
    private void changeView(int desTab) {
        mViewPager.setCurrentItem(desTab, true);
    }
}
