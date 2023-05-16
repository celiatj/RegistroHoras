package com.example.calculadorhoras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class RegistroUsuarios extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private TextView tvnombre;
    private AdapterUsu mAdapterU;
    private ArrayList<RegistroUsu> mRegistrosU;
    private FirebaseDatabase db;
    private DatabaseReference usuarioRef;
    String idRegistro, tipoRegistro, incidenciaRegistro;

    public Date getFecha() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            return dateFormat.parse(this.idRegistro.substring(0, 12));
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.registro);
        setContentView(R.layout.activity_registro_usuarios);
        Button btnUBuscar = findViewById(R.id.btnUbuscar);
        EditText etUFechaInicio = findViewById(R.id.etUFechaInicio);
        EditText etUFechaFin = findViewById(R.id.etUFechaFin);

        // Obtener la referencia al RecyclerView
        mRecyclerView = findViewById(R.id.rvusu);

        // Inicializar la lista de registros
        mRegistrosU = new ArrayList<>();

        // Obtener el correo electrónico del usuario desde los extras del intent
        SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        db = FirebaseDatabase.getInstance();
        String corr = preferenciasCompartidas.getString("email", "");

        tvnombre = findViewById(R.id.tvnombreUsuario);
        tvnombre.setText(corr);

        // Configurar el RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapterU = new AdapterUsu();
        mRecyclerView.setAdapter(mAdapterU);

        // Obtener referencia a la base de datos
        DatabaseReference ref = db.getReference("usuarios").child(corr).child("Registros");

        // Leer los datos de Firebase
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRegistrosU.clear();
                for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                    // Aquí se obtiene el ID del registro utilizando getKey() en el nivel "registroSnapshot"
                    idRegistro = registroSnapshot.getKey();
                    tipoRegistro = registroSnapshot.child("tipo").getValue(String.class);
                    incidenciaRegistro = registroSnapshot.child("incidencia").getValue(String.class);

                    // Crea un objeto RegistroUsu y agrégalo a la lista
                    RegistroUsu registro = new RegistroUsu(idRegistro, tipoRegistro, incidenciaRegistro);
                    mRegistrosU.add(registro);
                }

                // Notificar al adaptador de que los datos han cambiado
                mAdapterU.setRegistros(mRegistrosU);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
                Toast.makeText(getApplicationContext(), R.string.falloEnLaBase, Toast.LENGTH_LONG).show();
            }
        });


        btnUBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaInicio = etUFechaInicio.getText().toString().trim().replace("/", "");
                String fechaFin = etUFechaFin.getText().toString().trim().replace("/", "");

                if (fechaInicio.isEmpty() && fechaFin.isEmpty()) {
                    // Si ambos campos de fecha están vacíos, actualizar el adaptador con todos los registros
                    mAdapterU.setRegistros(mRegistrosU);
                    return;
                }

                // Obtener la referencia a la base de datos
                DatabaseReference registrosRef = db.getReference("usuarios").child(corr).child("Registros");

                // Convertir las fechas en formato numérico para compararlas como enteros
                int fechaInicioNum = Integer.parseInt(fechaInicio);
                int fechaFinNum = Integer.parseInt(fechaFin);

                registrosRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mRegistrosU.clear();
                        for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                            // Obtén el ID del registro
                            String idRegistro = registroSnapshot.getKey();

                            // Extraer la parte de fecha del ID (primeros 8 dígitos)
                            String fechaRegistro = idRegistro.substring(0, 8);
                            int fechaRegistroNum = Integer.parseInt(fechaRegistro);

                            // Verificar si la fecha del registro está dentro del rango especificado
                            if (fechaRegistroNum >= fechaInicioNum && fechaRegistroNum <= fechaFinNum) {
                                // Obtén los datos del registro
                                String tipoRegistro = registroSnapshot.child("tipo").getValue(String.class);
                                String incidenciaRegistro = registroSnapshot.child("incidencia").getValue(String.class);

                                // Crea un objeto Registro y agrégalo a la lista
                                RegistroUsu registro = new RegistroUsu(idRegistro, tipoRegistro, incidenciaRegistro);
                                mRegistrosU.add(registro);
                            }
                        }

                        // Actualiza el adaptador del RecyclerView con la lista de registros
                        mAdapterU.setRegistros(mRegistrosU);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Manejar errores de lectura de la base de datos
                    }
                });
            }
        });
    }}