package com.yyxnb.arch.frag


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.yyxnb.arch.R
import com.yyxnb.yyxarch.base.BaseFragment
import com.yyxnb.yyxarch.utils.BarStyle
import com.yyxnb.yyxarch.utils.ToastUtils
import com.yyxnb.yyxarch.utils.log.LogUtils
import kotlinx.android.synthetic.main.fragment_test.*


/**
 * A simple [Fragment] subclass.
 */
class TestFragment : BaseFragment() {


    private var value: String? = null

    override fun initLayoutResID(): Int {
        return R.layout.fragment_test
    }

    override fun initView(savedInstanceState: Bundle?) {



        value = arguments!!.getString("value")
    }

    override fun initVariables(bundle: Bundle) {
        super.initVariables(bundle)

        value = bundle.getString("value")
    }

    override fun initViewData() {
        super.initViewData()

        //        tvShow.setText(getDebugTag());
        tvShow.text = value

        LogUtils.w("--- " + value!!)

        button.setOnClickListener { startActivityRootFragment(TestFragment(), true) }


        button1.setOnClickListener { startFragment(TestFragment()) }

        button2.setOnClickListener { v ->

            startFragment(TwoFragment(), 0x111)
        }

        button3.setOnClickListener { v ->

            val navigationFragment = getNavigationFragment()
            navigationFragment?.popToRootFragment()
        }

        button4.setOnClickListener { v ->

            finish()
        }

    }

    override fun preferredStatusBarStyle(): BarStyle {
        return BarStyle.DarkContent
    }

    override fun preferredStatusBarColor(): Int {
        return Color.TRANSPARENT
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, result: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, result)
        if (requestCode == 0x111 && resultCode == 0x110) {
            ToastUtils.normal("0x111 " + result!!.getString("msg", "null"))
        }
    }

    companion object {

        fun newInstance(value: String): TestFragment {

            val args = Bundle()
            args.putString("value", value)
            val fragment = TestFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
