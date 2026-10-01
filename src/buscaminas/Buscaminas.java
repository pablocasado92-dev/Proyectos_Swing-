package buscaminas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Buscaminas extends JFrame {

    private static final int FILAS = 8;
    private static final int COLUMNAS = 8;
    private static final int MINAS = 10;
    private static final int TIEMPO_MAXIMO_SEGUNDOS = 180;

    private final Color COLOR_FONDO_OSCURO = new Color(43, 43, 43);
    private final Color COLOR_PANEL_OSCURO = new Color(60, 63, 65);
    private final Color COLOR_TABLERO_OSCURO = new Color(51, 51, 51);
    private final Color COLOR_BOTON_OSCURO = new Color(70, 73, 75);
    private final Color COLOR_BORDE_OSCURO = new Color(85, 85, 85);
    private final Color COLOR_TEXTO_OSCURO = new Color(187, 187, 187);

    private final Color COLOR_FONDO_CLARO = new Color(238, 238, 238);
    private final Color COLOR_PANEL_CLARO = new Color(220, 220, 220);
    private final Color COLOR_TABLERO_CLARO = new Color(210, 210, 210);
    private final Color COLOR_BOTON_CLARO = new Color(225, 225, 225);
    private final Color COLOR_BORDE_CLARO = new Color(160, 160, 160);
    private final Color COLOR_TEXTO_CLARO = new Color(40, 40, 40);

    private final TableroBuscaminas tablero;

    private JMenuBar menuBar;
    private JPanel panelSuperior;
    private JPanel panelTablero;
    private JLabel labelMinas;
    private JLabel labelTiempo;
    private JButton btnCarita;
    private JButton[][] botones;

    private Timer timer;
    private int segundosRestantes;
    private boolean temaOscuro = false;

    public Buscaminas() {
        this.tablero = new TableroBuscaminas();

        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        inicializarPanelSuperior();
        inicializarPanelTablero();
        inicializarTimer();
        inicializarMenuBar();

        aplicarTemaClaro();
        reiniciarJuego();

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

        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                gbc.gridx = columna;
                gbc.gridy = fila;
                gbc.fill = GridBagConstraints.BOTH;

                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(32, 32));
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
                btn.setFocusPainted(false);

                int filaFinal = fila;
                int columnaFinal = columna;

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            manejarClicIzquierdo(filaFinal, columnaFinal);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            manejarClicDerecho(filaFinal, columnaFinal);
                        }
                    }
                });

                botones[fila][columna] = btn;
                panelTablero.add(btn, gbc);
            }
        }

        add(panelTablero, BorderLayout.CENTER);
    }

    private void inicializarTimer() {
        segundosRestantes = TIEMPO_MAXIMO_SEGUNDOS;

        timer = new Timer(1000, e -> {
            if (tablero.isJuegoTerminado()) {
                timer.stop();
                return;
            }

            segundosRestantes--;
            labelTiempo.setText(String.format("%03d", segundosRestantes));

            if (segundosRestantes <= 0) {
                timer.stop();
                btnCarita.setText("😵");
                tablero.perderJuego();
                mostrarEstadoJuego("¡Se acabó el tiempo! Has perdido.");
                renderizarTablero();
            }
        });
    }

    private void manejarClicIzquierdo(int fila, int columna) {
        if (tablero.isJuegoTerminado()) {
            return;
        }

        boolean revelada = tablero.revelarCasilla(fila, columna);
        if (!revelada) {
            return;
        }

        if (tablero.isVictoria()) {
            btnCarita.setText("😎");
            timer.stop();
            mostrarEstadoJuego("¡Has ganado! Enhorabuena.");
        } else if (tablero.isJuegoTerminado()) {
            btnCarita.setText("😵");
            timer.stop();
            mostrarEstadoJuego("¡Boom! Has pisado una mina.");
        }

        renderizarTablero();
    }

    private void manejarClicDerecho(int fila, int columna) {
        if (tablero.isJuegoTerminado()) {
            return;
        }

        boolean banderaCambiada = tablero.alternarBandera(fila, columna);
        if (!banderaCambiada) {
            return;
        }

        actualizarCabecera();
        renderizarTablero();
    }

    private void reiniciarJuego() {
        tablero.reiniciar();
        segundosRestantes = TIEMPO_MAXIMO_SEGUNDOS;
        btnCarita.setText("🙂");
        labelTiempo.setText(String.format("%03d", segundosRestantes));
        actualizarCabecera();
        renderizarTablero();

        if (timer != null) {
            timer.stop();
        }
        timer.start();
    }

    private void renderizarTablero() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                JButton boton = botones[fila][columna];
                boolean revelada = tablero.isRevelada(fila, columna);
                boolean marcada = tablero.isMarcada(fila, columna);

                if (revelada) {
                    boton.setEnabled(false);
                    String texto = tablero.getTextoCasilla(fila, columna);
                    boton.setText(texto);

                    if (tablero.isMina(fila, columna)) {
                        boton.setBackground(new Color(180, 50, 50));
                    } else if (tablero.getNumeroCasilla(fila, columna) > 0) {
                        boton.setBackground(new Color(200, 200, 200));
                        boton.setForeground(getColorNumero(tablero.getNumeroCasilla(fila, columna)));
                    } else {
                        boton.setBackground(new Color(220, 220, 220));
                        boton.setForeground(Color.BLACK);
                    }
                } else {
                    boton.setEnabled(true);
                    boton.setText(marcada ? "🚩" : "");
                    boton.setBackground(temaOscuro ? COLOR_BOTON_OSCURO : COLOR_BOTON_CLARO);
                    boton.setForeground(temaOscuro ? Color.WHITE : COLOR_TEXTO_CLARO);
                }

                boton.setBorder(BorderFactory.createLineBorder(
                        temaOscuro ? COLOR_BORDE_OSCURO : COLOR_BORDE_CLARO
                ));
            }
        }
    }

    private void actualizarCabecera() {
        labelMinas.setText(String.format("%03d", tablero.getMinasRestantes()));
        labelTiempo.setText(String.format("%03d", segundosRestantes));
    }

    private Color getColorNumero(int numero) {
        return switch (numero) {
            case 1 -> new Color(37, 99, 235);
            case 2 -> new Color(22, 163, 74);
            case 3 -> new Color(220, 38, 38);
            case 4 -> new Color(79, 70, 229);
            case 5 -> new Color(146, 64, 14);
            case 6 -> new Color(14, 116, 144);
            case 7 -> new Color(17, 24, 39);
            default -> new Color(75, 85, 99);
        };
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

        renderizarTablero();
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

        renderizarTablero();
    }

    private void mostrarEstadoJuego(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private void mostrarAcercaDe() {
        JDialog dialog = new JDialog(this, "Acerca de", true);
        dialog.setSize(300, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JLabel texto = new JLabel(
                "<html><center>Buscaminas<br><br>Proyecto Java Swing</center></html>",
                SwingConstants.CENTER
        );
        dialog.add(texto, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Buscaminas().setVisible(true);
        });
    }
}
