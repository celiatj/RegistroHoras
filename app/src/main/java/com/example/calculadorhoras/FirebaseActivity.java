package com.example.calculadorhoras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


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

    private EditText correo;
    private EditText contraseya;
    private EditText nombre;
    private EditText apellido;
    private Button registro;
    private Button login;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private FirebaseDatabase db;
    int contador = 0;
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

    // Manejar la respuesta de los permisos
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
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Aquí puedes utilizar las coordenadas de latitud y longitud obtenidas
                            // por ejemplo, guardarlas en la base de datos o mostrarlas en la interfaz de usuario
                            SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
                            SharedPreferences.Editor editorPreferencias = preferencias.edit();

                            // Ejemplo: Guardar las coordenadas en la base de datos
                            DatabaseReference ubicacionRef = db.getReference("ubicacion");
                            ubicacionRef.child("latitude").setValue(latitude);
                            ubicacionRef.child("longitude").setValue(longitude);
                            editorPreferencias.putString("longitude", String.valueOf(longitude));;
                            editorPreferencias.putString("latitud", String.valueOf(latitude));
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
        boolean isInternetAvailable = NetworkUtils.isNetworkAvailable(getApplicationContext());
        if (isInternetAvailable) {
            // Hay conexión a Internet, realiza las operaciones necesarias

        // Guardar correo en las SharedPreferences para posterior uso
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        SharedPreferences.Editor editorPreferencias = preferencias.edit();
        Intent objetoMensajero = new Intent(getApplicationContext(), MainActivity3.class);
       // Intent objetoMensajeroDatos = new Intent(getApplicationContext(), MainActivityConf.class);

        registro.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
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
                    Toast.makeText(getApplicationContext(),
                                    getText(R.string.toast_introducir_nombre).toString(),
                                    Toast.LENGTH_LONG)
                            .show();return;}
                    if (TextUtils.isEmpty(corr)) {
                        Toast.makeText(getApplicationContext(),
                                        getText(R.string.toast_introducir_correo).toString(),
                                        Toast.LENGTH_LONG)
                                .show();
                        return;}
                        if (TextUtils.isEmpty(ap)) {
                            Toast.makeText(getApplicationContext(),
                                            getText(R.string.toast_introducir_apellido).toString(),
                                            Toast.LENGTH_LONG)
                                    .show();
                            return;}
                            if (TextUtils.isEmpty(cont)) {

                        Toast.makeText(getApplicationContext(),
                                        getText(R.string.toast_introducir_password).toString(),
                                        Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                if(cont.length()<6){
                    Toast.makeText(getApplicationContext(),
                                    getText(R.string.toast_introducir_longitud).toString(),
                                    Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                    if (!validarCorreo(corr)) {
                        Toast.makeText(getApplicationContext(),
                                        getText(R.string.correo_Invalido).toString(),
                                        Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                    // create new user or register new user
                    mAuth
                            .createUserWithEmailAndPassword(corr, cont)
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
                                        DatabaseReference refUsuario = db.getReference("usuarios").child(corr.replace(".", ""));

                                        // Crear un nuevo objeto de datos en formato JSON

                                        Map<String, String> datos = new HashMap<>();

                                        datos.put("nombre", nom);
                                        datos.put("apellidos", ap);
                                        datos.put("correo", corr);
                                        //datos.put("Admin",false);
                                        databaseReference.child("usuarios").child(corr.replace(".", "")).setValue(datos);


                                    }
                                    else {

                                        // Registro fallido
                                        Toast.makeText(
                                                        getApplicationContext(),
                                                        getText(R.string.toast_registro_fallo).toString(),
                                                        Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });


                   // objetoMensajeroDatos.putExtra("correo", corr.replace(".", ""));
                   // startActivity(objetoMensajeroDatos);
                    /*
                    objetoMensajeroDatos.putExtra("apellido", apellido.getText().toString());
*/
                    // Reinicia el contador para permitir la próxima secuencia de pulsaciones
                    contador = 0;
                    Toast.makeText(getApplicationContext(), "Registro completado correctamente", Toast.LENGTH_SHORT).show();
                    editorPreferencias.putString("email", corr.replace(".", ""));
                    editorPreferencias.putString("correo", corr);
                    editorPreferencias.putString("contrasenya", cont);
                    editorPreferencias.commit();
                    startActivity(objetoMensajero);
            }}
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String correoText = correo.getText().toString();
                String contrasenaText = contraseya.getText().toString();


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

                                            // Como el login es exitoso, ir a la Activity principal
                                            startActivity(objetoMensajero);
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


            }});
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
                            datos.put("Admin",true);
                            databaseReference.child("usuarios").child("celiatoribio95@gmail.com".replace(".", "")).setValue(datos);

                        }}});
                        */


    }
     else {
            Toast.makeText(getApplicationContext(),
                            getText(R.string.toast_sin_conexion).toString(),
                            Toast.LENGTH_LONG)
                    .show();
    }
}}

