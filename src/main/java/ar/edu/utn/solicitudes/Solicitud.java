package ar.edu.utn.solicitudes;

public class Solicitud {

    public int id;
    public String legajo;
    public String tipo;
    public String descripcion;
    public String fecha;
    public String estado;
    public String prioridad;
    public String responsable;

    public void cambiarEstado(String nuevo) {
        this.estado = nuevo;
    }
}
