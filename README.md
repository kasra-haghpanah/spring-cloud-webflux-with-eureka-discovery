# Spring Cloud Webflux for Reactive Applications


This project includes Spring Service Registry, Spring Cloud Gateway, Spring Reactive Microservice, and Swagger 3.1. Alternatively, broadcasting Websocket among multiple machines has been used from the Redis database.

# Notice:
This project is designed for Reactive Programming, so it is used from Spring Webflux alongside Spring Cloud Gateway Webflux.
JDK version is used 21 as well as Spring Cloud with version 2023.0.0 and also Spring Webflux 3.2.4

# Prerequisites
- JDK 21
- Redis 7.0 or later

# Guidelines:
- **First**, execute **clean install** command to compile all three modules.<br />
- **Second**, run **RegistryApplication**, **CloudGatewayApplication**, and **CustomerApplication** in order.

To look at the result, refer to the addresses below:  

# Eureka Server Registry:                
http://localhost:8761/    
# Spring Cloud Gateway Webflux(Swagger):     
http://localhost:9999/webjars/swagger-ui/index.html#/     
# Spring Cloud Gateway Webflux(Websocket):     
 http://localhost:9999/ws.html     

> [!TIP]
> The address below exists on Swagger.   

http://localhost:9999/customer/resource/1.0.0/ws.html    
# Spring Webflux Microservice(Swagger):     
http://localhost:8181/webjars/swagger-ui/index.html#/    
# Spring Webflux Microservice(Websocket):       
http://localhost:8181/customer/ws.html   

> [!TIP]  
> The address below exists on Swagger.   

http://localhost:8181/customer/resource/1.0.0/ws.html     

If you'd like to run multiple instances of your microservice, you should refer to the **customer-reactive-microservice** module. Then, as shown in the following picture, copy the files bordered by red rectangles to the directory of your choice.


![01](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/3c21d5bc-5f5a-43b4-869a-2173edb1df3d)




# On windows:
- **First**, open a command prompt.
- **Second**, run the command opposite: **cd your-address**, for example, cd C:\Users\kasra\Desktop\cloud\microservice
- **Next**, run the command below<br />
**java -jar -Dserver.port=8182 customer-reactive-microservice.jar**
> [!NOTE]
> Repeat as required to execute this command. If you'd like to launch more instances of your microservice, there is no need to copy again. Just execute the command but with another unique port.   

![02](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/5e2e3512-0f4c-4ccb-8037-41e74d8ab845)

![03](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/1314c04c-55b1-4974-898f-dd2b59930ad2)

![04](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/61fb7a72-2cd4-44e7-920f-cfcffb910068)

![05](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/7bb012a2-6736-42b8-8306-07d7a91eff52)











