package com.fwdekker.randomness.uuid

import com.fwdekker.randomness.Settings
import com.fwdekker.randomness.SettingsConfigurable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


/**
 * Contains settings for generating random UUIDs.
 *
 * @property enclosure The string that encloses the generated UUID on both sides.
 *
 * @see UuidInsertAction
 * @see UuidSettingsAction
 * @see UuidSettingsComponent
 */
@State(name = "UuidSettings", storages = [Storage("\$APP_CONFIG\$/randomness.xml")])
data class UuidSettings(var enclosure: String = DEFAULT_ENCLOSURE) : Settings<UuidSettings> {
    companion object {
        /**
         * The default value of the [enclosure][UuidSettings.enclosure] field.
         */
        const val DEFAULT_ENCLOSURE = "\""


        /**
         * The persistent `UuidSettings` instance.
         */
        val default: UuidSettings
            get() = ServiceManager.getService(UuidSettings::class.java)
    }


    override fun copyState() = UuidSettings().also { it.loadState(this) }

    override fun getState() = this

    override fun loadState(state: UuidSettings) = XmlSerializerUtil.copyBean(state, this)
}


/**
 * The configurable for UUID settings.
 *
 * @see UuidSettingsAction
 */
class UuidSettingsConfigurable(
    override val component: UuidSettingsComponent = UuidSettingsComponent()
) : SettingsConfigurable<UuidSettings>() {
    override fun getDisplayName() = "UUIDs"
}