package com.mpesa.mpesab2c.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpesa.mpesab2c.dtos.*;
import com.mpesa.mpesab2c.entities.GwRequest;
import com.mpesa.mpesab2c.repository.GwRequestRepository;
import com.mpesa.mpesab2c.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mobile-money")
@Tag(name = "B2C Transactions")
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final GwRequestRepository gwRequestRepository;
    private final AcknowledgeResponse acknowledgeResponse;
    private final ObjectMapper objectMapper;

    public PaymentController(PaymentService paymentService, GwRequestRepository gwRequestRepository,  AcknowledgeResponse acknowledgeResponse, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.gwRequestRepository = gwRequestRepository;

        this.acknowledgeResponse = acknowledgeResponse;
        this.objectMapper = objectMapper;
    }

    @PostMapping(path = "/b2c-transaction", produces = "application/json")
    public ResponseEntity<B2CTransactionSyncResponse> performB2CTransaction(@RequestBody @Valid GwRequestDto dto) throws JsonProcessingException {

        return ResponseEntity.ok(paymentService.performB2CTransaction(dto));
    }

    @PostMapping(path = "/transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> b2cTransactionAsyncResults(@RequestBody B2CTransactionAsyncResponse b2CTransactionAsyncResponse)
            throws JsonProcessingException {

        log.info(objectMapper.writeValueAsString(b2CTransactionAsyncResponse));

        Result b2cResult = b2CTransactionAsyncResponse.getResult();

        GwRequest request = gwRequestRepository.findByOriginatorConversationIDOrConversationID(
                b2cResult.getOriginatorConversationID(), b2cResult.getConversationID());

        request.setRef(b2cResult.getTransactionID());

        gwRequestRepository.save(request);

        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/b2c-queue-timeout", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> queueTimeout(@RequestBody Object object) {
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping(path = "/simulate-transaction-result", produces = "application/json")
    public ResponseEntity<B2CTransactionSyncResponse> getTransactionStatusResult(@RequestBody InternalTransactionStatusRequest internalTransactionStatusRequest) throws JsonProcessingException {
        return ResponseEntity.ok(paymentService.getTransactionResult(internalTransactionStatusRequest));
    }
}
