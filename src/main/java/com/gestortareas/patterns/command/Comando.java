package com.gestortareas.patterns.command;

// Interfaz base para acciones reversibles (deshacer / rehacer)
public interface Comando {

    void ejecutar();

    void deshacer();

    String getDescripcion();
}
