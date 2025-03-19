package com.github.embolo.v10t1;

import java.util.ArrayList;

public class CarDataStorage {
    private static CarDataStorage carDataStorage;
    private String city;
    private int year;
    private ArrayList<CarData> carData = new ArrayList<>();

    static public CarDataStorage getInstance() {
        if(carDataStorage == null) {
            carDataStorage = new CarDataStorage();
        }
        return carDataStorage;
    }
    public ArrayList<CarData> getCarData() {
        return carData;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getYear() {
        return year;
    }
    public void clearData() {
        carData.clear();
    }
    public void addCarData(CarData carDataParameter) {
        carData.add(carDataParameter);
    }
}
