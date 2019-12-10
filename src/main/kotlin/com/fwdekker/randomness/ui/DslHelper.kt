package com.fwdekker.randomness.ui

import com.intellij.ui.layout.Cell
import com.intellij.ui.layout.ComponentPredicate


/**
 * Returns the inversion of this predicate.
 *
 * @return the inversion of this predicate
 */
fun ComponentPredicate.not(): ComponentPredicate = object : ComponentPredicate() {
    private val self = this@not

    override fun addListener(listener: (Boolean) -> Unit) = self.addListener { listener.invoke(!it) }

    override fun invoke(): Boolean = !self.invoke()
}

/**
 * Creates a radio button and adds it to this cell.
 *
 * @param text the text that should appear next to the radio button
 * @param actionCommand the action command of the button
 * @param name the name of the button
 */
fun Cell.radioButton(text: String, actionCommand: String? = null, name: String? = null) =
    radioButton(text)
        .also { if (actionCommand != null) it.component.actionCommand = actionCommand }
        .also { if (name != null) it.component.name = name }
