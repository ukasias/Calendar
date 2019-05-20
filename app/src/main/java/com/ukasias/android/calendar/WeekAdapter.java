package com.ukasias.android.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class WeekAdapter extends BaseAdapter {
    public static final String TAG = "WeekAdapter";

    Context mContext;

    private ArrayList<ScheduleItem> scheduleList;

    public WeekAdapter(Context context) {
        super();
        mContext = context;

        init();
    }

    public void setScheduleList(ArrayList<ScheduleItem> list) {
        Log.d(TAG, "setScheduleList(): " + (list == null? true : false));
        scheduleList = list;
    }

    public void init() {
        Log.d(TAG, "init()");

        scheduleList = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d(TAG, "getView(" + position + ")");
        ScheduleTitleView cellView;
        if (convertView == null) {
            cellView = new ScheduleTitleView(mContext);
        }
        else {
            cellView = (ScheduleTitleView) convertView;
        }

        ListView.LayoutParams params = new ListView.LayoutParams(
                ListView.LayoutParams.MATCH_PARENT,
                150);
        cellView.setLayoutParams(params);
        cellView.setPadding(10, 0, 10, 0);
        cellView.setItem(scheduleList.get(position));
        cellView.setTextColor(Color.BLACK);
        cellView.setTextSize((float)15.0);

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
