package com.github.set08103_group_17.set08103_coursework;

import java.sql.*;
import java.util.ArrayList;

/**
 * Generate instance of Class App
 */
public class App {
    /**
     * Connection to MySQL database, can be injected for testing
     */
    private Connection con;

    /**
     * Default constructor for App, initializes without a connection
     */
    public App() {
    }

    /**
     * Constructor for App allowing for a pre-existing connection
     *
     * @param connection Mocked or external connection
     */
    public App(Connection connection) {
        this.con = connection;
    }

    /**
     * Main Method with configurable connection
     *
     * @param args           List of parameters
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
            System.out.println("Population Information:");
            System.out.println("\tWorld - " + a.getPopulation());
            System.out.println("\tEurope - " + a.getPopulation(Country.Continent.EUROPE));
            System.out.println("\tBritish Islands - " + a.getPopulationByRegion("British Islands"));
            System.out.println("\tScotland - " + a.getPopulationByDistrict("Scotland"));

            System.out.println("Population Reports:");

            // 1. All countries in world by population
            System.out.println("\n1. Countries in world by population:");
            a.printCountries(a.getCountries());

            // 2. All countries in continent by population
            System.out.println("\n2. Countries in continent by population:");
            a.printCountries(a.getCountries(Country.Continent.EUROPE));

            // 3. All countries in region by population
            System.out.println("\n3. Countries in region by population:");
            a.printCountries(a.getCountries("British Islands"));

            // 4. Top N populated countries in world
            System.out.println("\n4. Top 5 populated countries in world:");
            a.printCountries(a.topPopulatedCountriesInWorld(5));

            // 5. Top N populated countries in continent
            System.out.println("\n5. Top 5 populated countries in continent:");
            a.printCountries(a.topPopulatedCountriesInContinent(5, "Europe"));

            // 7. All cities in world by population
            System.out.println("\n7. Cities in world by population:");
            a.printCities(a.getCities());

            // 8. All cities in continent by population
            System.out.println("\n8. Cities in continent by population:");
            a.printCities(a.getCitiesByContinent(Country.Continent.EUROPE));

            // 9. All cities in region by population
            System.out.println("\n9. Cities in region by population:");
            a.printCities(a.getCitiesByRegion("British Islands"));

            // 12. Top N populated cities in world
            System.out.println("\n12. Top 5 populated cities in world:");
            a.printCities(a.getTopNCities(5));

            // 13. Top N populated cities in continent
            System.out.println("\n13. Top 5 populated cities in continent:");
            a.printCities(a.getTopNCitiesByContinent(5, Country.Continent.EUROPE));

            // 14. Top N populated cities in region
            System.out.println("\n14. Top 5 populated cities in region:");
            a.printCities(a.getTopNCitiesByRegion(5, "British Islands"));

            // 15. Top N populated cities in country
            System.out.println("\n15. Top 5 populated cities in country:");
            a.printCities(a.topPopulatedCitiesInCountry(5, "United Kingdom"));

            // 16. Top N populated cities in district
            System.out.println("\n16. Top 5 populated cities in district:");
            a.printCities(a.topPopulatedCitiesInDistrict(5, "Scotland"));

            // 18. Capital cities in continent by population
            System.out.println("\n18. Capital cities in continent by population:");
            a.printCities(a.getCapitalCitiesByContinent(Country.Continent.EUROPE));

            // 19. Capital cities in region by population
            System.out.println("\n19. Capital cities in region by population:");
            a.printCities(a.getCapitalCitiesByRegion("British Islands"));

            // 20. Top N populated capital cities in world
            System.out.println("\n20. Top 5 populated capital cities in world:");
            a.printCities(a.topPopulatedCapitalsInWorld(5));

            // 21. Top N populated capital cities in continent
            System.out.println("\n21. Top 5 populated capital cities in continent:");
            a.printCities(a.topPopulatedCapitalsInContinent(5, "Europe"));

            // 22. Top N populated capital cities in region
            System.out.println("\n22. Top 5 populated capital cities in region:");
            a.printCities(a.topPopulatedCapitalsInRegion(5, "British Islands"));

            // 23. Population statistics by continent
            System.out.println("\n23. Population statistics by continent:");
            ArrayList<Object[]> continentStats = a.getContinentPopulationReport();
            for (Object[] stat : continentStats) {
                System.out.printf("Continent: %s, Total: %d, In Cities: %d (%s), Rural: %d (%s)\n",
                        stat[0], stat[1], stat[2], stat[4], stat[3], stat[5]);
            }

            // 24. Population statistics by region
            System.out.println("\n24. Population statistics by region:");
            ArrayList<Object[]> regionStats = a.getRegionPopulationReport();
            for (Object[] stat : regionStats) {
                System.out.printf("Region: %s, Total: %d, In Cities: %d (%s), Rural: %d (%s)\n",
                        stat[0], stat[1], stat[2], stat[4], stat[3], stat[5]);
            }

            // 25. Population statistics by country
            System.out.println("\n25. Population statistics by country:");
            ArrayList<Object[]> countryStats = a.getCountryPopulationReport();
            for (Object[] stat : countryStats) {
                System.out.printf("Country: %s, Total: %d, In Cities: %d (%s), Rural: %d (%s)\n",
                        stat[0], stat[1], stat[2], stat[4], stat[3], stat[5]);
            }

            // 26-31. Population access reports
            System.out.println("\n26. World population: " + a.getPopulation());
            System.out.println("27. Continent population (Europe): " + a.getPopulation(Country.Continent.EUROPE));
            System.out.println("28. Region population (British Islands): " + a.getPopulationByRegion("British Islands"));
            Country uk = a.getCountries().stream()
                    .filter(c -> c.getCode().equals("GBR"))
                    .findFirst()
                    .orElse(null);
            System.out.println("29. Country population (UK): " + (uk != null ? a.getPopulation(uk) : "Not found"));
            System.out.println("30. District population (Scotland): " + a.getPopulationByDistrict("Scotland"));
            City london = a.getCities().stream()
                    .filter(c -> c.getName().equals("London"))
                    .findFirst()
                    .orElse(null);
            System.out.println("31. City population (London): " + (london != null ? a.getPopulation(london) : "Not found"));

            // 32. Language speakers report
            System.out.println("\n32. Language speakers report:");
            ArrayList<Object[]> languageStats = a.getLanguageSpeakersReport();
            for (Object[] stat : languageStats) {
                System.out.printf("Language: %s, Speakers: %d (%s of world population)\n",
                        stat[0], stat[1], stat[2]);
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
     *
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

        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
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
            } catch (SQLException sqle) {
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
    public void disconnect() {
        if (con != null) {
            try {
                // Close connection
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Executes SQL statement that selects countries and returns results
     *
     * @param statement SQL statement to run
     * @return List of countries
     */
    private ArrayList<Country> executeGetCountriesStatement(String statement) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(statement);
            // Extract employee information
            ArrayList<Country> countries = new ArrayList<>();
            while (rs.next()) {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get country details");
            return null;
        }
    }

    /**
     * Get all countries in the world
     *
     * @return list of countries
     */
    public ArrayList<Country> getCountries() {
        // Create string for SQL statement
        String select =
                "SELECT * "
                        + "FROM country "
                        + "ORDER BY population DESC";
        return executeGetCountriesStatement(select);
    }

    /**
     * Get all countries in the world in a continent
     *
     * @param continentInput continent in the world
     * @return list of countries
     */
    public ArrayList<Country> getCountries(Country.Continent continentInput) {
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
     *
     * @param regionInput region in the world
     * @return list of countries
     */
    public ArrayList<Country> getCountries(String regionInput) {
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
     *
     * @param statement SQL statement to run
     * @return List of cities
     */
    private ArrayList<City> executeGetCitiesStatement(String statement) {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get country details");
            return null;
        }
    }

    /**
     * Get all cities in the world
     *
     * @return ArrayList of City objects
     */
    public ArrayList<City> getCities() {
        // Create string for SQL statement
        String select = "SELECT * FROM city ORDER BY population DESC";
        return executeGetCitiesStatement(select);
    }

    /**
     * Executes SQL statement that gets population details and returns results
     *
     * @param statement SQL statement to run
     * @return Population
     */
    private long executeGetPopulationStatement(String statement) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(statement);
            if (rs.next()) {
                return rs.getLong("Population");
            }
            return -1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get population details");
            return -1;
        }
    }

    /**
     * Get the population of the world
     *
     * @return the population
     */
    public long getPopulation() {
        // Create string for SQL statement
        String select =
                "SELECT SUM(Population) AS Population "
                        + "FROM country "
                        + "ORDER BY Population DESC";
        return executeGetPopulationStatement(select);
    }

    /**
     * Get the population of a country
     *
     * @return the population
     */
    public long getPopulation(Country country) {
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
     *
     * @return the population
     */
    public long getPopulation(City city) {
        // Create string for SQL statement
        String select =
                "SELECT Population "
                        + "FROM city "
                        + "WHERE ID = '" + city.getID() + "' "
                        + "ORDER BY Population DESC";
        return executeGetPopulationStatement(select);
    }

    /**
     * Get the population in a continent
     * @return the population
     */
    public long getPopulation(Country.Continent continent)
    {
        // Create string for SQL statement
        String select =
                "SELECT SUM(Population) AS Population "
                        + "FROM country "
                        + "WHERE Continent = '" + continent + "' "
                        + "ORDER BY Population DESC";
        return executeGetPopulationStatement(select);
    }

    /**
     * Get the population in a region
     * @return the population
     */
    public long getPopulationByRegion(String region)
    {
        // Create string for SQL statement
        String select =
                "SELECT SUM(Population) AS Population "
                        + "FROM country "
                        + "WHERE Region = '" + region + "' "
                        + "ORDER BY Population DESC";
        return executeGetPopulationStatement(select);
    }

    /**
     * Get the population in a district
     * @return the population
     */
    public long getPopulationByDistrict(String district)
    {
        // Create string for SQL statement
        String select =
                "SELECT SUM(Population) AS Population "
                        + "FROM city "
                        + "WHERE District = '" + district + "' "
                        + "ORDER BY Population DESC";
        return executeGetPopulationStatement(select);
    }

    /**
     * Print a report on countries
     *
     * @param countries the countries to report on
     */
    public void printCountries(ArrayList<Country> countries) {
        // Print header
        System.out.printf("%-4s %-52s %-15s %-26s %-15s %-10s%n", "Code", "Name", "Continent", "Region", "Population",
                "Capital");
        // Loop over all employees in the list
        for (Country country : countries) {
            String countryData = String.format("%-4s %-52s %-15s %-26s %-15s %-10s",
                    country.getCode(), country.getName(), country.getContinent(), country.getRegion(),
                    country.getPopulation(), country.getCapital());
            System.out.println(countryData);
        }
    }

    /**
     * Print a report on cities
     *
     * @param cities the cities to report on
     */
    public void printCities(ArrayList<City> cities) {
        // Print header
        System.out.printf("%-4s %-35s %-6s %-20s %-15s%n", "ID", "Name", "Code", "District", "Population");
        // Loop over all employees in the list
        for (City City : cities) {
            String CityData = String.format("%-4s %-35s %-6s %-20s %-15s",
                    City.getID(), City.getName(), City.getCode(), City.getDistrict(),
                    City.getPopulation());
            System.out.println(CityData);
        }
    }

    /**
     * Get cities in a specific continent, sorted by population in descending order.
     *
     * @param continentInput The continent to filter by.
     * @return A list of cities in the specified continent, sorted by population.
     */
    public ArrayList<City> getCitiesByContinent(Country.Continent continentInput) {
        ArrayList<City> cities = new ArrayList<>();
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String select = "SELECT city.ID, city.Name, city.CountryCode, city.District, city.Population " +
                    "FROM city " +
                    "JOIN country ON city.CountryCode = country.Code " +
                    "WHERE country.Continent = '" + continentInput.toString().replace("_", " ") + "' " +
                    "ORDER BY city.Population DESC";
            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);

            // Extract city information
            while (rs.next()) {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get cities by continent.");
        }
        return cities;
    }

    /**
     * Get cities in a specific region, sorted by population in descending order.
     *
     * @param regionInput The region to filter by.
     * @return A list of cities in the specified region, sorted by population.
     **/
    public ArrayList<City> getCitiesByRegion(String regionInput) {
        ArrayList<City> cities = new ArrayList<>();
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String select = "SELECT city.ID, city.Name, city.CountryCode, city.District, city.Population " +
                    "FROM city " +
                    "JOIN country ON city.CountryCode = country.Code " +
                    "WHERE country.Region = '" + regionInput + "' " +
                    "ORDER BY city.Population DESC";
            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);

            // Extract city information
            while (rs.next()) {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get cities by region.");
        }
        return cities;
    }

    /**
     * Get all capital cities in a specific continent, sorted by population in descending order.
     *
     * @param continentInput The continent to filter by.
     * @return A list of capital cities in the specified continent, sorted by population.
     **/
    public ArrayList<City> getCapitalCitiesByContinent(Country.Continent continentInput) {
        ArrayList<City> cities = new ArrayList<>();
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String select = "SELECT city.ID, city.Name, city.CountryCode, city.District, city.Population " +
                    "FROM city " +
                    "JOIN country ON city.ID = country.Capital " +
                    "WHERE country.Continent = '" + continentInput.toString().replace("_", " ") + "' " +
                    "ORDER BY city.Population DESC";
            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);

            // Extract city information
            while (rs.next()) {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get capital cities by continent.");
        }
        return cities;
    }

    /**
     * Get all capital cities in a specific region, sorted by population in descending order.
     *
     * @param regionInput The region to filter by.
     * @return A list of capital cities in the specified region, sorted by population.
     **/
    public ArrayList<City> getCapitalCitiesByRegion(String regionInput) {
        ArrayList<City> cities = new ArrayList<>();
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String select = "SELECT city.ID, city.Name, city.CountryCode, city.District, city.Population " +
                    "FROM city " +
                    "JOIN country ON city.ID = country.Capital " +
                    "WHERE country.Region = '" + regionInput + "' " +
                    "ORDER BY city.Population DESC";
            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);

            // Extract city information
            while (rs.next()) {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get capital cities by region.");
        }
        return cities;
    }

    //Report.No.20 - The top N populated capital cities in the world where N is provided by the user.
    public ArrayList<City> topPopulatedCapitalsInWorld(int limitInput) {
        try {
            //Create an SQL statement
            Statement TopCapCities_World = con.createStatement();

            //Create string for SQL statement
            String select =
                    "SELECT city.* "
                            + "FROM city "
                            + "JOIN country ON city.ID = country.Capital "
                            + "ORDER BY city.Population DESC "
                            + "LIMIT " + limitInput;

            //Execute SQL statement
            ResultSet rs = TopCapCities_World.executeQuery(select);

            //Extract information
            ArrayList<City> capCities = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                String countryCode = rs.getString("CountryCode");
                String district = rs.getString("District");
                int population = rs.getInt("Population");

                City city = new City(id, name, countryCode, district, population);
                capCities.add(city);
            }
            rs.close();
            TopCapCities_World.close();

            return capCities;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve capital city details");
            return null;
        }
    }

    //Report.No.21 - The top N populated capital cities in a continent where N and continent is provided by the user.
    public ArrayList<City> topPopulatedCapitalsInContinent(int limitInput, String continentInput) {
        try {
            //Create an SQL statement
            Statement TopCapCities_Continent = con.createStatement();

            //Create string for SQL statement
            String select =
                    "SELECT city.* "
                            + "FROM city "
                            + "JOIN country ON city.ID = country.Capital "
                            + "WHERE country.Continent = '" + continentInput + "' "
                            + "ORDER BY city.Population DESC "
                            + "LIMIT " + limitInput;

            //Execute SQL statement
            ResultSet rs = TopCapCities_Continent.executeQuery(select);

            //Extract information
            ArrayList<City> capCities = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                String countryCode = rs.getString("CountryCode");
                String district = rs.getString("District");
                int population = rs.getInt("Population");

                City city = new City(id, name, countryCode, district, population);
                capCities.add(city);
            }
            rs.close();
            TopCapCities_Continent.close();

            return capCities;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve capital city details");
            return null;
        }
    }

    //Report.No.22 - The top N populated capital cities in a region where N and region is provided by the user.
    public ArrayList<City> topPopulatedCapitalsInRegion(int limitInput, String regionInput) {
        try {
            //Create an SQL statement
            Statement TopCapCities_Region = con.createStatement();

            //Create string for SQL statement
            String select =
                    "SELECT city.* "
                            + "FROM city "
                            + "JOIN country ON city.ID = country.Capital "
                            + "WHERE country.Region = '" + regionInput + "' "
                            + "ORDER BY city.Population DESC "
                            + "LIMIT " + limitInput;

            //Execute SQL statement
            ResultSet rs = TopCapCities_Region.executeQuery(select);

            //Extract information
            ArrayList<City> capCities = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                String countryCode = rs.getString("CountryCode");
                String district = rs.getString("District");
                int population = rs.getInt("Population");

                City city = new City(id, name, countryCode, district, population);
                capCities.add(city);
            }
            rs.close();
            TopCapCities_Region.close();

            return capCities;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve capital city details");
            return null;
        }
    }

    //Report.No.4 - The top N populated countries in the world where N is provided by the user.
    public ArrayList<Country> topPopulatedCountriesInWorld(int limitInput) {
        try {
            // Create an SQL statement
            Statement TopCountries_World = con.createStatement();

            // Create string for SQL statement - select all columns
            String select =
                    "SELECT * "
                            + "FROM country "
                            + "ORDER BY Population DESC "
                            + "LIMIT " + limitInput;

            // Execute SQL statement
            ResultSet rs = TopCountries_World.executeQuery(select);

            // Extract information
            ArrayList<Country> countries = new ArrayList<>();
            while (rs.next()) {
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
            rs.close();
            TopCountries_World.close();

            return countries;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve country details");
            return null;
        }
    }

    //Report.No.5 - The top N populated countries in a continent where N is provided by the user.
    public ArrayList<Country> topPopulatedCountriesInContinent(int limitInput, String continentInput) {
        try {
            //Create an SQL statement
            Statement TopCountries_Continent = con.createStatement();

            //Create string for SQL statement
            String select =
                    "SELECT * "
                            + "FROM country "
                            + "WHERE continent = '" + continentInput + "' "
                            + "ORDER BY Population DESC "
                            + "LIMIT " + limitInput;

            //Execute SQL statement
            ResultSet rs = TopCountries_Continent.executeQuery(select);

            //Extract information
            ArrayList<Country> countries = new ArrayList<>();
            while (rs.next()) {
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
            rs.close();
            TopCountries_Continent.close();

            return countries;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve country details");
            return null;
        }
    }

    //Report.No.15 - The top N populated cities in a country where N is provided by the user.
    public ArrayList<City> topPopulatedCitiesInCountry(int limitInput, String countryInput) {
        try {
            //Create an SQL statement
            Statement TopCities_Country = con.createStatement();

            //Create string for SQL statement
            String select =
                    "SELECT * "
                            + "FROM city "
                            + "WHERE CountryCode = (SELECT Code FROM country WHERE Name = '" + countryInput + "') "
                            + "ORDER BY Population DESC "
                            + "LIMIT " + limitInput;

            //Execute SQL statement
            ResultSet rs = TopCities_Country.executeQuery(select);

            //Extract information
            ArrayList<City> cities = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                String countryCode = rs.getString("CountryCode");
                String district = rs.getString("District");
                int population = rs.getInt("Population");

                City city = new City(id, name, countryCode, district, population);
                cities.add(city);
            }
            rs.close();
            TopCities_Country.close();

            return cities;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve city details");
            return null;
        }
    }



    //Report.No.16 - The top N populated cities in a district where N is provided by the user.
    public ArrayList<City> topPopulatedCitiesInDistrict(int limitInput, String districtInput) {
        try {
            //Create an SQL statement
            Statement TopCities_District = con.createStatement();

            //Create string for SQL statement
            String select =
                    "SELECT * "
                            + "FROM city "
                            + "WHERE District = '" + districtInput + "' "
                            + "ORDER BY Population DESC "
                            + "LIMIT " + limitInput;

            //Execute SQL statement
            ResultSet rs = TopCities_District.executeQuery(select);

            //Extract information
            ArrayList<City> cities = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                String countryCode = rs.getString("CountryCode");
                String district = rs.getString("District");
                int population = rs.getInt("Population");

                City city = new City(id, name, countryCode, district, population);
                cities.add(city);
            }
            rs.close();
            TopCities_District.close();

            return cities;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve city details");
            return null;
        }
    }

    /**
     * Reports the total population, city population, and rural population for each continent,
     * including the percentage of people who live in cities versus rural areas
     * @return An array of objects containing [continent, total_pop, city_pop, non_city_pop, city_pop_percentage, non_city_pop_percentage]
     */
    public ArrayList<Object[]> getContinentPopulationReport() {
        ArrayList<Object[]> reports = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();

            // Modified SQL to properly sum populations
            String select =
                    "SELECT " +
                            "c.Continent, " +
                            "SUM(c.Population) as TotalPopulation, " +
                            "(SELECT SUM(city.Population) " +
                            "FROM city " +
                            "JOIN country ON city.CountryCode = country.Code " +
                            "WHERE country.Continent = c.Continent) as CityPopulation " +
                            "FROM country c " +
                            "GROUP BY c.Continent";

            ResultSet rs = stmt.executeQuery(select);

            while (rs.next()) {
                String continent = rs.getString("Continent");
                long totalPop = rs.getLong("TotalPopulation");
                long cityPop = rs.getLong("CityPopulation");
                long ruralPop = totalPop - cityPop;

                // Calculate percentages, handle division by zero
                double cityPercent = totalPop > 0 ? ((double)cityPop * 100.0) / totalPop : 0;
                double ruralPercent = totalPop > 0 ? ((double)ruralPop * 100.0) / totalPop : 0;

                reports.add(new Object[]{
                        continent,
                        totalPop,
                        cityPop,
                        ruralPop,
                        String.format("%.2f%%", cityPercent),
                        String.format("%.2f%%", ruralPercent)
                });
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get continent population statistics");
        }
        return reports;
    }

    /**
     * Reports the total population, city population, and rural population for each region,
     * including the percentage of people who live in cities versus rural areas
     */
    public ArrayList<Object[]> getRegionPopulationReport() {
        ArrayList<Object[]> reports = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();

            // Modified SQL to properly sum populations by region
            String select =
                    "SELECT " +
                            "c.Region, " +
                            "SUM(c.Population) as TotalPopulation, " +
                            "(SELECT SUM(city.Population) " +
                            "FROM city " +
                            "JOIN country ON city.CountryCode = country.Code " +
                            "WHERE country.Region = c.Region) as CityPopulation " +
                            "FROM country c " +
                            "GROUP BY c.Region";

            ResultSet rs = stmt.executeQuery(select);

            while (rs.next()) {
                String region = rs.getString("Region");
                long totalPop = rs.getLong("TotalPopulation");
                long cityPop = rs.getLong("CityPopulation");
                long ruralPop = totalPop - cityPop;

                // Calculate percentages, handle division by zero
                double cityPercent = totalPop > 0 ? ((double)cityPop * 100.0) / totalPop : 0;
                double ruralPercent = totalPop > 0 ? ((double)ruralPop * 100.0) / totalPop : 0;

                reports.add(new Object[]{
                        region,
                        totalPop,
                        cityPop,
                        ruralPop,
                        String.format("%.2f%%", cityPercent),
                        String.format("%.2f%%", ruralPercent)
                });
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get region population statistics");
        }
        return reports;
    }

    /**
     * Reports the total population, city population, and rural population for each country,
     * including the percentage of people who live in cities versus rural areas
     */
    public ArrayList<Object[]> getCountryPopulationReport() {
        ArrayList<Object[]> reports = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();

            // Modified SQL to properly sum populations by country
            String select =
                    "SELECT " +
                            "c.Name, " +
                            "c.Population as TotalPopulation, " +
                            "(SELECT SUM(city.Population) " +
                            "FROM city " +
                            "WHERE city.CountryCode = c.Code) as CityPopulation " +
                            "FROM country c " +
                            "ORDER BY c.Population DESC";

            ResultSet rs = stmt.executeQuery(select);

            while (rs.next()) {
                String country = rs.getString("Name");
                long totalPop = rs.getLong("TotalPopulation");
                long cityPop = rs.getLong("CityPopulation");
                long ruralPop = totalPop - cityPop;

                // Calculate percentages, handle division by zero
                double cityPercent = totalPop > 0 ? ((double)cityPop * 100.0) / totalPop : 0;
                double ruralPercent = totalPop > 0 ? ((double)ruralPop * 100.0) / totalPop : 0;

                reports.add(new Object[]{
                        country,
                        totalPop,
                        cityPop,
                        ruralPop,
                        String.format("%.2f%%", cityPercent),
                        String.format("%.2f%%", ruralPercent)
                });
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get country population statistics");
        }
        return reports;
    }

    /**
     * Reports the number and percentage of the world's population that speaks Chinese, Hindi, Spanish, or Arabic,
     * ordered from most speakers to fewest
     * @return An array of objects containing [language, speakers, percentage_of_world_pop]
     */
    public ArrayList<Object[]> getLanguageSpeakersReport() {
        ArrayList<Object[]> reports = new ArrayList<>();
        try {
            // Create SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement that calculates speakers for specified languages
            String select =
                    "WITH WorldPop AS (SELECT SUM(Population) as total FROM country) " +
                            "SELECT cl.Language, " +
                            "ROUND(SUM(c.Population * (cl.Percentage/100))) as Speakers, " +
                            "ROUND(SUM(c.Population * (cl.Percentage/100)) * 100.0 / (SELECT total FROM WorldPop), 2) as WorldPercentage " +
                            "FROM countrylanguage cl " +
                            "JOIN country c ON cl.CountryCode = c.Code " +
                            "WHERE cl.Language IN ('Chinese', 'Hindi', 'Spanish', 'Arabic') " +
                            "GROUP BY cl.Language " +
                            "ORDER BY Speakers DESC";

            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);

            // Process results
            while (rs.next()) {
                String language = rs.getString("Language");
                long speakers = rs.getLong("Speakers");
                double percentage = rs.getDouble("WorldPercentage");

                reports.add(new Object[]{
                        language,
                        speakers,
                        String.format("%.2f%%", percentage)
                });
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get language speaker statistics");
            return null;
        }
        return reports;
    }

    /**
     * Get the top N populated cities in the world
     * @param n The number of cities to return
     * @return ArrayList of the top N cities by population
     */
    public ArrayList<City> getTopNCities(int n) {
        try {
            // Create SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String select =
                    "SELECT * " +
                            "FROM city " +
                            "ORDER BY Population DESC " +
                            "LIMIT " + n;

            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);

            // Extract city information
            ArrayList<City> cities = new ArrayList<>();
            while (rs.next()) {
                City city = new City(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("CountryCode"),
                        rs.getString("District"),
                        rs.getInt("Population")
                );
                cities.add(city);
            }
            return cities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get top N cities");
            return null;
        }
    }

    /**
     * Get the top N populated cities in a continent
     * @param n The number of cities to return
     * @param continent The continent to filter by
     * @return ArrayList of the top N cities in the specified continent by population
     */
    public ArrayList<City> getTopNCitiesByContinent(int n, Country.Continent continent) {
        try {
            // Create SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String select =
                    "SELECT city.* " +
                            "FROM city " +
                            "JOIN country ON city.CountryCode = country.Code " +
                            "WHERE country.Continent = '" + continent.toString().replace("_", " ") + "' " +
                            "ORDER BY city.Population DESC " +
                            "LIMIT " + n;

            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);

            // Extract city information
            ArrayList<City> cities = new ArrayList<>();
            while (rs.next()) {
                City city = new City(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("CountryCode"),
                        rs.getString("District"),
                        rs.getInt("Population")
                );
                cities.add(city);
            }
            return cities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get top N cities in continent");
            return null;
        }
    }

    /**
     * Get the top N populated cities in a region
     * @param n The number of cities to return
     * @param region The region to filter by
     * @return ArrayList of the top N cities in the specified region by population
     */
    public ArrayList<City> getTopNCitiesByRegion(int n, String region) {
        try {
            // Create SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String select =
                    "SELECT city.* " +
                            "FROM city " +
                            "JOIN country ON city.CountryCode = country.Code " +
                            "WHERE country.Region = '" + region + "' " +
                            "ORDER BY city.Population DESC " +
                            "LIMIT " + n;

            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);

            // Extract city information
            ArrayList<City> cities = new ArrayList<>();
            while (rs.next()) {
                City city = new City(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("CountryCode"),
                        rs.getString("District"),
                        rs.getInt("Population")
                );
                cities.add(city);
            }
            return cities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get top N cities in region");
            return null;
        }
    }
}