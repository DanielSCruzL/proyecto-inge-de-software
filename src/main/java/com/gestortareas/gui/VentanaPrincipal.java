package com.gestortareas.gui;

import com.gestortareas.model.PlantillaTarea;
import com.gestortareas.model.Prioridad;
import com.gestortareas.model.TareaBase;
import com.gestortareas.patterns.command.AccionEstado;
import com.gestortareas.patterns.composite.Proyecto;
import com.gestortareas.service.GestorTareasService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Interfaz gráfica sencilla en Java Swing.
 * Comparte el mismo núcleo de negocio (GestorTareasService) que la aplicación de consola.
 */
public class VentanaPrincipal extends JFrame {

    private final GestorTareasService servicio;

    private JTree arbolProyectos;
    private DefaultTreeModel modeloArbol;
    private JTable tablaTareas;
    private DefaultTableModel modeloTabla;

    private JComboBox<String> comboFiltroEstado;
    private JComboBox<String> comboFiltroPrioridad;
    private JTextField campoBusqueda;

    private JButton btnDeshacer;
    private JButton btnRehacer;
    private JLabel lblAlertas;

    public VentanaPrincipal(GestorTareasService servicio) {
        this.servicio = servicio;
        configurarVentana();
        construirInterfaz();
        refrescarVista();
    }

