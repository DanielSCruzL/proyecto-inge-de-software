package gestortareas.patterns.state;

import gestortareas.model.TareaBase;

/**
 * Estado de verificación previa al cierre.
 * Permite aprobar y pasar a Terminada, devolver a En curso si requiere ajustes, o reabrir a Pendiente.
 */
public class EstadoEnRevision implements EstadoTarea {

    @Override
    public String getNombre() {
        return "En revisión";
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
        throw new IllegalStateException("La tarea ya se encuentra En revisión.");
    }

    @Override
    public void completar(TareaBase tarea) {
        tarea.setEstado(new EstadoTerminada());
    }

    @Override
    public void reabrir(TareaBase tarea) {
        tarea.setEstado(new EstadoPendiente());
    }
}
