package com.yyxnb.arch.frag;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.yyxnb.arch.R;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.base.BaseFragment;
import com.yyxnb.yyxarch.base.BaseFragmentStatePagerAdapter;
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM;
import com.yyxnb.yyxarch.interfaces.LayoutResId;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * ViewPager和Fragment的组合使用
 */
@LayoutResId(value = R.layout.fragment_view_page)
public class ViewPageFragment extends BaseFragment implements View.OnClickListener {

    /**
     * 四个导航按钮
     */
    @BindView(R.id.btn_one)
    Button buttonOne;
    @BindView(R.id.btn_two)
    Button buttonTwo;
    @BindView(R.id.btn_three)
    Button buttonThree;

    /**
     * 作为页面容器的ViewPager
     */
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    /**
     * 页面集合
     */
    private List<Fragment> fragmentList;

    /**
     * Fragment
     */
    private OneFragment oneFragment;
    private TwoFragment twoFragment;
    private ThreeFragment threeFragment;

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
//        buttonOne = findViewById(R.id.btn_one);
//        buttonTwo = findViewById(R.id.btn_two);
//        buttonThree = findViewById(R.id.btn_three);

        mViewPager = findViewById(R.id.viewpager);

        buttonOne.setOnClickListener(this);
        buttonTwo.setOnClickListener(this);
        buttonThree.setOnClickListener(this);

    }

    @Override
    public void initViewData() {
        super.initViewData();

        fragmentList = new ArrayList();
        oneFragment = new OneFragment();
        twoFragment = new TwoFragment();
        threeFragment = new ThreeFragment();

        fragmentList.add(oneFragment);
        fragmentList.add(twoFragment);
        fragmentList.add(threeFragment);

        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mViewPager.setAdapter(new BaseFragmentStatePagerAdapter(getChildFragmentManager(), fragmentList));

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
                changeView(0);
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
