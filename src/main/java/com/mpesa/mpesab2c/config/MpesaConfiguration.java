package com.mpesa.mpesab2c.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mpesa.daraja")
public class MpesaConfiguration {

    private String oauthEndpoint;
    private String grantType;
    private String registerUrlEndpoint;
    private String confirmationURL;
    private String validationURL;
    private String simulateTransactionEndpoint;
    private Long shortCode;
    private String responseType;
    private String b2cTransactionEndpoint;
    private String b2cResultUrl;
    private String b2cQueueTimeoutUrl;
    private String b2cInitiatorName;
    private String b2cInitiatorPassword;
    private String transactionResultUrl;

    @Override
    public String toString() {
        return String.format("{ grantType='%s', oauthEndpoint='%s'}",
                 grantType, oauthEndpoint);
    }

}
