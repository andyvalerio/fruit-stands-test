package com.andy.fruit.customer;

import com.andy.fruit.FruitEnum;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@ToString
@Getter
public class Customer {
    private final String name;
    @NonNull
    private final List<FruitEnum> preferences;
    @Setter
    private FruitEnum fruitBought;
}
