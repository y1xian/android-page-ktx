package com.yyxnb.arch.frag;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.yyxnb.arch.R;
import com.yyxnb.yyxarch.base.BaseFragment;
import com.yyxnb.yyxarch.interfaces.BarStyle;
import com.yyxnb.yyxarch.interfaces.LayoutResId;
import com.yyxnb.yyxarch.interfaces.StatusBarColor;
import com.yyxnb.yyxarch.interfaces.StatusBarDarkTheme;
import com.yyxnb.yyxarch.interfaces.StatusBarTranslucent;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * FrameLayout和Fragment的组合使用
 */
@LayoutResId(value = R.layout.fragment_content_view)
public class ContentViewFragment extends BaseFragment implements View.OnClickListener {

    /**
     * 导航按钮
     */
    @BindView(R.id.btn_one)
    Button buttonOne;
    @BindView(R.id.btn_two)
    Button buttonTwo;
    @BindView(R.id.btn_three)
    Button buttonThree;

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

    private int currentIndex;

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
//        buttonOne = findViewById(R.id.btn_one);
//        buttonTwo = findViewById(R.id.btn_two);
//        buttonThree = findViewById(R.id.btn_three);

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
        threeFragment = new ThreeFragment();

        fragmentList.add(oneFragment);
        fragmentList.add(twoFragment);
        fragmentList.add(threeFragment);

        changeView(0);

//        mViewPager.setOffscreenPageLimit(2);
//        mViewPager.setAdapter(new BaseFragmentPagerAdapter(getChildFragmentManager(),fragmentList));

//        LogUtils.INSTANCE.w(" " + isSingleFragment() + " , " + getClass().getSimpleName());
    }

    public static ContentViewFragment newInstance() {

        Bundle args = new Bundle();

        ContentViewFragment fragment = new ContentViewFragment();
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

    //设置Fragment页面
    private void changeView(int index) {

//        if (currentIndex == index) {
//            return;
//        }
        //开启事务
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        //隐藏当前Fragment
        ft.hide(fragmentList.get(currentIndex));
        //判断Fragment是否已经添加
        if (!fragmentList.get(index).isAdded()) {
            ft.add(R.id.fragment_content_view, fragmentList.get(index)).show(fragmentList.get(index));
        } else {
            //显示新的Fragment
            ft.show(fragmentList.get(index));
        }
        ft.commitAllowingStateLoss();
        currentIndex = index;
    }
}
