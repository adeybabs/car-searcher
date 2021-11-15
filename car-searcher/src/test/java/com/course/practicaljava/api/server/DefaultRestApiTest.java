package com.course.practicaljava.api.server;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DefaultRestApiTest {

    private Logger LOG = LoggerFactory.getLogger(DefaultRestApiTest.class);

    //for mocking autowire webTestClient variable
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testWelcome() {
        webTestClient
                .get()
                .uri("/api/welcome")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .value(IsEqualIgnoringCase
                        .equalToIgnoringCase("welcome to spring boot"));
    }

    @Test
    public void testTime() {
        webTestClient
                .get()
                .uri("/api/time")
                .exchange()
                .expectBody(String.class)
                .value(v -> isCorrectTime(v));
    }

    private LocalTime isCorrectTime(String v) {
        LocalTime responseLocalTime = LocalTime.parse(v);
        LOG.info("responseLocalTime is : {}", responseLocalTime);
        LocalTime now = LocalTime.now();
        LOG.info("now is : {}", now);
        LocalTime nowMinus30Seconds = now.minusSeconds(30);
        LOG.info("nowMinus30Seconds is : {}", nowMinus30Seconds);
        assertTrue(responseLocalTime.isAfter(nowMinus30Seconds)
                && responseLocalTime.isBefore(now));

        return responseLocalTime;
    }

    @Test
    public void testHeaderByAnnotation() {
        String headerOne = "Spring Boot Test";
        String headerTwo = "Spring Boot Test on Practical Java";

        WebTestClient.BodySpec<String, ?> stringBodySpec = webTestClient
                .get()
                .uri("/api/header-one")
                .header("User-agent", headerOne)
                .header("Practical-java", headerTwo)
                .exchange()
                .expectBody(String.class)
                .value(v -> {
                    assertTrue(v.contains(headerOne));
                    assertTrue(v.contains(headerTwo));
                });
    }
}