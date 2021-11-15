package com.course.practicaljava.service;

import java.util.Arrays;
import java.util.List;

public interface CarPromotionService {

    List<String> PROMOTION_TYPES = Arrays.asList("bonus", "discount");

    boolean isValidPromotionType(String promotionType);
}
