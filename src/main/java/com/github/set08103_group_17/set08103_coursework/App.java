package com.github.set08103_group_17.set08103_coursework;

import java.sql.*;
import java.util.ArrayList;

/**
 * Generate instance of Class App
 */
public class App
{
    /**
     * Connection to MySQL database, can be injected for testing
     */
    private Connection con;

    /**
     * Default constructor for App, initializes without a connection
     */
    public App() {}

    /**
     * Constructor for App allowing for a pre-existing connection
     * @param connection Mocked or external connection
     */
    public App(Connection connection) {
        this.con = connection;
    }

    /**
     * Main Method with configurable connection
     * @param args List of parameters
     * @param testConnection Optional test connection for integration testing
     */
    public static void runMain(String[] args, Connection testConnection) {
        // Create new Application
        App a = testConnection != null ? new App(testConnection) : new App();

        // Connect to database if no test connection provided
        if (testConnection == null) {
            if (args.length < 1) {
                a.connect("localhost:33060", 10000);
            } else {
                a.connect(args[0], Integer.parseInt(args[1]));
            }
        }

        // Run main logic
        try {
            System.out.println("World: " + a.getPopulation());
            ArrayList<Country> countries = a.getCountries();
            if (!countries.isEmpty()) {
                System.out.println(countries.get(0).getName() + ": " + a.getPopulation(countries.get(0)));
            }
            ArrayList<City> cities = a.getCities();
            if (!cities.isEmpty()) {
                System.out.println(cities.get(0).getName() + ": " + a.getPopulation(cities.get(0)));
            }
        } finally {
            // Disconnect from database if we created the connection
            if (testConnection == null) {
                a.disconnect();
            }
        }
    }

    /**
     * Main Method
     * @param args List of parameters that should be added to when new args are made
     */
    public static void main(String[] args) {
        runMain(args, null);
    }

    /**
     * Connect to the MySQL database.
     */
    public void connect(String location, int delay)
    {
        // Skip connecting if a connection has been injected (for testing)
        if (con != null) {
            return;
        }

        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        boolean shouldWait = false;
        for (int i=0; i < retries; i++)
        {
            System.out.println("Connecting to database...");
            try
            {
                if (shouldWait) {
                    // Wait a bit for db to start
                    Thread.sleep(delay);
                }
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location + "/world?allowPublicKeyRetrieval=true&useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());

                // Let's wait before attempting to reconnect
                shouldWait = true;
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Executes SQL statement that selects countries and returns results
     * @param statement SQL statement to run
     * @return List of countries
     */
    private ArrayList<Country> executeGetCountriesStatement(String statement)
    {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(statement);
            // Extract employee information
            ArrayList<Country> countries = new ArrayList<>();
            while (rs.next())
            {
                String code = rs.getString("Code");
                String name = rs.getString("Name");
                Country.Continent continent = Country.Continent.valueOf(rs.getString("Continent")
                        .replaceAll(" ", "_")
                        .toUpperCase());
                String region = rs.getString("Region");
                double surfaceArea = rs.getDouble("SurfaceArea");
                int independenceYear = rs.getInt("IndepYear");
                int population = rs.getInt("Population");
                double lifeExpectancy = rs.getDouble("LifeExpectancy");
                double GNP = rs.getDouble("GNP");
                double GNPOld = rs.getDouble("GNPOld");
                String localName = rs.getString("LocalName");
                String governmentForm = rs.getString("GovernmentForm");
                String headOfState = rs.getString("HeadOfState");
                int capital = rs.getInt("Capital");
                String code2 = rs.getString("Code2");
                Country country = new Country(code, name, continent, region, surfaceArea, independenceYear, population,
                        lifeExpectancy, GNP, GNPOld, localName, governmentForm, headOfState, capital, code2);
                countries.add(country);
            }
            return countries;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get country details");
            return null;
        }
    }

    /**
     * Get all countries in the world
     * @return list of countries
     */
    public ArrayList<Country> getCountries()
    {
        // Create string for SQL statement
        String select =
                "SELECT * "
                        + "FROM country "
                        + "ORDER BY population DESC";
        return executeGetCountriesStatement(select);
    }

    /**
     * Get all countries in the world in a continent
     * @param continentInput continent in the world
     * @return list of countries
     */
    public ArrayList<Country> getCountries(Country.Continent continentInput)
    {
        // Create string for SQL statement
        String select =
                "SELECT * "
                        + "FROM country "
                        + "WHERE continent = '" + continentInput + "' "
                        + "ORDER BY population DESC";
        return executeGetCountriesStatement(select);
    }

    /**
     * Get all countries in the world in a region
     * @param regionInput region in the world
     * @return list of countries
     */
    public ArrayList<Country> getCountries(String regionInput)
    {
        // Create string for SQL statement
        String select =
                "SELECT * "
                        + "FROM country "
                        + "WHERE region = '" + regionInput + "' "
                        + "ORDER BY population DESC";
        return executeGetCountriesStatement(select);
    }

    /**
     * Executes SQL statement that selects cities and returns results
     * @param statement SQL statement to run
     * @return List of cities
     */
    private ArrayList<City> executeGetCitiesStatement(String statement)
    {
        ArrayList<City> cities = new ArrayList<>();  // Initialize the list
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(statement);

            // Extract city information
            while (rs.next()) {
                // Retrieve city data from the ResultSet
                int ID = rs.getInt("ID");
                String Name = rs.getString("Name");
                String Code = rs.getString("CountryCode");
                String District = rs.getString("District");
                int Population = rs.getInt("Population");

                // Create a new City object
                City city = new City(ID, Name, Code, District, Population);

                // Add the City object to the list
                cities.add(city);
            }
            return cities;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get country details");
            return null;
        }
    }

    /**
     * Get all cities in the world
     * @return ArrayList of City objects
     */
    public ArrayList<City> getCities() {
        // Create string for SQL statement
        String select = "SELECT * FROM city ORDER BY population DESC";
        return executeGetCitiesStatement(select);
    }

    /**
     * Executes SQL statement that gets population details and returns results
     * @param statement SQL statement to run
     * @return Population
     */
    private long executeGetPopulationStatement(String statement)
    {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(statement);
            if (rs.next())
            {
                return rs.getLong("Population");
            }
            return -1;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get population details");
            return -1;
        }
    }

