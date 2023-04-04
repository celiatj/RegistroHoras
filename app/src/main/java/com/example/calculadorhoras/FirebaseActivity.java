package com.example.calculadorhoras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseActivity extends Activity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        mAuth = FirebaseAuth.getInstance();

        contraseya = findViewById(R.id.edContraseya);
        correo = findViewById(R.id.edCorreo);

        nombre = findViewById(R.id.edtnombre);
        apellido = findViewById(R.id.edtap);
        nombre.setVisibility(View.GONE);
        apellido.setVisibility(View.GONE);

        registro = findViewById(R.id.btnregistro);
        login = findViewById(R.id.btnlogin);
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
        Intent objetoMensajero = new Intent(getApplicationContext(), MainActivity3.class);
        Intent objetoMensajeroDatos = new Intent(getApplicationContext(), MainActivityConf.class);

        // Inicializar aplicación de Firebase
        FirebaseApp.initializeApp(getApplicationContext());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseDatabase.getInstance();

        registro.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                 contador++;

                if (contador == 1) {
                    nombre.setVisibility(View.VISIBLE);
                    apellido.setVisibility(View.VISIBLE);
                    // Avisa al usuario que tiene que pulsar de nuevo
                    Toast.makeText(getApplicationContext(), "Pulse de nuevo para completar el registro", Toast.LENGTH_SHORT).show();
                } else if (contador == 2) {

                String nom = nombre.getText().toString();
                String ap = apellido.getText().toString();
                String corr = correo.getText().toString();
                String cont = contraseya.getText().toString();

                if (TextUtils.isEmpty(cont)) {
                        Toast.makeText(getApplicationContext(),
                                        getText(R.string.toast_introducir_password).toString(),
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


                    objetoMensajeroDatos.putExtra("correo", corr.replace(".", ""));
                    startActivity(objetoMensajeroDatos);
                    /*
                    objetoMensajeroDatos.putExtra("apellido", apellido.getText().toString());
*/
                    // Reinicia el contador para permitir la próxima secuencia de pulsaciones
                    contador = 0;
                   // Toast.makeText(getApplicationContext(), "Registro completado correctamente", Toast.LENGTH_SHORT).show();

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

                                            // Guardar correo en las SharedPreferences para posterior uso
                                            SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
                                            SharedPreferences.Editor editorPreferencias = preferencias.edit();
                                            editorPreferencias.putString("email", correoText);
                                            editorPreferencias.commit();

                                            // Como el login es exitoso, ir a la Activity principal
                                            startActivity(objetoMensajero);
                                            objetoMensajeroDatos.putExtra("correo", correoText.replace(".", ""));
                                            startActivity(objetoMensajeroDatos);
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
    }
}

