package ctj.celia.calculadorhoras;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
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
import java.util.List;
import java.util.Locale;

public class Admin3 extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private RecyclerView mRecyclerView;
    private AdaptadorRegistro mAdapter;
    private List<String> mHorasTrabajadasList;
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