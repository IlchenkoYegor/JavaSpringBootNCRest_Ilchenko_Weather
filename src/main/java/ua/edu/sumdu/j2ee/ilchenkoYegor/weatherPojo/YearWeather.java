package ua.edu.sumdu.j2ee.ilchenkoYegor.weatherPojo;

public class YearWeather {
    private int yearOfMaxT;
    private int yearOfMinT;
    private double maxT;
    private double latitude;
    private double longitude;

    public YearWeather(int yearOfMaxT, int yearOfMinT, double maxT, double latitude, double longitude, double minT) {
        this.yearOfMaxT = yearOfMaxT;
        this.yearOfMinT = yearOfMinT;
        this.maxT = maxT;
        this.latitude = latitude;
        this.longitude = longitude;
        this.minT = minT;
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

    public YearWeather(int yearOfMaxT, int yearOfMinT, double maxT, double minT) {
        this.yearOfMaxT = yearOfMaxT;
        this.yearOfMinT = yearOfMinT;
        this.maxT = maxT;
        this.minT = minT;
    }
    public YearWeather(){
    }

    public int getYearOfMaxT() {
        return yearOfMaxT;
    }

    public void setYearOfMaxT(int yearOfMaxT) {
        this.yearOfMaxT = yearOfMaxT;
    }

    public int getYearOfMinT() {
        return yearOfMinT;
    }

    public void setYearOfMinT(int yearOfMinT) {
        this.yearOfMinT = yearOfMinT;
    }



    public void setMaxT(double maxT) {
        this.maxT = maxT;
    }

    public void setMinT(double minT) {
        this.minT = minT;
    }

    public double getMaxT() {
        return maxT;
    }

    public double getMinT() {
        return minT;
    }

    private double minT;
}
