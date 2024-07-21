package com.mpesa.mpesab2c.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpesa.mpesab2c.config.MpesaConfiguration;
import com.mpesa.mpesab2c.dtos.*;
import com.mpesa.mpesab2c.entities.GwRequest;
import com.mpesa.mpesab2c.exceptions.FailedB2CTransaction;
import com.mpesa.mpesab2c.exceptions.NullBodyRequest;
import com.mpesa.mpesab2c.repository.GwRequestRepository;
import com.mpesa.mpesab2c.service.OAuthService;
import com.mpesa.mpesab2c.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OAuthService authService;
    private final ObjectMapper objectMapper;
    private final MpesaConfiguration mpesaConfiguration;
    private final GwRequestRepository gwRequestRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    public PaymentServiceImpl(OAuthService authService, ObjectMapper objectMapper, MpesaConfiguration mpesaConfiguration, GwRequestRepository gwRequestRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.authService = authService;
        this.objectMapper = objectMapper;
        this.mpesaConfiguration = mpesaConfiguration;
        this.gwRequestRepository = gwRequestRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public B2CTransactionSyncResponse performB2CTransaction(GwRequestDto dto) throws JsonProcessingException {

        GwRequest gwRequest = new GwRequest();

        gwRequest.setStatus("Pending");
        gwRequest.setMobileNumber(dto.getPartyB());
        gwRequest.setAmount(dto.getAmount());
        gwRequest.setOriginatorConversationID(dto.getOriginatorConversationID());
        gwRequest.setTransactionDate(LocalDate.now());
        gwRequest.setConversationID(dto.getConversationID());

        log.info("GwRequestDto Object {}", objectMapper.writeValueAsString(gwRequest));

        gwRequestRepository.save(gwRequest);

        kafkaTemplate.send("payment_requests", dto.toString());

        OAuthResponseDto accessToken = authService.generateToken();

        log.info("accessToken:" + accessToken);

        B2CTransactionRequest transactionRequest = new B2CTransactionRequest();
        transactionRequest.setCommandID("BusinessPayment");
        transactionRequest.setInitiatorName(mpesaConfiguration.getB2cInitiatorName());
        transactionRequest.setOriginatorConversationID(dto.getOriginatorConversationID());
        transactionRequest.setPartyA(mpesaConfiguration.getShortCode());
        transactionRequest.setAmount(dto.getAmount());
        transactionRequest.setOccassion("Test");
        transactionRequest.setPartyB(dto.getPartyB());
        transactionRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        transactionRequest.setRemarks("Testiiii");
        transactionRequest.setSecurityCredential(mpesaConfiguration.getB2cInitiatorPassword());
        transactionRequest.setResultURL(mpesaConfiguration.getTransactionResultUrl());

        OkHttpClient client = new OkHttpClient().newBuilder().build();


        String json = objectMapper.writeValueAsString(transactionRequest);

        RequestBody body = RequestBody.create(mediaType, Objects.requireNonNull(json));


        Request request = new Request.Builder()
                .url(mpesaConfiguration.getB2cTransactionEndpoint())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken.getAccessToken())
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {

                String responseBody = response.body().string();

                JsonNode jsonNode = objectMapper.readTree(responseBody);

                String originatorConversationID = jsonNode.get("OriginatorConversationID").asText();
                String conversationID = jsonNode.get("ConversationID").asText();
                String responseCode = jsonNode.get("ResponseCode").asText();
                String responseDesc = jsonNode.get("ResponseDescription").asText();

                log.info("Response:" + response.body());
                B2CTransactionSyncResponse syncResponse = new B2CTransactionSyncResponse();

                syncResponse.setOriginatorConversationID(originatorConversationID);
                syncResponse.setConversationID(conversationID);
                syncResponse.setResponseCode(responseCode);
                syncResponse.setResponseDescription(responseDesc);


//             return  objectMapper.readValue(response.body().toString(), B2CTransactionSyncResponse.class);
                return syncResponse;

            } else {
                throw new NullBodyRequest("Request body cannot be null");
            }
        } catch (IOException e) {
            log.info("Could not perform B2C transaction with error:{}", e.getLocalizedMessage());
            throw new FailedB2CTransaction("Could not perform B2C transaction");
        }
    }

    @Override
    public B2CTransactionSyncResponse getTransactionResult(InternalTransactionStatusRequest transactionStatusRequest) throws JsonProcessingException {

        TransactionStatusRequest statusRequest = new TransactionStatusRequest();
        statusRequest.setTransactionID(transactionStatusRequest.getTransactionID());

        statusRequest.setInitiator(mpesaConfiguration.getB2cInitiatorName());
        statusRequest.setSecurityCredential(mpesaConfiguration.getB2cInitiatorPassword());
        statusRequest.setCommandID("TransactionStatusQuery");
        statusRequest.setPartyA(mpesaConfiguration.getShortCode().toString());
        statusRequest.setIdentifierType("4");
        statusRequest.setResultURL(mpesaConfiguration.getB2cResultUrl());
        statusRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        statusRequest.setRemarks("Transaction Status");
        statusRequest.setOccasion("Transaction Status");

        OAuthResponseDto accessToken = authService.generateToken();
        String json = objectMapper.writeValueAsString(statusRequest);

        RequestBody body = RequestBody.create(mediaType, Objects.requireNonNull(json));

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getTransactionResultUrl())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken.getAccessToken())
                .build();

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            // use Jackson to Decode the ResponseBody ...

            return objectMapper.readValue(response.body().string(), B2CTransactionSyncResponse.class);
        } catch (IOException e) {
            log.info(String.format("Could not fetch transaction result -> %s", e.getLocalizedMessage()));
            return null;
        }
    }

    @Scheduled(fixedRate = 60000)
    public void queryPaymentStatuses() {
        List<GwRequest> pendingRequests = gwRequestRepository.findAllByStatus("Pending");
        for (GwRequest request : pendingRequests) {
            // Query payment status and update accordingly
        }
    }
}


