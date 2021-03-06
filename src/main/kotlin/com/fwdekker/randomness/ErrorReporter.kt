package com.fwdekker.randomness

import com.intellij.ide.BrowserUtil
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.util.Consumer
import java.awt.Component


/**
 * A report submitter that opens a pre-filled issue creation form on Randomness' GitHub repository.
 *
 * This class pertains to reports of exceptions that are not caught by the plugin and end up being shown to the user
 * as a notification by IntelliJ.
 */
class ErrorReporter : ErrorReportSubmitter() {
    /**
     * Returns the text that is displayed in the button to report the error.
     *
     * @return the text that is displayed in the button to report the error
     */
    override fun getReportActionText() = "Report on GitHub"

    /**
     * Submits the exception as desired by the user.
     *
     * @param events the events that caused the exception
     * @param additionalInfo additional information provided by the user
     * @param parentComponent ignored
     * @param consumer ignored
     * @return `true`
     */
    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<SubmittedReportInfo>
    ): Boolean {
        val project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(parentComponent))
        object : Backgroundable(project, "Opening GitHub in Browser") {
            override fun run(indicator: ProgressIndicator) {
                BrowserUtil.open("https://github.com/FWDekker/intellij-randomness/issues/new" +
                    "?labels=bug" +
                    "&assignee=FWDekker" +
                    "&title=Bug report" +
                    "&body=" +
                    createMarkdownSection(
                        "Additional info",
                        if (additionalInfo.isNullOrBlank()) "_No additional information provided._" else additionalInfo
                    ) +
                    createMarkdownSection("Stacktraces", formatEvents(events)) +
                    createMarkdownSection("Version information", getFormattedVersionInformation())
                )

                ApplicationManager.getApplication().invokeLater {
                    consumer.consume(SubmittedReportInfo(
                        "https://github.com/FWDekker/intellij-randomness/issues",
                        "Issue on GitHub",
                        SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
                    ))
                }
            }
        }.queue()
        return true
    }


    /**
     * Creates a Markdown "section" containing the title in bold followed by the contents on the next line, finalized by
     * two newlines.
     *
     * @param title the title of the section
     * @param contents the contents of the section
     * @return a Markdown "section" with the given title and contents
     */
    private fun createMarkdownSection(title: String, contents: String) = "**${title.trim()}**\n${contents.trim()}\n\n"

    /**
     * Formats IDEA events as Markdown-style code blocks inside spoilers.
     *
     * @param events the events to format
     */
    private fun formatEvents(events: Array<out IdeaLoggingEvent>) =
        events.mapIndexed { i, event ->
            wrapInMarkdownSpoiler(
                title = "Stacktrace ${i + 1}/${events.size}",
                contents = wrapInCodeBlock(contents = event.throwableText.trim(), language = "java")
            )
        }.joinToString("\n\n")

    /**
     * Creates a Markdown-style code block with the given language.
     *
     * @param contents the contents of the code block
     * @param language the language of the contents
     * @return a Markdown-style code block with the given language
     */
    private fun wrapInCodeBlock(contents: String, language: String = "") = "```$language\n$contents\n```"

    /**
     * Creates a Markdown-style spoiler with the given title and contents.
     *
     * @param title the title, which is the only thing that is displayed when the contents are hidden
     * @param contents the contents which are initially hidden
     * @return a Markdown-style spoiler with the given title
     */
    private fun wrapInMarkdownSpoiler(title: String, contents: String) =
        "<details>\n<summary>$title</summary>\n<p>\n\n$contents\n\n</p>\n</details>"

    /**
     * Returns the version number of Randomness, or `null` if it could not be determined.
     *
     * @return the version number of Randomness, or `null` if it could not be determined
     */
    private fun getPluginVersion() =
        if (pluginDescriptor is IdeaPluginDescriptor) (pluginDescriptor as IdeaPluginDescriptor).version
        else null

    /**
     * Returns version information on the user's environment as a Markdown-style list.
     *
     * @return version information on the user's environment as a Markdown-style list
     */
    private fun getFormattedVersionInformation() =
        listOf(
            Pair("Randomness version", getPluginVersion() ?: "_Unknown_"),
            Pair("IDE version", ApplicationInfo.getInstance().apiVersion),
            Pair("Operating system", System.getProperty("os.name")),
            Pair("Java version", System.getProperty("java.version"))
        ).joinToString("\n") { "- ${it.first}: ${it.second}" }
}
