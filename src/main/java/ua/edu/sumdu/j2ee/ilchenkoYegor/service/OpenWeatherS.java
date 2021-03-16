package ua.edu.sumdu.j2ee.ilchenkoYegor.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ua.edu.sumdu.j2ee.ilchenkoYegor.interfaces.Parser;
import ua.edu.sumdu.j2ee.ilchenkoYegor.interfaces.WeatherService;
import ua.edu.sumdu.j2ee.ilchenkoYegor.properties.RestProperties;
import ua.edu.sumdu.j2ee.ilchenkoYegor.configuration.WeatherStaticFunctions;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.Weather;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.WeatherRequestParams;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.YearWeather;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.YearWeatherRequestParams;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@PropertySource("classpath:application.properties")
public class OpenWeatherS implements Parser, WeatherService {
    Logger serviceLogger = LogManager.getLogger(OpenWeatherS.class);
    private final String NAME_OF_SERVISE = "OpenWeather";

    RestTemplate weatherRest;
    RestProperties restProperties;
    @Autowired
    OpenWeatherS(RestTemplate weatherRest, RestProperties restProperties){
        this.weatherRest = weatherRest;
        this.restProperties = restProperties;
        APIKEY = restProperties.getApiKey();
        APIKEYNAME = restProperties.getApiKeyName();
        APIHOSTNAME = restProperties.getApiHostName();
        WHERE = restProperties.getWhere();
        AND = restProperties.getAnd();
    }
    private final String APIKEY;
    private final String APIKEYNAME;
    private final String APIHOSTNAME;
    private final String APIHOST = "community-open-weather-map.p.rapidapi.com";
    private final String WHERE;
    private final String AND;
    private final String LINK = "https://community-open-weather-map.p.rapidapi.com/forecast";
    private final String LOCATION = "location";
    private final String FORECAST = "forecast";
    private final String LONGITUDE = "longitude";
    private final String LATITUDE = "latitude";
    private final String ROOTTIME = "time";
    private final String FROM = "from";
    private final String TO = "to";
    private final String TEMPERATURE = "temperature";
    private final String TEMPERATUREMAX = "max";
    private final String TEMPERATUREMIN = "min";
    private final String VISIBILITY = "visibility";
    private final String VISIBILITYVAL = "value";
    private final String CLOUDCOVER = "clouds";
    private final String CLOUDCOVERVALUE = "all";
    private final String WEATHERCONDITIONS = "symbol";
    private final String WEATHERCONDITIONSDESC = "name";
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<YearWeather> getHistoricalWeather(YearWeatherRequestParams params) {
        return CompletableFuture.completedFuture(null);
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Weather>> getTodayWeather(WeatherRequestParams params) throws RuntimeException{
        try {
            List<Weather> weathers = toParse(params);
            return CompletableFuture.completedFuture(WeatherStaticFunctions.toDay(weathers,params));
        } catch (InterruptedException e) {
            serviceLogger.error(e.getMessage() + " was occured in method getTodayWeather");
            throw new RuntimeException();
        } catch (IOException e) {
            serviceLogger.error(e.getMessage() + " was occured in method getTodayWeather");
        }
        return null;
    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Weather>> getFutureWeather(WeatherRequestParams params) throws RuntimeException{
        try {
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
    public List<Weather> toParse(WeatherRequestParams params) throws IOException, InterruptedException
    {
        URL url  = new URL(LINK + WHERE+"units=metric"+ AND+"mode=xml"+AND+"lat="+params.getLat()+AND+"lon="+params.getLongt());
        HttpURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(APIKEYNAME, APIKEY);
        connection.setRequestProperty(APIHOSTNAME, APIHOST);
        List<Weather> weathersTMP = new ArrayList<>();
        try (DataInputStream ws = new DataInputStream(connection.getInputStream())){
            DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = xml.parse(ws);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            NodeList loc = root.getElementsByTagName(LOCATION);
            NodeList forecast = root.getElementsByTagName(FORECAST);
            double longt = 0;
            double lat = 0;
            for (int i = 0; i < loc.getLength(); i++) {
                Node currNode = loc.item(i);
                    if (currNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) currNode;
                        String longtitude = element.getAttribute(LONGITUDE);
                        String latitude = element.getAttribute(LATITUDE);
                        if(!longtitude.equals("") && !latitude.equals("")) {
                            longt= Double.parseDouble(longtitude);
                            lat = Double.parseDouble(latitude);
                        }
                    }
            }
            for (int i = 0; i < forecast.getLength(); i++) {
                Element currNode = (Element) forecast.item(i);
                if (currNode.getNodeType() == Node.ELEMENT_NODE) {
                   NodeList currNodeL = currNode.getElementsByTagName(ROOTTIME);
                    for (int j = 0; j < currNodeL.getLength(); j++) {
                        Node currTimeNode = currNodeL.item(j);
                        if(currTimeNode.getNodeType()==Node.ELEMENT_NODE) {
                            Element element = (Element) currTimeNode;
                            String from =  element.getAttribute(FROM);
                            String to =  element.getAttribute(TO);
                            double maxTempC = Double.parseDouble(((Element)element.getElementsByTagName(TEMPERATURE).item(0)).getAttribute(TEMPERATUREMAX));
                            double minTempC = Double.parseDouble(((Element)element.getElementsByTagName(TEMPERATURE).item(0)).getAttribute(TEMPERATUREMIN));
                            double visibility = Double.parseDouble(((Element)element.getElementsByTagName(VISIBILITY).item(0)).getAttribute(VISIBILITYVAL));
                            double cloudCover = Double.parseDouble(((Element)element.getElementsByTagName(CLOUDCOVER).item(0)).getAttribute(CLOUDCOVERVALUE));
                            String conditions = ((Element)element.getElementsByTagName(WEATHERCONDITIONS).item(0)).getAttribute(WEATHERCONDITIONSDESC);
                            weathersTMP.add(new Weather(LocalDateTime.parse(to),maxTempC,minTempC,lat,longt,cloudCover,visibility,conditions));
                        }
                    }
                }
            }
            connection.disconnect();
            return weathersTMP;
        }catch (ParserConfigurationException | SAXException ex){
            serviceLogger.error(ex.getMessage() + " was occured in method to Parse  (some with parsing by DOM)");
        }
        return weathersTMP;

    }
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(NAME_OF_SERVISE);
    }
}
