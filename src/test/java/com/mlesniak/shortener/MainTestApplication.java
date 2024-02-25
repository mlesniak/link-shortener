package com.mlesniak.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@SpringBootApplication
public class MainTestApplication {
    private static final int DYNAMODB_PORT = 8000;

    static class ContainerConfig {
        @Bean
        public GenericContainer<?> dynamoDbDatabase() {
            return new GenericContainer<>("amazon/dynamodb-local")
                    .withExposedPorts(DYNAMODB_PORT)
                    .withReuse(false);
        }

        @Bean
        @Primary
        public DynamoDbClient dynamoDbDockerClient(@Autowired GenericContainer<?> dynamoDb) {
            var port = dynamoDb.getMappedPort(DYNAMODB_PORT);
            return DynamoDbClient.builder()
                    .region(Region.of("eu-central-1"))
                    .endpointOverride(URI.create("http://localhost:" + port))
                    .build();
        }
    }

    public static void main(String[] args) {
        SpringApplication.from(Main::main)
                .run(args);
    }
}
