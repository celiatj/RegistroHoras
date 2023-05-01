package com.example.calculadorhoras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MiAdapter extends RecyclerView.Adapter<MiAdapter.MiViewHolder> {
    private ArrayList<String> listaNombres;

    public MiAdapter(ArrayList<String> listaNombres) {
        this.listaNombres = listaNombres;
    }

    @NonNull

    public MiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MiViewHolder(v);
    }


    public void onBindViewHolder(@NonNull MiViewHolder holder, int position) {
        holder.nombreTextView.setText(listaNombres.get(position));
    }

    public int getItemCount() {
        return listaNombres.size();
    }

    public static class MiViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreTextView;

        public MiViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}

