package ua.edu.sumdu.j2ee.ilchenkoYegor.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class VisualCrossingWS implements Parser, WeatherService {
    private final String NAME_OF_SERVISE = "Visual Crossing";
    Logger serviceLogger = LogManager.getLogger(VisualCrossingWS.class);
    @Autowired
    public VisualCrossingWS(RestProperties restProperties) {
        this.restProperties = restProperties;
        PATTERN = "MM/dd/yyyy HH:mm:ss";
        PATTERN_YEAR = "MM/dd/yyyy";
        APIKEY = restProperties.getApiKey();
        APIKEYNAME = restProperties.getApiKeyName();
        APIHOSTNAME = restProperties.getApiHostName();
        WHERE = restProperties.getWhere();
        AND = restProperties.getAnd();
    }
    RestProperties restProperties;
    private final String PATTERN;
    private final String PATTERN_YEAR;
    private final String APIKEY;
    private final String APIKEYNAME;
    private final String APIHOSTNAME;
    private final String WHERE;
    private final String AND;
    private final String APIHOST = "visual-crossing-weather.p.rapidapi.com";
    private final String LINK_FORECAST = "https://visual-crossing-weather.p.rapidapi.com/forecast";
    private final String LINK_HIST = "https://visual-crossing-weather.p.rapidapi.com/history";
    private final String LATITUDE ="Latitude";
    private final String LONGITUDE = "Longitude";
    private final String CLOUD_COVER = "Cloud Cover";
    private final String TEMPERATURE = "Temperature";
    private final String CONDITIONS = "Conditions";
    private final String DATE = "Date time";
    private final String MAX_TEMP ="Maximum Temperature";
    private final String MIN_TEMP ="Minimum Temperature";
    private final String VISIBILITY ="Visibility";

    @Async("asyncExecutor")
    public List<Weather> toParseHistorical(YearWeatherRequestParams params)throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LINK_HIST+WHERE+"startDateTime="+params.getStartTime()+"T00%3A00%3A00"+ AND + "aggregateHours="+ params.getPeriodOfMisurement()+AND+
                        "location="+ params.getLongt()+"%2C"+params.getLat()+AND+"endDateTime="+params.getEndTime()+"T00%3A00%3A00" +
                        AND+"unitGroup=metric"+AND+"shortColumnNames=false"))
                .header(APIKEYNAME, APIKEY)
                .header(APIHOSTNAME, APIHOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        CSVParser parser = CSVParser.parse(response.body(), CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());
        List<Weather> weatherTMP = new ArrayList<>();
        Iterator<CSVRecord> it = parser.iterator();

        while (it.hasNext()) {
            try {
                CSVRecord record = it.next();
                double latitude = Double.parseDouble(record.get(LATITUDE));
                double longitude = Double.parseDouble(record.get(LONGITUDE));
                double maxTemp = Double.parseDouble(record.get(MAX_TEMP));
                double minTemp = Double.parseDouble(record.get(MIN_TEMP));
                double visibility = Double.parseDouble(record.get(VISIBILITY));
                double cloudCover = Double.parseDouble(record.get(CLOUD_COVER));
                String conditions = record.get(CONDITIONS);
                String date = record.get(DATE);
                LocalDateTime time = LocalDate.parse(date, DateTimeFormatter.ofPattern(PATTERN_YEAR)).atTime(0,0,0);
                Weather weather = new Weather(time, maxTemp, minTemp, latitude, longitude, cloudCover, visibility, conditions);
                weatherTMP.add(weather);
            }catch(NumberFormatException ex){
                serviceLogger.error(ex.getMessage() + " was occured in method toParse (using CSV records) some problems with data on API mainly the problem");
                continue;
            }
        }

        return weatherTMP;
    }
    @Override
    public List<Weather> toParse(WeatherRequestParams params) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LINK_FORECAST+WHERE+"location="+params.getLongt()+"%2C"+params.getLat()+ AND +"aggregateHours=1"+ AND+"contentType=csv"
                        +AND+"shortColumnNames=0"+AND+"&unitGroup=metric"))
                .header(APIKEYNAME,APIKEY)
                .header(APIHOSTNAME, APIHOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        CSVParser parser = CSVParser.parse(response.body(), CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());
        Iterator<CSVRecord> it = parser.iterator();
        List<Weather> weatherTMP = new ArrayList<>();
        while (it.hasNext()) {
            CSVRecord record = it.next();
            try {
                double latitude = Double.parseDouble(record.get(LATITUDE));
                double longitude = Double.parseDouble(record.get(LONGITUDE));
                double maxTemp = Double.parseDouble(record.get(TEMPERATURE));
                double minTemp = Double.parseDouble(record.get(TEMPERATURE));
                double cloudCover = Double.parseDouble(record.get(CLOUD_COVER));
                String conditions = record.get(CONDITIONS);
                String date = record.get(DATE);
                LocalDateTime time = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(PATTERN));
                weatherTMP.add(new Weather(time, maxTemp, minTemp, latitude, longitude, cloudCover, -500, conditions));
            }
            catch(NumberFormatException ex){
                serviceLogger.error(ex.getLocalizedMessage() + " was occured in method toParse (using CSV records) some problems with data on API mainly the problem");
                continue;
            }
        }
        return weatherTMP;
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<YearWeather> getHistoricalWeather(YearWeatherRequestParams params) throws RuntimeException{
        try {
            List<Weather> list = toParseHistorical(params);
            YearWeather statistic = new YearWeather(list.get(0).getTime().getYear(),list.get(0).getTime().getYear(), list.get(0).getMinTemperatureC(), list.get(0).getMaxTemperatureC());
            double minTemp = statistic.getMinT();
            double maxTemp = statistic.getMaxT();
            int yearWithMinTemp = statistic.getYearOfMinT();
            int yearWithMaxTemp = statistic.getYearOfMaxT();
            for(int i = 0; i<list.size(); i++){
                Weather currentWeather =  list.get(i);
                double curYMinTemp = currentWeather.getMinTemperatureC();
                double curYMaxTemp = currentWeather.getMaxTemperatureC();
                int currYear = currentWeather.getTime().getYear();
                if(minTemp>curYMinTemp){
                    minTemp = curYMinTemp;
                    yearWithMinTemp = currYear;
                }
                if(maxTemp<curYMaxTemp){
                    maxTemp = curYMaxTemp;
                    yearWithMaxTemp = currYear;
                }
            }
            statistic.setMaxT(maxTemp);
            statistic.setMinT(minTemp);
            statistic.setYearOfMaxT(yearWithMaxTemp);
            statistic.setYearOfMinT(yearWithMinTemp);
            statistic.setLatitude(list.get(0).getLatitude());
            statistic.setLongitude(list.get(0).getLongitude());
            return CompletableFuture.completedFuture(statistic);
        } catch (IOException | InterruptedException e) {
            serviceLogger.error(e.getMessage() + " was occured in method getHistoricalWeather");
            throw new RuntimeException();
        }
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Weather>> getTodayWeather(WeatherRequestParams params) throws RuntimeException{
        try {
            List<Weather> weathers = toParse(params);
            return CompletableFuture.completedFuture(WeatherStaticFunctions.futureWeather(weathers,params));
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
    public CompletableFuture<List<Weather>> getFutureWeather(WeatherRequestParams params)throws RuntimeException {
        try {
            List<Weather> weathers =  toParse(params);
            return CompletableFuture.completedFuture(WeatherStaticFunctions.toDay(weathers,params));
        } catch (InterruptedException | IOException e) {
            serviceLogger.error(e.getMessage() + " was occured in method getFutureWeather");
            throw new RuntimeException();
        } catch(Exception e){
            serviceLogger.error(e.getMessage() + " was occured in method getFutureWeather");
        }
        return null;
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(NAME_OF_SERVISE);
    }
}
