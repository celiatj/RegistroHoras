package com.example.calculadorhoras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
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

// Agregar un escuchador de eventos para el campo de texto de correo electrónico
       /* correo.addTextChangedListener(new TextWatcher() {
                                          @Override
                                          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                              // No se necesita implementación aquí
                                          }

                                          @Override
                                          public void onTextChanged(CharSequence s, int start, int before, int count) {
                                              // Actualizar el estado del botón de registro
                                              if (s.toString().trim().length() > 0) {
                                                  registro.setEnabled(true);
                                              } else {
                                                  registro.setEnabled(false);
                                              }
                                          }

                                          @Override
                                          public void afterTextChanged(Editable s) {

                                          }
                                      });
*/// fin de la escucha
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
/*
            mAuth = FirebaseAuth.getInstance().createUserWithEmailAndPassword(corr,cont).addnCompleteListener{
                if(this.isSuccessful){
//intent pasa nombre a otra activity
                }else{
                    //show alert error
                }
*/
                Map<String, String> datos = new HashMap<>();

                datos.put("nombre", nom);
                datos.put("apellidos", ap);
                datos.put("correo", corr);
                datos.put("contraseña", cont);
                databaseReference.child("usuarios").child("usuario").push().setValue(datos);


                mAuth.signInWithEmailAndPassword(corr, cont)
                        .addOnCompleteListener(FirebaseActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);

                                }

                            }
                        });
                    objetoMensajeroDatos.putExtra("nombre", nombre.getText().toString());
                    objetoMensajeroDatos.putExtra("apellido", apellido.getText().toString());

                    // Reinicia el contador para permitir la próxima secuencia de pulsaciones
                    contador = 0;
                    Toast.makeText(getApplicationContext(), "Registro completado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(objetoMensajero);
                    //startActivity(objetoMensajeroDatos);
            }}
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  /*
                String corr = correo.getText().toString();
                String cont = contraseya.getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    // Name, email address, and profile photo Url
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();

                    // Check if user's email is verified
                    boolean emailVerified = user.isEmailVerified();

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getIdToken() instead.
                    String uid = user.getUid();
                    if(emailVerified==true){

                        startActivity(objetoMensajero);
                    }else{
                        Toast.makeText(getApplicationContext(), "No se encontro el usuario", Toast.LENGTH_SHORT).show();
                        System.out.println("no se encontro el usuario");
                    }
                }
*/

                        String correoText = correo.getText().toString();
                        String contrasenaText = contraseya.getText().toString();

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signInWithEmailAndPassword(correoText, contrasenaText)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user.isEmailVerified()) {
                                                startActivity(objetoMensajero);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "El usuario no ha verificado su correo electrónico", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "El correo o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });



            }

}