package com.andy.fruit.util;

import com.andy.fruit.FruitEnum;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FruitUtil {
    public static List<FruitEnum> randomFruits(List<FruitEnum> allFruits) {
        return allFruits.stream().filter(x -> new Random().nextBoolean())
                .collect(Collectors.toList());
    }
}
