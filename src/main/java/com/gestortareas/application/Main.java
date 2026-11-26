package com.gestortareas.application;

import com.gestortareas.console.ConsolaApp;
import com.gestortareas.gui.VentanaPrincipal;
import com.gestortareas.service.GestorTareasService;

import javax.swing.SwingUtilities;
import java.util.Scanner;

/**
 * Punto de entrada unificado de la aplicación.
 * Permite ejecutar la interfaz por consola (--console) o la interfaz gráfica Swing (--gui),
 * compartiendo exactamente el mismo núcleo de lógica (GestorTareasService).
 */
public class Main {

    public static void main(String[] args) {
        GestorTareasService servicio = new GestorTareasService();

        if (args.length > 0) {
            if ("--console".equalsIgnoreCase(args[0])) {
                new ConsolaApp(servicio).iniciar();
                return;
            }
            if ("--gui".equalsIgnoreCase(args[0])) {
                lanzarGui(servicio);
                return;
            }
        }

        System.out.println("===========================================");
        System.out.println("   GESTOR DE TAREAS Y PROYECTOS - JAVA");
        System.out.println("===========================================");
        System.out.println("1. Iniciar en modo Consola");
        System.out.println("2. Iniciar en modo Interfaz Gráfica (Swing)");
        System.out.print("Seleccione una opción [1/2]: ");

        Scanner scanner = new Scanner(System.in);
        String opcion = scanner.nextLine().trim();
        if ("2".equals(opcion)) {
            lanzarGui(servicio);
        } else {
            new ConsolaApp(servicio, scanner).iniciar();
        }
    }

    private static void lanzarGui(GestorTareasService servicio) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(servicio);
            ventana.setVisible(true);
        });
    }
}
