package com.github.set08103_group_17.set08103_coursework;

import java.sql.*;
import java.util.ArrayList;

/**
 * generate instance of Class App
 */
public class App
{
    /**
     * List of parameters that should be added to when new args are made
     * @param args
     */
    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        ArrayList<City> cities = a.getCity();
        a.printCity(cities);

        // Disconnect from database
        a.disconnect();
    }

    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
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
        for (int i=0; i < retries; i++)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/world?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
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
     * @return
     */
    public ArrayList<Country> getCountries()
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String select =
                    "SELECT * "
                    + "FROM country "
                    + "ORDER BY population DESC";
            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);
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
     * @return ArrayList of City objects
     */
    public ArrayList<City> getCity() {
        ArrayList<City> cities = new ArrayList<>();  // Initialize the list here
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String select = "SELECT * FROM city ORDER BY population DESC";
            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(select);

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
     * do not delete others print statement simply comment out

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
}
*/


    public void printCity(ArrayList<City> cities)
    {
        // Print header
        System.out.printf("%-4s %-35s %-6s %-20s %-15s", "ID", "Name", "Code", "District", "Population \n");
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