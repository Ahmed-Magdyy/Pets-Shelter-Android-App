/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetsEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;
    private boolean mPetHasChanged = false;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetsEntry.GENDER_UNKNOWN;
    private  Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner=(Spinner)findViewById(R.id.spinner_gender);
        setupSpinner();
        mNameEditText.setOnTouchListener(mTouchListner);
        mBreedEditText.setOnTouchListener(mTouchListner);
        mWeightEditText.setOnTouchListener(mTouchListner);
        mGenderSpinner.setOnTouchListener(mTouchListner);
        Intent intent= getIntent();
        uri=intent.getData();
        if(uri!=null)
            setTitle("Edit pet");
        invalidateOptionsMenu();
        getLoaderManager().initLoader(3, null, this);
    }
    private View.OnTouchListener mTouchListner=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged=true;
            return false;
        }
    };

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsEntry.GENDER_MALE;; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsEntry.GENDER_FEMALE;; // Female
                    } else {
                        mGender = PetsEntry.GENDER_UNKNOWN;; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                String nameString = mNameEditText.getText().toString().trim();
                String breedString = mBreedEditText.getText().toString().trim();
                String weightString = mWeightEditText.getText().toString().trim();
                int weight = Integer.parseInt(weightString);
                if (uri != null) {
                    ContentValues Values = new ContentValues();
                    Values.put(PetsEntry.COLUMN_PET_NAME, mNameEditText.getText().toString());
                    Values.put(PetsEntry.COLUMN_PET_BREED, mBreedEditText.getText().toString());
                    Values.put(PetsEntry.COLUMN_PET_WEIGHT, Integer.parseInt(mWeightEditText.getText().toString()));
                    Values.put(PetsEntry.COLUMN_PET_GENDER, mGender);
                    getContentResolver().update(uri, Values, null, null);
                    Toast.makeText(this, "pet updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (uri == null &&
                            TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) &&
                            TextUtils.isEmpty(weightString) && mGender == PetsEntry.GENDER_UNKNOWN) {
                        finish();
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PetsEntry.COLUMN_PET_NAME, mNameEditText.getText().toString());

                    contentValues.put(PetsEntry.COLUMN_PET_BREED, mBreedEditText.getText().toString());

                    int weightt = 0;
                    if (!TextUtils.isEmpty(weightString)) {
                        weightt = Integer.parseInt(weightString);
                    }
                    contentValues.put(PetsEntry.COLUMN_PET_WEIGHT, weightt);
                    contentValues.put(PetsEntry.COLUMN_PET_WEIGHT, Integer.parseInt(mWeightEditText.getText().toString()));
                    contentValues.put(PetsEntry.COLUMN_PET_GENDER, mGender);
                    getContentResolver().insert(PetContract.CONTENT_URI, contentValues);
                    Toast.makeText(this, "pet saved", Toast.LENGTH_SHORT).show();
                    finish();
                }


                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
        private void showDeleteConfirmationDialog() {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the postivie and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_dialog_msg);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Delete" button, so delete the pet.

                    getContentResolver().delete(uri,null,null);

                    finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Cancel" button, so dismiss the dialog
                    // and continue editing the pet.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }




    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (uri == null) {
            return null;
        }
        String[] mProjection =
                {
                        PetContract.PetsEntry._ID,
                        PetContract.PetsEntry.COLUMN_PET_NAME,
                        PetContract.PetsEntry.COLUMN_PET_BREED,
                        PetsEntry.COLUMN_PET_GENDER,
                        PetsEntry.COLUMN_PET_WEIGHT
                };
        return new CursorLoader(this,uri,mProjection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int weight = cursor.getInt(weightColumnIndex);
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));
            switch (gender) {
                case PetsEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetsEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (uri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

}