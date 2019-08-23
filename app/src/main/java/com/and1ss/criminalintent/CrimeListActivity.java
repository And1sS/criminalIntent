package com.and1ss.criminalintent;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
    implements CrimeListFragment.Callbacs, CrimeFragment.Callbacs {

    private Fragment mDetailsFragment;

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            mDetailsFragment = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,
                            mDetailsFragment)
                    .commit();
        }
    }

    @Override
    public void onCrimeDeleted(String uuidString) {
        if(mDetailsFragment != null &&
                ((CrimeFragment) mDetailsFragment).getUUID()
                .equals(uuidString))
        getSupportFragmentManager().beginTransaction()
                .remove(mDetailsFragment)
                .commit();
    }

    @Override
    public void onCrimeUpdate(Crime crime) {
        if(mDetailsFragment != null) {
            CrimeLab.get(this)
                    .updateCrime(crime);
            ((CrimeListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container))
                    .updateUI();
            Log.d("debug1", "CALLBACK!");
        }
    }
}
