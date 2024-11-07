package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;


public class AppTest {
    private App app;

    @BeforeEach
    void setUp() {
        app = new App();
        app.connect();
    }

    @AfterEach
    void tearDown() {
        app.disconnect();
    }

    @Test
    @DisplayName("Test database connection")
    void testConnection() {
        assertNotNull(app, "App instance should not be null");
    }

    @Test
    @DisplayName("Test getting all cities")
    void testGetCity() {
        ArrayList<City> cities = app.getCity();
        assertNotNull(cities, "Cities list should not be null");
        assertFalse(cities.isEmpty(), "Cities list should not be empty");

        // Test first city in list has valid data
        City firstCity = cities.get(0);
        assertNotNull(firstCity.getName(), "City name should not be null");
        assertNotNull(firstCity.getCode(), "City code should not be null");
        assertTrue(firstCity.getPopulation() >= 0, "Population should be non-negative");
    }

    @Test
    @DisplayName("Test getting all countries")
    void testGetCountries() {
        ArrayList<Country> countries = app.getCountries();
        assertNotNull(countries, "Countries list should not be null");
        assertFalse(countries.isEmpty(), "Countries list should not be empty");

        // Test first country in list has valid data
        Country firstCountry = countries.get(0);
        assertNotNull(firstCountry.getName(), "Country name should not be null");
        assertNotNull(firstCountry.getCode(), "Country code should not be null");
        assertTrue(firstCountry.getPopulation() >= 0, "Population should be non-negative");
    }

    @Test
    @DisplayName("Test getting countries by continent")
    void testGetCountriesByContinent() {
        ArrayList<Country> europeanCountries = app.getCountries(Country.Continent.EUROPE);
        assertNotNull(europeanCountries, "European countries list should not be null");
        assertFalse(europeanCountries.isEmpty(), "European countries list should not be empty");

        // Verify all countries are from Europe
        for (Country country : europeanCountries) {
            assertEquals(Country.Continent.EUROPE, country.getContinent(),
                    "All countries should be from Europe");
        }
    }

    @Test
    @DisplayName("Test getting countries by region")
    void testGetCountriesByRegion() {
        String testRegion = "Southern Europe";
        ArrayList<Country> regionalCountries = app.getCountries(testRegion);
        assertNotNull(regionalCountries, "Regional countries list should not be null");
        assertFalse(regionalCountries.isEmpty(), "Regional countries list should not be empty");

        // Verify all countries are from the specified region
        for (Country country : regionalCountries) {
            assertEquals(testRegion, country.getRegion(),
                    "All countries should be from " + testRegion);
        }
    }
}
