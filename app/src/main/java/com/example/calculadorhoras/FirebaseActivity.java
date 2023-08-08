package com.example.calculadorhoras;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FirebaseActivity extends AppCompatActivity {
    public static class NetworkUtils {
        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        }



    }
    String esAdmin;
    private EditText correo;
    private EditText contraseya;
    private EditText nombre;
    private EditText apellido;
    private Button registro;
    private Button login;
    private ImageView imgOjo;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private FirebaseDatabase db;
    int contador = 0;
    private boolean contadorOjo = true;
    double longitude,latitude;
    Map<String, String> ubicacion = new HashMap<>();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean validarCorreo(String correo) {
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        // Inicializar aplicación de Firebase
        FirebaseApp.initializeApp(getApplicationContext());

        db = FirebaseDatabase.getInstance();
        getSupportActionBar().setTitle(R.string.login);

        mAuth = FirebaseAuth.getInstance();

        contraseya = findViewById(R.id.edContraseya);
        correo = findViewById(R.id.edCorreo);

        nombre = findViewById(R.id.edtnombre);
        apellido = findViewById(R.id.edtap);
        nombre.setVisibility(View.GONE);
        apellido.setVisibility(View.GONE);

        registro = findViewById(R.id.btnregistro);
        login = findViewById(R.id.btnlogin);
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        SharedPreferences.Editor editorPreferencias = preferencias.edit();
        imgOjo = findViewById(R.id.imgOjo);


        imgOjo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (contadorOjo) {
                    imgOjo.setImageResource(R.drawable.en);
                    contraseya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    contadorOjo = false;
                } else {
                    imgOjo.setImageResource(R.drawable.sa);
                    contraseya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    contadorOjo = true;
                }
            }
        });

        CheckBox checkBox = findViewById(R.id.checkBox);
        checkBox.setChecked(preferencias.getBoolean("record", false));
        if (checkBox.isChecked()) {
            String correoT = preferencias.getString("correo", "");
            String contrasenya = preferencias.getString("contrasenya", "");
            contraseya.setText(contrasenya);
            correo.setText(correoT);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editorPreferencias.putBoolean("record", isChecked);
                editorPreferencias.apply(); // Guarda los cambios en SharedPreferences

                if (isChecked) {
                    // CheckBox marcado
                    String correoT = preferencias.getString("correo", "");
                    String contrasenya = preferencias.getString("contrasenya", "");
                    contraseya.setText(contrasenya);
                    correo.setText(correoT);
                } else {
                    // CheckBox desmarcado
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void reload() {
    }

    private void updateUI(FirebaseUser user) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Verificar si el servicio de ubicación está habilitado
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // Comprobar si la ubicación está habilitada
        if (!isLocationEnabled) {
            // Mostrar un diálogo o notificación al usuario para que active la ubicación
            // Aquí puedes mostrar un mensaje de error o solicitar que el usuario active la ubicación en la configuración del dispositivo
            Toast.makeText(this, "Por favor, activa la ubicación para guardar el registro.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        /*else
            obtenerUbicacion();
        */





        // Guardar correo en las SharedPreferences para posterior uso
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        SharedPreferences.Editor editorPreferencias = preferencias.edit();
        Intent objetoMensajero = new Intent(getApplicationContext(), MainActivity.class);
        Intent objetoMensajeroAdmin = new Intent(getApplicationContext(), Administrador.class);
       // Intent objetoMensajeroDatos = new Intent(getApplicationContext(), MainActivityConf.class);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInternetAvailable = NetworkUtils.isNetworkAvailable(getApplicationContext());
                editorPreferencias.putString("email", "");
                editorPreferencias.putString("correo", "");
                editorPreferencias.putString("contrasenya", "");
                editorPreferencias.putString("entrada", "");
                editorPreferencias.putInt("horaE", 0);
                editorPreferencias.putInt("minE", 0);
                editorPreferencias.putString("salida", "");
                editorPreferencias.putString("codigo_idioma", "es");
                editorPreferencias.putString("nombre", "");
                editorPreferencias.putString("apellidos", "");
                editorPreferencias.putBoolean("contadorE", true);
                editorPreferencias.commit();

                if (isInternetAvailable) {
                    // Hay conexión a Internet, realiza las operaciones necesarias
                    contador++;

                    if (contador == 1) {
                        nombre.setVisibility(View.VISIBLE);
                        apellido.setVisibility(View.VISIBLE);
                        login.setEnabled(false);
                        // Avisa al usuario que tiene que pulsar de nuevo
                        Toast.makeText(getApplicationContext(), "Pulse de nuevo para completar el registro", Toast.LENGTH_SHORT).show();
                    } else if (contador >= 2) {
                        String nom = nombre.getText().toString();
                        String ap = apellido.getText().toString();
                        String corr = correo.getText().toString();
                        String cont = contraseya.getText().toString();

                        if (TextUtils.isEmpty(nom)) {
                            Toast.makeText(getApplicationContext(), getText(R.string.toast_introducir_nombre).toString(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (TextUtils.isEmpty(corr)) {
                            Toast.makeText(getApplicationContext(), getText(R.string.toast_introducir_correo).toString(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (TextUtils.isEmpty(ap)) {
                            Toast.makeText(getApplicationContext(), getText(R.string.toast_introducir_apellido).toString(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (TextUtils.isEmpty(cont)) {
                            Toast.makeText(getApplicationContext(), getText(R.string.toast_introducir_password).toString(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (cont.length() < 6) {
                            Toast.makeText(getApplicationContext(), getText(R.string.toast_introducir_longitud).toString(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!validarCorreo(corr)) {
                            Toast.makeText(getApplicationContext(), getText(R.string.correo_Invalido).toString(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // create new user or register new user
                        mAuth.createUserWithEmailAndPassword(corr, cont)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), getText(R.string.toast_registro_exito).toString(), Toast.LENGTH_LONG).show();

                                            // Creamos ese usuario en la base de datos en tiempo real de Firebase
                                            // No se permite el caracter punto en las rutas de Firebase así que lo filtramos
                                            DatabaseReference refUsuario = db.getReference("usuarios").child(corr.replace(".", ""));

                                            // Crear un nuevo objeto de datos en formato JSON
                                            Map<String, String> datos = new HashMap<>();
                                            datos.put("nombre", nom);
                                            datos.put("apellidos", ap);
                                            datos.put("correo", corr);
                                            datos.put("Admin", "no");

                                            refUsuario.setValue(datos);

                                            // Reinicia el contador para permitir la próxima secuencia de pulsaciones
                                            contador = 0;
                                            Toast.makeText(getApplicationContext(), "Registro completado correctamente", Toast.LENGTH_SHORT).show();
                                            editorPreferencias.putString("email", corr.replace(".", ""));
                                            editorPreferencias.putString("correo", corr);
                                            editorPreferencias.putString("contrasenya", cont);
                                            editorPreferencias.commit();
                                            startActivity(objetoMensajero);
                                        } else {
                                            // Registro fallido
                                            Toast.makeText(getApplicationContext(), getText(R.string.toast_registro_fallo).toString(), Toast.LENGTH_LONG).show();

                                            // Vaciar los campos de correo y contraseña
                                            correo.setText("");
                                            contraseya.setText("");
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getText(R.string.toast_sin_conexion).toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    boolean isInternetAvailable = NetworkUtils.isNetworkAvailable(getApplicationContext());
                    if (isInternetAvailable) {
                        // Hay conexión a Internet, realiza las operaciones necesarias

                        String correoText = correo.getText().toString();
                        String contrasenaText = contraseya.getText().toString();
                        SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
                       // editorPreferencias = preferenciasCompartidas.edit();
                        String corr = preferenciasCompartidas.getString("email", "");
                     /*   if(corr.equals(correoText)){}else{
                            editorPreferencias.putString("email", "");
                            editorPreferencias.putString("correo", "");
                            editorPreferencias.putString("contrasenya","");
                            editorPreferencias.putString("entrada", "");
                            editorPreferencias.putInt("horaE", 0);
                            editorPreferencias.putInt("minE",0);
                            editorPreferencias.putString("salida", "");
                            editorPreferencias.putString("codigo_idioma", "es");
                            editorPreferencias.putString("nombre","");
                            editorPreferencias.putString("apellidos","");
                            editorPreferencias.putBoolean("contadorE", true);
                            editorPreferencias.commit();

                        }*/
                        // validations for input email and password
                        if (TextUtils.isEmpty(correoText)) {
                            Toast.makeText(getApplicationContext(),
                                            getText(R.string.toast_introducir_email).toString(),
                                            Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        if (TextUtils.isEmpty(contrasenaText)) {
                            Toast.makeText(getApplicationContext(),
                                            getText(R.string.toast_introducir_password).toString(),
                                            Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        // signin existing user
                        mAuth.signInWithEmailAndPassword(correoText, contrasenaText)
                                .addOnCompleteListener(
                                        new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(
                                                    @NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(),
                                                                    getText(R.string.toast_identificacion_exito).toString() + " " + correoText,
                                                                    Toast.LENGTH_LONG)
                                                            .show();


                                                    editorPreferencias.putString("email", correoText.replace(".", ""));
                                                    editorPreferencias.putString("correo", correoText);
                                                    editorPreferencias.putString("contrasenya", contrasenaText);
                                                    editorPreferencias.commit();

                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("usuarios").child(correoText.replace(".", "")).child("Admin");
                                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            esAdmin = dataSnapshot.getValue(String.class);

                                                            if (esAdmin != null && esAdmin.equals("si")) {
                                                                // Realiza las operaciones necesarias si esAdmin no es nulo y es igual a "si"
                                                                startActivity(objetoMensajeroAdmin);
                                                            } else {
                                                                // Realiza las operaciones necesarias si esAdmin es nulo o no es igual a "si"
                                                                startActivity(objetoMensajero);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            // Manejo de errores
                                                        }
                                                    });
                                                    // objetoMensajeroDatos.putExtra("correo", correoText.replace(".", ""));
                                                    // startActivity(objetoMensajeroDatos);
                                                } else {

                                                    // sign-in failed
                                                    Toast.makeText(getApplicationContext(),
                                                                    getText(R.string.toast_identificacion_fallo).toString(),
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            }
                                        });
                                            }else {
                                       Toast.makeText(getApplicationContext(),
                                            getText(R.string.toast_sin_conexion).toString(),
                                            Toast.LENGTH_LONG)
                                    .show();
                        } }});


            CheckBox checkBox = findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editorPreferencias.putBoolean("record", isChecked);
                    editorPreferencias.apply(); // Guarda los cambios en SharedPreferences

                    if (isChecked) {
                        // CheckBox marcado
                        String correoT = preferencias.getString("correo", "");
                        String contrasenya = preferencias.getString("contrasenya", "");
                        contraseya.setText(contrasenya);
                        correo.setText(correoT);
                    } else {
                        // CheckBox desmarcado
                    }
                }
            });

/*
        mAuth
                .createUserWithEmailAndPassword("celiatoribio95@gmail.com", "practicas")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            getText(R.string.toast_registro_exito).toString(),
                                            Toast.LENGTH_LONG)
                                    .show();

                            // Creamos ese usuario en la base de datos en tiempo real de Firebase
                            // No se permite el caracter punto en las rutas de Firebase así que lo filtramos
                            DatabaseReference refUsuario = db.getReference("usuarios").child("celiatoribio95@gmail.com".replace(".", ""));

                            // Crear un nuevo objeto de datos en formato JSON

                            Map<String, String> datos = new HashMap<>();

                            datos.put("nombre", "CELIA");
                            datos.put("apellidos", "TORIBIO");
                            datos.put("correo", "celiatoribio95@gmail.com");
                            datos.put("Admin","si");
                            databaseReference.child("usuarios").child("celiatoribio95@gmail.com".replace(".", "")).setValue(datos);

                        }}});

*/

    }

}

