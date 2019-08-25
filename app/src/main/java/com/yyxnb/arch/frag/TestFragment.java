package com.yyxnb.arch.frag;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yyxnb.arch.R;
import com.yyxnb.arch.TestActivity;
import com.yyxnb.yyxarch.base.BaseFragment;
import com.yyxnb.yyxarch.interfaces.LayoutResId;
import com.yyxnb.yyxarch.interfaces.StatusBarColor;
import com.yyxnb.yyxarch.interfaces.StatusBarDarkTheme;
import com.yyxnb.yyxarch.interfaces.BarStyle;
import com.yyxnb.yyxarch.utils.ToastUtils;

import org.jetbrains.annotations.Nullable;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
@LayoutResId(value = R.layout.fragment_test)
@StatusBarColor(color = R.color.orange)
@StatusBarDarkTheme(value = BarStyle.LightContent)
public class TestFragment extends BaseFragment {


    @BindView(R.id.tvShow)
    TextView tvShow;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.button4)
    Button button4;



    @Override
    public void initView(Bundle savedInstanceState) {

//        tvShow = findViewById(R.id.tvShow);
//        button = findViewById(R.id.button);
//        button1 = findViewById(R.id.button1);
//        button2 = findViewById(R.id.button2);
//        button3 = findViewById(R.id.button3);
//        button4 = findViewById(R.id.button4);

    }

    @Override
    public void initViewData() {
        super.initViewData();

        tvShow.setText(getDebugTag());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(TestActivity.class);
            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startFragment(new TestFragment());

            }
        });

        button2.setOnClickListener(v -> {

            startFragment(new TwoFragment());
        });

        button3.setOnClickListener(v -> {
            startFragment(new ContentViewFragment());
        });

        button4.setOnClickListener(v -> {

            finish();
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x111 && resultCode == 0x110) {
            ToastUtils.INSTANCE.normal("onActivityResult  " + data.getExtras().getString("msg", "null"));
        }
    }

    public static TestFragment newInstance() {

        Bundle args = new Bundle();

        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }
}

