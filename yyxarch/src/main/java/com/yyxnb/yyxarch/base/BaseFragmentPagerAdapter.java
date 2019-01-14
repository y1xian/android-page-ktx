package com.yyxnb.yyxarch.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Description:
 *
 * @author : yyx
 * @date ：2018/6/9
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;
    private String[] titles;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    public BaseFragmentPagerAdapter(FragmentManager fm, String[] titles, List<Fragment> list) {
        super(fm);
        this.list = list;
        this.titles = titles;
    }

    //设置每页的标题
    @Override
    public CharSequence getPageTitle(int position) {
        if (titles.length > 0){
            return titles[position];
        }else {
            return super.getPageTitle(position);
        }
    }

    //设置每一页对应的fragment
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    //fragment的数量
    @Override
    public int getCount() {
        return list.size();
    }
}