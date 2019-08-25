package com.yyxnb.arch.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.yyxnb.arch.BaseDatas;
import com.yyxnb.arch.TestData;
import com.yyxnb.yyxarch.base.mvvm.BaseViewModel;
import com.yyxnb.yyxarch.bean.Lcee;
import com.yyxnb.yyxarch.livedata.SingleLiveEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestViewModel extends BaseViewModel {

    private TestRepository mRepository = new TestRepository();

    private SingleLiveEvent<Map<String, String>> reqTeam = new SingleLiveEvent();

    public MutableLiveData<BaseDatas<List<TestData>>> reqTest = new MutableLiveData();
//

    public void getTest2() {
//        return Transformations.switchMap(reqTeam, input -> mRepository.getTeam(input));

        reqTest.postValue(mRepository.getTest2().getValue());
    }
//
    public LiveData<Lcee<BaseDatas<List<TestData>>>> getTest(){
        return Transformations.switchMap(reqTeam, input -> mRepository.getTest());
    }


//
    public void reqTest(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", "李白");
        reqTeam.postValue(map);
    }

}
