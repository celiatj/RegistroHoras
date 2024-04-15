package com.example.calculadorhoras;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class AdapterUsu extends RecyclerView.Adapter<AdapterUsu.ViewHolder> {
    private ArrayList<RegistroUsu> mRegistrosU;
    private double ubicacionLatitude,registroLatitude;
    private double ubicacionLongitude, registroLongitude;
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

    public AdapterUsu(double ubicacionLatitude, double ubicacionLongitude) {
        this.ubicacionLatitude = ubicacionLatitude;
        this.ubicacionLongitude = ubicacionLongitude;

    }

    public void setRegistros(ArrayList<RegistroUsu> registros) {
        mRegistrosU = registros;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.regusuview, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroUsu registro = mRegistrosU.get(position);
        holder.midTextView.setText(registro.getTextoId());
        holder.mTipoTextView.setText(registro.getTipo());
        holder.mIncidenciaTextView.setText(registro.getIncidencia());

        //Verificar si la ubicación existe
        if (registro.getUbicacion() != null) {
             this.registroLatitude = Double.parseDouble(registro.getUbicacion().get("latitude"));
             this.registroLongitude = Double.parseDouble(registro.getUbicacion().get("longitude"));


            if (distanciaEntreCoordenadas(registroLatitude, registroLongitude, ubicacionLatitude, ubicacionLongitude) <= 2) {
                holder.mTipoTextView.setBackgroundColor(Color.GREEN);
            } else {
                holder.mTipoTextView.setBackgroundColor(Color.RED);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mRegistrosU == null) {
            return 0;
        }
        return mRegistrosU.size();
    }

    private double distanciaEntreCoordenadas(double lat1, double lon1, double lat2, double lon2) {
        // Fórmula para calcular la distancia entre dos coordenadas geográficas (en este caso, la distancia euclidiana)
        double distanciaLat = Math.toRadians(lat2 - lat1);
        double distanciaLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(distanciaLat / 2) * Math.sin(distanciaLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(distanciaLon / 2) * Math.sin(distanciaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distancia = 6371 * c; // Radio de la Tierra en kilómetros

        return distancia;
    }
}