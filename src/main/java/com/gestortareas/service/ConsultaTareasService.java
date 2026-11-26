package com.gestortareas.service;

import com.gestortareas.model.NivelAlerta;
import com.gestortareas.model.Prioridad;
import com.gestortareas.model.TareaBase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio dedicado a consultas, filtros y alertas por fecha límite.
 * Opera sobre colecciones de TareaBase de manera independiente a la interfaz de usuario.
 */
public class ConsultaTareasService {

    public List<TareaBase> filtrarPorEstado(List<TareaBase> elementos, String nombreEstado) {
        List<TareaBase> resultado = new ArrayList<>();
        if (nombreEstado == null || nombreEstado.isBlank() || "Todos".equalsIgnoreCase(nombreEstado)) {
            resultado.addAll(elementos);
            return resultado;
        }
        for (TareaBase elemento : elementos) {
            if (elemento.getNombreEstado().equalsIgnoreCase(nombreEstado.trim())) {
                resultado.add(elemento);
            }
        }
        return resultado;
    }

    public List<TareaBase> filtrarPorPrioridad(List<TareaBase> elementos, Prioridad prioridad) {
        List<TareaBase> resultado = new ArrayList<>();
        if (prioridad == null) {
            resultado.addAll(elementos);
            return resultado;
        }
        for (TareaBase elemento : elementos) {
            if (elemento.getPrioridad() == prioridad) {
                resultado.add(elemento);
            }
        }
        return resultado;
    }

    public List<TareaBase> buscarPorTexto(List<TareaBase> elementos, String texto) {
        List<TareaBase> resultado = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            resultado.addAll(elementos);
            return resultado;
        }
        String criterio = texto.trim().toLowerCase();
        for (TareaBase elemento : elementos) {
            String titulo = elemento.getTitulo().toLowerCase();
            String descripcion = elemento.getDescripcion().toLowerCase();
            String id = elemento.getId().toLowerCase();
            if (titulo.contains(criterio) || descripcion.contains(criterio) || id.contains(criterio)) {
                resultado.add(elemento);
            }
        }
        return resultado;
    }

    public List<TareaBase> obtenerAlertasPorNivel(List<TareaBase> elementos,
                                                  NivelAlerta nivelEsperado,
                                                  LocalDate fechaReferencia) {
        List<TareaBase> resultado = new ArrayList<>();
        for (TareaBase elemento : elementos) {
            if (elemento.evaluarAlerta(fechaReferencia) == nivelEsperado) {
                resultado.add(elemento);
            }
        }
        return resultado;
    }

    public List<TareaBase> obtenerTareasVencidas(List<TareaBase> elementos, LocalDate fechaReferencia) {
        return obtenerAlertasPorNivel(elementos, NivelAlerta.VENCIDA, fechaReferencia);
    }

    public List<TareaBase> obtenerTareasProximasAVencer(List<TareaBase> elementos, LocalDate fechaReferencia) {
        return obtenerAlertasPorNivel(elementos, NivelAlerta.PROXIMA_A_VENCER, fechaReferencia);
    }
}
