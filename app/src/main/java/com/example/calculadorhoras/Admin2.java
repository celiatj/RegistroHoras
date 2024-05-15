package com.example.calculadorhoras;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Admin2 extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
        private RecyclerView mRecyclerView;
        private TextView tvnombre;
    private AdapterUsu mAdapterU;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private NavigationView navigationView;
    String idRegistro;
    private String fechaInicio = "";
    private String fechaFin = "";
    private double ubicacionLatitude;
    private double ubicacionLongitude;
    String longitude,latitude;
    private FirebaseDatabase db;
    private ArrayList<RegistroUsu> mRegistrosU;
    private EditText etFechaInicio;
    private EditText etFechaFin;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin2);
            // Inicializar aplicación de Firebase
            FirebaseApp.initializeApp(getApplicationContext());
            db = FirebaseDatabase.getInstance();
            // Obtener el correo electrónico del usuario desde los extras del intent
            String nombre = getIntent().getStringExtra("nombre");
            String correo = getIntent().getStringExtra("correo");

            drawerLayout = findViewById(R.id.drawer_layout);
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
            drawerLayout.addDrawerListener(drawerToggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);

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

             etFechaInicio = findViewById(R.id.etFechaInicio);
            etFechaFin = findViewById(R.id.etFechaFin);

            Button btnBuscar = findViewById(R.id.btnBuscar);
            Button atras = findViewById(R.id.btnBack);
            // Obtener la referencia al RecyclerView
            mRecyclerView = findViewById(R.id.rvRegistros);

            tvnombre = findViewById(R.id.tvNombreUsuario);
            tvnombre.setText(correo);

            etFechaInicio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        showDatePickerDialog(true);
                    }
                }
            });


            etFechaFin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        showDatePickerDialog(false);
                    }
                }
            });

            // Obtener la referencia al RecyclerView
            mRecyclerView = findViewById(R.id.rvRegistros);

            // Inicializar la lista de registros
            mRegistrosU = new ArrayList<>();

            // Obtener el correo electrónico del usuario desde los extras del intent

            String corr = preferenciasCompartidas.getString("email", "");// del admin

            // Obtener la referencia al nodo de la ubicación de la oficina
            DatabaseReference ubicacionRef = db.getReference("usuarios").child(correo.replace(".", "")).child("ubicacionOficina");

            // Obtener los valores de latitud y longitud
            ubicacionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Obtener el valor completo de ubicacionOficina
                        String ubiOficial = dataSnapshot.getValue(String.class);

                        // Dividir la cadena en latitud y longitud
                        String[] partes = ubiOficial.split(":");
                        if (partes.length == 2) {
                            ubicacionLongitude = Double.parseDouble(partes[1]);
                            ubicacionLatitude = Double.parseDouble(partes[0]);

                        } else {
                            // Manejar la situación en la que la ubicación no tiene el formato esperado
                            // Podrías lanzar una excepción, imprimir un mensaje de error, o tomar otra acción adecuada
                        }
                    } else {
                        // Manejar la situación en la que no existe el nodo de ubicacionOficina para el usuario
                        // Podrías lanzar una excepción, imprimir un mensaje de error, o tomar otra acción adecuada
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // Obtener referencia a la base de datos
            DatabaseReference ref = db.getReference("usuarios").child(correo.replace(".", "")).child("Registros");

            // Leer los datos de Firebase y establecer un listener para obtener actualizaciones en tiempo real
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mRegistrosU.clear();
                    for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                        // Obtener los datos de cada registro
                        String idRegistro = registroSnapshot.getKey();
                        String tipoRegistro = registroSnapshot.child("tipo").getValue(String.class);
                        String incidenciaRegistro = registroSnapshot.child("incidencia").getValue(String.class);

                        latitude = registroSnapshot.child("ubicacion").child("latitude").getValue(String.class);
                        longitude = registroSnapshot.child("ubicacion").child("longitude").getValue(String.class);

                        if (latitude != null && longitude != null) {
                            RegistroUsu registro = new RegistroUsu(idRegistro, tipoRegistro, incidenciaRegistro, latitude, longitude);
                            mRegistrosU.add(registro);
                        } else {
                            RegistroUsu registro = new RegistroUsu(idRegistro, tipoRegistro, incidenciaRegistro, "00", "00");
                            mRegistrosU.add(registro);
                        }
                    }
                    // Configurar el RecyclerView
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mAdapterU = new AdapterUsu(ubicacionLatitude, ubicacionLongitude);
                    mRecyclerView.setAdapter(mAdapterU);

                    // Notificar al adaptador de los cambios en los datos
                    mAdapterU.setRegistros(mRegistrosU);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar el error de lectura de la base de datos
                }
            });

            btnBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fechaInicio = etFechaInicio.getText().toString().trim().replace("/", "");
                    String fechaFin = etFechaFin.getText().toString().trim().replace("/", "");

                    if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
                        mAdapterU.setRegistros(mRegistrosU);
                        return;
                    }

                    DatabaseReference registrosRef = db.getReference("usuarios").child(correo.replace(".", "")).child("Registros");

                    // Convertir las fechas en formato numérico para compararlas como enteros
                    int fechaInicioNum = Integer.parseInt(fechaInicio);
                    int fechaFinNum = Integer.parseInt(fechaFin);

                    registrosRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mRegistrosU.clear();
                            for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                                String idRegistro = registroSnapshot.getKey();
                                String fechaRegistro = idRegistro.substring(0, 8);
                                int fechaRegistroNum = Integer.parseInt(fechaRegistro);

                                if (fechaRegistroNum >= fechaInicioNum && fechaRegistroNum <= fechaFinNum) {
                                    String tipoRegistro = registroSnapshot.child("tipo").getValue(String.class);
                                    String incidenciaRegistro = registroSnapshot.child("incidencia").getValue(String.class);

                                    String latitude = registroSnapshot.child("ubicacion").child("latitude").getValue(String.class);
                                    String longitude = registroSnapshot.child("ubicacion").child("longitude").getValue(String.class);

                                    RegistroUsu registro = new RegistroUsu(idRegistro, tipoRegistro, incidenciaRegistro, latitude, longitude);
                                    mRegistrosU.add(registro);
                                }
                            }

                            mAdapterU.setRegistros(mRegistrosU);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Manejar errores de lectura de la base de datos
                        }
                    });
                }
            });

            atras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String empresa = getIntent().getStringExtra("empresa");
                    Intent intentBack = new Intent(getApplicationContext(), Administrador.class);
                    intentBack.putExtra("empresa", empresa);
                    startActivity(intentBack);
                    finish();
                }});

        }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);

        if (!isStartDate && !fechaInicio.isEmpty()) {
            String[] parts = fechaInicio.split("/");
            int startYear = Integer.parseInt(parts[2]);
            int startMonth = Integer.parseInt(parts[1]) - 1;
            int startDay = Integer.parseInt(parts[0]);
            datePickerDialog.getDatePicker().setMinDate(new GregorianCalendar(startYear, startMonth, startDay).getTimeInMillis());
        }

        if (isStartDate) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        datePickerDialog.show();
    }


    @Override
    public void onDateSet(android.widget.DatePicker view, int anyo, int mes, int dia) {
        String fechaSeleccionada = String.format("%02d/%02d/%02d", anyo, (mes + 1), dia); // Formato año/mes/día

        // Obtener la referencia al EditText que tiene el foco
        EditText editTextConFoco = etFechaInicio.hasFocus() ? etFechaInicio : etFechaFin;

        // Establecer la fecha seleccionada en el EditText correspondiente
        editTextConFoco.setText(fechaSeleccionada);

        // Actualizar la variable de fecha correspondiente con el valor seleccionado
        if (editTextConFoco == etFechaInicio) {
            fechaInicio = fechaSeleccionada;
        } else if (editTextConFoco == etFechaFin) {
            fechaFin = fechaSeleccionada;
        }
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

    private void setAppLocale(String localeCode) {
        Locale myLocale = new Locale(localeCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        Locale.setDefault(myLocale);
        conf.setLayoutDirection(myLocale);
        res.updateConfiguration(conf, dm);
    }
}






           /*
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
*/