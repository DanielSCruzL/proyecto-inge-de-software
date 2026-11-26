package com.gestortareas.patterns.state;

import com.gestortareas.model.TareaBase;

/**
 * Estado final de una tarea completada.
 * Únicamente permite reabrirse para volver al estado Pendiente.
 */
public class EstadoTerminada implements EstadoTarea {

    @Override
    public String getNombre() {
        return "Terminada";
    }

    @Override
    public boolean esFinal() {
        return true;
    }

    @Override
    public void iniciar(TareaBase tarea) {
        throw new IllegalStateException("Una tarea Terminada debe reabrirse antes de volver a iniciarse.");
    }

    @Override
    public void enviarARevision(TareaBase tarea) {
        throw new IllegalStateException("Una tarea Terminada no puede enviarse a revisión sin reabrirse.");
    }

    @Override
    public void completar(TareaBase tarea) {
        throw new IllegalStateException("La tarea ya se encuentra en estado Terminada.");
    }

    @Override
    public void reabrir(TareaBase tarea) {
        tarea.setEstado(new EstadoPendiente());
    }
}
