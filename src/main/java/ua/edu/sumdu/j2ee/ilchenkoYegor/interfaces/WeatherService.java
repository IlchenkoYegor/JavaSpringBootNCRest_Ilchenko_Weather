package ua.edu.sumdu.j2ee.ilchenkoYegor.interfaces;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.Weather;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.WeatherRequestParams;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.YearWeather;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.YearWeatherRequestParams;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public interface WeatherService {
    @Async("asyncExecutor")
    CompletableFuture<String> getName();
    @Async("asyncExecutor")
    CompletableFuture<YearWeather> getHistoricalWeather(YearWeatherRequestParams params) throws RuntimeException;
    @Async("asyncExecutor")
    CompletableFuture<List<Weather>> getTodayWeather(WeatherRequestParams params) throws RuntimeException;
    @Async("asyncExecutor")
    CompletableFuture<List<Weather>> getFutureWeather(WeatherRequestParams params) throws RuntimeException;
}
