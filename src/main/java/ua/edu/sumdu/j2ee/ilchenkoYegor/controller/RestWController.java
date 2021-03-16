package ua.edu.sumdu.j2ee.ilchenkoYegor.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.edu.sumdu.j2ee.ilchenkoYegor.interfaces.WeatherService;
import ua.edu.sumdu.j2ee.ilchenkoYegor.properties.RestProperties;
import ua.edu.sumdu.j2ee.ilchenkoYegor.service.*;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.*;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/weather")
public class RestWController {
    Logger controllerLoger = LogManager.getLogger(RestWController.class);
    RestProperties restProperties;
    WordService wordService;
    private final String LONGTITUDE = "longitude";
    private final String LATITUDE = "latitude";
    private final String DEFAULT_FORMAT = "JSON";
    private final String PATTERN;
    private final int TIMEOUT;
    @Autowired
    RestWController(AerisWeatherS aerisWeatherS, OpenWeatherS openWeatherS, ClimeaCellS climeaCellS, VisualCrossingWS visualCrossingWS,
                    WeatherBitS weatherBitS, RestProperties restProperties, WordService wordService){
        weatherService = new ArrayList<>();
        weatherService.add(aerisWeatherS);
        weatherService.add(openWeatherS);
        weatherService.add(climeaCellS);
        weatherService.add(visualCrossingWS);
        weatherService.add(weatherBitS);
        this.wordService = wordService;
        this.restProperties = restProperties;
        PATTERN = restProperties.getPatternForDate();
        TIMEOUT = restProperties.getTimeoutOfResponse();
    }
    List<WeatherService> weatherService;
    @RequestMapping(value = "/todayWeather")
    public ResponseEntity<?> todayWeather(@RequestParam(defaultValue = "${weather.standart-latitude}") double lat,
                                                      @RequestParam(defaultValue = "${weather.standart-longitude}") double longt,
                                                      @RequestParam(defaultValue = DEFAULT_FORMAT) String format) {
        try {
            format = format.toUpperCase();
            boolean isXML = format.equals("XML");
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            List<NamedWeatherList> namedWeatherLists = new ArrayList<>();
            List<CompletableFuture<List<Weather>>> completableFutureList = new ArrayList<>();
            try {
                for (int i = 0; i < weatherService.size(); i++) {
                    completableFutureList.add(weatherService.get(i).
                            getTodayWeather(new WeatherRequestParams(longt, lat, today, tomorrow, isXML)).orTimeout(TIMEOUT, TimeUnit.MILLISECONDS));
                }
                for (int i = 0; i < weatherService.size(); i++) {
                    namedWeatherLists.add(new NamedWeatherList(weatherService.get(i).getName().join(), completableFutureList.get(i).join()));
                }

            } catch ( HttpMessageNotWritableException ex ) {
                controllerLoger.error(ex.getMessage() + " were occured (wrong parameters) " + ex.getLocalizedMessage() + " method weatherService");
                return ResponseEntity.badRequest().body("were entered the wrong parameters!" + ex);

            } catch ( CompletionException ex ) {
                controllerLoger.error(ex.getMessage() + " were occured (timeout exceeded) " + ex.getLocalizedMessage() + " method weatherService");
                return ResponseEntity.accepted().body("The remoted API doesn`t response: timeout exceeded ");
            }
            if (isXML) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(namedWeatherLists);
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(namedWeatherLists);

        }catch ( UnsupportedOperationException | DateTimeParseException e ) {
            controllerLoger.error(e.getMessage() + " was occured, probably because of the wrong parameters");
            return ResponseEntity.badRequest().body("the parameters must be: longt(-180;180), lat(-90;90)");
        }
    }

