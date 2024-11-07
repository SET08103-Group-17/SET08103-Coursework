package com.github.set08103_group_17.set08103_coursework;

/**
 * Class representing a country
 */
public class Country
{
    private String code;
    private String name;
    public enum Continent
    {
        ASIA,
        EUROPE,
        NORTH_AMERICA,
        AFRICA,
        OCEANIA,
        ANTARCTICA,
        SOUTH_AMERICA
    }
    private Continent continent;
    private String region;
    private double surfaceArea;
    private int independenceYear;
    private int population;
    private double lifeExpectancy;
    private double GNP;
    private double GNPOld;
    private String localName;
    private String governmentForm;
    private String headOfState;
    private int capital;
    private String code2;

    /**
     * Constructer of a country
     * @param code
     * @param name
     * @param continent
     * @param region
     * @param surfaceArea
     * @param independenceYear
     * @param population
     * @param lifeExpectancy
     * @param GNP
     * @param GNPOld
     * @param localName
     * @param governmentForm
     * @param headOfState
     * @param capital
     * @param code2
     */
    public Country(String code, String name, Continent continent,
                   String region, double surfaceArea, int independenceYear,
                   int population, double lifeExpectancy, double GNP, double GNPOld,
                   String localName, String governmentForm, String headOfState,
                   int capital, String code2)
    {
        this.code = code;
        this.name = name;
        this.continent = continent;
        this.region = region;
        this.surfaceArea = surfaceArea;
        this.independenceYear = independenceYear;
        this.population = population;
        this.lifeExpectancy = lifeExpectancy;
        this.GNP = GNP;
        this.GNPOld = GNPOld;
        this.localName = localName;
        this.governmentForm = governmentForm;
        this.headOfState = headOfState;
        this.capital = capital;
        this.code2 = code2;
    }

    /**
     * Get the code of a country
     * @return the code of a country
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Get the name of a country
     * @return the name of a country
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the continent of a country
     * @return the continent of a country
     */
    public Continent getContinent()
    {
        return continent;
    }

    /**
     * Get the region of a country
     * @return the region of a country
     */
    public String getRegion()
    {
        return region;
    }

    /**
     * Get the surface area of a country
     * @return the surface area of a country
     */
    public double getSurfaceArea()
    {
        return surfaceArea;
    }

    /**
     * Get the independence year of a country
     * @return the independence of a country
     */
    public int getIndependenceYear()
    {
        return independenceYear;
    }

    /**
     * Get the population of a country
     * @return the population of a country
     */
    public int getPopulation()
    {
        return population;
    }

    /**
     * Get the life expectancy of a country
     * @return the life expectancy of a country
     */
    public double getLifeExpectancy()
    {
        return lifeExpectancy;
    }

    /**
     * Get the GNP of a country
     * @return the GNP of a country
     */
    public double getGNP()
    {
        return GNP;
    }

    /**
     * Get the GNPOld of a country
     * @return the GNPOld of a country
     */
    public double getGNPOld()
    {
        return GNPOld;
    }

    /**
     * Get the local name of a country
     * @return the local name of a country
     */
    public String getLocalName()
    {
        return localName;
    }

    /**
     * Get the goverment form of a country
     * @return the goverment form of a country
     */
    public String getGovernmentForm()
    {
        return governmentForm;
    }

    /**
     * Get the head of state of a country
     * @return the head of state of a country
     */
    public String getHeadOfState()
    {
        return headOfState;
    }

    /**
     * Get the capital of a country
     * @return the capital of a country
     */
    public int getCapital()
    {
        return capital;
    }

    /**
     * Get the second code of a country
     * @return the second code of a country
     */
    public String getCode2()
    {
        return code2;
    }
}