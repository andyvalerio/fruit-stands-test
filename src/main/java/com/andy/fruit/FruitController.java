package com.andy.fruit;

import com.andy.fruit.config.AppConfiguration;
import com.andy.fruit.config.FruitConfiguration;
import com.andy.fruit.customer.Customer;
import com.andy.fruit.stand.Stand;
import com.andy.fruit.stand.StandsFactory;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.andy.fruit.FruitEnum.*;
import static com.andy.fruit.util.PublisherUtil.*;

@RestController
@RequestMapping("/fruit")
@AllArgsConstructor
public class FruitController {

    private final FruitConfiguration configuration;
    private final AppConfiguration appConfig;

    @GetMapping(value = "/base-scenario", produces = "application/stream+json")
    public Flux<Object> baseScenario() {
        // Create customers with names and preferences
        Customer pelle = Customer.builder().name("Pelle").preferences(List.of(CHERRIES)).build();
        Customer kajsa = Customer.builder().name("Kajsa").preferences(List.of(PEACHES)).build();
        List<Customer> customers = List.of(pelle, kajsa);
        // Create stands
        List<Stand> stands = StandsFactory.buildStands(
            configuration.getNumberOfStands(),
            List.of(CHERRIES, PEACHES),
            configuration.getMaxFruitPrice()
        );

        // Find stand with minimal cost
        Stand closestStandMinimumCombinedPrice = stands.stream()
                .filter(s -> s.satisfies(pelle) && s.satisfies(kajsa))
                .min((s1, s2) -> {
                    int totalPriceS1 = s1.getFruitToPrice().values().stream().mapToInt(Integer::intValue).sum();
                    int totalPriceS2 = s2.getFruitToPrice().values().stream().mapToInt(Integer::intValue).sum();
                    if (totalPriceS1 == totalPriceS2) return s1.getId() - s2.getId();
                    return totalPriceS1 - totalPriceS2;
                }).orElseThrow();

        return Flux.merge(
                List.of(
                    printString("Number of stands for this scenario: " + configuration.getNumberOfStands()),
                    printStands(stands),
                    printString("They stopped at the stand: " + closestStandMinimumCombinedPrice),
                    printCustomers(customers)
                )
            ).delayElements(Duration.ofMillis(appConfig.getDelayEvents()));
    }

    @GetMapping(value = "/extension-1-2", produces = "application/stream+json")
    public Flux<Object> extension12() {
        // Create customers with names and preferences
        Customer pelle = Customer.builder().name("Pelle").preferences(List.of(CHERRIES, PEARS)).build();
        Customer kajsa = Customer.builder().name("Kajsa").preferences(List.of(PEACHES, PEARS)).build();
        List<Customer> customers = List.of(pelle, kajsa);
        // Create stands
        List<Stand> stands = StandsFactory.buildStands(
                configuration.getNumberOfStands(),
                List.of(CHERRIES, PEACHES, PEARS),
                configuration.getMaxFruitPrice()
        );

        // Find stand with minimal cost
        Stand bestWithPears = stands.stream()
                .filter(s -> s.satisfies(pelle) && s.satisfies(kajsa) && s.getFruitToPrice().containsKey(PEARS))
                .min((s1, s2) -> {
                    int totalPriceS1 = s1.getFruitToPrice().get(PEARS)
                            + Math.min(s1.getFruitToPrice().get(CHERRIES), s1.getFruitToPrice().get(PEACHES));
                    int totalPriceS2 = s2.getFruitToPrice().get(PEARS)
                            + Math.min(s2.getFruitToPrice().get(CHERRIES), s2.getFruitToPrice().get(PEACHES));
                    if (totalPriceS1 == totalPriceS2) return s1.getId() - s2.getId();
                    return totalPriceS1 - totalPriceS2;
                }).orElseThrow();

        FruitEnum otherFruit = bestWithPears.getFruitToPrice().get(CHERRIES) > bestWithPears.getFruitToPrice().get(PEACHES) ?
                PEACHES : CHERRIES;

        if (kajsa.getPreferences().contains(otherFruit)) {
            kajsa.setFruitBought(otherFruit);
            pelle.setFruitBought(PEARS);
        }
        else {
            kajsa.setFruitBought(PEARS);
            pelle.setFruitBought(otherFruit);
        }

        return Flux.merge(
                List.of(
                        printString("Number of stands for this scenario: " + configuration.getNumberOfStands()),
                        printStands(stands),
                        printString("They stopped at the stand: " + bestWithPears),
                        printCustomers(customers),
                        printString("They spent a total of " +
                                (bestWithPears.getFruitToPrice().get(PEARS) + bestWithPears.getFruitToPrice().get(otherFruit)))
                )
        ).delayElements(Duration.ofMillis(appConfig.getDelayEvents()));
    }

