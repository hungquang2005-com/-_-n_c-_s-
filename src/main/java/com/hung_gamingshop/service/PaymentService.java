package com.hung_gamingshop.service;

import com.hung_gamingshop.model.Order;
import com.hung_gamingshop.model.Payment;
import com.hung_gamingshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Tạo bản ghi thanh toán
    public Payment createPayment(Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(order.getPaymentMethod());
        payment.setAmount(order.getTotalAmount());

        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
        } else {
            payment.setStatus(Payment.PaymentStatus.PENDING);
        }

        return paymentRepository.save(payment);
    }
}