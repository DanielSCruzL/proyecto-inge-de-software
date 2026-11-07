# Proyecto de Ingeniería de Software / Patrones de Diseño
## Propuesta 2: Gestor de Tareas y Proyectos (Task & Project Manager)

---

### Documento de Entrega — Fase de Definición Inicial
**Puntos a entregar:**
1. **Alcance del Sistema (In-Scope, Out-of-Scope y Áreas con Variación Frecuente)**
2. **Requisitos del Sistema (Requisitos Funcionales y No Funcionales)**

---

## 1. Alcance del Sistema

### 1.1. Contexto y Propósito General
El sistema a desarrollar es una aplicación en **Java** diseñada para la organización, seguimiento y gestión eficiente del trabajo personal y de equipos de trabajo. El sistema proporcionará una doble interfaz de usuario (**Consola de comandos interactiva y GUI sencilla en Java Swing**) operando sobre un **mismo núcleo desacoplado de lógica de negocio**. 

Permitirá gestionar tareas individuales y complejas estructuras jerárquicas de subtareas, controlar el ciclo de vida de cada tarea mediante transiciones de estado validadas, revertir y repetir acciones (Deshacer/Rehacer), emitir alertas y recordatorios de vencimiento para responsables, instanciar proyectos a partir de plantillas preconfiguradas y consultar el trabajo mediante múltiples criterios de ordenamiento y filtrado.

---

### 1.2. Funcionalidades Dentro del Alcance (In-Scope)

| Identificador | Funcionalidad Implementada | Descripción y Justificación Técnica |
| :--- | :--- | :--- |
| **F-IN-01** | **Estructura Jerárquica de Tareas y Subtareas** | Implementación de una estructura en árbol donde un elemento puede ser una tarea simple (hoja) o una tarea compuesta que contiene múltiples subtareas a cualquier nivel de anidamiento. El cálculo del porcentaje de avance, duración total y estado global de una tarea compuesta se calcula de forma acumulativa y uniforme. |
| **F-IN-02** | **Ciclo de Vida y Transiciones Controladas de Estado** | Soporte para los cuatro estados requeridos: *Pendiente*, *En Curso*, *En Revisión* y *Terminada*. Cada estado encapsula sus propias reglas de negocio y transiciones válidas, impidiendo transiciones ilegales (ej. no se puede pasar directo de *Pendiente* a *Terminada* sin pasar por *En Curso* o *En Revisión*; una tarea compuesta no puede marcarse *Terminada* si tiene subtareas activas). |
| **F-IN-03** | **Sistema de Deshacer y Rehacer (Undo / Redo)** | Pila de comandos reversibles que permite al usuario deshacer y rehacer operaciones críticas: creación/eliminación de tareas, modificaciones en el título/descripción/prioridad, y cambios de estado. |
| **F-IN-04** | **Motor Dinámico de Filtrado y Ordenamiento** | Capacidad de visualizar y listar tareas aplicando filtros independientes o combinados (por prioridad: *Alta, Media, Baja*; por estado; por fecha límite; o por responsable asignado), así como ordenamiento dinámico por múltiples criterios (por proximidad de fecha límite, por nivel de prioridad, o alfabético). |
| **F-IN-05** | **Sistema de Recordatorios y Alertas por Fecha Límite** | Detección periódica o bajo demanda de tareas vencidas o próximas a vencer (ej. a menos de 48 horas), notificando de forma automática a los responsables asignados a través de la consola y la interfaz gráfica. |
| **F-IN-06** | **Gestor de Plantillas para Proyectos y Tareas Recurrentes** | Catálogo de plantillas predeterminadas (ej. "Sprint de Desarrollo", "Auditoría de Calidad", "Entrega Académica") que pueden ser clonadas íntegramente (clonación profunda de la jerarquía) para crear nuevos proyectos o tareas recurrentes sin reescribir su configuración. |
| **F-IN-07** | **Doble Interfaz de Usuario sobre Núcleo Compartido** | Implementación de una interfaz por consola (CLI de texto interactiva) y una interfaz gráfica de usuario sencilla (GUI desarrollada en Java Swing), ambas consumiendo el mismo API del controlador y modelo de dominio sin duplicación de lógica. |
| **F-IN-08** | **Persistencia Local en Disco** | Mecanismo de almacenamiento y recuperación del estado del gestor (archivos JSON / serialización local) para garantizar la continuidad de la información entre ejecuciones de la aplicación. |

---

