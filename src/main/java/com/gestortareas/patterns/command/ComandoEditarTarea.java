package com.gestortareas.patterns.command;

import com.gestortareas.model.Prioridad;
import com.gestortareas.model.TareaBase;

import java.time.LocalDate;

// Edita los datos de una tarea o proyecto guardando los valores previos para poder deshacer
public class ComandoEditarTarea implements Comando {

    private final TareaBase elemento;
    private final String nuevoTitulo;
    private final String nuevaDescripcion;
    private final Prioridad nuevaPrioridad;
    private final LocalDate nuevaFechaLimite;

    private String tituloAnterior;
    private String descripcionAnterior;
    private Prioridad prioridadAnterior;
    private LocalDate fechaAnterior;

    public ComandoEditarTarea(TareaBase elemento,
                              String nuevoTitulo,
                              String nuevaDescripcion,
                              Prioridad nuevaPrioridad,
                              LocalDate nuevaFechaLimite) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento a editar no puede ser nulo.");
        }
        this.elemento = elemento;
        this.nuevoTitulo = nuevoTitulo;
        this.nuevaDescripcion = nuevaDescripcion;
        this.nuevaPrioridad = nuevaPrioridad;
        this.nuevaFechaLimite = nuevaFechaLimite;
    }

    @Override
    public void ejecutar() {
        if (nuevoTitulo == null || nuevoTitulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (nuevaPrioridad == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula.");
        }

        this.tituloAnterior = elemento.getTitulo();
        this.descripcionAnterior = elemento.getDescripcion();
        this.prioridadAnterior = elemento.getPrioridad();
        this.fechaAnterior = elemento.getFechaLimite();

        elemento.setTitulo(nuevoTitulo);
        elemento.setDescripcion(nuevaDescripcion);
        elemento.setPrioridad(nuevaPrioridad);
        elemento.setFechaLimite(nuevaFechaLimite);
    }

    @Override
    public void deshacer() {
        elemento.setTitulo(tituloAnterior);
        elemento.setDescripcion(descripcionAnterior);
        elemento.setPrioridad(prioridadAnterior);
        elemento.setFechaLimite(fechaAnterior);
    }

    @Override
    public String getDescripcion() {
        return "Editar [" + elemento.getId() + "] -> " + nuevoTitulo;
    }
}
