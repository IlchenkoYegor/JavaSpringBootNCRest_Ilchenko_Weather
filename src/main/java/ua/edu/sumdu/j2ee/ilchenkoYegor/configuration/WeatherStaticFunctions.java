package ua.edu.sumdu.j2ee.ilchenkoYegor.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.Weather;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.WeatherRequestParams;

import java.util.ArrayList;
import java.util.List;

public class WeatherStaticFunctions {
    static Logger log = LogManager.getLogger(WeatherConfigurations.class);
    public static List<Weather> toDay(List<Weather> weathers, WeatherRequestParams params){
        log.info("entered into static function \"today\" in "+log.getName());
        List<Weather> weathersToday = new ArrayList<>();
        for(Weather e: weathers){
            if(e.getTime().toLocalDate().isBefore(params.getTo()) && !e.getTime().toLocalDate().isBefore(params.getFrom()))
                weathersToday.add(e);
        }
        log.info("exited from static function \"today\" in "+log.getName());
        return weathersToday;
    }
    public static List<Weather> futureWeather(List<Weather> weathers, WeatherRequestParams params){
        log.info("entered into static function \"futureWeather\" in "+log.getName());
        List<Weather> weathersFuture = new ArrayList<>();
        for(Weather e: weathers){

            if(e.getTime().toLocalDate().isBefore(params.getFrom().plusDays(1)) && !e.getTime().toLocalDate().isBefore(params.getFrom()))
                weathersFuture.add(e);
        }
        log.info("exited from static function \"futureWeather\" in "+log.getName());
        return weathersFuture;
    }

}
