package com.yyxnb.arch.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.yyxnb.arch.BaseDatas;
import com.yyxnb.arch.TestData;
import com.yyxnb.yyxarch.base.mvvm.BaseViewModel;
import com.yyxnb.yyxarch.bean.Lcee;
import com.yyxnb.yyxarch.livedata.SingleLiveEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestViewModel extends BaseViewModel<TestRepository> {

    private SingleLiveEvent<Map<String,String>> reqTeam = new SingleLiveEvent();
    private SingleLiveEvent<Map<String,String>> reqTeam2 = new SingleLiveEvent();
    private SingleLiveEvent<Map<String,String>> reqTest = new SingleLiveEvent();

    public LiveData<Lcee<BaseDatas<List<TestData>>>> getTeam(){
        return Transformations.switchMap(reqTeam, input -> mRepository.getTeam(input));
    }

    public LiveData<Lcee<BaseDatas<List<TestData>>>> getTeam2(){
        return Transformations.switchMap(reqTeam2, input -> mRepository.getTeam2(input));
    }

    public LiveData<Lcee<BaseDatas<List<TestData>>>> getTest(){
        return Transformations.switchMap(reqTest, input -> mRepository.getTest(input));
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
   public void reqTest(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", "杜甫");
       reqTest.postValue(map);
    }

}
