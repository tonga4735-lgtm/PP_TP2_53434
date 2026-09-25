package modelo.actividades;

import excepciones.CupoExcedidoException;
import modelo.Estudiante;
import modelo.Inscripcion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Actividad implements Serializable {
    private int id;
    private String titulo;
    private int cupoMaximo;
    public static final int CUPO_MINIMO = 1;
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public Actividad(int id, String titulo, int cupoMaximo) {
        this.id = id;
        this.titulo = titulo;
        this.cupoMaximo = cupoMaximo;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public Inscripcion inscribir(Estudiante estudiante) throws CupoExcedidoException {
        if (inscripciones.size() >= cupoMaximo) {
            throw new CupoExcedidoException("Cupo excedido para la actividad '" + titulo + "' (maximo " + cupoMaximo + ")");
        }
        Inscripcion inscripcion = new Inscripcion(estudiante, this);
        inscripciones.add(inscripcion);
        return inscripcion;
    }

    public void mostrarInscripciones() {
        System.out.println("Inscripciones en '" + titulo + "':");
        if (inscripciones.isEmpty()) {
            System.out.println("  (sin inscripciones)");
        }
        for (Inscripcion i : inscripciones) {
            System.out.println("  - " + i);
        }
    }

    public final void mostrarIdentificacion() {
        System.out.println("[" + getTipo() + "] #" + id + " - " + titulo + " - cupo maximo: " + cupoMaximo);
    }

    public abstract double calcularCostoMateriales();

    public abstract String getTipo();
}
