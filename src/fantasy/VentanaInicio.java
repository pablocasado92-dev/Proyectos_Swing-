package fantasy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Primera pantalla del juego (Primer JFrame).
 * Permite al usuario configurar el nombre de su club de Esports y elegir la liga/región.
 */
public class VentanaInicio extends JFrame {

    // Componentes interactivos del formulario
    private JTextField txtNombreClub;
    private JComboBox<String> cbRegion;
    private JButton btnComenzar;

    public VentanaInicio() {
        // --- 1. CONFIGURACIÓN DE LA VENTANA (TAMAÑO FIJO) ---
        setTitle("LoL Fantasy Manager - Configuración Inicial");
        setSize(520, 480);
        setResizable(false);                  // Ventana de tamaño fijo
        setLocationRelativeTo(null);         // Centrada en la pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Color de fondo oscuro estilo League of Legends
        Color fondoHextech = new Color(10, 20, 40);
        Color doradoHextech = new Color(200, 170, 110);
        Color cianHextech = new Color(10, 200, 185);

        // Panel principal con BorderLayout y márgenes interiores
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 15));
        panelPrincipal.setBackground(fondoHextech);
        panelPrincipal.setBorder(new EmptyBorder(20, 25, 20, 25));

        // --- 2. ZONA NORTE: LOGO LOCAL DEL PROYECTO ---
        // Se carga la imagen local desde la carpeta de recursos usando el classpath
        URL urlLogo = getClass().getResource("/fantasy/recursos/logo_fantasy.png");
        JLabel lblLogo;
        if (urlLogo != null) {
            ImageIcon iconoOriginal = new ImageIcon(urlLogo);
            // Si fuera necesario reescalar proporcionalmente (según apuntes):
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(380, 100, Image.SCALE_SMOOTH);
            lblLogo = new JLabel(new ImageIcon(imgEscalada));
        } else {
            // Fallback por seguridad si no encontrara la ruta
            lblLogo = new JLabel("LOL FANTASY MANAGER", SwingConstants.CENTER);
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblLogo.setForeground(doradoHextech);
        }
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblLogo, BorderLayout.NORTH);

        // --- 3. ZONA CENTRO: FORMULARIO DE CREACIÓN DEL CLUB ---
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setOpaque(false); // Fondo transparente para ver el azul oscuro

        // Etiqueta y campo de texto para el nombre del club
        JLabel lblNombre = new JLabel("Nombre de tu Club de Esports:");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtNombreClub = new JTextField("KOI Gaming");
        txtNombreClub.setMaximumSize(new Dimension(340, 35));
        txtNombreClub.setPreferredSize(new Dimension(340, 35));
        txtNombreClub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombreClub.setBackground(new Color(25, 35, 60));
        txtNombreClub.setForeground(Color.WHITE);
        txtNombreClub.setCaretColor(cianHextech);
        txtNombreClub.setHorizontalAlignment(JTextField.CENTER);
        txtNombreClub.setBorder(BorderFactory.createLineBorder(doradoHextech, 1));
        txtNombreClub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Etiqueta y combo box para la región/liga
        JLabel lblRegion = new JLabel("Región competitiva:");
        lblRegion.setForeground(Color.WHITE);
        lblRegion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRegion.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] regiones = {
            "LEC (Europa)",
            "LCK (Corea del Sur)",
            "LPL (China)",
            "LCS (Norteamérica)"
        };
        cbRegion = new JComboBox<>(regiones);
        cbRegion.setMaximumSize(new Dimension(340, 35));
        cbRegion.setPreferredSize(new Dimension(340, 35));
        cbRegion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbRegion.setBackground(new Color(25, 35, 60));
        cbRegion.setForeground(Color.WHITE);
        cbRegion.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Información de presupuesto inicial
        JLabel lblPresupuesto = new JLabel("Presupuesto inicial: 100M de Oro Hextech");
        lblPresupuesto.setForeground(cianHextech);
        lblPresupuesto.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblPresupuesto.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Añadimos los elementos con espaciado vertical
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(lblNombre);
        panelFormulario.add(Box.createVerticalStrut(6));
        panelFormulario.add(txtNombreClub);
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(lblRegion);
        panelFormulario.add(Box.createVerticalStrut(6));
        panelFormulario.add(cbRegion);
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(lblPresupuesto);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        // --- 4. ZONA SUR: BOTÓN DE ACCIÓN CON DISPOSE() Y PASO DE PARÁMETROS ---
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);

        btnComenzar = new JButton("Entrar a la Grieta del Invocador");
        btnComenzar.setPreferredSize(new Dimension(300, 42));
        btnComenzar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComenzar.setBackground(doradoHextech);
        btnComenzar.setForeground(new Color(10, 20, 40));
        btnComenzar.setFocusPainted(false);
        btnComenzar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Evento al pulsar el botón: validación, dispose() y paso de parámetros
        btnComenzar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJuego();
            }
        });

        panelBoton.add(btnComenzar);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        // Asignamos el panel a la ventana
        setContentPane(panelPrincipal);
    }

    /**
     * Valida los campos, cierra la ventana actual con dispose()
     * y abre la VentanaPrincipal pasando los parámetros introducidos por el usuario.
     */
    private void iniciarJuego() {
        String nombre = txtNombreClub.getText().trim();
        String region = (String) cbRegion.getSelectedItem();

        // Control de validación sencillo
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor, escribe un nombre para tu club de Esports.",
                "Nombre requerido",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 1. Cerramos la ventana actual liberando sus recursos
        this.dispose();

        // 2. Abrimos la ventana principal pasando los parámetros (nombre y región)
        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(nombre, region);
        ventanaPrincipal.setVisible(true);
    }
}

