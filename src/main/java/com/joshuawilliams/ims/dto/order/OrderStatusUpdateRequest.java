package com.joshuawilliams.ims.dto.order;

import com.joshuawilliams.ims.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateRequest {

    @NotNull
    private OrderStatus status;
}
