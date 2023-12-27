package com.expandapis.testtask;

import com.expandapis.testtask.dto.UserCredentialsRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerInterationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private String token;
    @BeforeEach
    void setUp() throws Exception {
        UserCredentialsRequestDto existingUser = new UserCredentialsRequestDto("testUser", "1234");
        String jsonRequest = objectMapper.writeValueAsString(existingUser);
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        this.token = mockMvc.perform(post("/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void shouldGetAllProductsAndResponseOk() throws Exception {
        String jsonRequest = """
                {
                    "table" : "products",
                    "records" : [
                        {
                            "entryDate": "03-01-2023",
                            "itemCode": "11111",
                            "itemName": "Test Inventory 1",
                            "itemQuantity": "20",
                            "status": "Paid"
                        },
                        {
                            "entryDate": "03-01-2023",
                            "itemCode": "11111",
                            "itemName": "Test Inventory 2",
                            "itemQuantity": "20",
                            "status": "Paid"
                        }
                    ]
                }""";
        Object[][] expected = new Object[][]{
                new Object[]{1, "03-01-2023", "11111", "Test Inventory 1", "20", "Paid"},
                new Object[]{2, "03-01-2023", "11111", "Test Inventory 2", "20", "Paid"},
        };
        JsonNode expectedJson = objectMapper.valueToTree(expected);

        mockMvc.perform(post("/products/add")
                .header("Authorization", String.format("Bearer %s", token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        String resultStr = mockMvc.perform(
                get("/products/all")
                        .header("Authorization", String.format("Bearer %s", token)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode actualJson = objectMapper.readTree(resultStr);

        assertTrue(actualJson.isArray());
        assertEquals(2, actualJson.size());
        assertEquals(expectedJson, actualJson);
    }

    @Test
    void shouldResponseBadReqWhenTableNotExist() throws Exception {
        mockMvc.perform(get("/products/all")
                        .header("Authorization", String.format("Bearer %s", token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddProductsAndResponseWithOk() throws Exception {
        String jsonRequest = """
                {
                    "table" : "products",
                    "records" : [
                        {
                            "entryDate": "03-01-2023",
                            "itemCode": "11111",
                            "itemName": "Test Inventory 1",
                            "itemQuantity": "20",
                            "status": "Paid"
                        },
                        {
                            "entryDate": "03-01-2023",
                            "itemCode": "11111",
                            "itemName": "Test Inventory 2",
                            "itemQuantity": "20",
                            "status": "Paid"
                        }
                    ]
                }""";

        mockMvc.perform(post("/products/add")
                        .header("Authorization", String.format("Bearer %s", token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void shouldResponseBadReqWhenJsonStructureInvalid() throws Exception {
        String jsonRequest = """
                {
                    "tabl" : "products",
                    "records" : [
                        {
                            "entryDate": "03-01-2023",
                            "itemCode": "11111",
                            "itemName": "Test Inventory 1",
                            "itemQuantity": "20",
                            "status": "Paid"
                        },
                        {
                            "entryDate": "03-01-2023",
                            "itemCode": "11111",
                            "itemName": "Test Inventory 2",
                            "itemQuantity": "20",
                            "status": "Paid"
                        }
                    ]
                }""";

        mockMvc.perform(post("/products/add")
                        .header("Authorization", String.format("Bearer %s", token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}