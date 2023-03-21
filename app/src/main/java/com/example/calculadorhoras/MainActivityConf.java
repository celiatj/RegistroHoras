package com.example.calculadorhoras;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;


import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

public class MainActivityConf extends AppCompatActivity {

    String ap, nom;
    private Spinner spPais;
    private EditText nombre;
    private EditText ap1;
    private String[] arrayPaises;
    private ArrayAdapter<String> adaptadorPaises;
    private Switch swNotificaciones;
    private Button guardar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ap1 = findViewById(R.id.etAp1);
        nombre=findViewById(R.id.etnombre);
        // Configuración de idioma
        SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferenciasCompartidas.edit();

        editor.apply();

        String codigoIdioma = preferenciasCompartidas.getString("codigo_idioma", "es");
        setAppLocale(codigoIdioma);
        setContentView(R.layout.activity_main_conf);


        getSupportActionBar().setTitle("Configuracion");

        // Rellenamos el Spinner
        spPais = (Spinner) findViewById(R.id.idiomas);
        Resources res = getResources();
        arrayPaises = res.getStringArray(R.array.array_paises);
        adaptadorPaises = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayPaises);
        spPais.setAdapter(adaptadorPaises);


        spPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  // Idioma
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
                SharedPreferences.Editor editorPreferencias = preferencias.edit();

                int idiomaAnterior = preferencias.getInt("idioma", 0);
                int idiomaActual = spPais.getSelectedItemPosition();

                if (idiomaAnterior != idiomaActual) {
                    AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(MainActivityConf.this);
                    constructorDialogo.setMessage(getText(R.string.aviso_cambio_idioma_mensaje));
                    constructorDialogo.setPositiveButton(getText(R.string.aviso_cambio_idioma_aceptar), (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog dialogoAvisoCambioIdioma = constructorDialogo.create();
                    dialogoAvisoCambioIdioma.show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
        // Idioma
        int idioma = preferenciasCompartidas.getInt("idioma", 0);
        spPais.setSelection(idioma);

        //nombres
        String nombreGuardado = preferenciasCompartidas.getString("nombre", "");
        String ap1Guardado = preferenciasCompartidas.getString("ap1", "");


}


    @Override
    protected void onPause() {
        super.onPause();
        // Guardamos las opciones seleccionadas por la persona usuaria en las SharedPreferences
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        // Creación de un objeto Editor para escribir en las preferencias
        SharedPreferences.Editor editorPreferencias = preferencias.edit();

        int idioma = (int) spPais.getSelectedItemId();
        editorPreferencias.putInt("idioma", idioma);
        // Código de idioma
        switch (idioma) {
            case 0:
                editorPreferencias.putString("codigo_idioma", "es");
                break;
            case 1:
                editorPreferencias.putString("codigo_idioma", "ca");
                break;
            case 2:
                editorPreferencias.putString("codigo_idioma", "en");
                break;
            default:
                editorPreferencias.putString("codigo_idioma", "es");
                break;
        }
        // Una vez hechos los cambios, debemos confirmarlos o consolidarlos
        String nom = nombre.getText().toString();
        String ap = ap1.getText().toString();
        editorPreferencias.putString("nombre", nom);
        editorPreferencias.putString("ap1", ap);
        editorPreferencias.apply();

        editorPreferencias.commit();

    }


    @Override
    protected void onResume() {
        super.onResume();
       //Guardamos las prefencias
        SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        guardar = findViewById(R.id.guardar);
        nombre = findViewById(R.id.etnombre);
        ap1 = findViewById(R.id.etAp1);
        String nombreGuardado = preferenciasCompartidas.getString("nombre", "");
        String ap1Guardado = preferenciasCompartidas.getString("ap1", "");
        ap1.setText(ap1Guardado);
        nombre.setText(nombreGuardado);

        // Guardamos los valores de nombre y apallido
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = nombre.getText().toString();
                String ap = ap1.getText().toString();

                SharedPreferences.Editor editorPreferencias = preferenciasCompartidas.edit();
                editorPreferencias.putString("nombre", nom);
                editorPreferencias.putString("ap1", ap);
                editorPreferencias.apply();

                Toast.makeText(getApplicationContext(), "Guardado", Toast.LENGTH_LONG).show();
            }

        });
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



}
