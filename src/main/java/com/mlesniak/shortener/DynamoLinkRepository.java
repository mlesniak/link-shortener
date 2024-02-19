package com.mlesniak.shortener;

import jakarta.annotation.PostConstruct;
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
    private final DynamoDbClient client;

    public DynamoLinkRepository(DynamoDbClient client) {
        this.client = client;
    }

    @PostConstruct
    public void createTable() {
        var exists = client.listTables().tableNames().contains("links");
        if (exists) {
            return;
        }

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
                .tableName("links")
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
                .tableName("links")
                .item(Map.of(
                        "id", AttributeValue.fromS(id.id()),
                        "url", AttributeValue.fromS(url.url())
                ))
                .build();
        client.putItem(request);
    }
}
