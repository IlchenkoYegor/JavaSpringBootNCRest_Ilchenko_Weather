package ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo;

import org.springframework.lang.NonNull;

import java.util.List;

public class NamedWeatherList {
    @NonNull

    private List<Weather> weatherList;
    private final String NAME;
    public List<Weather> getWeatherList() {
        return weatherList;
    }

    public String getNAME() {
        return NAME;
    }


    public void setWeatherList(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }
    public NamedWeatherList(String NAME, List<Weather> weatherList) {
        this.NAME = NAME;
        this.weatherList = weatherList;
    }
}
