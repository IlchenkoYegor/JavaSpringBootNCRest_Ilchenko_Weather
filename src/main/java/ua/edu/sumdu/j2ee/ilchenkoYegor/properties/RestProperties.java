package ua.edu.sumdu.j2ee.ilchenkoYegor.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "weather")
@Validated
public class RestProperties {
    private String apiKey;
    private String apiKeyName;
    private String and;
    private String where;
    private String standartLatitude;
    private String standartLongitude;
    private String patternForDate;
    private int timeoutOfResponse;
    private String apiHost;
    private String apiHostName;
    private int agregatedHours;
    private int queueCapacity;

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getAgregatedHours() {
        return agregatedHours;
    }

    public void setAgregatedHours(int agregatedHours) {
        this.agregatedHours = agregatedHours;
    }

    public String getApiKeyName() {
        return apiKeyName;
    }

    public void setApiKeyName(String apiKeyName) {
        this.apiKeyName = apiKeyName;
    }

    public String getApiHostName() {
        return apiHostName;
    }

    public void setApiHostName(String apiHostName) {
        this.apiHostName = apiHostName;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAnd() {
        return and;
    }

    public void setAnd(String and) {
        this.and = and;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getStandartLatitude() {
        return standartLatitude;
    }

    public void setStandartLatitude(String standartLatitude) {
        this.standartLatitude = standartLatitude;
    }

    public String getStandartLongitude() {
        return standartLongitude;
    }

    public void setStandartLongitude(String standartLongitude) {
        this.standartLongitude = standartLongitude;
    }

    public String getPatternForDate() {
        return patternForDate;
    }

    public void setPatternForDate(String patternForDate) {
        this.patternForDate = patternForDate;
    }

    public int getTimeoutOfResponse() {
        return timeoutOfResponse;
    }

    public void setTimeoutOfResponse(int timeoutOfResponse) {
        this.timeoutOfResponse = timeoutOfResponse;
    }

    public int getAmountOfThreads() {
        return amountOfThreads;
    }

    public void setAmountOfThreads(int amountOfThreads) {
        this.amountOfThreads = amountOfThreads;
    }

    private int amountOfThreads;
    private int amountOfCores;

    public int getAmountOfCores() {
        return amountOfCores;
    }

    public void setAmountOfCores(int amountOfCores) {
        this.amountOfCores = amountOfCores;
    }
}
