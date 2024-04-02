package com.example.configurationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fb")
public class FallbackController {

    @RequestMapping(value = "/customer", method = RequestMethod.POST)
    @ResponseBody
    public Mono<String> customerFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just("customerFallback");
    }

//    @PostMapping(value = "/customer")
//    public ResponseEntity<HttpStatus> ticketFallback(){
//        return ResponseEntity.ok(HttpStatus.SERVICE_UNAVAILABLE);
//    }

}
