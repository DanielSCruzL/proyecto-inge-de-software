package com.gestortareas.console;

import com.gestortareas.model.PlantillaTarea;
import com.gestortareas.model.Prioridad;
import com.gestortareas.model.TareaBase;
import com.gestortareas.patterns.command.AccionEstado;
import com.gestortareas.service.GestorTareasService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaz de usuario por consola.
 * Consume directamente GestorTareasService sin duplicar lógica de negocio.
 */
public class ConsolaApp {

    private final GestorTareasService servicio;
    private final Scanner scanner;

    public ConsolaApp(GestorTareasService servicio) {
        this(servicio, new Scanner(System.in));
    }

    public ConsolaApp(GestorTareasService servicio, Scanner scanner) {
        this.servicio = servicio;
        this.scanner = (scanner == null) ? new Scanner(System.in) : scanner;
    }

    public void iniciar() {
        System.out.println("====================================================");
        System.out.println("    GESTOR DE TAREAS Y PROYECTOS (MODO CONSOLA)");
        System.out.println("====================================================");

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcion = leerTexto("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case "1":
                        mostrarArbol();
                        break;
                    case "2":
                        crearProyecto();
                        break;
                    case "3":
                        crearTareaSimple();
                        break;
                    case "4":
                        crearDesdePlantilla();
                        break;
                    case "5":
                        editarElemento();
                        break;
                    case "6":
                        eliminarElemento();
                        break;
                    case "7":
                        cambiarEstado();
                        break;
                    case "8":
                        consultarYFiltrar();
                        break;
                    case "9":
                        mostrarAlertas();
                        break;
                    case "10":
                        deshacer();
                        break;
                    case "11":
                        rehacer();
                        break;
                    case "12":
                        mostrarHistorial();
                        break;
                    case "13":
                        if (servicio.cargarDatosEjemplo()) {
                            System.out.println("Datos de ejemplo cargados correctamente.");
                        } else {
                            System.out.println("Los datos de ejemplo ya fueron cargados anteriormente.");
                        }
                        break;
                    case "0":
                        continuar = false;
                        System.out.println("Saliendo del sistema. ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("[AVISO] " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("[ERROR] Ocurrió un problema inesperado: " + ex.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("--- MENÚ PRINCIPAL ---");
        System.out.println("1. Ver árbol de proyectos y tareas");
        System.out.println("2. Crear proyecto");
        System.out.println("3. Crear tarea simple");
        System.out.println("4. Crear tarea desde plantilla recurrente");
        System.out.println("5. Editar tarea o proyecto");
        System.out.println("6. Eliminar tarea o proyecto");
        System.out.println("7. Cambiar estado de tarea/proyecto");
        System.out.println("8. Filtrar y buscar tareas");
        System.out.println("9. Ver alertas por fecha límite");
        System.out.println("10. Deshacer última acción (Undo)");
        System.out.println("11. Rehacer acción (Redo)");
        System.out.println("12. Ver historial de comandos");
        System.out.println("13. Cargar datos de ejemplo");
        System.out.println("0. Salir");
    }

    private void mostrarArbol() {
        List<TareaBase> raices = servicio.obtenerElementosRaiz();
        if (raices.isEmpty()) {
            System.out.println("No hay proyectos ni tareas registrados todavía.");
            return;
        }
        System.out.println();
        System.out.println("=== ESTRUCTURA DE PROYECTOS Y TAREAS ===");
        for (TareaBase raiz : raices) {
            System.out.println(raiz.mostrarResumen(""));
        }
    }

    private void crearProyecto() {
        String titulo = leerTexto("Título del proyecto: ");
        String descripcion = leerTexto("Descripción: ");
        Prioridad prioridad = leerPrioridad();
        LocalDate fecha = leerFechaOpcional("Fecha límite (AAAA-MM-DD, vacío si no aplica): ");
        String idPadre = leerTexto("ID del proyecto padre (vacío para nivel raíz): ");

        TareaBase proyecto = servicio.crearProyecto(titulo, descripcion, prioridad, fecha, idPadre);
        System.out.println("Proyecto creado con éxito: " + proyecto);
    }

    private void crearTareaSimple() {
        String titulo = leerTexto("Título de la tarea: ");
        String descripcion = leerTexto("Descripción: ");
        Prioridad prioridad = leerPrioridad();
        LocalDate fecha = leerFechaOpcional("Fecha límite (AAAA-MM-DD, vacío si no aplica): ");
        String idPadre = leerTexto("ID del proyecto padre (vacío para nivel raíz): ");

        TareaBase tarea = servicio.crearTareaSimple(titulo, descripcion, prioridad, fecha, idPadre);
        System.out.println("Tarea creada con éxito: " + tarea);
    }

    private void crearDesdePlantilla() {
        List<PlantillaTarea> plantillas = servicio.obtenerPlantillas();
        if (plantillas.isEmpty()) {
            System.out.println("No hay plantillas disponibles.");
            return;
        }
        System.out.println("--- Plantillas recurrentes disponibles ---");
        for (int i = 0; i < plantillas.size(); i++) {
            System.out.println((i + 1) + ". " + plantillas.get(i));
        }
        String seleccion = leerTexto("Número de plantilla: ");
        int indice;
        try {
            indice = Integer.parseInt(seleccion) - 1;
        } catch (NumberFormatException ex) {
            System.out.println("Debe ingresar un número entero válido.");
            return;
        }
        String idPadre = leerTexto("ID del proyecto padre (vacío para nivel raíz): ");
        TareaBase creada = servicio.crearTareaDesdePlantilla(indice, idPadre, LocalDate.now());
        System.out.println("Tarea generada desde plantilla: " + creada);
    }

    private void editarElemento() {
        String id = leerTexto("ID del elemento a editar (ej. P-1 o T-1): ");
        TareaBase actual = servicio.buscarPorId(id);
        if (actual == null) {
            System.out.println("No se encontró ningún elemento con el ID: " + id);
            return;
        }
        System.out.println("Elemento actual: " + actual);
        String titulo = leerTexto("Nuevo título: ");
        String descripcion = leerTexto("Nueva descripción: ");
        Prioridad prioridad = leerPrioridad();
        LocalDate fecha = leerFechaOpcional("Nueva fecha límite (AAAA-MM-DD, vacío para sin fecha): ");

        servicio.editarElemento(id, titulo, descripcion, prioridad, fecha);
        System.out.println("Elemento actualizado correctamente.");
    }

    private void eliminarElemento() {
        String id = leerTexto("ID del elemento a eliminar: ");
        servicio.eliminarElemento(id);
        System.out.println("Elemento eliminado correctamente. Puede usar 'Deshacer' para restaurarlo.");
    }

    private void cambiarEstado() {
        String id = leerTexto("ID de la tarea o proyecto: ");
        System.out.println("Seleccione la transición:");
        System.out.println("1. Iniciar (Pendiente -> En curso / En revisión -> En curso)");
        System.out.println("2. Enviar a revisión (En curso -> En revisión)");
        System.out.println("3. Completar (En revisión -> Terminada)");
        System.out.println("4. Reabrir (-> Pendiente)");
        String op = leerTexto("Opción: ");

        AccionEstado accion;
        switch (op) {
            case "1":
                accion = AccionEstado.INICIAR;
                break;
            case "2":
                accion = AccionEstado.ENVIAR_A_REVISION;
                break;
            case "3":
                accion = AccionEstado.COMPLETAR;
                break;
            case "4":
                accion = AccionEstado.REABRIR;
                break;
            default:
                System.out.println("Opción de transición no válida.");
                return;
        }

        servicio.cambiarEstado(id, accion);
        TareaBase actualizado = servicio.buscarPorId(id);
        System.out.println("Nuevo estado de [" + id + "]: " + actualizado.getNombreEstado());
    }

    private void consultarYFiltrar() {
        System.out.println("--- Filtros disponibles ---");
        System.out.println("1. Filtrar por estado");
        System.out.println("2. Filtrar por prioridad");
        System.out.println("3. Buscar por texto");
        String op = leerTexto("Opción: ");

        List<TareaBase> resultados;
        switch (op) {
            case "1":
                String estado = leerTexto("Estado (Pendiente / En curso / En revisión / Terminada): ");
                resultados = servicio.filtrarPorEstado(estado);
                break;
            case "2":
                Prioridad prioridad = leerPrioridad();
                resultados = servicio.filtrarPorPrioridad(prioridad);
                break;
            case "3":
                String texto = leerTexto("Texto a buscar en ID, título o descripción: ");
                resultados = servicio.buscarPorTexto(texto);
                break;
            default:
                System.out.println("Opción de filtro no válida.");
                return;
        }

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron elementos con ese criterio.");
            return;
        }
        System.out.println("=== RESULTADOS (" + resultados.size() + ") ===");
        for (TareaBase item : resultados) {
            System.out.println(" - " + item + " | Alerta: " + item.evaluarAlerta(LocalDate.now()));
        }
    }

    private void mostrarAlertas() {
        LocalDate hoy = LocalDate.now();
        List<TareaBase> vencidas = servicio.obtenerTareasVencidas(hoy);
        List<TareaBase> proximas = servicio.obtenerTareasProximasAVencer(hoy);

        System.out.println("=== ALERTAS POR FECHA LÍMITE (" + hoy + ") ===");
        System.out.println("-- Vencidas (" + vencidas.size() + ") --");
        if (vencidas.isEmpty()) {
            System.out.println("   Ninguna.");
        } else {
            for (TareaBase item : vencidas) {
                System.out.println("   ! " + item);
            }
        }

        System.out.println("-- Próximas a vencer en <= " + TareaBase.DIAS_ALERTA_PROXIMA + " días (" + proximas.size() + ") --");
        if (proximas.isEmpty()) {
            System.out.println("   Ninguna.");
        } else {
            for (TareaBase item : proximas) {
                System.out.println("   * " + item);
            }
        }
    }

    private void deshacer() {
        if (servicio.deshacer()) {
            System.out.println("Última acción deshecha correctamente.");
        } else {
            System.out.println("No hay acciones disponibles para deshacer.");
        }
    }

    private void rehacer() {
        if (servicio.rehacer()) {
            System.out.println("Acción rehecha correctamente.");
        } else {
            System.out.println("No hay acciones disponibles para rehacer.");
        }
    }

    private void mostrarHistorial() {
        List<String> historial = servicio.obtenerHistorialAcciones();
        if (historial.isEmpty()) {
            System.out.println("El historial de comandos está vacío.");
            return;
        }
        System.out.println("=== HISTORIAL DE COMANDOS (más reciente primero) ===");
        for (int i = 0; i < historial.size(); i++) {
            System.out.println((i + 1) + ". " + historial.get(i));
        }
    }

    private Prioridad leerPrioridad() {
        while (true) {
            System.out.println("Prioridad: [1] Baja | [2] Media (por defecto) | [3] Alta");
            String op = leerTexto("Seleccione prioridad: ");
            if (op.isBlank() || "2".equals(op)) {
                return Prioridad.MEDIA;
            }
            if ("1".equals(op)) {
                return Prioridad.BAJA;
            }
            if ("3".equals(op)) {
                return Prioridad.ALTA;
            }
            System.out.println("Opción inválida. Ingrese 1, 2 o 3 (o presione Enter para Media).");
        }
    }

    private LocalDate leerFechaOpcional(String mensaje) {
        while (true) {
            String entrada = leerTexto(mensaje);
            if (entrada.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(entrada);
            } catch (DateTimeParseException ex) {
                System.out.println("Formato inválido. Use AAAA-MM-DD (ejemplo: 2026-12-15) o deje vacío.");
            }
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
}
