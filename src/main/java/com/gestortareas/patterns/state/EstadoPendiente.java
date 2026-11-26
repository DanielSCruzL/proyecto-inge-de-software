package com.gestortareas.patterns.state;

import com.gestortareas.model.TareaBase;

/**
 * Estado inicial de una tarea o proyecto.
 * Desde Pendiente únicamente puede pasar a En curso.
 */
public class EstadoPendiente implements EstadoTarea {

    @Override
    public String getNombre() {
        return "Pendiente";
    }

    @Override
    public boolean esFinal() {
        return false;
    }

    @Override
    public void iniciar(TareaBase tarea) {
        tarea.setEstado(new EstadoEnCurso());
    }

    @Override
    public void enviarARevision(TareaBase tarea) {
        throw new IllegalStateException("No se puede enviar a revisión una tarea en estado Pendiente. Debe iniciarse primero.");
    }

    @Override
    public void completar(TareaBase tarea) {
        throw new IllegalStateException("No se puede terminar directamente una tarea Pendiente. Debe pasar por En curso y En revisión.");
    }

    @Override
    public void reabrir(TareaBase tarea) {
        throw new IllegalStateException("La tarea ya se encuentra en estado Pendiente.");
    }
}
