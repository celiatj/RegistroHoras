package com.example.calculadorhoras;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
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

public class Admin2 extends AppCompatActivity {
        private RecyclerView mRecyclerView;
        private TextView tvnombre;
        private MiAdapter2 mAdapter;
        private ArrayList<Registro> mRegistros;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private NavigationView navigationView;
    String idRegistro;
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
            setContentView(R.layout.activity_admin2);
            // Obtener el correo electrónico del usuario desde los extras del intent
            String nombre = getIntent().getStringExtra("nombre");
            String correo = getIntent().getStringExtra("correo");

            drawerLayout = findViewById(R.id.drawer_layout);
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
            drawerLayout.addDrawerListener(drawerToggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            navigationView = findViewById(R.id.navigation_view);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_current_page:
                            Intent intentCurrentPage = new Intent(getApplicationContext(), Admin2.class);
                            intentCurrentPage.putExtra("correo", correo);
                            startActivity(intentCurrentPage);
                            finish();
                            break;
                        case R.id.nav_daily_reports:
                            Intent intentDailyReports = new Intent(getApplicationContext(), Admin3.class);
                            intentDailyReports.putExtra("correo", correo);
                            startActivity(intentDailyReports);
                            finish();
                            break;
                        case R.id.nav_month_reports:
                            Intent intentMonthReports = new Intent(getApplicationContext(), Admin4.class);
                            intentMonthReports.putExtra("correo", correo);
                            startActivity(intentMonthReports);
                            finish();
                            break;
                        case R.id.nav_changeUbi:
                            Intent intentUbi = new Intent(getApplicationContext(), Ubication.class);
                            intentUbi.putExtra("correo", correo);
                            startActivity(intentUbi);
                            finish();
                            break;
                    }
                    drawerLayout.closeDrawers();
                    return true;
                }
            });

            EditText etFechaInicio = findViewById(R.id.etFechaInicio);
            EditText etFechaFin = findViewById(R.id.etFechaFin);

            Button btnBuscar = findViewById(R.id.btnBuscar);

            // Obtener la referencia al RecyclerView
            mRecyclerView = findViewById(R.id.rvRegistros);

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
                        // DatabaseReference registrosRef = usuariosRef.child(correo.replace(".", "")).child("Registros");
                        DatabaseReference registrosRef = usuariosRef.child(correo.replace(".", "")).child("Registros");
                        registrosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mRegistros = new ArrayList<>();
                                for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                                    // Obtén los datos del registro
                                    //idRegistro = registroSnapshot.getKey();
                                    idRegistro= registroSnapshot.getKey().substring(6, 12); // Aquí obtenemos el ID del registro y eliminamos los 6 primeros digitos por ser año y mes
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
                                Toast.makeText(getApplicationContext(), R.string.falloEnLaBase, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar errores de lectura de la base de datos.
                }

            });
            btnBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fechaInicio = etFechaInicio.getText().toString().trim();
                    String fechaFin = etFechaFin.getText().toString().trim();

                    if (fechaInicio.isEmpty() && fechaFin.isEmpty()) {
                        // Si ambos campos de fecha están vacíos, actualizar el adaptador con todos los registros
                        mAdapter.setRegistros(mRegistros);
                        return;
                    }

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
                                // Usa el correo para obtener los registros del usuario.
                                DatabaseReference registrosRef = usuariosRef.child(correo.replace(".", "")).child("Registros");
                                registrosRef.orderByKey().startAt(fechaInicio).endAt(fechaFin).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        mRegistros = new ArrayList<>();
                                        for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                                            // Obtén los datos del registro
                                            String idRegistro = registroSnapshot.getKey().substring(6, 12);; // Aquí obtenemos el ID del registro
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
            });
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
