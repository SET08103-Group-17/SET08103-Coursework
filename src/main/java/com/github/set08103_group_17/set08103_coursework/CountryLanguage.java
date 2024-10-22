package com.github.set08103_group_17.set08103_coursework;

/**
 * TODO: Add Comment here
 */
public class CountryLanguage {
    private String CountryCode;
    private String Language;
    private Boolean IsOfficial;
    private Double Decimal;

    /**
     * TODO: Add Comment here
     * @param CountryCode
     * @param Language
     * @param IsOfficial
     * @param Decimal
     */
    public CountryLanguage(String CountryCode, String Language, Boolean IsOfficial, Double Decimal) {
        this.CountryCode = CountryCode;
        this.Language = Language;
        this.IsOfficial = IsOfficial;
        this.Decimal = Decimal;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getCountryCode()
    {
        return CountryCode;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public String getLanguage()
    {
        return Language;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public Boolean getIsOfficial()
    {
        return IsOfficial;
    }

    /**
     * TODO: Add Comment here
     * @return
     */
    public Double getDecimal()
    {
        return Decimal;
    }
}
