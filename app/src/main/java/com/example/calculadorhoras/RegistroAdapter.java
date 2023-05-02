package com.example.calculadorhoras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {
    private ArrayList<Registro> listaRegistros;

    public RegistroAdapter(ArrayList<Registro> listaRegistros) {
        this.listaRegistros = listaRegistros;
    }

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.registrosview, parent, false);
        return new RegistroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        Registro registro = listaRegistros.get(position);

        // Obtener los datos del registro y mostrarlos en los TextView correspondientes
        holder.tvDia.setText(registro.getDia());
        holder.tvHoraEntrada.setText(registro.getHoraEntrada());
        holder.tvHoraSalida.setText(registro.getHoraSalida());
        holder.tvTiempoTotal.setText(String.valueOf(registro.getTiempoTotal()));
    }

    @Override
    public int getItemCount() {
        return listaRegistros.size();
    }

    public static class RegistroViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDia;
        public TextView tvHoraEntrada;
        public TextView tvHoraSalida;
        public TextView tvTiempoTotal;

        public RegistroViewHolder(View itemView) {
            super(itemView);
            tvDia = itemView.findViewById(R.id.tvDia);
            tvHoraEntrada = itemView.findViewById(R.id.tvHoraEntrada);
            tvHoraSalida = itemView.findViewById(R.id.tvHoraSalida);
            tvTiempoTotal = itemView.findViewById(R.id.tvTiempoTotal);
        }
    }
}