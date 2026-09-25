package modelo;

import modelo.actividades.Actividad;
import modelo.actividades.Charla;
import modelo.actividades.Curso;
import modelo.actividades.Taller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventoUniversitario implements Serializable {
    private final String id;
    private String titulo;
    private double costoBase;
    private boolean gratuito;
    private static int cantidadEventos = 0;
    private Sala sala;
    private List<Actividad> actividades = new ArrayList<>();

    public EventoUniversitario(String id, String titulo, double costoBase, boolean gratuito) {
        this.id = id;
        this.titulo = titulo;
        this.costoBase = costoBase;
        this.gratuito = gratuito;
        cantidadEventos++;
    }

    public EventoUniversitario(EventoUniversitario otro) {
        this.id = otro.id;
        this.titulo = otro.titulo;
        this.costoBase = otro.costoBase;
        this.gratuito = otro.gratuito;
        this.sala = otro.sala;
        this.actividades = new ArrayList<>(otro.actividades);
        cantidadEventos++;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public double getCostoBase() {
        return costoBase;
    }

    public boolean isGratuito() {
        return gratuito;
    }

    public Sala getSala() {
        return sala;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public double calcularCostoEstimado() {
        if (gratuito) {
            return 0.0;
        }
        double costoActividades = 0.0;
        for (Actividad a : actividades) {
            costoActividades += a.calcularCostoMateriales();
        }
        return (costoBase + costoActividades) * 1.21;
    }

    public void asignarSala(Sala sala) {
        this.sala = sala;
    }

    public void crearActividad(int id, String titulo, int cupo, String tipo, String datoAdicional) {
        Actividad actividad;
        switch (tipo) {
            case "Charla":
                actividad = new Charla(id, titulo, cupo, datoAdicional);
                break;
            case "Taller":
                actividad = new Taller(id, titulo, cupo, Boolean.parseBoolean(datoAdicional));
                break;
            case "Curso":
                actividad = new Curso(id, titulo, cupo, Integer.parseInt(datoAdicional));
                break;
            default:
                throw new IllegalArgumentException("Tipo de actividad desconocido: " + tipo);
        }
        actividades.add(actividad);
    }

    public <T extends Actividad> List<T> filtrarActividadesPorTipo(Class<T> tipo) {
        List<T> resultado = new ArrayList<>();
        for (Actividad a : actividades) {
            if (tipo.isInstance(a)) {
                resultado.add(tipo.cast(a));
            }
        }
        return resultado;
    }

    public double calcularCostoMateriales(List<? extends Actividad> actividades) {
        double total = 0.0;
        for (Actividad a : actividades) {
            total += a.calcularCostoMateriales();
        }
        return total;
    }

    public void mostrarDatos() {
        System.out.println("Evento [" + id + "] " + titulo + " - gratuito: " + gratuito
                + " - costo estimado: $" + String.format("%.2f", calcularCostoEstimado()));
        System.out.println("Sala asignada: " + (sala != null ? sala : "sin asignar"));
        System.out.println("Actividades:");
        for (Actividad a : actividades) {
            a.mostrarIdentificacion();
        }
    }

    public boolean persistirEvento() {
        String nombreArchivo = "evento_" + id + ".ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            oos.writeObject(this);
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("No se pudo crear el archivo de persistencia: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de entrada/salida al persistir el evento: " + e.getMessage());
        }
        return false;
    }

    public static EventoUniversitario recuperarEvento(String id) {
        String nombreArchivo = "evento_" + id + ".ser";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
            return (EventoUniversitario) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo del evento: " + e.getMessage());
        } catch (InvalidClassException e) {
            System.out.println("Version de clase incompatible al leer el evento: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de entrada/salida al recuperar el evento: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Clase no encontrada al deserializar el evento: " + e.getMessage());
        }
        return null;
    }

    public static int getCantidadEventos() {
        return cantidadEventos;
    }
}
