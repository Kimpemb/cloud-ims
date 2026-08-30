package com.joshuawilliams.ims.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SellerProfileResponse {
    private String username;
    private String businessName;
    private long availableProductCount;
}
