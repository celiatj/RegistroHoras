package com.example.calculadorhoras;

public class Registro {

        private String id;
        private String tipo;
        private String incidencia;

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
    }

