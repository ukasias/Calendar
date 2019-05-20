package com.ukasias.android.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MonthItemView extends LinearLayout {
    private MonthItem item;
    private Context context;

    private TextView textView;
    private TextView scheduleTextView;

    public MonthItemView(Context context) {
        super(context);
        this.context = context;

        init();
    }

    public MonthItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

    public void init() {
        LayoutInflater inflater =
                (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.month_item, this, true);

        textView = findViewById(R.id.monthItemText);
        scheduleTextView = findViewById(R.id.monthItemSchedule);

        setBackgroundResource(R.drawable.month_back_line);
    }

    public MonthItem getItem() {
        return item;
    }

    public void setItem(MonthItem item) {
        this.item = item;

        int day = item.getDay();
        if (day != 0) {
            textView.setText(String.valueOf(day));
        }
        else {
            textView.setText("");
        }
    }

    public void setSunday() {
        setTextColor(Color.RED);
    }

    public void setSaturday() {
        setTextColor(Color.BLUE);
    }

    public void setNormalDay() {
        setTextColor(Color.BLACK);
    }

    private void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setDayCheck(boolean check) {
        if (check) {
            textView.setBackgroundResource(R.drawable.text_back_line);
        }
        else {
            textView.setBackgroundResource(0);
        }
    }

    public void setToday(boolean isToday) {
        if (isToday) {
            textView.setTypeface(null, Typeface.BOLD_ITALIC);
        }
        else {
            textView.setTypeface(null, Typeface.NORMAL);
        }
    }

    public void setScheduleCount(int scheduleCount) {
        if (scheduleCount > 0) {
            scheduleTextView.setText("+" + scheduleCount);
        }
        else {
            scheduleTextView.setText("");
        }
    }
}
