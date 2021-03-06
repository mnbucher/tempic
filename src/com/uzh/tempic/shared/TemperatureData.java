package com.uzh.tempic.shared;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TemperatureData implements Serializable {
    private Date date;
    private Double avgTemperature;
    private Double avgTemperatureUncertainty;
    private String city;
    private String country;
    private String latitude;
    private String longitude;

    public TemperatureData() {}

    public TemperatureData(Date date, Double avgTemperature, Double avgTemperatureUncertainty,
                           String city, String country, String latitude, String longitude) {
        this.date = date;
        this.avgTemperature = avgTemperature;
        this.avgTemperatureUncertainty = avgTemperatureUncertainty;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public int getYear() {
        return Integer.parseInt(DateTimeFormat.getFormat( "yyyy" ).format( this.date ));
    }

    public Double getAvgTemperature() {
        return avgTemperature;
    }

    public Double getAvgTemperatureUncertainty() {
        return avgTemperatureUncertainty;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String toString() {
        return "TemperatureData{" +
                "date=" + date +
                ", avgTemperature=" + avgTemperature +
                ", avgTemperatureUncertainty=" + avgTemperatureUncertainty +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
    
    // TODO: Write test for this function
    public void setAvgTemperature(double avgTemperature) {
        this.avgTemperature = avgTemperature;
    }
    
    // TODO: Write test for this function
    public void setAvgTemperatureUncertainty(double avgTemperatureUncertainty) {
        this.avgTemperatureUncertainty = avgTemperatureUncertainty;
    }
        
    
    /**
     * Parses the string coordinate value to a double decimal value
     * @pre coordinate String must have "N", "S", "W", "E" as the last character
     * @post -
     * @param coordinate A string which contains the coordinates and the direction
     * @return decimal value of coordinate
     */

    // TODO: Write Test for this function
    private double decimalConversion(String coordinate) {
        Double decimalCoordinate = 0.0;
        String dir = coordinate.substring(-1);
        coordinate = coordinate.substring(0,coordinate.length() - 1);
        if(Objects.equals(dir, "E") || Objects.equals(dir, "N")) {
            decimalCoordinate = Double.parseDouble(coordinate);
        } else if(Objects.equals(dir, "W") || Objects.equals(dir, "S")) {
            decimalCoordinate = -1 * Double.parseDouble(coordinate);
        }
        return decimalCoordinate;
    }
    // TODO: Write Test for this function
    public double getDecimalLongitude() {
        return decimalConversion(this.longitude);
    }
    // TODO: Write Test for this function
    public double getDecimalLatitude() {
        return decimalConversion(this.latitude);
    }
    
}