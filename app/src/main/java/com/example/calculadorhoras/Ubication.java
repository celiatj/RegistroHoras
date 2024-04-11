package com.example.calculadorhoras;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Ubication extends AppCompatActivity {
    private Button ubicacion;
    private DatabaseReference usuarioRef; // Referencia a los datos del usuario en Firebase
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    SharedPreferences preferencias;
    double longitude,latitude;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubication);
        getSupportActionBar().setTitle(R.string.configuracion);

        ubicacion = findViewById(R.id.btnUbi);
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String corr = preferencias.getString("email", "");
        // Obtener la referencia  a los datos del usuario en Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuarioRef = database.getReference("usuarios").child(corr);

        // mapView = findViewById(R.id.mapView);
        // mapView.onCreate(savedInstanceState);

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




}

