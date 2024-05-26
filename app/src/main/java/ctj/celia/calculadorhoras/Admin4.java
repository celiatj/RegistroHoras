package ctj.celia.calculadorhoras;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Admin4 extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private RecyclerView mRecyclerView;
    private AdaptadorRegistro mAdapter;
    private List<String> mHorasTrabajadasList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin4);
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
        mRecyclerView = findViewById(R.id.rvRegistrosMesuales);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mHorasTrabajadasList = new ArrayList<>();
        mAdapter = new AdaptadorRegistro(mHorasTrabajadasList);
        mRecyclerView.setAdapter(mAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usuariosRef = database.getReference("usuarios");
        Query query = usuariosRef.orderByChild("correo").equalTo(correo);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Mapa para almacenar las horas trabajadas por mes
                Map<String, Map<Integer, Integer>> horasPorAñoYMesMap = new HashMap<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot registrosSnapshot = userSnapshot.child("Registros");
                    for (DataSnapshot registroSnapshot : registrosSnapshot.getChildren()) {
                        String horasTrabajadas = registroSnapshot.child("HorasTrabajadas").getValue(String.class);
                        if (horasTrabajadas != null) {
                            String[] fechaHora = horasTrabajadas.split(",")[0].split(" "); // Dividir la fecha y la hora
                            String[] fecha = fechaHora[0].split("/"); // Dividir la fecha en día, mes y año
                            int año = Integer.parseInt(fecha[0]);
                            int mes = Integer.parseInt(fecha[1]);
                            String[] horasMinutos = horasTrabajadas.split(";"); // Divide las horas y los minutos
                            int horas = Integer.parseInt(horasMinutos[0].split(": ")[1]);
                            int minutos = Integer.parseInt(horasMinutos[1].split(": ")[1]);

                            // Obtener el mapa de horas por mes para el año correspondiente
                            Map<Integer, Integer> horasPorMesMap = horasPorAñoYMesMap.getOrDefault(Integer.toString(año), new HashMap<>());
                            // Sumar horas y minutos al mes correspondiente
                            int totalMinutos = horasPorMesMap.getOrDefault(mes, 0) + horas * 60 + minutos;
                            horasPorMesMap.put(mes, totalMinutos);
                            // Actualizar el mapa de horas por mes para el año correspondiente
                            horasPorAñoYMesMap.put(Integer.toString(año), horasPorMesMap);
                        }
                    }
                }

                // Construir el string de horas por mes y año
                StringBuilder horasPorMesYAño = new StringBuilder();
                horasPorMesYAño.append("\n");
                for (Map.Entry<String, Map<Integer, Integer>> añoEntry : horasPorAñoYMesMap.entrySet()) {
                    String año = añoEntry.getKey();
                    Map<Integer, Integer> horasPorMesMap = añoEntry.getValue();
                    for (Map.Entry<Integer, Integer> mesEntry : horasPorMesMap.entrySet()) {
                        int mes = mesEntry.getKey();
                        int totalHoras = mesEntry.getValue() / 60; // Convertir minutos a horas
                        int totalMinutos = mesEntry.getValue() % 60;

                        horasPorMesYAño.append("Año: ").append(año).append(", Mes: ").append(mes).append(", Horas trabajadas: ").append(totalHoras).append(", minutos: ").append(totalMinutos).append("\n");
                    }
                }

                // Agregar el resultado al adaptador
                mHorasTrabajadasList.add(horasPorMesYAño.toString());
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
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