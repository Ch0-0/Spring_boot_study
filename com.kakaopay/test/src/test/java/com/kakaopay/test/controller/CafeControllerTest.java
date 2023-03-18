package com.kakaopay.test.controller;

import com.kakaopay.test.service.CoffeeService;
import com.kakaopay.test.service.OrderService;
import com.kakaopay.test.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CafeController.class)
class CafeControllerTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mockMvc;

    @MockBean
    private CoffeeService coffeeService;
    @MockBean
    private OrderService orderService;
    @MockBean
    private PointService pointService;

    @Test
    @Order(1)
    void 커피목록_조회() throws Exception {
        mockMvc.perform(get("/api/v1/coffee"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void 포인트충전() throws Exception {
        mockMvc.perform(put("/api/v1/point")
                        .param("memberId", "X00000")
                        .param("points", "10000"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void 커피주문() throws Exception {
        mockMvc.perform(post("/api/v1/coffee")
                        .param("memberId", "X00000")
                        .param("menuId", "100000"))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(4)
    void 인기커피목록_조회() throws Exception {
        mockMvc.perform(get("/api/v1/top/coffee"))
                .andExpect(status().isOk());
    }

}