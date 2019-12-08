package com.fwdekker.randomness

import com.fwdekker.randomness.array.ArraySettings


/**
 * Inserts a dummy value.
 *
 * Mostly for testing and demonstration purposes.
 *
 * @property dummyValue the dummy value that is inserted
 */
class DummyInsertAction(val dummyValue: String) : DataInsertAction() {
    override val name = "Random Dummy"

    override fun generateStrings(count: Int) = List(count) { dummyValue }
}

/**
 * Inserts an array of dummy values.
 *
 * Mostly for testing and demonstration purposes.
 *
 * @param arraySettings the settings to use for generating arrays
 */
class DummyInsertArrayAction(arraySettings: ArraySettings = ArraySettings.default, dummyValue: String) :
    DataInsertArrayAction(arraySettings, DummyInsertAction(dummyValue)) {
    override val name = "Random Dummy Array"
}