### 1.3. Funcionalidades Fuera del Alcance (Out-of-Scope) y su Justificación

| Identificador | Funcionalidad Excluida | Razón de Exclusión |
| :--- | :--- | :--- |
| **F-OUT-01** | **Colaboración en Tiempo Real Multi-usuario vía WebSockets/Red** | *Razón:* Las directrices del parcial establecen que la aplicación es de escritorio/consola local en Java ("una aplicación web no es obligatoria ni da puntos adicionales"). Introducir un servidor concurrente distribuido desviaría el esfuerzo hacia problemas de sincronización de red y sockets, en lugar de centrarse en la aplicación rigurosa de patrones de diseño GoF y principios SOLID. |
| **F-OUT-02** | **Envío Real de Correos Electrónicos (SMTP) o SMS mediante APIs Externas (ej. Twilio, SendGrid)** | *Razón:* Requiere llaves de API pagas, credenciales confidenciales y conexión a Internet activa, lo cual genera riesgo de fallos en el equipo de la docente durante la sustentación offline. En su lugar, el sistema implementa la arquitectura completa de observadores y notificación multicanal simulando la salida por consola, registro de eventos (log) y panel visual de alertas. |
| **F-OUT-03** | **Autenticación Empresarial Compleja y Control de Acceso RBAC (OAuth2 / JWT)** | *Razón:* La gestión de sesiones criptográficas y permisos granulares por roles excede el objetivo pedagógico de modelado orientado a objetos del proyecto. Se mantendrá un catálogo simple y transparente de usuarios/responsables locales asignables a las tareas. |
| **F-OUT-04** | **Diagramas de Gantt Interactivos y Tableros Kanban con Drag & Drop Complejo** | *Razón:* La rúbrica exige una interfaz gráfica sencilla y funcional. El desarrollo de widgets gráficos avanzados con arrastrar y soltar consume un tiempo desproporcionado en capas visuales de Swing que no aportan a la evaluación de patrones de diseño ni a la arquitectura del núcleo. |
| **F-OUT-05** | **Integración con Servicios Externos de Calendario (Google Calendar / Microsoft 365)** | *Razón:* Introduce dependencias de librerías externas no estándar y flujos de autenticación de terceros propensos a bloqueos locales de red durante la evaluación. |

---

### 1.4. Identificación y Justificación de Áreas con Variación o Cambio Frecuente
El sistema ha sido delimitado para contener **cuatro áreas principales con variación o cambio frecuente**, cumpliendo con creces el requisito de contar con al menos tres áreas de variabilidad. Estas áreas constituyen los ejes donde un diseño ingenuo resultaría rígido o lleno de condicionales frágiles, y donde se aplicarán patrones de diseño:

```
+----------------------------------------------------------------------------------------------------+
|                                ÁREAS CON VARIACIÓN FRECUENTE                                      |
+----------------------------------------------------------------------------------------------------+
|  [ÁREA 1: Ordenamiento y Filtrado]       <--->  Estrategias intercambiables de consulta/filtrado   |
|  [ÁREA 2: Ciclo de Vida y Transiciones]  <--->  Reglas dinámicas de estados (Pendiente/Curso/etc.) |
|  [ÁREA 3: Mecanismo de Alertas/Avisos]   <--->  Canales y receptores desacoplados de notificación  |
|  [ÁREA 4: Jerarquía y Plantillas]        <--->  Clonación y composición uniforme de tareas/árbol   |
+----------------------------------------------------------------------------------------------------+
```

#### Área de Variación 1: Políticas y Criterios de Ordenamiento y Filtrado de Tareas
* **Naturaleza de la variación:** Las necesidades de visualización cambian constantemente según el usuario y el momento del proyecto. Un usuario puede necesitar ver las tareas ordenadas por fecha límite de entrega, por nivel de prioridad (Urgente > Alta > Media > Baja) o alfabéticamente; asimismo, los filtros pueden operar por responsable, por estado o por umbral de fechas.
* **Justificación de diseño:** Si se implementa con estructuras condicionales (`if/else` o `switch`), cada nuevo criterio de ordenación o combinación de filtros obligaría a modificar las clases de negocio existentes (violando el principio Abierto/Cerrado - OCP). Se estructurará mediante una familia de algoritmos intercambiables en tiempo de ejecución.
* **Patrón de diseño asociado:** *Strategy* (Estrategias de ordenamiento y filtrado de tareas).

