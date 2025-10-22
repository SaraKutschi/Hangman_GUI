import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.IntConsumer;

public class Settings extends JDialog {
    private JPanel SettingsPanel;
    private JLabel settingsLbl;
    private JLabel errorsLbl;
    private JComboBox<Integer> errorsCb;
    private JButton okBtn;
    private JButton cancelBtn;

    public Settings(Window owner, int current, IntConsumer onSave) {
        super(owner, "Einstellungen", ModalityType.APPLICATION_MODAL);
        buildUI(current);

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

    private void buildUI(int current) {
        SettingsPanel = new JPanel(new GridBagLayout());
        SettingsPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        SettingsPanel.setBackground(new Color(250, 240, 235)); // passt zum Theme

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;

        settingsLbl = new JLabel("Einstellungen");
        settingsLbl.setFont(settingsLbl.getFont().deriveFont(Font.BOLD, 16f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        SettingsPanel.add(settingsLbl, g);

        errorsLbl = new JLabel("Fehler-Limit:");
        g.gridx = 0; g.gridy = 1; g.gridwidth = 1;
        SettingsPanel.add(errorsLbl, g);

        errorsCb = new JComboBox<>();
        for (int i = 6; i <= 11; i++) errorsCb.addItem(i);
        errorsCb.setSelectedItem(current);
        g.gridx = 1; g.gridy = 1;
        SettingsPanel.add(errorsCb, g);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        cancelBtn = new JButton("Abbrechen");
        okBtn = new JButton("OK");
        btns.add(cancelBtn);
        btns.add(okBtn);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.anchor = GridBagConstraints.EAST;
        SettingsPanel.add(btns, g);

        setContentPane(SettingsPanel);
    }
}
