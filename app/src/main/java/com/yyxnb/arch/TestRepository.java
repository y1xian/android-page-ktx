package com.yyxnb.arch;

import android.arch.lifecycle.LiveData;

import com.yyxnb.yyxarch.base.BaseRepository;
import com.yyxnb.yyxarch.livedata.LiveDataObservableAdapter;

import java.util.Map;

public class TestRepository extends BaseRepository<api> {

    public LiveData getTeam(Map map){
        return LiveDataObservableAdapter.fromObservable(mApiServer.getTeam(map));
    }
}
