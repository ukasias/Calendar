package com.ukasias.android.calendar;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.ukasias.android.calendar.db.ScheduleDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthAdapter extends BaseAdapter {
    public static final String TAG = "MonthAdapter";

    private int todayYear;
    private int todayMonth;
    private int todayDay;

    private Context mContext;
    private MonthlyFragment.OnFragmentInteractionListener listener;

    private MonthItem[] items;
    private Calendar calendar;
    private int scheduleCount[];

    private final int numColumns = 7;
    private final int maxWeeks = 6;
    private int currentWeeks;

    private final int maxGridCells = numColumns * maxWeeks;

    int emptyDaysOfFirstWeek;
    int daysOfMonth;

    public MonthAdapter(Context context, Calendar calendar) {
        super();
        mContext = context;
        listener = (MonthlyFragment.OnFragmentInteractionListener) context;
        this.calendar = calendar;

        init();
    }

    public void init() {
        Log.d(TAG, "init(): " + calendar.toString());
        items = new MonthItem[maxGridCells];
        for (int position = 0; position < maxGridCells; position++) {
            items[position] = new MonthItem();
        }

        scheduleCount = new int[31];

        currentWeeks = maxWeeks;

        todayYear = CalendarActivity.getTodayYear();
        todayMonth = CalendarActivity.getTodayMonth();
        todayDay = CalendarActivity.getTodayDay();

        recalculate();
        resetDayNumbers();
        resetSchedule();
    }

    private void recalculate() {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "day : " + day);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        emptyDaysOfFirstWeek = calendar.get(Calendar.DAY_OF_WEEK)
                - Calendar.SUNDAY;
        calendar.set(Calendar.DAY_OF_MONTH, day);
        daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        currentWeeks = (emptyDaysOfFirstWeek + daysOfMonth) / 7;
        currentWeeks += (emptyDaysOfFirstWeek + daysOfMonth) % 7 > 0? 1 : 0;

        Log.d(TAG, "빈날 수     : " + emptyDaysOfFirstWeek);
        Log.d(TAG, "이번 달 수   : " + daysOfMonth);
        Log.d(TAG, "년도        : " + calendar.get(Calendar.YEAR));
        Log.d(TAG, "달:         : " + (calendar.get(Calendar.MONTH) + 1));
    }

    private void resetDayNumbers(){
        int date = 0;

        for (int position = 0; position < maxGridCells; position++) {
            if (position < emptyDaysOfFirstWeek) {
                items[position].setDay(0);
            }
            else {
                if (++date <= daysOfMonth) {
                    items[position].setDay(date);

                } else {
                    items[position].setDay(0);
                }
            }
        }
    }

    private void resetSchedule() {
        String SQL;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Log.d(TAG, "resetSchedule()");

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        for (int d = 1; d <= daysOfMonth; d++) {
            SQL = "select * from " + ScheduleDatabase.TABLE_SCHEDULE
                    + " where INPUT_DATE = '"
                    + BasicInfo.dateFormat.format(calendar.getTime())
                    + "'";
            ArrayList<ScheduleItem> items = CalendarActivity.rawQuery(SQL, null);
            scheduleCount[d - 1] = items.size();
            Log.d(TAG, "SQL: " + SQL);
            Log.d(TAG, calendar.get(Calendar.YEAR) + "년 "
                + (calendar.get(Calendar.MONTH) + 1) + "월 "
                + d + "일: "
                + scheduleCount[d - 1]);

            calendar.add(Calendar.DAY_OF_MONTH, +1);
        }

        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    public void updateSchedule() {
        Log.d(TAG, "updateSchedule()");
        resetSchedule();
        notifyDataSetInvalidated();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView(" + position + ")");

        MonthItemView cellView;

        if (convertView == null) {
            cellView = new MonthItemView(mContext);
        }
        else {
            cellView = (MonthItemView) convertView;
        }

        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                parent.getHeight() / currentWeeks - 5);
        cellView.setLayoutParams(params);
        cellView.setPadding(2, 20, 2, 2);
        cellView.setItem(items[position]);

        /* 글자 색 */
        if (items[position].getDay() > 0) {
            if (position % numColumns == 0) {
                cellView.setSunday();
            }
            if (position % numColumns == 6) {
                cellView.setSaturday();
            }
        }
        else {
            cellView.setNormalDay();
        }


        /* 일자 선택 */
        if ((items[position].getDay() > 0) &&
                (items[position].getDay()
                        == calendar.get(Calendar.DAY_OF_MONTH))) {
            cellView.setDayCheck(true);
        }
        else {
            cellView.setDayCheck(false);
        }

        /* 오늘 */
        if ((calendar.get(Calendar.YEAR) == todayYear) &&
                (calendar.get(Calendar.MONTH) == todayMonth) &&
                (items[position].getDay() == todayDay)) {
            cellView.setToday(true);
        }
        else {
            cellView.setToday(false);
        }

        if (items[position].getDay() > 0) {
            cellView.setScheduleCount(scheduleCount[items[position].getDay() - 1]);
        }
        else {
            cellView.setScheduleCount(0);
        }

        return cellView;
    }

    public void setPrevMonth() {
        calendar.add(Calendar.MONTH, -1);
        recalculate();
        resetDayNumbers();
        resetSchedule();
    }

    public void setNextMonth() {
        calendar.add(Calendar.MONTH, +1);
        recalculate();
        resetDayNumbers();
        resetSchedule();
    }

    public void onItemClicked(int position) {
        int day = -1;
        if ((day = items[position].getDay()) == calendar.get(Calendar.DAY_OF_MONTH)) {
            if (scheduleCount[day - 1] > 0) {
                ArrayList<ScheduleItem> items;

                String SQL = "select * from " + CalendarActivity.mDatabase.TABLE_SCHEDULE
                        + " where INPUT_DATE = '"
                        + BasicInfo.dateFormat.format(calendar.getTime())
                        + "'";
                items = CalendarActivity.rawQuery(SQL, null);
                listener.onShowScheduleListDialog(items,
                        calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY,
                        BasicInfo.SHOW_SCHEDULE);
            }
            else {
                ScheduleItem item = new ScheduleItem();
                item.setYear(calendar.get(Calendar.YEAR));
                item.setMonth(calendar.get(Calendar.MONTH) + 1);
                item.setDay(calendar.get(Calendar.DAY_OF_MONTH));

                Log.d(TAG, item.getYear() + ", "
                        + item.getMonth() + ", " + item.getDay());

                listener.onShowScheduleActivity(item, true);
            }
        }
        else {
            calendar.set(Calendar.DAY_OF_MONTH, items[position].getDay());
        }

        Log.d(TAG, "onItemClicked(position: " + position
                + "), day: " + items[position].getDay());

        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return currentWeeks * numColumns;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
