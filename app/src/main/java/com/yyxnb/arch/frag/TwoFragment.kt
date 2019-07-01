package com.yyxnb.arch.frag


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.yyxnb.arch.R
import com.yyxnb.arch.vm.TestViewModel
import com.yyxnb.yyxarch.annotation.LceeStatus
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM
import com.yyxnb.yyxarch.utils.BarStyle
import com.yyxnb.yyxarch.utils.log.LogUtils
import kotlinx.android.synthetic.main.fragment_two.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class TwoFragment : BaseFragmentVM<TestViewModel>() {


    private var hehe = "222"

    override fun initLayoutResID(): Int {
        return R.layout.fragment_two
    }

    override fun initVariables(bundle: Bundle) {
        super.initVariables(bundle)
        hehe = bundle.getString("hehe", "222222")
    }

    override fun initView(savedInstanceState: Bundle?) {

        //        hehe = getArguments().getString("hehe","");


    }

    override fun initViewData() {
        super.initViewData()
        tvShow.setOnClickListener { v ->
            //            startFragment(OneFragment.newInstance());
            val bundle = initArguments()
            bundle.putString("msg", "哈哈哈 " + Random().nextInt(100))
            setResult(0x110, bundle)

            finish()
        }

        //        setStatusBarColor(Color.TRANSPARENT);
        //        setStatusBarStyle(BarStyle.DarkContent);
        //        setStatusBarHidden(true);
        //        setStatusBarTranslucent(true);

        tvShow.text = getDebugTag()

    }

    override fun preferredStatusBarColor(): Int {
        return Color.TRANSPARENT
    }

    override fun preferredStatusBarStyle(): BarStyle {
        return BarStyle.LightContent
    }


    override fun isSwipeBackEnabled(): Boolean {
        return true
    }

    override fun initViewObservable() {
        super.initViewObservable()

        mViewModel.team2.observe(this, Observer { baseDataLcee ->
            when (baseDataLcee.status) {
                LceeStatus.Content -> {
                    tvShow!!.text = baseDataLcee.data!!.result!![0].content
                    LogUtils.i("two Content " + LceeStatus.Content)
                }
                LceeStatus.Empty -> LogUtils.i("two Empty")
                LceeStatus.Error -> LogUtils.i("two Error")
                LceeStatus.Loading -> LogUtils.e("two Loading " + LceeStatus.Loading)
            }
        })

    }

    companion object {

        fun newInstance(): TwoFragment {

            val args = Bundle()
            val fragment = TwoFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
