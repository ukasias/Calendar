package com.ukasias.android.calendar;

public class MonthItem {
    private int day;
    private boolean hasSchedule;

    public MonthItem() {
    }

    public MonthItem(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHasSchedule(boolean hasSchedule) {
        this.hasSchedule = hasSchedule;
    }

    public boolean isHasSchedule() {
        return hasSchedule;
    }
}
