package com.yyxnb.arch;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.yyxnb.yyxarch.utils.ScreenUtils;
import com.yyxnb.yyxarch.utils.dialog.BaseSheetDialog;

import me.bakumon.statuslayoutmanager.library.StatusLayoutManager;

public class TestDialog extends BaseSheetDialog<TestDialog> {

    private TestViewModel mViewModel;
    private boolean isFirst = true;

    @Override
    public int initLayoutId() {
        return R.layout.dialog_test_layout;
    }

    @Override
    public void initViews(View view) {

        setDimAmount(0f).setHeight((int) (ScreenUtils.INSTANCE.getScreenHeight() * 0.7f));

        mViewModel = ViewModelProviders.of(this).get(TestViewModel.class);



//        if (isFirst){
            isFirst = false;
            mViewModel.reqTeam();
//        }

        TextView tvShow = view.findViewById(R.id.tvShow);

        StatusLayoutManager statusLayoutManager = new StatusLayoutManager.Builder(tvShow)
                .setDefaultLayoutsBackgroundColor(Color.BLACK)
                .build();
        statusLayoutManager.showLoadingLayout();
//        mViewModel.getTeam().observeForever(baseDataLcee -> {
//            if (baseDataLcee != null){
//                tvShow.setText(baseDataLcee.getResult().get(0).getContent());
//                statusLayoutManager.showSuccessLayout();
//            }
//        });
//        mViewModel.getTeam().observeForever(baseDataLcee -> {
//            switch (baseDataLcee.getStatus()) {
//                case LceeStatus.Content:
//                    statusLayoutManager.showSuccessLayout();
//                    tvShow.setText(baseDataLcee.getData().getResult().get(0).getContent());
//                    LogUtils.i("Content " + LceeStatus.Content);
//                    break;
//                case LceeStatus.Empty:
//                    statusLayoutManager.showEmptyLayout();
//                    LogUtils.i("Empty");
//                    break;
//                case LceeStatus.Error:
//                    statusLayoutManager.showErrorLayout();
//                    LogUtils.i("Error");
//                    break;
//                case LceeStatus.Loading:
//                    statusLayoutManager.showLoadingLayout();
//                    LogUtils.i("Loading " + LceeStatus.Loading);
//                    break;
//            }
//        });

    }
}
