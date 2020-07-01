package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.pets.data.PetContract.PetsEntry;

/**
 * Created by Ahmed Magdy on 6/25/2020.
 */

public class PetDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Shlter.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_PETS_TABLE =
            "CREATE TABLE " + PetsEntry.TABLE_NAME + " ("
                    + PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PetsEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                    + PetsEntry.COLUMN_PET_BREED + " TEXT, "
                    + PetsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                    + PetsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";


    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*
    sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
    onCreate(sqLiteDatabase);
    8*/
    }
}
