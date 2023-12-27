package com.expandapis.testtask.service;

import com.expandapis.testtask.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void createAndFillInTable(JsonNode jsonNode) {
        JsonNode tableNameNode = jsonNode.get("table");
        JsonNode recordsNodeArray = jsonNode.get("records");
        if (Objects.nonNull(tableNameNode) && Objects.nonNull(recordsNodeArray) && recordsNodeArray.isArray() && !recordsNodeArray.isEmpty()) {
            String tableName = tableNameNode.asText();
            List<String> tableFieldsList = productRepository.createTableFromJson(tableName, recordsNodeArray);
            productRepository.insertAllFromJson(tableName, recordsNodeArray, tableFieldsList);
        } else {
            throw new IllegalArgumentException("Incorrect structure of input json object");
        }
    }

    public Object getAllProducts() {
        return productRepository.findAll();
    }
}