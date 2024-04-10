package com.example.calculadorhoras;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity3 extends Fragment {
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

    double longitude,latitude;
    Map<String, String> ubicacion = new HashMap<>();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    // Manejar la respuesta de los permisos
    private SharedPreferences.Editor editorPreferencias;
    private float ubicacionLatitude;
    private float ubicacionLongitude ;


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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Los permisos de ubicación no están concedidos, puedes mostrar un mensaje o solicitar los permisos nuevamente.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // La ubicación está disponible
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    // Guardar las coordenadas en la base de datos o donde sea necesario
                    ubicacion.put("latitude", String.valueOf(latitude));
                    ubicacion.put("longitude", String.valueOf(longitude));
                } else {
                    // No se pudo obtener la ubicación actual, guarda "00" en latitud y longitud
                    ubicacion.put("latitude", "00");
                    ubicacion.put("longitude", "00");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Ocurrió un error al obtener la ubicación, guarda "00" en latitud y longitud
                ubicacion.put("latitude", "00");
                ubicacion.put("longitude", "00");
            }
        });
    }
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main3, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        obtenerUbicacion();


/*
        while (!isLocationEnabled) {
            // Comprobar si la ubicación está habilitada
            // Verificar si el servicio de ubicación está habilitado
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isLocationEnabled) {
                // Mostrar un diálogo o notificación al usuario para que active la ubicación
                // Aquí puedes mostrar un mensaje de error o solicitar que el usuario active la ubicación en la configuración del dispositivo
                Toast.makeText(getContext(), "Por favor, activa la ubicación para guardar el registro.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            } else {
                obtenerUbicacion();
            }}
        obtenerUbicacion();
*/
        SharedPreferences preferenciasCompartidas = getActivity().getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);

        total = view.findViewById(R.id.textViewTotal2);
        // Crear canal de notificaciones
        createNotificationChannel();
        // Configuración de idioma

        String codigoIdioma = preferenciasCompartidas.getString("codigo_idioma", "");
         timeE = preferenciasCompartidas.getString("entrada", "");
        contadorE=preferenciasCompartidas.getBoolean("contadorE", true);
        setAppLocale(codigoIdioma);
        //getSupportActionBar().setTitle(R.string.app_name);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        // Verificar si se ha concedido el permiso de ubicación
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // El permiso no se ha concedido, se solicita al usuario
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            //se vuelve a solicitar??
            ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);}

        // Guardar debe estar desactivado hasta que se pulse Calcular

        total = view.findViewById(R.id.textViewTotal2);
        registroEntrada = view.findViewById(R.id.btnRegistroEntrada2);
        entrada = view.findViewById(R.id.textViewE2);
        salida = view.findViewById(R.id.textViewS2);
        registroSalida = view.findViewById(R.id.btnRegistroSalida2);
        //registroSalida.setEnabled(false);

        // Rellenamos el Spinner
        spInci = (Spinner) view.findViewById(R.id.spnIncidencia);
        Resources res = getResources();
        inci = res.getStringArray(R.array.array_inci);
        adaptadorInci = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, inci);
        spInci.setAdapter(adaptadorInci);

        spInci.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences preferenciasCompartidas = getActivity().getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
                editorPreferencias = preferenciasCompartidas.edit();

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
                FirebaseApp.initializeApp(getActivity().getApplicationContext());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                db = FirebaseDatabase.getInstance();

                // SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
                String corr = preferenciasCompartidas.getString("email", "");
                //String ubi= preferenciasCompartidas.getString("latitude", "")+" "+preferenciasCompartidas.getString("longitude", "");

                Map<String, String> datos = new HashMap<>();
                int posicionInci = spInci.getSelectedItemPosition();
                datos.put("tipo", "entrada");
                datos.put("incidencia", inci[posicionInci]);
                db.getReference("usuarios").child(corr).child("Registros").child(String.format("%04d%02d%02d%02d%02d", anyoE, mesE, diaE, horaE, minE)).setValue(datos);
                db.getReference("usuarios").child(corr).child("Registros").child(String.format("%04d%02d%02d%02d%02d", anyoE, mesE, diaE, horaE, minE)).child("ubicacion").setValue(ubicacion);
                Toast.makeText(getContext(), latitude+"", Toast.LENGTH_SHORT).show();

                editorPreferencias.putBoolean("contadorE", false);

                editorPreferencias.commit();
                registroSalida.setEnabled(true);
                registroEntrada.setEnabled(false);
                // Cambiar el color de fondo del botón de salida cuando se desactiva
               // registroEntrada.setBackgroundColor(getResources().getColor(R.color.purple_500));
               // registroSalida.setBackgroundColor(getResources().getColor(R.color.purple_700));
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
                FirebaseApp.initializeApp(getActivity().getApplicationContext());
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
                Toast.makeText(getContext(), latitude+"", Toast.LENGTH_SHORT).show();

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
                // Cambiar el color de fondo del botón de salida cuando se desactiva
                //registroEntrada.setBackgroundColor(getResources().getColor(R.color.purple_700));
                //registroSalida.setBackgroundColor(getResources().getColor(R.color.purple_500));
                editorPreferencias.putString("entrada", "");
               // entrada.setText("");
               // editorPreferencias.putString("entrada", "");
            }

        });



        return view;
    }

    //Guardamos los valores cuando la aplicacion este en pause (se gire la pantalla por ejemplo)

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            entrada.setText(savedInstanceState.getString("entrada"));
            salida.setText(savedInstanceState.getString("salida"));
            total.setText(savedInstanceState.getString("total"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("total", total.getText().toString());
        outState.putString("entrada", entrada.getText().toString());
        outState.putString("salida", salida.getText().toString());
    }

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setHasOptionsMenu(true);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      int id = item.getItemId();
      if (id == R.id.configuracion) {
          Toast.makeText(getActivity().getApplicationContext(), "Configuración pulsado", Toast.LENGTH_LONG).show();
          Intent intencion = new Intent(getActivity().getApplicationContext(), MainActivityConf.class);
          startActivity(intencion);
          return true;
      }
      return super.onOptionsItemSelected(item);
  }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_en_activity, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            NotificationManager gestorNotificaciones = getActivity().getSystemService(NotificationManager.class);
            gestorNotificaciones.createNotificationChannel(canal);
        }

    }

  /*  @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
        // Realiza alguna acción cuando cambia el estado de captura del puntero
    }*/

}


