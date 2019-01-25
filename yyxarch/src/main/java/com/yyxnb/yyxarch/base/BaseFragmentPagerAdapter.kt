package com.yyxnb.yyxarch.base

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Description:
 *
 * @author : yyx
 * @date ：2018/6/9
 */
class BaseFragmentPagerAdapter : FragmentPagerAdapter {
    private var list: List<Fragment>? = null
    private lateinit var titles: Array<String>

    constructor(fm: FragmentManager, list: List<Fragment>) : super(fm) {
        this.list = list
    }

    constructor(fm: FragmentManager, titles: Array<String>, list: List<Fragment>) : super(fm) {
        this.list = list
        this.titles = titles
    }

    //设置每页的标题
    override fun getPageTitle(position: Int): CharSequence? {
        return if (titles.size > 0) {
            titles[position]
        } else {
            super.getPageTitle(position)
        }
    }

    //设置每一页对应的fragment
    override fun getItem(position: Int): Fragment {
        return list!![position]
    }

    //fragment的数量
    override fun getCount(): Int {
        return list!!.size
    }
}