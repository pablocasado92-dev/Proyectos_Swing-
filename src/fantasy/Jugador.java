package fantasy;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa a un jugador profesional de League of Legends en el Fantasy.
 * Guarda sus datos básicos, precio de fichaje, posición y la URL de internet con su fotografía.
 */
public class Jugador {

    // --- Atributos del jugador ---
    private String nombre;        // Nombre o nickname del jugador
    private String posicion;      // Posición en la Grieta: "TOP", "JGL", "MID", "ADC", "SUP"
    private String equipo;        // Equipo real del jugador
    private int precio;           // Coste de fichaje en millones de oro
    private String urlFoto;       // URL de internet con la fotografía del jugador
    private int puntosBase;       // Puntuación promedio por jornada
    private String campeonFav;    // Campeón más característico del jugador

    /**
     * Constructor para inicializar todos los datos del jugador.
     */
    public Jugador(String nombre, String posicion, String equipo, int precio, String urlFoto, int puntosBase, String campeonFav) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.equipo = equipo;
        this.precio = precio;
        this.urlFoto = urlFoto;
        this.puntosBase = puntosBase;
        this.campeonFav = campeonFav;
    }

    // --- Getters ---

    public String getNombre() {
        return nombre;
    }

    public String getPosicion() {
        return posicion;
    }

    public String getEquipo() {
        return equipo;
    }

    public int getPrecio() {
        return precio;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public int getPuntosBase() {
        return puntosBase;
    }

    public String getCampeonFav() {
        return campeonFav;
    }

    /**
     * Devuelve una lista inicial con jugadores profesionales para el mercado de fichajes.
     * Incluye opciones para cada una de las cinco posiciones de LoL (TOP, JGL, MID, ADC, SUP).
     * Las fotografías están alojadas en la nube de Firebase Storage y se descargan mediante sus URLs públicas.
     */
    public static List<Jugador> obtenerListaInicial() {
        List<Jugador> lista = new ArrayList<>();

        // --- TOP LANERS ---
        lista.add(new Jugador(
            "Zeus", "TOP", "Hanwha Life", 28,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FT1_Zeus_2024_Split_1.png?alt=media&token=200eb46f-35d1-4740-ae40-4d51863c89c5",
            82, "Aatrox"
        ));
        lista.add(new Jugador(
            "BrokenBlade", "TOP", "G2 Esports", 20,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FG2_BrokenBlade_2026_Split_1.png?alt=media&token=c2057ae0-2c5b-4c5c-b11d-218124af0feb",
            75, "Ornn"
        ));
        lista.add(new Jugador(
            "Myrwn", "TOP", "KOI", 22,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FMKOI_Myrwn_2026_Split_1.png?alt=media&token=6674f666-fc62-448d-b848-49125857da22",
            78, "Gwen"
        ));

        // --- JUNGLERS ---
        lista.add(new Jugador(
            "Elyoya", "JGL", "KOI", 24,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FMKOI_Elyoya_2026_Split_2.png?alt=media&token=73868b24-0376-47c1-9f58-97887b0f4d0d",
            80, "Lee Sin"
        ));
        lista.add(new Jugador(
            "Oner", "JGL", "T1", 26,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FT1_Oner_2026_LCK_Cup.png?alt=media&token=038b20e1-154e-4c9e-b277-440fa564c221",
            84, "Xin Zhao"
        ));
        lista.add(new Jugador(
            "Yike", "JGL", "Karmine Corp", 18,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FKC_Yike_2026_Split_2.png?alt=media&token=78ae6ac7-6378-4b1e-ab05-f72d62693226",
            72, "Bel'Veth"
        ));

        // --- MID LANERS ---
        lista.add(new Jugador(
            "Faker", "MID", "T1", 35,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FT1_Faker_2026_LCK_Cup.png?alt=media&token=982edf2b-e269-4d40-8fa3-7e69b4762f9c",
            95, "Azir / Ahri"
        ));
        lista.add(new Jugador(
            "Caps", "MID", "G2 Esports", 30,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FG2_Caps_2026_Split_1.png?alt=media&token=68882c85-047d-4619-b8e5-078c27125c96",
            90, "LeBlanc"
        ));
        lista.add(new Jugador(
            "Chovy", "MID", "Gen.G", 32,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FGEN_Chovy_2026_Split_1.png?alt=media&token=ea09a733-5daa-4c54-818f-74cd217ab8c8",
            92, "Yone / Akali"
        ));

        // --- ADC (BOT LANERS) ---
        lista.add(new Jugador(
            "Rekkles", "ADC", "Fnatic / T1", 22,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FLR_Rekkles_2026_Split_1.png?alt=media&token=6cbddd31-8364-4acb-b26e-064b848d564f",
            78, "Tristana / Jinx"
        ));
        lista.add(new Jugador(
            "Deft", "ADC", "KT Rolster", 25,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FKT_Deft_2024_Split_2.png?alt=media&token=89c01076-cd7f-487e-ba56-0199c46ec0a2",
            83, "Ezreal"
        ));
        lista.add(new Jugador(
            "Gumayusi", "ADC", "T1", 27,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FHLE_Gumayusi_2026_Split_1.png?alt=media&token=c394ebc7-8448-4542-aa5f-c9c0f545edf4",
            86, "Varus / Lucian"
        ));

        // --- SUPPORTS ---
        lista.add(new Jugador(
            "Keria", "SUP", "T1", 28,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FT1_Keria_2026_LCK_Cup.png?alt=media&token=b1e06e79-c37f-4d71-b307-794b391957d7",
            88, "Thresh / Bard"
        ));
        lista.add(new Jugador(
            "Mikyx", "SUP", "Fnatic", 20,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FSK_Mikyx_2026_Split_3.png?alt=media&token=7ad62c89-40a2-4c4e-b7f8-e59a05282bce",
            76, "Nautilus"
        ));
        lista.add(new Jugador(
            "Alvaro", "SUP", "KOI", 17,
            "https://firebasestorage.googleapis.com/v0/b/lolfantasy-ac1e5.firebasestorage.app/o/JugadoresLOL%2FMKOI_Alvaro_2026_Split_1.png?alt=media&token=9148d783-e108-437a-8a39-22a7238f4535",
            71, "Leona"
        ));

        return lista;
    }
}

