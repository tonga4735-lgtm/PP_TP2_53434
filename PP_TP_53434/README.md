# Sistema de Eventos Universitarios

Proyecto Java que implementa, en su estado final e integrado, todo lo pedido en el TP1
y el TP2 de Paradigmas de Programación (UTN - FRM). Cada clase, interfaz y excepción
está en su propio archivo, organizada en los packages `modelo`, `excepciones`,
`certificacion` e `hilos`, tal como indican los diagramas de clases finales.

## Estructura

```
src/
  modelo/
    EventoUniversitario.java
    Sala.java
    Actividad.java        (abstracta)
    Charla.java
    Taller.java
    Curso.java
    Estudiante.java
    Inscripcion.java      (contiene la clase anidada TicketDeAcceso)
  excepciones/
    CupoExcedidoException.java
  certificacion/
    Certificable.java
  hilos/
    EnvioTicketsThread.java
  App.java
```

## Qué resuelve

- **Encapsulamiento y constructor de copia** en `EventoUniversitario`, con contador
  estático de eventos creados.
- **Composición y agregación**: `EventoUniversitario` compone `Actividad` (1..*) y
  agrega `Sala` (1); `Actividad` inscribe `Estudiante` a través de `Inscripcion`.
- **Herencia y polimorfismo**: `Actividad` es abstracta, con `Charla`, `Taller` y
  `Curso` como subtipos concretos, cada uno con su propio `calcularCostoMateriales()`
  y `getTipo()`. `mostrarIdentificacion()` es `final` en `Actividad`.
- **Excepciones**: `Actividad.inscribir()` lanza `CupoExcedidoException` (excepción
  chequeada) cuando se supera el cupo máximo; `App` la atrapa y sigue ejecutando.
- **Interfaces**: `Certificable` es implementada por `Taller` y `Curso` (no por
  `Charla`), para emitir certificados de asistencia.
- **Genéricos con wildcards**: `filtrarActividadesPorTipo(Class<T> tipo)` y
  `calcularCostoMateriales(List<? extends Actividad> actividades)` en
  `EventoUniversitario`.
- **Persistencia**: `persistirEvento()` y `recuperarEvento(id)` serializan y
  deserializan un `EventoUniversitario` a disco, con manejo granular de excepciones
  (`FileNotFoundException`, `InvalidClassException`, `IOException`,
  `ClassNotFoundException`) y un flujo `try-catch-finally` en `App`.
- **Clases anidadas e hilos**: `TicketDeAcceso` es una clase miembro anidada dentro
  de `Inscripcion`, y solo se genera para inscripciones confirmadas.
  `EnvioTicketsThread` (package `hilos`) envía todos los tickets en un hilo separado
  mientras el hilo principal sigue mostrando el estado del evento por consola.

## Nota sobre `crearActividad`

El método `crearActividad` recibe un parámetro adicional `datoAdicional` (String)
además de `id`, `titulo`, `cupo` y `tipo`, para poder inicializar el dato propio de
cada subtipo sin romper el encapsulamiento: nombre del disertante (Charla),
`"true"`/`"false"` para uso de notebook (Taller) o nivel numérico (Curso).

## Cómo compilar y ejecutar

Desde la carpeta `src`:

```bash
find . -name "*.java" > sources.txt
javac -d ../out @sources.txt
cd ../out
java App
```

También puede abrirse directamente como proyecto en IntelliJ IDEA (`File > Open`,
seleccionando la carpeta raíz del proyecto) y ejecutar la clase `App`.

Ejecutar el programa genera además un archivo `evento_EV002.ser` (serialización del
evento) en el directorio de trabajo.
