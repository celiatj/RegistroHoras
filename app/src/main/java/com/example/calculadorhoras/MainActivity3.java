package com.example.calculadorhoras;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity {
    public TextView total;
    public TextView entrada;
    public TextView salida;
    private Button registroEntrada, registroSalida;
    private Button botonSalir;
    String horasTotal;
    String dia;
    String timeE, timeS;
    int horaE, horaS, minE, minS, anyoE, anyoS, mesE, mesS, diaE, diaS;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private Spinner spInci;
    private String[] inci;
    private ArrayAdapter<String> adaptadorInci;
    final static String CHANNEL_ID = "NOTIFICACIONES";
    boolean contadorE;
    String localizacion;
    double longitude,latitude;
    Map<String, String> ubicacion = new HashMap<>();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    // Manejar la respuesta de los permisos
    public static class MiFragmento extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_main3, container, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso ha sido concedido, puedes iniciar la obtención de la ubicación aquí
                obtenerUbicacion();
            } else {
                // El permiso ha sido denegado, puedes mostrar un mensaje o tomar alguna otra acción
            }
        }
    }

    // Método para obtener la ubicación
    private void obtenerUbicacion() {
        // Aquí puedes utilizar las API de ubicación de Android para obtener la ubicación del dispositivo
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            // Aquí puedes utilizar las coordenadas de latitud y longitud obtenidas
                            // por ejemplo, guardarlas en la base de datos o mostrarlas en la interfaz de usuario
                            ubicacion.put("latitude", String.valueOf(latitude));
                            ubicacion.put("longitude", String.valueOf(longitude));

                            Toast.makeText(getApplicationContext(),
                                    longitude + " " + latitude,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // No se pudo obtener la ubicación actual
                            Toast.makeText(getApplicationContext(),
                                    "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al obtener la ubicación
                        Toast.makeText(getApplicationContext(),
                                "Error al obtener la ubicación: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        getSupportActionBar().setTitle(R.string.app_name);

        // Agregar un fragmento a tu actividad
        Fragment fragment = new MiFragmento();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();


        total = findViewById(R.id.textViewTotal2);

        // Crear canal de notificaciones
        createNotificationChannel();
        // Configuración de idioma
        SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String codigoIdioma = preferenciasCompartidas.getString("codigo_idioma", "es");
         timeE = preferenciasCompartidas.getString("entrada", "");
        contadorE=preferenciasCompartidas.getBoolean("contadorE", true);
        setAppLocale(codigoIdioma);
        getSupportActionBar().setTitle(R.string.app_name);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Verificar si se ha concedido el permiso de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // El permiso no se ha concedido, se solicita al usuario
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            // El permiso ya se ha concedido, puedes iniciar la obtención de la ubicación aquí
            obtenerUbicacion();
        }



        // Guardar debe estar desactivado hasta que se pulse Calcular

        botonSalir = findViewById(R.id.btnSalir2);
        total = findViewById(R.id.textViewTotal2);
        registroEntrada = findViewById(R.id.btnRegistroEntrada2);
        entrada = findViewById(R.id.textViewE2);
        salida = findViewById(R.id.textViewS2);
        registroSalida = findViewById(R.id.btnRegistroSalida2);
        registroSalida.setEnabled(false);

        // Rellenamos el Spinner
        spInci = (Spinner) findViewById(R.id.spnIncidencia);
        Resources res = getResources();
        inci = res.getStringArray(R.array.array_inci);
        adaptadorInci = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, inci);
        spInci.setAdapter(adaptadorInci);

        spInci.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
                SharedPreferences.Editor editorPreferencias = preferencias.edit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        // Recuperar el valor de contadorE de las preferencias compartidas
        contadorE = preferenciasCompartidas.getBoolean("contadorE", true);

       // Actualiza la visibilidad y el texto de los botones y la entrada según el valor de contadorE
        if (contadorE) {
            registroEntrada.setEnabled(true);
            registroSalida.setEnabled(false);
            salida.setText(timeS);

        } else {
            registroEntrada.setEnabled(false);
            registroSalida.setEnabled(true);
            entrada.setText(timeE);

        }

        registroEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                diaE = c.get(Calendar.DATE);
                anyoE = c.get(Calendar.YEAR);
                mesE = c.get(Calendar.MONTH) + 1;
                //  String fecha = Integer.toString(c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DATE));
                horaE = c.get(Calendar.HOUR_OF_DAY);
                minE = c.get(Calendar.MINUTE);

                timeE = String.format("%02d:%02d", horaE, minE);
                entrada.setText(timeE);
                SharedPreferences.Editor editorPreferencias = preferenciasCompartidas.edit();
                editorPreferencias.putString("entrada", timeE);
                editorPreferencias.putInt("horaE", horaE);
                editorPreferencias.putInt("minE", minE);
                ;
                //   FirebaseUser currentUser = mAuth.getCurrentUser();
                // Inicializar aplicación de Firebase
                FirebaseApp.initializeApp(getApplicationContext());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                db = FirebaseDatabase.getInstance();
                // SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
                String corr = preferenciasCompartidas.getString("email", "");
                //String ubi= preferenciasCompartidas.getString("latitude", "")+" "+preferenciasCompartidas.getString("longitude", "");
                Map<String, String> datos = new HashMap<>();
                int posicionInci = spInci.getSelectedItemPosition();
                // datos.put("ubicacion", String.valueOf(ubicacion));
                datos.put("tipo", "entrada");
                datos.put("incidencia", inci[posicionInci]);

                db.getReference("usuarios").child(corr).child("Registros").child(String.format("%04d%02d%02d%02d%02d", anyoE, mesE, diaE, horaE, minE)).setValue(datos);
                // db.getReference("usuarios").child(corr).child("Registros").child("ubicacion").setValue(ubicacion);
                db.getReference("usuarios").child(corr).child("Registros").child(String.format("%04d%02d%02d%02d%02d", anyoE, mesE, diaE, horaE, minE)).child("ubicacion").setValue(ubicacion);

                editorPreferencias.putBoolean("contadorE", false);

                editorPreferencias.commit();
                registroSalida.setEnabled(true);
                registroEntrada.setEnabled(false);
            }

        });


        registroSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contadorE = true;
                SharedPreferences.Editor editorPreferencias = preferenciasCompartidas.edit();
                Calendar c = Calendar.getInstance();
                dia = Integer.toString(c.get(Calendar.DATE));
                String fecha = Integer.toString(c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DATE));
                diaS = c.get(Calendar.DATE);
                anyoS = c.get(Calendar.YEAR);
                mesS = c.get(Calendar.MONTH)+1;
                horaS = c.get(Calendar.HOUR_OF_DAY);
                minS = c.get(Calendar.MINUTE);
                horaE = preferenciasCompartidas.getInt("horaE", 0);
                minE = preferenciasCompartidas.getInt("minE", 0);
                timeS = String.format("%02d:%02d", horaS, minS);
                salida.setText(timeS);
                // Inicializar aplicación de Firebase
                FirebaseApp.initializeApp(getApplicationContext());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                db = FirebaseDatabase.getInstance();
                //SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
                String corr = preferenciasCompartidas.getString("email", "");
                //String ubi= preferenciasCompartidas.getString("latitude", "")+" "+preferenciasCompartidas.getString("longitude", "");
                Map<String, String> datos = new HashMap<>();
                int posicionInci = spInci.getSelectedItemPosition();

                datos.put("tipo", "salida");
                datos.put("incidencia", inci[posicionInci]);
                db.getReference("usuarios").child(corr).child("Registros").child(String.format("%04d%02d%02d%02d%02d", anyoS, mesS, diaS, horaS, minS)).setValue(datos);
                db.getReference("usuarios").child(corr).child("Registros").child(String.format("%04d%02d%02d%02d%02d", anyoS, mesS, diaS, horaS, minS)).child("ubicacion").setValue(ubicacion);
                // calculamos las horas trabajadas:

                int tE = (horaE * 60) + minE;
                int tS = (horaS * 60) + minS;
                int tot;

                if (tS < tE) {
                    tot = (24 * 60 - tE) + tS;
                } else {
                    tot = tS - tE;
                }

                int horas = tot / 60;
                int minutos = tot % 60;


                // Indicamos al usuario con el textView de total el resultado
                horasTotal = "Día: " + dia + "; Horas: " + horas + ",Minutos: " + minutos + ";";
                total.setText(horasTotal);
                editorPreferencias.putString("salida", timeS);

                // Guardar el estado del contador antes de cambiar los estados de los botones

                editorPreferencias.putBoolean("contadorE", true);
                editorPreferencias.commit();
                registroEntrada.setEnabled(true);
                registroSalida.setEnabled(false);
                editorPreferencias.putString("entrada", "");
               // entrada.setText("");
               // editorPreferencias.putString("entrada", "");
            }

        });


        // Salir de la app
        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear el objeto constructor de alerta AlertDialog.Builder
                AlertDialog.Builder constructorAlerta = new AlertDialog.Builder(com.example.calculadorhoras.MainActivity3.this);

                // Establecer título y mensaje
                constructorAlerta.
                        setTitle(getResources().getText(R.string.txt_salir_app_titulo)).
                        setMessage(getResources().getText(R.string.txt_salir_app_mensaje));

                // Hacer que no se pueda dejar el diálogo sin pulsar una opción
                constructorAlerta.setCancelable(false);

                // Establecer botón de respuesta positiva
                constructorAlerta.setPositiveButton(getResources().getText(R.string.txt_si), (DialogInterface.OnClickListener) (dialog, which) -> {
                    finishAffinity();

                });

                // Establecer botón de respuesta negativa
                constructorAlerta.setNegativeButton(getResources().getText(R.string.txt_no), (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });

                // Crear y mostrar diálogo de alerta
                AlertDialog dialogoAlertaSalir = constructorAlerta.create();
                dialogoAlertaSalir.show();

            }


        });
    }

    //Guardamos los valores cuando la aplicacion este en pause (se gire la pantalla por ejemplo)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        entrada.setText(savedInstanceState.getString("entrada"));
        salida.setText(savedInstanceState.getString("salida"));
        total.setText(savedInstanceState.getString("total"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("total", total.getText().toString());
        outState.putString("entrada", entrada.getText().toString());
        outState.putString("salida", salida.getText().toString());
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Control de opciones de la action bar
        int id = item.getItemId();
        if (id == R.id.configuracion) {
            Toast.makeText(getApplicationContext(), "Configuración pulsado", Toast.LENGTH_LONG).show();
            Intent intencion = new Intent(com.example.calculadorhoras.MainActivity3.this, MainActivityConf.class);
            startActivity(intencion);

            return true;
        } else if (id == R.id.registro) {
            Intent objetoMensajero2 = new Intent(getApplicationContext(), RegistroUsuarios.class);
            startActivity(objetoMensajero2);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_en_activity, menu);
        return true;
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

    // Crear canal de notificaciones
    private void createNotificationChannel() {
        // Crear el canal de notificaciones solo a partir de la API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = getString(R.string.nombre_canal);
            String descripcion = getString(R.string.descripcion_canal);
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(CHANNEL_ID, nombre, importancia);
            canal.setDescription(descripcion);

            // Registrar el canal de notificaciones
            NotificationManager gestorNotificaciones = getSystemService(NotificationManager.class);
            gestorNotificaciones.createNotificationChannel(canal);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}


