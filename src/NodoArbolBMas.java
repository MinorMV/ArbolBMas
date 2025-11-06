import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.Map;

public class NodoArbolBMas {

    boolean esHoja;
    ArrayList<Integer> listaClaves;
    ArrayList<NodoArbolBMas> listaHijos;
    NodoArbolBMas siguiente;
    ArrayList<String> listaValores;
    NodoArbolBMas padre;

    //el orden del arbol y los limites de claves
    final int orden;
    final int maxClaves;
    final int minClaves;

    // constructor 

    public NodoArbolBMas(boolean esHoja, int orden) {
        this.esHoja = esHoja;
        this.orden = orden;
        this.maxClaves = orden - 1;
        this.minClaves = (int) Math.ceil((orden - 1) / 2.0);

        // inicializar listas

        this.listaClaves = new ArrayList<>();
        this.listaHijos = new ArrayList<>();
        this.listaValores = esHoja ? new ArrayList<>() : null;

        this.siguiente = null;
        this.padre = null;
    }

    // buscar la posicion donde deberia ir clave
    int buscarPos(int clave) {
        int i = 0;
        while (i < listaClaves.size() && clave > listaClaves.get(i)) {
            i++;
        }
        return i;
    }

    // insertar clave en el nodo 
    void insertarClave(int clave, String valor) {
        int pos = buscarPos(clave);
        if (esHoja) {
            // si la clave ya existe, actualizar el valor
            if (pos < listaClaves.size() && listaClaves.get(pos) == clave) {
                listaValores.set(pos, valor);
            } else {
                // si no existe, agregar la clave y el valor en la posicion correcta
                listaClaves.add(pos, clave);
                listaValores.add(pos, valor);
            }
        } else {
            // si es nodo interno solo agregar la clave
            listaClaves.add(pos, clave);
        }
    }

    // dividir un nodo 
    Map.Entry<Integer, NodoArbolBMas> dividir() {
        // calcular el punto donde se va a dividir el nodo
        int corte = esHoja ? (listaClaves.size() + 1) / 2 : (listaClaves.size() / 2);

        // crear el nodo derecho que se forma al dividir
        NodoArbolBMas derecho = new NodoArbolBMas(esHoja, orden);
        derecho.padre = this.padre;

        // mover las claves al nuevo nodo derecho
        for (int i = corte; i < listaClaves.size(); i++) {
            derecho.listaClaves.add(listaClaves.get(i));
            if (esHoja) {
                derecho.listaValores.add(listaValores.get(i));
            }
        }

        // si es un nodo interno, mover tambien los hijos de la derecha
        if (!esHoja) {
            int hijosDesde = corte + 1;
            for (int i = hijosDesde; i < listaHijos.size(); i++) {
                NodoArbolBMas h = listaHijos.get(i);
                derecho.listaHijos.add(h);
                if (h != null) {
                    h.padre = derecho;
                }
            }
            // eliminar los hijos que ya se pasaron al derecho
            while (listaHijos.size() > hijosDesde) {
                listaHijos.remove(listaHijos.size() - 1);
            }
        }

        // eliminar del actual las claves que ya se pasaron
        while (listaClaves.size() > corte) {
            listaClaves.remove(listaClaves.size() - 1);
            if (esHoja) {
                listaValores.remove(listaValores.size() - 1);
            }
        }

        // si es hoja, conectar con el siguiente
        if (esHoja) {
            derecho.siguiente = this.siguiente;
            this.siguiente = derecho;
        }

        // tomar la primera clave del nuevo nodo derecho para subirla al padre
        int separadora = derecho.listaClaves.get(0);

        // devolver la clave separadora junto con el nodo derecho
        return new AbstractMap.SimpleEntry<>(separadora, derecho);
    }

    // revisar si el nodo tiene mas claves de las permitidas
    boolean estaLleno() {
        return listaClaves.size() > maxClaves;
    }

    // revisar si el nodo tiene menos claves de las que deberia
    boolean estaBajo() {
        return listaClaves.size() < minClaves;
    }
}
