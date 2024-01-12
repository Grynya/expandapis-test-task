package com.expandapis.testtask.repository;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    @PersistenceContext
    EntityManager entityManager;
    public List<String> createTableFromJson(String tableName, JsonNode recordsNodeArray) {
        List<String> tableFieldsList = new ArrayList<>();

        StringBuilder createTableStatement = new StringBuilder("CREATE TABLE ")
                .append(tableName)
                .append("(id int PRIMARY KEY AUTO_INCREMENT");
        JsonNode recordNode = recordsNodeArray.get(0);

        recordNode
                .fieldNames()
                .forEachRemaining(fieldName -> {
                    tableFieldsList.add(fieldName);
                    createTableStatement.append(", ")
                                    .append(fieldName)
                                    .append(" varchar(255)");
                        }
                );
        createTableStatement.append(")");
        entityManager.createNativeQuery(String.format("DROP TABLE IF EXISTS %s", tableName)).executeUpdate();
        entityManager.createNativeQuery(String.valueOf(createTableStatement)).executeUpdate();
        return tableFieldsList;
    }

    public void insertAllFromJson(String tableName, JsonNode recordsNodeArray, List<String> tableFieldsList) {
        String insertStatement = getInsertStatement(tableName, tableFieldsList);

        for (JsonNode objToInsert : recordsNodeArray) {
            Query insertQuery = entityManager.createNativeQuery(insertStatement);
            tableFieldsList.forEach(fieldName -> {
                JsonNode valueNode = objToInsert.get(fieldName);
                if (Objects.nonNull(valueNode)) {
                    insertQuery.setParameter(tableFieldsList.indexOf(fieldName) + 1, valueNode.asText());
                } else {
                    throw new IllegalArgumentException();
                }
            });
            insertQuery.executeUpdate();
        }
    }

    private static String getInsertStatement(String tableName, List<String> allFields) {
        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName,
                String.join(", ", allFields),
                allFields.stream().map(element -> "?").collect(Collectors.joining(", ")));
    }

    public Object findAll() {
        if (!entityManager.createNativeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'products';").getResultList().isEmpty()) {
            return entityManager.createNativeQuery("SELECT * FROM products").getResultList();
        } else {
            throw new InvalidDataAccessApiUsageException("Table products doesn't exist.");
        }
    }
}