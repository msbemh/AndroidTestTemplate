package com.example.test.models;

import java.time.LocalDate;

public class TestModel {
    public String test1;
    public int test2;
    public LocalDate localDate;

    public TestModel(){

    }

    public TestModel(String test1, int test2, LocalDate localDate) {
        this.test1 = test1;
        this.test2 = test2;
        this.localDate = localDate;
    }

    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public int getTest2() {
        return test2;
    }

    public void setTest2(int test2) {
        this.test2 = test2;
    }
}
