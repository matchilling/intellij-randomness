package com.fwdekker.randomness.word

import com.fwdekker.randomness.ValidationInfo
import com.fwdekker.randomness.ui.ActivityTableModelEditor
import com.fwdekker.randomness.ui.EditableDatum
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.util.ui.CollectionItemEditor
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.LocalPathCellEditor


private typealias EditableDictionary = EditableDatum<Dictionary>


/**
 * An editable table for selecting and editing [Dictionaries][Dictionary].
 *
 * @see WordSettingsComponent
 */
class DictionaryTable : ActivityTableModelEditor<Dictionary>(
    arrayOf(TYPE_COLUMN, LOCATION_COLUMN), ITEM_EDITOR, EMPTY_TEXT, EMPTY_SUB_TEXT, { it is UserDictionary }) {
    companion object {
        /**
         * The error message that is displayed if an unknown dictionary implementation is used.
         */
        const val DICTIONARY_CAST_EXCEPTION = "Unexpected dictionary implementation."

        /**
         * The column showing the type of the dictionary.
         */
        private val TYPE_COLUMN = object : ColumnInfo<EditableDictionary, String>("Type") {
            override fun getColumnClass() = String::class.java

            override fun valueOf(item: EditableDictionary) =
                item.datum.let { dictionary ->
                    when (dictionary) {
                        is BundledDictionary -> "bundled"
                        is UserDictionary -> "user"
                        else -> error(DICTIONARY_CAST_EXCEPTION)
                    }
                }
        }

        /**
         * The column with the dictionary's file location.
         */
        private val LOCATION_COLUMN = object : EditableColumnInfo<EditableDictionary, String>("Location") {
            override fun getColumnClass() = String::class.java

            override fun valueOf(item: EditableDictionary) =
                item.datum.let { dictionary ->
                    when (dictionary) {
                        is BundledDictionary -> dictionary.filename
                        is UserDictionary -> dictionary.filename
                        else -> error(DICTIONARY_CAST_EXCEPTION)
                    }
                }

            override fun setValue(item: EditableDictionary, value: String) {
                item.datum = UserDictionary.cache.get(value, true)
            }

            override fun isCellEditable(item: EditableDictionary) = item.datum is UserDictionary

            override fun getEditor(item: EditableDictionary?) =
                LocalPathCellEditor()
                    .fileChooserDescriptor(FileChooserDescriptorFactory.createSingleFileDescriptor("dic"))
                    .normalizePath(true)
        }

        /**
         * Describes how table rows are edited.
         */
        private val ITEM_EDITOR = object : CollectionItemEditor<EditableDictionary> {
            override fun getItemClass() = createElement()::class.java

            override fun isRemovable(item: EditableDictionary) = item.datum is UserDictionary

            override fun clone(item: EditableDictionary, forInPlaceEditing: Boolean): EditableDatum<Dictionary> {
                val dictionary = item.datum
                require(dictionary is UserDictionary) { "Cannot copy non-user dictionary." }

                return EditableDatum(item.active, UserDictionary.cache.get(dictionary.filename))
            }
        }

        /**
         * The text that is displayed when the table is empty.
         */
        private const val EMPTY_TEXT = "No dictionaries configured."

        /**
         * The instruction that is displayed when the table is empty.
         */
        private const val EMPTY_SUB_TEXT = "Add dictionary"


        /**
         * Creates a new placeholder [Dictionary] instance.
         *
         * @return a new placeholder [Dictionary] instance
         */
        private fun createElement() = EditableDictionary(DEFAULT_STATE, UserDictionary.cache.get("", true))
    }


    /**
     * Creates a new placeholder [Dictionary] instance.
     *
     * @return a new placeholder [Dictionary] instance
     */
    override fun createElement() = Companion.createElement()

    /**
     * Returns `null` if a unique, non-empty selection of valid dictionaries has been made, or a `ValidationInfo` object
     * explaining which input should be changed.
     *
     * @return `null` if a unique, non-empty selection of valid dictionaries has been made, or a `ValidationInfo` object
     * explaining which input should be changed
     */
    @Suppress("ReturnCount") // Acceptable for validation functions
    fun doValidate(): ValidationInfo? {
        if (data.distinct().size != data.size)
            return ValidationInfo("Dictionaries must be unique.", panel)
        if (activeData.isEmpty())
            return ValidationInfo("Select at least one dictionary.", panel)

        data.forEach { dictionary ->
            try {
                dictionary.validate()
            } catch (e: InvalidDictionaryException) {
                return ValidationInfo("Dictionary `$dictionary` is invalid: ${e.message}", panel)
            }

            if (dictionary.words.isEmpty())
                return ValidationInfo("Dictionary `$dictionary` is empty.", panel)
        }

        return null
    }
}
