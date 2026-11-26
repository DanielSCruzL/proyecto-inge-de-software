package com.gestortareas.service;

import com.gestortareas.model.NivelAlerta;
import com.gestortareas.model.PlantillaTarea;
import com.gestortareas.model.Prioridad;
import com.gestortareas.model.TareaBase;
import com.gestortareas.patterns.composite.TareaSimple;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de consultas, filtros, alertas, plantillas y validaciones del servicio central.
 */
class GestorTareasServiceTest {

    @Test
    void alertasPorFechaLimiteClasificanCorrectamente() {
        GestorTareasService servicio = new GestorTareasService();
        LocalDate referencia = LocalDate.of(2026, 11, 25);

        TareaSimple vencida = servicio.crearTareaSimple("Vencida", "", Prioridad.ALTA, referencia.minusDays(2), null);
        TareaSimple proxima = servicio.crearTareaSimple("Próxima", "", Prioridad.MEDIA, referencia.plusDays(2), null);
        TareaSimple aTiempo = servicio.crearTareaSimple("A tiempo", "", Prioridad.BAJA, referencia.plusDays(10), null);
        TareaSimple sinFecha = servicio.crearTareaSimple("Sin fecha", "", Prioridad.BAJA, null, null);
        TareaSimple terminada = servicio.crearTareaSimple("Ya cerrada", "", Prioridad.ALTA, referencia.minusDays(5), null);

        servicio.iniciarTarea(terminada.getId());
        servicio.enviarTareaARevision(terminada.getId());
        servicio.completarTarea(terminada.getId());

        assertEquals(NivelAlerta.VENCIDA, vencida.evaluarAlerta(referencia));
        assertEquals(NivelAlerta.PROXIMA_A_VENCER, proxima.evaluarAlerta(referencia));
        assertEquals(NivelAlerta.A_TIEMPO, aTiempo.evaluarAlerta(referencia));
        assertEquals(NivelAlerta.SIN_FECHA, sinFecha.evaluarAlerta(referencia));
        assertEquals(NivelAlerta.COMPLETADA, terminada.evaluarAlerta(referencia));

        assertEquals(1, servicio.obtenerTareasVencidas(referencia).size());
        assertEquals(1, servicio.obtenerTareasProximasAVencer(referencia).size());
    }

    @Test
    void filtrosIndividualesYCombinadosFuncionanCorrectamente() {
        GestorTareasService servicio = new GestorTareasService();
        TareaSimple t1 = servicio.crearTareaSimple("Informe final", "Redactar conclusiones", Prioridad.ALTA, null, null);
        servicio.crearTareaSimple("Revisar menú", "Ajustar consola", Prioridad.BAJA, null, null);
        servicio.iniciarTarea(t1.getId());

        List<TareaBase> enCurso = servicio.filtrarPorEstado("En curso");
        assertEquals(1, enCurso.size());
        assertEquals(t1.getId(), enCurso.get(0).getId());

        List<TareaBase> prioridadAlta = servicio.filtrarPorPrioridad(Prioridad.ALTA);
        assertEquals(1, prioridadAlta.size());

        List<TareaBase> porTexto = servicio.buscarPorTexto("conclusiones");
        assertEquals(1, porTexto.size());

        List<TareaBase> combinado = servicio.filtrarCombinado("En curso", Prioridad.ALTA, "informe");
        assertEquals(1, combinado.size());
        assertEquals(t1.getId(), combinado.get(0).getId());
    }

    @Test
    void plantillasRecurrentesPermitenRegistrarYCrearTareas() {
        GestorTareasService servicio = new GestorTareasService();
        LocalDate base = LocalDate.of(2026, 11, 25);

        int cantidadInicial = servicio.obtenerPlantillas().size();
        assertFalse(servicio.obtenerPlantillas().isEmpty());

        PlantillaTarea nuevaPlantilla = new PlantillaTarea(
                "Respaldo semanal",
                "Realizar copia de seguridad",
                "Guardar versión del repositorio",
                Prioridad.MEDIA,
                3,
                7
        );
        servicio.registrarPlantilla(nuevaPlantilla);
        assertEquals(cantidadInicial + 1, servicio.obtenerPlantillas().size());

        TareaSimple creada = servicio.crearTareaDesdePlantilla(cantidadInicial, null, base);
        assertNotNull(creada);
        assertEquals(base.plusDays(3), creada.getFechaLimite());
        assertEquals(Prioridad.MEDIA, creada.getPrioridad());
    }

    @Test
    void validacionesDeIntegridadYCargaDeEjemploIdempotente() {
        GestorTareasService servicio = new GestorTareasService();
        TareaSimple simple = servicio.crearTareaSimple("Tarea hoja", "", Prioridad.BAJA, null, null);

        // No se puede crear una subtarea dentro de una tarea simple
        assertThrows(IllegalArgumentException.class,
                () -> servicio.crearTareaSimple("Subtarea inválida", "", Prioridad.MEDIA, null, simple.getId()));

        // ID inexistente lanza IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarElemento("NO-EXISTE"));

        // Cargar datos de ejemplo una sola vez sin borrar historial previo
        assertTrue(servicio.puedeDeshacer());
        assertTrue(servicio.cargarDatosEjemplo());
        int totalTrasPrimeraCarga = servicio.obtenerTodosLosElementosAplanados().size();
        assertFalse(servicio.cargarDatosEjemplo());
        assertEquals(totalTrasPrimeraCarga, servicio.obtenerTodosLosElementosAplanados().size());
        assertTrue(servicio.puedeDeshacer());
    }
}
