package com.yyxnb.arch.frag;


import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.yyxnb.arch.R;
import com.yyxnb.arch.TestData;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.annotation.LceeStatus;
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM;
import com.yyxnb.yyxarch.utils.BarStyle;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThreeFragment extends BaseFragmentVM<TestViewModel> {

    private ImageView ivHead;
    private TextView tvShow;

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_three;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        ivHead = fv(R.id.ivHead);
        tvShow = fv(R.id.tvShow);

    }

    @Override
    public void initViewData() {
        super.initViewData();


        ivHead.setOnClickListener(v -> mViewModel.reqTest());

//        mViewModel.reqTeam();
//        mViewModel.reqTeam2();
        mViewModel.reqTest();

        tvShow.setOnClickListener(v -> {

//            startActivityRootFragment(new TestFragment(),true);
//            tvShow.setText("突突突突突突");
            Bundle bundle = initArguments();
            bundle.putString("value",tvShow.getText().toString());
//            startFragment(TestFragment.newInstance(tvShow.getText().toString()));
            startFragment(fragment(new TestFragment(),bundle));
        });

    }

    @Override
    public int preferredStatusBarColor() {
        return Color.TRANSPARENT;
    }


    @NotNull
    @Override
    public BarStyle preferredStatusBarStyle() {
        return BarStyle.DarkContent;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        mViewModel.getTeam().observe(this, baseDataLcee -> {
            switch (baseDataLcee.getStatus()) {
                case LceeStatus.Content:
                    tvShow.setText(baseDataLcee.getData().getResult().get(0).getContent());
                    LogUtils.INSTANCE.i("1 Content " + LceeStatus.Content);
                    break;
                case LceeStatus.Empty:
                    LogUtils.INSTANCE.i("1 Empty");
                    break;
                case LceeStatus.Error:
                    LogUtils.INSTANCE.i("1 Error");
                    break;
                case LceeStatus.Loading:
                    LogUtils.INSTANCE.e("1 Loading " + LceeStatus.Loading);
                    break;
            }
        });

        mViewModel.getTeam2().observe(this, baseDataLcee -> {
            switch (baseDataLcee.getStatus()) {
                case LceeStatus.Content:
                    tvShow.setText(baseDataLcee.getData().getResult().get(0).getContent());
                    LogUtils.INSTANCE.i("2 Content " + LceeStatus.Content);
                    break;
                case LceeStatus.Empty:
                    LogUtils.INSTANCE.i("2 Empty");
                    break;
                case LceeStatus.Error:
                    LogUtils.INSTANCE.i("2 Error");
                    break;
                case LceeStatus.Loading:
                    LogUtils.INSTANCE.e("2 Loading " + LceeStatus.Loading);
                    break;
            }
        });

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
        mViewModel.getTest().observe(this, baseDataLcee -> {

            if (200 == baseDataLcee.getCode()){

                TestData data = baseDataLcee.getResult().get(0);
                if (data != null) {

                    tvShow.setText(data.getTestInt() + " \n"
                            + data.getTestInt2() + " \n"
                            + data.getTestInt3() + " \n"
                            + data.getTestDouble() + " \n"
                            + data.getTestDouble2() + " \n"
                            + data.getTestDouble3() + " \n"
                            + data.getTestString() + " \n"
                            + data.getTestString2() + " \n"
                            + data.getTestString3() + " \n");
                }
                LogUtils.INSTANCE.i("Test Content " + LceeStatus.Content);
            }

        });
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, @NotNull Bundle result) {
        super.onFragmentResult(requestCode, resultCode, result);
//        if (requestCode == 0x11 && resultCode == RESULT_OK) {
//            ToastUtils.INSTANCE.normal(result.getString("msg", "?"));
//        }

        LogUtils.INSTANCE.w("requestCode " + requestCode + " ,resultCode  " + resultCode);
    }

}
