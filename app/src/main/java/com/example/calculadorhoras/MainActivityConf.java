package com.example.calculadorhoras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;


import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivityConf extends AppCompatActivity {

    private EditText nombre;
    private EditText ap1;
    private Switch swNotificaciones;
    private Button guardar;
    private Spinner spIdiomas;
    private String[] arrayIdiomas;
    private ArrayAdapter<String> adaptadorIdiomas;
    private DatabaseReference usuarioRef; // Referencia a los datos del usuario en Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configuración de idioma
        SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String codigoIdioma = preferenciasCompartidas.getString("codigo_idioma", "es");
        setAppLocale(codigoIdioma);

        setContentView(R.layout.activity_main_conf); // Asignar el layout correspondiente
        getSupportActionBar().setTitle(R.string.configuracion);
        //SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String corr = preferenciasCompartidas.getString("email", "");
        // Obtener la referencia a los datos del usuario en Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuarioRef = database.getReference("usuarios").child(corr);

// Rellenamos el Spinner
        spIdiomas = (Spinner) findViewById(R.id.idiomas);
        Resources res = getResources();
        arrayIdiomas = res.getStringArray(R.array.array_paises);

        adaptadorIdiomas = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayIdiomas);
        spIdiomas.setAdapter(adaptadorIdiomas);

        spIdiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
               // SharedPreferences.Editor editorPreferencias = preferencias.edit();

                int idiomaAnterior = preferencias.getInt("idioma", 0);
                int idiomaActual = spIdiomas.getSelectedItemPosition();

                if (idiomaAnterior != idiomaActual) {
                    AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(MainActivityConf.this);
                    constructorDialogo.setMessage(getText(R.string.aviso_cambio_idioma_mensaje));
                    constructorDialogo.setPositiveButton(getText(R.string.aviso_cambio_idioma_aceptar), (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog dialogoAvisoCambioIdioma = constructorDialogo.create();
                    dialogoAvisoCambioIdioma.show();
                }
                int idioma = preferenciasCompartidas.getInt("idioma", 0);
                spIdiomas.setSelection(idioma);


                // Obtener referencias a las vistas
                ap1 = findViewById(R.id.etAp1);
                nombre = findViewById(R.id.etnombre);
                guardar = findViewById(R.id.guardar);

                // Recuperar datos del usuario y mostrarlos en los EditTexts correspondientes
                usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("nombre").getValue(String.class);
                        String subname = dataSnapshot.child("apellidos").getValue(String.class);
                        nombre.setText(name);
                        ap1.setText(subname);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar errores de lectura de la base de datos
                    }

                });

                // Configurar el botón "guardar"
                guardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Obtener los valores de los EditTexts
                        String nom = nombre.getText().toString();
                        String ap = ap1.getText().toString();

                        // Guardar los valores en Firebase
                        usuarioRef.child("nombre").setValue(nom);
                        usuarioRef.child("apellidos").setValue(ap);

                        // Mostrar un mensaje de confirmación
                        Toast.makeText(MainActivityConf.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                    }
                });

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });}



            @Override
    protected void onPause() {
        super.onPause();

        // Guardar los valores de los EditTexts en SharedPreferences
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        SharedPreferences.Editor editorPreferencias = preferencias.edit();
        editorPreferencias.putString("nombre", nombre.getText().toString());
        editorPreferencias.putString("apellidos", ap1.getText().toString());
        editorPreferencias.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recuperar los valores de los EditTexts desde SharedPreferences
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String nombreGuardado = preferencias.getString("nombre", "");
        String apellidoGuardado = preferencias.getString("apellidos", "");

        // Mostrar los valores recuperados en los EditTexts correspondientes
//        nombre.setText(nombreGuardado);
//        ap1.setText(apellidoGuardado);
    }
    private void setAppLocale(String localeCode){
        Locale myLocale = new Locale(localeCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        Locale.setDefault(myLocale);
        conf.setLayoutDirection(myLocale);
        res.updateConfiguration(conf, dm);
    }

}