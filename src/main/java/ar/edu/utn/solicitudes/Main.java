package ar.edu.utn.solicitudes;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String archivo = args.length > 0 ? args[0] : "solicitudes.csv";
        GestorSolicitudes g = new GestorSolicitudes(archivo);
        Scanner sc = new Scanner(System.in);
        int op = -1;
        while (op != 0) {
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
            if (!sc.hasNextLine()) {
                break;
            }
            String linea = sc.nextLine();
            try {
                op = Integer.parseInt(linea.trim());
            } catch (Exception e) {
                System.out.println("Opcion invalida");
                continue;
            }
            if (op == 1) {
                System.out.print("Legajo: ");
                String legajo = sc.nextLine();
                System.out.print("Nombre: ");
                String nombre = sc.nextLine();
                System.out.print("Email: ");
                String email = sc.nextLine();
                System.out.print("Carrera: ");
                String carrera = sc.nextLine();
                System.out.print("Tipo (CONSTANCIA, EXAMEN, EQUIVALENCIA, RECLAMO): ");
                String tipo = sc.nextLine();
                System.out.print("Descripcion: ");
                String desc = sc.nextLine();
                System.out.print("Fecha (dd/mm/aaaa): ");
                String fecha = sc.nextLine();
                System.out.print("Urgente (s/n): ");
                String urg = sc.nextLine();
                int r = g.registrar(legajo, nombre, email, carrera, tipo, desc, fecha, urg.trim().equalsIgnoreCase("s"));
                if (r == -1) {
                    System.out.println("Error: el legajo debe tener 5 digitos");
                } else if (r == -2) {
                    System.out.println("Error: el nombre es obligatorio");
                } else if (r == -3) {
                    System.out.println("Error: email invalido");
                } else if (r == -4) {
                    System.out.println("Error: tipo de solicitud invalido");
                } else if (r == -5) {
                    System.out.println("Error: la descripcion es obligatoria y no puede superar los 300 caracteres");
                } else if (r == -6) {
                    System.out.println("Error: la fecha debe tener el formato dd/mm/aaaa");
                }
            } else if (op == 2) {
                System.out.print("Id de la solicitud: ");
                int id;
                try {
                    id = Integer.parseInt(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Id invalido");
                    continue;
                }
                System.out.print("Nuevo estado (EN_PROCESO, RESUELTA, RECHAZADA): ");
                String est = sc.nextLine();
                if (g.cambiarEstado(id, est)) {
                    System.out.println("Estado actualizado");
                } else {
                    System.out.println("No se pudo cambiar el estado");
                }
            } else if (op == 3) {
                System.out.print("Id de la solicitud: ");
                int id;
                try {
                    id = Integer.parseInt(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Id invalido");
                    continue;
                }
                System.out.print("Responsable: ");
                String resp = sc.nextLine();
                int r = g.asignarResponsable(id, resp);
                if (r == 0) {
                    System.out.println("Responsable asignado");
                } else if (r == 1) {
                    System.out.println("Error: no existe la solicitud " + id);
                } else if (r == 2) {
                    System.out.println("Error: no existe el responsable");
                } else if (r == 3) {
                    System.out.println("Error: el responsable no pertenece al area de la solicitud");
                } else if (r == 4) {
                    System.out.println("Error: la solicitud esta cerrada");
                }
            } else if (op == 4) {
                System.out.print("Tipo (vacio = todos): ");
                String tipo = sc.nextLine();
                System.out.print("Estado (vacio = todos): ");
                String est = sc.nextLine();
                System.out.print("Prioridad (vacio = todas): ");
                String pri = sc.nextLine();
                System.out.print("Legajo (vacio = todos): ");
                String leg = sc.nextLine();
                ArrayList<Solicitud> lst = g.buscar(tipo, est, pri, leg);
                if (lst.size() == 0) {
                    System.out.println("No hay solicitudes que coincidan");
                }
                for (Solicitud s : lst) {
                    Estudiante e = g.estudiante(s.legajo);
                    System.out.println("#" + s.id + " [" + s.prioridad + "] " + s.tipo + " - " + s.estado + " - "
                            + s.legajo + " " + e.nombre + " - "
                            + (s.responsable.equals("") ? "Sin asignar" : s.responsable));
                }
            } else if (op == 5) {
                System.out.print(g.reporte());
            } else if (op == 6) {
                ArrayList<Solicitud> lst = g.todas();
                if (lst.size() == 0) {
                    System.out.println("No hay solicitudes registradas");
                }
                for (Solicitud s : lst) {
                    Estudiante e = g.estudiante(s.legajo);
                    System.out.println("#" + s.id + " [" + s.prioridad + "] " + s.tipo + " - " + s.estado + " - "
                            + s.legajo + " " + e.nombre + " - "
                            + (s.responsable.equals("") ? "Sin asignar" : s.responsable));
                }
            } else if (op == 0) {
                System.out.println("Hasta luego");
            } else {
                System.out.println("Opcion invalida");
            }
        }
    }
}
