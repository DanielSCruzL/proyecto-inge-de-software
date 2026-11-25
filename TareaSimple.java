package com.gestortareas.patterns.composite;

import com.gestortareas.model.Prioridad;
import com.gestortareas.model.TareaBase;

import java.time.LocalDate;

/**
 * Hoja (Leaf) del patrón Composite.
 * Representa una tarea individual sin subtareas.
 */
public class TareaSimple extends TareaBase {

    public TareaSimple(String id, String titulo, String descripcion, Prioridad prioridad, LocalDate fechaLimite) {
        super(id, titulo, descripcion, prioridad, fechaLimite);
    }

    @Override
    public boolean esCompuesta() {
        return false;
    }

    @Override
    public int contarTareasSimples() {
        return 1;
    }

    @Override
    public int contarTareasTerminadas() {
        return estaTerminada() ? 1 : 0;
    }

    @Override
    public String mostrarResumen(String prefijo) {
        String fecha = (getFechaLimite() == null) ? "Sin fecha" : getFechaLimite().toString();
        return prefijo + "- [Tarea " + getId() + "] " + getTitulo()
                + " | Estado: " + getNombreEstado()
                + " | Prioridad: " + getPrioridad()
                + " | Límite: " + fecha;
    }
}
