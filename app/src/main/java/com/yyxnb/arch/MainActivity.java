package com.yyxnb.arch;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yanzhenjie.permission.AndPermission;
import com.yyxnb.yyxarch.utils.FragmentUtils;

public class MainActivity extends AppCompatActivity {

    String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentUtils.add(getSupportFragmentManager(),TestFragment.newInstance(),R.id.mFrameLayout);

        AndPermission.with(this)
                .runtime()
                .permission(permission)
                .start();
//        Observable.interval(1, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                //AutoDispose的使用就是这句
////                .as(AutoDispose.<Long>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        Log.i("接收数据,当前线程"+Thread.currentThread().getName(), String.valueOf(aLong));
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

    }
}
