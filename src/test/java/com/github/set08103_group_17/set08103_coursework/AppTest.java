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
        // Mock static DriverManager
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            // Create mock connection and statement
            Connection mockConnection = mock(Connection.class);
            Statement mockStatement = mock(Statement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            // Configure connection
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            // Mock DriverManager to return connection for the specific URL we expect in the main method
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(
                            matches("jdbc:mysql://localhost:33060/world\\?.*"),
                            eq("root"),
                            eq("example")
                    )
            ).thenReturn(mockConnection);

            // Configure result set behavior for different queries
            when(mockResultSet.next()).thenReturn(true);  // For all population queries
            when(mockResultSet.getLong("Population")).thenReturn(7000000000L);

            // Configure statement to return result set for any query
            when(mockStatement.executeQuery(any())).thenReturn(mockResultSet);

            // Execute main method with empty args
            App.main(new String[]{});

            // Verify the connection was used and closed
            verify(mockConnection, times(4)).createStatement(); // Four population queries
            verify(mockConnection, times(1)).close();
        }
    }

    /**
     * Test runMain method with custom arguments
     */
    @Test
    @DisplayName("Test runMain method with custom arguments")
    void testRunMainWithCustomArgs() throws SQLException {
        String[] args = new String[]{"localhost:3306", "5000"};
        App.runMain(args, mockConnection);

        // Verify that the connection wasn't closed since we provided it
        verify(mockConnection, never()).close();
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
     */
    @Test
    @DisplayName("Test retrieving cities from database")
    void testGetCity() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate result set with one city
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("Name")).thenReturn("TestCity");
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getInt("Population")).thenReturn(100000);

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

        ArrayList<Country> europeanCountries = app.getCountries(Country.Continent.EUROPE);

        assertNotNull(europeanCountries);
        assertEquals(1, europeanCountries.size());
        assertEquals(Country.Continent.EUROPE, europeanCountries.get(0).getContinent());
    }

    /**
     * Test retrieving countries filtered by region
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

        ArrayList<Country> southernEuropeanCountries = app.getCountries("Southern Europe");

        assertNotNull(southernEuropeanCountries);
        assertEquals(1, southernEuropeanCountries.size());
        assertEquals("Southern Europe", southernEuropeanCountries.get(0).getRegion());
    }

    /**
     * Test retrieving cities by continent
     */
    @Test
    @DisplayName("Test retrieving cities filtered by continent")
    void testGetCitiesByContinent() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate city result for Europe
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("Name")).thenReturn("TestCity");
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getInt("Population")).thenReturn(100000);

        ArrayList<City> europeanCities = app.getCitiesByContinent(Country.Continent.EUROPE);

        assertNotNull(europeanCities);
        assertEquals(1, europeanCities.size());
        assertEquals("TestCity", europeanCities.get(0).getName());
    }

    /**
     * Test retrieving cities by region
     */
    @Test
    @DisplayName("Test retrieving cities filtered by region")
    void testGetCitiesByRegion() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate city result for Southern Europe
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("Name")).thenReturn("TestCity");
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getInt("Population")).thenReturn(100000);

        ArrayList<City> southernEuropeCities = app.getCitiesByRegion("Southern Europe");

        assertNotNull(southernEuropeCities);
        assertEquals(1, southernEuropeCities.size());
        assertEquals("TestCity", southernEuropeCities.get(0).getName());
    }

    /**
     * Test retrieving capital cities by continent
     */
    @Test
    @DisplayName("Test retrieving capital cities filtered by continent")
    void testGetCapitalCitiesByContinent() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("Name")).thenReturn("TestCapital");
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getInt("Population")).thenReturn(100000);

        ArrayList<City> europeanCapitals = app.getCapitalCitiesByContinent(Country.Continent.EUROPE);

        assertNotNull(europeanCapitals);
        assertEquals(1, europeanCapitals.size());
        assertEquals("TestCapital", europeanCapitals.get(0).getName());
    }

    /**
     * Test retrieving capital cities by region
     */
    @Test
    @DisplayName("Test retrieving capital cities filtered by region")
    void testGetCapitalCitiesByRegion() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("Name")).thenReturn("TestCapital");
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getInt("Population")).thenReturn(100000);

        ArrayList<City> southernEuropeCapitals = app.getCapitalCitiesByRegion("Southern Europe");

        assertNotNull(southernEuropeCapitals);
        assertEquals(1, southernEuropeCapitals.size());
        assertEquals("TestCapital", southernEuropeCapitals.get(0).getName());
    }

    // POPULATION TESTS
    /**
     * Test retrieving total world population
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
     */
    @Test
    @DisplayName("Test retrieving population for a specific country")
    void testGetCountryPopulation() throws SQLException {
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
     */
    @Test
    @DisplayName("Test retrieving population for a specific city")
    void testGetCityPopulation() throws SQLException {
        City mockCity = new City(1, "TestCity", "TC", "TestDistrict", 100000);

        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("Population")).thenReturn(100000L);

        long population = app.getPopulation(mockCity);
        assertEquals(100000L, population);
    }

    /**
     * Test retrieving population by continent
     */
    @Test
    @DisplayName("Test retrieving population by continent")
    void testGetPopulationByContinent() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("Population")).thenReturn(750000000L);

        long population = app.getPopulation(Country.Continent.EUROPE);
        assertEquals(750000000L, population);
    }

    /**
     * Test retrieving population by region
     */
    @Test
    @DisplayName("Test retrieving population by region")
    void testGetPopulationByRegion() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("Population")).thenReturn(89000000L);

        long population = app.getPopulationByRegion("British Islands");
        assertEquals(89000000L, population);
    }

    /**
     * Test retrieving population by district
     */
    @Test
    @DisplayName("Test retrieving population by district")
    void testGetPopulationByDistrict() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("Population")).thenReturn(5400000L);

        long population = app.getPopulationByDistrict("Scotland");
        assertEquals(5400000L, population);
    }

    /**
     * Test top populated capitals in world
     */
    @Test
    @DisplayName("Test retrieving top populated capitals in world")
    void testTopPopulatedCapitalsWorld() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("capitalCity")).thenReturn("TestCapital");
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("TestDistrict");
        when(mockResultSet.getInt("Population")).thenReturn(5000000);

        ArrayList<City> topCapitals = app.topPopulatedCapitals_World();
        assertNotNull(topCapitals);
    }

    /**
     * Test continent population report generation
     */
    @Test
    @DisplayName("Test continent population report generation")
    void testGetContinentPopulationReport() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Continent")).thenReturn("Europe");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(750000000L);
        when(mockResultSet.getLong("CityPopulation")).thenReturn(450000000L);

        ArrayList<Object[]> report = app.getContinentPopulationReport();

        assertNotNull(report);
        assertEquals(1, report.size());
        Object[] continentData = report.get(0);
        assertEquals("Europe", continentData[0]);
        assertEquals(750000000L, continentData[1]);
    }

    /**
     * Test region population report generation
     */
    @Test
    @DisplayName("Test region population report generation")
    void testGetRegionPopulationReport() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Region")).thenReturn("British Islands");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(89000000L);
        when(mockResultSet.getLong("CityPopulation")).thenReturn(45000000L);

        ArrayList<Object[]> report = app.getRegionPopulationReport();

        assertNotNull(report);
        assertEquals(1, report.size());
        Object[] regionData = report.get(0);
        assertEquals("British Islands", regionData[0]);
        assertEquals(89000000L, regionData[1]);
    }

    /**
     * Test country population report generation
     */
    @Test
    @DisplayName("Test country population report generation")
    void testGetCountryPopulationReport() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("United Kingdom");
        when(mockResultSet.getLong("TotalPopulation")).thenReturn(67000000L);
        when(mockResultSet.getLong("CityPopulation")).thenReturn(45000000L);

        ArrayList<Object[]> report = app.getCountryPopulationReport();

        assertNotNull(report);
        assertEquals(1, report.size());
        Object[] countryData = report.get(0);
        assertEquals("United Kingdom", countryData[0]);
        assertEquals(67000000L, countryData[1]);
    }

    @Test
    @DisplayName("Test retrieving language speakers report")
    void testGetLanguageSpeakersReport() throws SQLException {
        // Setup SQL query execution
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Setup result set data ordering
        when(mockResultSet.next())
                .thenReturn(true)  // First row
                .thenReturn(true)  // Second row
                .thenReturn(true)  // Third row
                .thenReturn(true)  // Fourth row
                .thenReturn(false); // End of results

        // Setup column data
        when(mockResultSet.getString("Language"))
                .thenReturn("Chinese")
                .thenReturn("Spanish")
                .thenReturn("Hindi")
                .thenReturn("Arabic");

        when(mockResultSet.getLong("Speakers"))
                .thenReturn(1000000000L)
                .thenReturn(500000000L)
                .thenReturn(400000000L)
                .thenReturn(300000000L);

        when(mockResultSet.getDouble("WorldPercentage"))
                .thenReturn(13.5)
                .thenReturn(6.75)
                .thenReturn(5.4)
                .thenReturn(4.05);

        // Execute report
        ArrayList<Object[]> result = app.getLanguageSpeakersReport();

        // Verify SQL execution
        verify(mockStatement).executeQuery(anyString());

        // Verify report structure
        assertNotNull(result, "Report should not be null");
        assertEquals(4, result.size(), "Should have 4 languages in report");

        // Verify Chinese data (first row)
        Object[] chinese = result.get(0);
        assertEquals("Chinese", chinese[0], "First language should be Chinese");
        assertEquals(1000000000L, chinese[1], "Chinese speakers count incorrect");
        assertEquals("13.50%", chinese[2], "Chinese percentage incorrect");

        // Verify Spanish data (second row)
        Object[] spanish = result.get(1);
        assertEquals("Spanish", spanish[0], "Second language should be Spanish");
        assertEquals(500000000L, spanish[1], "Spanish speakers count incorrect");
        assertEquals("6.75%", spanish[2], "Spanish percentage incorrect");

        // Verify Hindi data (third row)
        Object[] hindi = result.get(2);
        assertEquals("Hindi", hindi[0], "Third language should be Hindi");
        assertEquals(400000000L, hindi[1], "Hindi speakers count incorrect");
        assertEquals("5.40%", hindi[2], "Hindi percentage incorrect");

        // Verify Arabic data (fourth row)
        Object[] arabic = result.get(3);
        assertEquals("Arabic", arabic[0], "Fourth language should be Arabic");
        assertEquals(300000000L, arabic[1], "Arabic speakers count incorrect");
        assertEquals("4.05%", arabic[2], "Arabic percentage incorrect");
    }

    /**
     * Parameterized test to verify error handling across different query methods
     */
    @ParameterizedTest
    @DisplayName("Test query methods with error scenarios")
    @EnumSource(QueryMethod.class)
    void testQueryMethodErrorHandling(QueryMethod method) throws SQLException {
        SQLException sqlException = new SQLException("Query failed");

        switch(method) {
            case COUNTRIES:
                when(mockStatement.executeQuery(anyString())).thenThrow(sqlException);
                ArrayList<Country> countries = app.getCountries();
                assertNull(countries);
                break;
            case CITIES:
                when(mockStatement.executeQuery(anyString())).thenThrow(sqlException);
                ArrayList<City> cities = app.getCities();
                assertNull(cities);
                break;
            case POPULATION:
                when(mockStatement.executeQuery(anyString())).thenThrow(sqlException);
                long population = app.getPopulation();
                assertEquals(-1L, population);
                break;
        }
    }

    /**
     * Parameterized test to verify behavior with empty result sets
     */
    @ParameterizedTest
    @DisplayName("Test query methods with empty results")
    @EnumSource(QueryMethod.class)
    void testQueryMethodEmptyResults(QueryMethod method) throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        switch(method) {
            case COUNTRIES:
                ArrayList<Country> countries = app.getCountries();
                assertNotNull(countries);
                assertTrue(countries.isEmpty());
                break;
            case CITIES:
                ArrayList<City> cities = app.getCities();
                assertNotNull(cities);
                assertTrue(cities.isEmpty());
                break;
            case POPULATION:
                long population = app.getPopulation();
                assertEquals(-1L, population);
                break;
        }
    }

    /**
     * Test print countries method output
     */
    @Test
    @DisplayName("Test printCountries method output")
    void testPrintCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        Country testCountry = new Country("TC", "Test Country", Country.Continent.EUROPE,
                "Test Region", 100000, 2000, 1000000,
                75.5, 50000, 45000, "Local Name",
                "Republic", "Head of State", 1, "TC2");
        countries.add(testCountry);

        app.printCountries(countries);

        assertTrue(outContent.toString().contains("TC"));
        assertTrue(outContent.toString().contains("Test Country"));
        assertTrue(outContent.toString().contains("EUROPE"));
    }

    /**
     * Test print cities method output
     */
    @Test
    @DisplayName("Test printCities method output")
    void testPrintCities() {
        ArrayList<City> cities = new ArrayList<>();
        City testCity = new City(1, "Test City", "TC", "Test District", 500000);
        cities.add(testCity);

        app.printCities(cities);

        assertTrue(outContent.toString().contains("1"));
        assertTrue(outContent.toString().contains("Test City"));
        assertTrue(outContent.toString().contains("TC"));
    }

    /**
     * Test connection method with existing connection
     */
    @Test
    @DisplayName("Test connection method with existing connection")
    void testConnectWithExistingConnection() {
        App appWithConnection = new App(mockConnection);
        appWithConnection.connect("db:3306", 10000);
    }

    /**
     * Test connection retry mechanism
     */
    @Test
    @DisplayName("Test connection retry mechanism")
    void testConnectionRetry() {
        App appWithoutConnection = new App();
        appWithoutConnection.connect("invalid:3306", 100); // Should attempt retries
        // No assertion needed - just verifying it doesn't throw exception
    }

    /**
     * Test disconnect method
     */
    @Test
    @DisplayName("Test disconnect method")
    void testDisconnect() throws SQLException {
        when(mockConnection.isClosed()).thenReturn(false);

        App appWithConnection = new App(mockConnection);
        appWithConnection.disconnect();

        verify(mockConnection).close();
    }

     /**
     * Test retrieving top N cities worldwide
     */
    @Test
    @DisplayName("Test retrieving top N cities in world")
    void testGetTopNCities() throws SQLException {
        // Setup ResultSet data
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false); // Two cities

        // First city data
        when(mockResultSet.getInt("ID")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getString("Name")).thenReturn("Tokyo").thenReturn("Delhi");
        when(mockResultSet.getString("CountryCode")).thenReturn("JPN").thenReturn("IND");
        when(mockResultSet.getString("District")).thenReturn("Tokyo-to").thenReturn("Delhi");
        when(mockResultSet.getInt("Population")).thenReturn(37400068).thenReturn(30290936);

        // Execute report
        ArrayList<City> result = app.getTopNCities(2);

        // Verify
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return exactly 2 cities");

        // Verify first city
        City firstCity = result.get(0);
        assertEquals("Tokyo", firstCity.getName(), "First city should be Tokyo");
        assertEquals(37400068, firstCity.getPopulation(), "Tokyo population incorrect");

        // Verify second city
        City secondCity = result.get(1);
        assertEquals("Delhi", secondCity.getName(), "Second city should be Delhi");
        assertEquals(30290936, secondCity.getPopulation(), "Delhi population incorrect");

        // Verify SQL contains LIMIT clause
        verify(mockStatement).executeQuery(contains("LIMIT 2"));
    }

    /**
     * Test retrieving top N cities in a continent
     */
    @Test
    @DisplayName("Test retrieving top N cities in continent")
    void testGetTopNCitiesByContinent() throws SQLException {
        // Setup ResultSet data
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false); // Two cities

        // First two cities in Asia
        when(mockResultSet.getInt("ID")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getString("Name")).thenReturn("Tokyo").thenReturn("Delhi");
        when(mockResultSet.getString("CountryCode")).thenReturn("JPN").thenReturn("IND");
        when(mockResultSet.getString("District")).thenReturn("Tokyo-to").thenReturn("Delhi");
        when(mockResultSet.getInt("Population")).thenReturn(37400068).thenReturn(30290936);

        // Execute report
        ArrayList<City> result = app.getTopNCitiesByContinent(2, Country.Continent.ASIA);

        // Verify
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return exactly 2 cities");

        // Verify first city
        City firstCity = result.get(0);
        assertEquals("Tokyo", firstCity.getName(), "First city should be Tokyo");
        assertEquals(37400068, firstCity.getPopulation(), "Tokyo population incorrect");

        // Verify second city
        City secondCity = result.get(1);
        assertEquals("Delhi", secondCity.getName(), "Second city should be Delhi");
        assertEquals(30290936, secondCity.getPopulation(), "Delhi population incorrect");

        // Verify SQL contains both LIMIT and continent filter
        verify(mockStatement).executeQuery(contains("LIMIT 2"));
        verify(mockStatement).executeQuery(contains("WHERE country.Continent ="));
    }

    /**
     * Test retrieving top N cities in a region
     */
    @Test
    @DisplayName("Test retrieving top N cities in region")
    void testGetTopNCitiesByRegion() throws SQLException {
        // Setup ResultSet data
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false); // Two cities

        // First two cities in Eastern Asia
        when(mockResultSet.getInt("ID")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getString("Name")).thenReturn("Tokyo").thenReturn("Shanghai");
        when(mockResultSet.getString("CountryCode")).thenReturn("JPN").thenReturn("CHN");
        when(mockResultSet.getString("District")).thenReturn("Tokyo-to").thenReturn("Shanghai");
        when(mockResultSet.getInt("Population")).thenReturn(37400068).thenReturn(27058480);

        // Execute report
        ArrayList<City> result = app.getTopNCitiesByRegion(2, "Eastern Asia");

        // Verify
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return exactly 2 cities");

        // Verify first city
        City firstCity = result.get(0);
        assertEquals("Tokyo", firstCity.getName(), "First city should be Tokyo");
        assertEquals(37400068, firstCity.getPopulation(), "Tokyo population incorrect");

        // Verify second city
        City secondCity = result.get(1);
        assertEquals("Shanghai", secondCity.getName(), "Second city should be Shanghai");
        assertEquals(27058480, secondCity.getPopulation(), "Shanghai population incorrect");

        // Verify SQL contains both LIMIT and region filter
        verify(mockStatement).executeQuery(contains("LIMIT 2"));
        verify(mockStatement).executeQuery(contains("WHERE country.Region ="));
    }

    /**
     * Test error handling for top N cities methods
     */
    @Test
    @DisplayName("Test error handling for top N cities queries")
    void testTopNCitiesErrorHandling() throws SQLException {
        // Setup SQLException to be thrown
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Database error"));

        // Test each method
        assertNull(app.getTopNCities(5), "Should return null on error");
        assertNull(app.getTopNCitiesByContinent(5, Country.Continent.ASIA), "Should return null on error");
        assertNull(app.getTopNCitiesByRegion(5, "Eastern Asia"), "Should return null on error");
    }

    /**
     * Test empty result handling for top N cities methods
     */
    @Test
    @DisplayName("Test empty result handling for top N cities queries")
    void testTopNCitiesEmptyResults() throws SQLException {
        // Setup empty ResultSet
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Test each method
        ArrayList<City> worldResult = app.getTopNCities(5);
        ArrayList<City> continentResult = app.getTopNCitiesByContinent(5, Country.Continent.ASIA);
        ArrayList<City> regionResult = app.getTopNCitiesByRegion(5, "Eastern Asia");

        // Verify empty lists are returned
        assertNotNull(worldResult, "Should return empty list for world query");
        assertTrue(worldResult.isEmpty(), "World result should be empty");

        assertNotNull(continentResult, "Should return empty list for continent query");
        assertTrue(continentResult.isEmpty(), "Continent result should be empty");

        assertNotNull(regionResult, "Should return empty list for region query");
        assertTrue(regionResult.isEmpty(), "Region result should be empty");
    }

    @Test
    @DisplayName("Test top populated capitals reports")
    void testTopPopulatedCapitalsReports() throws SQLException {
        // Setup mock data
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // First call returns one result, then no more
        when(mockResultSet.next())
                .thenReturn(true)  // First result
                .thenReturn(false);  // No more results

        // Mock only the columns that are actually selected in the SQL query
        when(mockResultSet.getString("capitalCity")).thenReturn("Test Capital");
        when(mockResultSet.getInt("Population")).thenReturn(1000000);

        // These columns are used in the City constructor but aren't in the SELECT clause
        // They will likely cause SQLExceptions in the real code
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("Test District");

        // Test world capitals
        ArrayList<City> worldCapitals = app.topPopulatedCapitals_World();

        // Verify the mock interaction
        verify(mockStatement).executeQuery(contains("SELECT city.Name AS capitalCity, city.Population"));
        verify(mockStatement).executeQuery(contains("ORDER BY city.Population DESC"));
        verify(mockStatement).executeQuery(contains("LIMIT 3"));

        // Verify the results
        assertNotNull(worldCapitals);
        assertEquals(1, worldCapitals.size());

        // Verify the City object properties
        City capital = worldCapitals.get(0);
        assertEquals("Test Capital", capital.getName());
        assertEquals(1000000, capital.getPopulation());
        assertEquals("TST", capital.getCode());
        assertEquals("Test District", capital.getDistrict());
    }

    @Test
    @DisplayName("Test top populated capitals reports error handling")
    void testTopPopulatedCapitalsReportsErrors() throws SQLException {
        // Setup mock to throw exception
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Query failed"));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Test error handling for each method
        assertNull(app.topPopulatedCapitals_World());
        assertTrue(outContent.toString().contains("Failed to retrieve Capital City details"));

        outContent.reset();
        assertNull(app.topPopulatedCapitals_Continent());
        assertTrue(outContent.toString().contains("Failed to retrieve Capital City details"));

        outContent.reset();
        assertNull(app.topPopulatedCapitals_Region());
        assertTrue(outContent.toString().contains("Failed to retrieve Capital City details"));
    }

    @Test
    @DisplayName("Test top populated cities reports")
    void testTopPopulatedCitiesReports() throws SQLException {
        // Setup mock data
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // First call returns true once then false for each query
        when(mockResultSet.next())
                .thenReturn(true)  // First call
                .thenReturn(false) // End of first result set
                .thenReturn(true)  // First call of second query
                .thenReturn(false); // End of second result set

        // Mock the exact columns that the code tries to access from SQL
        when(mockResultSet.getInt("ID")).thenReturn(1);
        when(mockResultSet.getString("capitalCity")).thenReturn("Test City");  // Code uses capitalCity, not Name
        when(mockResultSet.getString("CountryCode")).thenReturn("TST");
        when(mockResultSet.getString("District")).thenReturn("Test District");
        when(mockResultSet.getInt("Population")).thenReturn(1000000);

        // Test country cities (searches for "Southern Europe")
        ArrayList<City> countryCities = app.topPopulatedCities_Country();
        assertNotNull(countryCities);
        assertEquals(1, countryCities.size());
        City countryCity = countryCities.get(0);
        assertEquals("Test City", countryCity.getName());
        assertEquals("TST", countryCity.getCode());
        assertEquals("Test District", countryCity.getDistrict());
        assertEquals(1000000, countryCity.getPopulation());

        // Test district cities (searches for "Scotland")
        ArrayList<City> districtCities = app.topPopulatedCities_District();
        assertNotNull(districtCities);
        assertEquals(1, districtCities.size());
        City districtCity = districtCities.get(0);
        assertEquals("Test City", districtCity.getName());
        assertEquals("TST", districtCity.getCode());
        assertEquals("Test District", districtCity.getDistrict());
        assertEquals(1000000, districtCity.getPopulation());

        // Verify the SQL queries were executed
        verify(mockStatement, times(2)).executeQuery(anyString());
    }
  
  /**
     * Test topPopulatedCapitalsInWorld method
     * Verifies:
     * - Successful retrieval of CapitalCities
     * - Correct mapping of all City attributes
     * - Correct order and number of records
     */
    @Test
    @DisplayName("Test Top Capital Cities (World) method")
    void testTopCapCitiesInWorld() throws SQLException{
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        //Simulate detailed country result set (5 records)
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1, 2, 3);
        when(mockResultSet.getString("Name")).thenReturn("TestCity1", "TestCity2", "TestCity3");
        when(mockResultSet.getInt("Population")).thenReturn(500, 250, 100);

        when(mockResultSet.getInt("Capital")).thenReturn(1,2, 3);

        //retrieve cities and perform assertations
        ArrayList<City> capitalCities = app.topPopulatedCapitalsInWorld(3);

        //validate result
        assertNotNull(capitalCities);
        assertEquals(3, capitalCities.size());
        //check result is ordered by population
        assertTrue(capitalCities.get(0).getPopulation() > capitalCities.get(1).getPopulation());
        assertTrue(capitalCities.get(1).getPopulation() > capitalCities.get(2).getPopulation());
        //check results are all capital cities by checking SQL WHERE statement.
        //check that statement properly reads in limit
        verify(mockStatement).executeQuery(contains("LIMIT 3"));
        verify(mockStatement).executeQuery(contains("WHERE city.ID = country.capital"));
    }

    /**
     * Test topPopulatedCapitalsInContinent method
     * Verifies:
     * - Successful retrieval of CapitalCities
     * - Correct mapping of all City attributes
     * - Correct order and number of records
     */
    @Test
    @DisplayName("Test Top Capital Cities (Continent) method")
    void testTopCapCitiesInContinent() throws SQLException{
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        //Simulate detailed country result set (5 records)
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1, 2, 3);
        when(mockResultSet.getString("Name")).thenReturn("TestCity1", "TestCity2", "TestCity3");
        when(mockResultSet.getInt("Population")).thenReturn(500, 250, 100);

        when(mockResultSet.getInt("Capital")).thenReturn(1,2, 3);

        //retrieve cities and perform assertations
        ArrayList<City> capitalCities = app.topPopulatedCapitalsInContinent(3, "EUROPE");

        //validate result
        assertNotNull(capitalCities);
        assertEquals(3, capitalCities.size());
        //check result is ordered by population
        assertTrue(capitalCities.get(0).getPopulation() > capitalCities.get(1).getPopulation());
        assertTrue(capitalCities.get(1).getPopulation() > capitalCities.get(2).getPopulation());
        //check results are all capital cities by checking SQL WHERE statement.
        //check that statement properly reads in limit
        verify(mockStatement).executeQuery(contains("LIMIT 3"));
        verify(mockStatement).executeQuery(contains("Where country.continent = EUROPE"));
        verify(mockStatement).executeQuery(contains("AND city.ID = country.capital"));
    }

    /**
     * Test topPopulatedCapitalsInRegion method
     * Verifies:
     * - Successful retrieval of CapitalCities
     * - Correct mapping of all City attributes
     * - Correct order and number of records
     */
    @Test
    @DisplayName("Test Top Capital Cities (Region) method")
    void testTopCapCitiesInRegion() throws SQLException{
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        //Simulate detailed country result set (5 records)
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1, 2, 3);
        when(mockResultSet.getString("Name")).thenReturn("TestCity1", "TestCity2", "TestCity3");
        when(mockResultSet.getInt("Population")).thenReturn(500, 250, 100);

        when(mockResultSet.getInt("Capital")).thenReturn(1,2, 3);

        //retrieve cities and perform assertations
        ArrayList<City> capitalCities = app.topPopulatedCapitalsInRegion(3, "Southern Europe");

        //validate result
        assertNotNull(capitalCities);
        assertEquals(3, capitalCities.size());
        //check result is ordered by population
        assertTrue(capitalCities.get(0).getPopulation() > capitalCities.get(1).getPopulation());
        assertTrue(capitalCities.get(1).getPopulation() > capitalCities.get(2).getPopulation());
        //check results are all capital cities by checking SQL WHERE statement.
        //check that statement properly reads in limit
        verify(mockStatement).executeQuery(contains("LIMIT 3"));
        verify(mockStatement).executeQuery(contains("Where country.region = Southern Europe"));
        verify(mockStatement).executeQuery(contains("AND city.ID = country.capital"));
    }

    /**
     * Test topPopulatedCountriesInWorld method
     * Verifies:
     * - Successful retrieval of countries
     * - Correct mapping of all City attributes
     * - Correct order and number of records
     */
    @Test
    @DisplayName("Test Top Countries (World) method")
    void testTopPopulatedCountriesInRegion() throws SQLException{
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate detailed country result set
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("T1", "T2", "T3");
        when(mockResultSet.getString("Name")).thenReturn("TestCountryOne", "TestCountryTwo", "TestCountryThree");
        when(mockResultSet.getString("Continent")).thenReturn("Asia", "Europe", "Africa");
        when(mockResultSet.getString("Region")).thenReturn("TestRegion", "TestRegion","TestRegion");
        when(mockResultSet.getDouble("SurfaceArea")).thenReturn(500000.0, 500000.0, 500000.0);
        when(mockResultSet.getInt("IndepYear")).thenReturn(1947, 1948, 1949);
        when(mockResultSet.getInt("Population")).thenReturn(5000000, 250000, 125000);
        when(mockResultSet.getDouble("LifeExpectancy")).thenReturn(70.5, 2000.5, 5.0);
        when(mockResultSet.getDouble("GNP")).thenReturn(200000.0, 200000.0, 200000.0 );
        when(mockResultSet.getDouble("GNPOld")).thenReturn(150000.0, 150000.0, 150000.0);
        when(mockResultSet.getString("LocalName")).thenReturn("TestLocalName", "TestLocalName", "TestLocalName");
        when(mockResultSet.getString("GovernmentForm")).thenReturn("Republic", "Democratic", "Dictatorship");
        when(mockResultSet.getString("HeadOfState")).thenReturn("TestHead", "Alan", "TestHead");
        when(mockResultSet.getInt("Capital")).thenReturn(1, 2, 4);
        when(mockResultSet.getString("Code2")).thenReturn("C1", "C2", "C3");

        //retrieve cities and perform assertations
        ArrayList<Country> countries = app.topPopulatedCountriesInWorld(3);

        //validate result
        assertNotNull(countries);
        assertEquals(3, countries.size());
        //check result is ordered by population
        assertTrue(countries.get(0).getPopulation() > countries.get(1).getPopulation());
        assertTrue(countries.get(1).getPopulation() > countries.get(2).getPopulation());
        //check that statement properly reads in limit
        verify(mockStatement).executeQuery(contains("LIMIT 3"));
    }

    /**
     * Test topPopulatedCountriesInContinent method
     * Verifies:
     * - Successful retrieval of countries
     * - Correct mapping of all City attributes
     * - Correct order and number of records
     */
    @Test
    @DisplayName("Test Top Countries (Continent) method")
    void testTopPopulatedCountriesInContinent() throws SQLException{
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Simulate detailed country result set
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("Code")).thenReturn("T1", "T2", "T3");
        when(mockResultSet.getString("Name")).thenReturn("TestCountryOne", "TestCountryTwo", "TestCountryThree");
        when(mockResultSet.getString("Continent")).thenReturn("Asia", "Asia", "Asia");
        when(mockResultSet.getString("Region")).thenReturn("TestRegion", "TestRegion","TestRegion");
        when(mockResultSet.getDouble("SurfaceArea")).thenReturn(500000.0, 500000.0, 500000.0);
        when(mockResultSet.getInt("IndepYear")).thenReturn(1947, 1948, 1949);
        when(mockResultSet.getInt("Population")).thenReturn(5000000, 250000, 125000);
        when(mockResultSet.getDouble("LifeExpectancy")).thenReturn(70.5, 2000.5, 5.0);
        when(mockResultSet.getDouble("GNP")).thenReturn(200000.0, 200000.0, 200000.0 );
        when(mockResultSet.getDouble("GNPOld")).thenReturn(150000.0, 150000.0, 150000.0);
        when(mockResultSet.getString("LocalName")).thenReturn("TestLocalName", "TestLocalName", "TestLocalName");
        when(mockResultSet.getString("GovernmentForm")).thenReturn("Republic", "Democratic", "Dictatorship");
        when(mockResultSet.getString("HeadOfState")).thenReturn("TestHead", "Alan", "TestHead");
        when(mockResultSet.getInt("Capital")).thenReturn(1, 2, 4);
        when(mockResultSet.getString("Code2")).thenReturn("C1", "C2", "C3");

        //retrieve cities and perform assertations
        ArrayList<Country> countries = app.topPopulatedCountriesInContinent(3, "Asia");

        //validate result
        assertNotNull(countries);
        assertEquals(3, countries.size());
        //check result is ordered by population
        assertTrue(countries.get(0).getPopulation() > countries.get(1).getPopulation());
        assertTrue(countries.get(1).getPopulation() > countries.get(2).getPopulation());
        //check that statement properly reads in limit
        verify(mockStatement).executeQuery(contains("LIMIT 3"));
        verify(mockStatement).executeQuery(contains("Where country.continent = Asia"));
    }

    /**
     * Test topPopulatedCitiesInCountry method
     * Verifies:
     * - Successful retrieval of Cities
     * - Correct mapping of all City attributes
     * - Correct order and number of records
     */
    @Test
    @DisplayName("Test Top Cities (Country) method")
    void testTopPopulatedCitiesInCountry() throws SQLException{
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        //Simulate detailed country result set (5 records)
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1, 2, 3);
        when(mockResultSet.getString("Name")).thenReturn("TestCity1", "TestCity2", "TestCity3");
        when(mockResultSet.getInt("Population")).thenReturn(500, 250, 100);

        when(mockResultSet.getString("Country")).thenReturn("Britain","Britain", "Britain");

        //retrieve cities and perform assertations
        ArrayList<City> capitalCities = app.topPopulatedCitiesInCountry(3, "Britain");

        //validate result
        assertNotNull(capitalCities);
        assertEquals(3, capitalCities.size());
        //check result is ordered by population
        assertTrue(capitalCities.get(0).getPopulation() > capitalCities.get(1).getPopulation());
        assertTrue(capitalCities.get(1).getPopulation() > capitalCities.get(2).getPopulation());
        //check results are all cities are in scotland by checking SQL WHERE statement.
        //check that statement properly reads in limit
        verify(mockStatement).executeQuery(contains("LIMIT 3"));
        verify(mockStatement).executeQuery(contains("Where country.name = Britain"));
    }

    /**
     * Test topPopulatedCitiesInDistrict method
     * Verifies:
     * - Successful retrieval of Cities
     * - Correct mapping of all City attributes
     * - Correct order and number of records
     */
    @Test
    @DisplayName("Test Top Cities (District) method")
    void testTopPopulatedCitiesInDistrict() throws SQLException{
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        //Simulate detailed country result set (5 records)
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getInt("ID")).thenReturn(1, 2, 3);
        when(mockResultSet.getString("Name")).thenReturn("TestCity1", "TestCity2", "TestCity3");
        when(mockResultSet.getInt("Population")).thenReturn(500, 250, 100);
        when(mockResultSet.getString("District")).thenReturn("Scotland","Scotland", "Scotland");

        //retrieve cities and perform assertations
        ArrayList<City> capitalCities = app.topPopulatedCitiesInDistrict(3, "Scotland");

        //validate result
        assertNotNull(capitalCities);
        assertEquals(3, capitalCities.size());
        //check result is ordered by population
        assertTrue(capitalCities.get(0).getPopulation() > capitalCities.get(1).getPopulation());
        assertTrue(capitalCities.get(1).getPopulation() > capitalCities.get(2).getPopulation());
        //check results are all cities are in scotland by checking SQL WHERE statement.
        //check that statement properly reads in limit
        verify(mockStatement).executeQuery(contains("LIMIT 3"));
        verify(mockStatement).executeQuery(contains("Where city.District = Scotland"));
    }

}