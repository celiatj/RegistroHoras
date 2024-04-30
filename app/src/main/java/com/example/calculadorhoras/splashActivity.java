package com.example.calculadorhoras;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class splashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animationView);
        animationView.playAnimation();
      //  getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                finish();
            }

        }, 4000);


    }


}
