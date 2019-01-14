package com.yyxnb.arch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.yyxnb.yyxarch.annotation.LceeStatus;
import com.yyxnb.yyxarch.base.mvvm.BaseMvvmFragment;
import com.yyxnb.yyxarch.utils.log.LogUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends BaseMvvmFragment<TestViewModel> {

    private TextView tvShow;
    private boolean isFirst = true;

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_test;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvShow = fv(R.id.tvShow);
    }

    @Override
    public void initViewData(Bundle savedInstanceState) {
        super.initViewData(savedInstanceState);
        tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startContainerActivity("com.y1xian.myapplication.arch.ArchOneFragment");
//                startContainerActivityForResult(1, TestFragment.newInstance());

//                startContainerActivity(OneFragment.newInstance());
            }
        });

        TestDialog dialog = new TestDialog();
        dialog.show(getFragmentManager(),dialog.getTag());

//        if (isFirst){
//            isFirst = false;
//            mViewModel.reqTeam();
//        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        mViewModel.getTeam().observe(this,baseDataLcee -> {
            if (baseDataLcee != null){
                tvShow.setText(baseDataLcee.getResult().get(0).getContent());
            }
        });

//        mViewModel.getTeam().observe(this,baseDataLcee -> {
//            switch (baseDataLcee.status) {
//                case LceeStatus.Content:
//                    tvShow.setText(baseDataLcee.data.getResult().get(0).getContent());
//                    LogUtils.i("Content " + LceeStatus.Content);
//                    break;
//                case LceeStatus.Empty:
//                    LogUtils.i("Empty");
//                    break;
//                case LceeStatus.Error:
//                    LogUtils.i("Error");
//                    break;
//                case LceeStatus.Loading:
//                    LogUtils.i("Loading " + LceeStatus.Loading);
//                    break;
//            }
//        });
    }

    public static TestFragment newInstance() {

        Bundle args = new Bundle();

        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
