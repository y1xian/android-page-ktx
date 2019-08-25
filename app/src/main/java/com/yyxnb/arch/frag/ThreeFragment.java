package com.yyxnb.arch.frag;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.yyxnb.arch.R;
import com.yyxnb.arch.TestData;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.annotation.LceeStatus;
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM;
import com.yyxnb.yyxarch.interfaces.FitsSystemWindows;
import com.yyxnb.yyxarch.interfaces.LayoutResId;
import com.yyxnb.yyxarch.interfaces.BarStyle;
import com.yyxnb.yyxarch.interfaces.StatusBarHidden;
import com.yyxnb.yyxarch.interfaces.StatusBarTranslucent;
import com.yyxnb.yyxarch.utils.StatusBarUtils;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
@LayoutResId(value = R.layout.fragment_three)
@FitsSystemWindows(value = false)
public class ThreeFragment extends BaseFragmentVM<TestViewModel> {

    @BindView(R.id.tvShow)
    TextView tvShow;

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
//        StatusBarUtils.INSTANCE.setStatusBarTranslucent(getWindow(), true,0);
//        StatusBarUtils.INSTANCE.setStatusBarHidden(getWindow(), true);
//        tvShow = findViewById(R.id.tvShow);

    }

    @Override
    public void initViewData() {
        super.initViewData();

        mViewModel.reqTest();
//        mViewModel.getTest2();

        tvShow.setOnClickListener(v -> {

//            startActivityRootFragment(new TestFragment(),true);
            startFragment(new TestFragment());
        });

    }

    @Override
    public void initObservable() {
        super.initObservable();

        mViewModel.getTest().observe(this, baseDataLcee -> {
            switch (baseDataLcee.getStatus()) {
                case LceeStatus.Content:
                    TestData data = baseDataLcee.getData().getResult().get(0);
//                    tvShow.setText(baseDataLcee.getData().getResult().get(0).toString());
                    tvShow.setText(data.getTestInt() + " \n"
                            + data.getTestInt2() + " \n"
                            + data.getTestInt3() + " \n"
                            + data.getTestDouble() + " \n"
                            + data.getTestDouble2() + " \n"
                            + data.getTestDouble3() + " \n"
                            + data.getTestString() + " \n"
                            + data.getTestString2() + " \n"
                            + data.getTestString3() + " \n\n\n\n\n\n" + data.toString());
                    LogUtils.INSTANCE.i("2 Content " + LceeStatus.Content);
                    break;
                case LceeStatus.Empty:
                    LogUtils.INSTANCE.i("2 Empty");
                    break;
                case LceeStatus.Error:
                    tvShow.setText("~~~");
                    LogUtils.INSTANCE.i("2 Error");
                    break;
                case LceeStatus.Loading:
                    LogUtils.INSTANCE.e("2 Loading " + LceeStatus.Loading);
                    break;
            }
        });

//        mViewModel.reqTest.observe(this, baseDataLcee -> {
//
//            if (baseDataLcee.getResult() != null){
//                TestData data = baseDataLcee.getResult().get(0);
//
//                if (data != null) {
//
//                    tvShow.setText(data.toString());
//                }
//            }
//
//
//
//        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.INSTANCE.w("requestCode " + requestCode + " ,resultCode  " + resultCode);
    }

}
