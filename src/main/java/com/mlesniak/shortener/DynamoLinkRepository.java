package com.mlesniak.shortener;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.Optional;

// @mlesniak Add testcontainer local dynamo
@Primary
@Repository
public class DynamoLinkRepository implements LinkRepository {
    private static final Logger log = LoggerFactory.getLogger(DynamoLinkRepository.class);
    private static final String TABLE_NAME = "links";
    private final DynamoDbClient client;

    public DynamoLinkRepository(DynamoDbClient client) {
        this.client = client;
    }

    @PostConstruct
    public void createTable() {
        var exists = client.listTables().tableNames().contains(TABLE_NAME);
        if (exists) {
            return;
        }

        log.info("Creating table {}", TABLE_NAME);
        CreateTableRequest request = CreateTableRequest.builder()
                .tableName("links")
                .keySchema(KeySchemaElement.builder()
                        .attributeName("id")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("id")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .build();
        client.createTable(request);
    }

    @Override
    public Optional<Url> get(Id id) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("id", AttributeValue.fromS(id.id())))
                .build();
        GetItemResponse item = client.getItem(request);
        if (!item.hasItem()) {
            return Optional.empty();
        }

        var url = new Url(item.item().get("url").s());
        return Optional.of(url);
    }

    @Override
    public void save(Id id, Url url) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(Map.of(
                        "id", AttributeValue.fromS(id.id()),
                        "url", AttributeValue.fromS(url.url())
                ))
                .build();
        client.putItem(request);
    }
}
