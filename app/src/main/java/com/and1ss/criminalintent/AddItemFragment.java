package com.and1ss.criminalintent;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Date;

public class AddItemFragment extends DialogFragment {

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private final int REQUEST_DATE = 0;
    private final int REQUEST_TIME = 1;


    public static final String EXTRA_ADDED = "ADDED";

    private EditText mTitleEdit;

    private Button mData;

    private CheckBox mSolved;

    private Crime mCrime;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_crime_add, null);

        mCrime = new Crime();

        mTitleEdit = (EditText) view.findViewById(R.id.crime_title);
        mTitleEdit.setText("Crime #" + CrimeLab.get(getActivity()).getCrimes().size());

        mData = (Button) view.findViewById(R.id.crime_date);
        mData.setText(mCrime.getDate().toString());

        mData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DateTimePickerFragment datePickerFragment = DateTimePickerFragment.newInstance(mCrime.getDate());
                datePickerFragment.setTargetFragment(AddItemFragment.this, REQUEST_DATE);
                datePickerFragment.show(manager, DIALOG_DATE);
            }
        });

        mSolved = (CheckBox) view.findViewById(R.id.crime_solved);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.new_crime)
                .setPositiveButton(R.string.add_crime, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (mTitleEdit.getText().length() == 0 ||
                                mTitleEdit.getText().length() > 100) {
                            int colorFrom = getResources().getColor(R.color.colorEditAccent);
                            int colorTo = getResources().getColor(R.color.colorGray);
                            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                            colorAnimation.setDuration(750); // milliseconds
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    mTitleEdit.setHintTextColor((int) animator.getAnimatedValue());
                                }

                            });
                            colorAnimation.start();
                        } else {
                            mCrime.setTitle(mTitleEdit.getText().toString());
                            mCrime.setSolved(mSolved.isChecked());

                            CrimeLab.get(getActivity()).addCrime(mCrime);
                            sendResult(Activity.RESULT_OK, true);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    private void sendResult(int resultCode, boolean added) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_ADDED, added);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            mCrime.setDate((Date) data.getSerializableExtra(DateTimePickerFragment.EXTRA_DATE));
            mData.setText(mCrime.getDate().toString());
        }
    }
}

