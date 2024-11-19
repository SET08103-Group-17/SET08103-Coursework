package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Country class, testing its constructor and getter methods
 */
class CountryTest {
    private Country country;

    /**
     * Initializes a Country instance with test data
     */
    @BeforeEach
    void setUp() {
        country = new Country(
                "TST", "Test Country", Country.Continent.EUROPE, "Test Region",
                100000.0, 1900, 1000000, 75.5, 1000000.0, 950000.0,
                "Local Name", "Republic", "Test President", 1, "TS"
        );
    }

    /**
     * Tests the constructor and getter methods of the Country class to ensure values are initialized correctly
     */
    @Test
    @DisplayName("Test Country constructor and getters")
    void testCountryConstructorAndGetters() {
        assertEquals("TST", country.getCode(), "Code should match constructor value");
        assertEquals("Test Country", country.getName(), "Name should match constructor value");
        assertEquals(Country.Continent.EUROPE, country.getContinent(), "Continent should match constructor value");
        assertEquals("Test Region", country.getRegion(), "Region should match constructor value");
        assertEquals(100000.0, country.getSurfaceArea(), "Surface area should match constructor value");
        assertEquals(1900, country.getIndependenceYear(), "Independence year should match constructor value");
        assertEquals(1000000, country.getPopulation(), "Population should match constructor value");
        assertEquals(75.5, country.getLifeExpectancy(), "Life expectancy should match constructor value");
        assertEquals(1000000.0, country.getGNP(), "GNP should match constructor value");
        assertEquals(950000.0, country.getGNPOld(), "GNP Old should match constructor value");
        assertEquals("Local Name", country.getLocalName(), "Local name should match constructor value");
        assertEquals("Republic", country.getGovernmentForm(), "Government form should match constructor value");
        assertEquals("Test President", country.getHeadOfState(), "Head of state should match constructor value");
        assertEquals(1, country.getCapital(), "Capital should match constructor value");
        assertEquals("TS", country.getCode2(), "Code2 should match constructor value");
    }
}
