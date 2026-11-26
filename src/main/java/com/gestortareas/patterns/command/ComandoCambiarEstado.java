package com.gestortareas.patterns.command;

import com.gestortareas.model.TareaBase;
import com.gestortareas.patterns.state.EstadoTarea;

// Ejecuta un cambio de estado y respalda el estado anterior para poder revertirlo
public class ComandoCambiarEstado implements Comando {

    private final TareaBase elemento;
    private final AccionEstado accion;
    private EstadoTarea estadoAnterior;

    public ComandoCambiarEstado(TareaBase elemento, AccionEstado accion) {
        this.elemento = elemento;
        this.accion = accion;
    }

    @Override
    public void ejecutar() {
        this.estadoAnterior = elemento.getEstado();
        switch (accion) {
            case INICIAR:
                elemento.iniciar();
                break;
            case ENVIAR_A_REVISION:
                elemento.enviarARevision();
                break;
            case COMPLETAR:
                elemento.completar();
                break;
            case REABRIR:
                elemento.reabrir();
                break;
            default:
                throw new IllegalArgumentException("Acción de estado no soportada: " + accion);
        }
    }

    @Override
    public void deshacer() {
        if (estadoAnterior != null) {
            elemento.setEstado(estadoAnterior);
        }
    }

    @Override
    public String getDescripcion() {
        return accion.getEtiqueta() + " [" + elemento.getId() + "] " + elemento.getTitulo();
    }
}
