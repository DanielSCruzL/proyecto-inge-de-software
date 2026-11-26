package com.gestortareas.model;

import com.gestortareas.patterns.composite.TareaSimple;

import java.time.LocalDate;

/**
 * Representa una plantilla reutilizable para crear tareas recurrentes
 * con valores predefinidos de título, descripción, prioridad y plazo.
 */
public class PlantillaTarea {

    private final String nombrePlantilla;
    private final String tituloBase;
    private final String descripcionBase;
    private final Prioridad prioridad;
    private final int diasHastaVencimiento;
    private final int periodicidadDias;

    public PlantillaTarea(String nombrePlantilla,
                          String tituloBase,
                          String descripcionBase,
                          Prioridad prioridad,
                          int diasHastaVencimiento,
                          int periodicidadDias) {
        if (nombrePlantilla == null || nombrePlantilla.isBlank()) {
            throw new IllegalArgumentException("El nombre de la plantilla no puede estar vacío.");
        }
        if (tituloBase == null || tituloBase.isBlank()) {
            throw new IllegalArgumentException("El título base no puede estar vacío.");
        }
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad de la plantilla no puede ser nula.");
        }
        if (diasHastaVencimiento < 0) {
            throw new IllegalArgumentException("Los días hasta el vencimiento no pueden ser negativos.");
        }
        if (periodicidadDias <= 0) {
            throw new IllegalArgumentException("La periodicidad en días debe ser mayor que cero.");
        }
        this.nombrePlantilla = nombrePlantilla.trim();
        this.tituloBase = tituloBase.trim();
        this.descripcionBase = (descripcionBase == null) ? "" : descripcionBase.trim();
        this.prioridad = prioridad;
        this.diasHastaVencimiento = diasHastaVencimiento;
        this.periodicidadDias = periodicidadDias;
    }

    public String getNombrePlantilla() {
        return nombrePlantilla;
    }

    public String getTituloBase() {
        return tituloBase;
    }

    public String getDescripcionBase() {
        return descripcionBase;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public int getDiasHastaVencimiento() {
        return diasHastaVencimiento;
    }

    public int getPeriodicidadDias() {
        return periodicidadDias;
    }

    /**
     * Genera una nueva tarea simple a partir de los datos de esta plantilla.
     */
    public TareaSimple crearTarea(String nuevoId, LocalDate fechaBase) {
        LocalDate base = (fechaBase == null) ? LocalDate.now() : fechaBase;
        LocalDate fechaLimite = base.plusDays(diasHastaVencimiento);
        return new TareaSimple(nuevoId, tituloBase, descripcionBase, prioridad, fechaLimite);
    }

    @Override
    public String toString() {
        return nombrePlantilla + " (cada " + periodicidadDias + " días | Prioridad: " + prioridad + ")";
    }
}
