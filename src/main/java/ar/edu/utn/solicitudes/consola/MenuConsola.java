package ar.edu.utn.solicitudes.consola;

import ar.edu.utn.solicitudes.aplicacion.FiltroSolicitudes;
import ar.edu.utn.solicitudes.aplicacion.NuevaSolicitud;
import ar.edu.utn.solicitudes.aplicacion.PersistenciaException;
import ar.edu.utn.solicitudes.aplicacion.SolicitudService;
import ar.edu.utn.solicitudes.dominio.DatosInvalidosException;
import ar.edu.utn.solicitudes.dominio.Estudiante;
import ar.edu.utn.solicitudes.dominio.Solicitud;
import ar.edu.utn.solicitudes.dominio.SolicitudException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** Interfaz de usuario por consola. Lee la entrada, llama al servicio y muestra los resultados. */
public class MenuConsola {

    private static final int SALIR = 0;

    private final SolicitudService servicio;
    private final Scanner entrada;
    private final PrintStream salida;

    public MenuConsola(SolicitudService servicio, InputStream entrada, PrintStream salida) {
        this.servicio = servicio;
        this.entrada = new Scanner(entrada);
        this.salida = salida;
    }

    public void ejecutar() {
        int opcion = -1;
        while (opcion != SALIR) {
            mostrarMenu();
            if (!entrada.hasNextLine()) {
                return;
            }
            Optional<Integer> elegida = aNumero(entrada.nextLine());
            if (elegida.isEmpty()) {
                salida.println("Opcion invalida");
                continue;
            }
            opcion = elegida.get();
            atender(opcion);
        }
    }

    private void atender(int opcion) {
        try {
            switch (opcion) {
                case 1 -> registrar();
                case 2 -> cambiarEstado();
                case 3 -> asignarResponsable();
                case 4 -> buscar();
                case 5 -> salida.print(servicio.reporte().comoTexto());
                case 6 -> listar(servicio.todas(), "No hay solicitudes registradas");
                case SALIR -> salida.println("Hasta luego");
                default -> salida.println("Opcion invalida");
            }
        } catch (PersistenciaException e) {
            salida.println("Error: " + e.getMessage());
        }
    }

    private void mostrarMenu() {
        salida.println();
        salida.println("=== SOLICITUDES ACADEMICAS ===");
        salida.println("1. Registrar solicitud");
        salida.println("2. Cambiar estado");
        salida.println("3. Asignar responsable");
        salida.println("4. Buscar solicitudes");
        salida.println("5. Reporte");
        salida.println("6. Listar todas");
        salida.println("0. Salir");
        salida.print("Opcion: ");
    }

    private void registrar() {
        NuevaSolicitud datos = new NuevaSolicitud(
                preguntar("Legajo: "),
                preguntar("Nombre: "),
                preguntar("Email: "),
                preguntar("Carrera: "),
                preguntar("Tipo (CONSTANCIA, EXAMEN, EQUIVALENCIA, RECLAMO): "),
                preguntar("Descripcion: "),
                preguntar("Fecha (dd/mm/aaaa): "),
                preguntar("Urgente (s/n): ").trim().equalsIgnoreCase("s"));
        try {
            Solicitud solicitud = servicio.registrar(datos);
            salida.println("Solicitud #" + solicitud.id() + " registrada. Prioridad " + solicitud.prioridad()
                    + ". Area: " + solicitud.tipo().area());
        } catch (DatosInvalidosException e) {
            salida.println("Error: " + e.getMessage());
        }
    }

    private void cambiarEstado() {
        Optional<Integer> id = leerId();
        if (id.isEmpty()) {
            return;
        }
        String estado = preguntar("Nuevo estado (EN_PROCESO, RESUELTA, RECHAZADA): ");
        try {
            servicio.cambiarEstado(id.get(), estado);
            salida.println("Estado actualizado");
        } catch (SolicitudException e) {
            salida.println("No se pudo cambiar el estado");
        }
    }

    private void asignarResponsable() {
        Optional<Integer> id = leerId();
        if (id.isEmpty()) {
            return;
        }
        String responsable = preguntar("Responsable: ");
        try {
            servicio.asignarResponsable(id.get(), responsable);
            salida.println("Responsable asignado");
        } catch (SolicitudException e) {
            salida.println("Error: " + e.getMessage());
        }
    }

    private void buscar() {
        FiltroSolicitudes filtro = new FiltroSolicitudes(
                preguntar("Tipo (vacio = todos): "),
                preguntar("Estado (vacio = todos): "),
                preguntar("Prioridad (vacio = todas): "),
                preguntar("Legajo (vacio = todos): "));
        listar(servicio.buscar(filtro), "No hay solicitudes que coincidan");
    }

    private void listar(List<Solicitud> solicitudes, String mensajeSiNoHay) {
        if (solicitudes.isEmpty()) {
            salida.println(mensajeSiNoHay);
        }
        for (Solicitud solicitud : solicitudes) {
            salida.println(comoLinea(solicitud));
        }
    }

    private String comoLinea(Solicitud solicitud) {
        Estudiante estudiante = servicio.estudiante(solicitud.legajo());
        return "#" + solicitud.id() + " [" + solicitud.prioridad() + "] " + solicitud.tipo() + " - "
                + solicitud.estado() + " - " + solicitud.legajo() + " " + estudiante.nombre() + " - "
                + solicitud.responsable().orElse("Sin asignar");
    }

    private Optional<Integer> leerId() {
        Optional<Integer> id = aNumero(preguntar("Id de la solicitud: "));
        if (id.isEmpty()) {
            salida.println("Id invalido");
        }
        return id;
    }

    private String preguntar(String pregunta) {
        salida.print(pregunta);
        return entrada.nextLine();
    }

    private static Optional<Integer> aNumero(String texto) {
        try {
            return Optional.of(Integer.parseInt(texto.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
