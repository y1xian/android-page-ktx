package com.yyxnb.arch.frag


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.yyxnb.arch.R
import com.yyxnb.yyxarch.base.BaseFragment
import com.yyxnb.yyxarch.base.BaseFragmentStatePagerAdapter
import kotlinx.android.synthetic.main.fragment_view_page.*
import java.util.*

/**
 * ViewPager和Fragment的组合使用
 */
class ViewPageFragment : BaseFragment(), View.OnClickListener {


    /**
     * 页面集合
     */
    private var fragmentList: MutableList<Fragment>? = null


    override fun initLayoutResID(): Int {
        return R.layout.fragment_view_page
    }

    override fun initView(savedInstanceState: Bundle?) {


    }

    override fun initViewData() {
        super.initViewData()
        fragmentList = ArrayList()
        fragmentList!!.add(OneFragment())
        fragmentList!!.add(TwoFragment())
        fragmentList!!.add(ThreeFragment())

        mViewPager.offscreenPageLimit = fragmentList!!.size - 1
        mViewPager.adapter = BaseFragmentStatePagerAdapter(childFragmentManager, fragmentList!!)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_one -> changeView(0)
            R.id.btn_two -> changeView(1)
            R.id.btn_three -> changeView(2)
            else -> {
            }
        }
    }

    //手动设置ViewPager要显示的视图
    private fun changeView(desTab: Int) {
        mViewPager!!.setCurrentItem(desTab, true)
    }

    override fun preferredStatusBarColor(): Int {
        return Color.TRANSPARENT
    }

    companion object {

        fun newInstance(): ViewPageFragment {

            val args = Bundle()

            val fragment = ViewPageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
