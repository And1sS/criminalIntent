package com.and1ss.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

public class TimeFragment extends Fragment {
    private static final String ARG_DATE = "DATE";

    private IDateReciever mTimeReciever;

    private TimePicker mTimePicker;

    private Date mDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = (Date) getArguments().getSerializable(ARG_DATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_time, container, false);

        mTimePicker = view.findViewById(R.id.dialog_time_picker);

        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                mTimeReciever.onRecieveTime(hourOfDay, minute);
                Log.d("debug1", "onTimeChanged: " + hourOfDay);
                Log.d("debug1", "onTimeChanged: " + minute);
            }
        });

        return view;
    }

    public static TimeFragment newInstance(Date date, IDateReciever reciever) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        TimeFragment fragment = new TimeFragment();
        fragment.setArguments(args);
        fragment.setReciever(reciever);
        return fragment;
    }

    private void setReciever(IDateReciever reciever) {
        mTimeReciever = reciever;
    }
}
