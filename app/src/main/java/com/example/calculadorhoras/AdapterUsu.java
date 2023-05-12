package com.example.calculadorhoras;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

    public class AdapterUsu extends RecyclerView.Adapter<AdapterUsu.ViewHolder> {
        private ArrayList<RegistroUsu> mRegistrosU;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView midTextView;
            public TextView mTipoTextView;
            public TextView mIncidenciaTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                midTextView = itemView.findViewById(R.id.tvDia);
                mTipoTextView = itemView.findViewById(R.id.tvTipo);
                mIncidenciaTextView = itemView.findViewById(R.id.tvIncidencia);
            }
        }
        public void setRegistros(ArrayList<RegistroUsu> registros) {
            mRegistrosU = registros;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.regusuview, parent, false);
            AdapterUsu.ViewHolder viewHolder = new AdapterUsu.ViewHolder(v);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RegistroUsu registro = mRegistrosU.get(position);
            holder.midTextView.setText(registro.getTextoId());
            holder.mTipoTextView.setText(registro.getTipo());
            holder.mIncidenciaTextView.setText(registro.getIncidencia());
        }

        @Override
        public int getItemCount() {
            if (mRegistrosU == null) {
                return 0;
            }
            return mRegistrosU.size();
        }    }