package com.mpesa.mpesab2c.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpesa.mpesab2c.entities.GwRequest;
import com.mpesa.mpesab2c.repository.GwRequestRepository;
import com.mpesa.mpesab2c.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PaymentUpdateEvent {
 private final PaymentService paymentService;
 private final GwRequestRepository gwRequestRepository;
 public PaymentUpdateEvent(PaymentService paymentService, GwRequestRepository gwRequestRepository){
     this.paymentService=paymentService;
     this.gwRequestRepository = gwRequestRepository;
 }
    @KafkaListener(topics = "payment_requests", groupId = "payment-group")
    public void listen(@Payload String message) {
     GwRequest requestDto= convertPayloadToObject(message);

     gwRequestRepository.save(requestDto);

    }

    private GwRequest convertPayloadToObject(String message) {
        try {
            return new ObjectMapper().readValue(message, GwRequest.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
