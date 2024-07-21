package com.mpesa.mpesab2c.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpesa.mpesab2c.dtos.*;
import com.mpesa.mpesab2c.entities.GwRequest;
import com.mpesa.mpesab2c.exceptions.FailedB2CTransaction;
import com.mpesa.mpesab2c.exceptions.NullBodyRequest;
import com.mpesa.mpesab2c.repository.GwRequestRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;



public interface PaymentService {


    B2CTransactionSyncResponse performB2CTransaction(GwRequestDto dto) throws JsonProcessingException;

    B2CTransactionSyncResponse getTransactionResult(InternalTransactionStatusRequest transactionStatusRequest) throws JsonProcessingException;
}


