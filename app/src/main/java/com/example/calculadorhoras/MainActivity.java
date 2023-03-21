package com.example.calculadorhoras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_SELECTED_TIME = "selected_time";
    private static final String KEY_SELECTED_TIME_Exit = "selected_timesExit";
    public TextView total;
    public TextView entrada;
    public TimePicker timePickerE;
    public TimePicker timePickerS;
    public TextView salida;
    private Button botonCalcular;
    private Button botonSalir;
    private Button botonGuardar;
    String horasTotal;
    FileOutputStream stream = null;
    String dia;
    int tot, t, t1;
    String fin;
    public DrawerLayout drawerLayout;
    final static String CHANNEL_ID = "NOTIFICACIONES";
    String[] frases;
    MyRecyclerViewAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        total = findViewById(R.id.textViewTotal);

        super.onCreate(savedInstanceState);
        // Crear canal de notificaciones
        createNotificationChannel();

        setContentView(R.layout.activity_main);
        // Configuración de idioma
        SharedPreferences preferenciasCompartidas = getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        String codigoIdioma = preferenciasCompartidas.getString("codigo_idioma", "es");
        setAppLocale(codigoIdioma);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.app_name);

        // Guardar debe estar desactivado hasta que se pulse Calcular
        botonGuardar = findViewById(R.id.btnGuardar);
        botonGuardar.setEnabled(false);

        botonSalir = findViewById(R.id.btnSalir);
        botonGuardar = findViewById(R.id.btnGuardar);
        botonCalcular = findViewById(R.id.btnCalcular);
        total = findViewById(R.id.textViewTotal);
        timePickerE = findViewById(R.id.timePickerE);
        entrada = findViewById(R.id.textViewE);
        salida = findViewById(R.id.textViewS);
        timePickerS = findViewById(R.id.timePickerS);

        Context contexto = getApplicationContext();
        File path = contexto.getFilesDir();
        File file = new File(path, "registros.csv");

        // sacamos el valor del timePicker
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        entrada.setText(String.format("%02d:%02d", hour, minute));
        timePickerE.setOnTimeChangedListener((timePicker1, i, i1) ->
                entrada.setText(i + ":" + i1));
        //t=(i + ":" + i1));
        salida.setText(String.format("%02d:%02d", hour, minute));
        timePickerS.setOnTimeChangedListener((timePicker1, j, j1) ->
                salida.setText(j + ":" + j1));
        //t1=(j + ":" + j1));

       //creo objeto FileOutStrem para escribir bytes
        try {
            stream = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

       // gestion del boton calcular
        botonCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (entrada.getText().toString().equals("") || salida.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Debes señalar la hora de entrada y la hora de salida", Toast.LENGTH_SHORT).show();
                } else {
                    String e = entrada.getText().toString();
                    String s = salida.getText().toString();

                // Como obtenemos string lo convertimos en un array para luego castearlo a Int

                    String[] entra = e.split(":");
                    String part1 = entra[0];
                    String part2 = entra[1];

                    String[] sale = s.split(":");
                    String part3 = sale[0];
                    String part4 = sale[1];
                    int he = Integer.parseInt(part1);
                    int me = Integer.parseInt(part2);
                    int hs = Integer.parseInt(part3);
                    int ms = Integer.parseInt(part4);

                    // Utilizamos la biblioteca de calendar para sacar el dia actual

                    java.util.Date fecha = new Date();
                    Calendar c = Calendar.getInstance();
                    dia = Integer.toString(c.get(Calendar.DATE));
                    //Calculamos la diferencia en minutos para sacar las horas "Trabajadas", luego sacamos horas / 60 y los minutos sera el resto de esta division
                    int t = (he * 60) + me;
                    int t1 = (hs * 60) + ms;
                    int tot;

                    if (t1 < t) {
                        tot = (24 * 60 - t) + t1;
                    } else {
                        tot = t1 - t;
                    }

                    int horas = tot / 60;
                    int minutos = tot % 60;
                // Indicamos al usuario con el textView de total el resultado
                    horasTotal = "Día: " + dia + "; Horas: " + horas + ",Minutos: " + minutos + ";";
                    total.setText(horasTotal);

                    // Ahora activo el botón de Guardar, una vez hecho el cálculo
                    botonGuardar.setEnabled(true);
                }
            }
        });

 //boton Guardar

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                NotificationManagerCompat gestorNotificaciones = NotificationManagerCompat.from(MainActivity.this);
               // Notificacion en la barra superior
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID).setSmallIcon(R.drawable.clock_icon_design_element_logo_element_illustration_clock_symbol_icon_free_vector).setContentTitle("Calculador Horas")
                        .setContentText("Registro guardado!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                gestorNotificaciones.notify(1, builder.build());

                try {
                    horasTotal += "\r\n";
                    stream.write(horasTotal.getBytes());
                    // Una vez usado el botón de Guardar, lo vuelvo a desactivar (hasta que se haga Calcular de nuevo)
                    view.setEnabled(false);
                } catch (
                        IOException e) {
                    e.printStackTrace();
                }

            }
        });

        // Salir de la app
        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear el objeto constructor de alerta AlertDialog.Builder
                AlertDialog.Builder constructorAlerta = new AlertDialog.Builder(MainActivity.this);

                // Establecer título y mensaje
                constructorAlerta.

                        setTitle(getResources().getText(R.string.txt_salir_app_titulo)).
                        setMessage(getResources().getText(R.string.txt_salir_app_mensaje));

                // Hacer que no se pueda dejar el diálogo sin pulsar una opción
                constructorAlerta.setCancelable(false);

                // Establecer botón de respuesta positiva
                constructorAlerta.setPositiveButton(getResources().getText(R.string.txt_si), (DialogInterface.OnClickListener) (dialog, which) -> {
                    finishAffinity();
                });

                // Establecer botón de respuesta negativa
                constructorAlerta.setNegativeButton(getResources().getText(R.string.txt_no), (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });

                // Crear y mostrar diálogo de alerta
                AlertDialog dialogoAlertaSalir = constructorAlerta.create();
                dialogoAlertaSalir.show();

            }


        });
    }
 //Guardamos los valores cuando la aplicacion este en pause (se gire la pantalla por ejemplo)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int houre = savedInstanceState.getInt("houre");
        int minutee = savedInstanceState.getInt("minutee");
        timePickerE.setHour(houre);
        timePickerE.setMinute(minutee);

        int hours = savedInstanceState.getInt("hours");
        int minutes = savedInstanceState.getInt("minutes");
        timePickerS.setHour(hours);
        timePickerS.setMinute(minutes);

        total.setText(savedInstanceState.getString("total"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("houre", timePickerE.getHour());
        outState.putInt("minutee", timePickerE.getMinute());

        outState.putInt("hours", timePickerS.getHour());
        outState.putInt("minutes", timePickerS.getMinute());

        outState.putString("total", total.getText().toString());
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Control de opciones de la action bar
        int id = item.getItemId();
        if (id == R.id.configuracion) {
            Toast.makeText(getApplicationContext(), "Configuración pulsado", Toast.LENGTH_LONG).show();
            Intent intencion = new Intent(MainActivity.this, MainActivityConf.class);
            startActivity(intencion);

            return true;
        } else if (id == R.id.registro) {
            Intent objetoMensajero2 = new Intent(getApplicationContext(), RegistroActivity.class);
            startActivity(objetoMensajero2);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_en_activity, menu);
        return true;
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

    // Crear canal de notificaciones
    private void createNotificationChannel() {
        // Crear el canal de notificaciones solo a partir de la API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = getString(R.string.nombre_canal);
            String descripcion = getString(R.string.descripcion_canal);
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(CHANNEL_ID, nombre, importancia);
            canal.setDescription(descripcion);

            // Registrar el canal de notificaciones
            NotificationManager gestorNotificaciones = getSystemService(NotificationManager.class);
            gestorNotificaciones.createNotificationChannel(canal);
        }

    }

}


