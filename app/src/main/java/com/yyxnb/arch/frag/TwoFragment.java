package com.yyxnb.arch.frag;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.TextView;

import com.yyxnb.arch.R;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM;
import com.yyxnb.yyxarch.interfaces.FitsSystemWindows;
import com.yyxnb.yyxarch.interfaces.LayoutResId;
import com.yyxnb.yyxarch.interfaces.BarStyle;
import com.yyxnb.yyxarch.interfaces.StatusBarColor;
import com.yyxnb.yyxarch.interfaces.StatusBarDarkTheme;
import com.yyxnb.yyxarch.interfaces.StatusBarTranslucent;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
@LayoutResId(value = R.layout.fragment_two)
//@StatusBarColor(color = R.color.purple)
@FitsSystemWindows(value = false)
@StatusBarDarkTheme(value = BarStyle.DarkContent)
public class TwoFragment extends BaseFragmentVM<TestViewModel> {

    @BindView(R.id.tvShow)
    TextView tvShow;
    private String hehe = "222";

    @Override
    public void initVariables(@NotNull Bundle bundle) {
        super.initVariables(bundle);
        hehe = bundle.getString("two", "two`222222");
    }

    @Override
    public void initView(Bundle savedInstanceState) {
//        StatusBarUtils.INSTANCE.setStatusBarTranslucent(getWindow(), true,0);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        hehe = getArguments().getString("hehe","");

//        tvShow = findViewById(R.id.tvShow);

    }

    @Override
    public void initViewData() {
        super.initViewData();
        tvShow.setOnClickListener(v -> {
//            startFragment(OneFragment.newInstance());
            Bundle bundle = initArguments();
            bundle.putString("msg", "哈哈哈 " + new Random().nextInt(100));

            finish();
        });

//        setStatusBarColor(Color.TRANSPARENT);
//        setStatusBarStyle(BarStyle.DarkContent);
//        setStatusBarHidden(true);
//        setStatusBarTranslucent(true);

        tvShow.setText(getDebugTag() + "\n\n" + hehe);

    }

    public static TwoFragment newInstance() {

        Bundle args = new Bundle();
        TwoFragment fragment = new TwoFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
