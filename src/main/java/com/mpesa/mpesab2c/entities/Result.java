package com.mpesa.mpesab2c.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Result {
    @Id
    private UUID id;
    private String status;
    private String ref;
}
