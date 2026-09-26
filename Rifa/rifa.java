import java.io.*;
import java.util.Scanner;

public class rifa {

    static final String ARCHIVO = "rifa.txt";
    static final int TOTAL_NUMEROS = 100;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n========== RIFA ==========");
            System.out.println("1. Registrar participante");
            System.out.println("2. Ver participantes");
            System.out.println("3. Buscar número");
            System.out.println("4. Ver números disponibles");
            System.out.println("5. Ver cantidad");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = sc.nextLine();

            switch (opcion) {

                case "1":
                    registrar(sc);
                    break;

                case "2":
                    verParticipantes();
                    break;

                case "3":
                    buscarNumero(sc);
                    break;

                case "4":
                    verDisponibles();
                    break;

                case "5":
                    mostrarCantidad();
                    break;

                case "6":
                    System.out.println("Programa finalizado.");
                    sc.close();
                    return;

                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    public static void registrar(Scanner sc) {

        System.out.println("\n--- REGISTRAR PARTICIPANTE ---");

        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            return;
        }

        System.out.print("Número (000-099): ");
        String numero = sc.nextLine().trim();

        if (!numero.matches("\\d{3}")) {
            System.out.println("El número debe tener exactamente 3 dígitos.");
            System.out.println("Ejemplos: 000, 001, 025, 099.");
            return;
        }

        int numeroEntero = Integer.parseInt(numero);

        if (numeroEntero < 0 || numeroEntero > 99) {
            System.out.println("El número debe estar entre 000 y 099.");
            return;
        }

        if (numeroExiste(numero)) {
            System.out.println("El número " + numero + " ya está ocupado.");
            return;
        }

        try {

            FileWriter fw = new FileWriter(ARCHIVO, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(nombre + "|" + numero);
            bw.newLine();

            bw.close();

            System.out.println("\nParticipante registrado correctamente.");
            System.out.println("Nombre: " + nombre);
            System.out.println("Número: " + numero);

            mostrarCantidad();

        } catch (IOException e) {

            System.out.println("Error al guardar:");
            System.out.println(e.getMessage());
        }
    }

    public static void verParticipantes() {

        System.out.println("\n--- PARTICIPANTES ---");

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            System.out.println("Todavía no hay participantes registrados.");
            mostrarCantidad();
            return;
        }

        String[] nombres = new String[TOTAL_NUMEROS];
        boolean[] ocupados = new boolean[TOTAL_NUMEROS];

        try {

            FileReader fr = new FileReader(ARCHIVO);
            BufferedReader br = new BufferedReader(fr);

            String linea;

            while ((linea = br.readLine()) != null) {

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split("\\|");

                if (datos.length >= 2) {

                    String nombre = datos[0].trim();
                    String numeroTexto = datos[1].trim();

                    try {

                        int numero = Integer.parseInt(numeroTexto);

                        if (numero >= 0 && numero < TOTAL_NUMEROS) {

                            nombres[numero] = nombre;
                            ocupados[numero] = true;
                        }

                    } catch (NumberFormatException e) {
                        // Ignorar registros inválidos
                    }
                }
            }

            br.close();

            boolean hayParticipantes = false;

            // Mostrar siempre en orden 000 -> 099
            for (int i = 0; i < TOTAL_NUMEROS; i++) {

                if (ocupados[i]) {

                    System.out.println("-------------------------");
                    System.out.printf("Número: %03d%n", i);
                    System.out.println("Nombre: " + nombres[i]);

                    hayParticipantes = true;
                }
            }

            if (!hayParticipantes) {
                System.out.println("No hay participantes registrados.");
            }

            mostrarCantidad();

        } catch (IOException e) {

            System.out.println("Error al leer el archivo:");
            System.out.println(e.getMessage());
        }
    }

    public static void buscarNumero(Scanner sc) {

        System.out.println("\n--- BUSCAR NÚMERO ---");

        System.out.print("Ingrese el número (000-099): ");
        String numeroBuscado = sc.nextLine().trim();

        if (!numeroBuscado.matches("\\d{3}")) {
            System.out.println("El número debe tener exactamente 3 dígitos.");
            return;
        }

        int numeroEntero = Integer.parseInt(numeroBuscado);

        if (numeroEntero < 0 || numeroEntero > 99) {
            System.out.println("El número debe estar entre 000 y 099.");
            return;
        }

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            System.out.println(
                "\nEl número " + numeroBuscado + " está DISPONIBLE."
            );
            return;
        }

