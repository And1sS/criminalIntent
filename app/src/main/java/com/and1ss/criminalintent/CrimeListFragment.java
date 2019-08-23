package com.and1ss.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private final int REQUEST_DELETE = 0;
    private final int REQUEST_ADD = 1;

    private final String DELETE_ITEM_TAG = "DELETE_ITEM";
    private final String ADD_ITEM_TAG = "ADD_ITEM";

    private final String ARG_SHOW_SUBTITLE = "SHOW_SUBTITLE";

    private RecyclerView mRecyclerView;

    private CrimeAdapter mCrimeAdapter;

    private DividerItemDecoration mDividerItemDecoration;

    private boolean mShowSubtitle;

    private Callbacs mCallbacs;

    /**
     * public interface for hosting activities
     */
    public interface Callbacs {
        void onCrimeSelected(Crime crime);

        void onCrimeDeleted(String uuidString);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState != null) {
            mShowSubtitle = savedInstanceState.getBoolean(ARG_SHOW_SUBTITLE);
        }

        if(mShowSubtitle) {
            updateSubtitle();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder{

        private Crime mCrime;

        private TextView mTitleTextView;
        private TextView mDateTextView;

        private ImageView mImageView;

        private GestureDetectorCompat mGestureDetector;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mImageView = (ImageView) itemView.findViewById(R.id.crime_solved);

            mGestureDetector = new GestureDetectorCompat(getContext(),
                    new GestureDetector.OnGestureListener() {
                        @Override
                        public boolean onDown(MotionEvent motionEvent) {
                            return true;
                        }

                        @Override
                        public void onShowPress(MotionEvent motionEvent) { }

                        @Override
                        public boolean onSingleTapUp(MotionEvent motionEvent) {
                            mCallbacs.onCrimeSelected(mCrime);
                            return true;
                        }

                        @Override
                        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                            return false;
                        }

                        @Override
                        public void onLongPress(MotionEvent motionEvent) {
                            FragmentManager fragmentManager = getFragmentManager();

                            DeleteItemFragment deleteItemFragment = DeleteItemFragment.newInstance(mCrime.getId());
                            deleteItemFragment.setTargetFragment(CrimeListFragment.this, REQUEST_DELETE);
                            deleteItemFragment.show(fragmentManager, DELETE_ITEM_TAG);
                        }

                        @Override
                        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                            return false;
                        }
            });

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mGestureDetector.onTouchEvent(motionEvent);
                    return true;
                }
            });
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDate().toString());
            mImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return new CrimeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() { return mCrimes.size(); }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacs = (Callbacs) context;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.mCrimes = CrimeLab.get(getActivity()).getCrimes();
            mCrimeAdapter.notifyDataSetChanged();
        }

        if(mDividerItemDecoration == null) {
            mDividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            mRecyclerView.addItemDecoration(mDividerItemDecoration);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_DELETE) {
            boolean deleted = data.getBooleanExtra(DeleteItemFragment.EXTRA_DELETED, false);

            if(deleted) {
                int index = data.getIntExtra(DeleteItemFragment.EXTRA_INDEX, -1);
                String uuidString = data.getStringExtra(DeleteItemFragment.EXTRA_UUID);
                mCrimeAdapter.mCrimes = CrimeLab.get(getActivity()).getCrimes();
                mCrimeAdapter.notifyItemRemoved(index);

                mCallbacs.onCrimeDeleted(uuidString);
            }
        } else if(requestCode == REQUEST_ADD) {
            boolean added = data.getBooleanExtra(AddItemFragment.EXTRA_ADDED, false);

            if(added) {
                mCrimeAdapter.mCrimes = CrimeLab.get(getActivity()).getCrimes();
                mCrimeAdapter.notifyItemInserted(mCrimeAdapter.mCrimes.size() - 1);
                AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                String title = null;

                if(mShowSubtitle) {
                    int count = CrimeLab.get(getActivity()).getCrimes().size();
                    title = getString(R.string.subtitle_format,  count);
                }
                appCompatActivity.getSupportActionBar().setTitle(title);
            }
        } else return;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        if(mShowSubtitle) {
            menu.findItem(R.id.show_subtitle)
                    .setTitle(R.string.hide_subtitle);
        } else {
            menu.findItem(R.id.show_subtitle)
                    .setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.new_crime:
                FragmentManager fragmentManager = getFragmentManager();
                AddItemFragment dialog = new AddItemFragment();
                dialog.setTargetFragment(CrimeListFragment.this, REQUEST_ADD);
                dialog.show(fragmentManager, ADD_ITEM_TAG);
                return true;
            case R.id.show_subtitle:
                mShowSubtitle = !mShowSubtitle;
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        int count = CrimeLab
                .get(getActivity()).getCrimes().size();
        String temp = getString(R.string.subtitle_format, count);

        if(!mShowSubtitle) {
            temp = null;
        }
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setTitle(temp);
        appCompatActivity.invalidateOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_SHOW_SUBTITLE, mShowSubtitle);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacs = null;
    }
}
