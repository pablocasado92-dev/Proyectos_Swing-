package buscaminas;

import java.util.ArrayDeque;
import java.util.Queue;

public class TableroBuscaminas {

    private final int filas;
    private final int columnas;
    private final int minasTotales;

    private final boolean[][] minas;
    private final boolean[][] reveladas;
    private final boolean[][] marcadas;
    private final int[][] numeros;

    private boolean juegoTerminado;
    private boolean victoria;

    public TableroBuscaminas() {
        this.filas = 8;
        this.columnas = 8;
        this.minasTotales = 10;

        this.minas = new boolean[filas][columnas];
        this.reveladas = new boolean[filas][columnas];
        this.marcadas = new boolean[filas][columnas];
        this.numeros = new int[filas][columnas];

        generarMinas();
        calcularNumeros();
    }

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
        generarMinas();
        calcularNumeros();
    }

    public boolean revelarCasilla(int fila, int columna) {
        if (!esValida(fila, columna) || juegoTerminado || marcadas[fila][columna]) {
            return false;
        }

        if (minas[fila][columna]) {
            reveladas[fila][columna] = true;
            revelarTodasLasMinas();
            juegoTerminado = true;
            victoria = false;
            return true;
        }

        revelarArea(fila, columna);

        if (haGanado()) {
            juegoTerminado = true;
            victoria = true;
        }

        return true;
    }

    public boolean alternarBandera(int fila, int columna) {
        if (!esValida(fila, columna) || juegoTerminado || reveladas[fila][columna]) {
            return false;
        }

        marcadas[fila][columna] = !marcadas[fila][columna];
        return true;
    }

    public boolean isRevelada(int fila, int columna) {
        return reveladas[fila][columna];
    }

    public boolean isMarcada(int fila, int columna) {
        return marcadas[fila][columna];
    }

    public boolean isMina(int fila, int columna) {
        return minas[fila][columna];
    }

    public int getNumeroCasilla(int fila, int columna) {
        return numeros[fila][columna];
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public int getMinasTotales() {
        return minasTotales;
    }

    public int getMinasRestantes() {
        return minasTotales - contarMarcadas();
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public void perderJuego() {
        juegoTerminado = true;
        victoria = false;
        revelarTodasLasMinas();
    }

    public boolean isVictoria() {
        return victoria;
    }

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

    private void calcularNumeros() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (!minas[f][c]) {
                    numeros[f][c] = contarMinasAlrededor(f, c);
                }
            }
        }
    }

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

    private void revelarTodasLasMinas() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (minas[f][c]) {
                    reveladas[f][c] = true;
                }
            }
        }
    }

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

    private boolean esValida(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }
}
