package com.mlesniak.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

@SpringBootApplication
public class MainTestApplication {
    private static final int DYNAMODB_PORT = 8000;

    static class ContainerConfig {
        @Bean
        public GenericContainer dynamoDbDatabase() {
            // @mlesniak Use GenericContainer once everything is running.
            return new FixedHostPortGenericContainer("amazon/dynamodb-local")
                    .withFixedExposedPort(DYNAMODB_PORT, DYNAMODB_PORT)
                    .withReuse(true);
        }
    }

    public static void main(String[] args) {
        SpringApplication.from(Main::main)
                .run(args);
    }
}
