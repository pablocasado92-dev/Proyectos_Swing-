package fantasy;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal para el juego LoL Fantasy Manager.
 */
public class Fantasy {

    public static void main(String[] args) {
        // Ejecutamos la interfaz dentro del hilo de despacho de eventos de Swing (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VentanaInicio inicio = new VentanaInicio();
                inicio.setVisible(true);
            }
        });
    }
}
