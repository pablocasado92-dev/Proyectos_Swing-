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

    // --- Getters y Setters ---

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
     * Las fotos provienen de URLs públicas de internet (Wikimedia Commons y CDN oficial de Riot).
     */
    public static List<Jugador> obtenerListaInicial() {
        List<Jugador> lista = new ArrayList<>();

        // --- TOP LANERS ---
        lista.add(new Jugador(
            "Zeus", "TOP", "Hanwha Life", 28,
            "https://upload.wikimedia.org/wikipedia/commons/e/e3/Zeus_2024_post-match_interview.jpg",
            82, "Aatrox"
        ));
        lista.add(new Jugador(
            "BrokenBlade", "TOP", "G2 Esports", 20,
            "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/Ornn.png",
            75, "Ornn"
        ));
        lista.add(new Jugador(
            "369", "TOP", "Top Esports", 22,
            "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/Jax.png",
            78, "Jax"
        ));

        // --- JUNGLERS ---
        lista.add(new Jugador(
            "Elyoya", "JGL", "MAD Lions KOI", 24,
            "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/LeeSin.png",
            80, "Lee Sin"
        ));
        lista.add(new Jugador(
            "Oner", "JGL", "T1", 26,
            "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/XinZhao.png",
            84, "Xin Zhao"
        ));
        lista.add(new Jugador(
            "Yike", "JGL", "Karmine Corp", 18,
            "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/Belveth.png",
            72, "Bel'Veth"
        ));

        // --- MID LANERS ---
        lista.add(new Jugador(
            "Faker", "MID", "T1", 35,
            "https://upload.wikimedia.org/wikipedia/commons/5/5c/T1_Faker_Lee_Sang-Hyeok_in_a_2026_Interview.png",
            95, "Azir / Ahri"
        ));
        lista.add(new Jugador(
            "Caps", "MID", "G2 Esports", 30,
            "https://upload.wikimedia.org/wikipedia/commons/1/18/Caps_2025.jpg",
            90, "LeBlanc"
        ));
        lista.add(new Jugador(
            "Chovy", "MID", "Gen.G", 32,
            "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/Yone.png",
            92, "Yone / Akali"
        ));

        // --- ADC (BOT LANERS) ---
        lista.add(new Jugador(
            "Rekkles", "ADC", "Fnatic / T1", 22,
            "https://upload.wikimedia.org/wikipedia/commons/7/7c/Rekkles_2020.jpg",
            78, "Tristana / Jinx"
        ));
        lista.add(new Jugador(
            "Deft", "ADC", "KT Rolster", 25,
            "https://upload.wikimedia.org/wikipedia/commons/a/a3/Deft_interview_in_2022.jpg",
            83, "Ezreal"
        ));
        lista.add(new Jugador(
            "Gumayusi", "ADC", "T1", 27,
            "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/Varus.png",
            86, "Varus / Lucian"
        ));

        // --- SUPPORTS ---
        lista.add(new Jugador(
            "Keria", "SUP", "T1", 28,
            "https://upload.wikimedia.org/wikipedia/commons/6/6e/Keria%2C_2023_worlds_winning_team_interview.jpg",
            88, "Thresh / Bard"
        ));
        lista.add(new Jugador(
            "Mikyx", "SUP", "Fnatic", 20,
            "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/Nautilus.png",
            76, "Nautilus"
        ));
        lista.add(new Jugador(
            "Alvaro", "SUP", "MAD Lions KOI", 17,
            "https://ddragon.leagueoflegends.com/cdn/14.1.1/img/champion/Leona.png",
            71, "Leona"
        ));

        return lista;
    }
}

