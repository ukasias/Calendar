package com.ukasias.android.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ScheduleListDialog extends Activity {
    private static final String TAG = "ScheduleListDialog";

    private TextView dateTextView;
    private Button addButton;
    private ListView scheduleListView;
    private Button closeButton;

    private ScheduleListAdapter adapter;

    public ScheduleListDialog() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_schedule_list);

        dateTextView = findViewById(R.id.dateText);

        addButton = findViewById(R.id.addButton);

        scheduleListView = findViewById(R.id.scheduleList);
        adapter = new ScheduleListAdapter(this);
        scheduleListView.setAdapter(adapter);

        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init();
    }

    private void init() {
        Intent intent = getIntent();

        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        final ArrayList<ScheduleItem> scheduleList =
                intent.getParcelableArrayListExtra(BasicInfo.KEY_SCHEDULE_LIST);
        int day = intent.getIntExtra(BasicInfo.KEY_SCHEDULE_LIST_DAY, -1);


        if (scheduleList.size() > 0) {
            String dateString =
                    scheduleList.get(0).getYear()
                    + "년 "
                    + scheduleList.get(0).getMonth()
                    + "월 " + scheduleList.get(0).getDay() + "일 " + days[day];
            Log.d(TAG, "init() - " + dateString);
            dateTextView.setText(dateString);
            adapter.setScheduleList(scheduleList);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleItem item = new ScheduleItem();
                item.setYear(scheduleList.get(0).getYear());
                item.setMonth(scheduleList.get(0).getMonth());
                item.setDay(scheduleList.get(0).getDay());

                showScheduleActivity(item, true);
            }
        });

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick(position: " + position + ")");

                ScheduleItem item = scheduleList.get(position);
                showScheduleActivity(item, false);
            }
        });
    }

    private void showScheduleActivity(ScheduleItem item, boolean isNewSchedule) {
        Intent intent
                = new Intent(getApplicationContext(), ScheduleActivity.class);

        Log.d(TAG, "showScheduleActivity(isNewSchedule: " + isNewSchedule);

        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_YEAR, item.getYear());
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_MONTH, item.getMonth());
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_DAY, item.getDay());

        if (isNewSchedule) {
            intent.putExtra(BasicInfo.KEY_SCHEDULE_MODE,
                    BasicInfo.SCHEDULE_MODE_INSERT);

            startActivityForResult(intent, BasicInfo.REQ_INSERT_ACTIVITY);
        }
        else {
            intent.putExtra(BasicInfo.KEY_SCHEDULE_MODE,
                    BasicInfo.SCHEDULE_MODE_VIEW);
            intent.putExtra(BasicInfo.KEY_SCHEDULE_ID, item.getId());
            intent.putExtra(BasicInfo.KEY_SCHEDULE_TITLE, item.getTitle());
            intent.putExtra(BasicInfo.KEY_SCHEDULE_TEXT, item.getContents());

            startActivityForResult(intent, BasicInfo.REQ_VIEW_ACTIVITY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BasicInfo.REQ_INSERT_ACTIVITY:
                if (requestCode == RESULT_OK) {
                    Log.d(TAG, "onACtivityResult(INSERT -> OK)");
                }
                else {
                    Log.d(TAG, "onACtivityResult(INSERT -> ERROR)");
                }
                break;

            case BasicInfo.REQ_VIEW_ACTIVITY:
                if (requestCode == RESULT_OK) {
                    Log.d(TAG, "onACtivityResult(VIEW -> OK)");
                }
                else {
                    Log.d(TAG, "onACtivityResult(VIEW -> ERROR)");
                }
                break;
        }

        finish();
    }
}

class ScheduleListAdapter extends BaseAdapter {
    ArrayList<ScheduleItem> scheduleList;
    Context mContext;

    public ScheduleListAdapter(Context context) {
        mContext = context;
    }

    public void setScheduleList(ArrayList<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @Override
    public int getCount() {
        if (scheduleList == null) {
            return 0;
        }
        return scheduleList.size();
    }

    @Override
    public ScheduleItem getItem(int position) {
        if (scheduleList == null) {
            return null;
        }
        return scheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
                250);
        cellView.setLayoutParams(params);
        cellView.setPadding(20, 20, 0, 0);
        cellView.setItem(scheduleList.get(position));
        //cellView.setBackgroundResource(R.drawable.back_line_pad);
        cellView.setTextColor(Color.BLACK);
        cellView.setTextSize((float)25.0);

        return cellView;
    }
}
