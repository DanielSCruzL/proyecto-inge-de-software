package com.gestortareas.patterns.state;

import com.gestortareas.model.Prioridad;
import com.gestortareas.patterns.composite.Proyecto;
import com.gestortareas.patterns.composite.TareaSimple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del patrón State (transiciones válidas, inválidas y polimorfismo en TareaBase).
 */
class StatePatternTest {

    @Test
    void flujoCompletoDeEstadosValidosFuncionaCorrectamente() {
        TareaSimple tarea = new TareaSimple("T-1", "Probar State", "", Prioridad.MEDIA, null);
        assertEquals("Pendiente", tarea.getNombreEstado());
        assertFalse(tarea.estaTerminada());

        tarea.iniciar();
        assertEquals("En curso", tarea.getNombreEstado());
        assertFalse(tarea.estaTerminada());

        tarea.enviarARevision();
        assertEquals("En revisión", tarea.getNombreEstado());
        assertFalse(tarea.estaTerminada());

        // Devolver a En curso si requiere ajustes
        tarea.iniciar();
        assertEquals("En curso", tarea.getNombreEstado());

        // Volver a revisión y completar
        tarea.enviarARevision();
        tarea.completar();
        assertEquals("Terminada", tarea.getNombreEstado());
        assertTrue(tarea.estaTerminada());

        // Reabrir vuelve a Pendiente
        tarea.reabrir();
        assertEquals("Pendiente", tarea.getNombreEstado());
        assertFalse(tarea.estaTerminada());
    }

    @Test
    void reabrirDesdeEnCursoYDesdeEnRevisionDevuelveAPendiente() {
        TareaSimple tarea = new TareaSimple("T-3", "Reapertura intermedia", "", Prioridad.BAJA, null);

        tarea.iniciar();
        tarea.reabrir();
        assertEquals("Pendiente", tarea.getNombreEstado());

        tarea.iniciar();
        tarea.enviarARevision();
        tarea.reabrir();
        assertEquals("Pendiente", tarea.getNombreEstado());
    }

    @Test
    void proyectoTambienRespetaElCicloDeVidaDelPatronState() {
        Proyecto proyecto = new Proyecto("P-1", "Entrega 1", "Proyecto académico", Prioridad.ALTA, null);
        assertEquals("Pendiente", proyecto.getNombreEstado());

        proyecto.iniciar();
        proyecto.enviarARevision();
        proyecto.completar();

        assertEquals("Terminada", proyecto.getNombreEstado());
        assertTrue(proyecto.estaTerminada());
    }

    @Test
    void transicionesInvalidasLanzanIllegalStateException() {
        TareaSimple tarea = new TareaSimple("T-2", "Validar restricciones", "", Prioridad.ALTA, null);

        // Desde Pendiente no se puede enviar a revisión, completar ni reabrir
        assertThrows(IllegalStateException.class, tarea::enviarARevision);
        assertThrows(IllegalStateException.class, tarea::completar);
        assertThrows(IllegalStateException.class, tarea::reabrir);

        // Desde En curso no se puede volver a iniciar ni completar directamente
        tarea.iniciar();
        assertThrows(IllegalStateException.class, tarea::iniciar);
        assertThrows(IllegalStateException.class, tarea::completar);

        // Desde En revisión no se puede enviar de nuevo a revisión
        tarea.enviarARevision();
        assertThrows(IllegalStateException.class, tarea::enviarARevision);

        // Desde Terminada no se puede iniciar, enviar a revisión ni completar
        tarea.completar();
        assertThrows(IllegalStateException.class, tarea::iniciar);
        assertThrows(IllegalStateException.class, tarea::enviarARevision);
        assertThrows(IllegalStateException.class, tarea::completar);
    }
}