#### Área de Variación 2: Reglas de Transición y Comportamiento de los Estados de una Tarea
* **Naturaleza de la variación:** El ciclo de vida de una tarea posee reglas estrictas que evolucionan:
  - *Pendiente:* puede iniciar (*En Curso*) o cancelarse, pero no terminarse de inmediato.
  - *En Curso:* puede pasar a *En Revisión* o volver a *Pendiente*.
  - *En Revisión:* puede aprobarse (*Terminada*) o rechazarse volviendo a *En Curso*.
  - *Terminada:* estado final que solo bajo ciertas condiciones excepcionales podría reabrirse.
  Además, las acciones permitidas (editar descripción, agregar subtareas, reasignar responsable) dependen directamente del estado actual.
* **Justificación de diseño:** Codificar los estados mediante variables de texto o enumeraciones combinadas con `switch-case` en la clase `Tarea` generaría código monolítico y frágil. Ante un nuevo estado solicitado por la docente en la sustentación (por ejemplo, *Bloqueada* o *En Pausa*), el diseño debe permitir añadir el nuevo estado creando una clase independiente sin alterar las existentes.
* **Patrón de diseño asociado:** *State* (Ciclo de vida y transiciones válidas de la tarea).

#### Área de Variación 3: Canales y Destinatarios de Alertas / Recordatorios
* **Naturaleza de la variación:** Cuando una tarea se acerca a su fecha límite, se vence o cambia de estado crítico, el sistema debe notificar a uno o varios responsables asignados y actualizar los paneles correspondientes. Los receptores y canales de aviso pueden ampliarse en cualquier momento: visualización en consola, alerta emergente en la GUI, registro en bitácora (log), o notificación a múltiples responsables suscritos.
* **Justificación de diseño:** La tarea o el proyecto no deben conocer directamente las clases de la interfaz ni estar fuertemente acoplados a la forma en que se presentan las alertas. Los responsables o componentes interesados deben poder suscribirse y desuscribirse dinámicamente a los eventos de recordatorio sin modificar el núcleo de la tarea.
* **Patrón de diseño asociado:** *Observer* (Notificación de alertas y recordatorios ante cambios de estado o fechas límite).

#### Área de Variación 4 (Complementaria): Composición Jerárquica y Generación de Plantillas Recurrentes
* **Naturaleza de la variación:** Las tareas pueden variar desde simples actividades individuales de 15 minutos hasta macro-proyectos con múltiples fases, tareas secundarias y subtareas anidadas. Además, surgen con frecuencia nuevos tipos de proyectos estandarizados (plantillas) que se replican periódicamente.
* **Justificación de diseño:** El cliente del sistema necesita interactuar con tareas simples y proyectos compuestos a través de la misma interfaz polimórfica (ej. `getProgreso()`, `isTerminada()`). Igualmente, la creación de nuevos proyectos a partir de plantillas preexistentes requiere clonar árboles enteros de objetos sin acoplarse a clases concretas.
* **Patrones de diseño asociados:** *Composite* (tratamiento uniforme de tareas simples y compuestas) y *Prototype* / *Factory Method* (clonación y generación de plantillas recurrentes).

---

## 2. Requisitos del Sistema

### 2.1. Requisitos Funcionales (RF)

