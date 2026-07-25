package com.fashionstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank
    private String cardholderName;

    @NotBlank
    private String cardLast4;

    /** Demo only — never store full card numbers in production. */
    @NotBlank
    private String billingAddress;
}
