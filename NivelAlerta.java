package com.gestortareas.model;

/**
 * Clasificación de alerta según la fecha límite y el estado actual de una tarea o proyecto.
 */
public enum NivelAlerta {
    SIN_FECHA("Sin fecha"),
    A_TIEMPO("A tiempo"),
    PROXIMA_A_VENCER("Próxima a vencer"),
    VENCIDA("Vencida"),
    COMPLETADA("Completada");

    private final String etiqueta;

    NivelAlerta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
