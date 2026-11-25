# Gestor de Tareas y Proyectos en Java

Proyecto  de Ingeniería de Software II — Universidad Libre.


Sobre el proyecto



La aplicacion va a tener dos formas de usarse: una por consola y una interfaz grafica sencilla hecha en Java Swing. Lo importante es que las dos se conectan exactamente a la misma logica de negocio, asi que no duplicamos reglas. Todo se maneja en memoria mientras el programa esta abierto.

Lo que va a hacer el programa:

Crear tareas simples y tareas compuestas que agrupan subtareas en varios niveles.

Controlar los estados de cada tarea (Pendiente, En curso, En revision y Terminada) respetando las transiciones permitidas.

Deshacer y rehacer acciones del usuario.

Mostrar alertas de tareas vencidas o proximas a vencer junto con sus responsables.

Usar plantillas listas para crear rapido tareas o proyectos que se repiten seguido.

Ordenar y filtrar tareas por prioridad, fecha limite, estado o responsable.

Como organizamos el codigo (Encargado: Juan)

Dividimos el proyecto en tres capas para mantener todo ordenado y evitar mezclar la interfaz con la logica:

Presentacion (paquetes console y gui): solo reciben datos del usuario y muestran resultados.

Servicios (paquete service): coordinan las acciones, el historial, las plantillas, los filtros y las alertas.

Modelo y Patrones (paquetes model y patterns): aqui viven las clases principales y las reglas del negocio.

Dentro de src/main/java/com/unilibre/gestortareas tenemos:

model: la enumeracion Prioridad.

patterns/composite: ComponenteTarea, TareaBase, TareaSimple y TareaCompuesta.

patterns/state: EstadoTarea y los cuatro estados concretos.

patterns/command: Comando, GestorHistorial y los comandos concretos.

service: GestorTareasService y PlantillaTareaService.

console: MenuConsola.

gui: VentanaPrincipal.

application: Main.

Y en src/test/java/com/unilibre/gestortareas/patterns iran las pruebas unitarias con JUnit 5 para comprobar que cada patron funcione bien.

Que se necesita para correrlo (Encargado: Juan)

Solo hace falta tener instalado Java JDK 17 o superior, Apache Maven y Git.

Desde la carpeta raiz del proyecto (donde esta el pom.xml), se usan estos comandos en la terminal:

Para compilar: mvn clean compile

Para correr las pruebas: mvn test

Para abrir el programa: mvn exec:java -Dexec.mainClass="com.unilibre.gestortareas.application.Main"

Tambien se puede abrir la carpeta directo en IntelliJ, NetBeans o VS Code y ejecutar la clase Main.

Lo que lleva cada uno

Cada uno va actualizando su parte aqui conforme termina y prueba sus clases.

Parte de Juan (modelo base y patron Composite): La idea de esta parte es poder trabajar igual con una tarea suelta o con un proyecto lleno de subtareas sin llenar todo el programa de validaciones manuales. Ya dejamos listos los archivos base del contrato: Prioridad, la interfaz ComponenteTarea y la clase abstracta TareaBase (donde se manejan los datos comunes, los responsables y la conexion con los estados). Lo siguiente es programar TareaSimple, TareaCompuesta (controlando duplicados, ciclos y el calculo de progreso) y las pruebas en CompositeTest.

Parte de Helen (patron State, filtros y alertas): Como las tareas pasan por cuatro estados y no se puede saltar de cualquiera a cualquiera, usamos State para que cada estado decida que permite y que rechaza, evitando bloques enormes de if y else. Ya esta acordada la interfaz EstadoTarea. En la siguiente ronda se van a programar EstadoPendiente, EstadoEnCurso, EstadoEnRevision y EstadoTerminada junto con sus pruebas en StateTest, y despues se agregan los filtros, el ordenamiento y las alertas por fecha limite en el servicio.

Parte de Daniel (patron Command y plantillas): Para poder deshacer y rehacer sin tener que guardar copias gigantes de todo el sistema cada vez que pasa algo, cada accion se va a manejar como un objeto comando. Ya quedo definida la interfaz Comando con ejecutar, deshacer y getDescripcion. Lo que sigue es armar GestorHistorial, los cuatro comandos concretos (crear, eliminar, agregar subtarea y cambiar estado), el servicio de plantillas y las pruebas en CommandTest.

Como vamos hasta hoy

Primero revisamos el enunciado del parcial, definimos que entraba y que dejabamos por fuera, y escogimos los tres patrones (Composite, State y Command). Esto ya quedo cerrado.

Despues definimos la arquitectura por capas, los paquetes, los contratos entre clases y los casos de prueba que vamos a cubrir. Tambien quedo listo.

Configuramos Maven con JUnit 5, el .gitignore y las ramas main y develop en el repositorio. Ya comprobamos que Maven compila bien en local.

Dejamos listos los archivos de contratos base (Prioridad, ComponenteTarea, EstadoTarea, TareaBase y Comando) para que los tres podamos programar en paralelo sin pisarnos. Falta confirmar que queden fusionados y compilando en develop.

Queda pendiente para las proximas rondas: implementar los tres patrones con sus pruebas unitarias, integrar los servicios, armar la consola, hacer la ventana en Swing, generar los diagramas UML en Word y cerrar el documento final junto con la sustentacion.

Como estamos usando Git

Mantenemos la rama main para la version estable, develop para ir juntando lo que ya funciona, y ramas feature/... para que cada uno trabaje tranquilo en su parte. Cada persona hace sus propios commits con su cuenta real y mensajes cortos que expliquen el cambio (usando prefijos como feat, fix, test, docs o build).

Uso de herramientas de IA

Cumpliendo con las reglas del curso, dejamos claro que usamos Gemini como apoyo durante el proyecto para organizar el trabajo por etapas, revisar decisiones de diseno y guiarnos con la estructura de clases, pruebas y documentacion. Todo lo que se sube al repositorio lo revisamos, compilamos y entendemos entre los tres.

Conclusiones

Esta parte la escribiremos entre los tres cuando terminemos el codigo y las pruebas.
- **Declaración de uso de IA:** Se registrará explícitamente la herramienta utilizada y su rol de apoyo académico/revisión técnica, verificando que los tres integrantes comprendan y defiendan la totalidad del código entregado.
