package com.yyxnb.arch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.yyxnb.yyxarch.base.BaseFragment;
import com.yyxnb.yyxarch.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends BaseFragment {

    private TextView tvShow;
    private String msg;

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_one;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tvShow = fv(R.id.tvShow);
        tvShow.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("hehe", "呵呵哒");
            startFragmentForResult(fragment(TwoFragment.class,bundle),0x11);
//            startFragment(TwoFragment.class);
        });

        msg = getArguments().getString("msg");
        tvShow.setText(msg);

//        RxHttpUtils.Companion.uploadImg("", "")
//                .compose(RxTransformerUtil.INSTANCE.<ResponseBody>switchSchedulers())
//                .subscribe(new CommonObserver<ResponseBody>() {
//                    @Override
//                    protected void onError(String errorMsg) {
//                        Log.e("allen", "上传失败: " + errorMsg);
//                    }
//
//                    @Override
//                    protected void onSuccess(ResponseBody responseBody) {
//                        try {
//                            Log.e("allen", "上传完毕: " + responseBody.string());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, @NotNull Bundle result) {
        super.onFragmentResult(requestCode, resultCode, result);
        if (requestCode == 0x11 && resultCode == RESULT_OK){
            ToastUtils.INSTANCE.normal(result.getString("msg","?"));
        }
    }

    public static OneFragment newInstance() {

        Bundle args = new Bundle();

        OneFragment fragment = new OneFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
