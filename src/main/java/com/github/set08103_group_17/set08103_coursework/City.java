package com.github.set08103_group_17.set08103_coursework;

/**
 * Class reprensenting a city
 */
public class City
{
    private int ID;
    private String Name;
    private String Code;
    private String District;
    private int Population;

    /**
     * Constructor for a city
     * @param ID the id of a city
     * @param Name the name of a city
     * @param Code the code of a city
     * @param District the district of a city
     * @param Population the population of a city
     */
    public City(int ID, String Name, String Code, String District,
                int Population)
    {
        this.ID = ID;
        this.Name = Name;
        this.Code = Code;
        this.District = District;
        this.Population = Population;
    }

    /**
     * Get the id of a city
     * @return the id of a city
     */
    public int getID()
    {
        return ID;
    }

    /**
     * Get the name of a city
     * @return the name of a city
     */
    public String getName()
    {
        return Name;
    }

    /**
     * Get the code of a city
     * @return the code of a city
     */
    public String getCode()
    {
        return Code;
    }

    /**
     * Get the district of a city
     * @return the district of a city
     */
    public String getDistrict()
    {
        return District;
    }

    /**
     * Get the population of a city
     * @return the population of a city
     */
    public int getPopulation()
    {
        return Population;
    }
}