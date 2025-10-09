import javax.swing.*;
import java.awt.*;

public class HangmanForm {
    private JPanel MainPanel;
    private JPanel GamePanel;
    private JPanel LetterPanel;
    private JButton aBtn, bBtn, cBtn, dBtn, eBtn, fBtn, gBtn, hBtn, iBtn, jBtn, kBtn, lBtn, mbtn, nBtn, oBtn,
            pBtn, qBtn, rBtn, sBtn, tBtn, uBtn, vBtn, wBtn, xBtn, yBtn, zBtn;
    private JPanel HeaderPanel;
    private JPanel ImagePanel;
    private JPanel InfoPanel;
    private JLabel wordLbl;
    private JLabel failLbl;

    // Logic
    private final Game game = new Game();

    private JLabel imageLbl;
    private ImageIcon[] hangmanImages; 

    public HangmanForm() {
        // === Bildbereich ===
        imageLbl = new JLabel("", SwingConstants.CENTER);
        ImagePanel.setLayout(new BorderLayout());
        ImagePanel.add(imageLbl, BorderLayout.CENTER);

        hangmanImages = new ImageIcon[12]; // 0..11
        for (int i = 0; i < hangmanImages.length; i++) {
            String path = "/Images/hangman" + i + ".jpg";
            var url = getClass().getResource(path);
            hangmanImages[i] = (url != null) ? new ImageIcon(url) : null;
        }

        wordLbl.setText(game.getGuessedWord());
        failLbl.setText("Fehler: 0 / " + game.getMaxFails());
        setHangmanImage(game.getFails());

        wireLetterButtons();

        ImagePanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                setHangmanImage(game.getFails());
            }
        });
    }

    // Skaliert passend ins Panel
    private void setHangmanImage(int fails) {
        int idx = Math.max(0, Math.min(fails, hangmanImages.length - 1)); // 0..11
        ImageIcon icon = hangmanImages[idx];
        if (icon == null) { imageLbl.setIcon(null); return; }

        int w = Math.max(1, ImagePanel.getWidth() == 0 ? 250 : ImagePanel.getWidth());
        int h = Math.max(1, ImagePanel.getHeight() == 0 ? 250 : ImagePanel.getHeight());
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        imageLbl.setIcon(new ImageIcon(scaled));
    }

    private void wireLetterButtons() {
        Object[][] map = {
                {aBtn, 'A'}, {bBtn, 'B'}, {cBtn, 'C'}, {dBtn, 'D'}, {eBtn, 'E'},
                {fBtn, 'F'}, {gBtn, 'G'}, {hBtn, 'H'}, {iBtn, 'I'}, {jBtn, 'J'},
                {kBtn, 'K'}, {lBtn, 'L'}, {mbtn, 'M'}, {nBtn, 'N'}, {oBtn, 'O'},
                {pBtn, 'P'}, {qBtn, 'Q'}, {rBtn, 'R'}, {sBtn, 'S'}, {tBtn, 'T'},
                {uBtn, 'U'}, {vBtn, 'V'}, {wBtn, 'W'}, {xBtn, 'X'}, {yBtn, 'Y'}, {zBtn, 'Z'}
        };
        for (Object[] pair : map) {
            JButton btn = (JButton) pair[0];
            char letter = (char) pair[1];
            if (btn != null) {
                btn.addActionListener(e -> {
                    btn.setEnabled(false); // nicht doppelt raten
                    handleGuess(letter);
                });
            }
        }
    }

    private void handleGuess(char letter) {
        boolean correct = game.guessLetter(letter);

        // Labels updaten
        wordLbl.setText(game.getGuessedWord());
        failLbl.setText("Fehler: " + game.getFails() + " / " + game.getMaxFails());

        // Bild updaten
        setHangmanImage(game.getFails());

        // Ende checken
        if (game.isGameOver()) {
            disableAllLetterButtons();
            JOptionPane.showMessageDialog(MainPanel,
                    game.hasWon() ? "Du hast gewonnen 🎉" : "Leider verloren 😢",
                    "Hangman", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void disableAllLetterButtons() {
        for (Component c : LetterPanel.getComponents()) {
            if (c instanceof JButton) c.setEnabled(false);
        }
    }

    public JPanel getMainPanel() {
        return MainPanel;
    }
}
