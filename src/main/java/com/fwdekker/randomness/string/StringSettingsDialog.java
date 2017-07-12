package com.fwdekker.randomness.string;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import java.text.ParseException;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;


/**
 * Dialog for settings of random integer generation.
 */
final class StringSettingsDialog extends DialogWrapper {
    private final StringSettings stringSettings = StringSettings.getInstance();

    private JPanel contentPane;
    private JSpinner minLength;
    private JSpinner maxLength;
    private ButtonGroup enclosureGroup;
    private JRadioButton enclosureNoneButton;
    private JRadioButton enclosureSingleButton;
    private JRadioButton enclosureDoubleButton;
    private JRadioButton enclosureBacktickButton;


    /**
     * Constructs a new {@code StringSettingsDialog}.
     */
    StringSettingsDialog() {
        super(null);

        init();
        loadSettings();
    }


    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected String getDimensionServiceKey() {
        return getClass().getSimpleName();
    }


    @Override
    protected void doOKAction() {
        processDoNotAskOnOk(OK_EXIT_CODE);

        if (getOKAction().isEnabled()) {
            saveSettings();
            close(OK_EXIT_CODE);
        }
    }


    /**
     * Loads settings from the model into the UI.
     */
    private void loadSettings() {
        minLength.setValue(stringSettings.getMinLength());
        maxLength.setValue(stringSettings.getMaxLength());
        setSelectedEnclosure(stringSettings.getEnclosure());
    }

    /**
     * Commits the values entered by the user to the model.
     *
     * @throws ParseException if the values entered by the user could not be parsed
     */
    private void saveSettings() {
        try {
            minLength.commitEdit();
            maxLength.commitEdit();
        } catch (final ParseException e) {
            throw new IllegalStateException("Settings were committed, but input could not be parsed.", e);
        }

        stringSettings.setMinLength((Integer) minLength.getValue());
        stringSettings.setMaxLength((Integer) maxLength.getValue());
        stringSettings.setEnclosure(getSelectedEnclosure());
    }

    /**
     * Validates all input fields.
     *
     * @return {@code null} if the input is valid, or {@code ValidationInfo} indicating the error if input is not valid
     */
    @Override
    protected ValidationInfo doValidate() {
        if (!(minLength.getValue() instanceof Integer)) {
            return new ValidationInfo("Minimum value must be an integer.", minLength);
        }
        if (!(maxLength.getValue() instanceof Integer)) {
            return new ValidationInfo("Maximum value must be an integer.", maxLength);
        }

        final double newMinLength = (Integer) minLength.getValue();
        final double newMaxLength = (Integer) maxLength.getValue();
        if (newMaxLength < newMinLength) {
            return new ValidationInfo("Maximum value cannot be smaller than minimum value.", maxLength);
        }

        return null;
    }


    /**
     * Returns the text of the currently selected {@code JRadioButton} in the {@code enclosureGroup} group.
     *
     * @return the text of the currently selected {@code JRadioButton} in the {@code enclosureGroup} group
     */
    private String getSelectedEnclosure() {
        if (enclosureNoneButton.isSelected()) {
            return "";
        } else if (enclosureSingleButton.isSelected()) {
            return "'";
        } else if (enclosureDoubleButton.isSelected()) {
            return "\"";
        } else if (enclosureBacktickButton.isSelected()) {
            return "`";
        } else {
            return "";
        }
    }

    /**
     * Selects the {@code JRadioButton} in the {@code enclosureGroup} group with the given text, and deselects all
     * other {@code JRadioButton}s in that group.
     *
     * @param enclosure the text of the {@code JRadioButton} to select
     */
    private void setSelectedEnclosure(final String enclosure) {
        switch (enclosure) {
            case "":
                enclosureGroup.setSelected(enclosureNoneButton.getModel(), true);
                break;
            case "'":
                enclosureGroup.setSelected(enclosureSingleButton.getModel(), true);
                break;
            case "\"":
                enclosureGroup.setSelected(enclosureDoubleButton.getModel(), true);
                break;
            case "`":
                enclosureGroup.setSelected(enclosureBacktickButton.getModel(), true);
                break;
            default:
                enclosureGroup.setSelected(enclosureNoneButton.getModel(), true);
                break;
        }
    }
}
