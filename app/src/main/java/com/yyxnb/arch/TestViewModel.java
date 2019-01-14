package com.yyxnb.arch;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.yyxnb.yyxarch.base.mvvm.BaseViewModel;
import com.yyxnb.yyxarch.bean.Lcee;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestViewModel extends BaseViewModel<TestRepository> {

    public TestViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<Map<String,String>> reqTeam = new MutableLiveData<>();

    public LiveData<BaseDatas<List<TestData>>> getTeam(){
        return Transformations.switchMap(reqTeam, input -> mRepository.getTeam(input));
    }

    public void reqTeam(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", "李白");
        reqTeam.setValue(map);
    }

}
