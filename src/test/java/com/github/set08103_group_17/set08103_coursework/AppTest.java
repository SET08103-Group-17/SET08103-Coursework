package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for the App class, testing its functionality with mocked database connections.
 * Uses Mockito to mock database interactions
 */
public class AppTest {
    private App app;
    private Statement mockStatement;
    private ResultSet mockResultSet;

    /**
     * Sets up the necessary mocks and initializes App with a mocked Connection
     *
     * @throws SQLException if an SQL error occurs during setup
     */
    @BeforeEach
    void setUp() throws SQLException {
        // Create mocks for Connection, Statement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockResultSet = mock(ResultSet.class);

        // Set up App with the mocked Connection
        app = new App(mockConnection);

        // Mock behavior for connection.createStatement()
        when(mockConnection.createStatement()).thenReturn(mockStatement);
    }

    /**
     * Tests the getCity() method of App using a mocked database connection to retrieve city data
     *
     * @throws SQLException if an SQL error occurs during the test
     */
    @Test
    @DisplayName("Test getting all cities with mocked connection")
    void testGetCity() throws SQLException {
        // Mock behavior for the SQL query
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate result set data for cities
        when(mockResultSet.next()).thenReturn(true, false);  // Only one city in mock
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("Name")).thenReturn("TestCity");
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getInt("Population")).thenReturn(100000);

        ArrayList<City> cities = app.getCities();

        // Assertions to validate the mock data is handled correctly
        assertNotNull(cities);
        assertEquals(1, cities.size());
        assertEquals("TestCity", cities.get(0).getName());
        assertEquals("TST", cities.get(0).getCode());
        assertEquals("TestDistrict", cities.get(0).getDistrict());
        assertEquals(100000, cities.get(0).getPopulation());
    }

    /**
     * Tests the getCountriesByContinent() method of App to retrieve countries by continent using a mocked database connection
     *
     * @throws SQLException if an SQL error occurs during the test
     */
    @Test
    @DisplayName("Test getting all countries with mocked connection")
    void testGetCountries() throws SQLException {
        // Mock behavior for the SQL query
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate result set data for countries
        when(mockResultSet.next()).thenReturn(true, false);  // Only one country in mock
        when(mockResultSet.getString("Code")).thenReturn("TC");
        when(mockResultSet.getString("Name")).thenReturn("TestCountry");
        when(mockResultSet.getString("Continent")).thenReturn("Asia");
        when(mockResultSet.getString("Region")).thenReturn("TestRegion");
        when(mockResultSet.getDouble("SurfaceArea")).thenReturn(500000.0);
        when(mockResultSet.getInt("IndepYear")).thenReturn(1947);
        when(mockResultSet.getInt("Population")).thenReturn(5000000);
        when(mockResultSet.getDouble("LifeExpectancy")).thenReturn(70.5);
        when(mockResultSet.getDouble("GNP")).thenReturn(200000.0);
        when(mockResultSet.getDouble("GNPOld")).thenReturn(150000.0);
        when(mockResultSet.getString("LocalName")).thenReturn("TestLocalName");
        when(mockResultSet.getString("GovernmentForm")).thenReturn("Republic");
        when(mockResultSet.getString("HeadOfState")).thenReturn("TestHead");
        when(mockResultSet.getInt("Capital")).thenReturn(1);
        when(mockResultSet.getString("Code2")).thenReturn("TC");

        ArrayList<Country> countries = app.getCountries();

        // Assertions to validate the mock data is handled correctly
        assertNotNull(countries);
        assertEquals(1, countries.size());
        assertEquals("TestCountry", countries.get(0).getName());
        assertEquals("ASIA", countries.get(0).getContinent().toString());
        assertEquals("TestRegion", countries.get(0).getRegion());
        assertEquals(5000000, countries.get(0).getPopulation());
    }

    @Test
    @DisplayName("Test getting countries by continent with mocked connection")
    void testGetCountriesByContinent() throws SQLException {
        // Mock behavior for the SQL query
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate result set data for countries in a continent
        when(mockResultSet.next()).thenReturn(true, false);  // Only one country in mock
        when(mockResultSet.getString("Code")).thenReturn("TC");
        when(mockResultSet.getString("Name")).thenReturn("TestCountry");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("TestRegion");
        when(mockResultSet.getInt("Population")).thenReturn(5000000);

        ArrayList<Country> europeanCountries = app.getCountries(Country.Continent.EUROPE);

        // Assertions to validate the mock data is handled correctly
        assertNotNull(europeanCountries);
        assertEquals(1, europeanCountries.size());
        assertEquals(Country.Continent.EUROPE, europeanCountries.get(0).getContinent());
    }

    /**
     * Tests the getCountriesByRegion() method of App to retrieve countries by region using a mocked database connection
     *
     * @throws SQLException if an SQL error occurs during the test
     */
    @Test
    @DisplayName("Test getting countries by region with mocked connection")
    void testGetCountriesByRegion() throws SQLException {
        // Mock behavior for the SQL query
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate result set data for countries in a region
        when(mockResultSet.next()).thenReturn(true, false);  // Only one country in mock
        when(mockResultSet.getString("Code")).thenReturn("TC");
        when(mockResultSet.getString("Name")).thenReturn("TestCountry");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Southern Europe");
        when(mockResultSet.getInt("Population")).thenReturn(5000000);

        ArrayList<Country> southernEuropeanCountries = app.getCountries("Southern Europe");

        // Assertions to validate the mock data is handled correctly
        assertNotNull(southernEuropeanCountries);
        assertEquals(1, southernEuropeanCountries.size());
        assertEquals("Southern Europe", southernEuropeanCountries.get(0).getRegion());
    }

    /**
     * Cleans up after each test by disconnecting the app's database connection
     */
    @AfterEach
    void tearDown() {
        app.disconnect();
    }
}