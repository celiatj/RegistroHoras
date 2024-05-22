package ctj.celia.calculadorhoras;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class Ubication extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    private Button ubicacion;
    private DatabaseReference usuarioRef; // Referencia a los datos del usuario en Firebase
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    SharedPreferences preferencias;
    double longitude, latitude;
    private FusedLocationProviderClient fusedLocationClient;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    CameraPosition mCameraPosition;

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubication);
        getSupportActionBar().setTitle(R.string.configuracion);
        SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String codigoIdioma = preferenciasCompartidas.getString("codigo_idioma", "");
        setAppLocale(codigoIdioma);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        navigationView = findViewById(R.id.navigation_view);

        ubicacion = findViewById(R.id.btnUbi);
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String corr = preferencias.getString("email", "");
        String correo = getIntent().getStringExtra("correo");
        // Obtener la referencia  a los datos del usuario en Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        usuarioRef = database.getReference("usuarios").child(correo.replace(".", "")).child("ubicacionOficina");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        if (savedInstanceState != null) {
            mLastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editorPreferencias = preferencias.edit();

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Los permisos de ubicación no están concedidos, puedes mostrar un mensaje o solicitar los permisos nuevamente.
                    return;
                }

                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // La ubicación está disponible
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                           usuarioRef.setValue(latitude+":"+longitude);
                            // Guardar las coordenadas en las preferencias
                            editorPreferencias.putFloat("uLatitud", (float) latitude);
                            editorPreferencias.putFloat("uLongitude", (float) longitude);
                            editorPreferencias.commit(); // Guarda los cambios en SharedPreferences

                            Toast.makeText(getApplicationContext(), latitude + "-" + longitude, Toast.LENGTH_SHORT).show();

                        } else {
                            // Solicitar actualizaciones de la ubicación
                            LocationRequest locationRequest = LocationRequest.create();
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationRequest.setInterval(10000); // Actualiza cada 10 segundos

                            fusedLocationClient.requestLocationUpdates(locationRequest,
                                    locationCallback,
                                    Looper.getMainLooper());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        latitude = 0;
                        longitude = 0;

                        Toast.makeText(getApplicationContext(), "Error obteniendo la ubicación", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });

        navigationView = findViewById(R.id.navigation_view);
        String empresa = getIntent().getStringExtra("empresa");
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
    }


    // Aquí necesitarías definir tu LocationCallback
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    // Guarda las coordenadas obtenidas en las preferencias
                    SharedPreferences.Editor editorPreferencias = preferencias.edit();
                    editorPreferencias.putFloat("uLatitud", (float) location.getLatitude());
                    editorPreferencias.putFloat("uLongitude", (float) location.getLongitude());
                    editorPreferencias.commit();

                    Toast.makeText(getApplicationContext(), location.getLatitude() + "-" + location.getLongitude(), Toast.LENGTH_SHORT).show();

                    // Detiene la actualización de la ubicación después de obtener una ubicación válida
                    fusedLocationClient.removeLocationUpdates(this);
                }
            }
        }
    };

    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        // Poner tipo de mapa: normal, terreno, satélite, híbrido...
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Creación de solicitud de ubicación
        mLocationRequest = new LocationRequest();
        // Solicitud de ubicación cada X milisegundos
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(60000);
        // Distintas opciones de precisión de la ubicación y consumo de batería
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            // Si el usuario nos ha concedido permiso de ubicación*/
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Activamos solicitudes de ubicación
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnPoiClickListener(this);
        } else {
            //De lo contrario solicitamos permiso de ubicación
            checkLocationPermission();
        }

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                // La última ubicación en la lista es la más actual
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Ubicación: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                // Quitamos el marcador de posición anterior
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                // Ponemos marcador de posición actual
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Mi posición actual");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                // Mover cámara del mapa a posición especificada con nivel de zoom Z: 2.0 <= Z <= 21.0
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Comprobar si hay que mostrar una explicación al usuario
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Mostrar explicación al usuario
                new AlertDialog.Builder(this)
                        .setTitle("Se necesita permiso de ubicación")
                        .setMessage("Esta app necesita el permiso de ubicación, por lo que deberás aceptar para utilizar esta funcionalidad.")
                        .setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Solicitar permiso al usuario una vez mostrada la explicación
                                ActivityCompat.requestPermissions(Ubication.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No se necesita explicación, podemos solicitar el permiso
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mGoogleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mGoogleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getApplicationContext(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Prevención de consumo inútil de batería
        //Quitar actualizaciones de ubicación si la actividad no está activa
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
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

