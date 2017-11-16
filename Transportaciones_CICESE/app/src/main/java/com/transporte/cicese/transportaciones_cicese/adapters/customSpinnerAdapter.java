package com.transporte.cicese.transportaciones_cicese.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.transporte.cicese.transportaciones_cicese.R;
import java.util.ArrayList;

/**
 * Created by blanca on 29/10/2017.
 */

public class customSpinnerAdapter extends BaseAdapter {
    Activity activity;
    String[] elements;
    LayoutInflater inflater;
    ArrayList indexElement;

    public customSpinnerAdapter(Activity activity, String [] solicitudes, ArrayList indexElement){
        this.activity       =   activity;
        this.elements       =   solicitudes;
        this.indexElement   =   indexElement;
        inflater            =   (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount(){
        return elements.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    public String getId(int i){
        return indexElement.get(i).toString();
    }

    public Integer getIndex(String i){ return indexElement.indexOf(i);}

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = inflater.inflate(R.layout.layout_spinner_adapter,null);

        TextView titleTV = (TextView) row.findViewById(R.id.title);
        titleTV.setText(elements[i]);
        return row;
    }


}
