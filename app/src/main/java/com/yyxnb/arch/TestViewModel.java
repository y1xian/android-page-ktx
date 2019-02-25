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
    private MutableLiveData<Map<String,String>> reqTeam2 = new MutableLiveData<>();

    public LiveData<Lcee<BaseDatas<List<TestData>>>> getTeam(){
        return Transformations.switchMap(reqTeam, input -> mRepository.getTeam(input));
    }

    public LiveData<Lcee<BaseDatas<List<TestData>>>> getTeam2(){
        return Transformations.switchMap(reqTeam2, input -> mRepository.getTeam2(input));
    }


    public void reqTeam(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", "李白");
        reqTeam.postValue(map);
    }
    public void reqTeam2(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", "杜甫");
        reqTeam2.postValue(map);
    }

}
