package com.joshuawilliams.ims.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderRequest {

    @NotNull
    private Long productId;

    @NotBlank
    private String buyerName;

    @NotBlank
    private String buyerContact;
}
