package com.gestortareas.patterns.command;

import com.gestortareas.model.TareaBase;
import com.gestortareas.patterns.composite.Proyecto;
import com.gestortareas.service.GestorTareasService;

public class ComandoAgregarElemento implements Comando {

    private final GestorTareasService servicio;
    private final Proyecto proyectoPadre;
    private final TareaBase elemento;

    public ComandoAgregarElemento(GestorTareasService servicio, Proyecto proyectoPadre, TareaBase elemento) {
        this.servicio = servicio;
        this.proyectoPadre = proyectoPadre;
        this.elemento = elemento;
    }

    @Override
    public void ejecutar() {
        servicio.agregarElementoInterno(proyectoPadre, elemento);
    }

    @Override
    public void deshacer() {
        servicio.eliminarElementoInterno(proyectoPadre, elemento);
    }

    @Override
    public String getDescripcion() {
        String tipo = elemento.esCompuesta() ? "proyecto" : "tarea";
        if (proyectoPadre == null) {
            return "Crear " + tipo + " [" + elemento.getId() + "] " + elemento.getTitulo();
        }
        return "Agregar " + tipo + " [" + elemento.getId() + "] en [" + proyectoPadre.getId() + "]";
    }
}
