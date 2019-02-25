package com.yyxnb.arch;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yyxnb.yyxarch.annotation.LceeStatus;
import com.yyxnb.yyxarch.base.mvvm.BaseMvvmFragment;
import com.yyxnb.yyxarch.utils.ToastUtils;
import com.yyxnb.yyxarch.utils.log.LogUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends BaseMvvmFragment<TestViewModel> {

    private SmartRefreshLayout mSmartRefreshLayout;
    private TextView tvShow;
    private boolean isFirst = true;


    @Override
    public int initLayoutResID() {
        return R.layout.fragment_test;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        LogUtils.INSTANCE.w("initView");

        tvShow = fv(R.id.tvShow);
        mSmartRefreshLayout = fv(R.id.mSmartRefreshLayout);




        mViewModel.reqTeam();
        mViewModel.reqTeam2();
//        getMViewModel().reqTeam();
//        getMViewModel().reqTeam();
//        getMViewModel().reqTeam();
    }

    @Override
    public void onVisible() {
        super.onVisible();
        LogUtils.INSTANCE.w("onVisible");
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        LogUtils.INSTANCE.w("onInVisible");
    }

    @Override
    public void initViewData() {
        super.initViewData();

        mSmartRefreshLayout.setEnableLoadMore(false);

        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mViewModel.reqTeam();
                mViewModel.reqTeam2();
            }
        });

        LogUtils.INSTANCE.w("initViewData");

        tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startContainerActivity("com.y1xian.myapplication.arch.ArchOneFragment");
//                startContainerActivityForResult(1, TestFragment.newInstance());

                Bundle bundle = new Bundle();
                bundle.putString("msg",tvShow.getText().toString());

                startContainerActivity(OneFragment.newInstance(),bundle);
//                startFragment(fragment(OneFragment.class));

                ToastUtils.INSTANCE.normal("666");
            }
        });

//        LogUtils.w(StringExtKt.upperFirstLetter("hello"));

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

//        LiveDataExtKt.toReactiveStream(getMViewModel().getTeam()).doOnNext(baseDataLcee -> {
//            if (baseDataLcee != null) {
//                tvShow.setText(baseDataLcee.getResult().get(0).getContent());
//            }
//        }).subscribe();


//        AsyncExtKt.Async(Deferred.class,t -> )



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
                    mSmartRefreshLayout.finishRefresh();
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
                    mSmartRefreshLayout.finishRefresh();
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

    public static TestFragment newInstance() {

        Bundle args = new Bundle();

        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
