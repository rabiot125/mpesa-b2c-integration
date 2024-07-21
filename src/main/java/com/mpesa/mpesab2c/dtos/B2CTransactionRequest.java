package com.mpesa.mpesab2c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class B2CTransactionRequest {
    @JsonProperty("OriginatorConversationID")
    private String OriginatorConversationID;

    @JsonProperty("InitiatorName")
    private String initiatorName;

    @JsonProperty("SecurityCredential")
    private String securityCredential;

    @JsonProperty("CommandID")
    private String commandID;

    @JsonProperty("Amount")
    private float amount;

    @JsonProperty("PartyA")
    private Long partyA;

    @JsonProperty("PartyB")
    private String partyB;
    @JsonProperty("Remarks")
    private String remarks;
    @JsonProperty("QueueTimeOutURL")
    private String queueTimeOutURL;

    @JsonProperty("ResultURL")
    private String resultURL;
    @JsonProperty("Occassion")
    private String occassion;

}
