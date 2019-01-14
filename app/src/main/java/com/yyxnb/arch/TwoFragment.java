package com.yyxnb.arch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.yyxnb.yyxarch.base.NoFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends NoFragment {

    private TextView tvShow;

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_two;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvShow = fv(R.id.tvShow);
        tvShow.setOnClickListener(v -> {
            startFragment(OneFragment.newInstance());
        });
    }

    public static TwoFragment newInstance() {

        Bundle args = new Bundle();

        TwoFragment fragment = new TwoFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
