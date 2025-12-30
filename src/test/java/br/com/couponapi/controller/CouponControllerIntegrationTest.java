package br.com.couponapi.controller;

import br.com.couponapi.config.TimeConfigTest;
import br.com.couponapi.dtos.CouponCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import(TimeConfigTest.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CouponControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build();
    }

    @Test
    void createCoupon_ShouldReturnCreatedAndNormalizedResponse() throws Exception {

        CouponCreateRequest request = new CouponCreateRequest(
            "TE-ST12",
            "Test Coupon",
            BigDecimal.valueOf(5.0),
            OffsetDateTime.parse("2025-01-02T10:00:00-03:00"),
            false
        );

        mockMvc.perform(post("/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("TEST12"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.published").value(false))
            .andExpect(jsonPath("$.redeemed").value(false));
    }

    @Test
    void getAllCoupons_ShouldReturnList() throws Exception {

        createCoupon("AAA111");
        createCoupon("BBB222");

        mockMvc.perform(get("/coupons"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getCouponById_ShouldReturnCoupon() throws Exception {

        String response = createCoupon("CCC333");

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/coupons/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("CCC333"));
    }

    @Test
    void consumeCoupon_ShouldReturnConsumedCoupon() throws Exception {

        String response = createCoupon("DDD444");
        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(patch("/coupons/{id}/consume", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.redeemed").value(true))
            .andExpect(jsonPath("$.status").value("INACTIVE"));
    }


    @Test
    void deleteCoupon_ShouldReturnNoContent() throws Exception {

        String response = createCoupon("EEE555");
        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/coupons/{id}", id))
            .andExpect(status().isNoContent());
    }


    private String createCoupon(String code) throws Exception {
        CouponCreateRequest request = new CouponCreateRequest(
            code,
            "Test Coupon",
            BigDecimal.ONE,
            OffsetDateTime.parse("2025-01-05T10:00:00-03:00"),
            true
        );

        return mockMvc.perform(post("/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    }
}
