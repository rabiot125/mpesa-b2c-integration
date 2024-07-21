package com.mpesa.mpesab2c;

import com.mpesa.mpesab2c.dtos.AcknowledgeResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MpesaB2CApplication {

    public static void main(String[] args) {
        SpringApplication.run(MpesaB2CApplication.class, args);
    }

    @Bean
    public AcknowledgeResponse getAcknowledgeResponse() {
        AcknowledgeResponse acknowledgeResponse = new AcknowledgeResponse();
        acknowledgeResponse.setMessage("Completed");
        return acknowledgeResponse;
    }

}
