package buscaminas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Buscaminas extends JFrame {

    // Esta clase representa la ventana principal del juego.
    // Aquí se conectan la lógica del tablero con la parte visual.
    // La ventana tiene el menú, los contadores, el tablero de botones y la cara del juego.

    // En este enum definimos los tres niveles de dificultad.
    // Cada nivel guarda: nombre visible, filas, columnas y cuántas minas habrá.
    private enum Dificultad {
        FACIL("Fácil", 8, 8, 10),
        NORMAL("Normal", 10, 10, 18),
        DIFICIL("Difícil", 12, 12, 30);

        private final String texto;
        private final int filas;
        private final int columnas;
        private final int minas;

        Dificultad(String texto, int filas, int columnas, int minas) {
            this.texto = texto;
            this.filas = filas;
            this.columnas = columnas;
            this.minas = minas;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    // Tiempo máximo de una partida, en segundos.
    // Después de 180 segundos, el juego termina si aún no se ha ganado.
    private static final int TIEMPO_MAXIMO_SEGUNDOS = 180;

    // Colores del tema oscuro.
    // Sirven para dar una apariencia más elegante y visualmente clara en modo noche.
    private final Color COLOR_FONDO_OSCURO = new Color(43, 43, 43);
    private final Color COLOR_PANEL_OSCURO = new Color(60, 63, 65);
    private final Color COLOR_TABLERO_OSCURO = new Color(51, 51, 51);
    private final Color COLOR_BOTON_OSCURO = new Color(70, 73, 75);
    private final Color COLOR_BORDE_OSCURO = new Color(85, 85, 85);
    private final Color COLOR_TEXTO_OSCURO = new Color(187, 187, 187);

    // Colores del tema claro.
    // Se usan para la vista por defecto y para dar contraste con el modo oscuro.
    private final Color COLOR_FONDO_CLARO = new Color(238, 238, 238);
    private final Color COLOR_PANEL_CLARO = new Color(220, 220, 220);
    private final Color COLOR_TABLERO_CLARO = new Color(210, 210, 210);
    private final Color COLOR_BOTON_CLARO = new Color(225, 225, 225);
    private final Color COLOR_CELDA_REVELADA_CLARO = new Color(206, 206, 206);
    private final Color COLOR_CELDA_REVELADA_OSCURO = new Color(92, 92, 92);
    private final Color COLOR_BORDE_CLARO = new Color(160, 160, 160);
    private final Color COLOR_TEXTO_CLARO = new Color(40, 40, 40);

    // Objeto que contiene la lógica del juego (minas, números, revelado, etc.).
    private TableroBuscaminas tablero;

    // Dificultad que está seleccionada en este momento.
    private Dificultad dificultadActual = Dificultad.FACIL;

    // Aquí guardamos los componentes visibles de la interfaz.
    // Cada uno controla una parte del diseño de la ventana.
    private JMenuBar menuBar;
    private JMenu menuTema;
    private JMenu menuAcercaDe;
    private JMenuItem itemClaro;
    private JMenuItem itemOscuro;
    private JMenuItem itemInformacion;
    private JPanel panelSuperior;
    private JPanel panelDificultad;
    private JPanel panelContadores;
    private JPanel panelTablero;
    private JLabel lblDificultad;
    private JLabel labelTextoMinas;
    private JLabel labelTextoTiempo;
    private JLabel labelMinas;
    private JLabel labelTiempo;
    private JButton btnCarita;
    private JButton[][] botones;
    private JComboBox<Dificultad> comboDificultad;

    // Timer y variables de control del juego.
    // timer hace la cuenta atrás mientras la partida está activa.
    // segundosRestantes guarda el tiempo que queda.
    // temporizadorIniciado evita que el reloj empiece antes de la primera jugada.
    private Timer timer;
    private int segundosRestantes;
    private boolean temporizadorIniciado;

    // temaOscuro indica qué esquema visual está activo en ese momento.
    private boolean temaOscuro = false;

    // Constructor principal de la ventana del juego.
    // Aquí se crea el tablero inicial y se monta la interfaz.
    public Buscaminas() {
        this.tablero = new TableroBuscaminas(
                dificultadActual.filas,
                dificultadActual.columnas,
                dificultadActual.minas
        );

        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        inicializarPanelSuperior();
        inicializarPanelTablero();
        add(panelTablero, BorderLayout.CENTER);
        inicializarTimer();
        inicializarMenuBar();

        aplicarTemaClaro();
        reiniciarJuego();

        pack();
        setLocationRelativeTo(null);
    }

    // Este método crea la barra de menú.
    // Tiene dos opciones principales: cambiar el tema y abrir la ventana de información.
    private void inicializarMenuBar() {
        menuBar = new JMenuBar();

        menuTema = new JMenu("Tema");
        menuTema.setOpaque(true);

        itemClaro = new JMenuItem("Claro");
        itemClaro.setOpaque(true);
        itemClaro.addActionListener(e -> aplicarTemaClaro());

        itemOscuro = new JMenuItem("Oscuro");
        itemOscuro.setOpaque(true);
        itemOscuro.addActionListener(e -> aplicarTemaOscuro());

        menuTema.add(itemClaro);
        menuTema.add(itemOscuro);
        configurarPopupMenu(menuTema.getPopupMenu());

        menuAcercaDe = new JMenu("Acerca de");
        menuAcercaDe.setOpaque(true);

        itemInformacion = new JMenuItem("Información");
        itemInformacion.setOpaque(true);
        itemInformacion.addActionListener(e -> mostrarAcercaDe());

        menuAcercaDe.add(itemInformacion);
        configurarPopupMenu(menuAcercaDe.getPopupMenu());

        menuBar.add(menuTema);
        menuBar.add(menuAcercaDe);
        setJMenuBar(menuBar);

        aplicarTemaMenu();
    }

    // Este método personaliza el menú desplegable para que no salga con fondo feo o blanco.
    // La idea es que se vea igual que el resto de la ventana.
    private void configurarPopupMenu(JPopupMenu popup) {
        popup.setOpaque(true);
        popup.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_CLARO));
    }

    // Este método cambia el color del menú según el tema actual.
    // Si el tema es oscuro, los textos y fondos pasan a tonos oscuros con letras claras.
    private void aplicarTemaMenu() {
        Color fondo = temaOscuro ? COLOR_PANEL_OSCURO : COLOR_PANEL_CLARO;
        Color texto = temaOscuro ? COLOR_TEXTO_OSCURO : COLOR_TEXTO_CLARO;
        Color borde = temaOscuro ? COLOR_BORDE_OSCURO : COLOR_BORDE_CLARO;

        menuBar.setBackground(fondo);
        menuBar.setBorder(BorderFactory.createLineBorder(borde));
        menuBar.setForeground(texto);

        menuTema.setForeground(texto);
        menuTema.setBackground(fondo);
        menuTema.setOpaque(true);

        menuAcercaDe.setForeground(texto);
        menuAcercaDe.setBackground(fondo);
        menuAcercaDe.setOpaque(true);

        for (JMenuItem item : new JMenuItem[] {itemClaro, itemOscuro, itemInformacion}) {
            item.setBackground(fondo);
            item.setForeground(texto);
            item.setOpaque(true);
            item.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            item.setFocusPainted(false);
        }

        for (JPopupMenu popup : new JPopupMenu[] {menuTema.getPopupMenu(), menuAcercaDe.getPopupMenu()}) {
            popup.setBackground(fondo);
            popup.setForeground(texto);
            popup.setBorder(BorderFactory.createLineBorder(borde));
        }
    }

    // Este bloque crea la parte superior del juego.
    // Se organiza con un panel que contiene:
    // - selector de dificultad
    // - contador de minas
    // - botón de la cara
    // - contador de tiempo
    private void inicializarPanelSuperior() {
        panelSuperior = new JPanel(new BorderLayout(10, 6));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        panelDificultad = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panelDificultad.setOpaque(true);
        panelDificultad.setBackground(COLOR_PANEL_CLARO);

        lblDificultad = new JLabel("Dificultad:");
        lblDificultad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDificultad.setForeground(COLOR_TEXTO_CLARO);

        comboDificultad = new JComboBox<>(Dificultad.values());
        comboDificultad.setSelectedItem(dificultadActual);
        comboDificultad.setPreferredSize(new Dimension(110, 28));
        comboDificultad.setFocusable(false);
        comboDificultad.addActionListener(e -> cambiarDificultad((Dificultad) comboDificultad.getSelectedItem()));

        panelDificultad.add(lblDificultad);
        panelDificultad.add(comboDificultad);

        panelContadores = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelContadores.setOpaque(true);
        panelContadores.setBackground(COLOR_PANEL_CLARO);

        labelTextoMinas = new JLabel("Minas:");
        labelTextoMinas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelTextoMinas.setForeground(COLOR_TEXTO_CLARO);

        labelMinas = new JLabel(String.format("%03d", dificultadActual.minas));
        labelMinas.setFont(new Font("Consolas", Font.BOLD, 16));

        btnCarita = new JButton("🙂");
        btnCarita.setFocusPainted(false);
        btnCarita.addActionListener(e -> reiniciarJuego());

        labelTextoTiempo = new JLabel("Tiempo:");
        labelTextoTiempo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelTextoTiempo.setForeground(COLOR_TEXTO_CLARO);

        labelTiempo = new JLabel(String.format("%03d", TIEMPO_MAXIMO_SEGUNDOS));
        labelTiempo.setFont(new Font("Consolas", Font.BOLD, 16));

        panelContadores.add(labelTextoMinas);
        panelContadores.add(labelMinas);
        panelContadores.add(btnCarita);
        panelContadores.add(labelTextoTiempo);
        panelContadores.add(labelTiempo);

        panelSuperior.add(panelDificultad, BorderLayout.NORTH);
        panelSuperior.add(panelContadores, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);
    }

    // Aquí se crean todos los botones del tablero.
    // Se recorre cada fila y columna y se genera un JButton para cada casilla.
    private void inicializarPanelTablero() {
        if (panelTablero == null) {
            panelTablero = new JPanel(new GridBagLayout());
        } else {
            panelTablero.removeAll();
            panelTablero.setLayout(new GridBagLayout());
        }

        GridBagConstraints gbc = new GridBagConstraints();
        botones = new JButton[tablero.getFilas()][tablero.getColumnas()];

        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
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
    }

    // Aquí se configura la cuenta atrás del juego.
    // Cada segundo se resta 1 al tiempo y, si llega a cero, el juego termina.
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

    // Cuando el usuario hace clic izquierdo, se intenta revelar la casilla.
    // Si esa casilla es válida, la lógica del tablero decide qué ocurre.
    private void manejarClicIzquierdo(int fila, int columna) {
        if (tablero.isJuegoTerminado()) {
            return;
        }

        boolean revelada = tablero.revelarCasilla(fila, columna);
        if (!revelada) {
            return;
        }

        if (!temporizadorIniciado) {
            temporizadorIniciado = true;
            timer.start();
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

    // Cuando el usuario hace clic derecho, se pone o quita una bandera.
    // Esto sirve para marcar casillas sospechosas antes de revelar.
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

    // Este método reinicia el juego completo.
    // Reubica las minas, devuelve el tiempo a su valor inicial y vuelve a pintar el tablero.
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
        temporizadorIniciado = false;
    }

    // Este método actualiza la apariencia del tablero según el estado real del juego.
    // Si una casilla está revelada, muestra el número o la mina.
    // Si está tapada, puede mostrar una bandera o quedar normal.
    private void renderizarTablero() {
        int filas = tablero.getFilas();
        int columnas = tablero.getColumnas();

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
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
                        boton.setBackground(temaOscuro ? COLOR_CELDA_REVELADA_OSCURO : COLOR_CELDA_REVELADA_CLARO);
                        boton.setForeground(getColorNumero(tablero.getNumeroCasilla(fila, columna)));
                    } else {
                        boton.setBackground(temaOscuro ? COLOR_CELDA_REVELADA_OSCURO : COLOR_CELDA_REVELADA_CLARO);
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

    // Este método actualiza los valores que se muestran en la parte superior.
    // Cambia el número de minas restantes y el tiempo que queda.
    private void actualizarCabecera() {
        labelMinas.setText(String.format("%03d", tablero.getMinasRestantes()));
        labelTiempo.setText(String.format("%03d", segundosRestantes));
    }

    // Cuando el usuario cambia la dificultad, se crea un tablero nuevo con nuevas medidas.
    // Esto implica cambiar filas, columnas y cuántas minas habrá.
    private void cambiarDificultad(Dificultad nuevaDificultad) {
        if (nuevaDificultad == null) {
            return;
        }

        dificultadActual = nuevaDificultad;
        tablero = new TableroBuscaminas(
                nuevaDificultad.filas,
                nuevaDificultad.columnas,
                nuevaDificultad.minas
        );

        inicializarPanelTablero();
        panelTablero.revalidate();
        panelTablero.repaint();

        reiniciarJuego();
        pack();
        setLocationRelativeTo(null);
    }

    // Cada número del tablero tiene un color diferente para mejor lectura.
    // Por ejemplo, el 1 suele ir en azul, el 2 en verde, el 3 en rojo, etc.
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

    // Este método activa el tema claro.
    // Modifica colores de fondo, texto y paneles para que la interfaz se vea clara.
    private void aplicarTemaClaro() {
        temaOscuro = false;

        getContentPane().setBackground(COLOR_FONDO_CLARO);
        panelSuperior.setBackground(COLOR_PANEL_CLARO);
        panelTablero.setBackground(COLOR_TABLERO_CLARO);

        aplicarTemaMenu();

        panelDificultad.setBackground(COLOR_PANEL_CLARO);
        panelContadores.setBackground(COLOR_PANEL_CLARO);
        lblDificultad.setForeground(COLOR_TEXTO_CLARO);
        labelTextoMinas.setForeground(COLOR_TEXTO_CLARO);
        labelTextoTiempo.setForeground(COLOR_TEXTO_CLARO);
        labelMinas.setForeground(COLOR_TEXTO_CLARO);
        labelTiempo.setForeground(COLOR_TEXTO_CLARO);
        btnCarita.setBackground(COLOR_BOTON_CLARO);
        btnCarita.setForeground(COLOR_TEXTO_CLARO);

        comboDificultad.setForeground(COLOR_TEXTO_CLARO);
        comboDificultad.setBackground(COLOR_BOTON_CLARO);
        DefaultListCellRenderer rendererClaro = new DefaultListCellRenderer();
        rendererClaro.setForeground(COLOR_TEXTO_CLARO);
        rendererClaro.setBackground(COLOR_BOTON_CLARO);
        comboDificultad.setRenderer(rendererClaro);

        renderizarTablero();
    }

    // Este método activa el tema oscuro.
    // Cambia la apariencia de la ventana para que sea más cómoda en entornos oscuros.
    private void aplicarTemaOscuro() {
        temaOscuro = true;

        getContentPane().setBackground(COLOR_FONDO_OSCURO);
        panelSuperior.setBackground(COLOR_PANEL_OSCURO);
        panelTablero.setBackground(COLOR_TABLERO_OSCURO);

        aplicarTemaMenu();

        panelDificultad.setBackground(COLOR_PANEL_OSCURO);
        panelContadores.setBackground(COLOR_PANEL_OSCURO);
        lblDificultad.setForeground(COLOR_TEXTO_OSCURO);
        labelTextoMinas.setForeground(COLOR_TEXTO_OSCURO);
        labelTextoTiempo.setForeground(COLOR_TEXTO_OSCURO);
        labelMinas.setForeground(COLOR_TEXTO_OSCURO);
        labelTiempo.setForeground(COLOR_TEXTO_OSCURO);
        btnCarita.setBackground(COLOR_BOTON_OSCURO);
        btnCarita.setForeground(Color.WHITE);

        comboDificultad.setForeground(COLOR_TEXTO_OSCURO);
        comboDificultad.setBackground(COLOR_BOTON_OSCURO);
        DefaultListCellRenderer rendererOscuro = new DefaultListCellRenderer();
        rendererOscuro.setForeground(COLOR_TEXTO_OSCURO);
        rendererOscuro.setBackground(COLOR_BOTON_OSCURO);
        comboDificultad.setRenderer(rendererOscuro);

        renderizarTablero();
    }

    // Cuando termina la partida, se usa este método para mostrar el mensaje final.
    // Puede ser victoria, derrota o tiempo agotado.
    private void mostrarEstadoJuego(String mensaje) {
        Color fondo = temaOscuro ? COLOR_PANEL_OSCURO : COLOR_PANEL_CLARO;
        Color texto = temaOscuro ? COLOR_TEXTO_OSCURO : COLOR_TEXTO_CLARO;

        JOptionPane panelMensaje = new JOptionPane(mensaje, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = panelMensaje.createDialog(this, "Buscaminas");
        aplicarColoresDialogo(dialog.getContentPane(), fondo, texto);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Este método recorre todos los elementos del diálogo y cambia su color.
    // Así el mensaje final sigue el mismo estilo visual que el resto del juego.
    private void aplicarColoresDialogo(Component componente, Color fondo, Color texto) {
        if (componente instanceof JComponent componenteSwing) {
            componenteSwing.setBackground(fondo);
            componenteSwing.setForeground(texto);
            if (componenteSwing instanceof JPanel || componenteSwing instanceof JOptionPane
                    || componenteSwing instanceof JButton) {
                componenteSwing.setOpaque(true);
            }
        }

        if (componente instanceof Container contenedor) {
            for (Component hijo : contenedor.getComponents()) {
                aplicarColoresDialogo(hijo, fondo, texto);
            }
        }
    }

    // Este método abre la ventana de información del juego.
    // Sirve para mostrar un pequeño resumen del proyecto y su nombre.
    private void mostrarAcercaDe() {
        JDialog dialog = new JDialog(this, "Acerca de", true);
        dialog.setSize(380, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        Color fondo = temaOscuro ? COLOR_PANEL_OSCURO : COLOR_PANEL_CLARO;
        Color textoColor = temaOscuro ? COLOR_TEXTO_OSCURO : COLOR_TEXTO_CLARO;
        dialog.getContentPane().setBackground(fondo);

        JLabel texto = new JLabel(
                "<html><center><b>Buscaminas</b><br><br>"
                        + "Juego creado por Pablo Casado<br><br>"
                        + "<b>Instrucciones</b><br>"
                        + "Clic izquierdo: revelar una casilla.<br>"
                        + "Clic derecho: poner o quitar una bandera.<br>"
                        + "Los números indican las minas cercanas.<br>"
                        + "Revela todas las casillas sin minas y evita tocarlas."
                        + "<br>Tienes 180 segundos para ganar.</center></html>",
                SwingConstants.CENTER
        );
        texto.setForeground(textoColor);
        dialog.add(texto, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Punto de entrada principal del programa.
    // Aquí se crea la ventana y se hace visible para empezar a jugar.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Buscaminas().setVisible(true);
        });
    }
}
