package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for the App class
 * Uses Mockito to simulate database connections and ResultSet behavior
 */
public class AppTest {
    // Enum to represent different query methods for parameterized testing
    // Allows us to test multiple query methods with similar error and empty result scenarios
    enum QueryMethod {
        COUNTRIES,   // Represents country-related queries
        CITIES,      // Represents city-related queries
        POPULATION   // Represents population-related queries
    }

    // Key test dependencies
    private App app;
    private Connection mockConnection;
    private Statement mockStatement;
    private ResultSet mockResultSet;

    // Output stream capture for testing print methods
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    /**
     * Setup method run before each test
     * Configures mock objects and redirects system output for testing
     */
    @BeforeEach
    void setUp() throws SQLException {
        // Capture system output to test print methods
        System.setOut(new PrintStream(outContent));

        // Create mock objects for database interactions
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockResultSet = mock(ResultSet.class);

        // Configure mock connection to return mock statement
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        // Create App instance with mock connection
        app = new App(mockConnection);
    }

    /**
     * Cleanup method run after each test
     * Restores system output and closes mock database resources
     */
    @AfterEach
    void tearDown() throws SQLException {
        // Restore original system output
        System.setOut(originalOut);

        // Close mock database resources
        if (mockResultSet != null) mockResultSet.close();
        if (mockStatement != null) mockStatement.close();
        if (mockConnection != null) mockConnection.close();
    }

