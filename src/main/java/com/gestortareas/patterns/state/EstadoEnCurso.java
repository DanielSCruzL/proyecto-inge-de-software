package gestortareas.;

import gestortareas.model.TareaBase;


public class EstadoEnCurso implements EstadoTarea {

    @Override
    public String getNombre() {
        return "En curso";
    }

    @Override
    public boolean esFinal() {
        return false;
    }

    @Override
    public void iniciar(TareaBase tarea) {
        throw new IllegalStateException("La tarea ya se encuentra En curso.");
    }

    @Override
    public void enviarARevision(TareaBase tarea) {
        tarea.setEstado(new EstadoEnRevision());
    }

    @Override
    public void completar(TareaBase tarea) {
        throw new IllegalStateException("La tarea debe pasar por En revisión antes de marcarse como Terminada.");
    }

    @Override
    public void reabrir(TareaBase tarea) {
        tarea.setEstado(new EstadoPendiente());
    }
}
