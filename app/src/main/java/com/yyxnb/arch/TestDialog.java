package com.yyxnb.arch;

import android.os.Bundle;

import com.yyxnb.arch.frag.ThreeFragment;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.utils.ScreenUtils;
import com.yyxnb.yyxarch.utils.dialog.BaseSheetDialog;

public class TestDialog extends BaseSheetDialog {

    private TestViewModel mViewModel;

    @Override
    public int initLayoutResID() {
        return R.layout.dialog_test_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        setDimAmount(0.5f).setHeight((int) (ScreenUtils.INSTANCE.getScreenHeight() * 0.7f));

//        FragmentHelper.INSTANCE.addFragment(getChildFragmentManager(), R.id.mFrameLayout1, OneFragment.newInstance());
//        FragmentHelper.INSTANCE.addFragment(getChildFragmentManager(), R.id.mFrameLayout1, ContentViewFragment.newInstance());
//        FragmentHelper.INSTANCE.addFragment(getChildFragmentManager(), R.id.mFrameLayout1, TwoFragment.newInstance());
//        FragmentHelper.INSTANCE.addFragment(getChildFragmentManager(), R.id.mFrameLayout1, TestFragment.newInstance());
//        FragmentHelper.INSTANCE.addFragment(getChildFragmentManager(), R.id.mFrameLayout1, new ThreeFragment());

//        mViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
//        mViewModel.reqTeam();
//
//        TextView tvShow = view.findViewById(R.id.tvShow);
//
//        StatusLayoutManager statusLayoutManager = new StatusLayoutManager.Builder(tvShow)
//                .setDefaultLayoutsBackgroundColor(Color.TRANSPARENT)
//                .build();
////        statusLayoutManager.showLoadingLayout();
////        mViewModel.getTeam().observeForever(baseDataLcee -> {
////            if (baseDataLcee != null){
////                tvShow.setText(baseDataLcee.getResult().get(0).getContent());
////                statusLayoutManager.showSuccessLayout();
////            }
////        });
//        mViewModel.getTeam().observeForever(baseDataLcee -> {
//            switch (baseDataLcee.getStatus()) {
//                case LceeStatus.Content:
//                    statusLayoutManager.showSuccessLayout();
//                    tvShow.setText(baseDataLcee.getData().getResult().get(0).getContent());
//                    LogUtils.INSTANCE.i("Content " + LceeStatus.Content);
//                    break;
//                case LceeStatus.Empty:
//                    statusLayoutManager.showEmptyLayout();
//                    LogUtils.INSTANCE.i("Empty");
//                    break;
//                case LceeStatus.Error:
//                    statusLayoutManager.showErrorLayout();
//                    LogUtils.INSTANCE.i("Error");
//                    break;
//                case LceeStatus.Loading:
//                    statusLayoutManager.showLoadingLayout();
//                    LogUtils.INSTANCE.i("Loading " + LceeStatus.Loading);
//                    break;
//            }
//        });

    }
}
