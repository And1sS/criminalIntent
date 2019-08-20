package com.and1ss.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

public class DateFragment extends Fragment {

    private static final String ARG_DATE = "DATE";

    private IDateReciever mDateReciever;

    private DatePicker mDatePicker;

    private Date mDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        mDate = (Date) getArguments().getSerializable(ARG_DATE);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_date, container, false);

        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_picker);
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        mDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker,
                                      int year, int month,
                                      int dayOfMonth) {
                mDateReciever.onRecieveDate(getDateFromDatePicker(mDatePicker));
            }
        });

        return view;
    }

    public static DateFragment newInstance(Date date, IDateReciever mDateReciever) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DateFragment dateFragment = new DateFragment();
        dateFragment.setArguments(args);
        dateFragment.setReciever(mDateReciever);

        return dateFragment;
    }

    private static Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    private void setReciever(IDateReciever reciever) {
        mDateReciever = reciever;
    }
}
