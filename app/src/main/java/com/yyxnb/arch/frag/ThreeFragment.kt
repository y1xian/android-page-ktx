package com.yyxnb.arch.frag


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.yyxnb.arch.R
import com.yyxnb.arch.vm.Test1ViewModel
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM
import com.yyxnb.yyxarch.ext.clickDelay
import com.yyxnb.yyxarch.ext.withState
import com.yyxnb.yyxarch.utils.BarStyle
import com.yyxnb.yyxarch.utils.ToastUtils
import com.yyxnb.yyxarch.utils.log.LogUtils
import kotlinx.android.synthetic.main.fragment_three.*

/**
 * A simple [Fragment] subclass.
 */
class ThreeFragment : BaseFragmentVM<Test1ViewModel>() {


    override fun initLayoutResID(): Int {
        return R.layout.fragment_three
    }

    override fun initView(savedInstanceState: Bundle?) {


    }

    override fun initViewData() {
        super.initViewData()


//        ivHead!!.setOnClickListener { v -> mViewModel.reqTest() }
        ivHead.clickDelay {
            ToastUtils.normal("请求")
            mViewModel.getTest()
        }

        //        mViewModel.reqTeam();
//        mViewModel.reqTest()

        tvShow!!.setOnClickListener { v ->

            //            startActivityRootFragment(new TestFragment(),true);
            //            tvShow.setText("突突突突突突");
            val bundle = initArguments()
            bundle.putString("value", tvShow!!.text.toString())
            //            startFragment(TestFragment.newInstance(tvShow.getText().toString()));
            startFragment(fragment(TestFragment(), bundle))
        }

    }

    override fun preferredStatusBarColor(): Int {
        return Color.TRANSPARENT
    }


    override fun preferredStatusBarStyle(): BarStyle {
        return BarStyle.DarkContent
    }

    override fun initViewObservable() {
        super.initViewObservable()

//        mViewModel.state.observe(this, androidx.lifecycle.Observer {
        withState(mViewModel) { data ->
            LogUtils.e("333 ${data.isLoading} , ${data.data.toString()}")

            if (data.data != null) {
                val d = data.data.result?.get(0)
                tvShow.text = d!!.testInt.toString()
            }
        }
//        })

//        mViewModel.team.observe(this, Observer{ baseDataLcee ->
//            when (baseDataLcee.status) {
//                LceeStatus.Content -> {
//                    tvShow!!.text = baseDataLcee.data!!.result!![0].content
//                    LogUtils.i("1 Content " + LceeStatus.Content)
//                }
//                LceeStatus.Empty -> LogUtils.i("1 Empty")
//                LceeStatus.Error -> LogUtils.i("1 Error")
//                LceeStatus.Loading -> LogUtils.e("1 Loading " + LceeStatus.Loading)
//            }
//        })
//
//        mViewModel.team2.observe(this, Observer{ baseDataLcee ->
//            when (baseDataLcee.status) {
//                LceeStatus.Content -> {
//                    tvShow.text = baseDataLcee.data!!.result!![0].content
//                    LogUtils.i("2 Content " + LceeStatus.Content)
//                }
//                LceeStatus.Empty -> LogUtils.i("2 Empty")
//                LceeStatus.Error -> LogUtils.i("2 Error")
//                LceeStatus.Loading -> LogUtils.e("2 Loading " + LceeStatus.Loading)
//            }
//        })

        //        mViewModel.getTest().observe(this, baseDataLcee -> {
        //            switch (baseDataLcee.getStatus()) {
        //                case LceeStatus.Content:
        //                    TestData data = baseDataLcee.getData().getResult().get(0);
        //                    if (data != null) {
        //
        //                        tvShow.setText(data.getTestInt() + " \n"
        //                                + data.getTestInt2() + " \n"
        //                                + data.getTestInt3() + " \n"
        //                                + data.getTestDouble() + " \n"
        //                                + data.getTestDouble2() + " \n"
        //                                + data.getTestDouble3() + " \n"
        //                                + data.getTestString() + " \n"
        //                                + data.getTestString2() + " \n"
        //                                + data.getTestString3() + " \n");
        //                    }
        //                    LogUtils.INSTANCE.i("Test Content " + LceeStatus.Content);
        //                    break;
        //                case LceeStatus.Empty:
        //                    LogUtils.INSTANCE.i("Test Empty");
        //                    break;
        //                case LceeStatus.Error:
        //                    LogUtils.INSTANCE.i("Test Error");
        //                    break;
        //                case LceeStatus.Loading:
        //                    LogUtils.INSTANCE.e("Test Loading " + LceeStatus.Loading);
        //                    break;
        //            }
        //        });
//        mViewModel.test.observe(this, Observer{ baseDataLcee ->
//
//            if (200 == baseDataLcee.code) {
//
//                val data = baseDataLcee.result!![0]
//                if (data != null) {
//
//                    tvShow!!.text = (data.testInt.toString() + " \n"
//                            + data.testInt2 + " \n"
//                            + data.testInt3 + " \n"
//                            + data.testDouble + " \n"
//                            + data.testDouble2 + " \n"
//                            + data.testDouble3 + " \n"
//                            + data.testString + " \n"
//                            + data.testString2 + " \n"
//                            + data.testString3 + " \n")
//                }
//                LogUtils.i("Test Content " + LceeStatus.Content)
//            }
//
//        })
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, result: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, result)
        //        if (requestCode == 0x11 && resultCode == RESULT_OK) {
        //            ToastUtils.INSTANCE.normal(result.getString("msg", "?"));
        //        }

        LogUtils.w("requestCode $requestCode ,resultCode  $resultCode")
    }

}
