package com.github.set08103_group_17.set08103_coursework;

/**
 * Class representing a language in a country
 */
public class CountryLanguage
{
    private String CountryCode;
    private String Language;
    private Boolean IsOfficial;
    private Double percentage;

    /**
     * Constructor for a country language
     * @param CountryCode the country code of a country language
     * @param Language the name of the language
     * @param IsOfficial if the language is official
     * @param percentage the percentage of a country language
     */
    public CountryLanguage(String CountryCode, String Language,
                           Boolean IsOfficial, Double percentage)
    {
        this.CountryCode = CountryCode;
        this.Language = Language;
        this.IsOfficial = IsOfficial;
        this.percentage = percentage;
    }

    /**
     * Get the country code of a country language
     * @return the country code of a country language
     */
    public String getCountryCode()
    {
        return CountryCode;
    }

    /**
     * Get the name if the language
     * @return the name of a language
     */
    public String getLanguage()
    {
        return Language;
    }

    /**
     * Get if a language of a country language is official
     * @return if a language of a country language is official
     */
    public Boolean getIsOfficial()
    {
        return IsOfficial;
    }

    /**
     * Get the percentage of a country language
     * @return the percentage of a country language
     */
    public Double getPercentage()
    {
        return percentage;
    }
}