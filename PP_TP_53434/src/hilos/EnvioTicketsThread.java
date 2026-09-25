package hilos;

import modelo.actividades.Actividad;
import modelo.EventoUniversitario;
import modelo.Inscripcion;

public class EnvioTicketsThread extends Thread {
    private EventoUniversitario evento;

    public EnvioTicketsThread(EventoUniversitario evento) {
        this.evento = evento;
    }

    @Override
    public void run() {
        System.out.println("[" + Thread.currentThread().getName() + "] Iniciando envio de tickets del evento " + evento.getId());
        for (Actividad actividad : evento.getActividades()) {
            for (Inscripcion inscripcion : actividad.getInscripciones()) {
                if (inscripcion.estaConfirmada() && inscripcion.getTicket() != null) {
                    inscripcion.getTicket().enviarTicket();
                }
            }
        }
        System.out.println("[" + Thread.currentThread().getName() + "] Envio de tickets finalizado");
    }
}
