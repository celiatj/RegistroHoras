package com.example.calculadorhoras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

    public class MiAdapter2 extends RecyclerView.Adapter<MiAdapter2.ViewHolder> {

        private ArrayList<Registro> mRegistros;

        // Clase interna para representar cada elemento del RecyclerView
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTipoTextView;
            public TextView mIncidenciaTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mTipoTextView = itemView.findViewById(R.id.tipoTextView);
                mIncidenciaTextView = itemView.findViewById(R.id.incidenciaTextView);
            }
        }

        public void setRegistros(ArrayList<Registro> registros) {
            mRegistros = registros;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.registrosview, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Registro registro = mRegistros.get(position);
            holder.mTipoTextView.setText(registro.getTipo());
            holder.mIncidenciaTextView.setText(registro.getIncidencia());
        }

        @Override
        public int getItemCount() {
            if (mRegistros == null) {
                return 0;
            }
            return mRegistros.size();
        }
    }