    /**
     * Test main method with mocked dependencies
     * Ensures main method can execute without throwing exceptions
     */
    @Test
    @DisplayName("Test main method execution")
    void testMainMethodExecution() throws SQLException {
        // Mock static DriverManager only
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            // Create mock connection and statement
            Connection mockConnection = mock(Connection.class);
            Statement mockStatement = mock(Statement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            // Configure connection
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            // Mock DriverManager to return connection immediately
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(
                            "jdbc:mysql://db:3306/world?useSSL=false",
                            "root",
                            "example"
                    )
            ).thenReturn(mockConnection);

            // Configure result set behavior for different queries
            // First configure basic next() behavior
            when(mockResultSet.next())
                    .thenReturn(true)  // World population
                    .thenReturn(true, false)  // Countries query
                    .thenReturn(true)  // Country population
                    .thenReturn(true, false)  // Cities query
                    .thenReturn(true); // City population

            // Set up the mock data
            when(mockResultSet.getLong("Population")).thenReturn(7000000000L);
            when(mockResultSet.getString("Code")).thenReturn("TST");
            when(mockResultSet.getString("Name")).thenReturn("Test Country");
            when(mockResultSet.getString("Continent")).thenReturn("Europe");
            when(mockResultSet.getString("Region")).thenReturn("Test Region");
            when(mockResultSet.getDouble("SurfaceArea")).thenReturn(100000.0);
            when(mockResultSet.getInt("IndepYear")).thenReturn(1990);
            when(mockResultSet.getInt("Population")).thenReturn(1000000);
            when(mockResultSet.getDouble("LifeExpectancy")).thenReturn(75.5);
            when(mockResultSet.getDouble("GNP")).thenReturn(50000.0);
            when(mockResultSet.getDouble("GNPOld")).thenReturn(45000.0);
            when(mockResultSet.getString("LocalName")).thenReturn("Test Local");
            when(mockResultSet.getString("GovernmentForm")).thenReturn("Republic");
            when(mockResultSet.getString("HeadOfState")).thenReturn("Test Head");
            when(mockResultSet.getInt("Capital")).thenReturn(1);
            when(mockResultSet.getString("Code2")).thenReturn("TS");
            when(mockResultSet.getInt("ID")).thenReturn(1);
            when(mockResultSet.getString("CountryCode")).thenReturn("TST");
            when(mockResultSet.getString("District")).thenReturn("Test District");

            // Configure statement to return result set for any query
            when(mockStatement.executeQuery(any())).thenReturn(mockResultSet);

            // Capture system output
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));

            try {
                // Execute main method with empty args
                App.main(new String[]{});

                // Get the output
                String output = outContent.toString();

                // Basic verification that output contains expected data
                assertTrue(output.contains("World:"), "Output should contain world population");
                assertTrue(output.contains("Test Country"), "Output should contain test country");

            } finally {
                System.setOut(originalOut);
            }

            // Verify the connection was closed
            verify(mockConnection).close();
        }
    }

    /**
     * Test default constructor behavior
     * Ensures default constructor can be instantiated without issues
     */
    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        App app = new App();
        assertNotNull(app);
    }

    // CORE DATA RETRIEVAL TESTS
    /**
     * Test retrieving cities from the database
     * Verifies correct mapping of ResultSet data to City objects
     * Checks:
     * - Successful retrieval of cities
     * - Correct population of city attributes
     * - Handling of single city result
     */
    @Test
    @DisplayName("Test retrieving cities from database")
    void testGetCity() throws SQLException {
        // Configure mock to return predefined result
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate result set with one city
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("Name")).thenReturn("TestCity");
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getInt("Population")).thenReturn(100000);

        // Retrieve cities and perform assertions
        ArrayList<City> cities = app.getCities();

        assertNotNull(cities);
        assertEquals(1, cities.size());
        assertEquals("TestCity", cities.get(0).getName());
        assertEquals("TST", cities.get(0).getCode());
        assertEquals("TestDistrict", cities.get(0).getDistrict());
        assertEquals(100000, cities.get(0).getPopulation());
    }

    /**
     * Test retrieving all countries from the database
     * Verifies:
     * - Successful retrieval of countries
     * - Correct mapping of all country attributes
     * - Handling of single country result
     */
    @Test
    @DisplayName("Test retrieving all countries from database")
    void testGetCountries() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate detailed country result set
        when(mockResultSet.next()).thenReturn(true, false);
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

        // Retrieve countries and perform assertions
        ArrayList<Country> countries = app.getCountries();

        assertNotNull(countries);
        assertEquals(1, countries.size());
        assertEquals("TestCountry", countries.get(0).getName());
        assertEquals("ASIA", countries.get(0).getContinent().toString());
        assertEquals("TestRegion", countries.get(0).getRegion());
        assertEquals(5000000, countries.get(0).getPopulation());
    }

    /**
     * Test retrieving countries filtered by continent
     * Verifies:
     * - Ability to filter countries by specific continent
     * - Correct continent mapping
     * - Filtering functionality works as expected
     */
    @Test
    @DisplayName("Test retrieving countries filtered by continent")
    void testGetCountriesByContinent() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate country result for Europe
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("TC");
        when(mockResultSet.getString("Name")).thenReturn("TestCountry");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("TestRegion");
        when(mockResultSet.getInt("Population")).thenReturn(5000000);

        // Retrieve European countries
        ArrayList<Country> europeanCountries = app.getCountries(Country.Continent.EUROPE);

        assertNotNull(europeanCountries);
        assertEquals(1, europeanCountries.size());
        assertEquals(Country.Continent.EUROPE, europeanCountries.get(0).getContinent());
    }

    /**
     * Test retrieving countries filtered by region
     * Verifies:
     * - Ability to filter countries by specific region
     * - Correct region filtering
     * - Retrieval of countries in a specific geographic region
     */
    @Test
    @DisplayName("Test retrieving countries filtered by region")
    void testGetCountriesByRegion() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate country result for Southern Europe
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Code")).thenReturn("TC");
        when(mockResultSet.getString("Name")).thenReturn("TestCountry");
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getString("Region")).thenReturn("Southern Europe");
        when(mockResultSet.getInt("Population")).thenReturn(5000000);

        // Retrieve Southern European countries
        ArrayList<Country> southernEuropeanCountries = app.getCountries("Southern Europe");

        assertNotNull(southernEuropeanCountries);
        assertEquals(1, southernEuropeanCountries.size());
        assertEquals("Southern Europe", southernEuropeanCountries.get(0).getRegion());
    }

    // POPULATION TESTS
    /**
     * Test retrieving total world population
     * Verifies:
     * - Ability to retrieve global population
     * - Correct population value extraction
     */
    @Test
    @DisplayName("Test retrieving total world population")
    void testGetWorldPopulation() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("Population")).thenReturn(7000000000L);

        long population = app.getPopulation();
        assertEquals(7000000000L, population);
    }

    /**
     * Test retrieving population for a specific country
     * Verifies:
     * - Ability to retrieve population for a given country
     * - Correct population extraction for country-level query
     */
    @Test
    @DisplayName("Test retrieving population for a specific country")
    void testGetCountryPopulation() throws SQLException {
        // Create mock country object
        Country mockCountry = new Country("TC", "TestCountry", Country.Continent.ASIA,
                "TestRegion", 500000.0, 1947, 5000000, 70.5, 200000.0,
                150000.0, "TestLocalName", "Republic", "TestHead", 1, "TC");

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("Population")).thenReturn(5000000L);

        long population = app.getPopulation(mockCountry);
        assertEquals(5000000L, population);
    }

    /**
     * Test retrieving population for a specific city
     * Verifies:
     * - Ability to retrieve population for a given city
     * - Correct population extraction for city-level query
     */
    @Test
    @DisplayName("Test retrieving population for a specific city")
    void testGetCityPopulation() throws SQLException {
        // Create mock city object
        City mockCity = new City(1, "TestCity", "TC", "TestDistrict", 100000);

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("Population")).thenReturn(100000L);

        long population = app.getPopulation(mockCity);
        assertEquals(100000L, population);
    }

    /**
     * Parameterized test to verify error handling across different query methods
     * Ensures graceful handling of SQL exceptions
     * Key scenarios:
     * - Countries query throws exception
     * - Cities query throws exception
     * - Population query throws exception
     */
    @ParameterizedTest
    @DisplayName("Test query methods with error scenarios")
    @EnumSource(QueryMethod.class)
    void testQueryMethodErrorHandling(QueryMethod method) throws SQLException {
        // Simulate SQL exception for different query types
        SQLException sqlException = new SQLException("Query failed");

        switch(method) {
            case COUNTRIES:
                // Test country query error handling
                when(mockStatement.executeQuery(anyString())).thenThrow(sqlException);
                ArrayList<Country> countries = app.getCountries();
                assertNull(countries);
                break;
            case CITIES:
                // Test city query error handling
                when(mockStatement.executeQuery(anyString())).thenThrow(sqlException);
                ArrayList<City> cities = app.getCities();
                assertNull(cities);
                break;
            case POPULATION:
                // Test population query error handling
                when(mockStatement.executeQuery(anyString())).thenThrow(sqlException);
                long population = app.getPopulation();
                assertEquals(-1L, population);
                break;
        }
    }

    /**
     * Parameterized test to verify behavior with empty result sets
     * Ensures methods handle scenarios with no data gracefully
     * Key scenarios:
     * - Countries query returns no results
     * - Cities query returns no results
     * - Population query returns no results
     */
    @ParameterizedTest
    @DisplayName("Test query methods with empty results")
    @EnumSource(QueryMethod.class)
    void testQueryMethodEmptyResults(QueryMethod method) throws SQLException {
        // Configure mock to return empty result set
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        switch(method) {
            case COUNTRIES:
                // Verify empty country list is returned
                ArrayList<Country> countries = app.getCountries();
                assertNotNull(countries);
                assertTrue(countries.isEmpty());
                break;
            case CITIES:
                // Verify empty city list is returned
                ArrayList<City> cities = app.getCities();
                assertNotNull(cities);
                assertTrue(cities.isEmpty());
                break;
            case POPULATION:
                // Verify -1 is returned for population query
                long population = app.getPopulation();
                assertEquals(-1L, population);
                break;
        }
    }

    /**
     * Test print countries method output
     * Verifies:
     * - Correct console output for country list
     * - Proper formatting of country information
     */
    @Test
    @DisplayName("Test printCountries method output")
    void testPrintCountries() {
        // Create test country list
        ArrayList<Country> countries = new ArrayList<>();
        Country testCountry = new Country("TC", "Test Country", Country.Continent.EUROPE,
                "Test Region", 100000, 2000, 1000000,
                75.5, 50000, 45000, "Local Name",
                "Republic", "Head of State", 1, "TC2");
        countries.add(testCountry);

        // Print countries and verify output
        app.printCountries(countries);

        assertTrue(outContent.toString().contains("TC"));
        assertTrue(outContent.toString().contains("Test Country"));
        assertTrue(outContent.toString().contains("EUROPE"));
    }

    /**
     * Test print cities method output
     * Verifies:
     * - Correct console output for city list
     * - Proper formatting of city information
     */
    @Test
    @DisplayName("Test printCities method output")
    void testPrintCities() {
        // Create test city list
        ArrayList<City> cities = new ArrayList<>();
        City testCity = new City(1, "Test City", "TC", "Test District", 500000);
        cities.add(testCity);

        // Print cities and verify output
        app.printCities(cities);

        assertTrue(outContent.toString().contains("1"));
        assertTrue(outContent.toString().contains("Test City"));
        assertTrue(outContent.toString().contains("TC"));
    }

    /**
     * Test connection method with existing connection
     * Verifies:
     * - Ability to handle connection when already established
     * - No errors occur during connection attempt
     */
    @Test
    @DisplayName("Test connection method with existing connection")
    void testConnectWithExistingConnection() {
        App appWithConnection = new App(mockConnection);
        appWithConnection.connect("db:3306", 10000);
    }

    /**
     * Test disconnect method
     * Verifies:
     * - Proper closing of database connection
     * - Invocation of connection close method
     */
    @Test
    @DisplayName("Test disconnect method")
    void testDisconnect() throws SQLException {
        when(mockConnection.isClosed()).thenReturn(false);

        App appWithConnection = new App(mockConnection);
        appWithConnection.disconnect();

        verify(mockConnection).close();
    }
}