package com.yyxnb.arch.frag


import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.yyxnb.arch.R
import com.yyxnb.yyxarch.base.BaseFragment
import java.util.*

/**
 * FrameLayout和Fragment的组合使用
 */
class ContentViewFragment : BaseFragment(), View.OnClickListener {

    /**
     * 导航按钮
     */
    private var buttonOne: Button? = null
    private var buttonTwo: Button? = null
    private var buttonThree: Button? = null

    /**
     * 页面集合
     */
    private var fragmentList: MutableList<Fragment>? = null

    private var currentIndex: Int = 0

    override fun initLayoutResID(): Int {
        return R.layout.fragment_content_view
    }

    override fun initView(savedInstanceState: Bundle?) {
        buttonOne = fv<Button>(R.id.btn_one)
        buttonTwo = fv<Button>(R.id.btn_two)
        buttonThree = fv<Button>(R.id.btn_three)

        buttonOne!!.setOnClickListener(this)
        buttonTwo!!.setOnClickListener(this)
        buttonThree!!.setOnClickListener(this)

    }

    override fun initViewData() {
        super.initViewData()
        fragmentList = ArrayList()
        fragmentList!!.add(OneFragment())
        fragmentList!!.add(TwoFragment())
        fragmentList!!.add(ThreeFragment())

        changeView(0)

        //        mViewPager.setOffscreenPageLimit(2);
        //        mViewPager.setAdapter(new BaseFragmentPagerAdapter(getChildFragmentManager(),fragmentList));

        //        LogUtils.INSTANCE.w(" " + isSingleFragment() + " , " + getClass().getSimpleName());
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

    //设置Fragment页面
    private fun changeView(index: Int) {

        //        if (currentIndex == index) {
        //            return;
        //        }
        //开启事务
        val ft = activity!!.supportFragmentManager.beginTransaction()
        //隐藏当前Fragment
        ft.hide(fragmentList!![currentIndex])
        //判断Fragment是否已经添加
        if (!fragmentList!![index].isAdded) {
            ft.add(R.id.fragment_content_view, fragmentList!![index]).show(fragmentList!![index])
        } else {
            //显示新的Fragment
            ft.show(fragmentList!![index])
        }
        ft.commitAllowingStateLoss()
        currentIndex = index
    }

    companion object {

        fun newInstance(): ContentViewFragment {

            val args = Bundle()

            val fragment = ContentViewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
