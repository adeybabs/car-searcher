package com.course.practicaljava.util;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDateUtil {
    //generating random date from min day to max day

    private static final long MIN_DAY = LocalDate.of(2018, Month.JANUARY, 1).toEpochDay();
    private static final long MAX_DAY = LocalDate.now().toEpochDay();

    public static LocalDate generateRandomLocalDate() {
        long randomDay = MIN_DAY + ThreadLocalRandom.current().nextLong(MAX_DAY - MIN_DAY);
        return LocalDate.ofEpochDay(randomDay);
    }

}
