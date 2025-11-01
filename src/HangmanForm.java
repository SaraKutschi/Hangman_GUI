import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
/**
 * HangmanForm:
 * Verwaltet die komplette GUI des Hangman-Spiels.
 * - Enthält alle Swing-Komponenten aus dem .form
 * - Steuert Spiellogik (Game) und Bildanzeige
 * - Initialisiert Tasten (A–Z) mit eigenem Button-Stil
 * - Skaliert Hangman-Bilder dynamisch
 * - Bietet Toolbar-Aktionen (Neustart, Einstellungen)
 */
public class HangmanForm {
    // === Panels & Komponenten ===
    private JPanel MainPanel, GamePanel, LetterPanel, HeaderPanel, ImagePanel, InfoPanel;
    private JToolBar Menu;
    private JButton settingsBtn, restartBtn;
    private JLabel wordLbl, failLbl;
    private JButton aBtn, bBtn, cBtn, dBtn, eBtn, fBtn, gBtn, hBtn, iBtn, jBtn, kBtn, lBtn,
            mBtn, nBtn, oBtn, pBtn, qBtn, rBtn, sBtn, tBtn, uBtn, vBtn, wBtn, xBtn, yBtn, zBtn;

    // === Spiellogik & Bilder ===
    private final Game game = new Game();
    private JLabel imageLbl;
    private ImageIcon rawIcon;
    private int[] frameByFail;

    // === Styles ===
    private static final Color KEY_FG       = new Color(255,255,255);
    private static final Color KEY_BG       = new Color(90,74,69);
    private static final Color KEY_BG_HOVER = new Color(106,88,82);
    private static final Color KEY_BG_DOWN  = new Color(75,61,56);
    private static final Color KEY_BG_OFF   = new Color(141,123,117);
    private static final Color KEY_FG_OFF   = new Color(245,236,232);
    private static final Color IMG_BG       = new Color(243,242,238);

    private static final int PIC_MIN = 1, PIC_MAX = 11, IMG_PADDING = 8, KEY_ARC = 10;
    private static final Dimension KEY_SIZE = new Dimension(36, 28);
    private static final String IMG_FMT = "/Image/hangman%d.jpeg";

