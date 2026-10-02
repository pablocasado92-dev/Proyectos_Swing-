package buscaminas;

import java.util.ArrayDeque;
import java.util.Queue;

public class TableroBuscaminas {

    // Estas variables guardan el tamaño del tablero.
    // filas: cuántas filas tiene el tablero.
    // columnas: cuántas columnas tiene el tablero.
    // minasTotales: cuántas minas habrá en total.
    private final int filas;
    private final int columnas;
    private final int minasTotales;

    // Aquí guardamos el estado interno del juego.
    // Una matriz de booleans es como una tabla donde cada celda puede ser true o false.
    // minas[f][c] = true significa que esa casilla tiene una mina.
    // reveladas[f][c] = true significa que esa casilla ya ha sido descubierta.
    // marcadas[f][c] = true significa que el usuario ha puesto una bandera.
    // numeros[f][c] guarda cuántas minas hay alrededor de esa casilla.
    private final boolean[][] minas;
    private final boolean[][] reveladas;
    private final boolean[][] marcadas;
    private final int[][] numeros;

    // Estado de la partida.
    // juegoTerminado indica si la partida ha terminado.
    // victoria indica si el jugador ha ganado.
    // primeraJugada sirve para asegurar que la primera casilla no sea una mina.
    private boolean juegoTerminado;
    private boolean victoria;
    private boolean primeraJugada;

    // Constructor por defecto: tablero clásico 8x8 con 10 minas.
    public TableroBuscaminas() {
        this(8, 8, 10);
    }

    // Constructor principal.
    // Aquí se crea el estado inicial del juego con el tamaño y número de minas indicados.
    public TableroBuscaminas(int filas, int columnas, int minasTotales) {
        this.filas = filas;
        this.columnas = columnas;
        this.minasTotales = minasTotales;

        this.minas = new boolean[filas][columnas];
        this.reveladas = new boolean[filas][columnas];
        this.marcadas = new boolean[filas][columnas];
        this.numeros = new int[filas][columnas];
        this.primeraJugada = true;

        generarMinas();
        calcularNumeros();
    }

