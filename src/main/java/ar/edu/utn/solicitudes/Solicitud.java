package ar.edu.utn.solicitudes;

public class Solicitud {

    public int id;
    public String legajo;
    public TipoSolicitud tipo;
    public String descripcion;
    public String fecha;
    public EstadoSolicitud estado;
    public Prioridad prioridad;
    public String responsable;

    public void cambiarEstado(EstadoSolicitud nuevo) {
        this.estado = nuevo;
    }
}
