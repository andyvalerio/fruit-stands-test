package com.andy.fruit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Setter
public class FruitConfiguration {
    @Getter
    private int numberOfStands;
    @Getter
    private int maxFruitPrice;
}
