package com.gestortareas.patterns.command;

import com.gestortareas.model.Prioridad;
import com.gestortareas.model.TareaBase;
import com.gestortareas.patterns.composite.Proyecto;
import com.gestortareas.patterns.composite.TareaSimple;
import com.gestortareas.service.GestorTareasService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del patrón Command (ejecución, deshacer, rehacer y manejo de errores).
 */
class CommandPatternTest {

    @Test
    void deshacerYRehacerCreacionYEdicionDeTarea() {
        GestorTareasService servicio = new GestorTareasService();
        TareaSimple tarea = servicio.crearTareaSimple("Original", "Desc 1", Prioridad.BAJA, null, null);
        String id = tarea.getId();

        servicio.editarElemento(id, "Editada", "Desc 2", Prioridad.ALTA, LocalDate.of(2026, 12, 10));
        assertEquals("Editada", servicio.buscarPorId(id).getTitulo());
        assertEquals(Prioridad.ALTA, servicio.buscarPorId(id).getPrioridad());

        // Deshacer edición
        assertTrue(servicio.deshacer());
        assertEquals("Original", servicio.buscarPorId(id).getTitulo());
        assertEquals(Prioridad.BAJA, servicio.buscarPorId(id).getPrioridad());

        // Deshacer creación
        assertTrue(servicio.deshacer());
        assertNull(servicio.buscarPorId(id));

        // Rehacer creación y edición
        assertTrue(servicio.rehacer());
        assertNotNull(servicio.buscarPorId(id));
        assertTrue(servicio.rehacer());
        assertEquals("Editada", servicio.buscarPorId(id).getTitulo());
    }

    @Test
    void deshacerYRehacerCambioDeEstadoYEliminacion() {
        GestorTareasService servicio = new GestorTareasService();
        Proyecto proyecto = servicio.crearProyecto("P-Demo", "", Prioridad.MEDIA, null, null);
        TareaSimple tarea = servicio.crearTareaSimple("Subtarea", "", Prioridad.MEDIA, null, proyecto.getId());

        servicio.iniciarTarea(tarea.getId());
        assertEquals("En curso", tarea.getNombreEstado());

        servicio.reabrirTarea(tarea.getId());
        assertEquals("Pendiente", tarea.getNombreEstado());

        // Deshacer reapertura vuelve a En curso
        assertTrue(servicio.deshacer());
        assertEquals("En curso", tarea.getNombreEstado());

        // Deshacer inicio vuelve a Pendiente
        assertTrue(servicio.deshacer());
        assertEquals("Pendiente", tarea.getNombreEstado());

        // Eliminar y deshacer eliminación
        servicio.eliminarElemento(tarea.getId());
        assertNull(servicio.buscarPorId(tarea.getId()));
        assertTrue(servicio.deshacer());
        TareaBase restaurada = servicio.buscarPorId(tarea.getId());
        assertNotNull(restaurada);
        assertEquals(1, proyecto.obtenerSubtareas().size());
    }

    @Test
    void nuevoComandoDespuesDeDeshacerLimpiaLaPilaDeRehacer() {
        GestorTareasService servicio = new GestorTareasService();
        assertFalse(servicio.deshacer());
        assertFalse(servicio.rehacer());

        TareaSimple t1 = servicio.crearTareaSimple("Primera", "", Prioridad.MEDIA, null, null);
        servicio.deshacer();
        assertTrue(servicio.puedeRehacer());

        // Al ejecutar un nuevo comando, la pila de rehacer debe vaciarse
        servicio.crearTareaSimple("Nueva rama", "", Prioridad.ALTA, null, null);
        assertFalse(servicio.puedeRehacer());
        assertFalse(servicio.rehacer());
        assertNull(servicio.buscarPorId(t1.getId()));
    }

    @Test
    void comandoFallidoNoModificaElObjetoNiAlteraElHistorial() {
        GestorTareasService servicio = new GestorTareasService();
        TareaSimple tarea = servicio.crearTareaSimple("Prueba error", "Desc inicial", Prioridad.MEDIA, null, null);
        int historialAntes = servicio.obtenerHistorialAcciones().size();

        // Transición inválida no entra al historial
        assertThrows(IllegalStateException.class, () -> servicio.completarTarea(tarea.getId()));
        assertEquals(historialAntes, servicio.obtenerHistorialAcciones().size());

        // Edición inválida (prioridad nula) no debe modificar parcialmente el título ni entrar al historial
        assertThrows(IllegalArgumentException.class,
                () -> servicio.editarElemento(tarea.getId(), "Título nuevo", "Desc nueva", null, null));
        assertEquals("Prueba error", tarea.getTitulo());
        assertEquals("Desc inicial", tarea.getDescripcion());
        assertEquals(Prioridad.MEDIA, tarea.getPrioridad());
        assertEquals(historialAntes, servicio.obtenerHistorialAcciones().size());
        assertFalse(servicio.puedeRehacer());
    }
}
