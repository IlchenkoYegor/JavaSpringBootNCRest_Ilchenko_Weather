package ua.edu.sumdu.j2ee.ilchenkoYegor.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.j2ee.ilchenkoYegor.interfaces.Parser;
import ua.edu.sumdu.j2ee.ilchenkoYegor.interfaces.WeatherService;
import ua.edu.sumdu.j2ee.ilchenkoYegor.properties.RestProperties;
import ua.edu.sumdu.j2ee.ilchenkoYegor.configuration.WeatherStaticFunctions;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.Weather;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.WeatherRequestParams;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.YearWeather;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.YearWeatherRequestParams;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ClimeaCellS implements Parser, WeatherService {
    Logger serviceLogger = LogManager.getLogger(ClimeaCellS.class);

    private final String NAME_OF_SERVISE = "ClimeaCell";
    private final String PATTERN;
    private final String APIKEY;
    private final String APIKEYNAME;
    private final String APIHOSTNAME;
    private final String WHERE;
    private final String AND ;
    private RestProperties restProperties;
    private final String LONGITUDE = "lon";
    private final String LATITUDE = "lat";
    private final String VISIBILITY = "visibility";
    private final String CLOUD_COVER = "cloud_cover";
    private final String WEATHER_CODE = "weather_code";
    private final String OBSERVATION_TIME = "observation_time";
    private final String TEMPERATURE = "temp";
    private final String VALUE_OF_PARAMETER = "value";
    private final String LINK = "https://climacell-microweather-v1.p.rapidapi.com/weather/forecast/hourly";
    private final String APIHOST = "climacell-microweather-v1.p.rapidapi.com";
    @Override
    public List<Weather> toParse(WeatherRequestParams params) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LINK + WHERE + "lat="+params.getLat()+ AND+ "lon="+params.getLongt()+AND+"end_time="+
                        params.getTo().atTime(0,0,0)+AND+"fields=temp%2Cvisibility%2Ccloud_cover%2Cweather_code"+AND+"unit_system=si"))
                .header(APIKEYNAME, APIKEY)
                .header(APIHOSTNAME, APIHOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        List<Weather> weathersTmp = new ArrayList<>();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray array = new JSONArray(response.body());
        Iterator it =  array.iterator();
        while(it.hasNext()){
            JSONObject currObject =  (JSONObject)it.next();
            double  longitude = currObject.getDouble(LONGITUDE);
            double  latitude = currObject.getDouble(LATITUDE);
            double temperature = currObject.getJSONObject(TEMPERATURE).getDouble(VALUE_OF_PARAMETER);
            double visibility = currObject.getJSONObject(VISIBILITY).getDouble(VALUE_OF_PARAMETER);
            double cloudCover = currObject.getJSONObject(CLOUD_COVER).getDouble(VALUE_OF_PARAMETER);
            String weatherCode = currObject.getJSONObject(WEATHER_CODE).getString(VALUE_OF_PARAMETER);
            String time = currObject.getJSONObject(OBSERVATION_TIME).getString(VALUE_OF_PARAMETER);
            LocalDateTime time1 =  LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
            weathersTmp.add(new Weather(time1, temperature, temperature, latitude, longitude, cloudCover, visibility, weatherCode));
        }
        return weathersTmp;
    }

    public ClimeaCellS(RestProperties restProperties) {
        this.restProperties = restProperties;
        PATTERN = restProperties.getPatternForDate();
        APIKEY = restProperties.getApiKey();
        APIKEYNAME = restProperties.getApiKeyName();
        APIHOSTNAME = restProperties.getApiHostName();
        WHERE = restProperties.getWhere();
        AND = restProperties.getAnd();
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<YearWeather> getHistoricalWeather(YearWeatherRequestParams params) throws RuntimeException{
        return CompletableFuture.completedFuture(null);
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Weather>> getTodayWeather(WeatherRequestParams params) throws RuntimeException{
        try{
            List<Weather> weathers;
            weathers = toParse(params);
            return CompletableFuture.completedFuture(WeatherStaticFunctions.toDay(weathers,params));
        } catch (InterruptedException | IOException e) {
            serviceLogger.error(e.getMessage() + " was occured in method getTodayWeather");
            throw new RuntimeException();//log
        } catch(Exception e){
            serviceLogger.error(e.getMessage() + " was occured in method getTodayWeather");
        }
        return null;
    }

    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Weather>> getFutureWeather(WeatherRequestParams params)throws RuntimeException {
        try{
            List<Weather> weathers;
            weathers = toParse(params);
            return CompletableFuture.completedFuture(WeatherStaticFunctions.futureWeather(weathers,params));
        } catch (InterruptedException | IOException e) {
            serviceLogger.error(e.getMessage() + " was occured in method getFutureWeather");
            throw new RuntimeException();
        } catch(Exception e){
            serviceLogger.error(e.getMessage() + " was occured in method getFutureWeather");
        }
        return null;
    }
    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(NAME_OF_SERVISE);
    }
}
