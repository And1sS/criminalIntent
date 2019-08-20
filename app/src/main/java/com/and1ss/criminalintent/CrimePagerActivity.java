package com.and1ss.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity{

    private static final String EXTRA_CRIME_ID =
            "com.and1ss.android.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    private Button mFirstCrimeButton;
    private Button mLastCrimeButton;

    private UUID mCurrentCrimeId;

    private FragmentStatePagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mCrimes = CrimeLab.get(getApplicationContext()).getCrimes();
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mAdapter = new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                mCurrentCrimeId = mCrimes.get(position).getId();
                return CrimeFragment.newInstance(mCurrentCrimeId);
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        };
        mViewPager.setAdapter(mAdapter);

        int currentIndex = CrimeLab.get(getApplicationContext()).getIndex(crimeId);
        if(currentIndex != CrimeLab.CRIME_NON_FOUND) {
            mViewPager.setCurrentItem(currentIndex);
        } else {
            mViewPager.setCurrentItem(0);
        }

        mFirstCrimeButton = (Button) findViewById(R.id.first_page_button);
        mFirstCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        mLastCrimeButton = (Button) findViewById(R.id.last_page_button);
        mLastCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });
    }

    public static Intent newIntent(Context packageContext, UUID id) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, id);
        return intent;
    }
}
