import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URL;

public class HangmanForm {
    private JPanel MainPanel;
    private JPanel GamePanel;
    private JPanel LetterPanel;
    private JPanel HeaderPanel;
    private JPanel ImagePanel;
    private JButton aBtn, bBtn, cBtn, dBtn, eBtn, fBtn, gBtn, hBtn, iBtn, jBtn, kBtn, lBtn,
            mBtn, nBtn, oBtn, pBtn, qBtn, rBtn, sBtn, tBtn, uBtn, vBtn, wBtn, xBtn, yBtn, zBtn;
    private JPanel InfoPanel;
    private JLabel wordLbl;
    private JLabel failLbl;

    private final Game game = new Game();
    private JLabel imageLbl;
    private JToolBar Menu;
    private JButton settingsBtn;
    private JButton restartBtn;
    private ImageIcon currentRawIcon;

    // Farben der Tasten
    private static final Color KEY_FG       = new Color(255, 255, 255);
    private static final Color KEY_BG       = new Color(90, 74, 69);
    private static final Color KEY_BG_HOVER = new Color(106, 88, 82);
    private static final Color KEY_BG_DOWN  = new Color(75, 61, 56);
    private static final Color KEY_BG_OFF   = new Color(141, 123, 117);
    private static final Color KEY_FG_OFF   = new Color(245, 236, 232);

    // Bilder der Galgenmännchen-Stufen
    private static final int PIC_MIN = 1;
    private static final int PIC_MAX = 10;
    private static final String IMG_FMT = "/Image/hangman%d.jpeg";
    private static final Color IMG_BG = new Color(243, 242, 238);
    private static final int IMG_PADDING = 8;

    private int picCounter = PIC_MIN;

    public HangmanForm() {
        // Image-Panel aufsetzen
        ImagePanel.setLayout(new BorderLayout());
        ImagePanel.setPreferredSize(new Dimension(250, 250));
        imageLbl = new JLabel("", SwingConstants.CENTER);
        imageLbl.setOpaque(true);
        imageLbl.setBorder(new EmptyBorder(IMG_PADDING, IMG_PADDING, IMG_PADDING, IMG_PADDING));
        ImagePanel.add(imageLbl, BorderLayout.CENTER);
        ImagePanel.setBackground(IMG_BG);
        imageLbl.setBackground(IMG_BG);

        // Labels initialisieren
        updateStatus();

        // Erstes Bild laden + bei Resize fitten
        loadRaw(picCounter);
        fitImageToCard();
        ImagePanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                fitImageToCard();
            }
        });

        // Toolbar
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

        buildKeyboard();
    }

    private void restartGame() {
        game.reset();
        updateStatus();
        loadRaw(PIC_MIN);
        fitImageToCard();
        buildKeyboard(); // alle Tasten wieder aktivieren
    }

    private void openSettings() {
        Window owner = SwingUtilities.getWindowAncestor(MainPanel);
        Settings dlg = new Settings(owner, game.getMaxFails(), (newMax) -> {
            game.setMaxFails(newMax);
            updateStatus();
            // Optional: sofort neu starten
            // restartGame();
        });
        dlg.setVisible(true);
    }

    private void updateStatus() {
        wordLbl.setText(game.getGuessedWord());
        failLbl.setText("Fehler: " + game.getFails() + " / " + game.getMaxFails());
    }

    private void loadRaw(int index) {
        // wrap-around zwischen PIC_MIN und PIC_MAX
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

        // skalieren, sodass das Bild vollständig in den verfügbaren Bereich passt
        double scale = Math.min(panelW / (double) imgW, panelH / (double) imgH);
        int w = (int) Math.max(1, Math.round(imgW * scale));
        int h = (int) Math.max(1, Math.round(imgH * scale));

        Image scaled = currentRawIcon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        imageLbl.setIcon(new ImageIcon(scaled));
        imageLbl.setText(null);
        imageLbl.setHorizontalAlignment(SwingConstants.CENTER);
        imageLbl.setVerticalAlignment(SwingConstants.CENTER);
    }

    private void handleGuess(char letter) {
        boolean correct = game.guessLetter(letter);
        updateStatus();

        // Bild je nach Fehlerstand wechseln (fails + 1, aber im Bereich halten)
        int idx = Math.min(PIC_MAX, Math.max(PIC_MIN, game.getFails() + 1));
        loadRaw(idx);
        fitImageToCard();

        if (game.isGameOver()) {
            disableAllLetterButtons();
            JOptionPane.showMessageDialog(MainPanel,
                    game.hasWon() ? "Du hast gewonnen!"
                            : "Leider verloren.\nDas Wort war: " + game.getWord(),
                    "Hangman", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void disableAllLetterButtons() {
        for (Component c : LetterPanel.getComponents()) {
            if (c instanceof JButton) ((JButton)c).setEnabled(false);
            if (c instanceof JPanel) {
                for (Component cc : ((JPanel)c).getComponents()) {
                    if (cc instanceof JButton) cc.setEnabled(false);
                }
            }
        }
    }

    private void buildKeyboard() {
        // QWERTZ-nah gruppierte Reihen
        String[] rows = {"ABCDEFG", "HIJKLMN", "OPQRSTU", "VWXYZ"};

        LetterPanel.removeAll();
        LetterPanel.setLayout(new BoxLayout(LetterPanel, BoxLayout.Y_AXIS));
        LetterPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        LetterPanel.setOpaque(true);

        for (String line : rows) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            row.setOpaque(false);
            for (char ch : line.toCharArray()) {
                JButton btn = new JButton(String.valueOf(ch));
                stylizeKey(btn);
                btn.setFocusable(false);
                btn.addActionListener(e -> {
                    btn.setEnabled(false);
                    handleGuess(ch);
                });
                row.add(btn);
            }
            LetterPanel.add(row);
            LetterPanel.add(Box.createVerticalStrut(6));
        }

        LetterPanel.revalidate();
        LetterPanel.repaint();
    }

    private void stylizeKey(JButton b) {
        b.setText(b.getText().toUpperCase());
        b.setForeground(KEY_FG);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(36, 28));
        b.setMargin(new Insets(0,0,0,0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.setUI(new javax.swing.plaf.basic.BasicButtonUI(){
            @Override public void paint(Graphics g, JComponent c) {
                AbstractButton ab = (AbstractButton)c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel m = ab.getModel();
                Color fill = KEY_BG;
                if (!ab.isEnabled()) fill = KEY_BG_OFF;
                else if (m.isArmed() && m.isPressed()) fill = KEY_BG_DOWN;
                else if (m.isRollover()) fill = KEY_BG_HOVER;

                g2.setColor(fill);
                g2.fillRoundRect(0, 0, ab.getWidth()-2, ab.getHeight()-2, 10, 10);

                FontMetrics fm = g2.getFontMetrics();
                String text = ab.getText();
                int tw = fm.stringWidth(text);
                int th = fm.getAscent();
                g2.setColor(ab.isEnabled() ? KEY_FG : KEY_FG_OFF);
                g2.drawString(text, (ab.getWidth() - tw) / 2, (ab.getHeight() + th) / 2 - 2);
                g2.dispose();
            }
        });

        b.addPropertyChangeListener("enabled", evt -> b.repaint());
        b.setRolloverEnabled(true);
    }

    public JPanel getMainPanel() { return MainPanel; }
}
