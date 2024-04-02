package com.example.customer.configuration;


import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebFilter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;

//https://www.baeldung.com/spring-boot-reactor-netty
@Configuration
@DependsOn("properties")
public class NettyWebServerFactorySslCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    @Override
    public void customize(NettyReactiveWebServerFactory serverFactory) {
//        Ssl ssl = new Ssl();
//        ssl.setEnabled(true);
//        ssl.setKeyStore("classpath:sample.jks");
//        ssl.setKeyAlias("alias");
//        ssl.setKeyPassword("password");
//        ssl.setKeyStorePassword("secret");
//        Http2 http2 = new Http2();
//        http2.setEnabled(false);
//        serverFactory.addServerCustomizers(new SslServerCustomizer(ssl, http2, null));
        serverFactory.setPort(Properties.getServerPort());
/*

        InetAddress address = null;
        try {
            address = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        serverFactory.setAddress(address);
*/
        //final String applicationPath = MessageFormat.format("/{0}",environment.getProperty("spring.application.name"));
        //((ConfigurableServletWebServerFactory) serverFactory).setContextPath(applicationPath);


    }


    @Bean
    public WebFilter contextPathWebFilter() {
        String contextPath = MessageFormat.format("/{0}", Properties.getApplicationName());

        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (request.getURI().getPath().startsWith(contextPath + "/")) {
                return chain.filter(
                        exchange.mutate()
                                .request(request.mutate().contextPath(contextPath).build())
                                .build());
            }
            return chain.filter(exchange);
        };
    }

}
