package com.example.calculadorhoras;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

public class RegistroUsuarios extends Fragment implements DatePickerDialog.OnDateSetListener {
    private RecyclerView mRecyclerView;
    private TextView tvnombre;
    private AdapterUsu mAdapterU;
    private Button btnUBuscar;
    private ArrayList<RegistroUsu> mRegistrosU;
    private FirebaseDatabase db;
    private DatabaseReference usuarioRef;
    private String fechaInicio = "";
    private String fechaFin = "";
    private double ubicacionLatitude = 39.4672809;
    private double ubicacionLongitude = -0.3869;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_registro_usuarios, container, false);

        Button btnUBuscar = view.findViewById(R.id.btnUbuscar);
        EditText etUFechaInicio = view.findViewById(R.id.etUFechaInicio);
        EditText etUFechaFin = view.findViewById(R.id.etUFechaFin);

        etUFechaInicio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                com.example.calculadorhoras.DatePicker fragmentSelectorFecha;
                fragmentSelectorFecha = new com.example.calculadorhoras.DatePicker();
                fragmentSelectorFecha.show(getParentFragmentManager(), (String) getText(R.string.selector_fecha_ini));
            }
        });

        etUFechaFin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                com.example.calculadorhoras.DatePicker fragmentSelectorFecha;
                fragmentSelectorFecha = new com.example.calculadorhoras.DatePicker();
                fragmentSelectorFecha.show(getParentFragmentManager(), (String) getText(R.string.selector_fecha_fin));
            }
        });

        etUFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(true);
            }
        });

        etUFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(false);
            }
        });

        // Obtener la referencia al RecyclerView
        mRecyclerView = view.findViewById(R.id.rvusu);

        // Inicializar la lista de registros
        mRegistrosU = new ArrayList<>();

        // Obtener el correo electrónico del usuario desde los extras del intent
        SharedPreferences preferenciasCompartidas = getActivity().getSharedPreferences("PreferenciasCompartidas", MODE_PRIVATE);
        db = FirebaseDatabase.getInstance();
        String corr = preferenciasCompartidas.getString("email", "");

        tvnombre = view.findViewById(R.id.tvnombreUsuario);
        tvnombre.setText(corr);

        // Configurar el RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapterU = new AdapterUsu(ubicacionLatitude, ubicacionLongitude);
        mRecyclerView.setAdapter(mAdapterU);

        // Obtener referencia a la base de datos
        DatabaseReference ref = db.getReference("usuarios").child(corr).child("Registros");

        // Leer los datos de Firebase y establecer un listener para obtener actualizaciones en tiempo real
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRegistrosU.clear();
                for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                    // Obtener los datos de cada registro
                    String idRegistro = registroSnapshot.getKey();
                    String tipoRegistro = registroSnapshot.child("tipo").getValue(String.class);
                    String incidenciaRegistro = registroSnapshot.child("incidencia").getValue(String.class);

                    String latitude = registroSnapshot.child("ubicacion").child("latitude").getValue(String.class);
                    String longitude = registroSnapshot.child("ubicacion").child("longitude").getValue(String.class);

                    if (latitude != null && longitude != null) {
                        RegistroUsu registro = new RegistroUsu(idRegistro, tipoRegistro, incidenciaRegistro, latitude, longitude);
                        mRegistrosU.add(registro);
                    } else {
                        RegistroUsu registro = new RegistroUsu(idRegistro, tipoRegistro, incidenciaRegistro, "00", "00");
                        mRegistrosU.add(registro);
                    }
                }

                // Notificar al adaptador de los cambios en los datos
                mAdapterU.setRegistros(mRegistrosU);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de lectura de la base de datos
            }
        });

        btnUBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaInicio = etUFechaInicio.getText().toString().trim().replace("/", "");
                String fechaFin = etUFechaFin.getText().toString().trim().replace("/", "");

                if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
                    mAdapterU.setRegistros(mRegistrosU);
                    return;
                }

                DatabaseReference registrosRef = db.getReference("usuarios").child(corr).child("Registros");

                // Convertir las fechas en formato numérico para compararlas como enteros
                int fechaInicioNum = Integer.parseInt(fechaInicio);
                int fechaFinNum = Integer.parseInt(fechaFin);

                registrosRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mRegistrosU.clear();
                        for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                            String idRegistro = registroSnapshot.getKey();
                            String fechaRegistro = idRegistro.substring(0, 8);
                            int fechaRegistroNum = Integer.parseInt(fechaRegistro);

                            if (fechaRegistroNum >= fechaInicioNum && fechaRegistroNum <= fechaFinNum) {
                                String tipoRegistro = registroSnapshot.child("tipo").getValue(String.class);
                                String incidenciaRegistro = registroSnapshot.child("incidencia").getValue(String.class);

                                String latitude = registroSnapshot.child("ubicacion").child("latitude").getValue(String.class);
                                String longitude = registroSnapshot.child("ubicacion").child("longitude").getValue(String.class);

                                RegistroUsu registro = new RegistroUsu(idRegistro, tipoRegistro, incidenciaRegistro, latitude, longitude);
                                mRegistrosU.add(registro);
                            }
                        }

                        mAdapterU.setRegistros(mRegistrosU);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Manejar errores de lectura de la base de datos
                    }
                });
            }
        });

        return view;
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), this, year, month, day);

        if (!isStartDate && !fechaInicio.isEmpty()) {
            String[] parts = fechaInicio.split("/");
            int startYear = Integer.parseInt(parts[2]);
            int startMonth = Integer.parseInt(parts[1]) - 1;
            int startDay = Integer.parseInt(parts[0]);
            datePickerDialog.getDatePicker().setMinDate(new GregorianCalendar(startYear, startMonth, startDay).getTimeInMillis());
        }

        if (isStartDate) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        datePickerDialog.show();
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int anyo, int mes, int dia) {
        String fechaSeleccionada = String.format("%02d/%02d/%02d", anyo, (mes + 1),dia);
        EditText etFechaInicio = getView().findViewById(R.id.etUFechaInicio);
        EditText etFechaFin = getView().findViewById(R.id.etUFechaFin);

        if (etFechaInicio.hasFocus()) {
            etFechaInicio.setText(fechaSeleccionada);
            fechaInicio = fechaSeleccionada; // Actualizar fechaInicio con el valor seleccionado
        } else if (etFechaFin.hasFocus()) {
            etFechaFin.setText(fechaSeleccionada);
            fechaFin = fechaSeleccionada; // Actualizar fechaFin con el valor seleccionado
        }
    }
}
/*
        private void showDatePickerDialog ( boolean isStartDate){
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // DatePickerListener listener = new DatePickerListener();  // Instancia de la clase interna creada anteriormente

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), this, year, month, day);

            if (!isStartDate && !fechaInicio.isEmpty()) {
                String[] parts = fechaInicio.split("/");
                int startYear = Integer.parseInt(parts[2]);
                int startMonth = Integer.parseInt(parts[1]) - 1;
                int startDay = Integer.parseInt(parts[0]);
                datePickerDialog.getDatePicker().setMinDate(new GregorianCalendar(startYear, startMonth, startDay).getTimeInMillis());
            }
            datePickerDialog.show();
        }
    }
    */
    /*
    @Override
    public void onDateSet(android.widget.DatePicker view, int anyo, int mes, int dia) {
        Calendar calendario = Calendar.getInstance();
        calendario.set(Calendar.YEAR, anyo);
        calendario.set(Calendar.MONTH, mes);
        calendario.set(Calendar.DAY_OF_MONTH, dia);
        //String fechaSeleccionada = DateFormat.getDateInstance(DateFormat.FULL).format(calendario.getTime());
        String fechaSeleccionada = dia + "/" + (mes + 1) + "/" + anyo;
        EditText etFechaInicio = getView().findViewById(R.id.etUFechaInicio);
        etFechaInicio.setText(fechaSeleccionada);

*/
        /* String fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%02d",  year, month + 1,dayOfMonth);

        EditText etFechaInicio = getView().findViewById(R.id.etUFechaInicio);
        EditText etFechaFin = getView().findViewById(R.id.etUFechaFin);

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            if (fechaInicio.isEmpty()) {
                etFechaInicio.setText(fechaSeleccionada);
                fechaInicio = fechaSeleccionada; // Actualizar fechaInicio con el valor seleccionado
            } else {
                etFechaFin.setText(fechaSeleccionada);
                fechaFin = fechaSeleccionada; // Actualizar fechaFin con el valor seleccionado
            }
        } else {
            etFechaInicio.setText(fechaSeleccionada);
            etFechaFin.setText("");
            fechaInicio = fechaSeleccionada; // Actualizar fechaInicio con el valor seleccionado
            fechaFin = ""; // Limpiar fechaFin
        }

        */