    /**
     * Konstruktor:
     * - Initialisiert Panels, Toolbar, Buttons und Bilder
     * - Setzt Layouts, Events und Startstatus des Spiels
     */
    public HangmanForm() {
        // === Bildbereich ===
        ImagePanel.setBackground(IMG_BG);
        imageLbl = new JLabel("", SwingConstants.CENTER);
        imageLbl.setBackground(IMG_BG);
        imageLbl.setBorder(new EmptyBorder(IMG_PADDING, IMG_PADDING, IMG_PADDING, IMG_PADDING));
        ImagePanel.add(imageLbl, BorderLayout.CENTER);
        ImagePanel.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { fitImageToCard(); }
        });

        // === Toolbar ===
        for (JButton b : new JButton[]{restartBtn, settingsBtn}) {
            b.setFocusable(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        restartBtn.addActionListener(e -> restartGame());
        settingsBtn.addActionListener(e -> openSettings());
        HeaderPanel.add(Menu, BorderLayout.NORTH);

        // === Startstatus ===
        recomputeFrameMap();
        updateStatus();
        refreshImage();
        initKeyboard();
    }

    /** Gibt ein Array mit allen 26 Buchstaben-Buttons zurück. */
    private JButton[] allKeys() {
        return new JButton[]{
                aBtn,bBtn,cBtn,dBtn,eBtn,fBtn,gBtn,hBtn,iBtn,jBtn,kBtn,lBtn,
                mBtn,nBtn,oBtn,pBtn,qBtn,rBtn,sBtn,tBtn,uBtn,vBtn,wBtn,xBtn,yBtn,zBtn
        };
    }

    /** Initialisiert das Keyboard (A–Z) mit Text, Stil und Klickverhalten. */
    private void initKeyboard() {
        JButton[] keys = allKeys();
        for (int i = 0; i < keys.length; i++) {
            JButton b = keys[i];
            if (b == null) continue;
            char letter = (char) ('A' + i);
            b.setText(String.valueOf(letter));
            styleKeyButton(b);
            b.addActionListener(e -> { b.setEnabled(false); handleGuess(letter); });
        }
    }

    /** Aktiviert oder deaktiviert alle Buchstabentasten. */
    private void setAllLetterButtonsEnabled(boolean on) {
        for (JButton b : allKeys()) if (b != null) b.setEnabled(on);
    }

    /**
     * Verarbeitet einen geratenen Buchstaben:
     * - Übergibt ihn an Game
     * - Aktualisiert Anzeige & Bild
     * - Zeigt Ergebnisdialog bei Sieg/Niederlage
     */
    private void handleGuess(char letter) {
        game.guessLetter(letter);
        updateStatus();
        refreshImage();
        if (game.isGameOver()) {
            setAllLetterButtonsEnabled(false);
            JOptionPane.showMessageDialog(
                    MainPanel,
                    game.hasWon() ? "Du hast gewonnen!" : "Leider verloren.\nWort: " + game.getWord(),
                    "Hangman",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /** Startet das Spiel neu und setzt UI & Bild zurück. */
    private void restartGame() {
        game.reset();
        updateStatus();
        recomputeFrameMap();
        refreshImage();
        setAllLetterButtonsEnabled(true);
    }

    /** Öffnet den Einstellungsdialog für die maximale Fehleranzahl. */
    private void openSettings() {
        Window owner = SwingUtilities.getWindowAncestor(MainPanel);
        Settings dlg = new Settings(owner, game.getMaxFails(), newMax -> {
            game.setMaxFails(newMax);
            updateStatus();
            recomputeFrameMap();
            refreshImage();
        });
        dlg.setVisible(true);
    }

    /** Aktualisiert Labels für Wortfortschritt und Fehleranzahl. */
    private void updateStatus() {
        wordLbl.setText(game.getGuessedWord());
        failLbl.setText("Fehler: " + game.getFails() + " / " + game.getMaxFails());
    }

    /**
     * Aktualisiert das Hangman-Bild anhand der Fehlerzahl.
     * Lädt das entsprechende Image aus den Ressourcen.
     */
    private void refreshImage() {
        int f = Math.min(game.getFails(), frameByFail.length - 1);
        int pic = frameByFail[f];
        String path = String.format(IMG_FMT, pic);
        URL url = getClass().getResource(path);
        if (url == null) {
            rawIcon = null;
            imageLbl.setIcon(null);
            imageLbl.setText("Bild fehlt: " + path);
            return;
        }
        rawIcon = new ImageIcon(url);
        fitImageToCard();
    }

    /** Skaliert das aktuelle Bild proportional an die Größe des Panels an. */
    private void fitImageToCard() {
        if (rawIcon == null) return;
        int panelW = Math.max(1, ImagePanel.getWidth() - IMG_PADDING);
        int panelH = Math.max(1, ImagePanel.getHeight() - IMG_PADDING);
        int iw = rawIcon.getIconWidth(), ih = rawIcon.getIconHeight();
        double s = Math.min(panelW / (double) iw, panelH / (double) ih);
        int w = Math.max(1, (int) Math.round(iw * s));
        int h = Math.max(1, (int) Math.round(ih * s));
        Image scaled = rawIcon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        imageLbl.setIcon(new ImageIcon(scaled));
        imageLbl.setText(null);
    }

    /** Berechnet, welches Bild bei welchem Fehlerstand angezeigt wird. */
    private void recomputeFrameMap() {
        int maxFails = Math.max(1, game.getMaxFails());
        frameByFail = new int[maxFails + 1];
        for (int i = 0; i <= maxFails; i++) {
            frameByFail[i] = PIC_MIN + (i * (PIC_MAX - PIC_MIN)) / maxFails;
        }
    }

    /**
     * Wendet einen benutzerdefinierten Stil auf Buchstabenbuttons an:
     * - Hover, Pressed, Disabled-Farben
     * - Abgerundete Ecken und zentrierter Text
     */
    private void styleKeyButton(JButton b) {
        b.setFocusable(false);
        b.setPreferredSize(KEY_SIZE);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setForeground(KEY_FG);
        b.setUI(new BasicButtonUI() {
            @Override public void paint(Graphics g, JComponent c) {
                AbstractButton ab = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = ab.getModel();
                Color fill = !ab.isEnabled() ? KEY_BG_OFF
                        : (m.isArmed() && m.isPressed()) ? KEY_BG_DOWN
                        : m.isRollover() ? KEY_BG_HOVER : KEY_BG;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, ab.getWidth() - 2, ab.getHeight() - 2, KEY_ARC, KEY_ARC);
                FontMetrics fm = g2.getFontMetrics();
                String t = ab.getText();
                g2.setColor(ab.isEnabled() ? KEY_FG : KEY_FG_OFF);
                g2.drawString(t, (ab.getWidth() - fm.stringWidth(t)) / 2, (ab.getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        });
        b.addPropertyChangeListener("enabled", evt -> b.repaint());
    }

    /** Gibt das Hauptpanel für das JFrame zurück. */
    public JPanel getMainPanel() { return MainPanel; }
}
