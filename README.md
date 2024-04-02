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

![01](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/8dcdf4fb-26d3-411c-b7e8-e0e31317487e)




# On windows:
- **First**, open a command prompt.
- **Second**, run the command opposite: **cd your-address**, for example, cd C:\Users\kasra\Desktop\cloud\microservice
- **Next**, run the command below<br />
**java -jar -Dserver.port=8182 customer-reactive-microservice.jar**
> [!NOTE]
> Repeat as required to execute this command. If you'd like to launch more instances of your microservice, there is no need to copy again. Just execute the command but with another unique port.   

![02](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/81e4b181-f67d-4c04-a33a-0a88c39350f0)

![image](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/475c022e-28e9-43ca-9a06-726aed33e896)

![image](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/baddb413-a206-4421-b2cf-71974052832f)

![01](https://github.com/kasra-haghpanah/spring-cloud-webflux-with-eureka-discovery/assets/53397941/60ce41e4-1960-480a-9587-94a5fb81449e)










