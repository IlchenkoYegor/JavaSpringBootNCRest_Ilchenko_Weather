package ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherBitDTO {
    public WeatherBitDTO(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public WeatherBitDTO() {
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "WeatherBitDTO{" +
                "dataList=" + data +
                '}';
    }

    private double lon;
    private double lat;
    DataWithWeather[] data;
    public static class DataWithWeather{
        public WeatherIn getWeather() {
            return weather;
        }

        public void setWeather(WeatherIn weather) {
            this.weather = weather;
        }

        WeatherIn weather;
        @Override
        public String toString() {
            return "Data{" +
                    "timestamp_utc=" + timestamp_utc +
                    ", clouds=" + clouds +
                    ", vis=" + vis +
                    ", temp=" + temp + ", weather conditions= "+
                    weather.getDescription()+'}';
        }

        public DataWithWeather() {
        }

        public DataWithWeather(LocalDateTime timestamp_utc, int clouds, double vis, double temp) {
            this.timestamp_utc = timestamp_utc;
            this.clouds = clouds;
            this.vis = vis;
            this.temp = temp;
        }

        LocalDateTime timestamp_utc;
        int clouds;
        double vis;
        double temp;
        public class WeatherIn{
            String description;

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }

        public LocalDateTime getTimestamp_utc() {
            return timestamp_utc;
        }

        public void setTimestamp_utc(LocalDateTime timestamp_utc) {
            this.timestamp_utc = timestamp_utc;
        }

        public int getClouds() {
            return clouds;
        }

        public void setClouds(int clouds) {
            this.clouds = clouds;
        }

        public double getVis() {
            return vis;
        }

        public void setVis(double vis) {
            this.vis = vis;
        }

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }
    }

    public DataWithWeather[] getData() {
        return data;
    }

    public void setData(DataWithWeather[] dataList) {
        this.data = dataList;
    }
}
