package com.example.test.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveDataModel extends ViewModel {
    private MutableLiveData<Map<LocalDate, List<MyCalendar>>> mMapLiveData = new MutableLiveData<>();

    public void setMap(Map<LocalDate, List<MyCalendar>> pMap){
        Map<LocalDate, List<MyCalendar>> map = mMapLiveData.getValue();
        if(map == null) map = new HashMap<LocalDate, List<MyCalendar>>();
        map.putAll(pMap);
        mMapLiveData.setValue(map);
    }

    public void setCalendar(LocalDate localDate, MyCalendar myCalendar){
        Map<LocalDate, List<MyCalendar>> map = mMapLiveData.getValue();
        if(map == null){
            map = new HashMap<LocalDate,List<MyCalendar>>();
        }
        List<MyCalendar> list = map.get(localDate);
        if(list == null) {
            list = new ArrayList<MyCalendar>();
            map.put(localDate, list);
        }
        list.add(myCalendar);
        mMapLiveData.setValue(map);
    }

    public MutableLiveData<Map<LocalDate, List<MyCalendar>>> getLiveData(){
        return mMapLiveData;
    }
}
