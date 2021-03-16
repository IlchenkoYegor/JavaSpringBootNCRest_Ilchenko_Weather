package ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo;

public class NamedYearWeather {
    YearWeather yearWeather;
    private final String NAME;

    public YearWeather getYearWeather() {
        return yearWeather;
    }

    public void setYearWeather(YearWeather yearWeather) {
        this.yearWeather = yearWeather;
    }

    public String getNAME() {
        return NAME;
    }

    public NamedYearWeather(YearWeather yearWeather, String name_of_service) {
        this.yearWeather = yearWeather;
        NAME = name_of_service;
    }

}
