package com.fwdekker.randomness.string;

import com.fwdekker.randomness.SettingsDialog;
import com.fwdekker.randomness.common.JSpinnerHelper;
import com.fwdekker.randomness.common.ValidationException;
import com.fwdekker.randomness.common.Validator;
import com.intellij.openapi.ui.ValidationInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import java.util.HashSet;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Dialog for settings of random integer generation.
 */
@SuppressFBWarnings(
        value = {"UWF_UNWRITTEN_FIELD", "NP_UNWRITTEN_FIELD"},
        justification = "Initialized by UI framework"
)
final class StringSettingsDialog extends SettingsDialog<StringSettings> {
    private JPanel contentPane;
    private JSpinner minLength;
    private JSpinner maxLength;
    private ButtonGroup enclosureGroup;
    private JList<Alphabet> alphabetList;


    /**
     * Constructs a new {@code StringSettingsDialog} that uses the singleton {@code StringSettings} instance.
     */
    StringSettingsDialog() {
        this(StringSettings.getInstance());
    }

    /**
     * Constructs a new {@code StringSettingsDialog} that uses the given {@code StringSettings} instance.
     *
     * @param settings the settings to manipulate with this dialog
     */
    StringSettingsDialog(@NotNull final StringSettings settings) {
        super(settings);

        init();
        loadSettings();
    }


    @Override
    @NotNull
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    /**
     * Initialises custom UI components.
     * <p>
     * This method is called by the scene builder at the start of the constructor.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod") // Method used by scene builder
    private void createUIComponents() {
        minLength = JSpinnerHelper.createLongSpinner();
        maxLength = JSpinnerHelper.createLongSpinner();

        alphabetList = new JList<>(Alphabet.values());
        alphabetList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        alphabetList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    }


    @Override
    public void loadSettings(@NotNull final StringSettings settings) {
        minLength.setValue(settings.getMinLength());
        maxLength.setValue(settings.getMaxLength());
        setSelectedEnclosure(settings.getEnclosure());

        for (int i = 0; i < Alphabet.values().length; i++) {
            if (settings.getAlphabets().contains(Alphabet.values()[i])) {
                alphabetList.addSelectionInterval(i, i);
            }
        }
    }

    @Override
    public void saveSettings(@NotNull final StringSettings settings) {
        final int newMinLength = ((Number) minLength.getValue()).intValue();
        final int newMaxLength = ((Number) maxLength.getValue()).intValue();

        settings.setMinLength(newMinLength);
        settings.setMaxLength(newMaxLength);
        settings.setEnclosure(getSelectedEnclosure());
        settings.setAlphabets(new HashSet<>(alphabetList.getSelectedValuesList()));
    }

    @Override
    @Nullable
    protected ValidationInfo doValidate() {
        try {
            Validator.isInteger(minLength);
            Validator.isGreaterThan(minLength, 0);

            Validator.isInteger(maxLength);
            Validator.isLessThanOrEqualTo(maxLength, Integer.MAX_VALUE);

            Validator.areValidRange(minLength, maxLength, Integer.MAX_VALUE);

            Validator.isNotEmpty(alphabetList);
        } catch (ValidationException e) {
            return new ValidationInfo(e.getMessage(), e.getComponent());
        }

        return null;
    }


    /**
     * Returns the text of the currently selected {@code JRadioButton} in the {@code enclosureGroup} group.
     *
     * @return the text of the currently selected {@code JRadioButton} in the {@code enclosureGroup} group
     */
    private String getSelectedEnclosure() {
        return Collections.list(enclosureGroup.getElements()).stream()
                .filter(AbstractButton::isSelected)
                .map(AbstractButton::getActionCommand)
                .findFirst()
                .orElse("");
    }

    /**
     * Selects the {@code JRadioButton} in the {@code enclosureGroup} group with the given text, and deselects all other
     * {@code JRadioButton}s in that group.
     *
     * @param enclosure the text of the {@code JRadioButton} to select
     */
    private void setSelectedEnclosure(final String enclosure) {
        Collections.list(enclosureGroup.getElements()).stream()
                .filter(button -> button.getActionCommand().equals(enclosure))
                .findFirst()
                .ifPresent(button -> button.setSelected(true));
    }
}
