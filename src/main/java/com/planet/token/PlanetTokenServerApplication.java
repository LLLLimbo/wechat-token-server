package com.planet.token;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.planet.token.service")
@PropertySource("classpath:/dubbo-provider.properties")
public class PlanetTokenServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanetTokenServerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
