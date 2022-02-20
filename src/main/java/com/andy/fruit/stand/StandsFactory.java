package com.andy.fruit.stand;

import com.andy.fruit.FruitEnum;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.andy.fruit.util.FruitUtil.randomFruits;

public class StandsFactory {

    public static List<Stand> buildStands(int numberOfStands, List<FruitEnum> fruit, int maxFruitPrice) {
        Random r = new Random();
        return IntStream.range(1, numberOfStands+1)
                .mapToObj(i -> Stand.builder().id(i)
                        .fruitToPrice(fruit.stream()
                                .collect(Collectors.toMap(
                                        Function.identity(), x -> 1 + r.nextInt(maxFruitPrice - 1)))).build())
                .collect(Collectors.toList());
    }

    public static List<Stand> buildStandsRandomFruit(int numberOfStands, List<FruitEnum> fruit, int maxFruitPrice) {
        Random r = new Random();
        return IntStream.range(1, numberOfStands+1)
                .mapToObj(i -> Stand.builder().id(i)
                        .fruitToPrice(randomFruits(fruit).stream()
                                .collect(Collectors.toMap(
                                        Function.identity(), x -> 1 + r.nextInt(maxFruitPrice - 1)))).build())
                .collect(Collectors.toList());
    }
}
