package com.fwdekker.randomness.array

import com.fwdekker.randomness.DummyInsertArrayAction
import com.fwdekker.randomness.SettingsComponent
import com.fwdekker.randomness.array.ArraySettings.Companion.default
import com.fwdekker.randomness.ui.JIntSpinner
import com.fwdekker.randomness.ui.PreviewPanel
import com.fwdekker.randomness.ui.getValue
import com.fwdekker.randomness.ui.not
import com.fwdekker.randomness.ui.setValue
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.selected
import javax.swing.ButtonGroup
import javax.swing.JCheckBox
import javax.swing.JPanel


/**
 * Component for settings of random array generation.
 *
 * @see ArraySettings
 * @see ArraySettingsAction
 */
@Suppress("LateinitUsage") // Initialized by scene builder
class ArraySettingsComponent(settings: ArraySettings = default) : SettingsComponent<ArraySettings>(settings) {
    companion object {
        private const val previewPlaceholder = "17"
    }


    private var contentPane: JPanel
    private lateinit var countSpinner: JIntSpinner
    private lateinit var bracketsGroup: ButtonGroup
    private lateinit var separatorGroup: ButtonGroup
    private lateinit var spaceAfterSeparatorCheckBox: JCheckBox

    override val rootPane get() = contentPane


    init {
        lateinit var previewPanel: PreviewPanel<DummyInsertArrayAction>

        contentPane = panel {
            lateinit var newlineSeparatorButton: CellBuilder<JBRadioButton>

            row("Count:") { JIntSpinner(value = 1, minValue = 1)(grow).also { countSpinner = it.component } }
            row("Brackets:") {
                withButtonGroup(ButtonGroup().also { bracketsGroup = it }) {
                    cell(isFullWidth = true) {
                        radioButton("[]")
                        radioButton("{}")
                        radioButton("()")
                        radioButton("None").also { it.component.actionCommand = "" }
                    }
                }
            }
            row("Separator:") {
                cell {
                    withButtonGroup(ButtonGroup().also { separatorGroup = it }) {
                        radioButton(",")
                        radioButton(";")
                        radioButton("\\n")
                            .also { it.component.actionCommand = "\n" }
                            .also { newlineSeparatorButton = it }
                    }
                }
            }
            row("") {
                checkBox("Space after separator")()
                    .enableIf(newlineSeparatorButton.selected.not())
                    .also { spaceAfterSeparatorCheckBox = it.component }
            }
            titledRow("Preview") {
                previewPanel = PreviewPanel {
                    DummyInsertArrayAction(ArraySettings().also { saveSettings(it) }, previewPlaceholder)
                }

                row {
                    cell(isFullWidth = true) {
                        previewPanel.previewLabel(grow)
                        previewPanel.refreshButton()
                    }
                }
            }
        }

        loadSettings()

        previewPanel.updatePreviewOnUpdateOf(countSpinner, bracketsGroup, separatorGroup, spaceAfterSeparatorCheckBox)
        previewPanel.updatePreview()
    }


    override fun loadSettings(settings: ArraySettings) {
        countSpinner.value = settings.count
        bracketsGroup.setValue(settings.brackets)
        separatorGroup.setValue(settings.separator)
        spaceAfterSeparatorCheckBox.isSelected = settings.isSpaceAfterSeparator
    }

    override fun saveSettings(settings: ArraySettings) {
        settings.count = countSpinner.value
        settings.brackets = bracketsGroup.getValue() ?: ArraySettings.DEFAULT_BRACKETS
        settings.separator = separatorGroup.getValue() ?: ArraySettings.DEFAULT_SEPARATOR
        settings.isSpaceAfterSeparator = spaceAfterSeparatorCheckBox.isSelected
    }

    override fun doValidate() = countSpinner.validateValue()
}
