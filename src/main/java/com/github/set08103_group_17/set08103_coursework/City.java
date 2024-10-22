package com.github.set08103_group_17.set08103_coursework;

import javax.print.DocFlavor;
import javax.swing.*;
import javax.swing.event.PopupMenuListener;

public class City {
    private int ID;
    private String Name;
    private String Code;
    private String District;
    private int Population;

    /**
     * @param ID
     * @param Name
     * @param Code
     * @param District
     */
    public City(int ID, String Name, String Code, String District, int Population) {
        this.ID = ID;
        this.Name = Name;
        this.Code = Code;
        this.District = District;
        this.Population = Population;
    }

    /**
     * @return
     */
    public int getID()
    {
        return ID;
    }

    /**
     * @return
     */
    public String getName()
    {
        return Name;
    }

    /**
     * @return
     */
    public String getCode()
    {
        return Code;
    }

    /**
     * @return
     */
    public String getDistrict()
    {
        return District;
    }

    /**
     * @return
     */
    public int getPopulation()
    {
        return Population;
    }
}
