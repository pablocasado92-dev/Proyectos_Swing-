package fantasy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Pantalla principal del juego (Segundo JFrame).
 * Recibe por parámetro el nombre del club y la región elegida en la primera pantalla.
 * 
 * En el siguiente paso completaremos aquí:
 * - El quinteto titular y filtros con FlowLayout.
 * - El catálogo de pro-players con fotos de internet (URL).
 * - El JDialog simple para ver las fichas técnicas.
 */
public class VentanaPrincipal extends JFrame {

    // Parámetros recibidos del primer JFrame
    private String nombreClub;
    private String region;

    // Variables de estado del juego
    private int presupuesto = 100; // 100 Millones de Oro iniciales
    private int puntosTotales = 0;

    /**
     * Constructor que recibe los parámetros desde VentanaInicio.
     */
    public VentanaPrincipal(String nombreClub, String region) {
        this.nombreClub = nombreClub;
        this.region = region;

        // Configuración de la ventana (tamaño fijo)
        setTitle("LoL Fantasy Manager - " + nombreClub + " (" + region + ")");
        setSize(1000, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel provisional para probar la navegación
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(10, 20, 40));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblBienvenida = new JLabel("¡Bienvenido, Entrenador del " + nombreClub + "!", SwingConstants.CENTER);
        lblBienvenida.setForeground(new Color(200, 170, 110));
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblSub = new JLabel("Parámetros recibidos con éxito: Región " + region + " | Presupuesto: " + presupuesto + "M", SwingConstants.CENTER);
        lblSub.setForeground(new Color(10, 200, 185));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        panel.add(lblBienvenida, BorderLayout.CENTER);
        panel.add(lblSub, BorderLayout.SOUTH);

        setContentPane(panel);
    }
}

