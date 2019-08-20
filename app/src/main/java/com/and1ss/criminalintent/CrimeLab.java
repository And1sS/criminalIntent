package com.and1ss.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.CrimeBaseHelper;
import database.CrimeCursorWrapper;

import static database.CrimeDbSchema.*;

public class CrimeLab {

    public static final int CRIME_NON_FOUND = -1;

    private static CrimeLab sCrimeLab;

    private Context mContext;

    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if(sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return  sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
    }

    public List<Crime> getCrimes() {
        ArrayList<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper wrapper = queryCrimes(null, null);

        try {
            wrapper.moveToFirst();
            while (!wrapper.isAfterLast()) {
                crimes.add(wrapper.getCrime());
                wrapper.moveToNext();
            }
        } finally {
            wrapper.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id) {
        Crime result = null;
        CrimeCursorWrapper wrapper = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            wrapper.moveToFirst();
            result = wrapper.getCrime();
        } finally {
            wrapper.close();
        }

        return result;
    }

    public int getIndex(UUID id) {
        List<Crime> crimes = getCrimes();
        if(crimes != null) {
            for (int i = 0; i < crimes.size(); i++) {
                if(crimes.get(i).getId().equals(id)) {
                    return i;
                }
            }
        }
        return CRIME_NON_FOUND;
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        contentValues.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        contentValues.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return contentValues;
    }

   public void addCrime(Crime crime) {
        ContentValues contentValues = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, contentValues);
    }

    public void updateCrime(Crime crime) {
        String uuid = crime.getId().toString();
        ContentValues contentValues = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, contentValues,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuid });
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null,
                null
        );

        return new CrimeCursorWrapper(cursor);
    }

    public boolean deleteCrime(Crime crime) {
        return mDatabase.delete(
                CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { crime.getId().toString() }
        ) > 0;
    }

    public File getPhotoFile(Crime crime) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFileName());
    }
}
