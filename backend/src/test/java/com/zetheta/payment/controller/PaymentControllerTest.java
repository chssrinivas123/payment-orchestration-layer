package com.zetheta.payment.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import com.zetheta.payment.config.SecurityConfiguration;
import com.zetheta.payment.entity.PaymentTransaction;
import com.zetheta.payment.enums.PaymentStatus;
import com.zetheta.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentController.class)
@Import({SecurityConfiguration.class, ApiExceptionHandler.class})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void validPaymentReturnsCreated() throws Exception {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId("TXN-1");
        transaction.setOrderId("ORD-POSTMAN-001");
        transaction.setAmount(new BigDecimal("500"));
        transaction.setCurrency("INR");
        transaction.setPaymentMethod("UPI");
        transaction.setIdempotencyKey("ORDER-POSTMAN-KEY");
        transaction.setStatus(PaymentStatus.CREATED);
        when(paymentService.createPayment(
                "ORD-POSTMAN-001", new BigDecimal("500"), "INR", "UPI", "ORDER-POSTMAN-KEY"))
                .thenReturn(transaction);

        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", "ORDER-POSTMAN-KEY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": "ORD-POSTMAN-001",
                                  "amount": 500,
                                  "currency": "INR",
                                  "paymentMethod": "UPI"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("ORD-POSTMAN-001"));

        verify(paymentService).createPayment(
                eq("ORD-POSTMAN-001"), eq(new BigDecimal("500")), eq("INR"), eq("UPI"),
                eq("ORDER-POSTMAN-KEY"));
    }

    @Test
    void missingIdempotencyKeyReturnsJsonBadRequest() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": "ORD-5001",
                                  "amount": 1000.00,
                                  "currency": "INR",
                                  "paymentMethod": "UPI"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Required request header 'Idempotency-Key' is missing"));

        verifyNoInteractions(paymentService);
    }

    @Test
    void invalidJsonReturnsJsonBadRequest() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .header("Idempotency-Key", "ORDER-5001-KEY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body must contain valid JSON"));

        verifyNoInteractions(paymentService);
    }
}
