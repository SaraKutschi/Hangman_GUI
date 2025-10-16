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

    private final Game game = new Game();

    private JLabel imageLbl;
    private ImageIcon[] hangmanImages;

    // === kompaktere Konstanten ===
    private static final int IMG_MAX_W = 300;
    private static final int IMG_MAX_H = 200;
    private static final int FRAME_W = 400;
    private static final int FRAME_H = 450;

    public HangmanForm() {
        imageLbl = new JLabel("", SwingConstants.CENTER);
        ImagePanel.setLayout(new BorderLayout());
        ImagePanel.add(imageLbl, BorderLayout.CENTER);
        ImagePanel.setPreferredSize(new Dimension(IMG_MAX_W, IMG_MAX_H));

        MainPanel.setPreferredSize(new Dimension(FRAME_W, FRAME_H));

        wordLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        failLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // === Bilder laden ===
        hangmanImages = new ImageIcon[12];
        for (int i = 0; i < hangmanImages.length; i++) {
            String path = "/Image/hangman" + i + ".jpeg";
            var url = getClass().getResource(path);
            hangmanImages[i] = (url != null) ? new ImageIcon(url) : null;
        }

        wordLbl.setText(game.getGuessedWord());
        failLbl.setText("Fehler: 0 / " + game.getMaxFails());
        setHangmanImage(game.getFails());

        // === kompaktere Buchstabenbuttons ===
        wireLetterButtons();
        LetterPanel.setLayout(new GridLayout(3, 9, 4, 4)); // dichter gepackt
        for (Component c : LetterPanel.getComponents()) {
            if (c instanceof JButton btn) {
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.setPreferredSize(new Dimension(35, 28));
                btn.setMargin(new Insets(1, 4, 1, 4));
            }
        }

        ImagePanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                setHangmanImage(game.getFails());
            }
        });
    }

    // === kleinere Bildskalierung ===
    private void setHangmanImage(int fails) {
        int idx = Math.max(0, Math.min(fails, hangmanImages.length - 1));
        ImageIcon icon = hangmanImages[idx];
        if (icon == null) { imageLbl.setIcon(null); return; }

        int panelW = Math.min(ImagePanel.getWidth(), IMG_MAX_W);
        int panelH = Math.min(ImagePanel.getHeight(), IMG_MAX_H);

        int imgW = icon.getIconWidth();
        int imgH = icon.getIconHeight();

        double scale = Math.min(panelW / (double) imgW, panelH / (double) imgH);
        int w = (int) (imgW * scale);
        int h = (int) (imgH * scale);

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
                    btn.setEnabled(false);
                    handleGuess(letter);
                });
            }
        }
    }

    private void handleGuess(char letter) {
        boolean correct = game.guessLetter(letter);

        wordLbl.setText(game.getGuessedWord());
        failLbl.setText("Fehler: " + game.getFails() + " / " + game.getMaxFails());
        setHangmanImage(game.getFails());

        if (game.isGameOver()) {
            disableAllLetterButtons();
            JOptionPane.showMessageDialog(MainPanel,
                    game.hasWon() ? "Du hast gewonnen!" : "Leider verloren!",
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
