package com.github.set08103_group_17.set08103_coursework;

/**
 * TODO: Add Comment here
 */
public class City {
    private int ID;
    private String Name;
    private String Code;
    private String District;
    private int Population;

    /**
     * TODO: Add Comment here
     * @param ID
     * @param Name
     * @param Code
     * @param District
     * @param Population
     */
    public City(int ID, String Name, String Code, String District, int Population) {
        this.ID = ID;
        this.Name = Name;
        this.Code = Code;
        this.District = District;
        this.Population = Population;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public int getID()
    {
        return ID;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getName()
    {
        return Name;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getCode()
    {
        return Code;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getDistrict()
    {
        return District;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public int getPopulation()
    {
        return Population;
    }
}