    @RequestMapping(value = "/futureWeather")
    public ResponseEntity<?> futureWeather(@RequestParam(defaultValue = "${weather.standart-latitude}") double lat,
                                           @RequestParam(defaultValue = "${weather.standart-longitude}") double longt,
                                           @RequestParam(defaultValue = DEFAULT_FORMAT)String format, @RequestParam(required = true) String date) {
        try {
            LocalDate dateS = LocalDate.parse(date, DateTimeFormatter.ofPattern(PATTERN));
            if (!(Math.abs(lat) < 90) || !(Math.abs(longt) < 180) || dateS.isBefore(LocalDate.now()) || dateS.isAfter(LocalDate.now().plusDays(27))) {
                throw new UnsupportedOperationException();
            }
            format = format.toUpperCase();
            boolean isXML = format.equals("XML");
            boolean isWord = format.equals("DOCX");

            List<NamedWeatherList> namedWeatherLists = new ArrayList<>();
            LocalDate inFuture = dateS;
            LocalDate inFutureS = dateS.plusDays(1);
            List<CompletableFuture<List<Weather>>> completableFutureList = new ArrayList<>();
            try {
                for (int i = 0; i < weatherService.size(); i++) {
                    completableFutureList.add(weatherService.get(i).
                            getFutureWeather(new WeatherRequestParams(longt, lat, inFuture, inFutureS, isXML)).orTimeout(TIMEOUT, TimeUnit.MILLISECONDS));
                }
                for (int i = 0; i < weatherService.size(); i++) {
                    namedWeatherLists.add(new NamedWeatherList(weatherService.get(i).getName().join(), completableFutureList.get(i).join()));
                }
            } catch ( HttpMessageNotWritableException ex ) {
                controllerLoger.error(ex.getMessage() + " were occured (wrong parameters) " + ex.getLocalizedMessage() + " method weatherService");
                return ResponseEntity.badRequest().body("were entered the wrong parameters!" + ex);

            } catch ( CompletionException ex ) {
                controllerLoger.error(ex.getMessage() + " were occured (timeout exceeded)" + ex.getLocalizedMessage() + " method weatherService");
                return ResponseEntity.accepted().body("The remoted API doesn`t response: timeout exceeded ");
            }
            if (isWord) {
                XWPFDocument doc = wordService.getDocument(namedWeatherLists).join();
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    doc.write(out);
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Disposition", "attachment;filename=weather forecast for " + date + ".docx");
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).headers(headers).body(out.toByteArray());
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            } else if (isXML) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(namedWeatherLists);
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(namedWeatherLists);
        } catch ( UnsupportedOperationException | DateTimeParseException e ) {
            controllerLoger.error(e.getMessage() + " was occured, probably because of the wrong parameters");
            return ResponseEntity.badRequest().body("the parameters must be: longt(-180;180), lat(-90;90), date(yyyy-MM-dd) could start at least from today "+LocalDate.now()+ " and can`t be further than "+ LocalDate.now().plusDays(26));
        }
    }
    @RequestMapping(value ="/getHistoricalWeather")
    public ResponseEntity<?> getHistoricalWeather(@RequestParam(defaultValue = "${weather.standart-latitude}") double lat,
                                                       @RequestParam(defaultValue = "${weather.standart-longitude}") double longt,
                                                       @RequestParam(defaultValue = DEFAULT_FORMAT) String format, @RequestParam(required = true) String startDate,
                                                       @RequestParam(required = true) String endDate,
                                                  @RequestParam(defaultValue = ((String) "${weather.agregated-hours}"))String interval) {
        try {
            if (!(Math.abs(lat) < 90) || !(Math.abs(longt) < 180)) {
                throw new UnsupportedOperationException();
            }
            format = format.toUpperCase();
            boolean isXML = format.equals("XML");
            boolean isWord = format.equals("DOCX");
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(PATTERN));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(PATTERN));
            List<NamedYearWeather> yearWeathers = new ArrayList<>();

            try {
                for (int i = 0; i < weatherService.size(); i++) {
                    yearWeathers.add(new NamedYearWeather(weatherService.get(i).
                            getHistoricalWeather(new YearWeatherRequestParams(lat, longt, start, end, Integer.parseInt(interval))).
                            orTimeout(TIMEOUT, TimeUnit.MILLISECONDS).join(), weatherService.get(i).getName().join()));
                }
            } catch (CompletionException ex) {
                controllerLoger.error(ex.getMessage() + " were occured wrong parameters " + ex.getLocalizedMessage() + " method weatherService");
                return ResponseEntity.accepted().body("The remoted API doesn`t response: timeout exceeded (probably cant find the weather in this point or interval is badly entered(try to avoid using it))");
            } catch (HttpMessageNotWritableException ex) {
                controllerLoger.error(ex.getMessage() + " were occured (timeout exceeded) " + ex.getLocalizedMessage() + " method weatherService");
                return ResponseEntity.badRequest().body("were entered the wrong parameters!" + ex);
            }
            if (isWord) {
                XWPFDocument doc = wordService.getHistoricalWeatherDocument(yearWeathers)./*orTimeout(100, TimeUnit.MILLISECONDS)*/join();
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    doc.write(out);
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Disposition", "attachment;filename=weather historical data.docx");
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).headers(headers).body(out.toByteArray());
                } catch (IOException e) {
                    controllerLoger.error(e.getMessage() + " occured when trying to open the stream in getHistoricalWeather for docx" + e.getLocalizedMessage());
                    ResponseEntity.notFound().build();
                    e.printStackTrace();
                }
            } else if (isXML) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(yearWeathers);
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(yearWeathers);
        } catch (UnsupportedOperationException | DateTimeParseException e) {
            controllerLoger.error(e.getMessage() + " was occured, probably because of the wrong parameters");
            return ResponseEntity.badRequest().body("the parameters must be: longt(-180;180), lat(-90;90), startDate(yyyy-MM-dd), endDate(yyyy-MM-dd), interval is better not to use but 1 - is 1 hour" );
        }
    }
}
