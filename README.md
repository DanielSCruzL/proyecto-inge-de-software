# Gestor de Tareas y Proyectos (Java)

Aplicación universitaria en Java 17 para gestionar proyectos y tareas de forma jerárquica, con un único núcleo de lógica compartido entre **consola** e **interfaz gráfica Swing**, aplicando los patrones **Composite**, **State** y **Command**.

## Integrantes

- **Juan David Díaz Ramírez**
- **Helen Sofía Malagón Cruz**
- **Daniel Santiago Cruz López**

---

## Requisitos del entorno

- **Java Development Kit (JDK):** 17 o superior
- **Apache Maven:** 3.8 o superior
- **Microsoft Word de escritorio:** requerido solo para ejecutar las macros VBA o editar las figuras de los diagramas UML

Para verificar Java y Maven en tu equipo:

```bash
java -version
javac -version
mvn -version
```

---

## Funcionalidades implementadas

Todas las funcionalidades operan sobre el mismo núcleo (`GestorTareasService` y `ConsultaTareasService`) y están disponibles tanto en la versión por consola (`ConsolaApp`) como en la interfaz gráfica Swing (`VentanaPrincipal`):

1. **Gestión jerárquica de proyectos y tareas (Patrón Composite):**
   - Crear proyectos en el nivel raíz, subproyectos dentro de otros proyectos y tareas simples.
   - Ver la estructura completa en forma de árbol (texto indentado en consola y `JTree` en Swing).
   - Calcular automáticamente el porcentaje de avance de cada proyecto según sus tareas simples terminadas en todo el subárbol.
   - Bloquear elementos nulos, duplicados, autorreferencias y referencias circulares entre proyectos.
2. **Edición y eliminación reversibles:**
   - Editar título, descripción, prioridad (`BAJA`, `MEDIA`, `ALTA`) y fecha límite opcional (`AAAA-MM-DD`) con validación atómica previa.
   - Eliminar tareas o proyectos conservando su posición original para restaurarlos al deshacer.
3. **Control del ciclo de vida y estados (Patrón State):**
   - Transiciones controladas entre `Pendiente`, `En curso`, `En revisión` y `Terminada` mediante `Iniciar`, `Enviar a revisión`, `Completar` y `Reabrir`.
   - Devolución de `En revisión` a `En curso` cuando se requieren ajustes y bloqueo de transiciones inválidas mediante `IllegalStateException`.
4. **Historial de deshacer y rehacer (Patrón Command):**
   - Deshacer (`Undo`) y rehacer (`Redo`) creación, edición, eliminación y cambios de estado.
   - Consultar el historial de acciones ejecutadas sin registrar comandos que hayan fallado por validación.
5. **Filtros, búsqueda y alertas por fecha límite:**
   - Filtrar por estado, por prioridad, por texto libre (ID, título o descripción) o de manera combinada.
   - Clasificar automáticamente alertas (`SIN_FECHA`, `A_TIEMPO`, `PROXIMA_A_VENCER` en 3 días o menos, `VENCIDA` y `COMPLETADA`).
6. **Plantillas recurrentes y datos de ejemplo:**
   - Crear tareas rápidas desde plantillas predefinidas con prioridad y plazo sugerido (`PlantillaTarea`).
   - Cargar un proyecto de ejemplo una sola vez sin borrar el historial previo del usuario (`cargarDatosEjemplo`).

---

## Estructura del proyecto

```text
gestor-tareas-proyectos/
├── pom.xml
├── .gitignore
├── README.md
├── docs/
│   ├── DISTRIBUCION_TRABAJO_E_INTEGRACION.md        # Reparto por integrante y orden de integración en GitHub
│   ├── Documento_Academico_Gestor_Tareas.docx       # Documento académico y de diseño (patrones, alternativas y SOLID)
│   └── uml/
│       ├── DiagramasUML_Parte1.bas                  # Macros VBA (AddCanvas): Figuras 1, 2 y 3
│       ├── DiagramasUML_Parte1_VBA.txt              # Código VBA Parte 1 en texto plano
│       ├── Diagramas_UML_Parte1_Validado.docx       # Figuras 1, 2 y 3 nativas editables en Word
│       ├── DiagramasUML_Parte2.bas                  # Macros VBA (AddCanvas): Figuras 4, 5 y 6
│       ├── DiagramasUML_Parte2_VBA.txt              # Código VBA Parte 2 en texto plano
│       ├── Diagramas_UML_Parte2_Validado.docx       # Figuras 4, 5 y 6 nativas editables en Word
│       └── INSTRUCCIONES_UML_VBA.txt                # Pasos para importar y ejecutar las macros en Word
└── src/
    ├── main/java/com/gestortareas/
    │   ├── application/Main.java
    │   ├── console/ConsolaApp.java
    │   ├── gui/VentanaPrincipal.java
    │   ├── model/{TareaBase, Prioridad, NivelAlerta, PlantillaTarea}.java
    │   ├── patterns/composite/{TareaSimple, Proyecto}.java
    │   ├── patterns/state/{EstadoTarea, EstadoPendiente, EstadoEnCurso, EstadoEnRevision, EstadoTerminada}.java
    │   ├── patterns/command/{Comando, GestorComandos, AccionEstado, ComandoAgregarElemento, ComandoEditarTarea, ComandoEliminarElemento, ComandoCambiarEstado}.java
    │   └── service/{GestorTareasService, ConsultaTareasService}.java
    └── test/java/com/gestortareas/
        ├── patterns/composite/CompositePatternTest.java
        ├── patterns/state/StatePatternTest.java
        ├── patterns/command/CommandPatternTest.java
        └── service/GestorTareasServiceTest.java
```

---

## Compilación, pruebas y ejecución

Sitúate dentro de la carpeta `gestor-tareas-proyectos/` (donde se encuentra `pom.xml`) y ejecuta:

```bash
# 1. Compilar el proyecto
mvn clean compile

# 2. Ejecutar las 16 pruebas unitarias (JUnit 5)
mvn test

# 3. Ejecutar eligiendo modo en el menú inicial (1: Consola, 2: Swing)
mvn compile exec:java

# 4. Abrir directamente en modo Consola
mvn compile exec:java -Dexec.args="--console"

# 5. Abrir directamente en modo Interfaz Gráfica (Swing)
mvn compile exec:java -Dexec.args="--gui"
```

---

## Diagramas UML y documento de diseño

- **Documento de diseño y arquitectura:** `docs/Documento_Academico_Gestor_Tareas.docx` (incluye problema motivador, descripción propia, roles vs. clases reales, alternativa descartada, ventajas/desventajas y relación con SOLID para Composite, State y Command).
- **Diagramas UML editables en Word (VBA):** `docs/uml/DiagramasUML_Parte1.bas` (Figuras 1–3), `docs/uml/DiagramasUML_Parte2.bas` (Figuras 4–6), sus versiones `.docx` editables y la guía `docs/uml/INSTRUCCIONES_UML_VBA.txt`.
- **Distribución del equipo e integración Git:** `docs/DISTRIBUCION_TRABAJO_E_INTEGRACION.md`.
