package com.and1ss.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.UUID;

public class DeleteItemFragment extends DialogFragment {

    private static final String ARG_CRIME_ID = "CRIME_ID";
    public static final String EXTRA_DELETED = "DELETED";
    public static final String EXTRA_INDEX = "INDEX";

    private Crime mCrime;

    private TextView mCrimeTitle;
    private TextView mCrimeDate;

    private ImageView mCrimeImage;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.list_item_crime, null);
        float dip = 8f;
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        view.setPadding(px, px, px, px);

        UUID id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(id);

        mCrimeTitle = (TextView) view.findViewById(R.id.crime_title);
        mCrimeTitle.setText(mCrime.getTitle());

        mCrimeDate = (TextView) view.findViewById(R.id.crime_date);
        mCrimeDate.setText(mCrime.getDate().toString());

        mCrimeImage = (ImageView) view.findViewById(R.id.crime_solved);
        if(mCrime.isSolved() == false) {
            mCrimeImage.setVisibility(View.GONE);
        }

        Dialog dialog = new AlertDialog.Builder(getActivity())
                                       .setTitle("")
                                       .setPositiveButton(R.string.delete_item_yes, new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {
                                                CrimeLab crimeLab = CrimeLab.get(getContext());
                                                int index = crimeLab.getIndex(mCrime.getId());
                                                crimeLab.deleteCrime(mCrime);
                                                sendResult(Activity.RESULT_OK, true, index);
                                                dialogInterface.cancel();
                                           }
                                       })
                                       .setNegativeButton(R.string.delete_item_no, new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {
                                               sendResult(Activity.RESULT_OK, false, -1);
                                               dialogInterface.cancel();
                                           }
                                       })
                                       .setTitle(R.string.delete_item_title)
                                       .setView(view)
                                       .create();
        return dialog;
    }

    public static DeleteItemFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, id);

        DeleteItemFragment fragment = new DeleteItemFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void sendResult(int resultCode, boolean deleted, int index) {
        if(getTargetFragment() == null) {
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_DELETED, deleted);
        data.putExtra(EXTRA_INDEX, index);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, data);
    }
}
