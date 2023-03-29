package com.example.calculadorhoras;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class splashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animationView);
        animationView.playAnimation();

/*
        // Inicializar aplicación de Firebase
        FirebaseApp.initializeApp(getApplicationContext());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", "Juan");
        datos.put("apellidos", "Pruebas");

        databaseReference.child("usuarios").child("123").setValue(datos);
    */
    }

    @Override
    protected void onResume() {
        super.onResume();


/**/
        //pasados 4 segundos la activity cambiara
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent objetoMensajero = new Intent(getApplicationContext(), FirebaseActivity.class);
                startActivity(objetoMensajero);
            }

        }, 4000);


    }


}
