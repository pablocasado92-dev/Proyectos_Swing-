package buscaminas;

import javax.swing.*;
import java.awt.*;

public class Buscaminas extends JFrame {

    // Configuración del tablero (Constantes: filas, columnas, minas)
    private final int FILAS = 8;
    private final int COLUMNAS = 8;
    private final int MINAS = 10;
    private final int TIEMPO_MAXIMO_SEGUNDOS = 180; // 3 minutos

    // Componentes de la interfaz (Paneles, Labels, Botón de carita)
    private JPanel panelSuperior;
    private JPanel panelTablero;
    private JLabel labelMinas;
    private JLabel labelTiempo;
    private JButton btnCarita;

    // Matriz de botones para el tablero y minas
    private JButton[][] botones;
    private boolean[][] minas;

    // Control del temporizador
    private Timer timer;
    private int segundosRestantes;

    public Buscaminas() {
        // Configuración de la ventana principal
        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Inicializar matriz de minas
        minas = new boolean[FILAS][COLUMNAS];

        // 1. Inicializar componentes superiores (marcadores/carita)
        inicializarPanelSuperior();

        // 2. Inicializar panel del tablero y matriz de botones
        inicializarPanelTablero();

        // 3. Generar minas e iniciar el temporizador de 3 minutos
        generarMinas(MINAS);
        inicializarTimer();

        // 4. Ajustar tamaño de ventana y centrar
        pack();
        setLocationRelativeTo(null);
    }

    private void inicializarPanelSuperior() {
        panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        labelMinas = new JLabel(String.format("%03d", MINAS));
        btnCarita = new JButton("🙂");
        
        // Listener asignado al botón de carita para reiniciar
        btnCarita.addActionListener(e -> reiniciarJuego());

        labelTiempo = new JLabel(String.format("%03d", TIEMPO_MAXIMO_SEGUNDOS));

        panelSuperior.add(labelMinas);
        panelSuperior.add(btnCarita);
        panelSuperior.add(labelTiempo);

        add(panelSuperior, BorderLayout.NORTH);
    }

    private void inicializarPanelTablero() {
        panelTablero = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        botones = new JButton[FILAS][COLUMNAS];

        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                gbc.gridx = c;
                gbc.gridy = f;
                gbc.fill = GridBagConstraints.BOTH;

                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(30, 30));

                final int fila = f;
                final int col = c;
                btn.addActionListener(e -> alHacerClicEnCasilla(fila, col));

                botones[f][c] = btn;
                panelTablero.add(btn, gbc);
            }
        }

        add(panelTablero, BorderLayout.CENTER);
    }

    private void inicializarTimer() {
        segundosRestantes = TIEMPO_MAXIMO_SEGUNDOS;
        
        // Ejecuta la acción cada 1000 ms (1 segundo)
        timer = new Timer(1000, e -> {
            segundosRestantes--;
            labelTiempo.setText(String.format("%03d", segundosRestantes));

            if (segundosRestantes <= 0) {
                timer.stop();
                btnCarita.setText("😵");
                JOptionPane.showMessageDialog(this, "¡Se acabó el tiempo! Has perdido.");
                deshabilitarTablero();
            }
        });
        
        timer.start();
    }

    private void generarMinas(int totalMinas) {
        int minasColocadas = 0;

        while (minasColocadas < totalMinas) {
            int fila = (int) (Math.random() * FILAS);
            int col  = (int) (Math.random() * COLUMNAS);

            if (!minas[fila][col]) {
                minas[fila][col] = true;
                minasColocadas++;
            }
        }
    }

    // --- MÉTODOS DE LÓGICA / MANEJO DE EVENTOS ---

    private void alHacerClicEnCasilla(int fila, int columna) {
        if (minas[fila][columna]) {
            botones[fila][columna].setText("💣");
            btnCarita.setText("😵");
            if (timer != null) timer.stop();
            JOptionPane.showMessageDialog(this, "¡Boom! Has pisado una mina.");
            deshabilitarTablero();
        } else {
            botones[fila][columna].setText("·");
            botones[fila][columna].setEnabled(false);
        }
    }

    private void deshabilitarTablero() {
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                botones[f][c].setEnabled(false);
            }
        }
    }

    private void reiniciarJuego() {
        // Resetear matriz de minas y estado de botones
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                minas[f][c] = false;
                botones[f][c].setText("");
                botones[f][c].setEnabled(true);
            }
        }

        generarMinas(MINAS);
        labelMinas.setText(String.format("%03d", MINAS));

        // Reiniciar y arrancar de nuevo el temporizador
        if (timer != null) {
            timer.stop();
        }
        segundosRestantes = TIEMPO_MAXIMO_SEGUNDOS;
        labelTiempo.setText(String.format("%03d", segundosRestantes));
        btnCarita.setText("🙂");
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Buscaminas().setVisible(true);
        });
    }
}