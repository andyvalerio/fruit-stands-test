package com.andy.fruit.stand;

import com.andy.fruit.FruitEnum;
import com.andy.fruit.customer.Customer;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
@Getter
public class Stand {
    private final int id;
    private final Map<FruitEnum, Integer> fruitToPrice;

    public boolean satisfies(Customer customer) {
        return fruitToPrice.keySet().containsAll(customer.getPreferences());
    }
}
