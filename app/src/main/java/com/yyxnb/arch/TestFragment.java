package com.yyxnb.arch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.yyxnb.yyxarch.base.mvvm.BaseMvvmFragment;
import com.yyxnb.yyxarch.utils.ToastUtils;


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

        getMViewModel().reqTeam();
        getMViewModel().reqTeam();
//        getMViewModel().reqTeam();
//        getMViewModel().reqTeam();
//        getMViewModel().reqTeam();
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

                ToastUtils.INSTANCE.normal("666");
            }
        });

//        Map<String, String> map = new LinkedHashMap<>();
//        map.put("name", "李白");
//        RxHttpUtils.createApi(api.class).getTeam(map)
//                .compose(RxTransformerUtil.schedulersTransformer())
//                .subscribe(new CommonObserver<TestData>() {
//                    @Override
//                    protected void onError(String errorMsg) {
//                        LogUtils.e(errorMsg);
//                    }
//
//                    @Override
//                    protected void onSuccess(TestData testData) {
//                        LogUtils.w(testData.getContent());
//                    }
//                });



//        TestDialog dialog = new TestDialog();
//        dialog.show(getFragmentManager(),dialog.getTag());

//        if (isFirst){
//            isFirst = false;
//            mViewModel.reqTeam();
//        }



//        LiveDataExtKt.toReactiveStream(getMViewModel().getTeam(), RxSchedulers.INSTANCE.getUi())
//                .doOnNext(new Consumer<Lcee<BaseDatas<List<TestData>>>>() {
//                    @Override
//                    public void accept(Lcee<BaseDatas<List<TestData>>> baseDataLcee) throws Exception {
//                        switch (baseDataLcee.getStatus()) {
//                            case LceeStatus.Content:
//                                tvShow.setText(baseDataLcee.getData().getResult().get(0).getContent());
//                                LogUtils.i("Content " + LceeStatus.Content);
//                                break;
//                            case LceeStatus.Empty:
//                                LogUtils.i("Empty");
//                                break;
//                            case LceeStatus.Error:
//                                LogUtils.i("Error");
//                                break;
//                            case LceeStatus.Loading:
//                                LogUtils.i("Loading " + LceeStatus.Loading);
//                                break;
//                        }
//                    }
//                }).subscribe();




    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        getMViewModel().getTeam().observe(this,baseDataLcee -> {
            if (baseDataLcee != null){
                tvShow.setText(baseDataLcee.getResult().get(0).getContent());
            }
        });

//        getMViewModel().getTeam().observe(this, baseDataLcee -> {
//            switch (baseDataLcee.getStatus()) {
//                case LceeStatus.Content:
//                    tvShow.setText(baseDataLcee.getData().getResult().get(0).getContent());
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
