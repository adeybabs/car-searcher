package com.course.practicaljava.service;

import com.course.practicaljava.entity.Car;
import com.course.practicaljava.entity.Engine;
import com.course.practicaljava.entity.Tire;
import com.course.practicaljava.util.RandomDateUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RandomCarService implements CarService {

    @Override
    public Car generateCar() {
        String brand = BRANDS.get(ThreadLocalRandom.current().nextInt(0, BRANDS.size()));
        String color = COLORS.get(ThreadLocalRandom.current().nextInt(0, COLORS.size()));
        String type = TYPES.get(ThreadLocalRandom.current().nextInt(0, TYPES.size()));

        boolean available = ThreadLocalRandom.current().nextBoolean();
        int price = ThreadLocalRandom.current().nextInt(5000, 12001);
        LocalDate firstReleaseDate = RandomDateUtil.generateRandomLocalDate();

        //Generate random elements taken from element list on CarService
        int randomCount = ThreadLocalRandom.current().nextInt(ADDITIONAL_FEATURES.size());
        List<String> additionalFeatures = new ArrayList<>();

        for (int i = 0; i < randomCount; i++) {
            additionalFeatures.add(ADDITIONAL_FEATURES.get(i));
        }

        String fuel = FUELS.get(ThreadLocalRandom.current().nextInt(FUELS.size()));
        int horsePower = ThreadLocalRandom.current().nextInt(100, 221);

        //new instance of Engine
        Engine engine = new Engine();
        engine.setFuelType(fuel);
        engine.setHorsePower(horsePower);

        List<Tire> tires = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Tire tire = new Tire();
            String manufacturer = TIRE_MANUFACTURER.get(
                    ThreadLocalRandom.current().nextInt(TIRE_MANUFACTURER.size()));

            int size = ThreadLocalRandom.current().nextInt(15, 18);
            int tirePrice = ThreadLocalRandom.current().nextInt(200, 401);
            tire.setManufacturer(manufacturer);
            tire.setPrice(tirePrice);
            tire.setSize(size);
            //Add to list of tires
            tires.add(tire);
        }

        String secretFeature = ThreadLocalRandom.current().nextBoolean() ? "Can fly" : null;

        Car result = new Car(brand, color, type);

        //setting it to car
        result.setAvailable(available);
        result.setFirstReleaseDate(firstReleaseDate);
        result.setPrice(price);
        result.setAdditionalFeatures(additionalFeatures);
        result.setEngine(engine);
        result.setTires(tires);
        result.setSecretFeature(secretFeature);

        return result;
    }
}