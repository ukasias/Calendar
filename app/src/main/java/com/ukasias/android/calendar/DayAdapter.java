package com.ukasias.android.calendar;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class DayAdapter extends BaseAdapter {
    public static final String TAG = "DayAdapter";

    Context mContext;

    private ArrayList<ScheduleItem> scheduleList;

    public DayAdapter(Context context) {
        super();
        mContext = context;

        init();
    }


    public void init() {
        Log.d(TAG, "init()");

        scheduleList = null;
    }

    public void setScheduleList(ArrayList<ScheduleItem> list) {
        scheduleList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScheduleItemView cellView;

        if (convertView == null) {
            cellView = new ScheduleItemView(mContext);
        }
        else {
            cellView = (ScheduleItemView) convertView;
        }
        cellView.setItem(scheduleList.get(position));

        return cellView;
    }

    @Override
    public int getCount() {
        if (scheduleList == null) {
            return 0;
        }
        return scheduleList.size();
    }

    @Override
    public Object getItem(int position) {
        if (scheduleList == null) {
            return null;
        }
        return scheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
