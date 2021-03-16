package ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo;

import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.sumdu.j2ee.ilchenkoYegor.properties.RestProperties;

import java.time.LocalDate;

public class YearWeatherRequestParams {
    private RestProperties restProperties;
    private double lat;
    private double longt;
    private LocalDate startTime;
    private LocalDate endTime;
    private int periodOfMisurement;
    @Autowired
    public YearWeatherRequestParams(RestProperties restProperties) {
        this.restProperties = restProperties;
        periodOfMisurement = restProperties.getAgregatedHours();
    }

    public YearWeatherRequestParams(double lat, double longt, LocalDate startTime, LocalDate endTime, int periodOfMisurement) {
        this.lat = lat;
        this.longt = longt;
        this.startTime = startTime;
        this.endTime = endTime;
        this.periodOfMisurement = periodOfMisurement;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongt() {
        return longt;
    }

    public void setLongt(double longt) {
        this.longt = longt;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public int getPeriodOfMisurement() {
        return periodOfMisurement;
    }

    public void setPeriodOfMisurement(int periodOfMisurement) {
        this.periodOfMisurement = periodOfMisurement;
    }
}
