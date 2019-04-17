package com.yyxnb.arch;

import android.os.Bundle;

import com.yyxnb.arch.frag.ViewPageFragment;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.utils.FragmentUtils;
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

        FragmentUtils.INSTANCE.addFragment(getChildFragmentManager(), ViewPageFragment.newInstance(), R.id.mFrameLayout1);
//        FragmentUtils.add(getChildFragmentManager(), ContentViewFragment.newInstance(), R.id.mFrameLayout1);

//        mViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
//
//
//        isFirst = false;
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
