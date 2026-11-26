package com.gestortareas.service;

import com.gestortareas.model.PlantillaTarea;
import com.gestortareas.model.Prioridad;
import com.gestortareas.model.TareaBase;
import com.gestortareas.patterns.command.AccionEstado;
import com.gestortareas.patterns.command.ComandoAgregarElemento;
import com.gestortareas.patterns.command.ComandoCambiarEstado;
import com.gestortareas.patterns.command.ComandoEditarTarea;
import com.gestortareas.patterns.command.ComandoEliminarElemento;
import com.gestortareas.patterns.command.GestorComandos;
import com.gestortareas.patterns.composite.Proyecto;
import com.gestortareas.patterns.composite.TareaSimple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Núcleo de lógica de negocio compartido entre la aplicación de consola y la interfaz gráfica Swing.
 * Coordina el árbol de proyectos y tareas (Composite), los cambios de estado (State),
 * el historial reversible (Command), las consultas y las plantillas recurrentes.
 */
public class GestorTareasService {

    private final List<TareaBase> elementosRaiz;
    private final List<PlantillaTarea> plantillas;
    private final GestorComandos gestorComandos;
    private final ConsultaTareasService consultaService;
    private int contadorProyectos;
    private int contadorTareas;
    private boolean datosEjemploCargados;

    public GestorTareasService() {
        this.elementosRaiz = new ArrayList<>();
        this.plantillas = new ArrayList<>();
        this.gestorComandos = new GestorComandos();
        this.consultaService = new ConsultaTareasService();
        this.contadorProyectos = 0;
        this.contadorTareas = 0;
        this.datosEjemploCargados = false;
        inicializarPlantillasPorDefecto();
    }

    private void inicializarPlantillasPorDefecto() {
        plantillas.add(new PlantillaTarea(
                "Reunión semanal de seguimiento",
                "Reunión semanal del equipo",
                "Revisar avances, bloqueos y acuerdos de la semana.",
                Prioridad.MEDIA,
                2,
                7
        ));
        plantillas.add(new PlantillaTarea(
                "Entrega académica periódica",
                "Preparar entrega semanal",
                "Consolidar código, pruebas unitarias y documento de avance.",
                Prioridad.ALTA,
                5,
                14
        ));
        plantillas.add(new PlantillaTarea(
                "Revisión de código en GitHub",
                "Revisar pull requests del repositorio",
                "Validar estilo, compilación y pruebas antes de integrar a main.",
                Prioridad.BAJA,
                1,
                7
        ));
    }

    private String generarIdProyecto() {
        contadorProyectos++;
        return "P-" + contadorProyectos;
    }

    private String generarIdTarea() {
        contadorTareas++;
        return "T-" + contadorTareas;
    }

    // --- Creación y gestión mediante comandos reversibles ---

    public Proyecto crearProyecto(String titulo,
                                  String descripcion,
                                  Prioridad prioridad,
                                  LocalDate fechaLimite,
                                  String idProyectoPadre) {
        Proyecto padre = resolverProyectoPadre(idProyectoPadre);
        Proyecto nuevo = new Proyecto(generarIdProyecto(), titulo, descripcion, prioridad, fechaLimite);
        gestorComandos.ejecutarComando(new ComandoAgregarElemento(this, padre, nuevo));
        return nuevo;
    }

    public TareaSimple crearTareaSimple(String titulo,
                                        String descripcion,
                                        Prioridad prioridad,
                                        LocalDate fechaLimite,
                                        String idProyectoPadre) {
        Proyecto padre = resolverProyectoPadre(idProyectoPadre);
        TareaSimple nueva = new TareaSimple(generarIdTarea(), titulo, descripcion, prioridad, fechaLimite);
        gestorComandos.ejecutarComando(new ComandoAgregarElemento(this, padre, nueva));
        return nueva;
    }

    public TareaSimple crearTareaDesdePlantilla(int indicePlantilla, String idProyectoPadre, LocalDate fechaBase) {
        if (indicePlantilla < 0 || indicePlantilla >= plantillas.size()) {
            throw new IllegalArgumentException("El índice de plantilla seleccionado no es válido.");
        }
        Proyecto padre = resolverProyectoPadre(idProyectoPadre);
        PlantillaTarea plantilla = plantillas.get(indicePlantilla);
        TareaSimple nueva = plantilla.crearTarea(generarIdTarea(), fechaBase);
        gestorComandos.ejecutarComando(new ComandoAgregarElemento(this, padre, nueva));
        return nueva;
    }

    public void registrarPlantilla(PlantillaTarea plantilla) {
        if (plantilla == null) {
            throw new IllegalArgumentException("La plantilla no puede ser nula.");
        }
        plantillas.add(plantilla);
    }

    public List<PlantillaTarea> obtenerPlantillas() {
        return Collections.unmodifiableList(plantillas);
    }