        try {

            FileReader fr = new FileReader(ARCHIVO);
            BufferedReader br = new BufferedReader(fr);

            String linea;
            boolean encontrado = false;

            while ((linea = br.readLine()) != null) {

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split("\\|");

                if (datos.length >= 2) {

                    String nombre = datos[0].trim();
                    String numero = datos[1].trim();

                    if (numero.equals(numeroBuscado)) {

                        System.out.println("\n=========================");
                        System.out.println("PARTICIPANTE ENCONTRADO");
                        System.out.println("=========================");
                        System.out.println("Nombre: " + nombre);
                        System.out.println("Número: " + numero);

                        encontrado = true;
                        break;
                    }
                }
            }

            br.close();

            if (!encontrado) {
                System.out.println(
                    "\nEl número " + numeroBuscado + " está DISPONIBLE."
                );
            }

        } catch (IOException e) {

            System.out.println("Error al buscar:");
            System.out.println(e.getMessage());
        }
    }

    public static void verDisponibles() {

        System.out.println("\n--- NÚMEROS DISPONIBLES ---");

        boolean[] ocupados = obtenerNumerosOcupados();

        int cantidadDisponibles = 0;

        for (int i = 0; i < TOTAL_NUMEROS; i++) {

            if (!ocupados[i]) {

                System.out.printf("%03d ", i);

                cantidadDisponibles++;

                if (cantidadDisponibles % 10 == 0) {
                    System.out.println();
                }
            }
        }

        if (cantidadDisponibles == 0) {

            System.out.println("No quedan números disponibles.");

        } else {

            System.out.println();
            System.out.println(
                "Total disponibles: " + cantidadDisponibles
            );
        }
    }

    public static boolean numeroExiste(String numeroBuscado) {

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            return false;
        }

        try {

            FileReader fr = new FileReader(ARCHIVO);
            BufferedReader br = new BufferedReader(fr);

            String linea;

            while ((linea = br.readLine()) != null) {

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split("\\|");

                if (datos.length >= 2) {

                    String numero = datos[1].trim();

                    if (numero.equals(numeroBuscado)) {
                        br.close();
                        return true;
                    }
                }
            }

            br.close();

        } catch (IOException e) {

            System.out.println("Error al comprobar el número:");
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static boolean[] obtenerNumerosOcupados() {

        boolean[] ocupados = new boolean[TOTAL_NUMEROS];

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            return ocupados;
        }

        try {

            FileReader fr = new FileReader(ARCHIVO);
            BufferedReader br = new BufferedReader(fr);

            String linea;

            while ((linea = br.readLine()) != null) {

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split("\\|");

                if (datos.length >= 2) {

                    try {

                        int numero = Integer.parseInt(datos[1].trim());

                        if (numero >= 0 && numero < TOTAL_NUMEROS) {
                            ocupados[numero] = true;
                        }

                    } catch (NumberFormatException e) {
                    }
                }
            }

            br.close();

        } catch (IOException e) {

            System.out.println("Error al leer los números:");
            System.out.println(e.getMessage());
        }

        return ocupados;
    }

    public static void mostrarCantidad() {

        boolean[] ocupados = obtenerNumerosOcupados();

        int ocupadosCantidad = 0;

        for (int i = 0; i < TOTAL_NUMEROS; i++) {

            if (ocupados[i]) {
                ocupadosCantidad++;
            }
        }

        int disponibles = TOTAL_NUMEROS - ocupadosCantidad;

        System.out.println("\n=========================");
        System.out.println("ESTADO DE LA RIFA");
        System.out.println("=========================");
        System.out.println("Números totales:     " + TOTAL_NUMEROS);
        System.out.println("Números ocupados:    " + ocupadosCantidad);
        System.out.println("Números disponibles: " + disponibles);
    }
}