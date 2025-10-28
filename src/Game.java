import java.util.*;

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

    public boolean guessLetter(char c) {
        c = Character.toUpperCase(c);
        boolean found = false;

        // geht jeden Buchstaben des Wortes durch
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == c) {
                guessed.setCharAt(i, c);
                found = true;
            }
        }

        if (!found) fails++;
        return found;
    }

    private String formatGuessedWord() {
        StringBuilder spaced = new StringBuilder();
        for (int i = 0; i < guessed.length(); i++) {
            spaced.append(guessed.charAt(i)).append(' ');
        }
        return spaced.toString().trim();
    }

    public String getGuessedWord() { return formatGuessedWord(); }

    public boolean isGameOver() {
        return fails >= maxFails || hasWon();
    }

    public boolean hasWon() {
        return guessed.toString().equals(word);
    }

    // Getter für Fehler & Limit
    public int getFails() { return fails; }
    public int getMaxFails() { return maxFails; }

    // setzt die maximale Fehleranzahl (z. B. aus den Settings)
    public void setMaxFails(int newMax) {
        if (newMax < 1) newMax = 1;
        if (newMax > 11) newMax = 11;
        this.maxFails = newMax;

        if (fails > maxFails) fails = maxFails;
    }

    public String getWord() { return word; }

    public void reset() {
        fails = 0;
        word = WORDS.get(rand.nextInt(WORDS.size()));
        guessed = new StringBuilder("_".repeat(word.length()));
    }
}
