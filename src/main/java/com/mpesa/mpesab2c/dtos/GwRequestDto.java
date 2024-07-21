package com.mpesa.mpesab2c.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class GwRequestDto {
    private String originatorConversationID;

    @NotNull(message = "Mobile number is mandatory")
    @Pattern(regexp = "^(?:\\+254|0)?(7[0-9]{8})$", message = "Invalid Kenyan Safaricom mobile number")
    private String partyB;

    private String conversationID;
    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "10.0", message = "Amount must be at least KSh 10")
    @DecimalMax(value = "150000.0", message = "Maximum Amount is KSh 150,000")
    private float amount;
}
