package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.View;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;
    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PET_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    private PetDbHelper petDbHelper;
    @Override
    public boolean onCreate() {
        petDbHelper= new PetDbHelper(getContext());


        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = petDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor=null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor=database.query(PetContract.PetsEntry.TABLE_NAME,projection,null,null,null,null,sortOrder);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetContract.PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetContract.PetsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return insertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException ("Insertion is not supported");
        }
       // return null;
    }
    public Uri insertPet(Uri uri,ContentValues contentValues)
    {


        if (contentValues.containsKey(PetContract.PetsEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (contentValues.containsKey(PetContract.PetsEntry.COLUMN_PET_BREED)) {
            String breed = contentValues.getAsString(PetContract.PetsEntry.COLUMN_PET_BREED);
            if (breed == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (contentValues.containsKey(PetContract.PetsEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = contentValues.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        // Get writable database
        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        long id=database.insert(PetContract.PetsEntry.TABLE_NAME,null,contentValues);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);

    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }
    public int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs)
    {
        if (contentValues.size() == 0) {
            return 0;
        }



        if (contentValues.containsKey(PetContract.PetsEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (contentValues.containsKey(PetContract.PetsEntry.COLUMN_PET_BREED)) {
            String breed = contentValues.getAsString(PetContract.PetsEntry.COLUMN_PET_BREED);
            if (breed == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (contentValues.containsKey(PetContract.PetsEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = contentValues.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        // Get writable database
        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        int rows=database.update(PetContract.PetsEntry.TABLE_NAME,contentValues,selection,selectionArgs);

        getContext().getContentResolver().notifyChange(uri,null);
        return rows;

    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
   @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
       // Get writeable database
       SQLiteDatabase database = petDbHelper.getWritableDatabase();
        int rowsDeleted=0;
       final int match = sUriMatcher.match(uri);
       switch (match) {
           case PETS:
               // Delete all rows that match the selection and selection args
               rowsDeleted=database.delete(PetContract.PetsEntry.TABLE_NAME, selection, selectionArgs);
               break;
           case PET_ID:
               // Delete a single row given by the ID in the URI
               selection = PetContract.PetsEntry._ID + "=?";
               selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
               rowsDeleted=database.delete(PetContract.PetsEntry.TABLE_NAME, selection, selectionArgs);
               break;
           default:
               throw new IllegalArgumentException("Deletion is not supported for " + uri);


       }
       if (rowsDeleted != 0) {
           getContext().getContentResolver().notifyChange(uri, null);
       }
       return rowsDeleted;
   }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.PetsEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}