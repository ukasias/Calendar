package com.ukasias.android.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleTitleView extends LinearLayout {
    private ScheduleItem item;
    private Context mContext;

    private TextView titleView;

    public ScheduleTitleView(Context context) {
        super(context);

        mContext = context;

        init();
    }

    public ScheduleTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        init();
    }

    public void init() {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.schedule_title, this, true);

        titleView = findViewById(R.id.scheduleTitle);

        setBackgroundResource(R.drawable.month_back_line);
    }

    public ScheduleItem getItem() {
        return item;
    }

    public void setItem(ScheduleItem item) {
        this.item = item;

        titleView.setText(item.getTitle());
        titleView.setLines(1);
    }

    public void setTextSize(float size) {
        titleView.setTextSize(size);
    }

    public void setTextColor(int color) {
        titleView.setTextColor(color);
    }
}
