package com.fwdekker.randomness.integer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


/**
 * Unit tests for the base conversion used in [IntegerInsertAction].
 */
class IntegerInsertActionBaseTest {
    companion object {
        @JvmStatic
        fun provider() =
            listOf(
                arrayOf(33360, 10, '.', "33.360"),
                arrayOf(48345, 10, '.', "48.345"),
                arrayOf(48345, 11, '.', "33360")
            )
    }


    @ParameterizedTest
    @MethodSource("provider")
    fun testValue(value: Long, base: Int, groupingSeparator: Char, expectedString: String) {
        val integerSettings = IntegerSettings()
        integerSettings.minValue = value
        integerSettings.maxValue = value
        integerSettings.base = base
        integerSettings.groupingSeparator = groupingSeparator

        val insertRandomInteger = IntegerInsertAction(integerSettings)
        val randomString = insertRandomInteger.generateString()

        assertThat(randomString).isEqualTo(expectedString)
    }
}