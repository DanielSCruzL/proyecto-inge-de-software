package com.gestortareas.model;

import com.gestortareas.patterns.state.EstadoPendiente;
import com.gestortareas.patterns.state.EstadoTarea;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Clase base abstracta del modelo de dominio.
 * Cumple dos roles centrales en el diseño:
 * 1) Componente (Component) del patrón Composite para tratar de forma uniforme tareas simples y proyectos.
 * 2) Contexto (Context) del patrón State para delegar las transiciones de estado.
 */
public abstract class TareaBase {

    public static final int DIAS_ALERTA_PROXIMA = 3;

    private final String id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private LocalDate fechaLimite;
    private EstadoTarea estado;

    protected TareaBase(String id, String titulo, String descripcion, Prioridad prioridad, LocalDate fechaLimite) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El identificador no puede estar vacío.");
        }
        this.id = id.trim();
        setTitulo(titulo);
        setDescripcion(descripcion);
        setPrioridad(prioridad);
        this.fechaLimite = fechaLimite;
        this.estado = new EstadoPendiente();
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        this.titulo = titulo.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = (descripcion == null) ? "" : descripcion.trim();
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula.");
        }
        this.prioridad = prioridad;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estado = estado;
    }

    public String getNombreEstado() {
        return estado.getNombre();
    }

    public boolean estaTerminada() {
        return estado.esFinal();
    }

    // --- Operaciones delegadas al patrón State ---

    public void iniciar() {
        estado.iniciar(this);
    }

    public void enviarARevision() {
        estado.enviarARevision(this);
    }

    public void completar() {
        estado.completar(this);
    }

    public void reabrir() {
        estado.reabrir(this);
    }

    // --- Evaluación de alertas por fecha límite ---

    public NivelAlerta evaluarAlerta(LocalDate fechaReferencia) {
        if (fechaReferencia == null) {
            throw new IllegalArgumentException("La fecha de referencia no puede ser nula.");
        }
        if (estaTerminada()) {
            return NivelAlerta.COMPLETADA;
        }
        if (fechaLimite == null) {
            return NivelAlerta.SIN_FECHA;
        }
        if (fechaLimite.isBefore(fechaReferencia)) {
            return NivelAlerta.VENCIDA;
        }
        if (!fechaLimite.isAfter(fechaReferencia.plusDays(DIAS_ALERTA_PROXIMA))) {
            return NivelAlerta.PROXIMA_A_VENCER;
        }
        return NivelAlerta.A_TIEMPO;
    }

    // --- Operaciones del patrón Composite ---

    public abstract boolean esCompuesta();

    public abstract int contarTareasSimples();

    public abstract int contarTareasTerminadas();

    public double calcularPorcentajeAvance() {
        int total = contarTareasSimples();
        if (total == 0) {
            return estaTerminada() ? 100.0 : 0.0;
        }
        return (contarTareasTerminadas() * 100.0) / total;
    }

    public List<TareaBase> obtenerSubtareas() {
        return Collections.emptyList();
    }

    public abstract String mostrarResumen(String prefijo);

    @Override
    public String toString() {
        String fechaTexto = (fechaLimite == null) ? "Sin fecha" : fechaLimite.toString();
        return "[" + id + "] " + titulo + " (" + getNombreEstado() + " | " + prioridad + " | " + fechaTexto + ")";
    }
}
