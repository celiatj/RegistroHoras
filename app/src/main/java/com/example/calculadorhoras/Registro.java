package com.example.calculadorhoras;

public class Registro {

    private String id;
    private String tipo;
    private String incidencia;
    private String dia;
    private String hora;
    private String min;

    public Registro(String id, String tipo, String incidencia) {
        this.id = id;
        this.tipo = tipo;
        this.incidencia = incidencia;

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
        this.dia= id.substring(0, 2);
        this.hora= id.substring(2, 4);

        if (id.length() >= 6) {
            this.min = id.substring(4, 6);
        } else {
            this.min = "00";
        }
        // cambiar cosas del dia;

        return "Dia: " + dia+" hora: "+hora+" min: "+min;
    }
}