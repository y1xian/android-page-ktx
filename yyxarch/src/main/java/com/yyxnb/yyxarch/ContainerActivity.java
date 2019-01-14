package com.yyxnb.yyxarch;

import android.os.Bundle;
import android.view.WindowManager;

import com.yyxnb.yyxarch.base.CompatActivity;
import com.yyxnb.yyxarch.base.NoFragment;

import static com.yyxnb.yyxarch.common.Config.BUNDLE;
import static com.yyxnb.yyxarch.common.Config.FRAGMENT;


/**
 * Description: 盛装Fragment的一个容器(代理)Activity
 * 普通界面只需要编写Fragment,使用此Activity盛装,这样就不需要每个界面都在AndroidManifest中注册一遍
 *
 * @author : yyx
 * @date ：2018/6/9
 */
public class ContainerActivity extends CompatActivity {

    @Override
    protected int initLayoutResID() {
        return 0;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.initView(savedInstanceState);
        if (null  == savedInstanceState) {

            try {
                if (null  == getIntent()) {
                    throw new RuntimeException("you must provide a page info to display");
                }
                String fragmentName = getIntent().getStringExtra(FRAGMENT);
                if (null  == fragmentName || "".equals(fragmentName)) {
                    throw new IllegalArgumentException("can not find page fragmentName");
                }
                Class<?> fragmentClass = Class.forName(fragmentName);
                NoFragment fragment = (NoFragment) fragmentClass.newInstance();
                Bundle args = getIntent().getBundleExtra(BUNDLE);
                if (null  != args) {
                    fragment.setArguments(args);
                }

                startFragment(fragment);
//                FragmentUtils.add(getSupportFragmentManager(), fragment, getFragmentContainer().getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

//    @Override
//    public void onBackPressed() {
//        Fragment  fragment = getSupportFragmentManager().findFragmentById(getFragmentContainer().getId());
//        if (fragment instanceof BaseFragment) {
//            if (!((BaseFragment) fragment).isBackPressed()) {
//                super.onBackPressed();
//            }
//        } else {
//            super.onBackPressed();
//        }
//    }

}
