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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        miRecyclerView = findViewById(R.id.rvNombres);
        miRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaNombres = new ArrayList<>();
        mostrarDatos();
    }

    private void mostrarDatos() {
        // Obtener referencia a la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("usuarios");

        // Leer los datos de Firebase
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Recorrer los datos y obtener los nombres de usuario
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String apellidos = snapshot.child("apellidos").getValue(String.class);
                    listaNombres.add(nombre+" "+apellidos);
                }

                // Mostrar los nombres en el RecyclerView
                MiAdapter adapter = new MiAdapter(listaNombres);
                miRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
            }
        });
    }
}
