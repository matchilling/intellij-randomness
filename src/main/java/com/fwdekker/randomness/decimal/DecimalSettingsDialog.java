package com.fwdekker.randomness.decimal;

import com.fwdekker.randomness.SettingsDialog;
import com.fwdekker.randomness.ui.ButtonGroupHelper;
import com.fwdekker.randomness.ui.JDoubleSpinner;
import com.fwdekker.randomness.ui.JLongSpinner;
import com.fwdekker.randomness.ui.JSpinnerRange;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Optional;


/**
 * Dialog for settings of random decimal generation.
 */
public final class DecimalSettingsDialog extends SettingsDialog<DecimalSettings> {
    private JPanel contentPane;
    private JSpinnerRange valueRange;
    private JDoubleSpinner minValue;
    private JDoubleSpinner maxValue;
    private JLongSpinner decimalCount;
    private ButtonGroup groupingSeparatorGroup;
    private ButtonGroup decimalSeparatorGroup;


    /**
     * Constructs a new {@code DecimalSettingsDialog} that uses the singleton {@code DecimalSettings} instance.
     */
    /* default */ DecimalSettingsDialog() {
        this(DecimalSettings.Companion.getDefault());
    }

    /**
     * Constructs a new {@code DecimalSettingsDialog} that uses the given {@code DecimalSettings} instance.
     *
     * @param settings the settings to manipulate with this dialog
     */
    /* default */ DecimalSettingsDialog(final @NotNull DecimalSettings settings) {
        super(settings);

        init();
        loadSettings();
    }


    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    /**
     * Initialises custom UI components.
     * <p>
     * This method is called by the scene builder at the start of the constructor.
     */
    private void createUIComponents() {
        minValue = new JDoubleSpinner();
        maxValue = new JDoubleSpinner();
        valueRange = new JSpinnerRange(minValue, maxValue, JSpinnerRange.DEFAULT_MAX_RANGE); // TODO remove default
        decimalCount = new JLongSpinner(0, 0, Integer.MAX_VALUE);
    }

    @Override
    @Nullable
    protected ValidationInfo doValidate() {
        // TODO Rewrite this in Kotlin
        return Optional.ofNullable(minValue.validateValue())
            .orElse(Optional.ofNullable(maxValue.validateValue())
                .orElse(Optional.ofNullable(valueRange.validateValue())
                    .orElse(decimalCount.validateValue())));
    }


    @Override
    public void loadSettings(final @NotNull DecimalSettings settings) {
        minValue.setValue(settings.getMinValue());
        maxValue.setValue(settings.getMaxValue());
        decimalCount.setValue(settings.getDecimalCount());
        ButtonGroupHelper.INSTANCE.setValue(groupingSeparatorGroup, String.valueOf(settings.getGroupingSeparator()));
        ButtonGroupHelper.INSTANCE.setValue(decimalSeparatorGroup, String.valueOf(settings.getDecimalSeparator()));
    }

    @Override
    public void saveSettings(final @NotNull DecimalSettings settings) {
        settings.setMinValue(minValue.getValue());
        settings.setMaxValue(maxValue.getValue());
        settings.setDecimalCount(Math.toIntExact(decimalCount.getValue()));
        settings.setGroupingSeparator(ButtonGroupHelper.INSTANCE.getValue(groupingSeparatorGroup));
        settings.setDecimalSeparator(ButtonGroupHelper.INSTANCE.getValue(decimalSeparatorGroup));
    }
}