| Código | Nombre del Requisito | Descripción | Actor(es) | Prioridad | Criterio de Aceptación |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **RF-01** | **Gestión de Tareas Simples** | El sistema debe permitir registrar, consultar, editar información básica (título, descripción, fecha límite, prioridad) y eliminar tareas individuales. | Usuario | Alta | La tarea se almacena en memoria con sus atributos completos y es accesible en ambas interfaces. |
| **RF-02** | **Gestión de Subtareas (Composición)** | El sistema debe permitir agregar una o más subtareas dentro de una tarea existente, permitiendo múltiples niveles jerárquicos de anidamiento. | Usuario | Alta | Una tarea compuesta contiene y administra su lista de subtareas de manera uniforme respecto a una tarea simple. |
| **RF-03** | **Cálculo Recursivo de Avance y Estado Global** | El sistema debe calcular automáticamente el porcentaje de progreso (0% a 100%) de una tarea compuesta en función del estado de completitud de todas sus subtareas hijas. | Sistema | Alta | El progreso de una tarea compuesta refleja el promedio ponderado o proporción de subtareas terminadas; no se puede marcar terminada la tarea padre si alguna subtarea sigue pendiente. |
| **RF-04** | **Transiciones Controladas de Estado** | El sistema debe permitir transicionar el estado de una tarea únicamente a través de los caminos válidos: *Pendiente* $\rightarrow$ *En Curso* $\rightarrow$ *En Revisión* $\rightarrow$ *Terminada* (y retornos válidos como *En Revisión* $\rightarrow$ *En Curso*). | Usuario | Alta | Intentar forzar una transición inválida (ej. *Pendiente* a *Terminada*) genera un mensaje de rechazo descriptivo sin modificar el estado actual de la tarea. |
| **RF-05** | **Asignación de Responsables** | El sistema debe permitir asignar uno o más responsables (usuarios miembros del equipo) a una tarea o subtarea específica. | Usuario | Media | La tarea refleja claramente sus responsables asociados tanto en listados como en detalles. |
| **RF-06** | **Detección y Disparo de Alertas por Fecha Límite** | El sistema debe evaluar las fechas límite y disparar alertas de recordatorio cuando una tarea esté próxima a vencer (umbral configurable, ej. 2 días antes) o cuando se encuentre vencida. | Sistema | Alta | Los responsables asignados y los componentes visuales suscritos reciben la alerta con los datos de la tarea afectada. |
| **RF-07** | **Función Deshacer (Undo)** | El sistema debe permitir revertir la última acción destructiva o modificadora realizada por el usuario (creación, edición de atributos, cambio de estado, eliminación de tareas). | Usuario | Alta | Al ejecutar "Deshacer", el estado del sistema vuelve al punto inmediatamente anterior a la ejecución de dicha acción. |
| **RF-08** | **Función Rehacer (Redo)** | El sistema debe permitir restablecer una acción que previamente fue revertida mediante "Deshacer", siempre que no se hayan introducido nuevas acciones intercaladas. | Usuario | Alta | Al ejecutar "Rehacer", se re-aplica la operación revertida restaurando su efecto sobre las tareas. |
| **RF-09** | **Filtrado Multicriterio de Tareas** | El sistema debe permitir filtrar la lista de tareas según diferentes criterios: por nivel de prioridad (*Baja, Media, Alta, Urgente*), por estado actual, por responsable asignado o por tareas vencidas. | Usuario | Media | La vista muestra únicamente los elementos que satisfacen la estrategia de filtrado seleccionada. |
| **RF-10** | **Ordenamiento Dinámico de Tareas** | El sistema debe permitir ordenar la lista de tareas activas según diversas estrategias: por fecha límite (ascendente/descendente), por prioridad (de mayor a menor importancia) o por nombre alfabético. | Usuario | Media | Las tareas se ordenan inmediatamente conforme a la estrategia elegida sin alterar el árbol jerárquico base. |
| **RF-11** | **Gestión de Plantillas de Proyectos/Tareas** | El sistema debe permitir guardar una estructura completa de tareas/subtareas como una plantilla reutilizable en el catálogo. | Usuario | Media | La plantilla queda almacenada con su configuración base y lista para ser clonada. |
| **RF-12** | **Creación a partir de Plantillas (Clonación)** | El sistema debe permitir instanciar un nuevo proyecto o conjunto de tareas a partir de una plantilla existente, generando copias independientes de todos sus elementos. | Usuario | Media | La nueva tarea/proyecto instanciado es autónomo; modificar sus datos no altera la plantilla original. |
| **RF-13** | **Interfaz de Línea de Comandos (CLI)** | El sistema debe proporcionar una interfaz interactiva de consola con menús numéricos claros, captura de datos y visualización textual legible de árboles de tareas. | Usuario | Alta | Todas las operaciones del sistema pueden ejecutarse fluidamente mediante comandos/opciones de consola. |
| **RF-14** | **Interfaz Gráfica de Usuario Sencilla (GUI)** | El sistema debe proporcionar una interfaz gráfica desarrollada en Java Swing que presente la lista/árbol de tareas, botones de acción (*Crear, Transicionar, Deshacer, Rehacer*), filtros desplegables y visualización de alertas. | Usuario | Alta | La GUI se ejecuta de forma autónoma utilizando los mismos controladores y servicios de la lógica de negocio que la consola. |
| **RF-15** | **Persistencia Local de Datos** | El sistema debe permitir guardar en archivo y cargar el estado completo de proyectos, tareas y plantillas al iniciar o cerrar la aplicación. | Sistema / Usuario | Media | Al reiniciar la aplicación, los proyectos y tareas previamente creados se recuperan íntegramente. |

---

### 2.2. Requisitos No Funcionales (RNF)