    public void editarElemento(String id,
                               String nuevoTitulo,
                               String nuevaDescripcion,
                               Prioridad nuevaPrioridad,
                               LocalDate nuevaFechaLimite) {
        TareaBase elemento = buscarPorIdObligatorio(id);
        gestorComandos.ejecutarComando(
                new ComandoEditarTarea(elemento, nuevoTitulo, nuevaDescripcion, nuevaPrioridad, nuevaFechaLimite)
        );
    }

    public void eliminarElemento(String id) {
        TareaBase elemento = buscarPorIdObligatorio(id);
        Proyecto padre = buscarPadreDe(elemento);
        gestorComandos.ejecutarComando(new ComandoEliminarElemento(this, padre, elemento));
    }

    public void cambiarEstado(String id, AccionEstado accion) {
        if (accion == null) {
            throw new IllegalArgumentException("La acción de estado no puede ser nula.");
        }
        TareaBase elemento = buscarPorIdObligatorio(id);
        gestorComandos.ejecutarComando(new ComandoCambiarEstado(elemento, accion));
    }

    public void iniciarTarea(String id) {
        cambiarEstado(id, AccionEstado.INICIAR);
    }

    public void enviarTareaARevision(String id) {
        cambiarEstado(id, AccionEstado.ENVIAR_A_REVISION);
    }

    public void completarTarea(String id) {
        cambiarEstado(id, AccionEstado.COMPLETAR);
    }

    public void reabrirTarea(String id) {
        cambiarEstado(id, AccionEstado.REABRIR);
    }

    // --- Operaciones de deshacer / rehacer ---

    public boolean deshacer() {
        return gestorComandos.deshacer();
    }

    public boolean rehacer() {
        return gestorComandos.rehacer();
    }

    public boolean puedeDeshacer() {
        return gestorComandos.puedeDeshacer();
    }

    public boolean puedeRehacer() {
        return gestorComandos.puedeRehacer();
    }

    public List<String> obtenerHistorialAcciones() {
        return gestorComandos.obtenerHistorialDeshacer();
    }

    // --- Métodos internos invocados por los comandos ---

    public void agregarElementoInterno(Proyecto proyectoPadre, TareaBase elemento) {
        if (proyectoPadre == null) {
            if (!elementosRaiz.contains(elemento)) {
                elementosRaiz.add(elemento);
            }
        } else {
            proyectoPadre.agregarElemento(elemento);
        }
    }

    public void eliminarElementoInterno(Proyecto proyectoPadre, TareaBase elemento) {
        if (proyectoPadre == null) {
            elementosRaiz.remove(elemento);
        } else {
            proyectoPadre.eliminarElemento(elemento);
        }
    }

    public int obtenerIndiceEnContenedor(Proyecto proyectoPadre, TareaBase elemento) {
        if (proyectoPadre == null) {
            return elementosRaiz.indexOf(elemento);
        }
        return proyectoPadre.obtenerIndiceElemento(elemento);
    }

    public void restaurarElementoInterno(Proyecto proyectoPadre, int indice, TareaBase elemento) {
        if (proyectoPadre == null) {
            if (!elementosRaiz.contains(elemento)) {
                if (indice < 0 || indice >= elementosRaiz.size()) {
                    elementosRaiz.add(elemento);
                } else {
                    elementosRaiz.add(indice, elemento);
                }
            }
        } else {
            proyectoPadre.insertarElementoEn(indice, elemento);
        }
    }

    // --- Búsqueda y recorrido de la estructura Composite ---

    public List<TareaBase> obtenerElementosRaiz() {
        return Collections.unmodifiableList(elementosRaiz);
    }

    public List<TareaBase> obtenerTodosLosElementosAplanados() {
        List<TareaBase> todos = new ArrayList<>();
        for (TareaBase raiz : elementosRaiz) {
            if (raiz instanceof Proyecto) {
                todos.addAll(((Proyecto) raiz).aplanarElementos());
            } else {
                todos.add(raiz);
            }
        }
        return todos;
    }

    public List<Proyecto> obtenerTodosLosProyectos() {
        List<Proyecto> proyectos = new ArrayList<>();
        for (TareaBase elemento : obtenerTodosLosElementosAplanados()) {
            if (elemento instanceof Proyecto) {
                proyectos.add((Proyecto) elemento);
            }
        }
        return proyectos;
    }