/*
        btnUBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fechaInicio = etUFechaInicio.getText().toString().trim().replace("/", "");
                fechaFin = etUFechaFin.getText().toString().trim().replace("/", "");

                if (fechaInicio.isEmpty() && fechaFin.isEmpty()) {
                    mAdapterU.setRegistros(mRegistrosU);
                    return;
                }

                DatabaseReference registrosRef = db.getReference("usuarios").child(corr).child("Registros");

                // Convertir las fechas en formato numérico para compararlas como enteros
                int fechaInicioNum = Integer.parseInt(fechaInicio);
                int fechaFinNum = Integer.parseInt(fechaFin);
                registrosRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mRegistrosU.clear();
                        for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                            String idRegistro = registroSnapshot.getKey();
                            String fechaRegistro = idRegistro.substring(0, 8);
                            int fechaRegistroNum = Integer.parseInt(fechaRegistro);

                            if (fechaRegistroNum >= fechaInicioNum && fechaRegistroNum <= fechaFinNum) {
                                String tipoRegistro = registroSnapshot.child("tipo").getValue(String.class);
                                String incidenciaRegistro = registroSnapshot.child("incidencia").getValue(String.class);

                                String latitude = registroSnapshot.child("ubicacion").child("latitude").getValue(String.class);
                                String longitude = registroSnapshot.child("ubicacion").child("longitude").getValue(String.class);


                                RegistroUsu registro = new RegistroUsu(idRegistro, tipoRegistro, incidenciaRegistro, latitude, longitude);
                                mRegistrosU.add(registro);
                            }
                        }

                        mAdapterU.setRegistros(mRegistrosU);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Manejar errores de lectura de la base de datos
                    }
                });
            }
        });

        return view;
    }
*/
// String.format("%02d:%02d:%02d", horaS, minS)

// Extraer los componentes de la fecha
               /* String yearF = fechaFin.substring(0, 4);
                String monthF = fechaFin.substring(4, 6);
                String dayF = fechaFin.substring(6);

                String yearE = fechaInicio.substring(0, 4);
                String monthE = fechaInicio.substring(4, 6);
                String dayE = fechaInicio.substring(6);

                */
// Construir la nueva cadena de fecha
//String nuevaFechaF = yearF + monthF + dayF;
// String nuevaFechaE = yearE + monthE + dayE;