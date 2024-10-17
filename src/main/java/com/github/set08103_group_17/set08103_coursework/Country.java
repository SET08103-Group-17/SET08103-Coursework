package com.github.set08103_group_17.set08103_coursework;

/**
 *
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
    public Country(String code, String name, Continent continent, String region, double surfaceArea, int independenceYear, int population, double lifeExpectancy, double GNP, double GNPOld, String localName, String governmentForm, String headOfState, int capital, String code2)
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
     * @return
     */
    public String getCode()
    {
        return code;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return
     */
    public Continent getContinent()
    {
        return continent;
    }

    /**
     * @return
     */
    public String getRegion()
    {
        return region;
    }

    /**
     * @return
     */
    public double getSurfaceArea()
    {
        return surfaceArea;
    }

    /**
     * @return
     */
    public int getIndependenceYear()
    {
        return independenceYear;
    }

    /**
     * @return
     */
    public int getPopulation()
    {
        return population;
    }

    /**
     * @return
     */
    public double getLifeExpectancy()
    {
        return lifeExpectancy;
    }

    /**
     * @return
     */
    public double getGNP()
    {
        return GNP;
    }

    /**
     * @return
     */
    public double getGNPOld()
    {
        return GNPOld;
    }

    /**
     * @return
     */
    public String getLocalName()
    {
        return localName;
    }

    /**
     * @return
     */
    public String getGovernmentForm()
    {
        return governmentForm;
    }

    /**
     * @return
     */
    public String getHeadOfState()
    {
        return headOfState;
    }

    /**
     * @return
     */
    public int getCapital()
    {
        return capital;
    }

    /**
     * @return
     */
    public String getCode2()
    {
        return code2;
    }
}
