package com.fwdekker.randomness.ui

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
