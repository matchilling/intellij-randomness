package com.fwdekker.randomness.string

import com.fwdekker.randomness.CapitalizationMode
import com.fwdekker.randomness.Settings
import com.fwdekker.randomness.SettingsConfigurable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


/**
 * Contains settings for generating random strings.
 *
 * @property minLength The minimum length of the generated string, inclusive.
 * @property maxLength The maximum length of the generated string, inclusive.
 * @property enclosure The string that encloses the generated string on both sides.
 * @property capitalization The capitalization mode of the generated string.
 * @property alphabets The alphabets to be used for generating strings.
 *
 * @see StringInsertAction
 * @see StringSettingsAction
 * @see StringSettingsComponent
 */
@State(name = "StringSettings", storages = [Storage("\$APP_CONFIG\$/randomness.xml")])
data class StringSettings(
    var minLength: Int = DEFAULT_MIN_LENGTH,
    var maxLength: Int = DEFAULT_MAX_LENGTH,
    var enclosure: String = DEFAULT_ENCLOSURE,
    var capitalization: CapitalizationMode = DEFAULT_CAPITALIZATION,
    var alphabets: MutableSet<Alphabet> = mutableSetOf(Alphabet.ALPHABET, Alphabet.DIGITS)
) : Settings<StringSettings> {
    companion object {
        /**
         * The default value of the [minLength][StringSettings.minLength] field.
         */
        const val DEFAULT_MIN_LENGTH = 3
        /**
         * The default value of the [maxLength][StringSettings.maxLength] field.
         */
        const val DEFAULT_MAX_LENGTH = 8
        /**
         * The default value of the [enclosure][StringSettings.enclosure] field.
         */
        const val DEFAULT_ENCLOSURE = "\""
        /**
         * The default value of the [capitalization][StringSettings.capitalization] field.
         */
        val DEFAULT_CAPITALIZATION = CapitalizationMode.RANDOM


        /**
         * The persistent `StringSettings` instance.
         */
        val default: StringSettings
            get() = ServiceManager.getService(StringSettings::class.java)
    }


    override fun copyState() = StringSettings().also { it.loadState(this) }

    override fun getState() = this

    override fun loadState(state: StringSettings) = XmlSerializerUtil.copyBean(state, this)
}


/**
 * The configurable for string settings.
 *
 * @see StringSettingsAction
 */
class StringSettingsConfigurable(
    override val component: StringSettingsComponent = StringSettingsComponent()
) : SettingsConfigurable<StringSettings>() {
    override fun getDisplayName() = "Strings"
}