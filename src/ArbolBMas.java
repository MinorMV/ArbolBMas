import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;

public class ArbolBMas {
    private NodoArbolBMas raiz;
    private final int orden;

    public ArbolBMas(int orden) {
        if (orden < 3) throw new IllegalArgumentException("El orden debe ser >= 3");
        this.orden = orden;
        // al inicio la raiz es una hoja vacía
        this.raiz = new NodoArbolBMas(true, orden);
    }

    // BUSCAR 
    public String buscar(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int i = hoja.buscarPos(clave);
        if (i < hoja.listaClaves.size() && hoja.listaClaves.get(i) == clave) {
            return hoja.listaValores.get(i);
        }
        return null;
    }

    private NodoArbolBMas buscarHoja(int clave) {
        NodoArbolBMas x = raiz;
        while (!x.esHoja) {
            int i = x.buscarPos(clave);
            if (i < x.listaHijos.size()) x = x.listaHijos.get(i);
            else x = x.listaHijos.get(x.listaHijos.size() - 1);
        }
        return x;
    }

    // INSERTAR 
    public void insertar(int clave, String valor) {
        NodoArbolBMas hoja = buscarHoja(clave);

        // si existe, solo actualizo 
        int pos = hoja.buscarPos(clave);
        if (pos < hoja.listaClaves.size() && hoja.listaClaves.get(pos) == clave) {
            hoja.listaValores.set(pos, valor);
            return;
        }

        // si no existe, la meto en orden
        hoja.insertarClave(clave, valor);

        // si me pasé del máximo, toca dividir y subir separadora
        if (hoja.estaLleno()) {
            dividirYSubir(hoja);
        }
    }

