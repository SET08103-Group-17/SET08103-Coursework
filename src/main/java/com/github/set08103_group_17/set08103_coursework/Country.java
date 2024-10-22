package com.github.set08103_group_17.set08103_coursework;

/**
 * TODO: Add Comment here
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
     * TODO: Add Comment here
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
     * TODO: Add Comment here
     * @return
     */
    public String getCode()
    {
        return code;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public Continent getContinent()
    {
        return continent;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getRegion()
    {
        return region;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public double getSurfaceArea()
    {
        return surfaceArea;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public int getIndependenceYear()
    {
        return independenceYear;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public int getPopulation()
    {
        return population;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public double getLifeExpectancy()
    {
        return lifeExpectancy;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public double getGNP()
    {
        return GNP;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public double getGNPOld()
    {
        return GNPOld;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getLocalName()
    {
        return localName;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getGovernmentForm()
    {
        return governmentForm;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getHeadOfState()
    {
        return headOfState;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public int getCapital()
    {
        return capital;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getCode2()
    {
        return code2;
    }
}
