package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the City class, testing its constructor and getter methods
 */
class CityTest {
    private City city;

    /**
     * Initializes a City instance with test data.
     */
    @BeforeEach
    void setUp() {
        city = new City(1, "Test City", "TST", "Test District", 1000000);
    }

    /**
     * Tests the constructor and getter methods of the City class to ensure values are initialized correctly
     */
    @Test
    @DisplayName("Test City constructor and getters")
    void testCityConstructorAndGetters() {
        assertEquals(1, city.getID(), "ID should match constructor value");
        assertEquals("Test City", city.getName(), "Name should match constructor value");
        assertEquals("TST", city.getCode(), "Code should match constructor value");
        assertEquals("Test District", city.getDistrict(), "District should match constructor value");
        assertEquals(1000000, city.getPopulation(), "Population should match constructor value");
    }
}
