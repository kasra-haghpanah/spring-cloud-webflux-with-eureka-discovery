package com.example.customer.controller;


import com.example.customer.ddd.dto.Customer;
import com.example.customer.ddd.service.CustomerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class RestController {

    private final Map<String, AtomicInteger> countOfErrors = new ConcurrentHashMap<>();

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            value = "/workers"
    )
    public Flux<Customer> workers(ServerWebExchange exchange) {

        HttpHeaders headers = exchange.getRequest().getHeaders();
        System.out.println(headers.get("Client-Ip"));

        List<Customer> lis = new ArrayList<>();
        return Flux.just("Jim", "Josh", "William", "Jack").reduce(0, (id, name) -> {
                    lis.add(new Customer().setName(name).setId(id + 1));
                    return id + 1;
                })
                .thenMany(Flux.fromIterable(lis));
    }

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            value = "/customers"
    )
    public Flux<Customer> customers(ServerWebExchange exchange) {

        HttpHeaders headers = exchange.getRequest().getHeaders();
        System.out.println(headers.get("Client-Ip"));
        // to detect the user who uses a Proxy Server
        System.out.println(headers.get("Forwarded"));
        System.out.println(headers.get("X-Forwarded-For"));
        System.out.println(headers.get("X-Real-Ip"));

        return CustomerService.customers(new String[]{"Jim", "Josh", "William", "Jack"});
    }

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            value = "/users"
    )
    public Flux<Customer> users(
            @RequestParam(value = "names") String[] names,
            ServerWebExchange exchange
    ) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        System.out.println(headers.get("Client-Ip"));

        return CustomerService.customers(names);
    }

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value = "/error/{id}"
    )
    public Flux<String> getError(@PathVariable(value = "id") String id, ServerWebExchange exchange) {

        HttpHeaders headers = exchange.getRequest().getHeaders();
        System.out.println(headers.get("Client-Ip"));

        var result = this.countOfErrors.compute(id, (idValue, atomicInteger) -> {
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger(0);
            }
            atomicInteger.incrementAndGet();
            return atomicInteger;
        }).get();

        if (result < 5) {
            System.out.println(String.format("success for ID '%d' on count # %d", 1, result));
            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            return Flux.just("{\"message\": \"HttpStatus.SERVICE_UNAVAILABLE\"}");
        }

        String value = String.format("good job, %s you did it on try # %s", id, countOfErrors.toString());
        value = String.format("{\"message\":\"%s\"}", value);
        return Flux.just(value);

    }

}
