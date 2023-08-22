package com.winters.tool.rental.data;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
public @Data class RentalRequest {
    String toolCode;
    int numDaysToRent;
    int discountPercent;
    Date checkoutDate;
}
