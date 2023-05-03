package com.example.calculadorhoras;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;

    public class Admin2 extends AppCompatActivity {    private RecyclerView mRecyclerView;
        private TextView tvnombre;
        private MiAdapter2 mAdapter;
        private ArrayList<Registro> mRegistros;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin2);

            // Obtener la referencia al RecyclerView
            mRecyclerView = findViewById(R.id.rvRegistros);

            // Obtener el correo electrónico del usuario desde los extras del intent
            String nombre = getIntent().getStringExtra("nombre");
            String correo = getIntent().getStringExtra("correo");
            tvnombre = findViewById(R.id.tvNombreUsuario);
            tvnombre.setText(correo);

            // Configurar el RecyclerView
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new MiAdapter2();
            mRecyclerView.setAdapter(mAdapter);

            // Obtener la referencia a la base de datos
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usuariosRef = database.getReference("usuarios");
            Query query = usuariosRef.orderByChild("correo").equalTo(correo);

            // Leer los datos de Firebase
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Recorre la lista de usuarios que coinciden con la consulta.
                    for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                        // Obtén el correo electrónico del usuario.
                       // String correo = usuarioSnapshot.getKey();

                        // Usa el correo para obtener los registros del usuario.
                        DatabaseReference registrosRef = usuariosRef.child(correo.replace(".", "")).child("Registros");
                        registrosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mRegistros = new ArrayList<>();
                                for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                                    // Obtén los datos del registro
                                    String idRegistro = registroSnapshot.getKey(); // Aquí obtenemos el ID del registro
                                    String tipoRegistro = registroSnapshot.child("tipo").getValue(String.class);
                                    String incidenciaRegistro = registroSnapshot.child("incidencia").getValue(String.class);

                                    // Crea un objeto Registro y agrégalo a la lista
                                    Registro registro = new Registro(idRegistro, tipoRegistro, incidenciaRegistro);
                                    mRegistros.add(registro);
                                }

                                // Actualiza el adaptador del RecyclerView con la lista de registros
                                mAdapter.setRegistros(mRegistros);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Manejar errores de lectura de la base de datos.
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar errores de lectura de la base de datos.
                }
            });
        }
    }