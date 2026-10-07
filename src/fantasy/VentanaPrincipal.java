package fantasy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Pantalla Principal del juego (Segundo JFrame).
 * Muestra el Quinteto Titular, el Mercado de Jugadores y la gestión del presupuesto.
 */
public class VentanaPrincipal extends JFrame {

    // --- Parámetros recibidos del primer JFrame ---
    private String nombreClub;
    private String region;

    // --- Variables de estado del juego ---
    private int presupuesto = 200; // Presupuesto inicial en millones de oro

    // Quinteto titular: guarda el jugador asignado a cada rol (null si está libre)
    // Índices: 0=TOP, 1=JGL, 2=MID, 3=ADC, 4=SUP
    private Jugador[] quintetoTitular = new Jugador[5];
    private final String[] ROLES = {"TOP", "JGL", "MID", "ADC", "SUP"};

    // Lista completa de jugadores en el mercado
    private List<Jugador> listaJugadores;
    private String filtroRolActual = "TODOS";

    // Caché de imágenes compartida para precarga en segundo plano
    public static final Map<String, ImageIcon> cacheImagenes = new ConcurrentHashMap<>();

    // --- Componentes visuales que se actualizan dinámicamente ---
    private JLabel lblPresupuestoTexto;
    private JLabel lblTitularesTexto;
    private JPanel panelQuinteto;      // Panel con FlowLayout para los 5 titulares
    private JPanel panelCatalogo;      // Panel interior del mercado con scroll

    // --- Paleta de colores Hextech LoL ---
    private final Color COLOR_FONDO_OSCURO = new Color(10, 20, 40);
    private final Color COLOR_PANEL_TARJETA = new Color(16, 31, 56);
    private final Color COLOR_DORADO = new Color(200, 170, 110);
    private final Color COLOR_CIAN = new Color(10, 200, 185);
    private final Color COLOR_ROJO = new Color(220, 70, 70);

    /**
     * Constructor principal: recibe los parámetros del primer JFrame.
     */
    public VentanaPrincipal(String nombreClub, String region) {
        this.nombreClub = nombreClub;
        this.region = region;
        this.listaJugadores = Jugador.obtenerListaInicial();

        // --- 1. CONFIGURACIÓN DEL JFRAME (VENTANA FIJA) ---
        setTitle("LoL Fantasy Manager - " + nombreClub + " [" + region + "]");
        setSize(1040, 750);
        setResizable(false);                  // Ventana a tamaño fijo
        setLocationRelativeTo(null);         // Centrada en pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- 2. BARRA DE MENÚS (JMenuBar, JMenu, JMenuItem) ---
        crearBarraMenu();

        // --- 3. PANEL CONTENEDOR PRINCIPAL ---
        JPanel panelContenedor = new JPanel(new BorderLayout(0, 10));
        panelContenedor.setBackground(COLOR_FONDO_OSCURO);
        panelContenedor.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Añadimos las tres secciones: Cabecera, Cuerpo (Alineación + Mercado) y Pie
        panelContenedor.add(crearPanelCabecera(), BorderLayout.NORTH);
        panelContenedor.add(crearPanelCuerpo(), BorderLayout.CENTER);
        panelContenedor.add(crearPanelInferior(), BorderLayout.SOUTH);

        setContentPane(panelContenedor);

        // Refrescamos la vista inicial del quinteto y del catálogo
        actualizarVistaQuinteto();
        actualizarCatalogoMercado();
    }

    /**
     * Construye la barra de menús superior con opciones de juego y ayuda.
     */
    private void crearBarraMenu() {
        JMenuBar barraMenu = new JMenuBar();
        barraMenu.setBackground(new Color(15, 25, 45));
        barraMenu.setBorder(new LineBorder(COLOR_DORADO, 1));

        // Menú "Juego"
        JMenu menuJuego = new JMenu("Juego");
        menuJuego.setForeground(COLOR_DORADO);

        JMenuItem itemReiniciar = new JMenuItem("Reiniciar Quinteto");
        itemReiniciar.addActionListener(e -> reiniciarQuinteto());

        JMenuItem itemSalir = new JMenuItem("Salir del Juego");
        itemSalir.addActionListener(e -> System.exit(0));

        menuJuego.add(itemReiniciar);
        menuJuego.addSeparator();
        menuJuego.add(itemSalir);

        // Menú "Ayuda"
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setForeground(COLOR_DORADO);

        JMenuItem itemReglas = new JMenuItem("Reglas del Fantasy");
        itemReglas.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                "REGLAS BÁSICAS DE LOL FANTASY:\n\n" +
                "1. Dispones de 200M de Oro para armar tu quinteto titular.\n" +
                "2. Debes fichar exactamente 1 jugador para cada rol: TOP, JGL, MID, ADC y SUP.\n" +
                "3. Puedes liberar jugadores en cualquier momento para recuperar tu oro.",
                "Reglas del Fantasy",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        menuAyuda.add(itemReglas);

        barraMenu.add(menuJuego);
        barraMenu.add(menuAyuda);

        setJMenuBar(barraMenu);
    }

