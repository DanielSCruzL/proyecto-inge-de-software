package gestortareas;

import gestortareas.model.TareaBase;

/**
 * Interfaz del patrón State.
 * Define el contrato de transiciones válidas para el ciclo de vida de una tarea o proyecto.
 */
public interface EstadoTarea {

    String getNombre();

    boolean esFinal();

    void iniciar(TareaBase tarea);

    void enviarARevision(TareaBase tarea);

    void completar(TareaBase tarea);

    void reabrir(TareaBase tarea);
}
