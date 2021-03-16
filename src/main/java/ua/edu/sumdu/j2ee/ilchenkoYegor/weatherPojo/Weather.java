package ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo;

import java.time.LocalDateTime;

public class Weather {
    LocalDateTime time;
    double maxTemperatureC;
    double minTemperatureC;
    double latitude;
    double longitude;
    double cloudCover;

    public Weather(LocalDateTime time, double maxTemperatureC, double minTemperatureC, double latitude, double longitude, double cloudCover, double visibility, String conditions) {
        this.time = time;
        this.maxTemperatureC = maxTemperatureC;
        this.minTemperatureC = minTemperatureC;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cloudCover = cloudCover;
        this.visibility = visibility;
        this.conditions = conditions;
    }

    public Weather() {
    }

    double visibility;
    String conditions;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getMaxTemperatureC() {
        return maxTemperatureC;
    }

    public void setMaxTemperatureC(double maxTemperatureC) {
        this.maxTemperatureC = maxTemperatureC;
    }

    public double getMinTemperatureC() {
        return minTemperatureC;
    }

    public void setMinTemperatureC(double minTemperatureC) {
        this.minTemperatureC = minTemperatureC;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public String getConditions() {
        return conditions;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "time=" + time +
                ", maxTemperatureC=" + maxTemperatureC +
                ", minTemperatureC=" + minTemperatureC +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", cloudCover=" + cloudCover +
                ", visibility=" + visibility +
                ", conditions='" + conditions + '\'' +
                '}';
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
}
