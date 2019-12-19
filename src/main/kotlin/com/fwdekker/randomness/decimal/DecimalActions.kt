package com.fwdekker.randomness.decimal

import com.fwdekker.randomness.DataGenerationException
import com.fwdekker.randomness.DataGroupAction
import com.fwdekker.randomness.DataInsertAction
import com.fwdekker.randomness.DataInsertArrayAction
import com.fwdekker.randomness.DataSettingsAction
import com.fwdekker.randomness.array.ArraySettings
import icons.RandomnessIcons
import java.text.DecimalFormat
import kotlin.math.nextUp


/**
 * All actions related to inserting decimals.
 */
class DecimalGroupAction : DataGroupAction(RandomnessIcons.Decimal.Base) {
    override val insertAction = DecimalInsertAction()
    override val insertArrayAction = DecimalInsertArrayAction()
    override val settingsAction = DecimalSettingsAction()
}


/**
 * Inserts random decimals.
 *
 * @param settings the settings to use for generating decimals
 *
 * @see DecimalInsertArrayAction
 * @see DecimalSettings
 */
class DecimalInsertAction(private val settings: DecimalSettings = DecimalSettings.default) :
    DataInsertAction(RandomnessIcons.Decimal.Base) {
    override val name = "Random Decimal"


    /**
     * Returns random decimals between the minimum and maximum value, inclusive.
     *
     * @param count the number of decimals to generate
     * @return random decimals between the minimum and maximum value, inclusive
     */
    override fun generateStrings(count: Int) =
        List(count) {
            val scheme = settings.currentScheme
            if (scheme.minValue > scheme.maxValue)
                throw DataGenerationException("Minimum value is larger than maximum value.")

            convertToString(random.nextDouble(scheme.minValue, scheme.maxValue.nextUp()))
        }


    /**
     * Returns a nicely formatted representation of a decimal.
     *
     * @param decimal the decimal to format
     * @return a nicely formatted representation of a decimal
     */
    private fun convertToString(decimal: Double): String {
        val scheme = settings.currentScheme

        val format = DecimalFormat()
        format.isGroupingUsed = scheme.groupingSeparator.isNotEmpty()

        val symbols = format.decimalFormatSymbols
        symbols.groupingSeparator = scheme.groupingSeparator.getOrElse(0) { Char.MIN_VALUE }
        symbols.decimalSeparator = scheme.decimalSeparator.getOrElse(0) { Char.MIN_VALUE }
        if (scheme.showTrailingZeroes) format.minimumFractionDigits = scheme.decimalCount
        format.maximumFractionDigits = scheme.decimalCount
        format.decimalFormatSymbols = symbols

        return format.format(decimal)
    }
}


/**
 * Inserts an array-like string of decimals.
 *
 * @param arraySettings the settings to use for generating arrays
 * @param settings the settings to use for generating decimals
 *
 * @see DecimalInsertAction
 */
class DecimalInsertArrayAction(
    arraySettings: ArraySettings = ArraySettings.default,
    settings: DecimalSettings = DecimalSettings.default
) : DataInsertArrayAction(arraySettings, DecimalInsertAction(settings), RandomnessIcons.Decimal.Array) {
    override val name = "Random Decimal Array"
}


/**
 * Controller for random decimal generation settings.
 *
 * @see DecimalSettings
 * @see DecimalSettingsComponent
 */
class DecimalSettingsAction : DataSettingsAction<DecimalSettings, DecimalScheme>(RandomnessIcons.Decimal.Settings) {
    override val title = "Decimal Settings"

    override val configurableClass = DecimalSettingsConfigurable::class.java
}
