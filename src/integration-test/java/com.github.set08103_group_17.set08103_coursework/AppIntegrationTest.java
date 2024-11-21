package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * 1. Test Environment:
 *    - Configures a MySQL test container using TestContainers library
 *    - Sets up database credentials (test/test) and parameters
 *    - Registers MySQL JDBC driver for database connectivity
 *
 * 2. Database:
 *    - Creates three main tables: country, city, and countrylanguage
 *    - Sets up foreign key relationships between tables
 *    - Populates tables with test data:
 *      * Two countries: USA and Japan
 *      * Two cities: New York and Tokyo
 *      * Three languages: English, Spanish (USA), and Japanese
 *
 * 3. Test Lifecycle:
 *    - @BeforeAll: Starts container and sets up initial database connection
 *    - @BeforeEach: Ensures fresh connection and captures system output
 *    - @AfterEach: Cleans up resources after each test
 *    - @AfterAll: Shuts down container and closes connections
 *
 * 4. Test Cases:
 *    a. Basic Functionality Test:
 *       - Verifies constructor initialization
 *       - Checks data retrieval for countries and cities
 *       - Validates specific entity data accuracy
 *
 *    b. Country Getters Test:
 *       - Tests all getter methods for Country class
 *       - Verifies accuracy of country-specific data
 *       - Compares data between different countries
 *
 *    c. Error Handling Test:
 *       - Checks NULL value handling
 *       - Tests system behavior with missing tables
 *
 *    d. Population Calculations Test:
 *       - Verifies world population calculation
 *       - Tests country-specific population
 *       - Validates city population data
 *
 *    e. Output Formatting Test:
 *       - Checks proper formatting of country data
 *       - Verifies city data output
 *
 *    f. Main Method Test:
 *       - Validates application's main execution path
 *
 */

