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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //DailyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DailyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String TAG = "DailyFragment";

    private Button preButton;
    private Button nextButton;
    private TextView textView;

    private LinearLayout linearLayout;
    private ListView listView;

    private int todayYear;
    private int todayMonth;
    private int todayDay;

    private DayAdapter dayAdapter;
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

    public DailyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyFragment newInstance(String param1, String param2) {
        DailyFragment fragment = new DailyFragment();
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
        return inflater.inflate(R.layout.fragment_daily, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()");
        preButton = view.findViewById(R.id.dayPrevious);
        nextButton = view.findViewById(R.id.dayNext);
        textView = view.findViewById(R.id.dayText);

        linearLayout = view.findViewById(R.id.linearLayout);
        listView = view.findViewById(R.id.dailySchedule);
        dayAdapter = new DayAdapter(mContext);
        listView.setAdapter(dayAdapter);

        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                setTextView();
                updateSchedules();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, +1);
                setTextView();
                updateSchedules();
            }
        });

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //handleClickEvent(calendar.get(Calendar.DAY_OF_MONTH));
                    Log.d(TAG, "LinearLayout - OnTouchListener()");
                }
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "ListView - OnItemClickListener() position: " + position);

                mListener.onShowScheduleActivity(scheduleList.get(position),
                        false);
            }
        });

        getToday();

        setTextView();

        updateSchedules();

        mListener.onSetFragmentNumber(2);
    }

    public void setTextView() {
        String[] days = new String[] {"일", "월", "화", "수", "목", "금", "토"};

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dateString = year + "년 " + (month + 1) + "월 " + day + " 일";
        dateString += " " + days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
                    + "요일";
        textView.setText(dateString);

        Log.d(TAG, "today(" + todayYear + ", " + todayMonth + ", " + todayDay + ")");
        Log.d(TAG, "day(" + year + ", " + month + ", " + day + ")");

        if (todayYear == year && todayMonth == month &&
            todayDay == day) {
            textView.setTypeface(null, Typeface.BOLD_ITALIC);
            Log.d(TAG, "굵게");
        }
        else {
            textView.setTypeface(null, Typeface.NORMAL);
            Log.d(TAG, "보통");
        }

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            textView.setTextColor(Color.RED);
        }
        else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            textView.setTextColor(Color.BLUE);
        }
        else {
            textView.setTextColor(Color.BLACK);
        }
    }

    public void updateSchedules() {
        String SQL
                = "select * from " + CalendarActivity.mDatabase.TABLE_SCHEDULE
                + " where INPUT_DATE = '"
                + BasicInfo.dateFormat.format(calendar.getTime())
                + "'";
        scheduleList = CalendarActivity.rawQuery(SQL, null);
        dayAdapter.setScheduleList(scheduleList);
        dayAdapter.notifyDataSetChanged();
    }

    public void getToday() {
        todayYear = CalendarActivity.getTodayYear();
        todayMonth = CalendarActivity.getTodayMonth();
        todayDay = CalendarActivity.getTodayDay();
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
        Log.d("DailyFragment", "onStart()");

        updateSchedules();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("DailyFragment", "onResume()");
    }

    public void updateUI() {
        setTextView();
        updateSchedules();
    }
}
