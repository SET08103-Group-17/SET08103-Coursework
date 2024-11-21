package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // Ensure predictable test order
@DisplayName("World Database Integration Tests")
public class AppIntegrationTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("world")
            .withUsername("test")
            .withPassword("test");

    private static Connection connection;
    private static App app;

    @BeforeAll
    static void setUp() throws Exception {
        mysqlContainer.start();
        connection = DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword()
        );
        setupTestDatabase();
    }

    @BeforeEach
    void setUpEach() throws Exception {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    mysqlContainer.getJdbcUrl(),
                    mysqlContainer.getUsername(),
                    mysqlContainer.getPassword()
            );
        }
        app = new App(connection);
    }

    @AfterEach
    void tearDownEach() {
        app = null;
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        mysqlContainer.stop();
    }

    private static void setupTestDatabase() throws Exception {
        Statement stmt = connection.createStatement();

        // Create tables
        stmt.execute("CREATE TABLE country (" +
                "Code varchar(3) NOT NULL," +
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
                "Code2 char(2) NOT NULL," +
                "PRIMARY KEY (Code)" +
                ")");

        stmt.execute("CREATE TABLE city (" +
                "ID int(11) NOT NULL AUTO_INCREMENT," +
                "Name varchar(35) NOT NULL," +
                "CountryCode varchar(3) NOT NULL," +
                "District varchar(20) NOT NULL," +
                "Population int(11) NOT NULL," +
                "PRIMARY KEY (ID)," +
                "FOREIGN KEY (CountryCode) REFERENCES country(Code)" +
                ")");

        stmt.execute("CREATE TABLE countrylanguage (" +
                "CountryCode varchar(3) NOT NULL," +
                "Language varchar(30) NOT NULL," +
                "IsOfficial enum('T','F') NOT NULL DEFAULT 'F'," +
                "Percentage float(4,1) NOT NULL," +
                "PRIMARY KEY (CountryCode,Language)," +
                "FOREIGN KEY (CountryCode) REFERENCES country(Code)" +
                ")");

        // Insert test data
        stmt.execute("INSERT INTO country VALUES " +
                "('USA', 'United States', 'North America', 'North America', " +
                "9833517.00, 1776, 331002651, 78.9, 20940000.00, 20350000.00, " +
                "'United States', 'Federal Republic', 'Joe Biden', 1, 'US')," +
                "('JPN', 'Japan', 'Asia', 'Eastern Asia', 377835.00, -660, " +
                "125836021, 84.5, 4872415.00, 4746000.00, 'Nippon', " +
                "'Constitutional Monarchy', 'Naruhito', 2, 'JP')");

        stmt.execute("INSERT INTO city VALUES " +
                "(1, 'New York', 'USA', 'New York', 8336817)," +
                "(2, 'Tokyo', 'JPN', 'Tokyo', 37393129)");

        stmt.execute("INSERT INTO countrylanguage VALUES " +
                "('USA', 'English', 'T', 86.2)," +
                "('USA', 'Spanish', 'F', 10.5)," +
                "('JPN', 'Japanese', 'T', 99.1)");

        stmt.close();
    }

    // Country-related tests
    @Nested
    @DisplayName("Country Integration Tests")
    class CountryTests {
        @Test
        @DisplayName("Test country retrieval and data integrity")
        void testCountryDataRetrieval() {
            ArrayList<Country> allCountries = app.getCountries();
            assertNotNull(allCountries);
            assertEquals(2, allCountries.size());

            Country usa = allCountries.stream()
                    .filter(c -> c.getCode().equals("USA"))
                    .findFirst()
                    .orElseThrow();

            // Verify all country fields
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

        @Test
        @DisplayName("Test country filtering by continent")
        void testCountryFiltering() {
            ArrayList<Country> asianCountries = app.getCountries(Country.Continent.ASIA);
            assertEquals(1, asianCountries.size());
            assertEquals("Japan", asianCountries.get(0).getName());

            ArrayList<Country> eastAsiaCountries = app.getCountries("Eastern Asia");
            assertEquals(1, eastAsiaCountries.size());
            assertEquals("Japan", eastAsiaCountries.get(0).getName());
        }
    }

    // City-related tests
    @Nested
    @DisplayName("City Integration Tests")
    class CityTests {
        @Test
        @DisplayName("Test city retrieval and data integrity")
        void testCityDataRetrieval() {
            ArrayList<City> allCities = app.getCities();
            assertNotNull(allCities);
            assertEquals(2, allCities.size());

            City newYork = allCities.stream()
                    .filter(c -> c.getName().equals("New York"))
                    .findFirst()
                    .orElseThrow();

            // Verify all city fields
            assertEquals(1, newYork.getID());
            assertEquals("USA", newYork.getCode());
            assertEquals("New York", newYork.getDistrict());
            assertEquals(8336817, newYork.getPopulation());
        }
    }

    // Population-related tests
    @Nested
    @DisplayName("Population Integration Tests")
    class PopulationTests {
        @Test
        @DisplayName("Test population calculations across entities")
        void testPopulationCalculations() {
            // World population
            long worldPop = app.getPopulation();
            assertEquals(456838672L, worldPop);

            // Country population
            ArrayList<Country> countries = app.getCountries();
            Country japan = countries.stream()
                    .filter(c -> c.getCode().equals("JPN"))
                    .findFirst()
                    .orElseThrow();
            long japanPop = app.getPopulation(japan);
            assertEquals(125836021L, japanPop);

            // City population
            ArrayList<City> cities = app.getCities();
            City tokyo = cities.stream()
                    .filter(c -> c.getName().equals("Tokyo"))
                    .findFirst()
                    .orElseThrow();
            long tokyoPop = app.getPopulation(tokyo);
            assertEquals(37393129L, tokyoPop);
        }
    }

    // Cross-entity relationship tests
    @Nested
    @DisplayName("Cross-Entity Integration Tests")
    class CrossEntityTests {
        @Test
        @DisplayName("Test relationships between cities and countries")
        void testCityCountryRelationship() {
            ArrayList<City> allCities = app.getCities();
            ArrayList<Country> allCountries = app.getCountries();

            // Verify each city has a valid country code
            for (City city : allCities) {
                boolean hasValidCountry = allCountries.stream()
                        .anyMatch(country -> country.getCode().equals(city.getCode()));
                assertTrue(hasValidCountry, "City " + city.getName() + " should have a valid country code");
            }
        }
    }

    // Connection management tests
    @Nested
    @DisplayName("Database Connection Tests")
    class ConnectionTests {
        @Test
        @DisplayName("Test connection management")
        void testConnectionManagement() {
            ArrayList<Country> initialCountries = app.getCountries();
            assertNotNull(initialCountries);
            assertFalse(initialCountries.isEmpty());

            App newApp = new App(connection);
            ArrayList<Country> newCountries = newApp.getCountries();
            assertNotNull(newCountries);
            assertEquals(initialCountries.size(), newCountries.size());
        }
    }
}