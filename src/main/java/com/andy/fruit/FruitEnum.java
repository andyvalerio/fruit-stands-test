package com.andy.fruit;

import lombok.ToString;

@ToString
public enum FruitEnum {
    CHERRIES("Cherries \uD83C\uDF52"),
    PEACHES("Peaches \uD83C\uDF51"),
    PEARS("Pears \uD83C\uDF50");

    public final String name;

    FruitEnum(String name) {
        this.name = name;
    }

}
