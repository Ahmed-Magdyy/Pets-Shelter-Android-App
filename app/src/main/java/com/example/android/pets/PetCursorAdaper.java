package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Ahmed Magdy on 6/30/2020.
 */

public class PetCursorAdaper extends CursorAdapter{

    public PetCursorAdaper(Context context,Cursor cursor)
    {
        super(context,cursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        int nameId=cursor.getColumnIndex("name");
        int breedId=cursor.getColumnIndex("breed");
        String name=cursor.getString(nameId);
        String summary=cursor.getString(breedId);
        if(TextUtils.isEmpty(summary))
            summary=("Unknown Breed");
        nameTextView.setText(name);
        summaryTextView.setText(summary);
    }
}
