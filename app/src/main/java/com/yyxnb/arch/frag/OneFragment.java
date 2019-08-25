package com.yyxnb.arch.frag;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.yyxnb.arch.R;
import com.yyxnb.arch.TestDialog;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.base.BaseFragment;
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM;
import com.yyxnb.yyxarch.interfaces.BarStyle;
import com.yyxnb.yyxarch.interfaces.LayoutResId;
import com.yyxnb.yyxarch.interfaces.StatusBarDarkTheme;
import com.yyxnb.yyxarch.interfaces.StatusBarTranslucent;
import com.yyxnb.yyxarch.utils.ToastUtils;
import com.yyxnb.yyxarch.utils.log.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
@LayoutResId(value = R.layout.fragment_one)
@StatusBarDarkTheme(value = BarStyle.DarkContent)
public class OneFragment extends BaseFragment {

    @BindView(R.id.tvShow)
    TextView tvShow;
    @BindView(R.id.tvShow2)
    TextView tvShow2;
    @BindView(R.id.textView)
    TextView textView;
    private String msg;

    @Override
    public void initVariables(@NotNull Bundle bundle) {
        super.initVariables(bundle);
        msg = bundle.getString("msg", "空");
        LogUtils.INSTANCE.w("initVariables msg " + msg);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
//        tvShow = findViewById(R.id.tvShow);
//        tvShow2 = findViewById(R.id.tvShow2);
//        textView = findViewById(R.id.textView);

    }

    @Override
    public void initViewData() {
        super.initViewData();
        tvShow.setOnClickListener(v -> {
            Bundle bundle = initArguments();
            bundle.putString("two", "呵呵哒");
            startFragment(fragment(new TwoFragment(), bundle));

//            startFragment(new TwoFragment());

//            startFragment(fragment(new TwoFragment(), bundle));

        });


        if (getMActivity().getIntent().getExtras() != null) {
            msg = getMActivity().getIntent().getExtras().getString("msg", "空");
        }


        tvShow.setText("666666  " + msg);

        tvShow2.setOnClickListener(v -> {

            startFragment(new ViewPageFragment());

        });

        textView.setOnClickListener(v -> {

            TestDialog dialog = new TestDialog();
            dialog.show(getChildFragmentManager());
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    LogUtils.INSTANCE.w("onCancel   " + dialog.toString());
                }
            });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    LogUtils.INSTANCE.w("onDismiss  " + dialog.toString());
                }
            });
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x666 && resultCode == 0x110) {
            ToastUtils.INSTANCE.normal(data.getExtras().getString("msg", "?"));
        }

        LogUtils.INSTANCE.w("requestCode " + requestCode + " ,resultCode  " + resultCode);
    }


    public static OneFragment newInstance() {

        Bundle args = new Bundle();

        OneFragment fragment = new OneFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
