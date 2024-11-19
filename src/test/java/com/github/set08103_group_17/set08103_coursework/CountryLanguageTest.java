package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the CountryLanguage class, testing its constructor and getter methods
 */
class CountryLanguageTest {
    private CountryLanguage language;

    /**
     * Initializes a CountryLanguage instance with test data
     */
    @BeforeEach
    void setUp() {
        language = new CountryLanguage("TST", "Test Language", true, 95.5);
    }

    /**
     * Tests the constructor and getter methods of the CountryLanguage class to ensure values are initialized correctly
     */
    @Test
    @DisplayName("Test CountryLanguage constructor and getters")
    void testCountryLanguageConstructorAndGetters() {
        assertEquals("TST", language.getCountryCode(), "Country code should match constructor value");
        assertEquals("Test Language", language.getLanguage(), "Language should match constructor value");
        assertTrue(language.getIsOfficial(), "IsOfficial should match constructor value");
        assertEquals(95.5, language.getPercentage(), "Percentage should match constructor value");
    }
}