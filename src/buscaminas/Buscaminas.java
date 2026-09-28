package buscaminas;

import javax.swing.*;
import java.awt.*;

public class Buscaminas extends JFrame {

    // Configuración del tablero
    private final int FILAS = 8;
    private final int COLUMNAS = 8;
    private final int MINAS = 10;
    private final int TIEMPO_MAXIMO_SEGUNDOS = 180;

    // --- PALETA DE COLORES (MODO OSCURO) ---
    private final Color COLOR_FONDO_VENTANA = new Color(43, 43, 43);
    private final Color COLOR_PANEL_SUPERIOR = new Color(60, 63, 65);
    private final Color COLOR_PANEL_TABLERO  = new Color(51, 51, 51);
    private final Color COLOR_BOTON_TABLERO  = new Color(70, 73, 75);
    private final Color COLOR_BORDE_BOTON   = new Color(85, 85, 85);
    private final Color COLOR_TEXTO_ETIQUETA = new Color(187, 187, 187);

    // Componentes de la interfaz
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
        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Fondo principal en modo oscuro
        getContentPane().setBackground(COLOR_FONDO_VENTANA);

        minas = new boolean[FILAS][COLUMNAS];

        inicializarPanelSuperior();
        inicializarPanelTablero();

        generarMinas(MINAS);
        inicializarTimer();

        pack();
        setLocationRelativeTo(null);
    }

    private void inicializarPanelSuperior() {
        panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelSuperior.setBackground(COLOR_PANEL_SUPERIOR);

        labelMinas = new JLabel(String.format("%03d", MINAS));
        labelMinas.setForeground(COLOR_TEXTO_ETIQUETA);
        labelMinas.setFont(new Font("Consolas", Font.BOLD, 16));

        btnCarita = new JButton("🙂");
        btnCarita.setBackground(COLOR_BOTON_TABLERO);
        btnCarita.setForeground(Color.WHITE);
        btnCarita.setFocusPainted(false);
        btnCarita.addActionListener(e -> reiniciarJuego());

        labelTiempo = new JLabel(String.format("%03d", TIEMPO_MAXIMO_SEGUNDOS));
        labelTiempo.setForeground(COLOR_TEXTO_ETIQUETA);
        labelTiempo.setFont(new Font("Consolas", Font.BOLD, 16));

        panelSuperior.add(labelMinas);
        panelSuperior.add(btnCarita);
        panelSuperior.add(labelTiempo);

        add(panelSuperior, BorderLayout.NORTH);
    }

    private void inicializarPanelTablero() {
        panelTablero = new JPanel(new GridBagLayout());
        panelTablero.setBackground(COLOR_PANEL_TABLERO);
        GridBagConstraints gbc = new GridBagConstraints();

        botones = new JButton[FILAS][COLUMNAS];

        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                gbc.gridx = c;
                gbc.gridy = f;
                gbc.fill = GridBagConstraints.BOTH;

                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(30, 30));
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
                
                // Estilo oscuro para los botones
                btn.setBackground(COLOR_BOTON_TABLERO);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BOTON));
                btn.setFocusPainted(false);

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
            botones[fila][columna].setBackground(new Color(180, 50, 50)); // Fondo rojo al explotar
            btnCarita.setText("😵");
            if (timer != null) timer.stop();
            JOptionPane.showMessageDialog(this, "¡Boom! Has pisado una mina.");
            deshabilitarTablero();
        } else {
            botones[fila][columna].setText("X");
            botones[fila][columna].setBackground(new Color(45, 45, 45)); // Tono más oscuro al revelar
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
                botones[f][c].setBackground(COLOR_BOTON_TABLERO);
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