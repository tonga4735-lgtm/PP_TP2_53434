package modelo;

import modelo.actividades.Actividad;

import java.io.Serializable;
import java.time.LocalDate;

public class Inscripcion implements Serializable {
    private Estudiante estudiante;
    private Actividad actividad;
    private LocalDate fecha;
    private String estado;
    private TicketDeAcceso ticket;

    public Inscripcion(Estudiante estudiante, Actividad actividad) {
        this.estudiante = estudiante;
        this.actividad = actividad;
        this.fecha = LocalDate.now();
        this.estado = "PENDIENTE";
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void confirmar() {
        this.estado = "CONFIRMADA";
    }

    public boolean estaConfirmada() {
        return "CONFIRMADA".equals(estado);
    }

    public TicketDeAcceso getTicket() {
        return ticket;
    }

    public TicketDeAcceso generarTicket() {
        if (!estaConfirmada()) {
            throw new IllegalStateException("La inscripcion no esta confirmada");
        }
        if (ticket == null) {
            ticket = new TicketDeAcceso();
        }
        return ticket;
    }

    @Override
    public String toString() {
        return estudiante.getNombre() + " - " + estado + " - " + fecha;
    }

    public class TicketDeAcceso implements Serializable {
        private String idTicket;
        private LocalDate fechaEmision;

        public TicketDeAcceso() {
            this.idTicket = "TCK-" + actividad.getId() + "-" + estudiante.getLegajo();
            this.fechaEmision = LocalDate.now();
        }

        public String getIdTicket() {
            return idTicket;
        }

        public LocalDate getFechaEmision() {
            return fechaEmision;
        }

        public void enviarTicket() {
            System.out.println("Enviando ticket " + idTicket + " a " + estudiante.getNombre() + "...");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Ticket " + idTicket + " enviado a " + estudiante.getNombre());
        }
    }
}
