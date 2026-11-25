package com.gestortareas.patterns.composite;

import com.gestortareas.model.Prioridad;
import com.gestortareas.model.TareaBase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compuesto (Composite) del patrón Composite.
 * Puede contener tareas simples y otros subproyectos de forma jerárquica.
 */
public class Proyecto extends TareaBase {

    private final List<TareaBase> elementos;

    public Proyecto(String id, String titulo, String descripcion, Prioridad prioridad, LocalDate fechaLimite) {
        super(id, titulo, descripcion, prioridad, fechaLimite);
        this.elementos = new ArrayList<>();
    }

    private void validarElementoParaAgregar(TareaBase elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede agregar un elemento nulo al proyecto.");
        }
        if (elemento == this || elemento.getId().equalsIgnoreCase(this.getId())) {
            throw new IllegalArgumentException("Un proyecto no puede agregarse a sí mismo.");
        }
        if (elemento instanceof Proyecto) {
            Proyecto subproyecto = (Proyecto) elemento;
            if (subproyecto.contieneElemento(this.getId())) {
                throw new IllegalArgumentException("No se puede crear una referencia circular entre proyectos.");
            }
        }
    }

    public void agregarElemento(TareaBase elemento) {
        validarElementoParaAgregar(elemento);
        if (!elementos.contains(elemento)) {
            elementos.add(elemento);
        }
    }

    public void insertarElementoEn(int indice, TareaBase elemento) {
        validarElementoParaAgregar(elemento);
        if (elementos.contains(elemento)) {
            return;
        }
        if (indice < 0 || indice >= elementos.size()) {
            elementos.add(elemento);
        } else {
            elementos.add(indice, elemento);
        }
    }

    public boolean eliminarElemento(TareaBase elemento) {
        return elementos.remove(elemento);
    }

    public int obtenerIndiceElemento(TareaBase elemento) {
        return elementos.indexOf(elemento);
    }

    public boolean contieneElemento(String idBuscado) {
        if (idBuscado == null || idBuscado.isBlank()) {
            return false;
        }
        String buscado = idBuscado.trim();
        if (this.getId().equalsIgnoreCase(buscado)) {
            return true;
        }
        for (TareaBase hijo : elementos) {
            if (hijo.getId().equalsIgnoreCase(buscado)) {
                return true;
            }
            if (hijo instanceof Proyecto) {
                if (((Proyecto) hijo).contieneElemento(buscado)) {
                    return true;
                }
            }
        }
        return false;
    }

    public TareaBase buscarPorId(String idBuscado) {
        if (idBuscado == null || idBuscado.isBlank()) {
            return null;
        }
        String buscado = idBuscado.trim();
        if (this.getId().equalsIgnoreCase(buscado)) {
            return this;
        }
        for (TareaBase hijo : elementos) {
            if (hijo.getId().equalsIgnoreCase(buscado)) {
                return hijo;
            }
            if (hijo instanceof Proyecto) {
                TareaBase encontrado = ((Proyecto) hijo).buscarPorId(buscado);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    public List<TareaBase> aplanarElementos() {
        List<TareaBase> resultado = new ArrayList<>();
        resultado.add(this);
        for (TareaBase hijo : elementos) {
            if (hijo instanceof Proyecto) {
                resultado.addAll(((Proyecto) hijo).aplanarElementos());
            } else {
                resultado.add(hijo);
            }
        }
        return resultado;
    }

    @Override
    public List<TareaBase> obtenerSubtareas() {
        return Collections.unmodifiableList(elementos);
    }

    @Override
    public boolean esCompuesta() {
        return true;
    }

    @Override
    public int contarTareasSimples() {
        int total = 0;
        for (TareaBase hijo : elementos) {
            total += hijo.contarTareasSimples();
        }
        return total;
    }

    @Override
    public int contarTareasTerminadas() {
        int terminadas = 0;
        for (TareaBase hijo : elementos) {
            terminadas += hijo.contarTareasTerminadas();
        }
        return terminadas;
    }

    @Override
    public String mostrarResumen(String prefijo) {
        StringBuilder sb = new StringBuilder();
        String fecha = (getFechaLimite() == null) ? "Sin fecha" : getFechaLimite().toString();
        String avance = String.format("%.0f%%", calcularPorcentajeAvance());
        sb.append(prefijo)
          .append("+ [Proyecto ")
          .append(getId())
          .append("] ")
          .append(getTitulo())
          .append(" | Estado: ").append(getNombreEstado())
          .append(" | Prioridad: ").append(getPrioridad())
          .append(" | Límite: ").append(fecha)
          .append(" | Avance: ").append(avance);

        for (TareaBase hijo : elementos) {
            sb.append(System.lineSeparator());
            sb.append(hijo.mostrarResumen(prefijo + "  "));
        }
        return sb.toString();
    }
}
