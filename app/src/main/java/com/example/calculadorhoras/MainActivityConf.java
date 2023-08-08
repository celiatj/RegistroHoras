package com.example.calculadorhoras;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivityConf extends AppCompatActivity {

    private EditText nombre;
    private EditText ap1;
    private Switch swNotificaciones;
    private Button guardar, ubicacion;
    private Spinner spIdiomas;
    private String[] arrayIdiomas;
    private ArrayAdapter<String> adaptadorIdiomas;
    private DatabaseReference usuarioRef; // Referencia a los datos del usuario en Firebase
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    SharedPreferences preferencias;
    double longitude, latitude;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración de idioma
        preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String codigoIdioma = preferencias.getString("codigo_idioma", "es");
        setAppLocale(codigoIdioma);

        setContentView(R.layout.activity_main_conf); // Asignar el layout correspondiente
        getSupportActionBar().setTitle(R.string.configuracion);
        //SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String corr = preferencias.getString("email", "");
        // Obtener la referencia a los datos del usuario en Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuarioRef = database.getReference("usuarios").child(corr);

        // Rellenamos el Spinner
        spIdiomas = (Spinner) findViewById(R.id.idiomas);
        Resources res = getResources();
        arrayIdiomas = res.getStringArray(R.array.array_paises);

        adaptadorIdiomas = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayIdiomas);
        spIdiomas.setAdapter(adaptadorIdiomas);

        int idioma = preferencias.getInt("idioma", 0);
        spIdiomas.setSelection(idioma);

        // Obtener referencias a las vistas
        ap1 = findViewById(R.id.etAp1);
        nombre = findViewById(R.id.etnombre);
        guardar = findViewById(R.id.guardar);
        ubicacion = findViewById(R.id.btnUbi);

        // mapView = findViewById(R.id.mapView);
        // mapView.onCreate(savedInstanceState);

        // Recuperar datos del usuario y mostrarlos en los EditTexts correspondientes
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("nombre").getValue(String.class);
                String subname = dataSnapshot.child("apellidos").getValue(String.class);
                nombre.setText(name);
                ap1.setText(subname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
            }

        });

        // Configurar el botón "guardar"
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener los valores de los EditTexts
                String nom = nombre.getText().toString();
                String ap = ap1.getText().toString();

                // Guardar los valores en Firebase
                usuarioRef.child("nombre").setValue(nom);
                usuarioRef.child("apellidos").setValue(ap);

                // Mostrar un mensaje de confirmación
                Toast.makeText(MainActivityConf.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        spIdiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                int idiomaAnterior = preferencias.getInt("idioma", 0);
                int idiomaActual = spIdiomas.getSelectedItemPosition();

                if (idiomaAnterior != idiomaActual) {
                    AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(MainActivityConf.this);
                    constructorDialogo.setMessage(getText(R.string.aviso_cambio_idioma_mensaje));
                    constructorDialogo.setPositiveButton(getText(R.string.aviso_cambio_idioma_aceptar), (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog dialogoAvisoCambioIdioma = constructorDialogo.create();
                    dialogoAvisoCambioIdioma.show();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Recuperar los valores de los EditTexts desde SharedPreferences
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String nombreGuardado = preferencias.getString("nombre", "");
        String apellidoGuardado = preferencias.getString("apellidos", "");

        // Mostrar los valores recuperados en los EditTexts correspondientes
//        nombre.setText(nombreGuardado);
//        ap1.setText(apellidoGuardado);

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




/*
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onResume();
        // Guardar los valores de los EditTexts en SharedPreferences
        SharedPreferences.Editor editorPreferencias = preferencias.edit();
        editorPreferencias.putString("nombre", nombre.getText().toString());
        editorPreferencias.putString("apellidos", ap1.getText().toString());

        // Idioma
        int idioma = (int) spIdiomas.getSelectedItemId();
        editorPreferencias.putInt("idioma", idioma);
        // Código de idioma
        switch (idioma) {
            case 0:
                editorPreferencias.putString("codigo_idioma", "es");
                break;
            case 1:
                editorPreferencias.putString("codigo_idioma", "en");
                break;
            case 2:
                editorPreferencias.putString("codigo_idioma", "fr");
                break;
            default:
                editorPreferencias.putString("codigo_idioma", "es");
                break;
        }
// Notificaciones
//                boolean notificaciones = swNotificaciones.isChecked();
        //           editorPreferencias.putBoolean("notificaciones", notificaciones);

        // Una vez hechos los cambios, debemos confirmarlos o consolidarlos
        editorPreferencias.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Obtener la ubicación actual
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Agregar un marcador en la ubicación actual
                LatLng currentLocation = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Mi ubicación"));

                // Mover la cámara a la ubicación actual
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        }
    }
*/


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