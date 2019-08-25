package com.yyxnb.arch.vm;

import android.arch.lifecycle.LiveData;

import com.yyxnb.arch.BaseDatas;
import com.yyxnb.arch.TestData;
import com.yyxnb.yyxarch.base.mvvm.BaseRepository;
import com.yyxnb.yyxarch.bean.Lcee;
import com.yyxnb.yyxarch.http.RetrofitManager;
import com.yyxnb.yyxarch.livedata.LiveDataObservableAdapter;

import java.util.List;
import java.util.Map;

public class TestRepository  {

    private api mApi = RetrofitManager.INSTANCE.createApi(api.class);

    public LiveData<Lcee<BaseDatas<List<TestData>>>> getTest(){
        return LiveDataObservableAdapter.INSTANCE.fromDeferredLcee(mApi.getTest());
    }

    public LiveData<BaseDatas<List<TestData>>> getTest2(){
        return mApi.getTest2();
    }

}