    // Reinicia el juego completo.
    // Se borra todo lo anterior y se genera una nueva distribución de minas.
    public void reiniciar() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                minas[f][c] = false;
                reveladas[f][c] = false;
                marcadas[f][c] = false;
                numeros[f][c] = 0;
            }
        }

        juegoTerminado = false;
        victoria = false;
        primeraJugada = true;
        generarMinas();
        calcularNumeros();
    }

    // Este método se usa cuando el usuario pulsa una casilla.
    // Devuelve false si la acción no es válida, por ejemplo si la casilla no existe,
    // si el juego ya terminó o si hay bandera puesta.
    public boolean revelarCasilla(int fila, int columna) {
        if (!esValida(fila, columna) || juegoTerminado || marcadas[fila][columna]) {
            return false;
        }

        // La primera jugada no puede ser una mina.
        // Si el usuario pincha encima de una mina al principio, se mueve esa mina a otro sitio.
        if (primeraJugada) {
            primeraJugada = false;
            if (minas[fila][columna]) {
                moverMinaASitioSeguro(fila, columna);
            }
        }

        // Si la casilla tiene una mina, el jugador pierde.
        if (minas[fila][columna]) {
            reveladas[fila][columna] = true;
            revelarTodasLasMinas();
            juegoTerminado = true;
            victoria = false;
            return true;
        }

        // Si no hay mina, se abre el área vacía y también se muestran los números alrededor.
        revelarArea(fila, columna);

        // Tras revelar casillas, comprobamos si el jugador ha ganado.
        if (haGanado()) {
            juegoTerminado = true;
            victoria = true;
        }

        return true;
    }

    // Este método permite poner o quitar una bandera con clic derecho.
    // La bandera suele usarse para marcar posibles minas.
    public boolean alternarBandera(int fila, int columna) {
        if (!esValida(fila, columna) || juegoTerminado || reveladas[fila][columna]) {
            return false;
        }

        if (!marcadas[fila][columna] && contarMarcadas() >= minasTotales) {
            return false;
        }

        marcadas[fila][columna] = !marcadas[fila][columna];
        return true;
    }

    // Devuelve true si la casilla ya ha sido descubierta.
    public boolean isRevelada(int fila, int columna) {
        return reveladas[fila][columna];
    }

    // Devuelve true si la casilla tiene bandera.
    public boolean isMarcada(int fila, int columna) {
        return marcadas[fila][columna];
    }

    // Devuelve true si esa casilla contiene una mina.
    public boolean isMina(int fila, int columna) {
        return minas[fila][columna];
    }

    // Devuelve el número de minas que hay alrededor de una casilla.
    public int getNumeroCasilla(int fila, int columna) {
        return numeros[fila][columna];
    }

    // Devuelve el número de filas del tablero.
    public int getFilas() {
        return filas;
    }

    // Devuelve el número de columnas del tablero.
    public int getColumnas() {
        return columnas;
    }

    // Devuelve la cantidad total de minas del juego.
    public int getMinasTotales() {
        return minasTotales;
    }

    // Calcula cuántas minas faltan por marcar con bandera.
    // Si ya se han puesto más banderas que minas, devuelve 0.
    public int getMinasRestantes() {
        return Math.max(0, minasTotales - contarMarcadas());
    }

    // Devuelve si la partida terminó.
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    // Este método se usa cuando se acaba el tiempo.
    // Muestra todas las minas para que el jugador vea dónde estaban.
    public void perderJuego() {
        juegoTerminado = true;
        victoria = false;
        revelarTodasLasMinas();
    }

    // Devuelve true si el jugador ha ganado la partida.
    public boolean isVictoria() {
        return victoria;
    }

    // Esta función sirve para decirle a la parte visual qué texto poner en cada botón.
    // Ejemplos:
    // - "🚩" si hay bandera
    // - "💣" si hay una mina
    // - un número si hay minas cerca
    // - "" si la casilla es vacía
    public String getTextoCasilla(int fila, int columna) {
        if (!reveladas[fila][columna]) {
            return marcadas[fila][columna] ? "🚩" : "";
        }

        if (minas[fila][columna]) {
            return "💣";
        }

        if (numeros[fila][columna] > 0) {
            return String.valueOf(numeros[fila][columna]);
        }

        return "";
    }

    // Genera la posición de todas las minas de forma aleatoria.
    // Cuando el bucle termina, se ha colocado el número correcto de minas.
    private void generarMinas() {
        int minasColocadas = 0;

        while (minasColocadas < minasTotales) {
            int fila = (int) (Math.random() * filas);
            int columna = (int) (Math.random() * columnas);

            if (!minas[fila][columna]) {
                minas[fila][columna] = true;
                minasColocadas++;
            }
        }
    }

    // Este método calcula el número de minas alrededor de cada casilla.
    // Por ejemplo, si una casilla tiene el número 3, significa que tiene 3 minas vecinas.
    private void calcularNumeros() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (!minas[f][c]) {
                    numeros[f][c] = contarMinasAlrededor(f, c);
                }
            }
        }
    }

    // Mira todas las posiciones de alrededor de una casilla.
    // Recorre una zona 3x3 centrada en esa casilla y cuenta cuántas minas hay dentro.
    private int contarMinasAlrededor(int fila, int columna) {
        int total = 0;

        for (int f = fila - 1; f <= fila + 1; f++) {
            for (int c = columna - 1; c <= columna + 1; c++) {
                if (esValida(f, c) && minas[f][c]) {
                    total++;
                }
            }
        }

        return total;
    }

    // Este método hace la “explosión” del buscaminas.
    // Si el usuario abre una casilla vacía, se abren también todas las casillas vacías
    // que estén conectadas con ella, sin tocar las minas ni las banderas.
    private void revelarArea(int fila, int columna) {
        Queue<int[]> cola = new ArrayDeque<>();
        cola.add(new int[] { fila, columna });

        while (!cola.isEmpty()) {
            int[] actual = cola.poll();
            int f = actual[0];
            int c = actual[1];

            if (!esValida(f, c) || reveladas[f][c] || marcadas[f][c]) {
                continue;
            }

            reveladas[f][c] = true;

            if (numeros[f][c] != 0 || minas[f][c]) {
                continue;
            }

            for (int filaVecina = f - 1; filaVecina <= f + 1; filaVecina++) {
                for (int columnaVecina = c - 1; columnaVecina <= c + 1; columnaVecina++) {
                    if (!esValida(filaVecina, columnaVecina) || (filaVecina == f && columnaVecina == c)) {
                        continue;
                    }

                    if (!minas[filaVecina][columnaVecina] && !reveladas[filaVecina][columnaVecina]) {
                        cola.add(new int[] { filaVecina, columnaVecina });
                    }
                }
            }
        }
    }

    // Cuando se pierde, todas las minas se muestran a la vez.
    // Esto ayuda a ver dónde estaban exactamente las minas.
    private void revelarTodasLasMinas() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (minas[f][c]) {
                    reveladas[f][c] = true;
                }
            }
        }
    }

    // Si la primera casilla del usuario es una mina, esta función la mueve a otra posición.
    // Así se evita que el primer movimiento sea una pérdida automática.
    private void moverMinaASitioSeguro(int filaOriginal, int columnaOriginal) {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (!minas[f][c] && !(f == filaOriginal && c == columnaOriginal)) {
                    minas[f][c] = true;
                    minas[filaOriginal][columnaOriginal] = false;
                    calcularNumeros();
                    return;
                }
            }
        }
    }

    // Comprueba si el jugador ya ha abierto todas las casillas seguras.
    // Si todas las casillas sin minas están descubiertas, ha ganado.
    private boolean haGanado() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (!minas[f][c] && !reveladas[f][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Cuenta cuántas banderas hay colocadas en el tablero.
    private int contarMarcadas() {
        int total = 0;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (marcadas[f][c]) {
                    total++;
                }
            }
        }
        return total;
    }

    // Comprueba si una fila y una columna están dentro del rango del tablero.
    // Si no lo están, la casilla es inválida.
    private boolean esValida(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }
}
