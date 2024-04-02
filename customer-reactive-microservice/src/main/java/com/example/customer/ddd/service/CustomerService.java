package com.example.customer.ddd.service;

import com.example.customer.ddd.dto.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerService {

    public static final Flux<Customer> getCustomers(String[] names) {

        final AtomicInteger counter = new AtomicInteger(0);

        return Flux.fromArray(names)
                .flatMap(name -> {
                    return Mono.just(new Customer().setName(name).setId(counter.incrementAndGet()));
                })
                .delayElements(Duration.ofSeconds(1));
    }

    public static Flux<Customer> customers(String[] names) {
        return getCustomers(names).publish().autoConnect();
    }

}
