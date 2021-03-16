package ua.edu.sumdu.j2ee.ilchenkoYegor.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.NamedWeatherList;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.NamedYearWeather;
import ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo.Weather;

import java.util.*;

@Component
public class DocumentConfiguration {
    Logger log = LogManager.getLogger(DocumentConfiguration.class);
    public void createTablesWithData(XWPFDocument document, List<NamedWeatherList> list){

        log.info("Were entered into createTablesWithData method of " +log.getName());
        for (int i = 0; i < list.size() ; i++) {
            XWPFParagraph paragraph= document.createParagraph();
            XWPFRun currentName = paragraph.createRun();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            currentName.setFontSize(20);
            currentName.setText(list.get(i).getNAME());
            if(list.get(i).getWeatherList() == null){
                XWPFParagraph errorParagraph = document.createParagraph();
                XWPFRun errorRun= errorParagraph.createRun();
                errorRun.setText("Can`t get such information from this API (the most popular problem is unavailable date to observe) but Restful service also can be unavailable");
                log.warn( list.get(i).getNAME() + "have null-arrays in (forecast) "+ log.getName());
            }
            else if(list.get(i).getWeatherList().size() == 0){
                XWPFParagraph errorParagraph = document.createParagraph();
                XWPFRun errorRun= errorParagraph.createRun();
                errorRun.setText("Unavailable date to observe for this API");
            }
            else {
                List<String> listWithNames = new ArrayList<>();
                listWithNames.add("Time");
                listWithNames.add("Longitude");
                listWithNames.add("Latitude");
                listWithNames.add("Max Temperature");
                listWithNames.add("Min Temperature");
                listWithNames.add("Visibility");
                listWithNames.add("Cloud Cover");
                listWithNames.add("Conditions");
                XWPFTable currentTable = document.createTable();
                Iterator<String> it = listWithNames.iterator();

                XWPFTableRow primalRow = currentTable.getRow(0);
                primalRow.getCell(0).setText(it.next());
                for (int j = 1; it.hasNext(); j++) {
                    primalRow.addNewTableCell();
                    primalRow.getCell(j).setText(it.next());
                }
                for (int j = 0; j < list.get(i).getWeatherList().size(); j++) {
                    XWPFTableRow currentRow = currentTable.createRow();
                    int r = 0;
                    currentRow.getCell(r++).setText(list.get(i).getWeatherList().get(j).getTime().toString());
                    currentRow.getCell(r++).setText(Double.toString(list.get(i).getWeatherList().get(j).getLongitude()));
                    currentRow.getCell(r++).setText(Double.toString(list.get(i).getWeatherList().get(j).getLatitude()));
                    currentRow.getCell(r++).setText(Double.toString(list.get(i).getWeatherList().get(j).getMaxTemperatureC()));
                    currentRow.getCell(r++).setText(Double.toString(list.get(i).getWeatherList().get(j).getMinTemperatureC()));
                    currentRow.getCell(r++).setText((list.get(i).getWeatherList().get(j).getVisibility()<=0)?
                            "cant evaluate":Double.toString(list.get(i).getWeatherList().get(j).getVisibility()));
                    currentRow.getCell(r++).setText(Double.toString(list.get(i).getWeatherList().get(j).getCloudCover()));
                    currentRow.getCell(r++).setText(list.get(i).getWeatherList().get(j).getConditions());
                }
            }
        }
    }

    public void createTablesWithHistoricalData(XWPFDocument document, List<NamedYearWeather> list){
        log.info("Were entered into createTablesWithHistoricalData method of " +log.getName());
        for (int i = 0; i < list.size() ; i++) {
            XWPFParagraph nameParagraph= document.createParagraph();
            XWPFRun currentName = nameParagraph.createRun();
            nameParagraph.setAlignment(ParagraphAlignment.CENTER);
            currentName.setFontSize(20);
            currentName.setText(list.get(i).getNAME());
            XWPFParagraph infoParagraph = document.createParagraph();
            if(list.get(i).getYearWeather()==null){
                XWPFRun run = infoParagraph.createRun();
                run.setFontSize(20);
                run.setText("This api can`t handle such function");
                log.warn( list.get(i).getNAME() + "have null-arrays in (historical weather) "+ log.getName());
            }
            else{
                XWPFRun run = infoParagraph.createRun();
                run.setFontSize(20);
                run.setText("Max Temperature was observed in "+ list.get(i).getYearWeather().getYearOfMaxT()+" and was "+ list.get(i).getYearWeather().getMaxT()
                        + " Min Temperature was observed in "+ list.get(i).getYearWeather().getYearOfMinT()+ " and was "+list.get(i).getYearWeather().getMinT());
            }
        }
    }
}
