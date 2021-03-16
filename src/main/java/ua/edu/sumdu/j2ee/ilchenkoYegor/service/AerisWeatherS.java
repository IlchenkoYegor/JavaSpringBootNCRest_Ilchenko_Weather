package ua.edu.sumdu.j2ee.ilchenkoYegor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@PropertySource("classpath:application.properties")
public class AerisWeatherS implements Parser, WeatherService{
    Logger serviceLogger = LogManager.getLogger(AerisWeatherS.class);
    private final String NAME_OF_SERVISE = "AerisWeather";
    @Autowired
    public AerisWeatherS(RestProperties restProperties) {
        this.restProperties = restProperties;
        PATTERN = restProperties.getPatternForDate();
        APIKEY = restProperties.getApiKey();
        APIKEYNAME = restProperties.getApiKeyName();
        APIHOSTNAME = restProperties.getApiHostName();
        WHERE = restProperties.getWhere();
        AND = restProperties.getAnd();
        serviceLogger.info("was created the bean of service "+ NAME_OF_SERVISE);
    }

    RestProperties restProperties;
    private final String PATTERN;
    private final String APIKEY;
    private final String APIKEYNAME;
    private final String APIHOSTNAME;
    private final String WHERE;
    private final String AND ;
    private final String FROM = "from=";
    private final String APIHOST = "aerisweather1.p.rapidapi.com";
    private final String TO= "to=";
    private final String LINK = "https://aerisweather1.p.rapidapi.com/forecasts/";
    @Override
    public List<Weather> toParse(WeatherRequestParams params) throws IOException, InterruptedException {
        String from = params.getFrom().format(DateTimeFormatter.ofPattern(PATTERN));
        String to = params.getTo().format(DateTimeFormatter.ofPattern(PATTERN));
        params.getTo().format(DateTimeFormatter.ofPattern(PATTERN));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LINK +params.getLat() +","+ params.getLongt()+WHERE+"filter=3hr" + AND + FROM +from.toString() + AND + TO + to.toString()))
                .header(APIKEYNAME, APIKEY)
                .header(APIHOSTNAME, APIHOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response1 = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper parser = new ObjectMapper();
        List<Weather> weathersTmp = new ArrayList<>();
        JsonNode mainNode =  parser.readTree(response1.body().toString());
        JsonNode periods = mainNode.get("response").get(0).get("periods");
        JsonNode location = mainNode.get("response").get(0).get("loc");
        double longitude = location.get("long").asDouble();
        double latitude = location.get("lat").asDouble();
        for (int i = 0; i < periods.size(); i++) {
            JsonNode periodsC = periods.get(i);
            LocalDateTime time = LocalDateTime.ofEpochSecond(periodsC.get("timestamp").asLong(), 0, ZoneOffset.UTC);
            double maxTempC = periodsC.get("maxTempC").asDouble();
            double minTempC = periodsC.get("minTempC").asDouble();
            double visibility = periodsC.get("visibilityKM").asDouble();
            double cloudCover = 100 - periodsC.get("sky").asDouble();
            String conditions = periodsC.get("weather").asText();

            weathersTmp.add(new Weather(time, maxTempC, minTempC,  latitude, longitude, cloudCover, visibility , conditions));
        }
        return weathersTmp;
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(NAME_OF_SERVISE);
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<YearWeather> getHistoricalWeather(YearWeatherRequestParams params)throws RuntimeException {
        return CompletableFuture.completedFuture(null);
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Weather>> getTodayWeather(WeatherRequestParams params)throws RuntimeException {

        try {
            List<Weather> weathers = toParse(params);
            return CompletableFuture.completedFuture(WeatherStaticFunctions.toDay(weathers, params));
        }
        catch (IOException | InterruptedException e) {
            serviceLogger.error(e.getMessage()+ "was occured in method getTodayWeather (in most of cases error with formatting because of bad Request)");
            throw new RuntimeException(e);
        }catch(Exception e){
            serviceLogger.error(e.getMessage() + "was occured in method getTodayWeather ");
        }
        return null;
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Weather>> getFutureWeather(WeatherRequestParams params) throws RuntimeException {
        try {
            List<Weather> weathers = toParse(params);
            return CompletableFuture.completedFuture(WeatherStaticFunctions.futureWeather(weathers,params));
        }catch(IOException  | InterruptedException e){
            serviceLogger.error(e.getMessage()+ "was occured in method getTodayWeather (in most of cases error with formatting because of bad Request)");
            throw new RuntimeException("exception while parsing the data from rest");
        }catch(Exception e){
            serviceLogger.error(e.getMessage() + "was occured in method getTodayWeather ");
        }
        return null;
    }



}
