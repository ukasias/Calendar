package com.ukasias.android.calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ukasias.android.calendar.db.ScheduleDatabase;

import java.text.ParseException;

public class ScheduleActivity extends AppCompatActivity {
    private final String TAG = "ScheduleActivity";

    private TextView scheduleModeText;
    private EditText scheduleNameText;
    private Button dateButton;
    private EditText scheduleContentsText;
    private Button saveButton;
    private Button cancelButton;
    private Button modifyButton;
    private Button removeButton;
    private Button closeButton;

    private String scheduleMode;

    private int scheduleId;
    private int scheduleYear;
    private int scheduleMonth;
    private int scheduleDay;
    private String scheduleTitle;
    private String scheduleContents;


    private DatePickerDialog.OnDateSetListener datePickerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        initVariables();

        setLayout();

        checkIntent(getIntent());
    }

    private void initVariables() {
        scheduleMode = "";
        scheduleTitle = "";
        scheduleContents = "";

        datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view,
                                  int _year, int _month, int _day) {
                Log.d(TAG, "onDateSet(year: "
                        + _year + ", month: " + _month + ", _day: " + _day + ")");
                scheduleYear = _year;
                scheduleMonth = _month + 1;
                scheduleDay = _day;

                String dateString = scheduleYear + "년 "
                        + scheduleMonth + "월 " + scheduleDay + "일";
                dateButton.setText(dateString);
            }
        };
    }

    private void setLayout() {
        scheduleModeText = findViewById(R.id.titleText);

        scheduleNameText = findViewById(R.id.scheduleName);

        dateButton = findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        ScheduleActivity.this,
                        datePickerListener,
                        scheduleYear,
                        scheduleMonth - 1,
                        scheduleDay).show();
            }
        });

        scheduleContentsText = findViewById(R.id.scheduleContents);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean noBlank = checkTitleContents();
                if (noBlank) {
                    if (scheduleMode.equals(BasicInfo.SCHEDULE_MODE_INSERT)) {
                        saveSchedule();
                    } else {
                        modifySchedule();
                    }
                }
            }
        });

        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scheduleMode.equals(BasicInfo.SCHEDULE_MODE_INSERT)) {
                    finish();
                }
                else {
                    initSchedule();
                    setEditEnabled(false);
                    setSaveEnabled(false);
                }
            }
        });

        modifyButton = findViewById(R.id.modifyButton);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditEnabled(true);
                setSaveEnabled(true);
            }
        });

        removeButton = findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(BasicInfo.CONFIRM_DELETE);
            }
        });

        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveSchedule() {
        Intent intent = getIntent();
        String SQL = null;
        boolean success = true;
        try {

            SQL
                    = "insert into " + ScheduleDatabase.TABLE_SCHEDULE
                    + "(INPUT_DATE, TITLE_TEXT, CONTENTS_TEXT) values ("
                    + "DATETIME('"
                    + BasicInfo.dateFormat.format(
                    BasicInfo.dateKorFormat.parse(dateButton.getText().toString().trim()))
                    + "'), "
                    + "'" + scheduleNameText.getText().toString().trim().replace("'", "''") + "', "
                    + "'" + scheduleContentsText.getText().toString().trim().replace("'", "''") + "')";

            if (CalendarActivity.mDatabase != null) {
                CalendarActivity.mDatabase.execSQL(SQL);
                Log.d(TAG, "CalendarActivity.mDatabase rawQuery excuted.");
            }
            else {
                Log.d(TAG, "CalendarActivity.mDatabase is null");
                success = false;
            }
        }
        catch(ParseException e) {
            Log.d(TAG, "ParseException in saveSchedule()");
            success = false;
        }
        Log.d(TAG, "saveSchedule() - SQL: " + SQL);
        Log.d(TAG, "success: " + success);

        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_YEAR, scheduleYear);
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_MONTH, scheduleMonth);
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_DAY, scheduleDay);

        if (success) {
            Log.d(TAG, "RESULT_OK");
            setResult(RESULT_OK, intent);
        }
        else {
            Log.d(TAG, "RESULT_CANCELED");
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    private void modifySchedule() {
        String SQL = null;
        boolean success = true;

        Intent intent = getIntent();
        try {
            SQL = "update " + ScheduleDatabase.TABLE_SCHEDULE
                    + " set "
                    + " INPUT_DATE = DATETIME('"
                    + BasicInfo.dateFormat.format(
                    BasicInfo.dateKorFormat.parse(dateButton.getText().toString().trim()))
                    + "'), "
                    + "TITLE_TEXT = '"
                    + scheduleNameText.getText().toString().trim().replace("'", "''") + "', "
                    + "CONTENTS_TEXT = '"
                    + scheduleContentsText.getText().toString().trim().replace("'", "''") + "'"
                    + " where _ID = '"
                    + scheduleId + "'";
            if (CalendarActivity.mDatabase != null) {
                CalendarActivity.mDatabase.execSQL(SQL);
            }
        }
        catch (ParseException e) {
            Log.d(TAG, "ParseException in modifySchedule()");
            success = false;
        }

        Log.d(TAG, "modifySchedule() - SQL: " + SQL);
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_YEAR, scheduleYear);
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_MONTH, scheduleMonth);
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_DAY, scheduleDay);

        if (success) {
            setResult(RESULT_OK, intent);
        }
        else {
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    private void deleteSchedule() {
        String SQL
                = "delete from " + ScheduleDatabase.TABLE_SCHEDULE
                + " where _ID = '"
                + scheduleId + "'";

        Log.d(TAG, "deleteScheduleSchedule() - SQL: " + SQL);
        if (CalendarActivity.mDatabase != null) {
            CalendarActivity.mDatabase.execSQL(SQL);
        }

        Intent intent = getIntent();
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_YEAR, scheduleYear);
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_MONTH, scheduleMonth);
        intent.putExtra(BasicInfo.KEY_SCHEDULE_DATE_DAY, scheduleDay);

        setResult(RESULT_OK, intent);

        finish();
    }

    private void setEditEnabled(boolean enabled) {
        scheduleNameText.setEnabled(enabled);
        dateButton.setEnabled(enabled);
        scheduleContentsText.setEnabled(enabled);
    }

    private void setSaveEnabled(boolean enabled) {
        LinearLayout layout = findViewById(R.id.bottomLayout);
        layout.removeAllViews();

        if (enabled) {
            layout.addView(saveButton);
            layout.addView(cancelButton);
        }
        else {
            layout.addView(modifyButton);
            layout.addView(removeButton);
            layout.addView(closeButton);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "onNewIntent()");

        checkIntent(intent);
    }

    private void checkIntent(Intent intent) {
        if (intent == null) {
            Log.d(TAG, "checkIntent(null)");
            return;
        }
        else Log.d(TAG, "checkIntent(not null)");

        try {
            scheduleMode = intent.getStringExtra(BasicInfo.KEY_SCHEDULE_MODE);
            Log.d(TAG, "checkIntent() - scheduleMode: " + scheduleMode);

            if (scheduleMode != null && scheduleMode.equals(BasicInfo.SCHEDULE_MODE_INSERT)) {
                Log.d(TAG, "SCHEDULE_MODE_INSERT");
                scheduleModeText.setText(R.string.schedule_new);

                setEditEnabled(true);
                setSaveEnabled(true);
            }
            else { // BasicInfo.SCHEDULE_MODE_VIEW
                Log.d(TAG, "SCHEDULE_MODE_VIEW");
                scheduleModeText.setText(R.string.schedule_view);

                setEditEnabled(false);
                setSaveEnabled(false);
            }

            processIntent(intent);
        }
        catch(Exception e) {
            Log.d(TAG, "checkIntent() exception ");
            e.printStackTrace();
        }
    }

    private void processIntent(Intent intent) {
        if (scheduleMode.equals(BasicInfo.SCHEDULE_MODE_INSERT)) {
            scheduleYear = intent.getIntExtra(BasicInfo.KEY_SCHEDULE_DATE_YEAR,
                    0);
            scheduleMonth = intent.getIntExtra(BasicInfo.KEY_SCHEDULE_DATE_MONTH,
                    0);
            scheduleDay = intent.getIntExtra(BasicInfo.KEY_SCHEDULE_DATE_DAY,
                    0);
        }
        else {
            scheduleId = intent.getIntExtra(BasicInfo.KEY_SCHEDULE_ID, 0);
            scheduleYear = intent.getIntExtra(BasicInfo.KEY_SCHEDULE_DATE_YEAR,
                    0);
            scheduleMonth = intent.getIntExtra(BasicInfo.KEY_SCHEDULE_DATE_MONTH,
                    0);
            scheduleDay = intent.getIntExtra(BasicInfo.KEY_SCHEDULE_DATE_DAY,
                    0);
            scheduleTitle = intent.getStringExtra(BasicInfo.KEY_SCHEDULE_TITLE);
            scheduleContents = intent.getStringExtra(BasicInfo.KEY_SCHEDULE_TEXT);
        }

        initSchedule();
    }

    private void initSchedule() {
        Log.d(TAG, "initSchedule()");

        scheduleNameText.setText(scheduleTitle);
        String dateString = scheduleYear + "년 " + scheduleMonth
                    + "월 " + scheduleDay + "일";
        dateButton.setText(dateString);
        scheduleContentsText.setText(scheduleContents);
    }

    private boolean checkTitleContents() {
        if (scheduleNameText.getText().toString().trim().length() < 1) {
            showDialog(BasicInfo.CONFIRM_TITLE_INPUT);
            return false;
        }

        if (scheduleContentsText.getText().toString().trim().length() < 1) {
            showDialog(BasicInfo.CONFIRM_CONTENTS_INPUT);
            return false;
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d(TAG, "onCreateDialog(id: " + id + ")");

        AlertDialog.Builder builder = null;

        switch(id) {
            case BasicInfo.CONFIRM_DELETE:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("일정");
                builder.setMessage("일정을 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSchedule();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismissDialog(BasicInfo.CONFIRM_DELETE);
                            }
                        });
                break;

            case BasicInfo.CONFIRM_TITLE_INPUT:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("일정");
                builder.setMessage("일정 제목을 입력하세요.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                break;

            case BasicInfo.CONFIRM_CONTENTS_INPUT:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("일정");
                builder.setMessage("일정 내용을 입력하세요.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                break;
        }

        return builder.create();
    }
}
