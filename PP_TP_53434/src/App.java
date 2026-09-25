import certificacion.Certificable;
import excepciones.CupoExcedidoException;
import hilos.EnvioTicketsThread;
import modelo.actividades.Actividad;
import modelo.actividades.Charla;
import modelo.actividades.Curso;
import modelo.Estudiante;
import modelo.EventoUniversitario;
import modelo.Inscripcion;
import modelo.Sala;
import modelo.actividades.Taller;

import java.util.List;

public class App {
    public static void main(String[] args) {
        Estudiante e1 = new Estudiante("1001", "Ana Gomez");
        Estudiante e2 = new Estudiante("1002", "Bruno Diaz");
        Estudiante e3 = new Estudiante("1003", "Marta Diaz");
        Estudiante e4 = new Estudiante("1004", "Carlos Ruiz");

        EventoUniversitario evento1 = new EventoUniversitario("EV001", "Semana de la Informatica", 10000.0, false);
        EventoUniversitario copiaEvento1 = new EventoUniversitario(evento1);

        System.out.println("=== Evento original ===");
        evento1.mostrarDatos();
        System.out.println("=== Copia del evento (constructor de copia) ===");
        copiaEvento1.mostrarDatos();
        System.out.println("Cantidad total de eventos creados: " + EventoUniversitario.getCantidadEventos());
        System.out.println();

        EventoUniversitario evento2 = new EventoUniversitario("EV002", "Jornada de Extension", 8000.0, false);

        Sala sala1 = new Sala(1, "Auditorio Principal");
        evento2.asignarSala(sala1);

        evento2.crearActividad(1, "Introduccion a Java", 2, "Charla", "Dr. Juan Lopez");
        evento2.crearActividad(2, "Taller de Spring Boot", 3, "Taller", "true");
        evento2.crearActividad(3, "Curso de Bases de Datos", 3, "Curso", "2");

        List<Actividad> actividadesEvento2 = evento2.getActividades();
        Actividad charla = actividadesEvento2.get(0);
        Actividad taller = actividadesEvento2.get(1);
        Actividad curso = actividadesEvento2.get(2);

        try {
            charla.inscribir(e1);
            charla.inscribir(e2);
            taller.inscribir(e1);
            taller.inscribir(e2);
            taller.inscribir(e3);
            curso.inscribir(e2);
            curso.inscribir(e3);
            curso.inscribir(e4);
        } catch (CupoExcedidoException e) {
            System.out.println("No se pudo inscribir: " + e.getMessage());
        }

        System.out.println("=== Intento de inscripcion que excede el cupo ===");
        try {
            charla.inscribir(e3);
            charla.inscribir(e4);
        } catch (CupoExcedidoException e) {
            System.out.println("Excepcion controlada: " + e.getMessage());
        }
        System.out.println();

        evento2.mostrarDatos();
        for (Actividad a : actividadesEvento2) {
            a.mostrarInscripciones();
        }
        System.out.println();

        System.out.println("=== Emision de certificados (Taller y Curso, no Charla) ===");
        for (Actividad a : actividadesEvento2) {
            if (a instanceof Certificable) {
                Certificable certificable = (Certificable) a;
                for (Inscripcion i : a.getInscripciones()) {
                    System.out.println(certificable.generarCertificado(i.getEstudiante()));
                }
            }
        }
        System.out.println();

        System.out.println("=== Filtrado de actividades por tipo (generics acotados) ===");
        List<Charla> charlas = evento2.filtrarActividadesPorTipo(Charla.class);
        List<Taller> talleres = evento2.filtrarActividadesPorTipo(Taller.class);
        List<Curso> cursos = evento2.filtrarActividadesPorTipo(Curso.class);
        System.out.println("Charlas: " + charlas.size());
        System.out.println("Talleres: " + talleres.size());
        System.out.println("Cursos: " + cursos.size());
        System.out.println("Costo materiales charlas: $" + evento2.calcularCostoMateriales(charlas));
        System.out.println("Costo materiales talleres: $" + evento2.calcularCostoMateriales(talleres));
        System.out.println("Costo materiales cursos: $" + evento2.calcularCostoMateriales(cursos));
        System.out.println();

        System.out.println("=== Confirmacion de inscripciones y generacion de tickets ===");
        for (Actividad a : actividadesEvento2) {
            List<Inscripcion> inscripciones = a.getInscripciones();
            for (int i = 0; i < inscripciones.size(); i++) {
                if (i % 2 == 0) {
                    Inscripcion insc = inscripciones.get(i);
                    insc.confirmar();
                    insc.generarTicket();
                }
            }
        }

        EnvioTicketsThread hiloEnvio = new EnvioTicketsThread(evento2);
        hiloEnvio.start();

        System.out.println("[" + Thread.currentThread().getName() + "] Mostrando datos del evento mientras se envian los tickets...");
        evento2.mostrarDatos();
        for (Actividad a : actividadesEvento2) {
            a.mostrarInscripciones();
        }

        try {
            hiloEnvio.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.println();

        System.out.println("=== Persistencia del evento (serializacion / deserializacion) ===");
        try {
            boolean persistido = evento2.persistirEvento();
            if (persistido) {
                System.out.println("Evento persistido correctamente.");
            } else {
                System.out.println("No se pudo persistir el evento.");
            }

            EventoUniversitario eventoRecuperado = EventoUniversitario.recuperarEvento(evento2.getId());
            if (eventoRecuperado != null) {
                System.out.println("Evento recuperado exitosamente:");
                eventoRecuperado.mostrarDatos();
            }

            EventoUniversitario eventoInexistente = EventoUniversitario.recuperarEvento("NOEXISTE");
            if (eventoInexistente == null) {
                System.out.println("Caso fallido controlado: no existe el evento solicitado.");
            }
        } catch (Exception ex) {
            System.out.println("Error inesperado en el flujo de persistencia: " + ex.getMessage());
        } finally {
            System.out.println("Flujo de persistencia finalizado.");
        }
        System.out.println();

        System.out.println("Cantidad total de eventos creados: " + EventoUniversitario.getCantidadEventos());
    }
}