    /**
     * Panel superior con datos del club, presupuesto con imagen local y puntos.
     */
    private JPanel crearPanelCabecera() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_DORADO, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));

        // Izquierda: Nombre del club y región
        JLabel lblClub = new JLabel("⚔ " + nombreClub + " (" + region + ")");
        lblClub.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClub.setForeground(COLOR_DORADO);

        // Derecha: Presupuesto y Puntos usando FlowLayout
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelStats.setOpaque(false);

        // Icono de oro local cargado con getResource()
        URL urlOro = getClass().getResource("/fantasy/recursos/moneda_oro.png");
        ImageIcon iconoOro = (urlOro != null) ? new ImageIcon(urlOro) : null;

        lblPresupuestoTexto = new JLabel(presupuesto + "M de Oro", iconoOro, SwingConstants.LEFT);
        lblPresupuestoTexto.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPresupuestoTexto.setForeground(new Color(245, 205, 60));

        lblTitularesTexto = new JLabel("Titulares: " + contarTitulares() + "/5");
        lblTitularesTexto.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitularesTexto.setForeground(COLOR_CIAN);

        panelStats.add(lblPresupuestoTexto);
        panelStats.add(lblTitularesTexto);

        panel.add(lblClub, BorderLayout.WEST);
        panel.add(panelStats, BorderLayout.EAST);

        return panel;
    }

    /**
     * Panel central que une el Quinteto Titular (arriba) y el Mercado (abajo).
     */
    private JPanel crearPanelCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 10));
        cuerpo.setOpaque(false);

        // --- 1. SECCIÓN SUPERIOR: MI QUINTETO TITULAR CON FLOWLAYOUT ---
        JPanel seccionQuinteto = new JPanel(new BorderLayout(0, 5));
        seccionQuinteto.setOpaque(false);

        JLabel lblTituloQuinteto = new JLabel("★ MI QUINTETO TITULAR (Haz clic en 'Liberar' para recuperar tu oro)", SwingConstants.LEFT);
        lblTituloQuinteto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloQuinteto.setForeground(COLOR_CIAN);
        seccionQuinteto.add(lblTituloQuinteto, BorderLayout.NORTH);

        // Panel con FlowLayout para colocar las 5 tarjetas de rol en fila horizontal
        panelQuinteto = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 5));
        panelQuinteto.setOpaque(false);
        seccionQuinteto.add(panelQuinteto, BorderLayout.CENTER);

        cuerpo.add(seccionQuinteto, BorderLayout.NORTH);

        // --- 2. SECCIÓN INFERIOR: MERCADO DE FICHAJES ---
        JPanel seccionMercado = new JPanel(new BorderLayout(0, 5));
        seccionMercado.setOpaque(false);

        // Barra de filtros por posición con FlowLayout
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panelFiltros.setOpaque(false);

        JLabel lblFiltro = new JLabel("Filtrar Mercado:");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFiltro.setForeground(Color.WHITE);
        panelFiltros.add(lblFiltro);

        String[] botonesRoles = {"TODOS", "TOP", "JGL", "MID", "ADC", "SUP"};
        for (String rol : botonesRoles) {
            JButton btnRol = new JButton(rol);
            btnRol.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnRol.setBackground(rol.equals("TODOS") ? COLOR_DORADO : new Color(30, 45, 75));
            btnRol.setForeground(rol.equals("TODOS") ? Color.BLACK : Color.WHITE);
            btnRol.setFocusPainted(false);
            btnRol.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnRol.addActionListener(e -> {
                filtroRolActual = rol;
                // Resaltamos el botón activo
                for (Component c : panelFiltros.getComponents()) {
                    if (c instanceof JButton) {
                        JButton b = (JButton) c;
                        boolean esActivo = b.getText().equals(rol);
                        b.setBackground(esActivo ? COLOR_DORADO : new Color(30, 45, 75));
                        b.setForeground(esActivo ? Color.BLACK : Color.WHITE);
                    }
                }
                actualizarCatalogoMercado();
            });
            panelFiltros.add(btnRol);
        }

        seccionMercado.add(panelFiltros, BorderLayout.NORTH);

        // Catálogo de tarjetas de pro-players con scroll vertical
        panelCatalogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        panelCatalogo.setBackground(COLOR_FONDO_OSCURO);

        JScrollPane scrollCatalogo = new JScrollPane(panelCatalogo);
        scrollCatalogo.setPreferredSize(new Dimension(1000, 310));
        scrollCatalogo.setBorder(new LineBorder(new Color(40, 60, 95), 1));
        scrollCatalogo.getVerticalScrollBar().setUnitIncrement(16); // Scroll más fluido

        seccionMercado.add(scrollCatalogo, BorderLayout.CENTER);
        cuerpo.add(seccionMercado, BorderLayout.CENTER);

        return cuerpo;
    }

    /**
     * Dibuja las 5 casillas de la alineación titular (TOP, JGL, MID, ADC, SUP).
     * Si el puesto está vacío, usa el icono local de rol_vacio.png.
     * Si está ocupado, usa la foto por URL del jugador fichado.
     */
    private void actualizarVistaQuinteto() {
        panelQuinteto.removeAll();

        for (int i = 0; i < ROLES.length; i++) {
            String rol = ROLES[i];
            Jugador jugador = quintetoTitular[i];

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(175, 210));
            card.setBackground(COLOR_PANEL_TARJETA);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(jugador != null ? COLOR_DORADO : new Color(50, 70, 100), 2),
                new EmptyBorder(6, 6, 6, 6)
            ));

            // Rol / Posición arriba
            JLabel lblRol = new JLabel("ROL: " + rol, SwingConstants.CENTER);
            lblRol.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblRol.setForeground(jugador != null ? COLOR_DORADO : Color.GRAY);
            lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lblRol);
            card.add(Box.createVerticalStrut(4));

            if (jugador == null) {
                // Hueco libre: mostramos icono local del proyecto
                ImageIcon iconoVacio = cargarImagenLocal("/fantasy/recursos/rol_vacio.png", 90, 90);
                JLabel lblFoto = new JLabel(iconoVacio);
                lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(lblFoto);

                card.add(Box.createVerticalStrut(6));
                JLabel lblEstado = new JLabel("DISPONIBLE", SwingConstants.CENTER);
                lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                lblEstado.setForeground(new Color(120, 140, 170));
                lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(lblEstado);
            } else {
                // Hueco ocupado: cargamos foto desde internet por URL en segundo plano
                JLabel lblFoto = new JLabel();
                lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
                cargarImagenEnLabel(lblFoto, jugador.getUrlFoto(), 90, 90);
                card.add(lblFoto);

                card.add(Box.createVerticalStrut(4));
                JLabel lblNick = new JLabel(jugador.getNombre(), SwingConstants.CENTER);
                lblNick.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lblNick.setForeground(Color.WHITE);
                lblNick.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(lblNick);

                JLabel lblEquipo = new JLabel(jugador.getEquipo(), SwingConstants.CENTER);
                lblEquipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                lblEquipo.setForeground(COLOR_CIAN);
                lblEquipo.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(lblEquipo);

                card.add(Box.createVerticalStrut(4));
                // Botón para vender / liberar al jugador
                final int indiceRol = i;
                JButton btnLiberar = new JButton("Liberar (" + jugador.getPrecio() + "M)");
                btnLiberar.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btnLiberar.setBackground(COLOR_ROJO);
                btnLiberar.setForeground(Color.WHITE);
                btnLiberar.setFocusPainted(false);
                btnLiberar.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnLiberar.setAlignmentX(Component.CENTER_ALIGNMENT);
                btnLiberar.addActionListener(e -> liberarJugador(indiceRol));
                card.add(btnLiberar);
            }

            panelQuinteto.add(card);
        }

        panelQuinteto.revalidate();
        panelQuinteto.repaint();
    }

    /**
     * Renderiza el catálogo de jugadores en venta según el filtro seleccionado.
     */
    private void actualizarCatalogoMercado() {
        panelCatalogo.removeAll();

        for (Jugador j : listaJugadores) {
            // Filtro por rol
            if (!filtroRolActual.equals("TODOS") && !j.getPosicion().equalsIgnoreCase(filtroRolActual)) {
                continue;
            }

            // Comprobamos si el jugador ya está fichado en el quinteto
            boolean yaFichado = false;
            for (Jugador titular : quintetoTitular) {
                if (titular != null && titular.getNombre().equals(j.getNombre())) {
                    yaFichado = true;
                    break;
                }
            }

            // Tarjeta visual del jugador
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(175, 235));
            card.setBackground(COLOR_PANEL_TARJETA);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(40, 60, 95), 1),
                new EmptyBorder(6, 6, 6, 6)
            ));

            // Foto cargada desde URL en segundo plano (para que la ventana abra de inmediato)
            JLabel lblFoto = new JLabel();
            lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
            cargarImagenEnLabel(lblFoto, j.getUrlFoto(), 85, 85);
            card.add(lblFoto);
            card.add(Box.createVerticalStrut(4));

            // Nombre y posición
            JLabel lblNick = new JLabel(j.getNombre() + " (" + j.getPosicion() + ")", SwingConstants.CENTER);
            lblNick.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblNick.setForeground(Color.WHITE);
            lblNick.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lblNick);

            // Equipo real
            JLabel lblEquipo = new JLabel(j.getEquipo(), SwingConstants.CENTER);
            lblEquipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblEquipo.setForeground(COLOR_CIAN);
            lblEquipo.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lblEquipo);

            // Precio
            JLabel lblPrecio = new JLabel("Precio: " + j.getPrecio() + "M Oro", SwingConstants.CENTER);
            lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblPrecio.setForeground(new Color(245, 205, 60));
            lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lblPrecio);

            card.add(Box.createVerticalStrut(6));

            // Botón Fichar
            JButton btnFichar = new JButton(yaFichado ? "FICHADO" : "Fichar");
            btnFichar.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnFichar.setBackground(yaFichado ? Color.GRAY : COLOR_DORADO);
            btnFichar.setForeground(yaFichado ? Color.LIGHT_GRAY : Color.BLACK);
            btnFichar.setEnabled(!yaFichado);
            btnFichar.setFocusPainted(false);
            btnFichar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnFichar.setAlignmentX(Component.CENTER_ALIGNMENT);

            btnFichar.addActionListener(e -> ficharJugador(j));
            card.add(btnFichar);

            card.add(Box.createVerticalStrut(3));

            // Botón para abrir la ficha técnica detallada del jugador en un diálogo modal
            JButton btnVerFicha = new JButton("Ver Ficha");
            btnVerFicha.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnVerFicha.setBackground(new Color(30, 50, 80));
            btnVerFicha.setForeground(Color.WHITE);
            btnVerFicha.setFocusPainted(false);
            btnVerFicha.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnVerFicha.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnVerFicha.addActionListener(e -> mostrarFichaJugador(j));
            card.add(btnVerFicha);

            panelCatalogo.add(card);
        }

        panelCatalogo.revalidate();
        panelCatalogo.repaint();
    }

    /**
     * Lógica para fichar a un jugador:
     * Verifica presupuesto y comprueba que la posición no esté ya ocupada.
     */
    private void ficharJugador(Jugador j) {
        if (presupuesto < j.getPrecio()) {
            JOptionPane.showMessageDialog(
                this,
                "No tienes suficiente oro para fichar a " + j.getNombre() + " (Necesitas " + j.getPrecio() + "M).",
                "Oro insuficiente",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Buscamos el índice del rol correspondiente
        int indiceRol = -1;
        for (int i = 0; i < ROLES.length; i++) {
            if (ROLES[i].equalsIgnoreCase(j.getPosicion())) {
                indiceRol = i;
                break;
            }
        }

        if (indiceRol == -1) return;

        if (quintetoTitular[indiceRol] != null) {
            JOptionPane.showMessageDialog(
                this,
                "Ya tienes a " + quintetoTitular[indiceRol].getNombre() + " en la posición " + j.getPosicion() + ".\n" +
                "Libéralo primero si deseas cambiarlo.",
                "Posición ocupada",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // Realizamos el fichaje
        presupuesto -= j.getPrecio();
        quintetoTitular[indiceRol] = j;

        actualizarIndicadores();
        actualizarVistaQuinteto();
        actualizarCatalogoMercado();

        JOptionPane.showMessageDialog(
            this,
            "¡Fichaje completado! " + j.getNombre() + " se une al " + nombreClub + ".",
            "Fichaje Exitoso",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Libera a un jugador del quinteto y devuelve su coste en oro al club.
     */
    private void liberarJugador(int indiceRol) {
        Jugador liberado = quintetoTitular[indiceRol];
        if (liberado != null) {
            presupuesto += liberado.getPrecio();
            quintetoTitular[indiceRol] = null;

            actualizarIndicadores();
            actualizarVistaQuinteto();
            actualizarCatalogoMercado();
        }
    }

    /**
     * Vacia el quinteto completo y restaura el presupuesto a 100M.
     */
    private void reiniciarQuinteto() {
        for (int i = 0; i < quintetoTitular.length; i++) {
            quintetoTitular[i] = null;
        }
        presupuesto = 200;
        actualizarIndicadores();
        actualizarVistaQuinteto();
        actualizarCatalogoMercado();
    }

    /**
     * Actualiza las etiquetas de presupuesto y titulares en la cabecera.
     */
    private void actualizarIndicadores() {
        lblPresupuestoTexto.setText(presupuesto + "M de Oro");
        lblTitularesTexto.setText("Titulares: " + contarTitulares() + "/5");
    }

    /**
     * Cuenta cuántos puestos del quinteto titular están actualmente ocupados.
     */
    private int contarTitulares() {
        int count = 0;
        for (Jugador t : quintetoTitular) {
            if (t != null) count++;
        }
        return count;
    }

    /**
     * Panel inferior con botón de acción usando FlowLayout.
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setOpaque(false);

        JButton btnReiniciar = new JButton("Reiniciar Quinteto");
        btnReiniciar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReiniciar.setPreferredSize(new Dimension(200, 36));
        btnReiniciar.setBackground(new Color(30, 45, 75));
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setFocusPainted(false);
        btnReiniciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReiniciar.addActionListener(e -> reiniciarQuinteto());

        panel.add(btnReiniciar);
        return panel;
    }

    /**
     * Muestra una ventana emergente modal (JDialog) con la información y estadísticas
     * completas del jugador seleccionado, incluyendo su fotografía ampliada.
     * 
     * @param j Jugador a consultar.
     */
    private void mostrarFichaJugador(Jugador j) {
        // 1. Instanciamos el JDialog en modo modal (bloquea la ventana de fondo mientras esté abierto)
        JDialog dialogo = new JDialog(this, "Ficha del Invocador: " + j.getNombre(), true);
        dialogo.setSize(380, 490);
        dialogo.setResizable(false);         // Tamaño fijo para mantener la estética
        dialogo.setLocationRelativeTo(this); // Centrado sobre la ventana principal

        // 2. Panel principal del diálogo con borde y fondo temático
        JPanel panelDialogo = new JPanel(new BorderLayout(10, 12));
        panelDialogo.setBackground(COLOR_FONDO_OSCURO);
        panelDialogo.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Título superior
        JLabel lblTitulo = new JLabel("FICHA TÉCNICA PRO PLAYER", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_DORADO);
        panelDialogo.add(lblTitulo, BorderLayout.NORTH);

        // Panel central con foto en alta resolución y estadísticas
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setOpaque(false);

        // Foto del jugador más grande (120x120) cargada de forma asíncrona
        JLabel lblFotoGrande = new JLabel();
        lblFotoGrande.setAlignmentX(Component.CENTER_ALIGNMENT);
        cargarImagenEnLabel(lblFotoGrande, j.getUrlFoto(), 120, 120);
        panelCentro.add(lblFotoGrande);

        panelCentro.add(Box.createVerticalStrut(12));

        // Datos del jugador en filas ordenadas
        String[] lineas = {
            "Nickname: " + j.getNombre(),
            "Rol / Posición: " + j.getPosicion(),
            "Equipo Competitivo: " + j.getEquipo(),
            "Campeón Firma: " + j.getCampeonFav(),
            "Puntos Promedio: " + j.getPuntosBase() + " pts",
            "Coste en el Mercado: " + j.getPrecio() + "M Oro"
        };

        for (String linea : lineas) {
            JLabel lblDato = new JLabel(linea, SwingConstants.CENTER);
            lblDato.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblDato.setForeground(Color.WHITE);
            lblDato.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelCentro.add(lblDato);
            panelCentro.add(Box.createVerticalStrut(4));
        }

        panelDialogo.add(panelCentro, BorderLayout.CENTER);

        // Panel inferior con botón cerrar usando FlowLayout
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);

        JButton btnCerrar = new JButton("Cerrar Ficha");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrar.setPreferredSize(new Dimension(140, 34));
        btnCerrar.setBackground(COLOR_DORADO);
        btnCerrar.setForeground(Color.BLACK);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Al pulsar el botón, cerramos el diálogo con dispose()
        btnCerrar.addActionListener(e -> dialogo.dispose());

        panelBoton.add(btnCerrar);
        panelDialogo.add(panelBoton, BorderLayout.SOUTH);

        // 3. Asignamos el contenido y mostramos el diálogo
        dialogo.setContentPane(panelDialogo);
        dialogo.setVisible(true);
    }

    // ========================================================
    // --- MÉTODOS AUXILIARES DE GESTIÓN Y ESCALADO DE IMÁGENES
    // ========================================================

    /**
     * Precarga todas las imágenes de los jugadores en segundo plano
     * mientras el usuario interactúa con la pantalla de inicio.
     * Al entrar en la ventana principal, las imágenes ya estarán en memoria sin retrasos.
     */
    public static void precargarImagenes() {
        new Thread(() -> {
            List<Jugador> lista = Jugador.obtenerListaInicial();
            for (Jugador j : lista) {
                if (j.getUrlFoto() != null && !j.getUrlFoto().isEmpty()) {
                    try {
                        URL url = URI.create(j.getUrlFoto()).toURL();
                        ImageIcon original = new ImageIcon(url);
                        if (original.getIconWidth() > 0) {
                            Image imgEscalada = original.getImage().getScaledInstance(85, 85, Image.SCALE_SMOOTH);
                            cacheImagenes.put(j.getUrlFoto(), new ImageIcon(imgEscalada));
                        }
                    } catch (Exception e) {
                        // Si falla una URL, continúa con las siguientes
                    }
                }
            }
        }).start();
    }

    /**
     * Carga una imagen local desde la carpeta de recursos del proyecto y la escala.
     */
    private ImageIcon cargarImagenLocal(String ruta, int ancho, int alto) {
        URL url = getClass().getResource(ruta);
        if (url == null) return null;
        ImageIcon original = new ImageIcon(url);
        Image imgEscalada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imgEscalada);
    }

    /**
     * Carga una imagen remota a partir de una URL de internet y la escala suavemente.
     * Método directo y estándar de Swing: usa new ImageIcon(url) con la URL web HTTPS.
     */
    private ImageIcon cargarImagenDesdeURL(String urlString, int ancho, int alto) {
        if (urlString == null || urlString.isEmpty()) {
            return cargarImagenLocal("/fantasy/recursos/rol_vacio.png", ancho, alto);
        }

        // Si ya está en la caché en memoria, la devolvemos inmediatamente
        if (cacheImagenes.containsKey(urlString)) {
            return cacheImagenes.get(urlString);
        }

        try {
            // Carga directa y limpia de Swing desde la URL web HTTPS
            URL url = URI.create(urlString).toURL();
            ImageIcon original = new ImageIcon(url);

            if (original.getIconWidth() > 0) {
                Image imgEscalada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                ImageIcon iconoFinal = new ImageIcon(imgEscalada);
                cacheImagenes.put(urlString, iconoFinal);
                return iconoFinal;
            }
        } catch (Exception e) {
            // Error de conexión o URL: fallback al icono local por seguridad
        }

        return cargarImagenLocal("/fantasy/recursos/rol_vacio.png", ancho, alto);
    }

    /**
     * Carga la imagen en el JLabel de forma asíncrona:
     * 1. Coloca inmediatamente el icono local para que la ventana abra al instante (0 segundos).
     * 2. Descarga la imagen de internet en un hilo en segundo plano para no congelar la ventana.
     * 3. Al terminar la descarga, actualiza el icono de la etiqueta con SwingUtilities.invokeLater.
     */
    private void cargarImagenEnLabel(JLabel label, String urlString, int ancho, int alto) {
        // Icono temporal inmediato mientras descarga
        label.setIcon(cargarImagenLocal("/fantasy/recursos/rol_vacio.png", ancho, alto));

        if (urlString == null || urlString.isEmpty()) {
            return;
        }

        // Si ya está en la memoria caché, la asignamos de inmediato
        if (cacheImagenes.containsKey(urlString)) {
            label.setIcon(cacheImagenes.get(urlString));
            return;
        }

        // Descarga en hilo secundario (no bloquea la interfaz de usuario)
        new Thread(() -> {
            ImageIcon iconoDescargado = cargarImagenDesdeURL(urlString, ancho, alto);
            if (iconoDescargado != null) {
                SwingUtilities.invokeLater(() -> {
                    label.setIcon(iconoDescargado);
                });
            }
        }).start();
    }
}
