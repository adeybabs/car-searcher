package com.course.practicaljava.api.server;

import com.course.practicaljava.entity.Car;
import com.course.practicaljava.exception.IllegalApiParamException;
import com.course.practicaljava.repository.CarElasticRepository;
import com.course.practicaljava.response.ErrorResponse;
import com.course.practicaljava.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping(value = "/api/car/v1")
@Tag(name = "Car API", description = "Documentation for Car API")
public class CarApi {

    private static final Logger LOG = LoggerFactory.getLogger(CarApi.class);

    @Autowired
    private CarElasticRepository carElasticRepository;

    @Autowired
    private CarService carService;

    //return instance of random car taken from car service
    @GetMapping(value = "/random", produces = MediaType.APPLICATION_JSON_VALUE)
    public Car random() {
        return carService.generateCar();
    }

    @Operation(summary = "Echo car", description = "Echo given car input")

    //method echo returning toString method of car input
    @PostMapping(value = "/echo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String echo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody
                    (description = "Car to be echoed") @RequestBody Car car) {
        LOG.info("Car is {}", car);

        return car.toString();
    }

    //To return list of cars (collection)
    @GetMapping(value = "/random-cars", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Car> randomCars() {
        List<Car> result = new ArrayList<>();

        for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 10); i++) {
            //populate some random cars as many as random count
            result.add(carService.generateCar());
        }

        return result;
    }

    //Endpoint to carElasticRepository.count
    @GetMapping(value = "/count")
    public String countCar() {
        return "There are : " + carElasticRepository.count() + " cars.";
    }

    //To save new car into elasticsearch
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String saveCar(@RequestBody Car car) {
        final String id = carElasticRepository.save(car).getId();
        return "Saved with ID : " + id;
    }

    //Endpoint for getting car buy the Id
    @GetMapping(value = "/{id}")
    public Car getCar(@PathVariable("id") String carId) {
        return carElasticRepository.findById(carId).orElse(null);
    }

    //Endpoint for updating data by Id
    @PutMapping(value = "/{id}")
    public String updateCar(@PathVariable("id") String carId, @RequestBody Car updatedCar) {
        updatedCar.setId(carId);
        final Car newCar = carElasticRepository.save(updatedCar);

        return "Updated car with ID : " + newCar.getId();
    }

    //A method that returns List of Cars, which takes car as a parameter
    @GetMapping(value = "find-json", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Car> findCarsByBrandAndColor(@RequestBody Car car,
           @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "price"));
        return carElasticRepository.findByBrandAndColor(car.getBrand(), car.getColor(), pageable).getContent();
    }

//    @GetMapping(value = "/cars/{brand}/{color}")
//    public List<Car> findCarByPath(@PathVariable String brand, @PathVariable String color,
//           @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
//        PageRequest pageable = PageRequest.of(page, size);
//        return carElasticRepository.findByBrandAndColor(brand, color, pageable).getContent();
//    }

    //Method that finds car by the path brand/color
    @GetMapping(value = "/cars/{brand}/{color}")
    @Operation(summary = "Find cars by path", description = "Find cars by path variable")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Everything is OK"),
            @ApiResponse(responseCode = "400", description = "Bad input parameter") })
    public ResponseEntity<Object> findCarByPath(
            @Parameter(description = "Brand to be find") @PathVariable String brand,
            @Parameter(description = "Color to be find", example = "white") @PathVariable String color,
            @Parameter(description = "Page number (for pagination)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (for pagination)") @RequestParam(defaultValue = "10") int size) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SERVER, "Spring");
        headers.add("X-Custom-Header", "Custom Response Header");

        if (StringUtils.isNumeric(color))  {
            ErrorResponse errorResponse = new ErrorResponse("Invalid color : " + color, LocalDateTime.now());

            return new ResponseEntity<Object>(errorResponse, headers, HttpStatus.BAD_REQUEST);
        }
        PageRequest pageable = PageRequest.of(page, size);
        List<Car> cars = carElasticRepository.findByBrandAndColor(brand, color, pageable).getContent();

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(cars);
    }

    //method for finding by parameters ?brand={brand}...
    @GetMapping(value = "/cars")
    public List<Car> findByParam(@RequestParam String brand, @RequestParam String color,
           @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        if (StringUtils.isNumeric(color)) {
            throw new IllegalArgumentException("Invalid color : " + color);
        }

        if (StringUtils.isNumeric(brand)) {
            throw new IllegalApiParamException("Invalid brand : " + brand);
        }

        PageRequest pageable = PageRequest.of(page, size);
        return carElasticRepository.findByBrandAndColor(brand, color, pageable).getContent();
    }

    @GetMapping(value = "/cars/date")
    public List<Car> findCarsReleaseAfter(
            @RequestParam(name = "first_release_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate firstReleaseDate) {
        return carElasticRepository.findByFirstReleaseDateAfter(firstReleaseDate);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    private ResponseEntity<ErrorResponse> handleInvalidColorException(IllegalArgumentException e) {
        String message = "Exception, " + e.getMessage();
        LOG.warn(message);
        ErrorResponse errorResponse = new ErrorResponse(message, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

//    @ExceptionHandler(value = IllegalApiParamException.class)
//    private ResponseEntity<ErrorResponse> handleInvalidColorException(IllegalApiParamException e) {
//        String message = "Exception API Param, " + e.getMessage();
//        LOG.warn(message);
//        ErrorResponse errorResponse = new ErrorResponse(message, LocalDateTime.now());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//    }
}