    private void configurarVentana() {
        setTitle("Gestor de Tareas y Proyectos");
        setSize(1020, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
    }

    private void construirInterfaz() {
        // --- Panel superior: acciones CRUD + filtros ---
        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 4, 4));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(6, 8, 2, 8));

        JPanel barraAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JButton btnNuevoProyecto = new JButton("Nuevo Proyecto");
        JButton btnNuevaTarea = new JButton("Nueva Tarea");
        JButton btnPlantilla = new JButton("Desde Plantilla");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        btnDeshacer = new JButton("Deshacer");
        btnRehacer = new JButton("Rehacer");
        JButton btnHistorial = new JButton("Historial");
        JButton btnEjemplo = new JButton("Cargar Ejemplo");

        btnNuevoProyecto.addActionListener(e -> abrirDialogoCrear(true));
        btnNuevaTarea.addActionListener(e -> abrirDialogoCrear(false));
        btnPlantilla.addActionListener(e -> abrirDialogoPlantilla());
        btnEditar.addActionListener(e -> abrirDialogoEditar());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnDeshacer.addActionListener(e -> {
            servicio.deshacer();
            refrescarVista();
        });
        btnRehacer.addActionListener(e -> {
            servicio.rehacer();
            refrescarVista();
        });
        btnHistorial.addActionListener(e -> mostrarHistorial());
        btnEjemplo.addActionListener(e -> {
            if (servicio.cargarDatosEjemplo()) {
                refrescarVista();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Los datos de ejemplo ya fueron cargados anteriormente.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        barraAcciones.add(btnNuevoProyecto);
        barraAcciones.add(btnNuevaTarea);
        barraAcciones.add(btnPlantilla);
        barraAcciones.add(btnEditar);
        barraAcciones.add(btnEliminar);
        barraAcciones.add(btnDeshacer);
        barraAcciones.add(btnRehacer);
        barraAcciones.add(btnHistorial);
        barraAcciones.add(btnEjemplo);

        JPanel barraFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        comboFiltroEstado = new JComboBox<>(new String[]{"Todos", "Pendiente", "En curso", "En revisión", "Terminada"});
        comboFiltroPrioridad = new JComboBox<>(new String[]{"Todas", "BAJA", "MEDIA", "ALTA"});
        campoBusqueda = new JTextField(16);
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnLimpiarFiltros = new JButton("Ver Todos");

        btnFiltrar.addActionListener(e -> refrescarTabla());
        btnLimpiarFiltros.addActionListener(e -> {
            comboFiltroEstado.setSelectedIndex(0);
            comboFiltroPrioridad.setSelectedIndex(0);
            campoBusqueda.setText("");
            refrescarTabla();
        });

        barraFiltros.add(new JLabel("Estado:"));
        barraFiltros.add(comboFiltroEstado);
        barraFiltros.add(new JLabel("Prioridad:"));
        barraFiltros.add(comboFiltroPrioridad);
        barraFiltros.add(new JLabel("Buscar:"));
        barraFiltros.add(campoBusqueda);
        barraFiltros.add(btnFiltrar);
        barraFiltros.add(btnLimpiarFiltros);

        panelSuperior.add(barraAcciones);
        panelSuperior.add(barraFiltros);
        add(panelSuperior, BorderLayout.NORTH);

        // --- Panel central: Árbol Composite (izquierda) + Tabla detallada (derecha) ---
        DefaultMutableTreeNode raizVisual = new DefaultMutableTreeNode("Espacio de Trabajo");
        modeloArbol = new DefaultTreeModel(raizVisual);
        arbolProyectos = new JTree(modeloArbol);
        arbolProyectos.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        arbolProyectos.addTreeSelectionListener(e -> sincronizarSeleccionDesdeArbol());

        JScrollPane scrollArbol = new JScrollPane(arbolProyectos);
        scrollArbol.setBorder(BorderFactory.createTitledBorder("Jerarquía de Proyectos y Tareas (Composite)"));

        String[] columnas = {"ID", "Tipo", "Título", "Estado", "Prioridad", "Fecha Límite", "Alerta", "Avance"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaTareas = new JTable(modeloTabla);
        tablaTareas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabla = new JScrollPane(tablaTareas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Listado y Consultas"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollArbol, scrollTabla);
        splitPane.setDividerLocation(320);
        add(splitPane, BorderLayout.CENTER);

        // --- Panel inferior: transiciones de estado + resumen de alertas ---
        JPanel panelInferior = new JPanel(new BorderLayout(8, 4));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        JPanel barraEstados = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JButton btnIniciar = new JButton("Iniciar");
        JButton btnRevision = new JButton("Enviar a Revisión");
        JButton btnCompletar = new JButton("Completar");
        JButton btnReabrir = new JButton("Reabrir");

        btnIniciar.addActionListener(e -> aplicarCambioEstado(AccionEstado.INICIAR));
        btnRevision.addActionListener(e -> aplicarCambioEstado(AccionEstado.ENVIAR_A_REVISION));
        btnCompletar.addActionListener(e -> aplicarCambioEstado(AccionEstado.COMPLETAR));
        btnReabrir.addActionListener(e -> aplicarCambioEstado(AccionEstado.REABRIR));

        barraEstados.add(new JLabel("Cambiar estado del elemento seleccionado:"));
        barraEstados.add(btnIniciar);
        barraEstados.add(btnRevision);
        barraEstados.add(btnCompletar);
        barraEstados.add(btnReabrir);

        lblAlertas = new JLabel("Alertas: 0 vencidas | 0 próximas a vencer");
        panelInferior.add(barraEstados, BorderLayout.WEST);
        panelInferior.add(lblAlertas, BorderLayout.EAST);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private void sincronizarSeleccionDesdeArbol() {
        Object nodoObj = arbolProyectos.getLastSelectedPathComponent();
        if (!(nodoObj instanceof DefaultMutableTreeNode)) {
            return;
        }
        String textoNodo = String.valueOf(((DefaultMutableTreeNode) nodoObj).getUserObject());
        String idNodo = extraerIdDeEtiqueta(textoNodo);
        if (idNodo == null) {
            return;
        }
        for (int fila = 0; fila < modeloTabla.getRowCount(); fila++) {
            String idFila = String.valueOf(modeloTabla.getValueAt(fila, 0));
            if (idNodo.equalsIgnoreCase(idFila)) {
                tablaTareas.setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }

    private String extraerIdDeEtiqueta(String texto) {
        if (texto == null || !texto.startsWith("[")) {
            return null;
        }
        int cierre = texto.indexOf(']');
        if (cierre <= 1) {
            return null;
        }
        return texto.substring(1, cierre).trim();
    }

    private void abrirDialogoCrear(boolean esProyecto) {
        JTextField txtTitulo = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JComboBox<Prioridad> cmbPrioridad = new JComboBox<>(Prioridad.values());
        JTextField txtFecha = new JTextField();

        List<Proyecto> proyectos = servicio.obtenerTodosLosProyectos();
        String[] opcionesPadre = new String[proyectos.size() + 1];
        opcionesPadre[0] = "(Nivel raíz)";
        for (int i = 0; i < proyectos.size(); i++) {
            Proyecto p = proyectos.get(i);
            opcionesPadre[i + 1] = p.getId() + " - " + p.getTitulo();
        }
        JComboBox<String> cmbPadre = new JComboBox<>(opcionesPadre);

        JPanel formulario = new JPanel(new GridLayout(5, 2, 6, 6));
        formulario.add(new JLabel("Título:"));
        formulario.add(txtTitulo);
        formulario.add(new JLabel("Descripción:"));
        formulario.add(txtDescripcion);
        formulario.add(new JLabel("Prioridad:"));
        formulario.add(cmbPrioridad);
        formulario.add(new JLabel("Fecha límite (AAAA-MM-DD):"));
        formulario.add(txtFecha);
        formulario.add(new JLabel("Proyecto padre:"));
        formulario.add(cmbPadre);

        String tituloDialogo = esProyecto ? "Nuevo Proyecto" : "Nueva Tarea Simple";
        int opcion = JOptionPane.showConfirmDialog(this, formulario, tituloDialogo, JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            LocalDate fecha = parsearFecha(txtFecha.getText().trim());
            String idPadre = null;
            int idxPadre = cmbPadre.getSelectedIndex();
            if (idxPadre > 0) {
                idPadre = proyectos.get(idxPadre - 1).getId();
            }

            if (esProyecto) {
                servicio.crearProyecto(
                        txtTitulo.getText(),
                        txtDescripcion.getText(),
                        (Prioridad) cmbPrioridad.getSelectedItem(),
                        fecha,
                        idPadre
                );
            } else {
                servicio.crearTareaSimple(
                        txtTitulo.getText(),
                        txtDescripcion.getText(),
                        (Prioridad) cmbPrioridad.getSelectedItem(),
                        fecha,
                        idPadre
                );
            }
            refrescarVista();
        } catch (DateTimeParseException ex) {
            mostrarAviso("Formato de fecha inválido. Use AAAA-MM-DD (ejemplo: 2026-12-15) o deje el campo vacío.");
        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        }
    }

    private void abrirDialogoPlantilla() {
        List<PlantillaTarea> plantillas = servicio.obtenerPlantillas();
        if (plantillas.isEmpty()) {
            mostrarAviso("No hay plantillas recurrentes registradas.");
            return;
        }
        String[] nombres = new String[plantillas.size()];
        for (int i = 0; i < plantillas.size(); i++) {
            nombres[i] = plantillas.get(i).toString();
        }
        JComboBox<String> cmbPlantilla = new JComboBox<>(nombres);

        List<Proyecto> proyectos = servicio.obtenerTodosLosProyectos();
        String[] opcionesPadre = new String[proyectos.size() + 1];
        opcionesPadre[0] = "(Nivel raíz)";
        for (int i = 0; i < proyectos.size(); i++) {
            Proyecto p = proyectos.get(i);
            opcionesPadre[i + 1] = p.getId() + " - " + p.getTitulo();
        }
        JComboBox<String> cmbPadre = new JComboBox<>(opcionesPadre);

        JPanel panel = new JPanel(new GridLayout(2, 2, 6, 6));
        panel.add(new JLabel("Plantilla:"));
        panel.add(cmbPlantilla);
        panel.add(new JLabel("Proyecto destino:"));
        panel.add(cmbPadre);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Crear desde Plantilla", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String idPadre = null;
                int idxPadre = cmbPadre.getSelectedIndex();
                if (idxPadre > 0) {
                    idPadre = proyectos.get(idxPadre - 1).getId();
                }
                servicio.crearTareaDesdePlantilla(cmbPlantilla.getSelectedIndex(), idPadre, LocalDate.now());
                refrescarVista();
            } catch (IllegalArgumentException ex) {
                mostrarAviso(ex.getMessage());
            }
        }
    }

    private void abrirDialogoEditar() {
        String id = obtenerIdSeleccionado();
        if (id == null) {
            mostrarAviso("Seleccione un elemento en la tabla o en el árbol para editar.");
            return;
        }
        TareaBase actual = servicio.buscarPorId(id);
        if (actual == null) {
            return;
        }

        JTextField txtTitulo = new JTextField(actual.getTitulo());
        JTextField txtDescripcion = new JTextField(actual.getDescripcion());
        JComboBox<Prioridad> cmbPrioridad = new JComboBox<>(Prioridad.values());
        cmbPrioridad.setSelectedItem(actual.getPrioridad());
        JTextField txtFecha = new JTextField(actual.getFechaLimite() == null ? "" : actual.getFechaLimite().toString());

        JPanel formulario = new JPanel(new GridLayout(4, 2, 6, 6));
        formulario.add(new JLabel("Título:"));
        formulario.add(txtTitulo);
        formulario.add(new JLabel("Descripción:"));
        formulario.add(txtDescripcion);
        formulario.add(new JLabel("Prioridad:"));
        formulario.add(cmbPrioridad);
        formulario.add(new JLabel("Fecha límite (AAAA-MM-DD):"));
        formulario.add(txtFecha);

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Editar [" + id + "]", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                LocalDate fecha = parsearFecha(txtFecha.getText().trim());
                servicio.editarElemento(
                        id,
                        txtTitulo.getText(),
                        txtDescripcion.getText(),
                        (Prioridad) cmbPrioridad.getSelectedItem(),
                        fecha
                );
                refrescarVista();
            } catch (DateTimeParseException ex) {
                mostrarAviso("Formato de fecha inválido. Use AAAA-MM-DD (ejemplo: 2026-12-15) o deje el campo vacío.");
            } catch (IllegalArgumentException ex) {
                mostrarAviso(ex.getMessage());
            }
        }
    }

    private void eliminarSeleccionado() {
        String id = obtenerIdSeleccionado();
        if (id == null) {
            mostrarAviso("Seleccione un elemento en la tabla o en el árbol para eliminar.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el elemento [" + id + "]? (Puede revertirlo con Deshacer)",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                servicio.eliminarElemento(id);
                refrescarVista();
            } catch (IllegalArgumentException ex) {
                mostrarAviso(ex.getMessage());
            }
        }
    }

    private void aplicarCambioEstado(AccionEstado accion) {
        String id = obtenerIdSeleccionado();
        if (id == null) {
            mostrarAviso("Seleccione una tarea o proyecto en la tabla o en el árbol para cambiar su estado.");
            return;
        }
        try {
            servicio.cambiarEstado(id, accion);
            refrescarVista();
        } catch (IllegalStateException | IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        }
    }

    private void mostrarHistorial() {
        List<String> historial = servicio.obtenerHistorialAcciones();
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay acciones registradas en el historial.", "Historial", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < historial.size(); i++) {
            sb.append(i + 1).append(". ").append(historial.get(i)).append(System.lineSeparator());
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Historial de Comandos (Undo)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refrescarVista() {
        refrescarArbol();
        refrescarTabla();
        actualizarControles();
    }

    private void refrescarArbol() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Espacio de Trabajo");
        for (TareaBase elemento : servicio.obtenerElementosRaiz()) {
            raiz.add(construirNodoArbol(elemento));
        }
        modeloArbol.setRoot(raiz);
        for (int i = 0; i < arbolProyectos.getRowCount(); i++) {
            arbolProyectos.expandRow(i);
        }
    }

    private DefaultMutableTreeNode construirNodoArbol(TareaBase elemento) {
        String etiqueta = "[" + elemento.getId() + "] " + elemento.getTitulo() + " (" + elemento.getNombreEstado() + ")";
        if (elemento.esCompuesta()) {
            etiqueta += " - " + String.format("%.0f%%", elemento.calcularPorcentajeAvance());
        }
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(etiqueta);
        for (TareaBase hijo : elemento.obtenerSubtareas()) {
            nodo.add(construirNodoArbol(hijo));
        }
        return nodo;
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        String estadoSeleccionado = (String) comboFiltroEstado.getSelectedItem();
        String prioridadTexto = (String) comboFiltroPrioridad.getSelectedItem();
        Prioridad prioridad = ("Todas".equals(prioridadTexto)) ? null : Prioridad.valueOf(prioridadTexto);
        String texto = campoBusqueda.getText();

        LocalDate hoy = LocalDate.now();
        List<TareaBase> elementos = servicio.filtrarCombinado(estadoSeleccionado, prioridad, texto);
        for (TareaBase item : elementos) {
            String tipo = item.esCompuesta() ? "Proyecto" : "Tarea";
            String fecha = (item.getFechaLimite() == null) ? "Sin fecha" : item.getFechaLimite().toString();
            String avance = String.format("%.0f%%", item.calcularPorcentajeAvance());
            modeloTabla.addRow(new Object[]{
                    item.getId(),
                    tipo,
                    item.getTitulo(),
                    item.getNombreEstado(),
                    item.getPrioridad().getEtiqueta(),
                    fecha,
                    item.evaluarAlerta(hoy).getEtiqueta(),
                    avance
            });
        }
    }

    private void actualizarControles() {
        btnDeshacer.setEnabled(servicio.puedeDeshacer());
        btnRehacer.setEnabled(servicio.puedeRehacer());
        LocalDate hoy = LocalDate.now();
        int vencidas = servicio.obtenerTareasVencidas(hoy).size();
        int proximas = servicio.obtenerTareasProximasAVencer(hoy).size();
        lblAlertas.setText("Alertas: " + vencidas + " vencidas | " + proximas + " próximas a vencer");
    }

    private String obtenerIdSeleccionado() {
        int fila = tablaTareas.getSelectedRow();
        if (fila >= 0) {
            return String.valueOf(modeloTabla.getValueAt(fila, 0));
        }
        Object nodoObj = arbolProyectos.getLastSelectedPathComponent();
        if (nodoObj instanceof DefaultMutableTreeNode) {
            String textoNodo = String.valueOf(((DefaultMutableTreeNode) nodoObj).getUserObject());
            return extraerIdDeEtiqueta(textoNodo);
        }
        return null;
    }

    private LocalDate parsearFecha(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return LocalDate.parse(texto);
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
