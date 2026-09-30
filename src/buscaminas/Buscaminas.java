package buscaminas;

import javax.swing.*;
import java.awt.*;

public class Buscaminas extends JFrame {

    private final int FILAS = 8;
    private final int COLUMNAS = 8;
    private final int MINAS = 10;
    private final int TIEMPO_MAXIMO_SEGUNDOS = 180;

    // Colores tema oscuro
    private final Color COLOR_FONDO_OSCURO = new Color(43, 43, 43);
    private final Color COLOR_PANEL_OSCURO = new Color(60, 63, 65);
    private final Color COLOR_TABLERO_OSCURO = new Color(51, 51, 51);
    private final Color COLOR_BOTON_OSCURO = new Color(70, 73, 75);
    private final Color COLOR_BORDE_OSCURO = new Color(85, 85, 85);
    private final Color COLOR_TEXTO_OSCURO = new Color(187, 187, 187);

    // Colores tema claro
    private final Color COLOR_FONDO_CLARO = new Color(238, 238, 238);
    private final Color COLOR_PANEL_CLARO = new Color(220, 220, 220);
    private final Color COLOR_TABLERO_CLARO = new Color(210, 210, 210);
    private final Color COLOR_BOTON_CLARO = new Color(225, 225, 225);
    private final Color COLOR_BORDE_CLARO = new Color(160, 160, 160);
    private final Color COLOR_TEXTO_CLARO = new Color(40, 40, 40);

    private JMenuBar menuBar;
    private JPanel panelSuperior;
    private JPanel panelTablero;
    private JLabel labelMinas;
    private JLabel labelTiempo;
    private JButton btnCarita;

    private JButton[][] botones;
    private boolean[][] minas;

    private Timer timer;
    private int segundosRestantes;
    private boolean temaOscuro = false;

    public Buscaminas() {
        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        minas = new boolean[FILAS][COLUMNAS];

        inicializarPanelSuperior();
        inicializarPanelTablero();

        generarMinas(MINAS);
        inicializarTimer();
        inicializarMenuBar();

        aplicarTemaClaro();

        pack();
        setLocationRelativeTo(null);
    }

    private void inicializarMenuBar() {
        menuBar = new JMenuBar();

        JMenu menuTema = new JMenu("Tema");

        JMenuItem itemClaro = new JMenuItem("Claro");
        itemClaro.addActionListener(e -> aplicarTemaClaro());

        JMenuItem itemOscuro = new JMenuItem("Oscuro");
        itemOscuro.addActionListener(e -> aplicarTemaOscuro());

        menuTema.add(itemClaro);
        menuTema.add(itemOscuro);

        JMenu menuAcercaDe = new JMenu("Acerca de");

        JMenuItem itemInformacion = new JMenuItem("Información");
        itemInformacion.addActionListener(e -> mostrarAcercaDe());

        menuAcercaDe.add(itemInformacion);

        menuBar.add(menuTema);
        menuBar.add(menuAcercaDe);

        setJMenuBar(menuBar);
    }

    private void inicializarPanelSuperior() {
        panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        labelMinas = new JLabel(String.format("%03d", MINAS));
        labelMinas.setFont(new Font("Consolas", Font.BOLD, 16));

        btnCarita = new JButton("🙂");
        btnCarita.setFocusPainted(false);
        btnCarita.addActionListener(e -> reiniciarJuego());

        labelTiempo = new JLabel(String.format("%03d", TIEMPO_MAXIMO_SEGUNDOS));
        labelTiempo.setFont(new Font("Consolas", Font.BOLD, 16));

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
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
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
            int col = (int) (Math.random() * COLUMNAS);

            if (!minas[fila][col]) {
                minas[fila][col] = true;
                minasColocadas++;
            }
        }
    }

    private void alHacerClicEnCasilla(int fila, int columna) {
        if (minas[fila][columna]) {
            botones[fila][columna].setText("💣");
            botones[fila][columna].setBackground(new Color(180, 50, 50));
            btnCarita.setText("😵");

            if (timer != null) {
                timer.stop();
            }

            JOptionPane.showMessageDialog(this, "¡Boom! Has pisado una mina.");
            deshabilitarTablero();
        } else {
            botones[fila][columna].setText("X");

            if (temaOscuro) {
                botones[fila][columna].setBackground(new Color(45, 45, 45));
            } else {
                botones[fila][columna].setBackground(new Color(200, 200, 200));
            }

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
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                minas[f][c] = false;
                botones[f][c].setText("");
                botones[f][c].setEnabled(true);

                if (temaOscuro) {
                    botones[f][c].setBackground(COLOR_BOTON_OSCURO);
                    botones[f][c].setForeground(Color.WHITE);
                    botones[f][c].setBorder(
                            BorderFactory.createLineBorder(COLOR_BORDE_OSCURO)
                    );
                } else {
                    botones[f][c].setBackground(COLOR_BOTON_CLARO);
                    botones[f][c].setForeground(COLOR_TEXTO_CLARO);
                    botones[f][c].setBorder(
                            BorderFactory.createLineBorder(COLOR_BORDE_CLARO)
                    );
                }
            }
        }

        generarMinas(MINAS);
        labelMinas.setText(String.format("%03d", MINAS));

        if (timer != null) {
            timer.stop();
        }

        segundosRestantes = TIEMPO_MAXIMO_SEGUNDOS;
        labelTiempo.setText(String.format("%03d", segundosRestantes));
        btnCarita.setText("🙂");
        timer.start();
    }

    private void aplicarTemaClaro() {
        temaOscuro = false;

        getContentPane().setBackground(COLOR_FONDO_CLARO);
        panelSuperior.setBackground(COLOR_PANEL_CLARO);
        panelTablero.setBackground(COLOR_TABLERO_CLARO);

        menuBar.setBackground(COLOR_PANEL_CLARO);
        menuBar.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_CLARO));

        labelMinas.setForeground(COLOR_TEXTO_CLARO);
        labelTiempo.setForeground(COLOR_TEXTO_CLARO);

        btnCarita.setBackground(COLOR_BOTON_CLARO);
        btnCarita.setForeground(COLOR_TEXTO_CLARO);

        reiniciarJuego();
    }

    private void aplicarTemaOscuro() {
        temaOscuro = true;

        getContentPane().setBackground(COLOR_FONDO_OSCURO);
        panelSuperior.setBackground(COLOR_PANEL_OSCURO);
        panelTablero.setBackground(COLOR_TABLERO_OSCURO);

        menuBar.setBackground(COLOR_PANEL_OSCURO);
        menuBar.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_OSCURO));

        labelMinas.setForeground(COLOR_TEXTO_OSCURO);
        labelTiempo.setForeground(COLOR_TEXTO_OSCURO);

        btnCarita.setBackground(COLOR_BOTON_OSCURO);
        btnCarita.setForeground(Color.WHITE);

        reiniciarJuego();
    }

    private void mostrarAcercaDe() {
        JFrame ventana = new JFrame("Acerca de");
        ventana.setSize(300, 180);
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(this);

        JLabel texto = new JLabel(
                "<html><center>Buscaminas<br><br>Proyecto realizado en Java Swing</center></html>",
                SwingConstants.CENTER
        );

        ventana.add(texto);
        ventana.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Buscaminas().setVisible(true);
        });
    }
}