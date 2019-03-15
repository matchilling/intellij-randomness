package com.fwdekker.randomness;

import com.fwdekker.randomness.array.ArraySettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;


/**
 * Inserts a randomly generated string at the positions of the event's editor's carets.
 */
public abstract class DataInsertAction extends AnAction {
    /**
     * Disables this action if no editor is currently opened.
     *
     * @param event the performed action
     */
    @Override
    public final void update(final AnActionEvent event) {
        final Presentation presentation = event.getPresentation();
        final Editor editor = event.getData(CommonDataKeys.EDITOR);

        presentation.setText(getName());
        presentation.setEnabled(editor != null);
    }

    /**
     * Inserts the string generated by {@link #generateString()} at the caret(s) in the editor.
     *
     * @param event the performed action
     */
    @Override
    public final void actionPerformed(final AnActionEvent event) {
        final Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        final Project project = event.getData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        final CaretModel caretModel = editor.getCaretModel();

        final Runnable replaceCaretSelections = () -> caretModel.getAllCarets().forEach(caret -> {
            final int start = caret.getSelectionStart();
            final int end = caret.getSelectionEnd();

            final String string = generateString();
            final int newEnd = start + string.length();

            document.replaceString(start, end, string);
            caret.setSelection(start, newEnd);
        });

        WriteCommandAction.runWriteCommandAction(project, replaceCaretSelections);
    }


    /**
     * Returns the name of the action to display.
     *
     * @return the name of the action to display
     */
    protected abstract String getName();

    /**
     * Generates a random string.
     *
     * @return a random string
     */
    protected abstract String generateString();


    /**
     * Inserts a randomly generated array of strings at the positions of the event's editor's carets.
     */
    public abstract class ArrayAction extends DataInsertAction {
        private final ArraySettings arraySettings;
        private final DataInsertAction dataInsertAction;


        /**
         * Constructs a new {@code DataInsertAction.ArrayAction} that uses the singleton {@code ArraySettings} instance.
         *
         * @param dataInsertAction the action to generate data with
         */
        public ArrayAction(final DataInsertAction dataInsertAction) {
            super();

            this.arraySettings = ArraySettings.getInstance();
            this.dataInsertAction = dataInsertAction;
        }


        /**
         * Generates a random array of strings.
         *
         * @return a random array of strings
         */
        @Override
        protected final String generateString() {
            final List<String> strings = new ArrayList<>();

            for (int i = 0; i < arraySettings.getCount(); i++) {
                strings.add(dataInsertAction.generateString());
            }

            return arraySettings.arrayify(strings);
        }
    }
}
