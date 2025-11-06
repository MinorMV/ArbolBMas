import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== PRACTICA 2 DE ESTRUCTURAS DE DATOS ===");
        System.out.println("=== MINOR MORA VARGAS ===");
        System.out.println("UNIVERSIDAD CENFOTEC");
        System.out.println("ARBOL B+ IMPLEMENTACION EN JAVA");

        System.out.print("Ingrese el orden del arbol: ");
        int orden = sc.nextInt();
        sc.nextLine(); 

        ArbolBMas arbol = new ArbolBMas(orden);

        while (true) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Insertar clave y valor");
            System.out.println("2. Buscar clave");
            System.out.println("3. Eliminar clave");
            System.out.println("4. Recorrer desde una clave");
            System.out.println("5. Mostrar estructura del arbol");
            System.out.println("6. Ver si esta vacio");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1":
                    System.out.print("Digite la clave (numero entero): ");
                    int claveIns = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Digite el valor (texto): ");
                    String valorIns = sc.nextLine();
                    arbol.insertar(claveIns, valorIns);
                    System.out.println("Se inserto correctamente la clave " + claveIns);
                    break;

                case "2":
                    System.out.print("Digite la clave a buscar: ");
                    int claveBus = sc.nextInt();
                    sc.nextLine();
                    String resultado = arbol.buscar(claveBus);
                    if (resultado != null) {
                        System.out.println("Valor encontrado: " + resultado);
                    } else {
                        System.out.println("La clave no existe en el arbol");
                    }
                    break;

                case "3":
                    System.out.print("Digite la clave a eliminar: ");
                    int claveElim = sc.nextInt();
                    sc.nextLine();
                    boolean eliminado = arbol.eliminar(claveElim);
                    if (eliminado) {
                        System.out.println("Se elimino correctamente la clave " + claveElim);
                    } else {
                        System.out.println("No se encontro la clave");
                    }
                    break;

                case "4":
                    System.out.print("Desde que clave desea recorrer: ");
                    int claveInicio = sc.nextInt();
                    System.out.print("Cuantos elementos desea ver: ");
                    int cantidad = sc.nextInt();
                    sc.nextLine();
                    ArrayList<String> lista = arbol.recorrer(claveInicio, cantidad);
                    if (lista.isEmpty()) {
                        System.out.println("No hay elementos en ese rango");
                    } else {
                        System.out.println("Elementos encontrados:");
                        for (String s : lista) {
                            System.out.println(s);
                        }
                    }
                    break;

                case "5":
                    System.out.println("Estructura del arbol B+");
                    arbol.imprimirEstructura();
                    break;

                case "6":
                    if (arbol.esVacio()) {
                        System.out.println("El arbol esta vacio");
                    } else {
                        System.out.println("El arbol tiene elementos");
                    }
                    break;

                case "0":
                    System.out.println("Saliendo del programa...");
                    return;

                default:
                    System.out.println("Opcion invalida, intente de nuevo");
                    break;
            }
        }
    }
}
