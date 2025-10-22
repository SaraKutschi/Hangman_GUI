import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.net.URL;

public class HangmanForm {
    private JPanel MainPanel;
    private JPanel GamePanel;
    private JPanel LetterPanel;
    private JPanel HeaderPanel;
    private JButton aBtn, bBtn, cBtn, dBtn, eBtn, fBtn, gBtn, hBtn, iBtn, jBtn, kBtn, lBtn,
            mBtn, nBtn, oBtn, pBtn, qBtn, rBtn, sBtn, tBtn, uBtn, vBtn, wBtn, xBtn, yBtn, zBtn;
    private JPanel ImagePanel;
    private JPanel InfoPanel;
    private JToolBar Menu;
    private JButton settingsBtn;
    private JButton restartBtn;
    private JLabel wordLbl;
    private JLabel failLbl;

    // ==== Game ====
    private final Game game = new Game();

    // ==== Image ====
    private JLabel imageLbl;
    private ImageIcon currentRawIcon;
    private int picCounter = PIC_MIN;

    // ==== Theme (unverändert) ====
    private static final Color KEY_FG       = new Color(255, 255, 255);
    private static final Color KEY_BG       = new Color(90, 74, 69);
    private static final Color KEY_BG_HOVER = new Color(106, 88, 82);
    private static final Color KEY_BG_DOWN  = new Color(75, 61, 56);
    private static final Color KEY_BG_OFF   = new Color(141, 123, 117);
    private static final Color KEY_FG_OFF   = new Color(245, 236, 232);

    private static final Color IMG_BG = new Color(243, 242, 238);

    // ==== Layout- & Bild-Konstanten (unverändert) ====
    private static final int PIC_MIN = 1;
    private static final int PIC_MAX = 10;
    private static final String IMG_FMT = "/Image/hangman%d.jpeg";
    private static final int IMG_PADDING = 8;

    // ==== Keyboard Layout (gleiches „Look & Feel“) ====
    private static final String[] KEY_ROWS = {"ABCDEFG", "HIJKLMN", "OPQRSTU", "VWXYZ"};
    private static final Dimension KEY_SIZE = new Dimension(36, 28);
    private static final int KEY_GAP = 6;
    private static final int KEY_ARC = 10;

    public HangmanForm() {
        // === Bildbereich ===
        ImagePanel.setLayout(new BorderLayout());
        ImagePanel.setPreferredSize(new Dimension(250, 250));
        ImagePanel.setBackground(IMG_BG);

        imageLbl = new JLabel("", SwingConstants.CENTER);
        imageLbl.setOpaque(true);
        imageLbl.setBackground(IMG_BG);
        imageLbl.setBorder(new EmptyBorder(IMG_PADDING, IMG_PADDING, IMG_PADDING, IMG_PADDING));
        ImagePanel.add(imageLbl, BorderLayout.CENTER);

        // === Header / Toolbar ===
        Menu.setFloatable(false);
        Menu.setRollover(true);
        for (JButton b : new JButton[]{restartBtn, settingsBtn}) {
            b.setFocusable(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        restartBtn.addActionListener(e -> restartGame());
        settingsBtn.addActionListener(e -> openSettings());

        HeaderPanel.setLayout(new BorderLayout());
        HeaderPanel.add(Menu, BorderLayout.NORTH);

        // === Initialstatus & Bild ===
        updateStatus();
        loadRaw(picCounter);
        fitImageToCard();
        ImagePanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                fitImageToCard();
            }
        });

