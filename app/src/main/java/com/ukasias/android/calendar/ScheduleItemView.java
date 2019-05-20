package com.ukasias.android.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleItemView extends LinearLayout {
    private ScheduleItem item;
    private Context mContext;

    private TextView titleView;
    private TextView contentsView;

    public ScheduleItemView(Context context) {
        super(context);

        mContext = context;

        init();
    }

    public ScheduleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        init();
    }

    public void init() {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.schedule_item, this, true);

        titleView = findViewById(R.id.scheduleItemTitle);
        contentsView = findViewById(R.id.scheduleItemContents);

        setBackgroundResource(R.drawable.month_back_line);
    }

    public ScheduleItem getItem() {
        return item;
    }

    public void setItem(ScheduleItem item) {
        this.item = item;

        titleView.setText(item.getTitle());
        titleView.setLines(1);

        contentsView.setText(item.getContents());
        contentsView.setLines(3);
    }
}
