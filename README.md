# Gestor de Tareas y Proyectos (Task & Project Manager)
> **Parcial de Patrones de Diseño — Ingeniería de Software**  
> Lenguaje: **Java** | Arquitectura: **Núcleo Compartido + Consola (CLI) + Interfaz Gráfica (GUI Swing)**

---

## 📌 Propuesta Seleccionada: Propuesta 2
Aplicación para la organización y seguimiento del trabajo personal o de un equipo.

### Características Principales:
1. **Tareas simples y tareas compuestas por subtareas** (Estructura jerárquica con cálculo acumulativo de avance).
2. **Ciclo de vida y estados de una tarea con transiciones controladas** (*Pendiente*, *En Curso*, *En Revisión*, *Terminada*).
3. **Mecanismo de Deshacer y Rehacer (Undo / Redo)** sobre las acciones del usuario.
4. **Recordatorios y alertas por fecha límite** hacia uno o varios responsables asignados.
5. **Plantillas reutilizables** para la creación de tareas o proyectos recurrentes.
6. **Múltiples formas de ordenar y filtrar** (por prioridad, fecha límite o responsable).

---

## 📄 Documentos de Entrega

- [Punto 1 y 2: Alcance y Requisitos del Sistema](file:///home/antigravity/DanielSCruzL/proyecto-inge-de-software/docs/01_Alcance_y_Requisitos.md)
  - **1. Alcance:** Funcionalidades dentro del alcance, funcionalidades fuera del alcance (con justificaciones técnicas) y las **4 áreas identificadas con variación o cambio frecuente**.
  - **2. Requisitos:** Lista de **Requisitos Funcionales (RF-01 a RF-15)** con actores y criterios de aceptación, y **Requisitos No Funcionales (RNF-01 a RNF-10)** estructurados bajo el estándar **ISO/IEC 25010**.

---

## 🏗️ Estructura del Proyecto (Planificación)
```
proyecto-inge-de-software/
│
├── docs/
│   └── 01_Alcance_y_Requisitos.md   # Entrega formal de Puntos 1 y 2
│
├── src/                             # Código fuente en Java (Próxima fase)
│   ├── main/java/
│   │   ├── core/                    # Lógica de negocio y patrones GoF
│   │   ├── service/                 # Servicios de aplicación y persistencia
│   │   └── ui/                      # Presentación: CLI y GUI Swing
│   └── test/java/                   # Pruebas unitarias con JUnit 5
│
└── README.md                        # Presentación del repositorio
```
