package com.course.practicaljava.service;

import com.course.practicaljava.entity.Car;

import java.util.Arrays;
import java.util.List;

public interface CarService {

    List<String> BRANDS = Arrays
            .asList("Toyota", "Honda", "Ford", "BMW", "Mitsubishi");

    List<String> COLORS = Arrays
            .asList("Red", "Black", "White", "Blue", "Silver");

    List<String> TYPES = Arrays
            .asList("Sedan", "SUV", "MPV", "Hatchback", "Convertible");

    List<String> ADDITIONAL_FEATURES = Arrays
            .asList("GPS", "Alarm", "Sunroof", "Media player", "Leather seats");

    List<String> FUELS = Arrays
            .asList("Petrol", "Electric", "Hybrid");

    List<String> TIRE_MANUFACTURER = Arrays
            .asList("Goodyear", "Bridgestone", "Dunlop");

    Car generateCar();
}
