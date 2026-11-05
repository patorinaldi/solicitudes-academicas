package ar.edu.utn.solicitudes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class GestorSolicitudes {

    private String archivo;
    private ArrayList<Solicitud> lista = new ArrayList<>();
    private HashMap<String, Estudiante> estudiantes = new HashMap<>();
    private ArrayList<Responsable> responsables = new ArrayList<>();
    private int ultimoId = 0;

    public GestorSolicitudes(String archivo) {
        this.archivo = archivo;
        responsables.add(new Responsable("Laura Gomez", "ALUMNOS"));
        responsables.add(new Responsable("Martin Paz", "BEDELIA"));
        responsables.add(new Responsable("Sofia Ruiz", "SECRETARIA_ACADEMICA"));
        responsables.add(new Responsable("Diego Lopez", "ALUMNOS"));
        cargar();
    }

    // Registra una solicitud. Devuelve el id, o un codigo negativo si hay un error:
    // -1 legajo, -2 nombre, -3 email, -4 tipo, -5 descripcion, -6 fecha
    public int registrar(String legajo, String nombre, String email, String carrera, String tipo,
                         String descripcion, String fecha, boolean urgente) {
        // validaciones
        if (legajo == null || !legajo.trim().matches("\\d{5}")) {
            return -1;
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            return -2;
        }
        if (email == null || email.indexOf("@") <= 0 || email.indexOf(".", email.indexOf("@")) < 0) {
            return -3;
        }
        String t = tipo == null ? "" : tipo.trim().toUpperCase();
        if (!t.equals("CONSTANCIA") && !t.equals("EXAMEN") && !t.equals("EQUIVALENCIA") && !t.equals("RECLAMO")) {
            return -4;
        }
        if (descripcion == null || descripcion.trim().isEmpty() || descripcion.length() > 300) {
            return -5;
        }
        if (fecha == null || !fecha.trim().matches("\\d{2}/\\d{2}/\\d{4}")) {
            return -6;
        }

        // estudiante: si ya existe se usa el que esta guardado
        Estudiante e = estudiantes.get(legajo.trim());
        if (e == null) {
            e = new Estudiante();
            e.legajo = legajo.trim();
            e.nombre = nombre.trim();
            e.email = email.trim();
            e.carrera = carrera == null ? "" : carrera.trim();
            estudiantes.put(e.legajo, e);
        }

        Solicitud s;
        if (urgente) {
            s = new SolicitudUrgente();
        } else {
            s = new Solicitud();
        }
        ultimoId++;
        s.id = ultimoId;
        s.legajo = e.legajo;
        s.tipo = t;
        s.descripcion = descripcion.trim();
        s.fecha = fecha.trim();
        s.estado = "PENDIENTE";
        s.responsable = "";

        // prioridad
        if (urgente) {
            s.prioridad = "ALTA";
        } else if (t.equals("EXAMEN")) {
            s.prioridad = "ALTA";
        } else if (t.equals("EQUIVALENCIA")) {
            s.prioridad = "MEDIA";
        } else if (t.equals("RECLAMO")) {
            if (s.descripcion.length() > 200) {
                s.prioridad = "ALTA";
            } else {
                s.prioridad = "MEDIA";
            }
        } else {
            s.prioridad = "BAJA";
        }

        lista.add(s);
        guardar();

        String area;
        if (t.equals("EXAMEN")) {
            area = "BEDELIA";
        } else if (t.equals("EQUIVALENCIA")) {
            area = "SECRETARIA_ACADEMICA";
        } else {
            area = "ALUMNOS";
        }
        System.out.println("Solicitud #" + s.id + " registrada. Prioridad " + s.prioridad + ". Area: " + area);
        return s.id;
    }

    // Cambia el estado. Devuelve false si no se pudo.
    public boolean cambiarEstado(int id, String nuevoEstado) {
        Solicitud s = null;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id == id) {
                s = lista.get(i);
            }
        }
        if (s == null) {
            return false;
        }
        String n = nuevoEstado == null ? "" : nuevoEstado.trim().toUpperCase();
        if (s.estado.equals("PENDIENTE")) {
            if (!n.equals("EN_PROCESO") && !n.equals("RECHAZADA")) {
                return false;
            }
        } else if (s.estado.equals("EN_PROCESO")) {
            if (!n.equals("RESUELTA") && !n.equals("RECHAZADA")) {
                return false;
            }
        } else {
            return false;
        }
        if (s instanceof SolicitudUrgente && n.equals("RECHAZADA")) {
            return false;
        }
        s.cambiarEstado(n);
        guardar();
        return true;
    }

    // 0 ok, 1 no existe la solicitud, 2 no existe el responsable, 3 el area no corresponde,
    // 4 la solicitud esta cerrada
    public int asignarResponsable(int id, String nombreResponsable) {
        Solicitud s = null;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id == id) {
                s = lista.get(i);
            }
        }
        if (s == null) {
            return 1;
        }
        Responsable r = null;
        for (Responsable aux : responsables) {
            if (aux.nombre.equalsIgnoreCase(nombreResponsable == null ? "" : nombreResponsable.trim())) {
                r = aux;
            }
        }
        if (r == null) {
            return 2;
        }
        String area;
        if (s.tipo.equals("EXAMEN")) {
            area = "BEDELIA";
        } else if (s.tipo.equals("EQUIVALENCIA")) {
            area = "SECRETARIA_ACADEMICA";
        } else {
            area = "ALUMNOS";
        }
        if (!r.area.equals(area)) {
            return 3;
        }
        if (s.estado.equals("RESUELTA") || s.estado.equals("RECHAZADA")) {
            return 4;
        }
        s.responsable = r.nombre;
        if (s.estado.equals("PENDIENTE")) {
            s.estado = "EN_PROCESO";
        }
        guardar();
        return 0;
    }

    // Filtros vacios = sin filtro
    public ArrayList<Solicitud> buscar(String tipo, String estado, String prioridad, String legajo) {
        ArrayList<Solicitud> res = new ArrayList<>();
        for (Solicitud s : lista) {
            boolean ok = true;
            if (tipo != null && !tipo.trim().isEmpty()) {
                if (!s.tipo.equals(tipo.trim().toUpperCase())) {
                    ok = false;
                }
            }
            if (estado != null && !estado.trim().isEmpty()) {
                if (!s.estado.equals(estado.trim().toUpperCase())) {
                    ok = false;
                }
            }
            if (prioridad != null && !prioridad.trim().isEmpty()) {
                if (!s.prioridad.equals(prioridad.trim().toUpperCase())) {
                    ok = false;
                }
            }
            if (legajo != null && !legajo.trim().isEmpty()) {
                if (!s.legajo.equals(legajo.trim())) {
                    ok = false;
                }
            }
            if (ok) {
                res.add(s);
            }
        }
        return res;
    }

    public ArrayList<Solicitud> todas() {
        return lista;
    }

    public Estudiante estudiante(String legajo) {
        return estudiantes.get(legajo);
    }

    public String reporte() {
        String r = "=== REPORTE ===\n";
        r += "Total: " + lista.size() + "\n";
        int c = 0;
        for (Solicitud s : lista) {
            if (s.estado.equals("PENDIENTE")) {
                c++;
            }
        }
        r += "PENDIENTE: " + c + "\n";
        c = 0;
        for (Solicitud s : lista) {
            if (s.estado.equals("EN_PROCESO")) {
                c++;
            }
        }
        r += "EN_PROCESO: " + c + "\n";
        c = 0;
        for (Solicitud s : lista) {
            if (s.estado.equals("RESUELTA")) {
                c++;
            }
        }
        r += "RESUELTA: " + c + "\n";
        c = 0;
        for (Solicitud s : lista) {
            if (s.estado.equals("RECHAZADA")) {
                c++;
            }
        }
        r += "RECHAZADA: " + c + "\n";
        int a = 0, m = 0, b = 0;
        for (Solicitud s : lista) {
            if (s.prioridad.equals("ALTA")) {
                a++;
            } else if (s.prioridad.equals("MEDIA")) {
                m++;
            } else {
                b++;
            }
        }
        r += "Prioridad ALTA: " + a + " | MEDIA: " + m + " | BAJA: " + b + "\n";
        int u = 0;
        int sin = 0;
        for (Solicitud s : lista) {
            if (s instanceof SolicitudUrgente) {
                u++;
            }
            if (s.responsable.equals("") && !s.estado.equals("RESUELTA") && !s.estado.equals("RECHAZADA")) {
                sin++;
            }
        }
        r += "Urgentes: " + u + "\n";
        r += "Abiertas sin responsable: " + sin + "\n";
        return r;
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
                s.tipo = p[5];
                s.descripcion = p[6];
                s.fecha = p[7];
                s.estado = p[8];
                s.prioridad = p[9];
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
