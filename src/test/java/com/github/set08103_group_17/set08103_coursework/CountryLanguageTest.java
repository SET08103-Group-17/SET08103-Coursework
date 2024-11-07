package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class CountryLanguageTest {
    private CountryLanguage language;

    @BeforeEach
    void setUp() {
        language = new CountryLanguage("TST", "Test Language", true, 95.5);
    }

    @Test
    @DisplayName("Test CountryLanguage constructor and getters")
    void testCountryLanguageConstructorAndGetters() {
        assertEquals("TST", language.getCountryCode(), "Country code should match constructor value");
        assertEquals("Test Language", language.getLanguage(), "Language should match constructor value");
        assertTrue(language.getIsOfficial(), "IsOfficial should match constructor value");
        assertEquals(95.5, language.getDecimal(), "Decimal should match constructor value");
    }
}