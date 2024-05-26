package ctj.celia.calculadorhoras;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.Locale;

public class MainActivityConf extends AppCompatActivity {

    private EditText nombre;
    private EditText ap1;
    private Switch swNotificaciones;
    private Button guardar, atras;
    private Spinner spIdiomas;
    private String[] arrayIdiomas;
    private ArrayAdapter<String> adaptadorIdiomas;
    private DatabaseReference usuarioRef; // Referencia a los datos del usuario en Firebase
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    SharedPreferences preferencias;
    double longitude, latitude;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_conf); // Asignar el layout correspondiente
        getSupportActionBar().setTitle(R.string.configuracion);
        // Configuración de idioma
        preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String codigoIdioma = preferencias.getString("codigo_idioma", "es");
        setAppLocale(codigoIdioma);

        //SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String corr = preferencias.getString("email", "");

        // Obtener la referencia a los datos del usuario en Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuarioRef = database.getReference("usuarios").child(corr);

        // Rellenamos el Spinner
        spIdiomas = findViewById(R.id.idiomas);
        Resources res = getResources();
        arrayIdiomas = res.getStringArray(R.array.array_paises);
// Obtener el color desde los recursos
        final int customColor = res.getColor(R.color.ic_launcher_background);

        adaptadorIdiomas = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayIdiomas) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setBackgroundColor(Color.TRANSPARENT); // Fondo transparente
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD); // Establecer la letra en negrita
                textView.setTextColor(customColor); // Usar color personalizado
                textView.setPadding(6, 6, 6, 6); // Agregar padding
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.dropdown_item, parent, false);
                }

                TextView textView = (TextView) convertView.findViewById(R.id.dropdown_item_text);
                textView.setText(getItem(position).toString());

                return convertView;
            }
        };

        spIdiomas.setAdapter(adaptadorIdiomas);

        int idioma = preferencias.getInt("idioma", 0);
        spIdiomas.setSelection(idioma);
        // Obtener referencias a las vistas
        ap1 = findViewById(R.id.etAp1);
        nombre = findViewById(R.id.etnombre);
        guardar = findViewById(R.id.guardar);
        atras = findViewById(R.id.btnAtras);

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

        spIdiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                int idiomaAnterior = preferencias.getInt("idioma", 0);
                int idiomaActual = spIdiomas.getSelectedItemPosition();
                String[] codigosIdioma = {"es", "en", "fr"}; // Mapeo de índices a códigos de idioma

                if (idiomaAnterior != idiomaActual) {
                    AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(MainActivityConf.this);
                    constructorDialogo.setMessage(getText(R.string.aviso_cambio_idioma_mensaje));
                    constructorDialogo.setPositiveButton(getText(R.string.aviso_cambio_idioma_aceptar), (dialog, which) -> {
                        // Guardar la selección del idioma
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putInt("idioma", idiomaActual);
                        editor.apply();

                        editor.putInt("idioma", idiomaActual);
                        editor.apply();
                        // Obtener el código de idioma correspondiente al índice seleccionado
                        String codigoIdiomaSeleccionado = codigosIdioma[idiomaActual];

                        // Guardar el código del idioma seleccionado en SharedPreferences
                        editor.putString("codigo_idioma", codigoIdiomaSeleccionado);
                        editor.apply();

                        // Establecer el idioma de la aplicación
                        setAppLocale(codigoIdiomaSeleccionado);

                        dialog.dismiss();
                    });
                    AlertDialog dialogoAvisoCambioIdioma = constructorDialogo.create();
                    dialogoAvisoCambioIdioma.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No es necesario hacer nada aquí
            }
        });


        // Recuperar los valores de los EditTexts desde SharedPreferences
        SharedPreferences preferencias = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String nombreGuardado = preferencias.getString("nombre", "");
        String apellidoGuardado = preferencias.getString("apellidos", "");

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBack = new Intent(getApplicationContext(), FirebaseActivity.class);
                startActivity(intentBack);
                finish();
            }});
        // Mostrar los valores recuperados en los EditTexts correspondientes
//        nombre.setText(nombreGuardado);
//        ap1.setText(apellidoGuardado);

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