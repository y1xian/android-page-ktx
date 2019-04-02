package com.yyxnb.arch;

import android.view.View;

import com.yyxnb.yyxarch.utils.FragmentUtils;
import com.yyxnb.yyxarch.utils.ScreenUtils;
import com.yyxnb.yyxarch.utils.dialog.BaseSheetDialog;

public class TestDialog extends BaseSheetDialog {

    private TestViewModel mViewModel;
    private boolean isFirst = true;

    @Override
    public int initLayoutId() {
        return R.layout.dialog_test_layout;
    }

    @Override
    public void initViews(View view) {

        setDimAmount(0f).setHeight((int) (ScreenUtils.INSTANCE.getScreenHeight() * 0.7f));

        FragmentUtils.add(getChildFragmentManager(), ViewPageFragment.newInstance(), R.id.mFrameLayout);

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
