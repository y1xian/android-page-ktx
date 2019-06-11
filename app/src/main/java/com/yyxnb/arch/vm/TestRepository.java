package com.yyxnb.arch.vm;


import androidx.lifecycle.LiveData;

import com.yyxnb.yyxarch.base.BaseRepository;
import com.yyxnb.yyxarch.livedata.LiveDataObservableAdapter;

import java.util.Map;

public class TestRepository extends BaseRepository<api> {


    public LiveData getTeam(Map map){
        return LiveDataObservableAdapter.INSTANCE.fromObservableLcee(mApi.getTeam(map));
    }

    public LiveData getTeam2(Map map){

        return LiveDataObservableAdapter.INSTANCE.fromDeferredLcee(mApi.getTeam2(map));
    }
    public LiveData getTest(){
        return mApi.getTest();
    }
}
