package com.ukasias.android.calendar;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ukasias.android.calendar.db.ScheduleDatabase;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class CalendarActivity extends AppCompatActivity
        implements MonthlyFragment.OnFragmentInteractionListener {

    private static final String TAG = "CalendarActivity";
    private final String logfile = "log";

    Toolbar toolbar;

    MonthlyFragment monthlyFragment;
    WeeklyFragment weeklyFragment;
    DailyFragment dailyFragment;

    TabLayout tabs;

    FloatingActionButton fab;

    Calendar calendar;
    private static int todayYear;
    private static int todayMonth;
    private static int todayDay;

    // 데이터베이스 인스턴스
    public static ScheduleDatabase mDatabase = null;

    LinkedList<ScheduleItem> scheduleList = null;

    /**
     * Permission Status
     */

    final int PERMISSION_ACQUIRED = 101;
    final int PERMISSION_NOT_ACQUIRED = 102;
    final int PERMISSION_REQUESTED = 103;

    private int permissionStatus = PERMISSION_NOT_ACQUIRED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        if (!checkExternalStorageState()) {
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        scheduleList = new LinkedList<>();

        monthlyFragment = new MonthlyFragment();
        weeklyFragment = new WeeklyFragment();
        dailyFragment = new DailyFragment();

        calendar = Calendar.getInstance();
        todayYear = calendar.get(Calendar.YEAR);
        todayMonth = calendar.get(Calendar.MONTH);
        todayDay = calendar.get(Calendar.DAY_OF_MONTH);

        Log.d(TAG, calendar.toString());

        monthlyFragment.setCalendar(calendar);
        weeklyFragment.setCalendar(calendar);
        dailyFragment.setCalendar(calendar);

        tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText(R.string.monthly));
        tabs.addTab(tabs.newTab().setText(R.string.weekly));
        tabs.addTab(tabs.newTab().setText(R.string.daily));

        tabs.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 " + position);

                Fragment selected = null;
                if (position == 0) selected = monthlyFragment;
                else if (position == 1) selected = weeklyFragment;
                else if (position == 2) selected = dailyFragment;

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScheduleItem item = new ScheduleItem();
                item.setYear(calendar.get(Calendar.YEAR));
                item.setMonth(calendar.get(Calendar.MONTH) + 1);
                item.setDay(calendar.get(Calendar.DAY_OF_MONTH));

                showScheduleActivity(item, true);
            }
        });


        checkDangerousPermissions();

        startFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("onFragmentInteraction()", uri.getPath());
    }

    @Override
    public void onSetFragmentNumber(int n) {
        SharedPreferences sharedPreferences
                = getSharedPreferences(logfile, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(BasicInfo.KEY_FRAGMENT_NUMBER, n);
        editor.commit();
    }

    public void onShowScheduleActivity(ScheduleItem item, boolean isNewSchedule) {
        showScheduleActivity(item, isNewSchedule);
    }

    public void onShowScheduleListDialog(ArrayList<ScheduleItem> items,
                                         int day, int id) {
        if (id == BasicInfo.SHOW_SCHEDULE) {
            Intent intent = new Intent(
                    getApplicationContext(),
                    ScheduleListDialog.class);

            Log.d(TAG, "onShowScheduleListDialog() : " + items.size());
            intent.putParcelableArrayListExtra(
                    BasicInfo.KEY_SCHEDULE_LIST,
                    items);
            intent.putExtra(
                    BasicInfo.KEY_SCHEDULE_LIST_DAY, day);

            startActivity(intent);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(),
                        message,
                        Toast.LENGTH_LONG).show();
    }

    private boolean checkExternalStorageState() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String warning = "SD 카드가 없습니다. SD 카드를 넣은 후 다시 실행하십시오.";
            toast(warning);
            Log.d(TAG, warning);

            return false;
        }
        else Log.d(TAG, "SD 카드가 있습니다.");

        String externalPath =
                Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d(TAG, "externalPath: " + externalPath);
        Log.d(TAG, "File.separator: " + File.separator + ", "
                + "File.pathSeparator: " + File.pathSeparator);

        if (!BasicInfo.ExternalPathChecked && externalPath != null) {
            BasicInfo.ExternalPath = externalPath + File.separator;
            Log.d(TAG, "ExternalPath: " + BasicInfo.ExternalPath);

            BasicInfo.DATABASE_NAME = BasicInfo.ExternalPath + BasicInfo.DATABASE_NAME;
            Log.d(TAG, "BasicInfo.DATABASE_NAME: " + BasicInfo.DATABASE_NAME);

            BasicInfo.ExternalPathChecked = true;
        }

        return true;
    }

    private void checkDangerousPermissions() {
        Log.d(TAG, "checkDangerousPermissions()");

        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String permission : permissions) {
            if ((permissionCheck = ContextCompat.checkSelfPermission(
                    this, permission))
                    == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permissionStatus = PERMISSION_ACQUIRED;
        } else {
            boolean shouldShow = false;
            for (String permission : permissions) {
                shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                if (shouldShow == true) break;
            }

            if (shouldShow) {
                permissionStatus = PERMISSION_NOT_ACQUIRED;
            } else {
                permissionStatus = PERMISSION_REQUESTED;
                ActivityCompat.requestPermissions(
                        this,
                        permissions,
                        1);
            }
        }

        if (permissionStatus != PERMISSION_REQUESTED) {
            handlePermission();
        }
    }

    private void startFragment() {
        SharedPreferences sharedPreferences
                = getSharedPreferences(logfile, MODE_PRIVATE);
        int fragmentNumber =
                sharedPreferences.getInt(BasicInfo.KEY_FRAGMENT_NUMBER,
                        0);

        TabLayout tabs = findViewById(R.id.tabs);
        TabLayout.Tab tab = tabs.getTabAt(fragmentNumber);
        tab.select();

        switch(fragmentNumber) {
            case 1:
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, weeklyFragment).commit();
                break;

            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, dailyFragment).commit();
                break;

            case 0:
            default:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, monthlyFragment).commit();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() called.(requestCode: "
                + requestCode + ")");

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, permissions[i] + " 권한이 승인됨.");
                }
                else {
                    Log.d(TAG, permissions[i] + " 권한이 승인되지 않음.");
                }
            }

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                permissionStatus = PERMISSION_ACQUIRED;
            }
            else {
                permissionStatus = PERMISSION_NOT_ACQUIRED;
            }

            handlePermission();
        }
    }

    private void handlePermission() {
        switch (permissionStatus) {
            case PERMISSION_ACQUIRED:
                Log.d(TAG, "handlePermission - PERMISSION_ACQUIRED");
                initDatabase();
                break;
            case PERMISSION_NOT_ACQUIRED:
                Log.d(TAG, "handlePermission - PERMISSION_NOT_ACQUIRED");
                distableUI();
                break;
            case PERMISSION_REQUESTED:
                Log.d(TAG, "handlePermission - PERMISSION_REQUESTED");
                break;
        }
    }

    private void initDatabase() {
        Log.d(TAG, "initDatabase()");
        openDatabase();
    }

    private void distableUI() {
        fab.setEnabled(false);
        /** FIXME **/
    }

    private void openDatabase() {
        Log.d(TAG, "openDatabase()");

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = ScheduleDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Schedule database opened");
        }
        else {
            Log.d(TAG, "Schedule database not opened");
        }
    }

    public static int getTodayYear() {
        return todayYear;
    }

    public static int getTodayDay() {
        return todayDay;
    }

    public static int getTodayMonth() {
        return todayMonth;
    }

    public static ArrayList<ScheduleItem> rawQuery(String SQL, String[] selectionArgs) {
        Cursor cursor;
        ArrayList<ScheduleItem> items = new ArrayList<>(0);
        if (mDatabase != null) {
            cursor = mDatabase.rawQuery(SQL, selectionArgs);

            if (cursor == null) {
                Log.d(TAG, "rawQuery() - cursor is null");
                return items;
            }

            int count = cursor.getCount();
            items = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                cursor.moveToNext();
                boolean parseSuccess = true;

                int id = Integer.parseInt(cursor.getString(0));
                Date date = null;
                try {
                    date = BasicInfo.dateFormat.parse(
                            cursor.getString(1));
                    Log.d(TAG, "date: " + date.getYear() + ", " +
                            date.getMonth() + ", " + date.getDate() + ", " +
                            date.getDay());
                }
                catch (ParseException e) {
                    Log.d(TAG, "rawQuery()");
                    parseSuccess = false;
                }

                if (parseSuccess) {
                    items.add(i,
                            new ScheduleItem(
                                    id,
                                    date.getYear() + 1900,
                                    date.getMonth() + 1,
                                    date.getDate(),
                                    cursor.getString(2),
                                    cursor.getString(3)));
                }
            }
        }

        return items;
    }

    public void showScheduleActivity(ScheduleItem item, boolean isNewSchedule) {
        Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
            intent.putExtra(BasicInfo.KEY_SCHEDULE_ID,
                    item.getId());
            intent.putExtra(BasicInfo.KEY_SCHEDULE_TITLE,
                    item.getTitle());
            intent.putExtra(BasicInfo.KEY_SCHEDULE_TEXT,
                    item.getContents());
            startActivityForResult(intent, BasicInfo.REQ_VIEW_ACTIVITY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult(): " + calendar.toString());
        switch (requestCode) {
            case BasicInfo.REQ_INSERT_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onActivityResult(INSERT -> OK)");
                }
                else {
                    Log.d(TAG, "onActivityResult(INSERT -> ERROR)");
                }
                break;

            case BasicInfo.REQ_VIEW_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onActivityResult(VIEW -> OK)");
                }
                else {
                    Log.d(TAG, "onActivityResult(VIEW -> ERROR)");
                }
                break;
        }

        if (resultCode == RESULT_OK) {
            calendar.set(Calendar.YEAR,
                    data.getIntExtra(BasicInfo.KEY_SCHEDULE_DATE_YEAR, 0));
            calendar.set(Calendar.MONTH,
                    data.getIntExtra(BasicInfo.KEY_SCHEDULE_DATE_MONTH, 0)
                            - 1);
            calendar.set(Calendar.DAY_OF_MONTH,
                    data.getIntExtra(BasicInfo.KEY_SCHEDULE_DATE_DAY, 0));

            switch(tabs.getSelectedTabPosition()) {
                case 0:
                    monthlyFragment.updateUI();
                    break;
                case 1:
                    weeklyFragment.updateUI();
                    break;
                case 2:
                    dailyFragment.updateUI();
                    break;
            }
        }
    }
}
