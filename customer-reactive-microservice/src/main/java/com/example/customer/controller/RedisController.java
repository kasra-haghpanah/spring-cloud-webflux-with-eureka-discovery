package com.example.customer.controller;

import com.example.customer.ddd.model.redis.User;
import com.example.customer.ddd.service.RedisService;
import com.example.customer.utility.MakeContent;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class RedisController {


    final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }


    @ResponseBody
    @RequestMapping(
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value = "/add/websocket/session"
    )
    public Mono<User> addWebSocketSession(
            @RequestPart(value = "username", required = false) String username,
            @RequestPart(value = "host", required = false) String host,
            @RequestPart(value = "port", required = false) String port,
            @RequestPart(value = "wsSessionId", required = false) String wsSessionId,
            ServerWebExchange exchange
    ) {
        return exchange.getFormData()
                .flatMap(form -> {
                    String username1 = form.get("username").get(0);
                    String host1 = form.get("host").get(0);
                    int port1 = Integer.valueOf(form.get("port").get(0));
                    String wsSessionId1 = form.get("wsSessionId").get(0);
                    return redisService.addWebSocket(username1, host1, port1, wsSessionId1);
                });
    }

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value = "/remove/websocket/session"
    )
    public Mono<User> removeWebSocketSession(
            @RequestPart(value = "username", required = false) String username,
            @RequestPart(value = "host", required = false) String host,
            @RequestPart(value = "port", required = false) String port,
            @RequestPart(value = "wsSessionId", required = false) String wsSessionId,
            ServerWebExchange exchange
    ) {
        return exchange.getFormData()
                .flatMap(form -> {
                    String username1 = form.get("username").get(0);
                    String host1 = form.get("host").get(0);
                    int port1 = Integer.valueOf(form.get("port").get(0));
                    String wsSessionId1 = form.get("wsSessionId").get(0);
                    return redisService.removeWebSocket(username1, host1, port1, wsSessionId1);
                });
    }

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value = "/find/all/keys"
    )
    public Flux<User> findAllKeys() {
        return redisService.findAllUsers();
    }

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE,
            value = "/find/all/hashkeys"
    )
    public Flux<String> findAllHashKeys() {
        return redisService.getAllHashKeys()
                .map(string -> {
                    return string + "\n";
                });
    }

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value = "/find/ids/{keys}"
    )
    public Flux<User> findAllById(@RequestParam(name = "keys") String[] keys) {
        return redisService.findAllUsersByKeys(keys);
    }

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_HTML_VALUE,
            value = "/publish"
    )
    public Mono<String> publish(
            @RequestBody MakeContent<User> content,
            @RequestHeader("topic-name") String topicName
    ) {
        return redisService.convertAndSend(topicName, content)
                .flatMap(aLong -> {
                    return Mono.just(aLong + "");
                });
    }


}