    /**
     * Get the population of the world
     * @return the population
     */
    public long getPopulation()
    {
        // Create string for SQL statement
        String select =
                "SELECT SUM(Population) AS Population "
                        + "FROM country "
                        + "ORDER BY Population DESC";
        return executeGetPopulationStatement(select);
    }

    /**
     * Get the population of a country
     * @return the population
     */
    public long getPopulation(Country country)
    {
        // Create string for SQL statement
        String select =
                "SELECT Population "
                        + "FROM country "
                        + "WHERE Code = '" + country.getCode() + "' "
                        + "ORDER BY Population DESC";
        return executeGetPopulationStatement(select);
    }

    /**
     * Get the population of a city
     * @return the population
     */
    public long getPopulation(City city)
    {
        // Create string for SQL statement
        String select =
                "SELECT Population "
                        + "FROM city "
                        + "WHERE ID = '" + city.getID()  + "' "
                        + "ORDER BY Population DESC";
        return executeGetPopulationStatement(select);
    }

    /**
     * Print a report on countries
     * @param countries the countries to report on
     */
    public void printCountries(ArrayList<Country> countries)
    {
        // Print header
        System.out.printf("%-4s %-52s %-15s %-26s %-15s %-10s%n", "Code", "Name", "Continent", "Region", "Population",
                "Capital");
        // Loop over all employees in the list
        for (Country country : countries)
        {
            String countryData = String.format("%-4s %-52s %-15s %-26s %-15s %-10s",
                    country.getCode(), country.getName(), country.getContinent(), country.getRegion(),
                    country.getPopulation(), country.getCapital());
            System.out.println(countryData);
        }
    }

    /**
     * Print a report on cities
     * @param cities the cities to report on
     */
    public void printCities(ArrayList<City> cities)
    {
        // Print header
        System.out.printf("%-4s %-35s %-6s %-20s %-15s%n", "ID", "Name", "Code", "District", "Population");
        // Loop over all employees in the list
        for (City City : cities)
        {
            String CityData = String.format("%-4s %-35s %-6s %-20s %-15s",
                    City.getID(), City.getName(), City.getCode(), City.getDistrict(),
                    City.getPopulation());
            System.out.println(CityData);
        }
    }
}