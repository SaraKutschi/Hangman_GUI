import java.util.*;
/**
 * Verwaltet die Logik des Hangman-Spiels:
 * - Wortwahl, Treffer, Fehler, Sieg/Niederlage
 * - Wird von der HangmanForm für die Spielmechanik verwendet
 */
public class Game {

    // === Konstanten ===
    private static final List<String> WORDS = List.of(
            "JAVA", "HANGMAN", "PROGRAMMIEREN", "LAPTOP", "SWING",
            "CODE", "DEBUG", "PIXEL", "KI", "SCHULE"
    );

    private int maxFails = 11;

    // === Attribute ===
    private String word;
    private StringBuilder guessed;
    private int fails;

    private final Random rand = new Random();

    // === Konstruktor ===
    public Game() {
        reset();
    }

    // === Methoden ===

    /**
     * Prüft, ob der geratene Buchstabe im Wort vorkommt.
     * Wenn ja: fügt ihn an allen passenden Positionen ein.
     * Wenn nein: erhöht die Fehlerzahl.
     *
     * @param c Buchstabe, der geraten wurde
     * @return true, wenn der Buchstabe enthalten war
     */
    public boolean guessLetter(char c) {
        c = Character.toUpperCase(c);
        boolean found = false;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == c) {
                guessed.setCharAt(i, c);
                found = true;
            }
        }

        if (!found) fails++;
        return found;
    }

    /**
     * Formatiert den aktuellen Stand des geratenen Wortes,
     * z. B. "_ A _ A _".
     */
    private String formatGuessedWord() {
        StringBuilder spaced = new StringBuilder();
        for (int i = 0; i < guessed.length(); i++) {
            spaced.append(guessed.charAt(i)).append(' ');
        }
        return spaced.toString().trim();
    }

    /** Gibt das formatierte, bisher erratene Wort zurück. */
    public String getGuessedWord() { return formatGuessedWord(); }

    /** Prüft, ob das Spiel vorbei ist (verloren oder gewonnen). */
    public boolean isGameOver() {
        return fails >= maxFails || hasWon();
    }

    /** Prüft, ob das ganze Wort erraten wurde. */
    public boolean hasWon() {
        return guessed.toString().equals(word);
    }

    // === Getter ===
    public int getFails() { return fails; }
    public int getMaxFails() { return maxFails; }

    /**
     * Setzt ein neues Limit für die erlaubten Fehlversuche.
     * Werte werden auf 1–11 begrenzt (Sicherheitscheck).
     */
    public void setMaxFails(int newMax) {
        if (newMax < 1) newMax = 1;
        if (newMax > 11) newMax = 11;
        this.maxFails = newMax;

        if (fails > maxFails) fails = maxFails;
    }

    public String getWord() { return word; }

    /**
     * Startet ein neues Spiel:
     * - setzt Fehler auf 0
     * - wählt ein neues zufälliges Wort
     * - erstellt ein neues "_ _ _" Muster für guessed
     */
    public void reset() {
        fails = 0;
        word = WORDS.get(rand.nextInt(WORDS.size()));
        guessed = new StringBuilder("_".repeat(word.length()));
    }
}
