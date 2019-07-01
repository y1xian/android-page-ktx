package com.yyxnb.arch.frag


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.yyxnb.arch.R
import com.yyxnb.arch.vm.TestViewModel
import com.yyxnb.yyxarch.annotation.LceeStatus
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM
import com.yyxnb.yyxarch.utils.ToastUtils
import com.yyxnb.yyxarch.utils.log.LogUtils
import kotlinx.android.synthetic.main.fragment_one.*


/**
 * A simple [Fragment] subclass.
 */
class OneFragment : BaseFragmentVM<TestViewModel>() {


    private var msg: String? = null

    override fun initLayoutResID(): Int {
        return R.layout.fragment_one
    }

    override fun initVariables(bundle: Bundle) {
        super.initVariables(bundle)
        msg = bundle.getString("msg", "空")
        LogUtils.w("initVariables msg " + msg!!)
    }

    override fun initView(savedInstanceState: Bundle?) {


    }

    override fun initViewData() {
        super.initViewData()
        tvShow!!.setOnClickListener { v ->
            val bundle = initArguments()
            bundle.putString("hehe", "呵呵哒")
            //            startFragment(fragment(new TwoFragment(), bundle), 0x666);

            startFragment(TwoFragment(), 0x666)

            //            startFragment(fragment(new TwoFragment(), bundle));


        }


        if (mActivity.intent.extras != null) {
            msg = mActivity.intent.extras!!.getString("msg", "空")
        }


        tvShow!!.text = "666666  " + msg!!

        tvShow2!!.setOnClickListener { v ->

            startFragment(ViewPageFragment())

        }

        textView!!.setOnClickListener { v ->


        }

    }


    override fun initViewObservable() {
        super.initViewObservable()

        mViewModel.team2.observe(this, Observer{ baseDataLcee ->
            when (baseDataLcee.status) {
                LceeStatus.Content -> {
                    tvShow!!.text = baseDataLcee.data!!.result!![0].content
                    LogUtils.i("one Content " + LceeStatus.Content)
                }
                LceeStatus.Empty -> LogUtils.i("one Empty")
                LceeStatus.Error -> LogUtils.i("one Error")
                LceeStatus.Loading -> LogUtils.e("one Loading " + LceeStatus.Loading)
            }
        })
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, result: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, result)
        if (requestCode == 0x666 && resultCode == 0x110) {
            ToastUtils.normal(result!!.getString("msg", "?"))
        }

        LogUtils.w("requestCode $requestCode ,resultCode  $resultCode")
    }

    companion object {


        fun newInstance(): OneFragment {

            val args = Bundle()

            val fragment = OneFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
