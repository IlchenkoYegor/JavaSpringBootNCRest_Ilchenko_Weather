package ua.edu.sumdu.j2ee.ilchenkoYegor.interfaces;

import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.Weather;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.WeatherRequestParams;

import java.io.IOException;
import java.util.List;

public interface Parser {
    List<Weather> toParse(WeatherRequestParams params) throws IOException, InterruptedException;
}
