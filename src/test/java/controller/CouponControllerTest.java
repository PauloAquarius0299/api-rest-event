package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulotech.api_event.controller.CuponController;
import com.paulotech.api_event.domain.cupon.Cupon;
import com.paulotech.api_event.domain.cupon.CuponRequestDTO;
import com.paulotech.api_event.service.CuponService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.UUID;

@WebMvcTest(CuponController.class)
class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CuponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test_shouldReturnOk() throws Exception {
        UUID eventId = UUID.randomUUID();
        CuponRequestDTO requestDTO = new CuponRequestDTO("CODE123", 10, new Date().getTime());
        Cupon responseCoupon = new Cupon();

        when(couponService.addCuponToEvent(eventId, requestDTO)).thenReturn(responseCoupon);

        mockMvc.perform(post("/api/coupon/event/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }
}
