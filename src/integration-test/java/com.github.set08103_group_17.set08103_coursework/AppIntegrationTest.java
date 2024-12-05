package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the App class using TestContainers for MySQL database testing.
 * These tests verify the application's interaction with a real database instance.
 * Setup includes:
 * - MySQL test container configuration
 * - Test database initialization
 * - Sample data population for countries, cities, and languages
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integration Tests")
public class AppIntegrationTest {

    // Load MySQL JDBC driver for database connectivity
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver", e);
        }
    }

    /**
     * TestContainer for MySQL database
     * Configured with:
     * - MySQL 8.0 image
     * - Custom database name, credentials
     * - SSL disabled for testing
     * - Public key retrieval allowed
     */
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

    /**
     * Global test setup executed once before all tests
     * - Starts MySQL container
     * - Establishes database connection
     * - Initializes test database schema and data
     */
    @BeforeAll
    static void setUp() throws Exception {
        mysqlContainer.start();
        connection = createConnection();
        setupTestDatabase();
    }

    /**
     * Setup executed before each test
     * - Ensures valid database connection
     * - Creates fresh App instance
     */
    @BeforeEach
    void setUpEach() throws Exception {
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
        }
        app = new App(connection);
    }

    /**
     * Global teardown executed after all tests
     * - Closes database connection
     * - Stops MySQL container
     */
    @AfterAll
    static void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        mysqlContainer.stop();
    }

    /**
     * Creates a database connection using TestContainer credentials
     * @return Connection to test database
     */
    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword()
        );
    }

    /**
     * Sets up test database schema and populates with sample data
     */
    private static void setupTestDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            createTables(stmt);
            insertTestData(stmt);
        }
    }

    /**
     * Creates database tables with appropriate constraints:
     * - country: Primary table with country information
     * - city: Cities with foreign key to country
     * - countrylanguage: Languages with composite key and foreign key to country
     */
    private static void createTables(Statement stmt) throws SQLException {
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

        stmt.execute("CREATE TABLE city (" +
                "ID int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "Name varchar(35) NOT NULL," +
                "CountryCode varchar(3) NOT NULL," +
                "District varchar(20) NOT NULL," +
                "Population int(11) NOT NULL," +
                "FOREIGN KEY (CountryCode) REFERENCES country(Code))");

        stmt.execute("CREATE TABLE countrylanguage (" +
                "CountryCode varchar(3) NOT NULL," +
                "Language varchar(30) NOT NULL," +
                "IsOfficial enum('T','F') NOT NULL DEFAULT 'F'," +
                "Percentage float(4,1) NOT NULL," +
                "PRIMARY KEY (CountryCode,Language)," +
                "FOREIGN KEY (CountryCode) REFERENCES country(Code))");
    }

    /**
     * Populates tables with test data including:
     * - Sample countries (USA, Japan)
     * - Major cities (New York, Tokyo)
     * - Official and secondary languages
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
     * Tests database relationship constraints:
     * - Foreign key constraints between tables
     * - Cascading delete restrictions
     */
    @Test
    @DisplayName("Test database relationships and constraints")
    void testDatabaseRelationships() throws SQLException {
        // Test foreign key constraint
        assertThrows(SQLException.class, () -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("INSERT INTO city (Name, CountryCode, District, Population) " +
                        "VALUES ('Invalid City', 'XXX', 'District', 1000)");
            }
        });

        // Test cascading operations
        try (Statement stmt = connection.createStatement()) {
            // Try to delete a country that has cities (should fail due to FK constraint)
            assertThrows(SQLException.class, () ->
                    stmt.execute("DELETE FROM country WHERE Code = 'USA'")
            );

            // Verify the data still exists
            ResultSet rs = stmt.executeQuery("SELECT * FROM country WHERE Code = 'USA'");
            assertTrue(rs.next());
        }
    }

    /**
     * Tests transaction management:
     * - Transaction rollback on error
     * - Data consistency after rollback
     * - Primary key constraint violations
     */
    @Test
    @DisplayName("Test transaction management")
    void testTransactionManagement() throws SQLException {
        connection.setAutoCommit(false);
        try {
            try (Statement stmt = connection.createStatement()) {
                // Insert test data
                stmt.execute("INSERT INTO country VALUES ('TST', 'Test', 'Asia', 'Test Region', " +
                        "1000.0, 2000, 1000000, 75.5, 50000.0, 45000.0, 'Test', 'Republic', " +
                        "'Test Leader', 1, 'TS')");

                // This should fail due to duplicate primary key
                stmt.execute("INSERT INTO country VALUES ('TST', 'Test2', 'Asia', 'Test Region', " +
                        "1000.0, 2000, 1000000, 75.5, 50000.0, 45000.0, 'Test', 'Republic', " +
                        "'Test Leader', 1, 'TS')");
            }
            fail("Expected SQLException was not thrown");
        } catch (SQLException e) {
            connection.rollback();

            // Verify rollback was successful
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM country WHERE Code = 'TST'")) {
                assertFalse(rs.next(), "Transaction should have been rolled back");
            }
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Tests handling of large datasets
     */
    @Test
    @DisplayName("Test large dataset handling")
    void testLargeDatasetHandling() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert large number of test records
            for (int i = 0; i < 1000; i++) {
                stmt.execute(String.format(
                        "INSERT INTO city (Name, CountryCode, District, Population) " +
                                "VALUES ('City%d', 'USA', 'TestDistrict', %d)", i, 1000 + i));
            }

            // Test data retrieval with large dataset
            ArrayList<City> cities = app.getCities();
            assertNotNull(cities);
            assertTrue(cities.size() >= 1000);

            // Test population calculations with large dataset
            long totalPopulation = app.getPopulation();
            assertTrue(totalPopulation > 0);
        }
    }

    /**
     * Tests complex database queries:
     * - Joins across multiple tables
     * - Aggregation operations
     * - Complex filtering conditions
     */
    @Test
    @DisplayName("Test complex queries and joins")
    void testComplexQueries() throws SQLException {
        // Test population reports (involves joins and aggregations)
        ArrayList<Object[]> continentReport = app.getContinentPopulationReport();
        assertNotNull(continentReport);
        assertFalse(continentReport.isEmpty());

        ArrayList<Object[]> regionReport = app.getRegionPopulationReport();
        assertNotNull(regionReport);
        assertFalse(regionReport.isEmpty());

        // Verify specific data relationships
        ArrayList<City> capitalCities = app.getCapitalCitiesByContinent(Country.Continent.ASIA);
        assertNotNull(capitalCities);
        assertTrue(capitalCities.stream()
                .anyMatch(city -> city.getName().equals("Tokyo")));
    }

    /**
     * Tests CountryLanguage relationships:
     * - Foreign key constraints with Country table
     * - Composite primary key constraints
     * - Data integrity for language relationships
     */
    @Test
    @DisplayName("Test CountryLanguage relationships and constraints")
    void testCountryLanguageRelationships() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Test foreign key constraint
            assertThrows(SQLException.class, () -> {
                stmt.execute("INSERT INTO countrylanguage (CountryCode, Language, IsOfficial, Percentage) " +
                        "VALUES ('XXX', 'Test Language', 'T', 50.0)");
            }, "Should not allow inserting language for non-existent country");

            // Test composite primary key constraint
            assertThrows(SQLException.class, () -> {
                stmt.execute("INSERT INTO countrylanguage (CountryCode, Language, IsOfficial, Percentage) " +
                        "VALUES ('USA', 'English', 'T', 75.0)");
            }, "Should not allow duplicate composite primary key");

            // Insert valid test data and verify relationships
            stmt.execute("INSERT INTO countrylanguage (CountryCode, Language, IsOfficial, Percentage) " +
                    "VALUES ('USA', 'French', 'F', 1.0)");

            // Verify data relationships
            ResultSet rs = stmt.executeQuery(
                    "SELECT cl.Language, c.Name " +
                            "FROM countrylanguage cl " +
                            "JOIN country c ON cl.CountryCode = c.Code " +
                            "WHERE cl.IsOfficial = 'T'"
            );

            // Verify Japan's official language
            boolean foundJapanese = false;
            while (rs.next()) {
                if (rs.getString("Language").equals("Japanese")) {
                    assertEquals("Japan", rs.getString("Name"));
                    foundJapanese = true;
                }
            }
            assertTrue(foundJapanese, "Should find Japanese as official language for Japan");

            // Clean up test data
            stmt.execute("DELETE FROM countrylanguage WHERE CountryCode = 'USA' AND Language = 'French'");
        }
    }

    /**
     * Tests complex language-related queries:
     * - Language distribution statistics
     * - Official language tracking
     * - Percentage validations
     */
    @Test
    @DisplayName("Test complex language-related queries")
    void testLanguageQueries() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Test querying official languages percentage distribution
            ResultSet rs = stmt.executeQuery(
                    "SELECT c.Name, COUNT(cl.Language) as LanguageCount, " +
                            "SUM(CASE WHEN cl.IsOfficial = 'T' THEN 1 ELSE 0 END) as OfficialCount " +
                            "FROM country c " +
                            "LEFT JOIN countrylanguage cl ON c.Code = cl.CountryCode " +
                            "GROUP BY c.Code"
            );

            while (rs.next()) {
                if (rs.getString("Name").equals("United States")) {
                    assertEquals(2, rs.getInt("LanguageCount"),
                            "USA should have 2 languages in test data");
                    assertEquals(1, rs.getInt("OfficialCount"),
                            "USA should have 1 official language");
                }
            }

            // Verify language percentages
            ResultSet languagePercentages = stmt.executeQuery(
                    "SELECT Language, Percentage " +
                            "FROM countrylanguage " +
                            "WHERE CountryCode = 'USA'"
            );

            while (languagePercentages.next()) {
                double percentage = languagePercentages.getDouble("Percentage");
                assertTrue(percentage >= 0.0 && percentage <= 100.0,
                        "Language percentage should be between 0 and 100");
            }
        }
    }

    /**
     * Tests complete Country object creation and getters from database
     */
    @Test
    @DisplayName("Test Country object creation and getters")
    void testCountryObjectCreation() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM country WHERE Code = 'USA'")) {

            assertTrue(rs.next(), "USA record should exist");

            // Create Country object from result set
            Country usa = new Country(
                    rs.getString("Code"),
                    rs.getString("Name"),
                    Country.Continent.valueOf(rs.getString("Continent").replace(" ", "_").toUpperCase()),
                    rs.getString("Region"),
                    rs.getDouble("SurfaceArea"),
                    rs.getInt("IndepYear"),
                    rs.getInt("Population"),
                    rs.getDouble("LifeExpectancy"),
                    rs.getDouble("GNP"),
                    rs.getDouble("GNPOld"),
                    rs.getString("LocalName"),
                    rs.getString("GovernmentForm"),
                    rs.getString("HeadOfState"),
                    rs.getInt("Capital"),
                    rs.getString("Code2")
            );

            // Test all getters
            assertEquals("USA", usa.getCode());
            assertEquals("United States", usa.getName());
            assertEquals(Country.Continent.NORTH_AMERICA, usa.getContinent());
            assertEquals("North America", usa.getRegion());
            assertEquals(9833517.00, usa.getSurfaceArea(), 0.01);
            assertEquals(1776, usa.getIndependenceYear());
            assertEquals(331002651, usa.getPopulation());
            assertEquals(78.9, usa.getLifeExpectancy(), 0.01);
            assertEquals(20940000.00, usa.getGNP(), 0.01);
            assertEquals(20350000.00, usa.getGNPOld(), 0.01);
            assertEquals("United States", usa.getLocalName());
            assertEquals("Federal Republic", usa.getGovernmentForm());
            assertEquals("Joe Biden", usa.getHeadOfState());
            assertEquals(1, usa.getCapital());
            assertEquals("US", usa.getCode2());
        }
    }

    /**
     * Tests CountryLanguage object creation and getters from database
     */
    @Test
    @DisplayName("Test CountryLanguage object creation and getters")
    void testCountryLanguageObjectCreation() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM countrylanguage WHERE CountryCode = 'USA' AND Language = 'English'")) {

            assertTrue(rs.next(), "English language record for USA should exist");

            // Create CountryLanguage object from result set
            CountryLanguage english = new CountryLanguage(
                    rs.getString("CountryCode"),
                    rs.getString("Language"),
                    rs.getString("IsOfficial").equals("T"),
                    rs.getDouble("Percentage")
            );

            // Test all getters
            assertEquals("USA", english.getCountryCode());
            assertEquals("English", english.getLanguage());
            assertTrue(english.getIsOfficial());
            assertEquals(86.2, english.getPercentage(), 0.01);
        }
    }

    /**
     * Tests creation of Country objects for all continents
     */
    @Test
    @DisplayName("Test Country continent enum handling")
    void testCountryContinents() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert test countries for each continent
            stmt.execute("INSERT INTO country VALUES " +
                    "('TST', 'Test Europe', 'Europe', 'Test Region', 1000.0, 2000, 1000000, " +
                    "75.5, 50000.0, 45000.0, 'Test', 'Republic', 'Test Leader', 1, 'TS')");

            stmt.execute("INSERT INTO country VALUES " +
                    "('TS2', 'Test Africa', 'Africa', 'Test Region', 1000.0, 2000, 1000000, " +
                    "75.5, 50000.0, 45000.0, 'Test', 'Republic', 'Test Leader', 1, 'T2')");

            // Query and verify continent enum handling
            ResultSet rs = stmt.executeQuery("SELECT * FROM country WHERE Code IN ('TST', 'TS2', 'USA', 'JPN')");

            while (rs.next()) {
                Country country = new Country(
                        rs.getString("Code"),
                        rs.getString("Name"),
                        Country.Continent.valueOf(rs.getString("Continent").replace(" ", "_").toUpperCase()),
                        rs.getString("Region"),
                        rs.getDouble("SurfaceArea"),
                        rs.getInt("IndepYear"),
                        rs.getInt("Population"),
                        rs.getDouble("LifeExpectancy"),
                        rs.getDouble("GNP"),
                        rs.getDouble("GNPOld"),
                        rs.getString("LocalName"),
                        rs.getString("GovernmentForm"),
                        rs.getString("HeadOfState"),
                        rs.getInt("Capital"),
                        rs.getString("Code2")
                );

                // Verify continent is correctly mapped
                switch (rs.getString("Code")) {
                    case "USA":
                        assertEquals(Country.Continent.NORTH_AMERICA, country.getContinent());
                        break;
                    case "JPN":
                        assertEquals(Country.Continent.ASIA, country.getContinent());
                        break;
                    case "TST":
                        assertEquals(Country.Continent.EUROPE, country.getContinent());
                        break;
                    case "TS2":
                        assertEquals(Country.Continent.AFRICA, country.getContinent());
                        break;
                }
            }

            // Clean up test data
            stmt.execute("DELETE FROM country WHERE Code IN ('TST', 'TS2')");
        }
    }

    /**
     * Tests CountryLanguage objects with various percentage values and official status
     */
    @Test
    @DisplayName("Test CountryLanguage variations")
    void testCountryLanguageVariations() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert test languages with different percentages and official status
            stmt.execute("INSERT INTO countrylanguage VALUES " +
                    "('USA', 'Test1', 'T', 0.1)," +
                    "('USA', 'Test2', 'F', 100.0)");

            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM countrylanguage WHERE CountryCode = 'USA' AND Language LIKE 'Test%'");

            while (rs.next()) {
                CountryLanguage language = new CountryLanguage(
                        rs.getString("CountryCode"),
                        rs.getString("Language"),
                        rs.getString("IsOfficial").equals("T"),
                        rs.getDouble("Percentage")
                );

                // Verify correct mapping of values
                assertEquals("USA", language.getCountryCode());
                if (language.getLanguage().equals("Test1")) {
                    assertTrue(language.getIsOfficial());
                    assertEquals(0.1, language.getPercentage(), 0.01);
                } else if (language.getLanguage().equals("Test2")) {
                    assertFalse(language.getIsOfficial());
                    assertEquals(100.0, language.getPercentage(), 0.01);
                }
            }

            // Clean up test data
            stmt.execute("DELETE FROM countrylanguage WHERE CountryCode = 'USA' AND Language LIKE 'Test%'");
        }
    }
}