    @GetMapping(value = "/extension-3", produces = "application/stream+json")
    public Flux<Object> extension3() {
        // Create customers with names and preferences
        Customer pelle = Customer.builder().name("Pelle").preferences(List.of(CHERRIES, PEARS)).build();
        Customer kajsa = Customer.builder().name("Kajsa").preferences(List.of(PEACHES, PEARS)).build();
        List<Customer> customers = List.of(pelle, kajsa);
        // Create stands
        List<Stand> stands = StandsFactory.buildStandsRandomFruit(
                configuration.getNumberOfStands(),
                List.of(CHERRIES, PEACHES, PEARS),
                configuration.getMaxFruitPrice()
        );

        List<Stand> usefulStands = new ArrayList<>();
        // Find stand with minimal cost
        Stand bestWithPears;
        FruitEnum otherFruit;
        try {
            bestWithPears = stands.stream()
                    .filter(s -> (s.satisfies(pelle) || s.satisfies(kajsa)) && s.getFruitToPrice().containsKey(PEARS))
                    .peek(usefulStands::add)
                    .min((s1, s2) -> {
                        int totalPriceS1 = s1.getFruitToPrice().get(PEARS)
                                + Math.min(s1.getFruitToPrice().getOrDefault(
                                CHERRIES, Integer.MAX_VALUE), s1.getFruitToPrice().getOrDefault(PEACHES, Integer.MAX_VALUE));
                        int totalPriceS2 = s2.getFruitToPrice().get(PEARS)
                                + Math.min(s2.getFruitToPrice().getOrDefault(
                                CHERRIES, Integer.MAX_VALUE), s2.getFruitToPrice().getOrDefault(PEACHES, Integer.MAX_VALUE));
                        if (totalPriceS1 == totalPriceS2) return s1.getId() - s2.getId();
                        return totalPriceS1 - totalPriceS2;
                    }).orElseThrow();

            otherFruit = bestWithPears.getFruitToPrice().getOrDefault(CHERRIES, Integer.MAX_VALUE) >
                    bestWithPears.getFruitToPrice().getOrDefault(PEACHES, Integer.MAX_VALUE) ? PEACHES : CHERRIES;

            if (kajsa.getPreferences().contains(otherFruit)) {
                kajsa.setFruitBought(otherFruit);
                pelle.setFruitBought(PEARS);
            }
            else {
                kajsa.setFruitBought(PEARS);
                pelle.setFruitBought(otherFruit);
            }
        } catch (NoSuchElementException nsee) {
            return Flux.merge(List.of(
                    printCustomers(customers),
                    printString("Number of stands for this scenario: " + configuration.getNumberOfStands()),
                    printStands(stands),
                    printString("They could not find any stand to buy their fruit baskets")
            ));
        }

        return Flux.concat(
                List.of(
                        printString("Number of stands for this scenario: " + configuration.getNumberOfStands()),
                        printStands(stands),
                        printString("They considered the following stands: "),
                        printStands(usefulStands),
                        printString("They stopped at the stand: " + bestWithPears),
                        printCustomers(customers),
                        printString("They spent a total of " +
                                (bestWithPears.getFruitToPrice().get(PEARS) + bestWithPears.getFruitToPrice().get(otherFruit)))
                )
        ).delayElements(Duration.ofMillis(appConfig.getDelayEvents()));
    }

    @GetMapping(value = "/extension-4", produces = "application/stream+json")
    public Flux<Object> extension4() {
        // Create customers with names and preferences
        Customer pelle = Customer.builder().name("Pelle").preferences(List.of(CHERRIES, PEARS)).build();
        Customer kajsa = Customer.builder().name("Kajsa").preferences(List.of(PEACHES, PEARS)).build();
        Customer friend = Customer.builder().name("Vännen").preferences(List.of()).build(); // He has no preference
        List<Customer> customers = List.of(pelle, kajsa, friend);
        // Create stands
        List<Stand> stands = StandsFactory.buildStandsRandomFruit(
                configuration.getNumberOfStands(),
                List.of(CHERRIES, PEACHES, PEARS),
                configuration.getMaxFruitPrice()
        );

        List<Stand> usefulStands = new ArrayList<>();
        // Find stand with minimal cost
        Stand bestStand;
        FruitEnum cheapestSecondaryFruit;
        try {
            bestStand = stands.stream()
                    .filter(s -> s.satisfies(pelle) && s.satisfies(kajsa) && s.satisfies(friend))
                    .peek(usefulStands::add)
                    .min((s1, s2) -> {
                        int totalPriceS1 = s1.getFruitToPrice().get(PEARS) + s1.getFruitToPrice().get(PEACHES)
                                + s1.getFruitToPrice().get(CHERRIES);
                        int totalPriceS2 = s2.getFruitToPrice().get(PEARS) + s2.getFruitToPrice().get(PEACHES)
                                + s2.getFruitToPrice().get(CHERRIES);
                        if (totalPriceS1 == totalPriceS2) return s1.getId() - s2.getId();
                        return totalPriceS1 - totalPriceS2;
                    }).orElseThrow();

            cheapestSecondaryFruit = bestStand.getFruitToPrice().get(CHERRIES) >
                    bestStand.getFruitToPrice().get(PEACHES) ? PEACHES : CHERRIES;
            if (kajsa.getPreferences().contains(cheapestSecondaryFruit)) {
                kajsa.setFruitBought(cheapestSecondaryFruit);
                pelle.setFruitBought(PEARS);
                friend.setFruitBought(CHERRIES);
            }
            else {
                kajsa.setFruitBought(PEARS);
                pelle.setFruitBought(cheapestSecondaryFruit);
                friend.setFruitBought(PEACHES);
            }
        } catch (NoSuchElementException nsee) {
            return Flux.merge(List.of(
                    printString("Number of stands for this scenario: " + configuration.getNumberOfStands()),
                    printStands(stands),
                    printString("They could not find any stand to buy their fruit baskets"),
                    printCustomers(customers)
                    ));
        }

        return Flux.concat(
                List.of(
                        printString("Number of stands for this scenario: " + configuration.getNumberOfStands()),
                        printStands(stands),
                        printString("They considered the following stands: "),
                        printStands(usefulStands),
                        printString("They stopped at the stand: " + bestStand),
                        printString("They bought: "),
                        printCustomers(customers)
                )
        ).delayElements(Duration.ofMillis(appConfig.getDelayEvents()));
    }

}

