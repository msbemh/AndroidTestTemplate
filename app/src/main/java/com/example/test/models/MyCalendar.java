package com.example.test.models;

import androidx.annotation.ColorRes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDate;

public class MyCalendar {
    public String title;
    public String todoContent;
    public String explain;
    public LocalDate localDate;
    public boolean isFinish;
    @ColorRes
    public int color;

    public MyCalendar(String title, String todoContent, String explain, LocalDate localDate, boolean isFinish, int color) {
        this.title = title;
        this.todoContent = todoContent;
        this.explain = explain;
        this.localDate = localDate;
        this.isFinish = isFinish;
        this.color = color;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTodoContent() {
        return todoContent;
    }

    public void setTodoContent(String todoContent) {
        this.todoContent = todoContent;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