Clasificados formalmente de acuerdo con el estándar de calidad de software **ISO/IEC 25010**:

#### A. Mantenibilidad y Extensibilidad (ISO/IEC 25010 - Maintainability)
* **RNF-01 (Desacoplamiento Arquitectónico Core / UI):** La lógica de negocio debe estar 100% aislada de las tecnologías de presentación (consola y Swing). Ninguna clase del dominio debe importar paquetes como `java.awt`, `javax.swing` ni realizar operaciones directas de `System.out.println()`.
* **RNF-02 (Principio Abierto/Cerrado - OCP):** El diseño debe permitir añadir nuevas estrategias de ordenamiento/filtrado, nuevos estados del ciclo de vida o nuevos tipos de alertas sin necesidad de modificar el código existente en los controladores o en el dominio.
* **RNF-03 (Modularidad y Cohesión):** El código fuente en Java debe estar organizado en paquetes con responsabilidades unívocas y explícitas (ej. `core.model`, `core.state`, `core.strategy`, `core.command`, `core.observer`, `core.prototype`, `service`, `ui.cli`, `ui.gui`).

#### B. Usabilidad (ISO/IEC 25010 - Usability)
* **RNF-04 (Consistencia entre Interfaces):** Tanto la versión de consola como la versión gráfica deben ofrecer paridad de funcionalidades y terminología consistente para todas las operaciones sobre tareas y proyectos.
* **RNF-05 (Claridad en Respuestas y Errores):** El sistema debe proporcionar mensajes informativos claros cuando una acción no pueda completarse (ej. "Transición inválida: No es posible pasar de 'Pendiente' a 'Terminada' directamente", "No hay acciones disponibles para deshacer").

#### C. Rendimiento y Eficiencia en Tiempo (ISO/IEC 25010 - Performance Efficiency)
* **RNF-06 (Tiempo de Respuesta en Memoria):** Las operaciones de filtrado, ordenamiento, cálculo acumulativo de progreso y ejecución/reversión de comandos (`undo`/`redo`) deben responder en un tiempo inferior a **100 milisegundos** para colecciones de hasta 1.000 tareas en memoria.

#### D. Confiabilidad e Integridad (ISO/IEC 25010 - Reliability)
* **RNF-07 (Tolerancia a Fallos y Validación de Entradas):** El sistema debe capturar y controlar todas las excepciones de entrada de datos (formatos de fecha erróneos, índices inexistentes, valores nulos) evitando que la aplicación sufra caídas o cierres abruptos (*crashes*).
* **RNF-08 (Consistencia de Estados en Jerarquía):** La estructura jerárquica no debe permitir referencias circulares (una tarea no puede ser subtarea de sí misma o de sus descendientes).

#### E. Portabilidad y Compatibilidad (ISO/IEC 25010 - Portability)
* **RNF-09 (Entorno de Ejecución Java Estándar):** El software debe compilar y ejecutarse correctamente sobre Java SE (JDK 17 o superior) utilizando herramientas de construcción estándar (Maven o Gradle), siendo multiplataforma (ejecutable en Windows, Linux y macOS sin código nativo).

#### F. Testabilidad (ISO/IEC 25010 - Testability)
* **RNF-10 (Cobertura de Pruebas Unitarias):** Todos los componentes que implementan patrones de diseño (estrategias, estados, comandos, observadores y estructuras compuestas) deben contar con pruebas unitarias automatizadas con **JUnit 5**, verificando su comportamiento independiente de la interfaz gráfica.

---

### Resumen de Cumplimiento de la Rúbrica del Parcial

| Exigencia del Parcial | Cumplimiento en el Documento de Definición |
| :--- | :--- |
| **Condiciones Generales:** Java, consola + GUI sencilla, mismo núcleo. | Especificado en RNF-01, RNF-04, RF-13 y RF-14. |
| **Alcance:** Funcionalidades dentro y fuera con razones explícitas. | Detallado en las secciones 1.2 y 1.3 con justificaciones arquitectónicas. |
| **Áreas con Variación:** Al menos 3 áreas con variación frecuente. | Identificadas y justificadas 4 áreas en la sección 1.4 (Estrategias de orden/filtro, Estados de tarea, Alertas/Observadores, y Composición/Plantillas). |
| **Requisitos:** Lista completa de funcionales y no funcionales. | Detallados 15 Requisitos Funcionales (RF-01 a RF-15) y 10 Requisitos No Funcionales (RNF-01 a RNF-10) bajo estándar ISO/IEC 25010. |
