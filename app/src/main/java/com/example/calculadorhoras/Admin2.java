package com.example.calculadorhoras;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Admin2 extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RegistroAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mNombreTextView;
    private ArrayList<Registro> mListaRegistros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin2);

        // Obtener el nombre del usuario seleccionado en la actividad anterior
        Intent intent = getIntent();
        String nombreUsuario = intent.getStringExtra("nombre");

        // Configurar el RecyclerView
        mRecyclerView = findViewById(R.id.rvRegistros);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Obtener la lista de registros del usuario seleccionado desde Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Registros").orderByChild("nombre").equalTo(nombreUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListaRegistros = new ArrayList<>();
                for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                    Registro registro = registroSnapshot.getValue(Registro.class);
                    mListaRegistros.add(registro);
                }

                // Configurar el adaptador y asignarlo al RecyclerView
                mAdapter = new RegistroAdapter(mListaRegistros);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Admin2Activity", "Error al obtener la lista de registros del usuario " + nombreUsuario);
            }
        });

        //Mostrar el nombre del usuario en el TextView correspondiente
        mNombreTextView = findViewById(R.id.tvNombreUsuario);
        mNombreTextView.setText(nombreUsuario);
    }
}