        // === Keyboard bauen ===
        buildKeyboard();
    }

    // ======= Actions =======
    private void restartGame() {
        game.reset();
        updateStatus();
        loadRaw(PIC_MIN);
        fitImageToCard();
        buildKeyboard(); // alle Tasten wieder aktivieren
    }

    private void openSettings() {
        Window owner = SwingUtilities.getWindowAncestor(MainPanel);
        Settings dlg = new Settings(owner, game.getMaxFails(), newMax -> {
            game.setMaxFails(newMax);
            updateStatus();
            // optional: restartGame();
        });
        dlg.setVisible(true);
    }

    private void handleGuess(char letter) {
        boolean correct = game.guessLetter(letter);
        updateStatus();

        // Bildschritt: (fails + 1) im gültigen Bereich halten
        int idx = Math.min(PIC_MAX, Math.max(PIC_MIN, game.getFails() + 1));
        loadRaw(idx);
        fitImageToCard();

        if (game.isGameOver()) {
            disableAllLetterButtons();
            JOptionPane.showMessageDialog(
                    MainPanel,
                    game.hasWon() ? "Du hast gewonnen!" : "Leider verloren.\nDas Wort war: " + game.getWord(),
                    "Hangman",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // ======= Status & Bild =======
    private void updateStatus() {
        wordLbl.setText(game.getGuessedWord());
        failLbl.setText("Fehler: " + game.getFails() + " / " + game.getMaxFails());
    }

    private void loadRaw(int index) {
        // Wrap-around (unverändert)
        if (index < PIC_MIN) index = PIC_MAX;
        if (index > PIC_MAX) index = PIC_MIN;
        picCounter = index;

        String path = String.format(IMG_FMT, picCounter);
        URL url = getClass().getResource(path);

        if (url == null) {
            currentRawIcon = null;
            imageLbl.setIcon(null);
            imageLbl.setText("Bild fehlt: " + path);
            return;
        }
        currentRawIcon = new ImageIcon(url);
        imageLbl.setText(null);
    }

    private void fitImageToCard() {
        if (currentRawIcon == null) return;

        int panelW = Math.max(1, ImagePanel.getWidth() - IMG_PADDING);
        int panelH = Math.max(1, ImagePanel.getHeight() - IMG_PADDING);

        int imgW = currentRawIcon.getIconWidth();
        int imgH = currentRawIcon.getIconHeight();

        double scale = Math.min(panelW / (double) imgW, panelH / (double) imgH);
        int w = (int) Math.max(1, Math.round(imgW * scale));
        int h = (int) Math.max(1, Math.round(imgH * scale));

        Image scaled = currentRawIcon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        imageLbl.setIcon(new ImageIcon(scaled));
        imageLbl.setText(null);
        imageLbl.setHorizontalAlignment(SwingConstants.CENTER);
        imageLbl.setVerticalAlignment(SwingConstants.CENTER);
    }

    // ======= Keyboard =======
    private void buildKeyboard() {
        LetterPanel.removeAll();
        LetterPanel.setLayout(new BoxLayout(LetterPanel, BoxLayout.Y_AXIS));
        LetterPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        LetterPanel.setOpaque(true);

        for (String rowSpec : KEY_ROWS) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, KEY_GAP, 0));
            row.setOpaque(false);

            for (char ch : rowSpec.toCharArray()) {
                JButton btn = makeKeyButton(ch);
                row.add(btn);
            }

            LetterPanel.add(row);
            LetterPanel.add(Box.createVerticalStrut(KEY_GAP));
        }

        LetterPanel.revalidate();
        LetterPanel.repaint();
    }

    private JButton makeKeyButton(char ch) {
        JButton b = new JButton(String.valueOf(Character.toUpperCase(ch)));
        b.setFocusable(false);
        b.setPreferredSize(KEY_SIZE);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setForeground(KEY_FG);

        b.setUI(new KeyButtonUI());
        b.addPropertyChangeListener("enabled", evt -> b.repaint());

        b.addActionListener(e -> {
            b.setEnabled(false);
            handleGuess(ch);
        });
        return b;
    }

    private void disableAllLetterButtons() {
        for (Component c : LetterPanel.getComponents()) {
            if (c instanceof JPanel) {
                for (Component cc : ((JPanel) c).getComponents()) {
                    if (cc instanceof JButton) cc.setEnabled(false);
                }
            } else if (c instanceof JButton) {
                c.setEnabled(false);
            }
        }
    }

    // ======= Custom Button UI =======
    private class KeyButtonUI extends BasicButtonUI {
        @Override public void paint(Graphics g, JComponent c) {
            AbstractButton ab = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            ButtonModel m = ab.getModel();
            Color fill = KEY_BG;
            if (!ab.isEnabled()) fill = KEY_BG_OFF;
            else if (m.isArmed() && m.isPressed()) fill = KEY_BG_DOWN;
            else if (m.isRollover()) fill = KEY_BG_HOVER;

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, ab.getWidth() - 2, ab.getHeight() - 2, KEY_ARC, KEY_ARC);

            String text = ab.getText();
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int th = fm.getAscent();

            g2.setColor(ab.isEnabled() ? KEY_FG : KEY_FG_OFF);
            g2.drawString(text, (ab.getWidth() - tw) / 2, (ab.getHeight() + th) / 2 - 2);
            g2.dispose();
        }
    }

    public JPanel getMainPanel() { return MainPanel; }
}
