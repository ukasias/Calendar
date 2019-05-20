package com.ukasias.android.calendar;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MonthlyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MonthlyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String TAG = "MonthlyFragment";

    private Button preButton;
    private Button nextButton;
    private TextView textView;
    private GridView gridView;
    private MonthAdapter monthAdapter;

    private Calendar calendar;

    public void setCalendar(Calendar calendar) {
        Log.d(TAG, "setCalendar(): " + calendar.toString());
        this.calendar = calendar;
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context mContext;

    public void setTextView() {
        String dateString = calendar.get(Calendar.YEAR) + "년 "
                + (calendar.get(Calendar.MONTH) + 1) + "월 ";
        textView.setText(dateString);
        textView.setTextColor(Color.BLACK);
    }

    public MonthlyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthlyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthlyFragment newInstance(String param1, String param2) {
        MonthlyFragment fragment = new MonthlyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_monthly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()");
        preButton = view.findViewById(R.id.monthPrevious);
        nextButton = view.findViewById(R.id.monthNext);
        textView = view.findViewById(R.id.monthText);
        gridView = view.findViewById(R.id.monthView);

        Log.d(TAG, "After calendar: " + calendar.get(Calendar.YEAR) + ", "
            + calendar.get(Calendar.MONTH) + ", "
                + calendar.get(Calendar.DAY_OF_MONTH));

        monthAdapter = new MonthAdapter(mContext, calendar);
        gridView.setAdapter(monthAdapter);

        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthAdapter.setPrevMonth();
                monthAdapter.notifyDataSetChanged();
                setTextView();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthAdapter.setNextMonth();
                monthAdapter.notifyDataSetChanged();
                setTextView();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick(): " + position);
                monthAdapter.onItemClicked(position);
            }
        });

        setTextView();

        mListener.onSetFragmentNumber(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mContext = context;
            mListener = (OnFragmentInteractionListener) context;
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
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onSetFragmentNumber(int n);
        void onShowScheduleActivity(ScheduleItem item, boolean isNewSchedule);
        void onShowScheduleListDialog(ArrayList<ScheduleItem> items, int day, int id);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MonthlyFragment", "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MonthlyFragment", "onResume()");
        monthAdapter.updateSchedule();
    }

    public void updateUI() {
        setTextView();
        monthAdapter.updateSchedule();
    }
}
