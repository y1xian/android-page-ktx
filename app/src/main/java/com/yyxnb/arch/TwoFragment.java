package com.yyxnb.arch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.yyxnb.yyxarch.base.BaseFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends BaseFragment {

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
            setResult(RESULT_OK,bundle);
            finish();
        });

        tvShow.setText(hehe);
    }

    public static TwoFragment newInstance() {

        Bundle args = new Bundle();
        TwoFragment fragment = new TwoFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}
