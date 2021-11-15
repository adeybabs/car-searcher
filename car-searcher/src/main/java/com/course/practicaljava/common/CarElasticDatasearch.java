package com.course.practicaljava.common;

import com.course.practicaljava.entity.Car;
import com.course.practicaljava.repository.CarElasticRepository;
import com.course.practicaljava.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

//Annotate to component so spring will automatically add to spring container
@Component
public class CarElasticDatasearch {

    //Logger to log the output
    private static final Logger LOG = LoggerFactory.getLogger(CarElasticDatasearch.class);

    private CarElasticRepository carElasticRepository;
    private CarService carService;
    private WebClient webClient;

    @Autowired
    public CarElasticDatasearch(
            CarElasticRepository carElasticRepository,
            CarService carService, WebClient webClient) {

        this.carElasticRepository = carElasticRepository;
        this.carService = carService;
        this.webClient = webClient;
    }

    //method to populate data to the algorithm
    //It will run on application startup
    @EventListener(ApplicationReadyEvent.class)
    public void populateData() {
        final String response = webClient.delete().uri("practical-java")
                .retrieve().bodyToMono(String.class).block();

        LOG.info("End delete with response {}", response);
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            cars.add(carService.generateCar());
        }
        //ElasticSearch saves all 10,000 data with its API
        carElasticRepository.saveAll(cars);

        //Adding some log to get count of data on elasticsearch
        LOG.info("Saved {} cars", carElasticRepository.count());
    }
}