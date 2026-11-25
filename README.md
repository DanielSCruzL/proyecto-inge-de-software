# Gestor de Tareas y Proyectos en Java

Proyecto académico de **Ingeniería de Software II** — Universidad Libre.

> **Documento vivo:** Este `README.md` se actualiza progresivamente en cada etapa del proyecto. Cada integrante documenta exclusivamente las decisiones, implementaciones y pruebas reales de su módulo.

---

## 1. Equipo y responsabilidades

| Integrante | Módulo principal | Sección principal en este README |
| :--- | :--- | :--- |
| **Juan David Diaz** | Arquitectura, Modelo base, Patrón **Composite**, integración del núcleo | Secciones 2, 3 y 4.1 |
| **Helen Sofia Malagon** | Patrón **State**, reglas de transición, filtros, ordenamiento y alertas | Sección 4.2 |
| **Daniel Santiago Cruz** | Patrón **Command**, historial Undo/Redo y plantillas recurrentes | Sección 4.3 |

---

## 2. Arquitectura y estructura del proyecto *(Responsable: Juan David Diaz)*

El sistema separa la presentación (Consola y Java Swing) del núcleo de negocio (`service`, `model` y `patterns`), garantizando que ambas interfaces consuman exactamente los mismos servicios.

```text
gestor-tareas/
├── pom.xml
├── README.md
├── src/
│   ├── main/java/com/unilibre/gestortareas/
│   │   ├── model/
│   │   ├── patterns/
│   │   │   ├── composite/
│   │   │   ├── state/
│   │   │   └── command/
│   │   ├── service/
│   │   ├── console/
│   │   ├── gui/
│   │   └── application/
│   └── test/java/com/unilibre/gestortareas/
├── documentacion/
└── respaldo_vba/
```

---

## 3. Compilación, pruebas y ejecución *(Responsable inicial: Juan David Diaz; actualización conjunta)*

### Requisitos técnicos
- Java JDK 17 o superior
- Apache Maven 3.8 o superior
- Git

### Comandos
```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar pruebas unitarias (JUnit 5)
mvn test

# Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="com.unilibre.gestortareas.application.Main"
```

---

## 4. Documentación por módulo y patrón

### 4.1. Modelo base y Patrón Composite *(Responsable: Juan David Diaz)*
- **Estado:** Asignado (pendiente de implementación en Etapa 4).
- **Decisiones registradas:** Progreso de tarea compuesta vacía depende de su propio estado; progreso multinivel recursivo; validación de duplicados por ID y prevención de ciclos jerárquicos.
- **Pruebas verificadas:** Pendiente.

### 4.2. Patrón State, filtros y alertas *(Responsable: Helen Sofia Malagon)*
- **Estado:** Asignado (pendiente de implementación en Etapa 4).
- **Decisiones registradas:** Estados `Pendiente`, `En curso`, `En revisión` y `Terminada` con transiciones controladas y método `restaurarEstado(...)` en `TareaBase` para desacoplar Undo de las reglas de negocio.
- **Pruebas verificadas:** Pendiente.

### 4.3. Patrón Command, historial y plantillas *(Responsable: Daniel Santiago Cruz)*
- **Estado:** Asignado (pendiente de implementación en Etapa 4).
- **Decisiones registradas:** `GestorHistorial` con pilas `pilaDeshacer`/`pilaRehacer`; protección ante comandos fallidos; preservación de índice original al deshacer eliminación; plantillas predefinidas en `PlantillaTareaService`.
- **Pruebas verificadas:** Pendiente.

---

## 5. Registro de estado por etapa *(Actualización conjunta)*

| Etapa | Descripción | Estado | Responsable(s) |
| :--- | :--- | :--- | :--- |
| Etapa 1 | Análisis y planificación | Verificada | Equipo completo |
| Etapa 2 | Arquitectura y diseño | Verificada | Equipo completo |
| Etapa 3 | Configuración Maven, JUnit 5 y Git | Asignada (pendiente de confirmación) | Juan David (inicialización), Helen y Daniel (verificación local) |
| Etapa 4 | Implementación del núcleo (Composite, State, Command, Servicios) | Pendiente | Juan David, Helen, Daniel |
| Etapa 5 | Interfaz por consola | Pendiente | Equipo completo |
| Etapa 6 | Interfaz gráfica Swing | Pendiente | Equipo completo |
| Etapa 7 | Pruebas unitarias JUnit 5 | Pendiente | Equipo completo |
| Etapa 8 | Diagramas UML editables en Word (VBA) | Pendiente | Equipo completo |
| Etapa 9 | Documento de diseño | Pendiente | Equipo completo |
| Etapa 10 | Preparación de sustentación | Pendiente | Equipo completo |

---

## 6. Conclusiones e integridad académica *(Actualización conjunta al cierre)*
- *Pendiente de completar al finalizar la implementación y pruebas.*
.
