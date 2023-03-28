package com.example.calculadorhoras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
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
        if(currentUser != null){
            reload();
        }
    }

    private void reload() {
    }
    private void updateUI(FirebaseUser user){}
    @Override
    protected void onResume() {
        super.onResume();
        // Inicializar aplicación de Firebase
        FirebaseApp.initializeApp(getApplicationContext());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        registro.setOnClickListener(new View.OnClickListener() {
           // if(correo.isNotEmpty()){}
           @Override
           public void onClick(View view) {
               nombre.setVisibility(View.VISIBLE);
               apellido.setVisibility(View.VISIBLE);


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
*/      Map<String, String> datos = new HashMap<>();

        datos.put("nombre", nom);
        datos.put("apellidos", ap);
        datos.put("correo", corr);
        datos.put("contraseña", cont);
        databaseReference.child("usuarios").child("usuario").setValue(datos);
               // [START create_user_with_email]
               mAuth.createUserWithEmailAndPassword(corr, cont)
                       .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()) {
                                   // Sign in success, update UI with the signed-in user's information
                                   Log.d(TAG, "createUserWithEmail:success");
                                   FirebaseUser user = mAuth.getCurrentUser();
                                   updateUI(user);
                               } else {
                                   // If sign in fails, display a message to the user.
                                   Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                   Toast.makeText(FirebaseActivity.this, "Authentication failed.",
                                           Toast.LENGTH_SHORT).show();
                                   updateUI(null);
                               }



                           ;
                           // [END create_user_with_email]
                       };
           });

        };

        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String corr = correo.getText().toString();
                String cont = contraseya.getText().toString();



                // [START sign_in_with_email]
                mAuth.signInWithEmailAndPassword(corr, cont)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(FirebaseActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
                // [END sign_in_with_email]
            }

        });
    }

        private void sendEmailVerification() {
            // Send verification email
            // [START send_email_verification]
            final FirebaseUser user = mAuth.getCurrentUser();
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Email sent
                        }
                    });
            // [END send_email_verification]
        }


    }