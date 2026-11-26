package com.gestortareas.patterns.command;

import com.gestortareas.model.TareaBase;
import com.gestortareas.patterns.composite.Proyecto;
import com.gestortareas.service.GestorTareasService;

// Elimina una tarea o proyecto guardando su posición original para restaurarlo al deshacer
public class ComandoEliminarElemento implements Comando {

    private final GestorTareasService servicio;
    private final Proyecto proyectoPadre;
    private final TareaBase elemento;
    private int indiceOriginal;

    public ComandoEliminarElemento(GestorTareasService servicio, Proyecto proyectoPadre, TareaBase elemento) {
        this.servicio = servicio;
        this.proyectoPadre = proyectoPadre;
        this.elemento = elemento;
        this.indiceOriginal = -1;
    }

    @Override
    public void ejecutar() {
        this.indiceOriginal = servicio.obtenerIndiceEnContenedor(proyectoPadre, elemento);
        servicio.eliminarElementoInterno(proyectoPadre, elemento);
    }

    @Override
    public void deshacer() {
        servicio.restaurarElementoInterno(proyectoPadre, indiceOriginal, elemento);
    }

    @Override
    public String getDescripcion() {
        return "Eliminar [" + elemento.getId() + "] " + elemento.getTitulo();
    }
}
