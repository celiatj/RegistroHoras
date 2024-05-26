package ctj.celia.calculadorhoras;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Admin3 extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private RecyclerView mRecyclerView;
    private AdaptadorRegistro mAdapter;
    private List<String> mHorasTrabajadasList;
    private EditText etFechaInicio;
    private EditText etFechaFin;
    private String fechaInicio = "";
    private String fechaFin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin3);
        String correo = getIntent().getStringExtra("correo");

        SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String codigoIdioma = preferenciasCompartidas.getString("codigo_idioma", "");
        setAppLocale(codigoIdioma);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        navigationView = findViewById(R.id.navigation_view);
        String empresa = getIntent().getStringExtra("empresa");
        // Cambiar el color del texto de los elementos del menú
        int color = getResources().getColor(R.color.ic_launcher_background); // Define el color en colors.xml
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            MenuItem menuItem = navigationView.getMenu().getItem(i);
            SpannableString s = new SpannableString(menuItem.getTitle());
            s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
            menuItem.setTitle(s);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_current_page:
                        Intent intentCurrentPage = new Intent(getApplicationContext(), Admin2.class);
                        intentCurrentPage.putExtra("correo", correo);
                        intentCurrentPage.putExtra("empresa", empresa);
                        startActivity(intentCurrentPage);
                        finish();
                        break;
                    case R.id.nav_daily_reports:
                        Intent intentDailyReports = new Intent(getApplicationContext(), Admin3.class);
                        intentDailyReports.putExtra("correo", correo);
                        intentDailyReports.putExtra("empresa", empresa);
                        startActivity(intentDailyReports);
                        finish();
                        break;
                    case R.id.nav_month_reports:
                        Intent intentMonthReports = new Intent(getApplicationContext(), Admin4.class);
                        intentMonthReports.putExtra("correo", correo);
                        intentMonthReports.putExtra("empresa", empresa);
                        startActivity(intentMonthReports);
                        finish();
                        break;
                    case R.id.nav_changeUbi:
                        Intent intentUbi = new Intent(getApplicationContext(), Ubication.class);
                        intentUbi.putExtra("correo", correo);
                        intentUbi.putExtra("empresa", empresa);
                        startActivity(intentUbi);
                        finish();
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Obtener la referencia a la base de datos
        mRecyclerView = findViewById(R.id.rvInformeDiario);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mHorasTrabajadasList = new ArrayList<>();
        mAdapter = new AdaptadorRegistro(mHorasTrabajadasList);
        mRecyclerView.setAdapter(mAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usuariosRef = database.getReference("usuarios");
        Query query = usuariosRef.orderByChild("correo").equalTo(correo);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot registrosSnapshot = userSnapshot.child("Registros");
                    for (DataSnapshot registroSnapshot : registrosSnapshot.getChildren()) {
                        String horasTrabajadas = registroSnapshot.child("HorasTrabajadas").getValue(String.class);
                        if (horasTrabajadas != null) {
                            mHorasTrabajadasList.add(horasTrabajadas);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);

        Button btnBuscar = findViewById(R.id.btnBuscar);

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
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaInicioStr = etFechaInicio.getText().toString().trim().replace("/", "");
                String fechaFinStr = etFechaFin.getText().toString().trim().replace("/", "");

                if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
                    mRecyclerView.setAdapter(mAdapter);
                    return;
                }

                DatabaseReference usuariosRef = database.getReference("usuarios");
                Query query = usuariosRef.orderByChild("correo").equalTo(correo);
                // Convertir las fechas en formato numérico para compararlas como enteros
                int fechaInicioNum = Integer.parseInt(fechaInicioStr);
                int fechaFinNum = Integer.parseInt(fechaFinStr);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mHorasTrabajadasList.clear(); // Limpiar la lista antes de agregar los resultados filtrados
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot registrosSnapshot = userSnapshot.child("Registros");
                            for (DataSnapshot registroSnapshot : registrosSnapshot.getChildren()) {
                                String idRegistro = registroSnapshot.getKey();
                                if (idRegistro != null && idRegistro.length() >= 8) {
                                    try {
                                        String fechaRegistro = idRegistro.substring(0, 8);
                                        int fechaRegistroNum = Integer.parseInt(fechaRegistro);
                                        if (fechaRegistroNum >= fechaInicioNum && fechaRegistroNum <= fechaFinNum) {
                                            String horasTrabajadas = registroSnapshot.child("HorasTrabajadas").getValue(String.class);
                                            if (horasTrabajadas != null) {
                                                mHorasTrabajadasList.add(horasTrabajadas);
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        // Manejar caso donde la cadena no es un número
                                        Log.e("Admin3", "Error parsing fechaRegistro: " + idRegistro, e);
                                    }
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Manejar errores de lectura de la base de datos
                        Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog( this, this, year, month, day);

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