/**
 * Integration tests for the App class using TestContainers for MySQL database testing.
 * These tests verify the application's interaction with a real database instance.
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integration Tests")
public class AppIntegrationTest {

    static {
        // Register MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver", e);
        }
    }

    // Test container configuration for MySQL
    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("world")
            .withUsername("test")
            .withPassword("test")
            .withUrlParam("useSSL", "false")
            .withUrlParam("allowPublicKeyRetrieval", "true")
            .waitingFor(Wait.forListeningPort());

    // Shared test resources
    private static Connection connection;
    private static App app;
    private final ByteArrayOutputStream outputCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    /**
     * Sets up the test environment before all tests.
     * Starts the MySQL container and initializes the database with test data.
     */
    @BeforeAll
    static void setUp() throws Exception {
        mysqlContainer.start();
        connection = createConnection();
        setupTestDatabase();
    }

    /**
     * Prepares the test environment before each test.
     * Ensures a valid database connection and captures system output.
     */
    @BeforeEach
    void setUpEach() throws Exception {
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
        }
        app = new App(connection);
        System.setOut(new PrintStream(outputCaptor));
    }

    /**
     * Cleans up after each test.
     * Resets the application state and system output.
     */
    @AfterEach
    void tearDownEach() {
        app = null;
        System.setOut(originalOut);
        outputCaptor.reset();
    }

    /**
     * Cleans up the test environment after all tests.
     * Closes database connections and stops the MySQL container.
     */
    @AfterAll
    static void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        mysqlContainer.stop();
    }

    /**
     * Creates a new database connection using the test container's credentials.
     * @return A Connection object to the test database
     */
    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword()
        );
    }

    /**
     * Initializes the test database with required tables and test data.
     */
    private static void setupTestDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            createTables(stmt);
            insertTestData(stmt);
        }
    }

    /**
     * Creates the necessary database tables for testing.
     * Includes country, city, and countrylanguage tables with appropriate constraints.
     */
    private static void createTables(Statement stmt) throws SQLException {
        // Create country table
        stmt.execute("CREATE TABLE country (" +
                "Code varchar(3) NOT NULL PRIMARY KEY," +
                "Name varchar(52) NOT NULL," +
                "Continent varchar(50) NOT NULL," +
                "Region varchar(26) NOT NULL," +
                "SurfaceArea float(10,2) NOT NULL," +
                "IndepYear smallint(6)," +
                "Population int(11) NOT NULL," +
                "LifeExpectancy float(3,1)," +
                "GNP float(10,2)," +
                "GNPOld float(10,2)," +
                "LocalName varchar(45) NOT NULL," +
                "GovernmentForm varchar(45) NOT NULL," +
                "HeadOfState varchar(60)," +
                "Capital int(11)," +
                "Code2 char(2) NOT NULL)");

        // Create city table with foreign key to country
        stmt.execute("CREATE TABLE city (" +
                "ID int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "Name varchar(35) NOT NULL," +
                "CountryCode varchar(3) NOT NULL," +
                "District varchar(20) NOT NULL," +
                "Population int(11) NOT NULL," +
                "FOREIGN KEY (CountryCode) REFERENCES country(Code))");

        // Create countrylanguage table with composite primary key
        stmt.execute("CREATE TABLE countrylanguage (" +
                "CountryCode varchar(3) NOT NULL," +
                "Language varchar(30) NOT NULL," +
                "IsOfficial enum('T','F') NOT NULL DEFAULT 'F'," +
                "Percentage float(4,1) NOT NULL," +
                "PRIMARY KEY (CountryCode,Language)," +
                "FOREIGN KEY (CountryCode) REFERENCES country(Code))");
    }

    /**
     * Populates the database with test data.
     * Includes sample countries, cities, and languages for testing.
     */
    private static void insertTestData(Statement stmt) throws SQLException {
        // Insert test countries
        stmt.execute("INSERT INTO country VALUES " +
                "('USA', 'United States', 'North America', 'North America', 9833517.00, 1776, " +
                "331002651, 78.9, 20940000.00, 20350000.00, 'United States', 'Federal Republic', " +
                "'Joe Biden', 1, 'US')," +
                "('JPN', 'Japan', 'Asia', 'Eastern Asia', 377835.00, -660, 125836021, 84.5, " +
                "4872415.00, 4746000.00, 'Nippon', 'Constitutional Monarchy', 'Naruhito', 2, 'JP')");

        // Insert test cities
        stmt.execute("INSERT INTO city VALUES " +
                "(1, 'New York', 'USA', 'New York', 8336817)," +
                "(2, 'Tokyo', 'JPN', 'Tokyo', 37393129)");

        // Insert test languages
        stmt.execute("INSERT INTO countrylanguage VALUES " +
                "('USA', 'English', 'T', 86.2)," +
                "('USA', 'Spanish', 'F', 10.5)," +
                "('JPN', 'Japanese', 'T', 99.1)");
    }

    /**
     * Tests basic application functionality including constructors and data retrieval.
     */
    @Test
    @DisplayName("Test basic functionality")
    void testBasicFunctionality() {
        // Test constructor initialization
        assertNotNull(new App());
        assertNotNull(new App(connection));

        // Test data retrieval
        ArrayList<Country> countries = app.getCountries();
        ArrayList<City> cities = app.getCities();

        assertNotNull(countries);
        assertNotNull(cities);
        assertEquals(2, countries.size());
        assertEquals(2, cities.size());

        // Test specific entity retrieval and data accuracy
        Country usa = findCountryByCode(countries, "USA");
        City tokyo = findCityByName(cities, "Tokyo");

        assertNotNull(usa);
        assertNotNull(tokyo);
        assertEquals("United States", usa.getName());
        assertEquals(37393129, tokyo.getPopulation());
    }

    /**
     * Tests all Country getter methods to ensure complete data retrieval.
     */
    @Test
    @DisplayName("Test Country getters")
    void testCountryGetters() {
        // Get USA data for testing
        Country usa = findCountryByCode(app.getCountries(), "USA");
        assertNotNull(usa);

        // Test surface area and independence year
        assertEquals(9833517.00, usa.getSurfaceArea(), 0.01);
        assertEquals(1776, usa.getIndependenceYear());

        // Test life expectancy and economic indicators
        assertEquals(78.9, usa.getLifeExpectancy(), 0.01);
        assertEquals(20940000.00, usa.getGNP(), 0.01);
        assertEquals(20350000.00, usa.getGNPOld(), 0.01);

        // Test additional string fields
        assertEquals("United States", usa.getLocalName());
        assertEquals("Federal Republic", usa.getGovernmentForm());
        assertEquals("US", usa.getCode2());

        // Test Japan data for comparison
        Country japan = findCountryByCode(app.getCountries(), "JPN");
        assertNotNull(japan);

        // Verify Japan's data is different
        assertEquals(377835.00, japan.getSurfaceArea(), 0.01);
        assertEquals(-660, japan.getIndependenceYear());
        assertEquals(84.5, japan.getLifeExpectancy(), 0.01);
        assertEquals(4872415.00, japan.getGNP(), 0.01);
        assertEquals(4746000.00, japan.getGNPOld(), 0.01);
        assertEquals("Nippon", japan.getLocalName());
        assertEquals("Constitutional Monarchy", japan.getGovernmentForm());
        assertEquals("JP", japan.getCode2());
    }


    /**
     * Tests application behavior with database errors and NULL values.
     */
    @Test
    @DisplayName("Test error handling")
    void testErrorHandling() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Test NULL value handling
            stmt.execute("UPDATE country SET HeadOfState = NULL WHERE Code = 'USA'");
            Country usa = findCountryByCode(app.getCountries(), "USA");
            assertNull(usa.getHeadOfState());

            // Test missing table handling
            stmt.execute("RENAME TABLE city TO city_backup");
            assertNull(app.getCities());
            stmt.execute("RENAME TABLE city_backup TO city");
        }
    }

    /**
     * Tests population calculation functionality across different entities.
     */
    @Test
    @DisplayName("Test population calculations")
    void testPopulations() {
        // Test world population
        assertEquals(456838672L, app.getPopulation());

        // Test country population
        Country japan = findCountryByCode(app.getCountries(), "JPN");
        assertEquals(125836021L, app.getPopulation(japan));

        // Test city population
        City newYork = findCityByName(app.getCities(), "New York");
        assertEquals(8336817L, app.getPopulation(newYork));
    }

    /**
     * Tests the application's output formatting functionality.
     */
    @Test
    @DisplayName("Test output formatting")
    void testOutput() {
        app.printCountries(app.getCountries());
        app.printCities(app.getCities());

        String output = outputCaptor.toString();
        assertTrue(output.contains("United States"));
        assertTrue(output.contains("Tokyo"));
    }

    /**
     * Tests the application's main method functionality.
     */
    @Test
    @DisplayName("Test main method")
    void testMain() {
        App.runMain(new String[]{}, connection);
        String output = outputCaptor.toString().trim();
        assertTrue(output.contains("World: 456838672"));
    }

    /**
     * Helper method to find a country by its code
     */
    private Country findCountryByCode(ArrayList<Country> countries, String code) {
        for (Country country : countries) {
            if (country.getCode().equals(code)) {
                return country;
            }
        }
        return null;
    }

    /**
     * Helper method to find a city by its name
     */
    private City findCityByName(ArrayList<City> cities, String name) {
        for (City city : cities) {
            if (city.getName().equals(name)) {
                return city;
            }
        }
        return null;
    }
}