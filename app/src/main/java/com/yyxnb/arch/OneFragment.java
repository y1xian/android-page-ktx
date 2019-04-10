package com.yyxnb.arch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.yyxnb.yyxarch.annotation.LceeStatus;
import com.yyxnb.yyxarch.base.mvvm.BaseMvvmFragment;
import com.yyxnb.yyxarch.utils.ToastUtils;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import org.jetbrains.annotations.NotNull;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends BaseMvvmFragment<TestViewModel> {

    private TextView tvShow;
    private TextView tvShow2;
    private String msg;

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_one;
    }

    @Override
    public void initVariables(@NotNull Bundle bundle) {
        super.initVariables(bundle);
        msg = bundle.getString("msg", "空");
        LogUtils.INSTANCE.w("initVariables msg " + msg);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        tvShow = fv(R.id.tvShow);
        tvShow2 = fv(R.id.tvShow2);

    }

    @Override
    public void initViewData() {
        super.initViewData();
        tvShow.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("hehe", "呵呵哒");
            startFragmentForResult(fragment(TwoFragment.class, bundle), 0x11);

//            startFragment(TwoFragment.class);
//            ToastUtils.INSTANCE.normal( ""+ ActivityStack.INSTANCE.topActivity());
        });


        if (mActivity.getIntent().getExtras() != null){
            msg = mActivity.getIntent().getExtras().getString("msg", "空");
        }


        tvShow.setText("666666  " + msg);

        tvShow2.setOnClickListener(v -> {
//            startFragment(ViewPageFragment.class);

            startFragmentForResult(ViewPageFragment.class, 0x22);
        });

//        LogUtils.INSTANCE.w("initViewData  msg " + msg);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        LogUtils.INSTANCE.e("onResume 11OneFragment");
//    }
//
//    @Override
//    public void onVisible() {
//        super.onVisible();
//        LogUtils.INSTANCE.e("onVisible 11OneFragment");
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        LogUtils.INSTANCE.e("onPause 11OneFragment");
//    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0x22 && resultCode == RESULT_OK) {
//            ToastUtils.INSTANCE.normal("0x22");
//        }
//    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();

        mViewModel.getTeam2().observe(this, baseDataLcee -> {
            switch (baseDataLcee.getStatus()) {
                case LceeStatus.Content:
                    tvShow.setText(baseDataLcee.getData().getResult().get(0).getContent());
                    LogUtils.INSTANCE.i("one Content " + LceeStatus.Content);
                    break;
                case LceeStatus.Empty:
                    LogUtils.INSTANCE.i("one Empty");
                    break;
                case LceeStatus.Error:
                    LogUtils.INSTANCE.i("one Error");
                    break;
                case LceeStatus.Loading:
                    LogUtils.INSTANCE.e("one Loading " + LceeStatus.Loading);
                    break;
            }
        });
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, @NotNull Bundle result) {
        super.onFragmentResult(requestCode, resultCode, result);
        if (requestCode == 0x11 && resultCode == RESULT_OK) {
            ToastUtils.INSTANCE.normal(result.getString("msg", "?"));
        }

        if (requestCode == 0x22 && resultCode == 0x2) {
            ToastUtils.INSTANCE.normal("0x22");
        }

        LogUtils.INSTANCE.w("requestCode " + requestCode + " ,resultCode  " + resultCode);
    }

    public static OneFragment newInstance() {

        Bundle args = new Bundle();

        OneFragment fragment = new OneFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
