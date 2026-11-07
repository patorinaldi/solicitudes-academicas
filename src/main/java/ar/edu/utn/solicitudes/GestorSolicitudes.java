package ar.edu.utn.solicitudes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class GestorSolicitudes {

    private static final int LARGO_MAXIMO_DESCRIPCION = 300;
    private static final int LARGO_RECLAMO_EXTENSO = 200;

    private String archivo;
    private ArrayList<Solicitud> lista = new ArrayList<>();
    private HashMap<String, Estudiante> estudiantes = new HashMap<>();
    private ArrayList<Responsable> responsables = new ArrayList<>();
    private int ultimoId = 0;

    public GestorSolicitudes(String archivo) {
        this.archivo = archivo;
        responsables.add(new Responsable("Laura Gomez", Area.ALUMNOS));
        responsables.add(new Responsable("Martin Paz", Area.BEDELIA));
        responsables.add(new Responsable("Sofia Ruiz", Area.SECRETARIA_ACADEMICA));
        responsables.add(new Responsable("Diego Lopez", Area.ALUMNOS));
        cargar();
    }

    // Registra una solicitud. Devuelve el id, o un codigo negativo si hay un error:
    // -1 legajo, -2 nombre, -3 email, -4 tipo, -5 descripcion, -6 fecha
    public int registrar(NuevaSolicitud datos) {
        int error = validar(datos);
        if (error != 0) {
            return error;
        }
        TipoSolicitud tipo = TipoSolicitud.desde(datos.tipo()).orElseThrow();
        Estudiante estudiante = obtenerOCrearEstudiante(datos);

        Solicitud solicitud = datos.urgente() ? new SolicitudUrgente() : new Solicitud();
        ultimoId++;
        solicitud.id = ultimoId;
        solicitud.legajo = estudiante.legajo;
        solicitud.tipo = tipo;
        solicitud.descripcion = datos.descripcion().trim();
        solicitud.fecha = datos.fecha().trim();
        solicitud.estado = EstadoSolicitud.PENDIENTE;
        solicitud.responsable = "";
        solicitud.prioridad = calcularPrioridad(tipo, solicitud.descripcion, datos.urgente());

        lista.add(solicitud);
        guardar();
        System.out.println("Solicitud #" + solicitud.id + " registrada. Prioridad " + solicitud.prioridad
                + ". Area: " + tipo.area());
        return solicitud.id;
    }

    private int validar(NuevaSolicitud datos) {
        String legajo = datos.legajo();
        String email = datos.email();
        String descripcion = datos.descripcion();
        String fecha = datos.fecha();
        if (legajo == null || !legajo.trim().matches("\\d{5}")) {
            return -1;
        }
        if (datos.nombre() == null || datos.nombre().trim().isEmpty()) {
            return -2;
        }
        if (email == null || email.indexOf("@") <= 0 || email.indexOf(".", email.indexOf("@")) < 0) {
            return -3;
        }
        if (TipoSolicitud.desde(datos.tipo()).isEmpty()) {
            return -4;
        }
        if (descripcion == null || descripcion.trim().isEmpty() || descripcion.length() > LARGO_MAXIMO_DESCRIPCION) {
            return -5;
        }
        if (fecha == null || !fecha.trim().matches("\\d{2}/\\d{2}/\\d{4}")) {
            return -6;
        }
        return 0;
    }

    // Si el estudiante ya existe se usa el que esta guardado
    private Estudiante obtenerOCrearEstudiante(NuevaSolicitud datos) {
        Estudiante estudiante = estudiantes.get(datos.legajo().trim());
        if (estudiante == null) {
            estudiante = new Estudiante();
            estudiante.legajo = datos.legajo().trim();
            estudiante.nombre = datos.nombre().trim();
            estudiante.email = datos.email().trim();
            estudiante.carrera = datos.carrera() == null ? "" : datos.carrera().trim();
            estudiantes.put(estudiante.legajo, estudiante);
        }
        return estudiante;
    }

    private Prioridad calcularPrioridad(TipoSolicitud tipo, String descripcion, boolean urgente) {
        if (urgente) {
            return Prioridad.ALTA;
        }
        if (tipo == TipoSolicitud.RECLAMO && descripcion.length() > LARGO_RECLAMO_EXTENSO) {
            return Prioridad.ALTA;
        }
        return tipo.prioridadBase();
    }

    private Solicitud buscarPorId(int id) {
        for (Solicitud solicitud : lista) {
            if (solicitud.id == id) {
                return solicitud;
            }
        }
        return null;
    }


    // Cambia el estado. Devuelve false si no se pudo.
    public boolean cambiarEstado(int id, String nuevoEstado) {
        Solicitud solicitud = buscarPorId(id);
        if (solicitud == null) {
            return false;
        }
        EstadoSolicitud nuevo = EstadoSolicitud.desde(nuevoEstado).orElse(null);
        if (nuevo == null || !solicitud.estado.puedePasarA(nuevo)) {
            return false;
        }
        if (solicitud instanceof SolicitudUrgente && nuevo == EstadoSolicitud.RECHAZADA) {
            return false;
        }
        solicitud.cambiarEstado(nuevo);
        guardar();
        return true;
    }

    // 0 ok, 1 no existe la solicitud, 2 no existe el responsable, 3 el area no corresponde,
    // 4 la solicitud esta cerrada
    public int asignarResponsable(int id, String nombreResponsable) {
        Solicitud solicitud = buscarPorId(id);
        if (solicitud == null) {
            return 1;
        }
        Responsable responsable = buscarResponsable(nombreResponsable);
        if (responsable == null) {
            return 2;
        }
        if (responsable.area != solicitud.tipo.area()) {
            return 3;
        }
        if (solicitud.estado.estaCerrado()) {
            return 4;
        }
        solicitud.responsable = responsable.nombre;
        if (solicitud.estado == EstadoSolicitud.PENDIENTE) {
            solicitud.estado = EstadoSolicitud.EN_PROCESO;
        }
        guardar();
        return 0;
    }

    private Responsable buscarResponsable(String nombre) {
        String buscado = nombre == null ? "" : nombre.trim();
        for (Responsable responsable : responsables) {
            if (responsable.nombre.equalsIgnoreCase(buscado)) {
                return responsable;
            }
        }
        return null;
    }

    public ArrayList<Solicitud> buscar(FiltroSolicitudes filtro) {
        ArrayList<Solicitud> resultado = new ArrayList<>();
        for (Solicitud solicitud : lista) {
            if (coincide(filtro.tipo(), solicitud.tipo.name(), true)
                    && coincide(filtro.estado(), solicitud.estado.name(), true)
                    && coincide(filtro.prioridad(), solicitud.prioridad.name(), true)
                    && coincide(filtro.legajo(), solicitud.legajo, false)) {
                resultado.add(solicitud);
            }
        }
        return resultado;
    }

    private boolean coincide(String filtro, String valor, boolean ignorarMayusculas) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return true;
        }
        String buscado = ignorarMayusculas ? filtro.trim().toUpperCase() : filtro.trim();
        return valor.equals(buscado);
    }

    public ArrayList<Solicitud> todas() {
        return lista;
    }

    public Estudiante estudiante(String legajo) {
        return estudiantes.get(legajo);
    }

    public String reporte() {
        String reporte = "=== REPORTE ===\n";
        reporte += "Total: " + lista.size() + "\n";
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            reporte += estado + ": " + contarPorEstado(estado) + "\n";
        }
        reporte += "Prioridad ALTA: " + contarPorPrioridad(Prioridad.ALTA) + " | MEDIA: "
                + contarPorPrioridad(Prioridad.MEDIA) + " | BAJA: " + contarPorPrioridad(Prioridad.BAJA) + "\n";
        reporte += "Urgentes: " + contarUrgentes() + "\n";
        reporte += "Abiertas sin responsable: " + contarAbiertasSinResponsable() + "\n";
        return reporte;
    }

    private int contarPorEstado(EstadoSolicitud estado) {
        int cantidad = 0;
        for (Solicitud solicitud : lista) {
            if (solicitud.estado == estado) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private int contarPorPrioridad(Prioridad prioridad) {
        int cantidad = 0;
        for (Solicitud solicitud : lista) {
            if (solicitud.prioridad == prioridad) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private int contarUrgentes() {
        int cantidad = 0;
        for (Solicitud solicitud : lista) {
            if (solicitud instanceof SolicitudUrgente) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private int contarAbiertasSinResponsable() {
        int cantidad = 0;
        for (Solicitud solicitud : lista) {
            if (solicitud.responsable.equals("") && !solicitud.estado.estaCerrado()) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private void guardar() {
        try {
            FileWriter fw = new FileWriter(archivo);
            fw.write("id;legajo;nombre;email;carrera;tipo;descripcion;fecha;estado;prioridad;responsable;urgente\n");
            for (Solicitud s : lista) {
                Estudiante e = estudiantes.get(s.legajo);
                fw.write(s.id + ";" + s.legajo + ";" + e.nombre + ";" + e.email + ";" + e.carrera + ";" + s.tipo
                        + ";" + s.descripcion + ";" + s.fecha + ";" + s.estado + ";" + s.prioridad + ";"
                        + s.responsable + ";" + (s instanceof SolicitudUrgente) + "\n");
            }
            fw.close();
        } catch (Exception ex) {
        }
    }

    private void cargar() {
        File f = new File(archivo);
        if (!f.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea = br.readLine();
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";", -1);
                Solicitud s;
                if (p[11].equals("true")) {
                    s = new SolicitudUrgente();
                } else {
                    s = new Solicitud();
                }
                s.id = Integer.parseInt(p[0]);
                s.legajo = p[1];
                s.tipo = TipoSolicitud.valueOf(p[5]);
                s.descripcion = p[6];
                s.fecha = p[7];
                s.estado = EstadoSolicitud.valueOf(p[8]);
                s.prioridad = Prioridad.valueOf(p[9]);
                s.responsable = p[10];
                if (!estudiantes.containsKey(p[1])) {
                    Estudiante e = new Estudiante();
                    e.legajo = p[1];
                    e.nombre = p[2];
                    e.email = p[3];
                    e.carrera = p[4];
                    estudiantes.put(p[1], e);
                }
                lista.add(s);
                if (s.id > ultimoId) {
                    ultimoId = s.id;
                }
            }
            br.close();
        } catch (Exception ex) {
        }
    }
}
