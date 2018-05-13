package com.fwdekker.randomness.word;

import com.intellij.openapi.ui.ValidationInfo;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;


/**
 * Unit tests for {@link Dictionary.UserDictionary}.
 */
public final class UserDictionaryTest {
    @Test
    public void testInvalidFile() {
        assertThatThrownBy(() -> Dictionary.UserDictionary.get("invalid_file"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Failed to read dictionary into memory.")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void testValidateSuccess() {
        final File dictionaryFile = setUpDictionary("Bbls\nOverpray\nTreeward");
        final Dictionary dictionary = Dictionary.UserDictionary.get(dictionaryFile.getAbsolutePath());

        final ValidationInfo validationInfo = dictionary.validate();

        assertThat(validationInfo).isNull();
    }

    @Test
    public void testValidateFileDoesNotExist() {
        final File dictionaryFile = setUpDictionary("Loafer\nAquavit\nSpahees");
        final Dictionary dictionary = Dictionary.UserDictionary.get(dictionaryFile.getAbsolutePath());

        if (!dictionaryFile.delete()) {
            fail("Failed to delete test file as part of test.");
        }

        final ValidationInfo validationInfo = dictionary.validate();

        assertThat(validationInfo).isNotNull();
        assertThat(validationInfo.message).matches("The dictionary file for .* no longer exists\\.");
        assertThat(validationInfo.component).isNull();
    }

    @Test
    public void testToString() {
        final File dictionaryFile = setUpDictionary("Cholers\nJaloused\nStopback");
        final Dictionary dictionary = Dictionary.UserDictionary.get(dictionaryFile.getAbsolutePath());

        assertThat(dictionary.toString()).matches("\\[custom\\] .*\\.dic");
    }


    /**
     * Creates a temporary dictionary file with the given contents.
     * <p>
     * Because the created file is a temporary file, it does not have to be cleaned up afterwards.
     *
     * @param contents the contents to write to the dictionary file
     * @return the created temporary dictionary file
     */
    private File setUpDictionary(final String contents) {
        final File dictionaryFile;

        try {
            dictionaryFile = File.createTempFile("dictionary", ".dic");
            Files.write(dictionaryFile.toPath(), contents.getBytes(StandardCharsets.UTF_8));

            return dictionaryFile;
        } catch (final IOException e) {
            fail("Could not set up dictionary file.");
            return new File("");
        }
    }
}
