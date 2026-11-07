package ar.edu.utn.solicitudes;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String archivo = args.length > 0 ? args[0] : "solicitudes.csv";
        GestorSolicitudes gestor = new GestorSolicitudes(archivo);
        Scanner entrada = new Scanner(System.in);
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            if (!entrada.hasNextLine()) {
                break;
            }
            try {
                opcion = Integer.parseInt(entrada.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Opcion invalida");
                continue;
            }
            switch (opcion) {
                case 1 -> registrar(entrada, gestor);
                case 2 -> cambiarEstado(entrada, gestor);
                case 3 -> asignarResponsable(entrada, gestor);
                case 4 -> buscar(entrada, gestor);
                case 5 -> System.out.print(gestor.reporte());
                case 6 -> listar(gestor, gestor.todas(), "No hay solicitudes registradas");
                case 0 -> System.out.println("Hasta luego");
                default -> System.out.println("Opcion invalida");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("=== SOLICITUDES ACADEMICAS ===");
        System.out.println("1. Registrar solicitud");
        System.out.println("2. Cambiar estado");
        System.out.println("3. Asignar responsable");
        System.out.println("4. Buscar solicitudes");
        System.out.println("5. Reporte");
        System.out.println("6. Listar todas");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
    }

    private static String preguntar(Scanner entrada, String pregunta) {
        System.out.print(pregunta);
        return entrada.nextLine();
    }

    private static void registrar(Scanner entrada, GestorSolicitudes gestor) {
        String legajo = preguntar(entrada, "Legajo: ");
        String nombre = preguntar(entrada, "Nombre: ");
        String email = preguntar(entrada, "Email: ");
        String carrera = preguntar(entrada, "Carrera: ");
        String tipo = preguntar(entrada, "Tipo (CONSTANCIA, EXAMEN, EQUIVALENCIA, RECLAMO): ");
        String descripcion = preguntar(entrada, "Descripcion: ");
        String fecha = preguntar(entrada, "Fecha (dd/mm/aaaa): ");
        boolean urgente = preguntar(entrada, "Urgente (s/n): ").trim().equalsIgnoreCase("s");
        int resultado = gestor.registrar(
                new NuevaSolicitud(legajo, nombre, email, carrera, tipo, descripcion, fecha, urgente));
        if (resultado == -1) {
            System.out.println("Error: el legajo debe tener 5 digitos");
        } else if (resultado == -2) {
            System.out.println("Error: el nombre es obligatorio");
        } else if (resultado == -3) {
            System.out.println("Error: email invalido");
        } else if (resultado == -4) {
            System.out.println("Error: tipo de solicitud invalido");
        } else if (resultado == -5) {
            System.out.println("Error: la descripcion es obligatoria y no puede superar los 300 caracteres");
        } else if (resultado == -6) {
            System.out.println("Error: la fecha debe tener el formato dd/mm/aaaa");
        }
    }

    // Devuelve null si el id no es un numero
    private static Integer leerId(Scanner entrada) {
        String texto = preguntar(entrada, "Id de la solicitud: ");
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            System.out.println("Id invalido");
            return null;
        }
    }

    private static void cambiarEstado(Scanner entrada, GestorSolicitudes gestor) {
        Integer id = leerId(entrada);
        if (id == null) {
            return;
        }
        String estado = preguntar(entrada, "Nuevo estado (EN_PROCESO, RESUELTA, RECHAZADA): ");
        if (gestor.cambiarEstado(id, estado)) {
            System.out.println("Estado actualizado");
        } else {
            System.out.println("No se pudo cambiar el estado");
        }
    }

    private static void asignarResponsable(Scanner entrada, GestorSolicitudes gestor) {
        Integer id = leerId(entrada);
        if (id == null) {
            return;
        }
        String responsable = preguntar(entrada, "Responsable: ");
        int resultado = gestor.asignarResponsable(id, responsable);
        if (resultado == 0) {
            System.out.println("Responsable asignado");
        } else if (resultado == 1) {
            System.out.println("Error: no existe la solicitud " + id);
        } else if (resultado == 2) {
            System.out.println("Error: no existe el responsable");
        } else if (resultado == 3) {
            System.out.println("Error: el responsable no pertenece al area de la solicitud");
        } else if (resultado == 4) {
            System.out.println("Error: la solicitud esta cerrada");
        }
    }

    private static void buscar(Scanner entrada, GestorSolicitudes gestor) {
        String tipo = preguntar(entrada, "Tipo (vacio = todos): ");
        String estado = preguntar(entrada, "Estado (vacio = todos): ");
        String prioridad = preguntar(entrada, "Prioridad (vacio = todas): ");
        String legajo = preguntar(entrada, "Legajo (vacio = todos): ");
        FiltroSolicitudes filtro = new FiltroSolicitudes(tipo, estado, prioridad, legajo);
        listar(gestor, gestor.buscar(filtro), "No hay solicitudes que coincidan");
    }

    private static void listar(GestorSolicitudes gestor, List<Solicitud> solicitudes, String mensajeSiVacia) {
        if (solicitudes.isEmpty()) {
            System.out.println(mensajeSiVacia);
        }
        for (Solicitud solicitud : solicitudes) {
            Estudiante estudiante = gestor.estudiante(solicitud.legajo);
            System.out.println("#" + solicitud.id + " [" + solicitud.prioridad + "] " + solicitud.tipo + " - "
                    + solicitud.estado + " - " + solicitud.legajo + " " + estudiante.nombre + " - "
                    + (solicitud.responsable.equals("") ? "Sin asignar" : solicitud.responsable));
        }
    }
}
