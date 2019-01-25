package com.yyxnb.arch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yyxnb.yyxarch.utils.FragmentUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentUtils.add(getSupportFragmentManager(),TestFragment.newInstance(),R.id.mFrameLayout);

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
