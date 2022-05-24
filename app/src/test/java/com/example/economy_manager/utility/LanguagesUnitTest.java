package com.example.economy_manager.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LanguagesUnitTest {

    @Test
    public void englishLanguageIsWellTranslated() {
        assertEquals(Languages.ENGLISH_LANGUAGE, "English");
    }

    @Test
    public void frenchLanguageIsWellTranslated() {
        assertEquals(Languages.FRENCH_LANGUAGE, "français");
    }

    @Test
    public void germanLanguageIsWellTranslated() {
        assertEquals(Languages.GERMAN_LANGUAGE, "Deutsch");
    }

    @Test
    public void italianLanguageIsWellTranslated() {
        assertEquals(Languages.ITALIAN_LANGUAGE, "italiano");
    }

    @Test
    public void portugueseLanguageIsWellTranslated() {
        assertEquals(Languages.PORTUGUESE_LANGUAGE, "português");
    }

    @Test
    public void romanianLanguageIsWellTranslated() {
        assertEquals(Languages.ROMANIAN_LANGUAGE, "română");
    }

    @Test
    public void spanishLanguageIsWellTranslated() {
        assertEquals(Languages.SPANISH_LANGUAGE, "español");
    }
}