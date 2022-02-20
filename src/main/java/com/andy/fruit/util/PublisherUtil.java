package com.andy.fruit.util;

import com.andy.fruit.customer.Customer;
import com.andy.fruit.stand.Stand;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;

public class PublisherUtil {
    public static Publisher<Object> printCustomers(List<Customer> objects) {
        return subscriber -> {
            for(Customer c : objects) subscriber.onNext(c.toString());
            subscriber.onComplete();
        };
    }

    public static Publisher<Object> printStands(List<Stand> objects) {
        return subscriber -> {
            for (Stand s : objects) {
                subscriber.onNext(s.toString());
            }
            subscriber.onComplete();
        };
    }

    public static Publisher<String> printString(String x) {
        return subscriber -> {
            subscriber.onNext(x);
            subscriber.onComplete();
        };
    }
}
