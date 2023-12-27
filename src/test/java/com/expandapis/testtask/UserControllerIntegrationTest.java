package com.expandapis.testtask;

import com.expandapis.testtask.dto.UserCredentialsRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${jwt.secret}")
    private String secretKey;

    @Test
    void shouldAddUserAndResponseWithOk() throws Exception {
        UserCredentialsRequestDto newUser = new UserCredentialsRequestDto("testUser", "1234");
        String jsonRequest = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }
    @Test
    void shouldReturnResponseWithConflictWhenAddDuplicate() throws Exception {
        UserCredentialsRequestDto existingUser = new UserCredentialsRequestDto("testUser", "1234");
        String jsonRequest = objectMapper.writeValueAsString(existingUser);

        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldAuthenticateUser() throws Exception {
        UserCredentialsRequestDto existingUser = new UserCredentialsRequestDto("testUser", "1234");
        String jsonRequest = objectMapper.writeValueAsString(existingUser);
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        String token = mockMvc.perform(post("/user/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Claims claims = parseToken(token);
        assertEquals("testUser", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }
    @Test
    void shouldNotAuthenticateUserWhenBadCred() throws Exception {
        UserCredentialsRequestDto existingUser = new UserCredentialsRequestDto("testUser", "1234");
        String jsonRequest = objectMapper.writeValueAsString(existingUser);
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        UserCredentialsRequestDto notExistingUser = new UserCredentialsRequestDto("estUser", "1234");
        jsonRequest = objectMapper.writeValueAsString(notExistingUser);

        mockMvc.perform(post("/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    private Claims parseToken(String token) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}