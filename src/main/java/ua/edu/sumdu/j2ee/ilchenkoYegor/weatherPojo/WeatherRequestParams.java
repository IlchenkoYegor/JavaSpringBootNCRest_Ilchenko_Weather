package ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo;

import java.time.LocalDate;

public class WeatherRequestParams {
    private double lat;
    private double longt;
    private LocalDate from;
    private LocalDate to;
    private boolean isXML;

    public WeatherRequestParams(double lat, double longt, LocalDate from, LocalDate to, boolean isXML) {
        this.lat = lat;
        this.longt = longt;
        this.from = from;
        this.to = to;
        this.isXML = isXML;
    }

    public WeatherRequestParams() {
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

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public boolean isXML() {
        return isXML;
    }

    public void setXML(boolean XML) {
        isXML = XML;
    }
}
