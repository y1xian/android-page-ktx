package com.yyxnb.arch.frag;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.yyxnb.arch.R;
import com.yyxnb.arch.TestDialog;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.annotation.LceeStatus;
import com.yyxnb.yyxarch.base.mvvm.BaseMvvmFragment;
import com.yyxnb.yyxarch.nav.NavigationFragment;
import com.yyxnb.yyxarch.utils.BarStyle;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThreeFragment extends BaseMvvmFragment<TestViewModel> {

    private TextView tvShow;

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_three;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        tvShow = fv(R.id.tvShow);

        //        mViewModel.reqTeam();
        mViewModel.reqTeam2();
    }

    @Override
    public void initViewData() {
        super.initViewData();


        tvShow.setOnClickListener(v -> {
//            startFragment(new TestFragment());

            NavigationFragment navigationFragment = new NavigationFragment();
            navigationFragment.setRootFragment(new TestFragment());
//            presentFragment(navigationFragment,1);

            startFragment(new TestFragment());

        });

        TestDialog dialog = new TestDialog();
        dialog.show(getChildFragmentManager());
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                LogUtils.INSTANCE.w("onCancel   " + dialog.toString());
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LogUtils.INSTANCE.w("onDismiss  " + dialog.toString());
            }
        });
    }

    @Override
    public int preferredStatusBarColor() {
        return Color.BLUE;
    }


//    @Nullable
//    @Override
//    public Integer preferredNavigationBarColor() {
//        return Color.GREEN;
//    }

    @NotNull
    @Override
    public BarStyle preferredStatusBarStyle() {
        return BarStyle.DarkContent;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
//        getMViewModel().getTeam().observe(this,baseDataLcee -> {
//            if (baseDataLcee != null){
//                tvShow.setText(baseDataLcee.getResult().get(0).getContent());
//            }
//        });

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
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, @NotNull Bundle result) {
        super.onFragmentResult(requestCode, resultCode, result);
//        if (requestCode == 0x11 && resultCode == RESULT_OK) {
//            ToastUtils.INSTANCE.normal(result.getString("msg", "?"));
//        }
//
//        if (requestCode == 0x22 && resultCode == 0x2) {
//            ToastUtils.INSTANCE.normal("0x22");
//        }

        LogUtils.INSTANCE.w("requestCode " + requestCode + " ,resultCode  " + resultCode);
    }
}
