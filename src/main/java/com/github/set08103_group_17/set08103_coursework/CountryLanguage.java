package com.github.set08103_group_17.set08103_coursework;

public class CountryLanguage {

    private String CountryCode;
    private String Language;
    private Boolean IsOfficial;
    private Double Decimal;

    /**
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
     * @return
     */
    public String getCountryCode()
    {
        return CountryCode;
    }

    /**
     * @return
     */
    public String getLanguage()
    {
        return Language;
    }

    /**
     * @return
     */
    public Boolean getIsOfficial()
    {
        return IsOfficial;
    }

    /**
     * @return
     */
    public Double getDecimal()
    {
        return Decimal;
    }
}

