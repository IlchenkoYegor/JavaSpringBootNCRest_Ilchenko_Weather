package ua.edu.sumdu.j2ee.ilchenkoYegor.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.j2ee.ilchenkoYegor.configuration.DocumentConfiguration;
import ua.edu.sumdu.j2ee.ilchenkoYegor.configuration.WeatherConfigurations;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.NamedWeatherList;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.NamedYearWeather;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Async("asyncExecutor")
public class WordService {
    Logger serviceLogger = LogManager.getLogger(WordService.class);

     DocumentConfiguration configurations;
     @Autowired
     public WordService(DocumentConfiguration documentConfiguration){
        configurations = documentConfiguration;
     }

     public CompletableFuture<XWPFDocument> getDocument(List<NamedWeatherList> list){
         serviceLogger.info("invocation of getDocument");
         XWPFDocument document = new XWPFDocument();
         configurations.createTablesWithData(document, list);
         serviceLogger.info("exiting from getDocument");
         return CompletableFuture.completedFuture(document);
     }
     public CompletableFuture<XWPFDocument> getHistoricalWeatherDocument(List<NamedYearWeather> list){
        serviceLogger.info("invocation of getHistoricalDocument");
         XWPFDocument document = new XWPFDocument();
        configurations.createTablesWithHistoricalData(document,list);
        serviceLogger.info("exiting of getDocument");
        return CompletableFuture.completedFuture(document);
     }
}
