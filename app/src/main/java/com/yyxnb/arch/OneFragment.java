package com.yyxnb.arch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;

import com.yyxnb.yyxarch.base.BaseFragment;
import com.yyxnb.yyxarch.http.RxHttpUtils;
import com.yyxnb.yyxarch.http.observer.CommonObserver;
import com.yyxnb.yyxarch.utils.RxTransformerUtil;

import java.io.IOException;

import okhttp3.ResponseBody;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends BaseFragment {

    private TextView tvShow;
    @Override
    public int initLayoutResID() {
        return R.layout.fragment_one;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvShow = fv(R.id.tvShow);
        tvShow.setOnClickListener(v -> {
//            startFragment(TwoFragment.newInstance());
        });

        RxHttpUtils.Companion.uploadImg("", "")
                .compose(RxTransformerUtil.<ResponseBody>switchSchedulers())
                .subscribe(new CommonObserver<ResponseBody>() {
                    @Override
                    protected void onError(String errorMsg) {
                        Log.e("allen", "上传失败: " + errorMsg);
                    }

                    @Override
                    protected void onSuccess(ResponseBody responseBody) {
                        try {
                            Log.e("allen", "上传完毕: " + responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public static OneFragment newInstance() {

        Bundle args = new Bundle();

        OneFragment fragment = new OneFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
