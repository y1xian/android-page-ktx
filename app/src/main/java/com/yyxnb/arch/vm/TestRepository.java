package com.yyxnb.arch.vm;

import android.arch.lifecycle.LiveData;

import com.yyxnb.arch.BaseDatas;
import com.yyxnb.arch.TestData;
import com.yyxnb.yyxarch.base.mvvm.BaseRepository;
import com.yyxnb.yyxarch.bean.Lcee;
import com.yyxnb.yyxarch.livedata.LiveDataObservableAdapter;

import java.util.List;
import java.util.Map;

public class TestRepository extends BaseRepository<api> {


    public LiveData<Lcee<BaseDatas<List<TestData>>>> getTeam(Map map){
        return LiveDataObservableAdapter.INSTANCE.fromDeferredLcee(mApi.getTeam(map));
    }

    public LiveData getTest(){
        return mApi.getTest();
    }
}
