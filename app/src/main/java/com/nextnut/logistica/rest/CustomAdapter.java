package com.nextnut.logistica.rest;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.data.CustomColumns;

/**
 * Created by perez.juan.jose on 15/06/2016.
 */
public class CustomAdapter extends CursorAdapter {

    public CustomAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);


    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.spiner_custom_layout, parent, false);
        ViewsHolder holder = new ViewsHolder();
        holder.text1 = (TextView) v.findViewById(R.id.customNameSpinner);
        v.setTag(holder);
        return v;


    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewsHolder holder = (ViewsHolder) view.getTag();
        String text1 = cursor.getString(cursor.getColumnIndex(CustomColumns.LASTNAME_CUSTOM))+" "+cursor.getString(cursor.getColumnIndex(CustomColumns.NAME_CUSTOM));
        holder.text1.setText(text1);
    }


    class ViewsHolder {
        TextView text1;
        int idCustomer;

        public int getIdCustomer(){
            return idCustomer;
        }
    }

}
