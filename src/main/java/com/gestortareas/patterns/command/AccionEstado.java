package com.gestortareas.patterns.command;

public enum AccionEstado {
    INICIAR("Iniciar"),
    ENVIAR_A_REVISION("Enviar a revisión"),
    COMPLETAR("Completar"),
    REABRIR("Reabrir");

    private final String etiqueta;

    AccionEstado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
