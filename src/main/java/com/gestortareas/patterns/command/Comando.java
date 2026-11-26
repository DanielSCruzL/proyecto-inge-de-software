package com.gestortareas.patterns.command;

public interface Comando {

    void ejecutar();

    void deshacer();

    String getDescripcion();
}
