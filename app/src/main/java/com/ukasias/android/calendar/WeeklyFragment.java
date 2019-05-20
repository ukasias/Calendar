package com.ukasias.android.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MonthlyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeeklyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeeklyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String TAG = "WeeklyFragment";

    private Button preButton;
    private Button nextButton;
    private TextView textView;

    private LinearLayout[] dayLayout;
    private TextView[] dateView;
    private ListView[] listView;

    private int[] scheduleCount;
    private int[] dayMatch;

    private int todayYear;
    private int todayMonth;
    private int todayDay;

    private WeekAdapter[] weekAdapter;
    private ArrayList<ScheduleItem> scheduleList;

    private Calendar calendar;

    public void setCalendar(Calendar calendar) {
        Log.d(TAG, "setCalendar(): " + calendar.toString());
        this.calendar = calendar;
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MonthlyFragment.OnFragmentInteractionListener mListener;
    private Context mContext;

    public WeeklyFragment() {
        dayLayout = new LinearLayout[7];

        dateView = new TextView[7];
        listView = new ListView[7];

        weekAdapter = new WeekAdapter[7];
        scheduleList = new ArrayList<>();

        scheduleCount = new int[7];
        dayMatch = new int[31];
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeeklyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeeklyFragment newInstance(String param1, String param2) {
        WeeklyFragment fragment = new WeeklyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()");
        preButton = view.findViewById(R.id.weekPrevious);
        nextButton = view.findViewById(R.id.weekNext);
        textView = view.findViewById(R.id.weekText);

        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                setTextViewAndDateViews();
                updateScheduleListViews();

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, +7);
                setTextViewAndDateViews();
                updateScheduleListViews();
            }
        });

        int[] layoutIds = new int[] { R.id.sundayLayout, R.id.mondayLayout,
                R.id.tuesdayLayout, R.id.wednesdayLayout, R.id.thursdayLayout,
                R.id.fridayLayout, R.id.saturdayLayout };
        int[] dateIds = new int[] { R.id.sunday, R.id.monday, R.id.tuesday,
                R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday };
        int[] listIds = new int[] { R.id.sundayList, R.id.mondayList,
                R.id.tuesdayList, R.id.wednesdayList, R.id.thursdayList,
                R.id.fridayList, R.id.saturdayList};

        for (int i = 0; i < 7; i++) {
            dayLayout[i] = view.findViewById(layoutIds[i]);
            dayLayout[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        LinearLayout layout = (LinearLayout) v;
                        if (layout == null) {
                            Log.d(TAG, "layout is null");
                            return false;
                        }
                        else {
                            Log.d(TAG, "layout is not null");
                        }

                        handleClickEvent((int)
                                layout.getTag(BasicInfo.DATE_INFORMATION));
                    }

                    return true;
                }
            });

            dateView[i] = view.findViewById(dateIds[i]);
            if (i == 0) dateView[i].setTextColor(Color.RED);
            else if (i == 6) dateView[i].setTextColor(Color.BLUE);
            else dateView[i].setTextColor(Color.BLACK);

            listView[i] = view.findViewById(listIds[i]);
            weekAdapter[i] = new WeekAdapter(mContext);
            listView[i].setAdapter(weekAdapter[i]);
            listView[i].setBackgroundResource(R.drawable.week_back_line);

            listView[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch() - " + event.toString());
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ListView layout = (ListView) v;
                        if (layout == null) {
                            Log.d(TAG, "ListView is null");
                            return false;
                        }
                        else {
                            Log.d(TAG, "ListView is not null");
                        }

                        handleClickEvent((int)
                                layout.getTag(BasicInfo.DATE_INFORMATION));
                    }

                    return true;
                }
            });
        }

        getToday();

        setTextViewAndDateViews();

        mListener.onSetFragmentNumber(1);
    }

    public void getToday() {
        todayYear = CalendarActivity.getTodayYear();
        todayMonth = CalendarActivity.getTodayMonth();
        todayDay = CalendarActivity.getTodayDay();
    }

    public void setTextViewAndDateViews() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String dateString = calendar.get(Calendar.YEAR) + "년 "
                + (calendar.get(Calendar.MONTH) + 1) + "월 ";
        textView.setText(dateString);

        if (todayYear == year && todayMonth == month) {
            textView.setTextColor(Color.RED);
        }
        else {
            textView.setTextColor(Color.BLACK);
        }

        String[] days = new String[] {"일", "월", "화", "수", "목", "금", "토"};

        Log.d(TAG, calendar.toString());

        Date date = calendar.getTime();

        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        int d;
        for (int i = 0; i < 7; i++) {
            dateString  =
                    days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
                    + " "
                    + (d = calendar.get(Calendar.DAY_OF_MONTH));
            dateView[i].setText(dateString);

            if (todayYear == year && todayMonth == month && todayDay == d) {
                dateView[i].setTypeface(null, Typeface.BOLD_ITALIC);
            }
            else {
                dateView[i].setTypeface(null, Typeface.NORMAL);
            }

            Log.d(TAG, dayOfWeek + ", i: " + i);
            if (dayOfWeek == (i + 1)) {
                dateView[i].setBackgroundResource(R.drawable.text_back_line);
            }
            else {
                dateView[i].setBackgroundResource(0);
            }

            dayLayout[i].setTag(BasicInfo.DATE_INFORMATION, d);
            listView[i].setTag(BasicInfo.DATE_INFORMATION, d);

            calendar.add(Calendar.DAY_OF_MONTH, +1);
        }

        calendar.setTime(date);
    }

    public void updateScheduleListViews() {
        Date date = calendar.getTime();

        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            calendar.add(Calendar.DAY_OF_MONTH, -1);

        for (int i = 0; i < 7; i++) {
            String SQL
                    = "select * from " + CalendarActivity.mDatabase.TABLE_SCHEDULE
                    + " where INPUT_DATE = '"
                    + BasicInfo.dateFormat.format(calendar.getTime())
                    + "'";
            scheduleList = CalendarActivity.rawQuery(SQL, null);
            scheduleCount[i] = scheduleList.size();
            dayMatch[calendar.get(Calendar.DAY_OF_MONTH) - 1] = i;
            weekAdapter[i].setScheduleList(scheduleList);
            weekAdapter[i].notifyDataSetChanged();

            calendar.add(Calendar.DAY_OF_MONTH, +1);
        }

        calendar.setTime(date);
    }

    private void handleClickEvent(int day) {
        if (day == calendar.get(Calendar.DAY_OF_MONTH)) {
            Log.d(TAG, "handleClickEvent(day: " + day + ") - 1");
            Log.d(TAG, "dayMatch: " + dayMatch[day - 1]);
            Log.d(TAG, "scheduleCount: "
                    + scheduleCount[dayMatch[day - 1]]);

            if (scheduleCount[dayMatch[day - 1]] > 0) {
                ArrayList<ScheduleItem> items = null;

                String SQL = "select * from " + CalendarActivity.mDatabase.TABLE_SCHEDULE
                        + " where INPUT_DATE = '"
                        + BasicInfo.dateFormat.format(calendar.getTime())
                        + "'";
                items = CalendarActivity.rawQuery(SQL, null);
                mListener.onShowScheduleListDialog(items,
                        calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY,
                        BasicInfo.SHOW_SCHEDULE);
            }
            else {
                ScheduleItem item = new ScheduleItem();
                item.setYear(calendar.get(Calendar.YEAR));
                item.setMonth(calendar.get(Calendar.MONTH) + 1);
                item.setDay(calendar.get(Calendar.DAY_OF_MONTH));

                mListener.onShowScheduleActivity(item, true);
            }
        }
        else {
            Log.d(TAG, "handleClickEvent(day: " + day + ") - 2");
            calendar.set(Calendar.DAY_OF_MONTH, day);
            setTextViewAndDateViews();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MonthlyFragment.OnFragmentInteractionListener) {
            mContext = context;
            mListener = (MonthlyFragment.OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
     */

    @Override
    public void onStart() {
        super.onStart();
        Log.d("WeeklyFragment", "onStart()");

        updateScheduleListViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("WeeklyFragment", "onResume()");
    }

    public void updateUI() {
        setTextViewAndDateViews();
        updateScheduleListViews();
    }
}
