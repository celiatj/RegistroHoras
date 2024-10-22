package com.example.calculadorhoras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
public class Administrador extends AppCompatActivity {

    private RecyclerView miRecyclerView;
    private FirebaseDatabase db;
    private ArrayList<String> listaNombres;
    private MiAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        miRecyclerView = findViewById(R.id.rvNombres);
        miRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaNombres = new ArrayList<>();
        mostrarDatos();

        // Definir el listener de clics en los elementos del RecyclerView
        MiAdapter.OnItemClickListener listener = new MiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String correo = listaNombres.get(position);
                Intent intent = new Intent(Administrador.this, Admin2.class);
                intent.putExtra("correo", correo);
                startActivity(intent);
            }
        };

        // Llamar al método setOnItemClickListener del adaptador
        adapter = new MiAdapter(listaNombres);
        adapter.setOnItemClickListener(listener);
        miRecyclerView.setAdapter(adapter);
    }

    private void mostrarDatos() {
        // Obtener referencia a la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("usuarios");

        // Leer los datos de Firebase
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Recorrer los datos y obtener los nombres y correos de usuario
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataSnapshot registrosSnapshot = snapshot.child("Registros");
                    if (registrosSnapshot.exists()) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        String correo = snapshot.child("correo").getValue(String.class);
                        listaNombres.add(correo);
                    }
                }

                // Notificar al adaptador de que los datos han cambiado
                adapter.notifyDataSetChanged();
            }

            @Override
               public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
            }
        });
    }}