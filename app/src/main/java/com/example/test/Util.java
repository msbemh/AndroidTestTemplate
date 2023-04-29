package com.example.test;

import com.example.test.models.MyCalendar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Util {
    public static final String ACTION_MUSIC_RESUME = "ACTION_MUSIC_RESUME";
    public static final String ACTION_MUSIC_NEXT = "ACTION_MUSIC_NEXT";
    public static final String ACTION_MUSIC_PREV = "ACTION_MUSIC_PREV";
    public static final int NOTIFICATION_ID = 1;
    public static Map<LocalDate, List<MyCalendar>> generateCalendars(){
        YearMonth currentMonth = YearMonth.now();
        List<MyCalendar> calendarList = new ArrayList<MyCalendar>();
        calendarList.add(new MyCalendar("title1", "할일1", "설명1", currentMonth.atDay(17), true, R.color.blue_800));
        calendarList.add(new MyCalendar("title2", "할일2", "설명2", currentMonth.atDay(17), false, R.color.red_800));
        calendarList.add(new MyCalendar("title3", "할일3", "설명3", currentMonth.atDay(22), true, R.color.brown_700));
        calendarList.add(new MyCalendar("title4title4", "할일4", "설명4", currentMonth.atDay(3), true, R.color.blue_grey_700));
        calendarList.add(new MyCalendar("title5title5", "할일5", "설명5", currentMonth.atDay(12), false, R.color.teal_700));
        calendarList.add(new MyCalendar("title6title6title6", "할일6", "설명6", currentMonth.atDay(1), false, R.color.cyan_700));
        calendarList.add(new MyCalendar("title7title7title7", "할일7", "설명7", currentMonth.atDay(1), true, R.color.pink_700));
        calendarList.add(new MyCalendar("title8title8title8", "할일8", "설명8", currentMonth.atDay(1), false, R.color.blue_grey_700));
        Map<LocalDate, List<MyCalendar>> calendarGroupMap = calendarList.stream().collect(Collectors.groupingBy(MyCalendar::getLocalDate));
        return calendarGroupMap;
    }
}
