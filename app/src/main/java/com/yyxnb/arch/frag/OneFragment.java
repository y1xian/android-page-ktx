package com.yyxnb.arch.frag;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.yyxnb.arch.R;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.annotation.LceeStatus;
import com.yyxnb.yyxarch.base.mvvm.BaseMvvmFragment;
import com.yyxnb.yyxarch.utils.ToastUtils;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends BaseMvvmFragment<TestViewModel> {

    private TextView tvShow;
    private TextView tvShow2;
    private TextView textView;
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
        textView = fv(R.id.textView);

    }

    @Override
    public void initViewData() {
        super.initViewData();
        tvShow.setOnClickListener(v -> {
            Bundle bundle = initArguments();
            bundle.putString("hehe", "呵呵哒");
//            startFragment(fragment(new TwoFragment(), bundle), 0x666);

            startFragment(new TwoFragment(),0x666);

//            startFragment(fragment(new TwoFragment(), bundle));


        });


        if (getMActivity().getIntent().getExtras() != null) {
            msg = getMActivity().getIntent().getExtras().getString("msg", "空");
        }


        tvShow.setText("666666  " + msg);

        tvShow2.setOnClickListener(v -> {

            startFragment(new ViewPageFragment());

        });

        textView.setOnClickListener(v -> {


        });

    }


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
    public void onFragmentResult(int requestCode, int resultCode, @Nullable Bundle result) {
        super.onFragmentResult(requestCode, resultCode, result);
        if (requestCode == 0x666 && resultCode == 0x110) {
            ToastUtils.INSTANCE.normal(result.getString("msg", "?"));
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
