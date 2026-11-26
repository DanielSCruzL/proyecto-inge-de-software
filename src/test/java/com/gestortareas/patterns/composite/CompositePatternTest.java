package com.gestortareas.patterns.composite;

import com.gestortareas.model.Prioridad;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del patrón Composite (TareaBase, TareaSimple y Proyecto).
 */
class CompositePatternTest {

    @Test
    void tareaSimpleReportaValoresCorrectos() {
        TareaSimple tarea = new TareaSimple("T-1", "Diseñar UML", "Diagrama de clases", Prioridad.ALTA, LocalDate.now().plusDays(3));

        assertFalse(tarea.esCompuesta());
        assertEquals(1, tarea.contarTareasSimples());
        assertEquals(0, tarea.contarTareasTerminadas());
        assertEquals(0.0, tarea.calcularPorcentajeAvance());

        tarea.iniciar();
        tarea.enviarARevision();
        tarea.completar();

        assertEquals(1, tarea.contarTareasTerminadas());
        assertEquals(100.0, tarea.calcularPorcentajeAvance());
    }

    @Test
    void proyectoCalculaAvanceRecursivoEnJerarquiaAnidada() {
        Proyecto raiz = new Proyecto("P-1", "Semestre", "Proyecto general", Prioridad.ALTA, null);
        Proyecto subproyecto = new Proyecto("P-2", "Backend", "Lógica en Java", Prioridad.MEDIA, null);

        TareaSimple t1 = new TareaSimple("T-1", "Modelo", "", Prioridad.ALTA, null);
        TareaSimple t2 = new TareaSimple("T-2", "State", "", Prioridad.MEDIA, null);
        TareaSimple t3 = new TareaSimple("T-3", "Command", "", Prioridad.MEDIA, null);
        TareaSimple t4 = new TareaSimple("T-4", "README", "", Prioridad.BAJA, null);

        subproyecto.agregarElemento(t2);
        subproyecto.agregarElemento(t3);
        raiz.agregarElemento(t1);
        raiz.agregarElemento(subproyecto);
        raiz.agregarElemento(t4);

        // Completar 2 de las 4 tareas simples (50% de avance)
        t1.iniciar();
        t1.enviarARevision();
        t1.completar();

        t2.iniciar();
        t2.enviarARevision();
        t2.completar();

        assertTrue(raiz.esCompuesta());
        assertEquals(4, raiz.contarTareasSimples());
        assertEquals(2, raiz.contarTareasTerminadas());
        assertEquals(50.0, raiz.calcularPorcentajeAvance(), 0.01);
        assertNotNull(raiz.buscarPorId("t-3"));
        assertTrue(raiz.contieneElemento("t-3"));
        assertFalse(raiz.contieneElemento(null));
    }

    @Test
    void proyectoVacioCalculaAvanceSegunSuPropioEstado() {
        Proyecto vacio = new Proyecto("P-10", "Sin subtareas", "", Prioridad.BAJA, null);

        assertEquals(0, vacio.contarTareasSimples());
        assertEquals(0.0, vacio.calcularPorcentajeAvance(), 0.01);

        vacio.iniciar();
        vacio.enviarARevision();
        vacio.completar();

        assertEquals(100.0, vacio.calcularPorcentajeAvance(), 0.01);
    }

    @Test
    void proyectoImpideReferenciasCircularesAlAgregarEInsertar() {
        Proyecto p1 = new Proyecto("P-1", "Raíz", "", Prioridad.ALTA, null);
        Proyecto p2 = new Proyecto("P-2", "Hijo", "", Prioridad.MEDIA, null);
        p1.agregarElemento(p2);

        assertThrows(IllegalArgumentException.class, () -> p1.agregarElemento(p1));
        assertThrows(IllegalArgumentException.class, () -> p2.agregarElemento(p1));
        assertThrows(IllegalArgumentException.class, () -> p2.insertarElementoEn(0, p1));
    }
}
