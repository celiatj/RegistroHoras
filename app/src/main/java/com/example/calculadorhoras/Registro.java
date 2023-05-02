package com.example.calculadorhoras;

public class Registro {
    private String dia;
    private String horaEntrada;
    private String horaSalida;
    private long tiempoTotal;

    public Registro(String dia, String horaEntrada, String horaSalida, long tiempoTotal) {
        this.dia = dia;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.tiempoTotal = tiempoTotal;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public long getTiempoTotal() {
        return tiempoTotal;
    }

    public void setTiempoTotal(long tiempoTotal) {
        this.tiempoTotal = tiempoTotal;
    }
}
