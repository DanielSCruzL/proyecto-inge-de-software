package com.gestortareas.patterns.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// Maneja las pilas de deshacer y rehacer para los comandos ejecutados
public class GestorComandos {

    private final Deque<Comando> pilaDeshacer;
    private final Deque<Comando> pilaRehacer;

    public GestorComandos() {
        this.pilaDeshacer = new ArrayDeque<>();
        this.pilaRehacer = new ArrayDeque<>();
    }

    public void ejecutarComando(Comando comando) {
        if (comando == null) {
            throw new IllegalArgumentException("El comando no puede ser nulo.");
        }
        comando.ejecutar();
        pilaDeshacer.push(comando);
        pilaRehacer.clear();
    }

    public boolean deshacer() {
        if (pilaDeshacer.isEmpty()) {
            return false;
        }
        Comando comando = pilaDeshacer.pop();
        comando.deshacer();
        pilaRehacer.push(comando);
        return true;
    }

    public boolean rehacer() {
        if (pilaRehacer.isEmpty()) {
            return false;
        }
        Comando comando = pilaRehacer.pop();
        comando.ejecutar();
        pilaDeshacer.push(comando);
        return true;
    }

    public boolean puedeDeshacer() {
        return !pilaDeshacer.isEmpty();
    }

    public boolean puedeRehacer() {
        return !pilaRehacer.isEmpty();
    }

    public List<String> obtenerHistorialDeshacer() {
        List<String> historial = new ArrayList<>();
        for (Comando comando : pilaDeshacer) {
            historial.add(comando.getDescripcion());
        }
        return historial;
    }

    public void limpiarHistorial() {
        pilaDeshacer.clear();
        pilaRehacer.clear();
    }
}
