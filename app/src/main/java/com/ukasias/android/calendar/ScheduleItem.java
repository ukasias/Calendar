package com.ukasias.android.calendar;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class ScheduleItem implements Parcelable {
    private int id;
    private int year;
    private int month;
    private int day;
    private String title;
    private String contents;

    public ScheduleItem() {

    }

    public ScheduleItem(int id, int year, int month, int day, String title, String contents) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.title = title;
        this.contents = contents;
    }

    protected ScheduleItem(Parcel in) {
        id = in.readInt();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        title = in.readString();
        contents = in.readString();
    }

    public static final Creator<ScheduleItem> CREATOR = new Creator<ScheduleItem>() {
        @Override
        public ScheduleItem createFromParcel(Parcel source) {
            return new ScheduleItem(source);
        }

        @Override
        public ScheduleItem[] newArray(int size) {
            return new ScheduleItem[size];
        }
    };

    public void setId(int id) {
        this.id = id;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeString(title);
        dest.writeString(contents);
    }
}
