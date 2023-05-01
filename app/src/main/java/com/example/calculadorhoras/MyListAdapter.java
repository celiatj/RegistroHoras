package com.example.calculadorhoras;

import static com.example.calculadorhoras.R.layout.nombresview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] maintitle;



    public MyListAdapter(Activity context, String[] maintitle) {
        super(context, R.layout.nombresview, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;



    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(nombresview, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.tvNombres);



        titleText.setText(maintitle[position]);



        return rowView;

    };
}

