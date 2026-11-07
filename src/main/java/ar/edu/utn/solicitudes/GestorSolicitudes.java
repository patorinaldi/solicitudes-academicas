package ar.edu.utn.solicitudes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class GestorSolicitudes {

    private static final int LARGO_MAXIMO_DESCRIPCION = 300;

    private String archivo;
    private ArrayList<Solicitud> lista = new ArrayList<>();
    private HashMap<String, Estudiante> estudiantes = new HashMap<>();
    private ArrayList<Responsable> responsables = new ArrayList<>();
    private int ultimoId = 0;
    private final CalculadoraPrioridad calculadoraPrioridad = new CalculadoraPrioridad();

    public GestorSolicitudes(String archivo) {
        this.archivo = archivo;
        responsables.add(new Responsable("Laura Gomez", Area.ALUMNOS));
        responsables.add(new Responsable("Martin Paz", Area.BEDELIA));
        responsables.add(new Responsable("Sofia Ruiz", Area.SECRETARIA_ACADEMICA));
        responsables.add(new Responsable("Diego Lopez", Area.ALUMNOS));
        cargar();
    }

    /** Registra una solicitud y devuelve su id. Lanza DatosInvalidosException si un dato no es válido. */
    public int registrar(NuevaSolicitud datos) {
        validar(datos);
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
        solicitud.prioridad = calculadoraPrioridad.calcular(tipo, solicitud.descripcion, datos.urgente());

        lista.add(solicitud);
        guardar();
        System.out.println("Solicitud #" + solicitud.id + " registrada. Prioridad " + solicitud.prioridad
                + ". Area: " + tipo.area());
        return solicitud.id;
    }

    private void validar(NuevaSolicitud datos) {
        String legajo = datos.legajo();
        String email = datos.email();
        String descripcion = datos.descripcion();
        String fecha = datos.fecha();
        if (legajo == null || !legajo.trim().matches("\\d{5}")) {
            throw new DatosInvalidosException("el legajo debe tener 5 digitos");
        }
        if (datos.nombre() == null || datos.nombre().trim().isEmpty()) {
            throw new DatosInvalidosException("el nombre es obligatorio");
        }
        if (email == null || email.indexOf("@") <= 0 || email.indexOf(".", email.indexOf("@")) < 0) {
            throw new DatosInvalidosException("email invalido");
        }
        if (TipoSolicitud.desde(datos.tipo()).isEmpty()) {
            throw new DatosInvalidosException("tipo de solicitud invalido");
        }
        if (descripcion == null || descripcion.trim().isEmpty() || descripcion.length() > LARGO_MAXIMO_DESCRIPCION) {
            throw new DatosInvalidosException("la descripcion es obligatoria y no puede superar los 300 caracteres");
        }
        if (fecha == null || !fecha.trim().matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new DatosInvalidosException("la fecha debe tener el formato dd/mm/aaaa");
        }
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

    private Solicitud buscarPorId(int id) {
        for (Solicitud solicitud : lista) {
            if (solicitud.id == id) {
                return solicitud;
            }
        }
        throw new SolicitudInexistenteException(id);
    }


    public void cambiarEstado(int id, String nuevoEstado) {
        Solicitud solicitud = buscarPorId(id);
        EstadoSolicitud nuevo = EstadoSolicitud.desde(nuevoEstado)
                .orElseThrow(() -> new TransicionInvalidaException("estado desconocido: " + nuevoEstado));
        if (!solicitud.estado.puedePasarA(nuevo)) {
            throw new TransicionInvalidaException("no se puede pasar de " + solicitud.estado + " a " + nuevo);
        }
        if (solicitud instanceof SolicitudUrgente && nuevo == EstadoSolicitud.RECHAZADA) {
            throw new TransicionInvalidaException("una solicitud urgente no se puede rechazar");
        }
        solicitud.cambiarEstado(nuevo);
        guardar();
    }

    public void asignarResponsable(int id, String nombreResponsable) {
        Solicitud solicitud = buscarPorId(id);
        Responsable responsable = buscarResponsable(nombreResponsable);
        if (responsable.area != solicitud.tipo.area()) {
            throw new AreaIncorrectaException();
        }
        if (solicitud.estado.estaCerrado()) {
            throw new SolicitudCerradaException();
        }
        solicitud.responsable = responsable.nombre;
        if (solicitud.estado == EstadoSolicitud.PENDIENTE) {
            solicitud.estado = EstadoSolicitud.EN_PROCESO;
        }
        guardar();
    }

    private Responsable buscarResponsable(String nombre) {
        String buscado = nombre == null ? "" : nombre.trim();
        for (Responsable responsable : responsables) {
            if (responsable.nombre.equalsIgnoreCase(buscado)) {
                return responsable;
            }
        }
        throw new ResponsableInexistenteException();
    }

    public ArrayList<Solicitud> buscar(FiltroSolicitudes filtro) {
        ArrayList<Solicitud> resultado = new ArrayList<>();
        for (Solicitud solicitud : lista) {
            if (filtro.admite(solicitud)) {
                resultado.add(solicitud);
            }
        }
        return resultado;
    }

    public ArrayList<Solicitud> todas() {
        return lista;
    }

    public Estudiante estudiante(String legajo) {
        return estudiantes.get(legajo);
    }

    public Reporte reporte() {
        return Reporte.de(lista);
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