    public TareaBase buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        String buscado = id.trim();
        for (TareaBase raiz : elementosRaiz) {
            if (raiz.getId().equalsIgnoreCase(buscado)) {
                return raiz;
            }
            if (raiz instanceof Proyecto) {
                TareaBase encontrado = ((Proyecto) raiz).buscarPorId(buscado);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    private TareaBase buscarPorIdObligatorio(String id) {
        TareaBase encontrado = buscarPorId(id);
        if (encontrado == null) {
            throw new IllegalArgumentException("No existe ninguna tarea o proyecto con el ID: " + id);
        }
        return encontrado;
    }

    private Proyecto resolverProyectoPadre(String idProyectoPadre) {
        if (idProyectoPadre == null || idProyectoPadre.isBlank()) {
            return null;
        }
        TareaBase encontrado = buscarPorIdObligatorio(idProyectoPadre);
        if (!(encontrado instanceof Proyecto)) {
            throw new IllegalArgumentException("El elemento [" + idProyectoPadre + "] es una tarea simple y no puede contener subtareas.");
        }
        return (Proyecto) encontrado;
    }

    public Proyecto buscarPadreDe(TareaBase objetivo) {
        for (TareaBase raiz : elementosRaiz) {
            if (raiz == objetivo) {
                return null;
            }
            if (raiz instanceof Proyecto) {
                Proyecto encontrado = buscarPadreRecursivo((Proyecto) raiz, objetivo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    private Proyecto buscarPadreRecursivo(Proyecto actual, TareaBase objetivo) {
        for (TareaBase hijo : actual.obtenerSubtareas()) {
            if (hijo == objetivo) {
                return actual;
            }
            if (hijo instanceof Proyecto) {
                Proyecto encontrado = buscarPadreRecursivo((Proyecto) hijo, objetivo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    // --- Consultas, filtros y alertas ---

    public List<TareaBase> filtrarPorEstado(String nombreEstado) {
        return consultaService.filtrarPorEstado(obtenerTodosLosElementosAplanados(), nombreEstado);
    }

    public List<TareaBase> filtrarPorPrioridad(Prioridad prioridad) {
        return consultaService.filtrarPorPrioridad(obtenerTodosLosElementosAplanados(), prioridad);
    }

    public List<TareaBase> buscarPorTexto(String texto) {
        return consultaService.buscarPorTexto(obtenerTodosLosElementosAplanados(), texto);
    }

    public List<TareaBase> filtrarCombinado(String nombreEstado, Prioridad prioridad, String texto) {
        List<TareaBase> resultado = obtenerTodosLosElementosAplanados();
        resultado = consultaService.filtrarPorEstado(resultado, nombreEstado);
        resultado = consultaService.filtrarPorPrioridad(resultado, prioridad);
        resultado = consultaService.buscarPorTexto(resultado, texto);
        return resultado;
    }

    public List<TareaBase> obtenerTareasVencidas(LocalDate fechaReferencia) {
        return consultaService.obtenerTareasVencidas(obtenerTodosLosElementosAplanados(), fechaReferencia);
    }

    public List<TareaBase> obtenerTareasProximasAVencer(LocalDate fechaReferencia) {
        return consultaService.obtenerTareasProximasAVencer(obtenerTodosLosElementosAplanados(), fechaReferencia);
    }

    /**
     * Carga un conjunto realista de proyectos y tareas de demostración una sola vez,
     * sin alterar el historial de comandos previo del usuario.
     *
     * @return true si se cargaron los datos; false si ya habían sido cargados.
     */
    public boolean cargarDatosEjemplo() {
        if (datosEjemploCargados) {
            return false;
        }
        LocalDate hoy = LocalDate.now();
        Proyecto p1 = new Proyecto(
                generarIdProyecto(),
                "Proyecto Final de Ingeniería",
                "Entrega del Gestor de Tareas y Proyectos en Java.",
                Prioridad.ALTA,
                hoy.plusDays(10)
        );
        TareaSimple t1 = new TareaSimple(
                generarIdTarea(),
                "Implementar patrón Composite",
                "Crear TareaBase, TareaSimple y Proyecto con cálculos recursivos.",
                Prioridad.ALTA,
                hoy.minusDays(1)
        );
        TareaSimple t2 = new TareaSimple(
                generarIdTarea(),
                "Implementar patrón State",
                "Definir transiciones válidas entre Pendiente, En curso, En revisión y Terminada.",
                Prioridad.ALTA,
                hoy.plusDays(2)
        );
        Proyecto subModulo = new Proyecto(
                generarIdProyecto(),
                "Módulo de Interfaces",
                "Capa de presentación en Consola y Swing.",
                Prioridad.MEDIA,
                hoy.plusDays(7)
        );
        TareaSimple t3 = new TareaSimple(
                generarIdTarea(),
                "Construir menú de consola",
                "Menú interactivo conectado a GestorTareasService.",
                Prioridad.MEDIA,
                hoy.plusDays(5)
        );
        TareaSimple t4 = new TareaSimple(
                generarIdTarea(),
                "Construir ventana Swing",
                "Interfaz gráfica sencilla con árbol y tabla.",
                Prioridad.MEDIA,
                hoy.plusDays(6)
        );

        t1.iniciar();
        t1.enviarARevision();
        t1.completar();
        t2.iniciar();

        subModulo.agregarElemento(t3);
        subModulo.agregarElemento(t4);
        p1.agregarElemento(t1);
        p1.agregarElemento(t2);
        p1.agregarElemento(subModulo);
        agregarElementoInterno(null, p1);

        datosEjemploCargados = true;
        return true;
    }
}
