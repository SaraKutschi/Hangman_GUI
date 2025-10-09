public class Game {
    private String word = "HALLO";
    private StringBuilder guessed;
    private int fails = 0;
    private int maxFails = 11;

    public Game() {
        guessed = new StringBuilder("_".repeat(word.length()));
    }

    public boolean guessLetter(char c) {
        boolean found = false;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == Character.toUpperCase(c)) {
                guessed.setCharAt(i, Character.toUpperCase(c));
                found = true;
            }
        }

        if (!found) fails++;
        return found;
    }

    public String getGuessedWord() {
        return guessed.toString();
    }

    public boolean isGameOver() {
        return fails >= maxFails || guessed.toString().equals(word);
    }

    public boolean hasWon() {
        return guessed.toString().equals(word);
    }

    public int getFails() {
        return fails;
    }

    public int getMaxFails() {
        return maxFails;
    }
}
