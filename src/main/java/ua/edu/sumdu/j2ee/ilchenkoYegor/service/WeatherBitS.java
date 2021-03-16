package ua.edu.sumdu.j2ee.ilchenkoYegor.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ua.edu.sumdu.j2ee.ilchenkoYegor.interfaces.Parser;
import ua.edu.sumdu.j2ee.ilchenkoYegor.interfaces.WeatherService;
import ua.edu.sumdu.j2ee.ilchenkoYegor.properties.RestProperties;
import ua.edu.sumdu.j2ee.ilchenkoYegor.configuration.WeatherStaticFunctions;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class WeatherBitS implements Parser, WeatherService {
    Logger serviceLogger = LogManager.getLogger(WeatherBitS.class);

    private final String NAME_OF_SERVISE = "Weather Bit";
    private RestTemplate restTemplate;
    private RestProperties restProperties;
    private final String LINK = "https://weatherbit-v1-mashape.p.rapidapi.com/forecast/3hourly";
    private final String APIKEY;
    private final String APIKEYNAME;
    private final String APIHOSTNAME;
    private final String WHERE;
    private final String AND;
    private final String APIHOST = "weatherbit-v1-mashape.p.rapidapi.com";
    WeatherBitS(RestTemplate restTemplate, RestProperties restProperties){
        this.restTemplate = restTemplate;
        this.restProperties = restProperties;
        APIKEY = restProperties.getApiKey();
        APIKEYNAME = restProperties.getApiKeyName();
        APIHOSTNAME = restProperties.getApiHostName();
        WHERE = restProperties.getWhere();
        AND = restProperties.getAnd();
    }

    @Override
    public List<Weather> toParse(WeatherRequestParams params) throws IOException, InterruptedException {
        HttpHeaders headers = new HttpHeaders();

        headers.add(APIKEYNAME, APIKEY);
        headers.add(APIHOSTNAME, APIHOST);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(LINK + WHERE +"lat="+params.getLat()+AND+"lon="+params.getLongt());
        HttpEntity<WeatherBitDTO> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                WeatherBitDTO.class);
        WeatherBitDTO dto = response.getBody();
        List<WeatherBitDTO.DataWithWeather> listOfData = Arrays.asList(dto.getData());
        Iterator<WeatherBitDTO.DataWithWeather> it = listOfData.iterator();
        double latitude = dto.getLat();
        double lonitude = dto.getLon();
        List<Weather> weatherTmp = new ArrayList<>();
        while(it.hasNext()){
            WeatherBitDTO.DataWithWeather current = it.next();
            LocalDateTime time = current.getTimestamp_utc();
            WeatherBitDTO.DataWithWeather.WeatherIn conditions = current.getWeather();
            double clouds = current.getClouds();
            double visibility = current.getVis();
            double temperatureMax = current.getTemp();
            double temperatureMin = current.getTemp();
            weatherTmp.add(new Weather(time,temperatureMax,temperatureMin,latitude,lonitude,clouds,visibility,conditions.getDescription()));
        }
        return weatherTmp;
    }

    @Async("asyncExecutor")
    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(NAME_OF_SERVISE);
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<YearWeather> getHistoricalWeather(YearWeatherRequestParams params) throws RuntimeException{
        return CompletableFuture.completedFuture(null);
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Weather>> getTodayWeather(WeatherRequestParams params)throws RuntimeException {
        try{
            List<Weather> weathers = toParse(params);
            return CompletableFuture.completedFuture(WeatherStaticFunctions.toDay(weathers,params));
        } catch (InterruptedException | IOException e) {
            serviceLogger.error(e.getMessage() + " was occured in method getTodayWeather");
            throw new RuntimeException();
        } catch(Exception e){
            serviceLogger.error(e.getMessage() + " was occured in method getTodayWeather");
        }
        return null;
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Weather>> getFutureWeather(WeatherRequestParams params) throws RuntimeException {
        try {
            List<Weather> weathers = toParse(params);
            return CompletableFuture.completedFuture(WeatherStaticFunctions.futureWeather(weathers,params));
        } catch (InterruptedException | IOException e) {
            serviceLogger.error(e.getMessage() + " was occured in method getFutureWeather");
            throw new RuntimeException();
        } catch(Exception e){
            serviceLogger.error(e.getMessage() + " was occured in method getFutureWeather");
        }
        return null;
    }
}