    private void dividirYSubir(NodoArbolBMas nodo) {
        // dividir el nodo y obtener (separadora, hermanoDerecho)
        Map.Entry<Integer, NodoArbolBMas> split = nodo.dividir();
        int separadora = split.getKey();
        NodoArbolBMas derecho = split.getValue();

        // si el nodo no tenía padre, creo una nueva raíz
        if (nodo.padre == null) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false, orden);
            nuevaRaiz.listaClaves.add(separadora);
            nuevaRaiz.listaHijos.add(nodo);
            nuevaRaiz.listaHijos.add(derecho);
            nodo.padre = nuevaRaiz;
            derecho.padre = nuevaRaiz;
            raiz = nuevaRaiz;
            return;
        }

        // si tenía padre, insertar la separadora en el padre
        NodoArbolBMas p = nodo.padre;
        int posSep = p.buscarPos(separadora);
        p.listaClaves.add(posSep, separadora);
        p.listaHijos.add(posSep + 1, derecho);
        derecho.padre = p;

        // si el padre quedó pasado, seguir subiendo
        if (p.estaLleno()) {
            dividirYSubir(p);
        }
    }

    // ELIMINAR 
    public boolean eliminar(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int i = hoja.buscarPos(clave);

        // si no existe, ya
        if (i >= hoja.listaClaves.size() || hoja.listaClaves.get(i) != clave) {
            return false;
        }

        // quito clave y valor en la hoja
        hoja.listaClaves.remove(i);
        hoja.listaValores.remove(i);

        // si la raiz es hoja se puede dejar vacía sin problema
        if (hoja == raiz) return true;

        // si quedó por debajo del mínimo, intento arreglar
        if (hoja.estaBajo()) {
            reequilibrar(hoja);
        }
        return true;
    }

    // intentar pedir prestado a hermanos o fusionar si no se puede
    private void reequilibrar(NodoArbolBMas x) {
        // si x es la raiz, revisar si hay que bajar un nivel
        if (x == raiz) {
            if (!x.esHoja && x.listaClaves.isEmpty() && x.listaHijos.size() == 1) {
                raiz = x.listaHijos.get(0);
                raiz.padre = null;
            }
            return;
        }

        NodoArbolBMas p = x.padre;
        int idx = p.listaHijos.indexOf(x);
        NodoArbolBMas izq = (idx > 0) ? p.listaHijos.get(idx - 1) : null;
        NodoArbolBMas der = (idx < p.listaHijos.size() - 1) ? p.listaHijos.get(idx + 1) : null;

        // redistribuir con izquierdo 
        if (izq != null && izq.listaClaves.size() > izq.minClaves) {
            if (x.esHoja) {
                // paso la última clave del izq a la primera de x
                int kPrest = izq.listaClaves.remove(izq.listaClaves.size() - 1);
                String vPrest = izq.listaValores.remove(izq.listaValores.size() - 1);
                x.listaClaves.add(0, kPrest);
                x.listaValores.add(0, vPrest);
                // actualizar separadora del padre para reflejar el nuevo mínimo en x
                p.listaClaves.set(idx - 1, x.listaClaves.get(0));
            } else {
                // para internos es una rotación usando la separadora del padre
                int kSube = p.listaClaves.get(idx - 1);
                // hijo prestado
                NodoArbolBMas hijoPrest = izq.listaHijos.remove(izq.listaHijos.size() - 1);
                int kBaja = izq.listaClaves.remove(izq.listaClaves.size() - 1);

                x.listaClaves.add(0, kSube);
                x.listaHijos.add(0, hijoPrest);
                hijoPrest.padre = x;

                p.listaClaves.set(idx - 1, kBaja);
            }
            return;
        }

        // redistribuir con derecho
        if (der != null && der.listaClaves.size() > der.minClaves) {
            if (x.esHoja) {
                int kPrest = der.listaClaves.remove(0);
                String vPrest = der.listaValores.remove(0);
                x.listaClaves.add(kPrest);
                x.listaValores.add(vPrest);
                // actualizar separadora del padre (nuevo mínimo del derecho)
                p.listaClaves.set(idx, der.listaClaves.get(0));
            } else {
                int kSube = p.listaClaves.get(idx);
                NodoArbolBMas hijoPrest = der.listaHijos.remove(0);
                int kBaja = der.listaClaves.remove(0);

                x.listaClaves.add(kSube);
                x.listaHijos.add(hijoPrest);
                hijoPrest.padre = x;

                p.listaClaves.set(idx, kBaja);
            }
            return;
        }

        // 3) si nadie puede prestar, fusiono con izq o der
        if (izq != null) {
            fusionar(izq, x, idx - 1);
        } else if (der != null) {
            fusionar(x, der, idx);
        }
    }

    // unir a y b, y actualizar el padre
    private void fusionar(NodoArbolBMas a, NodoArbolBMas b, int idxSepEnPadre) {
        NodoArbolBMas p = a.padre;

        if (a.esHoja) {
            // concatenar claves/valores y arreglar el enlace siguiente
            a.listaClaves.addAll(b.listaClaves);
            a.listaValores.addAll(b.listaValores);
            a.siguiente = b.siguiente;
        } else {
            // en internos bajar la separadora del padre y concatenar todo
            int sep = p.listaClaves.get(idxSepEnPadre);
            a.listaClaves.add(sep);
            a.listaClaves.addAll(b.listaClaves);
            for (NodoArbolBMas h : b.listaHijos) {
                a.listaHijos.add(h);
                if (h != null) h.padre = a;
            }
        }

        // quitar del padre la separadora y el puntero a b
        p.listaClaves.remove(idxSepEnPadre);
        p.listaHijos.remove(b);

        // si el padre quedó bajo, sigo arreglando
        if (p != raiz && p.estaBajo()) {
            reequilibrar(p);
        } else if (p == raiz && !p.esHoja && p.listaClaves.isEmpty()) {
            // si la raiz interna quedó vacía, bajo la altura
            raiz = p.listaHijos.get(0);
            raiz.padre = null;
        }
    }

    // desde claveInicio, devolver hasta n pares recorriendo hojas
    public ArrayList<String> recorrer(int claveInicio, int n) {
        ArrayList<String> out = new ArrayList<>();
        if (n <= 0) return out;

        NodoArbolBMas hoja = buscarHoja(claveInicio);
        int i = hoja.buscarPos(claveInicio);

        // Para agregar esta hoja y luego seguir por siguiente
        while (true) {
            while (i < hoja.listaClaves.size() && out.size() < n) {
                int k = hoja.listaClaves.get(i);
                String v = hoja.listaValores.get(i);
                out.add(k + " -> " + v);
                i++;
            }
            if (out.size() >= n) break;
            if (hoja.siguiente == null) break;
            hoja = hoja.siguiente;
            i = 0;
        }
        return out;
    }

    public boolean esVacio() {
        return raiz.esHoja && raiz.listaClaves.isEmpty();
    }

    // ver el árbol por niveles (I=interno, H=hoja)
    public void imprimirEstructura() {
        Queue<NodoArbolBMas> q = new ArrayDeque<>();
        q.add(raiz);
        while (!q.isEmpty()) {
            int sz = q.size();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sz; i++) {
                NodoArbolBMas x = q.poll();
                sb.append(x.esHoja ? "[H]" : "[I]").append(x.listaClaves).append("  ");
                if (!x.esHoja) {
                    for (NodoArbolBMas h : x.listaHijos) q.add(h);
                }
            }
            System.out.println(sb.toString());
        }
    }
}
