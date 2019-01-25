package com.yyxnb.arch;

import android.arch.lifecycle.LiveData;

import com.yyxnb.yyxarch.base.BaseRepository;
import com.yyxnb.yyxarch.livedata.LiveDataObservableAdapter;
import com.yyxnb.yyxarch.utils.RxTransformerUtil;

import java.util.Map;

public class TestRepository extends BaseRepository<api> {

    public LiveData getTeam(Map map){
        return LiveDataObservableAdapter.INSTANCE.fromObservable(getMApiServer().getTeam(map).compose(RxTransformerUtil.schedulersTransformer()));
    }

    public LiveData getTeam2(Map map){
        return LiveDataObservableAdapter.INSTANCE.fromDeferred(getMApiServer().getTeam2(map));
    }
}
