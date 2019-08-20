package com.and1ss.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Calendar;
import java.util.Date;

public class DateTimePickerFragment extends DialogFragment implements IDateReciever{

    private static final int DATE_PAGE = 0;
    private static final int TIME_PAGE = 1;
    private static final int PAGES_COUNT = 2;

    private static final String ARG_DATE = "DATE";
    public static final String EXTRA_DATE = "DATE";

    private MyPager mViewPager;

    private Button mLeftButton;
    private Button mRightButton;

    private DateFragment mDateFragment;
    private TimeFragment mTimeFragment;

    private Date mDatePicked;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Date mDate = (Date) getArguments().getSerializable(ARG_DATE);
        mDatePicked = new Date(mDate.getTime());

        mDateFragment = DateFragment.newInstance(mDatePicked, this);
        mTimeFragment = TimeFragment.newInstance(mDatePicked, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.date_time_dialog, null);

        FragmentManager fragmentManager = getChildFragmentManager();
        mViewPager = (MyPager) view.findViewById(R.id.date_time_pager);
        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                if(position == DATE_PAGE) {
                    return mDateFragment;
                } else if(position == TIME_PAGE) {
                    return mTimeFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return PAGES_COUNT;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if(position == DATE_PAGE) {
                    mLeftButton.setText(R.string.cancel_title);
                    mRightButton.setText(R.string.next_title);
                } else {
                    mLeftButton.setText(R.string.back_title);
                    mRightButton.setText(R.string.save_title);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        mLeftButton = (Button) view.findViewById(R.id.positive);
        mRightButton = (Button) view.findViewById(R.id.negative);

        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewPager.getCurrentItem() == DATE_PAGE) {
                    getTargetFragment()
                            .onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                    dismiss();
                } else {
                    mViewPager.setCurrentItem(DATE_PAGE);
                }
            }
        });

        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewPager.getCurrentItem() == DATE_PAGE) {
                    mViewPager.setCurrentItem(TIME_PAGE);
                } else {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_DATE, mDatePicked);

                    getTargetFragment()
                            .onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
                    dismiss();
                }
            }
        });

        return view;
    }

    public static DateTimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DateTimePickerFragment fragment = new DateTimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onRecieveDate(Date date) {
        mDatePicked.setTime(date.getTime());
    }

    @Override
    public void onRecieveTime(int hour, int minute) {
        mDatePicked.setHours(hour);
        mDatePicked.setMinutes(minute);
    }
}
