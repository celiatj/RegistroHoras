package ctj.celia.calculadorhoras;

import java.util.HashMap;
import java.util.Map;

public class RegistroUsu {
    private String id;
    private String tipo;
    private String incidencia;
    private String dia,mes,anyo;
    private String horas;
    private String min;
    private String latitud,longitud;

    public RegistroUsu(String id, String tipo, String incidencia, String latitud,String longitud) {
        this.id = id;
        this.tipo = tipo;
        this.incidencia = incidencia;
        this.latitud = latitud;
        this.longitud=longitud;
        // Extraer los componentes individuales de la fecha y hora del ID
        String year = id.substring(0, 4);
        String month = id.substring(4, 6);
        String day = id.substring(6, 8);
        String hour = id.substring(8, 10);
        String minute = id.substring(10, 12);

        // Asignar los componentes a los atributos correspondientes
        this.dia = day;
        this.mes = month;
        this.anyo = year;
        this.horas = hour;
        this.min = minute;
    }

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIncidencia() {
        return incidencia;
    }

    public String getTextoId() {
        return "Fecha: " + dia + "/" + mes + "/" + anyo + " Hora: " + horas + ":" + min;
    }
    public Map<String, String> getUbicacion() {
        Map<String, String> ubicacion = new HashMap<>();
        ubicacion.put("latitude", latitud);
        ubicacion.put("longitude", longitud);
        return ubicacion;
    }
}