package com.mpesa.mpesab2c.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "GwRequest")
public class GwRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private float amount;
    private String mobileNumber;
    private String status;
    private String originatorConversationID;
    private String conversationID;
    private LocalDate transactionDate;
     private String ref;

}
