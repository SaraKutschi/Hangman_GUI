import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

/**
 * Settings:
 * Dialogfenster für die Spieleinstellungen (maximale Fehleranzahl).
 * - Zeigt ein Dropdown (6–11 Fehler)
 * - Speichert Auswahl über Callback (onSave)
 * - Modal: blockiert das Hauptfenster bis zur Bestätigung oder Abbruch
 */
public class Settings extends JDialog {
    // === GUI-Komponenten ===
    private JPanel SettingsPanel;
    private JLabel settingsLbl;
    private JLabel errorsLbl;
    private JComboBox<Integer> errorsCb;
    private JButton okBtn;
    private JButton cancelBtn;
    private JPanel HeaderPanel;
    private JPanel EnterPanel;

    /**
     * Erstellt den Einstellungsdialog.
     *
     * @param owner   Hauptfenster, dem der Dialog zugeordnet ist
     * @param current Aktuell eingestellter Max-Fehlerwert
     * @param onSave  Callback, das den neuen Wert entgegennimmt (z. B. zum Aktualisieren im Game)
     */
    public Settings(Window owner, int current, IntConsumer onSave) {
        super(owner, "Einstellungen", ModalityType.APPLICATION_MODAL);

        setContentPane(SettingsPanel);

        if (errorsCb.getItemCount() == 0) {
            for (int i = 6; i <= 11; i++) errorsCb.addItem(i);
        }
        errorsCb.setSelectedItem(current);

        okBtn.addActionListener(e -> {
            Integer sel = (Integer) errorsCb.getSelectedItem();
            if (sel != null && onSave != null) onSave.accept(sel);
            dispose();
        });

        cancelBtn.addActionListener(e -> dispose());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(owner);
    }
}
