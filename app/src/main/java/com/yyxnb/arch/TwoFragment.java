package com.yyxnb.arch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.yyxnb.yyxarch.annotation.LceeStatus;
import com.yyxnb.yyxarch.base.mvvm.BaseMvvmFragment;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends BaseMvvmFragment<TestViewModel> {

    private TextView tvShow;
    private String hehe;

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_two;
    }

    @Override
    public void initVariables(@NotNull Bundle bundle) {
        super.initVariables(bundle);
        hehe = bundle.getString("hehe");
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        tvShow = fv(R.id.tvShow);

    }

    @Override
    public void initViewData() {
        super.initViewData();
        tvShow.setOnClickListener(v -> {
//            startFragment(OneFragment.newInstance());
            Bundle bundle = new Bundle();
            bundle.putString("msg", "哈哈哈 " + new Random().nextInt(100));
            setResult(RESULT_OK, bundle);
            finish();
        });

        tvShow.setText(hehe);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();

        mViewModel.getTeam2().observe(this, baseDataLcee -> {
            switch (baseDataLcee.getStatus()) {
                case LceeStatus.Content:
                    tvShow.setText(baseDataLcee.getData().getResult().get(0).getContent());
                    LogUtils.INSTANCE.i("two Content " + LceeStatus.Content);
                    break;
                case LceeStatus.Empty:
                    LogUtils.INSTANCE.i("two Empty");
                    break;
                case LceeStatus.Error:
                    LogUtils.INSTANCE.i("two Error");
                    break;
                case LceeStatus.Loading:
                    LogUtils.INSTANCE.e("two Loading " + LceeStatus.Loading);
                    break;
            }
        });
    }

    public static TwoFragment newInstance() {

        Bundle args = new Bundle();
        TwoFragment fragment = new TwoFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
